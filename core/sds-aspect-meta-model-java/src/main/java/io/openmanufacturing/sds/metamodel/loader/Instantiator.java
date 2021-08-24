/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship. 
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package io.openmanufacturing.sds.metamodel.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import com.google.common.collect.ImmutableList;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMMC;
import io.openmanufacturing.sds.aspectmodel.vocabulary.UNIT;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.QuantityKinds;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.Units;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalar;
import io.openmanufacturing.sds.metamodel.impl.DefaultUnit;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public abstract class Instantiator<T extends Base> implements Function<Resource, T> {
   protected final ModelElementFactory modelElementFactory;
   protected Class<T> targetClass;
   protected BAMM bamm;
   protected BAMMC bammc;
   protected UNIT unit;
   protected Model model;
   protected KnownVersion metaModelVersion;

   public Instantiator( final ModelElementFactory modelElementFactory, final Class<T> targetClass ) {
      this.modelElementFactory = modelElementFactory;
      this.targetClass = targetClass;
      bamm = modelElementFactory.getBamm();
      bammc = modelElementFactory.getBammc();
      unit = modelElementFactory.getUnit();
      model = modelElementFactory.getModel();
      metaModelVersion = modelElementFactory.getMetaModelVersion();
   }

   protected MetaModelBaseAttributes buildBaseAttributes( final Resource resource ) {
      return MetaModelBaseAttributes.fromMetaModelElement( metaModelVersion, resource, model, bamm );
   }

   protected Optional<Statement> optionalPropertyValue( final Resource subject,
      final org.apache.jena.rdf.model.Property type ) {
      return modelElementFactory.propertyValue( subject, type );
   }

   protected Statement propertyValue( final Resource subject,
      final org.apache.jena.rdf.model.Property type ) {
      return optionalPropertyValue( subject, type )
         .orElseThrow( () -> new AspectLoadingException( "Missing Property " + type + " on " + subject ) );
   }

   /**
    * Extracts all {@link BAMM#Property()} model elements which are referenced in the given Property List, e.g.
    * {@link BAMM#properties()}, and creates {@link Property} instances for these model elements.
    *
    * @param elementWithProperties the {@link Resource} which has the propertyRdfClass list for which the model
    *       elements are extracted
    * @param rootProperty the {@link Property} defining the property list
    * @return a {@link List} containing the {@link Property} instances
    */
   protected List<Property> getPropertiesModels( final Resource elementWithProperties,
      final org.apache.jena.rdf.model.Property rootProperty ) {
      final Stream<Resource> properties = getResourcesFromList( elementWithProperties, rootProperty );
      final Stream<Resource> refinedProperties = elementWithProperties.hasProperty( bamm.refines() ) ?
         getRefinedProperties( elementWithProperties, rootProperty ) : Stream.empty();
      return Stream.concat( properties, refinedProperties )
         .map( propertyResource -> modelElementFactory.create( Property.class, propertyResource ) )
         .collect( Collectors.toList() );
   }

   private Stream<Resource> getRefinedProperties( final Resource elementWithProperties,
      final org.apache.jena.rdf.model.Property rootProperty ) {
      final List<Resource> refinedProperties =
         getResourcesFromList( elementWithProperties, rootProperty ).map( property ->
            property.getProperty( bamm.refines() ).getObject().asResource() ).collect( Collectors.toList() );

      final Resource refinedEntity = elementWithProperties.getProperty( bamm.refines() ).getObject().asResource();
      return getResourcesFromList( refinedEntity, rootProperty )
         .filter( property -> !refinedProperties.contains( property ) );
   }

   protected Stream<RDFNode> getNodesFromList( final Resource element,
      final org.apache.jena.rdf.model.Property property ) {
      return element.getProperty( property ).getObject().as( RDFList.class ).asJavaList().stream();
   }

   protected Stream<Resource> getResourcesFromList( final Resource element,
      final org.apache.jena.rdf.model.Property property ) {
      return getNodesFromList( element, property ).map( RDFNode::asResource );
   }

   protected Type getType( final Resource characteristicResource ) {
      final Statement dataType = getDataType( characteristicResource );
      final Resource dataTypeResource = dataType.getObject().asResource();

      final Optional<Statement> entityStatement = optionalPropertyValue( dataTypeResource, RDF.type );
      final Optional<Statement> refinedEntityStatement = optionalPropertyValue( dataTypeResource, bamm.refines() );

      if ( entityStatement.isPresent() && isEntity( entityStatement.get().getObject().asResource() ) ) {
         return modelElementFactory.create( Entity.class, entityStatement.get().getSubject() );
      }

      if ( entityStatement.isEmpty() && refinedEntityStatement.isPresent() ) {
         return modelElementFactory.create( Entity.class, refinedEntityStatement.get().getSubject() );
      }

      return new DefaultScalar( dataTypeResource.getURI(), metaModelVersion );
   }

   /**
    * Recursively search the datatype in a Characteristic chain, baseCharacteristics first
    *
    * @param resource The Characteristic resource
    * @return The statement describing the datatype
    */
   private Statement getDataType( final Resource resource ) {
      return Optional.ofNullable( resource.getPropertyResourceValue( bamm.baseCharacteristic() ) )
         .map( this::getDataType )
         .orElseGet( () -> resource.getProperty( bamm.dataType() ) );
   }

   /**
    * Determines whether a {@link Resource} is a {@link BAMM#Entity()}.
    * This is done recursively in order to include Entities which refine another Entity and hence are not a
    * {@link BAMM#Entity()} themselves.
    *
    * @param modelElement the {@link Resource} to be checked
    * @return a boolean value indicating whether the #modelElement is an Entity or not
    */
   private boolean isEntity( Resource modelElement ) {
      if ( bamm.Entity().equals( modelElement ) ) {
         return true;
      }

      while ( modelElement.hasProperty( RDF.type ) ) {
         modelElement = modelElement.getProperty( RDF.type ).getObject().asResource();
         if ( bamm.Entity().equals( modelElement ) ) {
            return true;
         }
      }

      return false;
   }

   protected Optional<Unit> findOrCreateUnit( final Resource unitResource ) {
      if ( unit.getNamespace().equals( unitResource.getNameSpace() ) ) {
         return Units.fromName( unitResource.getProperty( bamm.name() ).getString(), metaModelVersion );
      }

      return Optional.of( new DefaultUnit(
         MetaModelBaseAttributes.fromMetaModelElement( metaModelVersion, unitResource, model, bamm ),
         optionalPropertyValue( unitResource, unit.symbol() ).map( Statement::getString ),
         optionalPropertyValue( unitResource, unit.commonCode() ).map( Statement::getString ),
         optionalPropertyValue( unitResource, unit.referenceUnit() ).map( Statement::getResource )
            .map( Resource::getLocalName ),
         optionalPropertyValue( unitResource, unit.conversionFactor() ).map( Statement::getString ),
         ImmutableList.copyOf( model.listStatements( unitResource, unit.quantityKind(), (RDFNode) null ) ).stream()
            .flatMap( quantityKindStatement -> QuantityKinds
               .fromName( quantityKindStatement.getObject().asResource().getLocalName() ).stream() )
            .collect( Collectors.toSet() ) ) );
   }

   protected Optional<Characteristic> getElementCharacteristic( final Resource collection ) {
      return optionalPropertyValue( collection, bammc.elementCharacteristic() )
         .map( Statement::getResource )
         .map( elementCharacteristicResource ->
            modelElementFactory.create( Characteristic.class, elementCharacteristicResource ) );
   }

   /**
    * Extracts the value of an Enum from the {@link RDFNode} representing this value. In case the value
    * is a {@link org.apache.jena.rdf.model.Literal} the value is returned directly. In case the value is an Entity,
    * a {@link Map} is returned.
    *
    * In case the value being processed is itself an Enum entry, the `enumerationKey` key is added to the map. The value
    * for this key is the name of the Enum entry. This `enumerationKey` is used to correctly deserialize the JSON.
    *
    * @param node the RDF node representing the value to be extracted
    */
   protected Object toEnumNodeValue( final RDFNode node ) {
      if ( node.isLiteral() ) {
         return node.asLiteral().getValue();
      }

      final Map<String, Object> nodeValues = toComplexNodeValue( node, bammc );
      nodeValues.put( bamm.name().toString(), node.asNode().getLocalName() );
      return nodeValues;
   }

   private Map<String, Object> toComplexNodeValue( final RDFNode node, final BAMMC bammc ) {
      return node.asResource()
         .listProperties()
         .toList()
         .stream()
         .filter( statement -> !RDF.type.equals( statement.getPredicate() ) )
         .map( statement -> getPropertyNameAndObject( statement, bammc ) )
         .collect( Collectors.groupingBy( PropertyNameAndObject::getName, Collectors.flatMapping(
            propertyNameAndObject -> Stream.of( propertyNameAndObject.getObject() ),
            Collectors.toList() ) ) )
         .entrySet().stream()
         .map( entry -> Tuple.of( entry.getKey(), unwrapList( entry.getValue() ) ) )
         .collect( Collectors.toMap( Tuple2::_1, Tuple2::_2 ) );
   }

   private PropertyNameAndObject getPropertyNameAndObject( final Statement valueStatement, final BAMMC bammc ) {
      if ( valueStatement.getObject().isLiteral() ) {
         final Literal literal = valueStatement.getObject().asLiteral();
         if ( literal.getDatatypeURI().equals( RDF.langString.getURI() ) ) {
            return new PropertyNameAndObject(
               valueStatement.getPredicate().getLocalName(),
               Map.of( literal.getLanguage(), literal.getLexicalForm() ) );
         }
      }
      if ( isEnumValue( valueStatement.getPredicate() ) ) {
         return new PropertyNameAndObject(
            valueStatement.getPredicate().getLocalName(),
            toEnumNodeValue( valueStatement.getObject() ) );
      } else if ( isCollectionValue( valueStatement.getPredicate(), bammc ) ) {
         final String propertyName = valueStatement.getPredicate().getLocalName();
         return new PropertyNameAndObject(
            propertyName, instantiateCollection( valueStatement ) );
      }
      return new PropertyNameAndObject(
         valueStatement.getPredicate().getLocalName(),
         toEnumNodeValue( valueStatement.getObject() ) );
   }

   private Object unwrapList( final List<Object> list ) {
      // Entity instance has a regular scalar value
      if ( list.size() == 1 ) {
         return list.get( 0 );
      }

      // Entity instance has values of type rdf:langString, which means multiple values.
      // Each value is a map of language code to text. Merge list of maps into one map.
      return list.stream().flatMap( element -> {
         final Map<String, String> map = (Map<String, String>) element;
         return map.entrySet().stream().map( entry -> Tuple.of( entry.getKey(), entry.getValue() ) );
      } ).collect( Collectors.toList() );
   }

   private Collection<Object> createEmptyCollectionForType( final Resource collectionType ) {
      if ( collectionType.equals( bammc.SortedSet() ) ) {
         return new LinkedHashSet<>();
      }
      if ( collectionType.equals( bammc.Set() ) ) {
         return new HashSet<>();
      }
      return new ArrayList<>();
   }

   private Collection<?> instantiateCollection( final Statement statement ) {
      final Resource collectionResource = statement.getPredicate()
         .asResource()
         .getProperty( bamm.characteristic() )
         .getObject()
         .asResource()
         .getProperty( RDF.type )
         .getObject()
         .asResource();

      final Collection<Object> collection = createEmptyCollectionForType( collectionResource );
      statement.getObject().as( RDFList.class ).asJavaList().stream().map( this::toEnumNodeValue )
         .forEach( collection::add );
      return collection;
   }

   /**
    * Determines whether a Property is an Enum by checking whether the Characteristic of the Property is
    * an Enumeration Characteristic.
    *
    * @param node the {@link RDFNode} representing the Property
    */
   private boolean isEnumValue( final RDFNode node ) {
      return node.asResource().getProperty( bamm.characteristic() )
         .getObject().asResource().getProperty( RDF.type ).getObject().asResource()
         .equals( bammc.Enumeration() );
   }

   /**
    * Determines whether a Property is an Collection by checking whether the Characteristic of the Property is
    * an Collection Characteristic.
    *
    * @param node the {@link RDFNode} representing the Property
    * @param bammc the vocabulary for the Characteristic catalogue
    */
   private boolean isCollectionValue( final RDFNode node, final BAMMC bammc ) {
      return bammc.allCollections().anyMatch( collection ->
         collection.equals( node.asResource().getProperty( bamm.characteristic() ).getObject().asResource()
            .getProperty( RDF.type ).getObject().asResource() ) );
   }

   private static class PropertyNameAndObject {
      private final String name;

      private final Object object;

      PropertyNameAndObject( final String name, final Object object ) {
         this.name = name;
         this.object = object;
      }

      public String getName() {
         return name;
      }

      public Object getObject() {
         return object;
      }
   }
}
