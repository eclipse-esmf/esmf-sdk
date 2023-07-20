/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.metamodel.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.eclipse.esmf.aspectmodel.resolver.services.ExtendedXsdDataType;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMMC;
import org.eclipse.esmf.aspectmodel.vocabulary.UNIT;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.impl.DefaultCollectionValue;
import org.eclipse.esmf.metamodel.impl.DefaultEntityInstance;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.eclipse.esmf.samm.KnownVersion;

public abstract class Instantiator<T extends ModelElement> extends AttributeValueRetriever implements Function<Resource, T> {
   protected final ModelElementFactory modelElementFactory;
   protected Class<T> targetClass;
   protected SAMMC sammc;
   protected UNIT unit;
   protected Model model;
   protected KnownVersion metaModelVersion;
   protected final RDFDatatype curieDataType = new CurieRdfType();

   public Instantiator( final ModelElementFactory modelElementFactory, final Class<T> targetClass ) {
      super( modelElementFactory.getSamm() );
      this.modelElementFactory = modelElementFactory;
      this.targetClass = targetClass;
      sammc = modelElementFactory.getSammc();
      unit = modelElementFactory.getUnit();
      model = modelElementFactory.getModel();
      metaModelVersion = modelElementFactory.getMetaModelVersion();
   }

   protected MetaModelBaseAttributes buildBaseAttributes( final Resource resource ) {
      return MetaModelBaseAttributes.fromModelElement( metaModelVersion, resource, model, samm );
   }

   protected Statement propertyValueFromTypeTree( final Resource subject, final org.apache.jena.rdf.model.Property property ) {
      final Optional<Statement> valueStatement = optionalAttributeValue( subject, property );
      if ( valueStatement.isPresent() ) {
         return valueStatement.get();
      }

      // Check if the subject is a Property reference, then we should continue to search the referenced Property
      final Optional<Statement> propertyStatement = optionalAttributeValue( subject, samm.property() );
      if ( propertyStatement.isPresent() ) {
         return propertyValueFromTypeTree( propertyStatement.get().getObject().asResource(), property );
      }

      final Statement extendsStatement = optionalAttributeValue( subject, samm._extends() )
            .orElseThrow( () -> new AspectLoadingException( "Property " + property + " not found on " + subject + " or its supertypes" ) );
      final Resource superType = extendsStatement.getObject().asResource();
      return propertyValueFromTypeTree( superType, property );
   }

   /**
    * Extracts all {@link SAMM#Property()} model elements which are referenced in the given Property List, e.g.
    * {@link SAMM#properties()}, and creates {@link Property} instances for these model elements.
    *
    * @param elementWithProperties the {@link Resource} which has the propertyRdfClass list for which the model
    *       elements are extracted
    * @param rootProperty the {@link org.apache.jena.rdf.model.Property} defining the property list
    * @return a {@link List} containing the {@link Property} instances
    */
   protected List<Property> getPropertiesModels( final Resource elementWithProperties, final org.apache.jena.rdf.model.Property rootProperty ) {
      return getResourcesFromList( elementWithProperties, rootProperty )
            .map( propertyResource -> modelElementFactory.create( Property.class, propertyResource ) )
            .collect( Collectors.toList() );
   }

   protected Stream<RDFNode> getNodesFromList( final Resource element, final org.apache.jena.rdf.model.Property property ) {
      return Optional.ofNullable( element.getProperty( property ) ).stream()
            .flatMap( p -> p.getObject().as( RDFList.class ).asJavaList().stream() );
   }

   protected Stream<Resource> getResourcesFromList( final Resource element, final org.apache.jena.rdf.model.Property property ) {
      return getNodesFromList( element, property ).map( RDFNode::asResource );
   }

   protected Type getType( final Resource characteristicResource ) {
      final Statement dataType = getDataType( characteristicResource );
      final Resource dataTypeResource = dataType.getObject().asResource();

      final Optional<Statement> entityStatement = optionalAttributeValue( dataTypeResource, RDF.type );

      if ( entityStatement.isPresent() && samm.Entity().equals( entityStatement.get().getObject().asResource() ) ) {
         return modelElementFactory.create( Entity.class, entityStatement.get().getSubject() );
      }

      if ( entityStatement.isPresent() && samm.AbstractEntity().equals( entityStatement.get().getObject().asResource() ) ) {
         return modelElementFactory.create( AbstractEntity.class, entityStatement.get().getSubject() );
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
      return Optional.ofNullable( resource.getPropertyResourceValue( samm.baseCharacteristic() ) )
            .map( this::getDataType )
            .orElseGet( () -> resource.getProperty( samm.dataType() ) );
   }

   protected Optional<Characteristic> getElementCharacteristic( final Resource collection ) {
      return optionalAttributeValue( collection, sammc.elementCharacteristic() )
            .map( Statement::getResource )
            .map( elementCharacteristicResource ->
                  modelElementFactory.create( Characteristic.class, elementCharacteristicResource ) );
   }

   protected boolean isTypeOfOrSubtypeOf( final Resource element, final Resource type ) {
      Resource typeInHierarchy = element.getPropertyResourceValue( RDF.type );
      while ( typeInHierarchy != null ) {
         if ( type.equals( typeInHierarchy ) ) {
            return true;
         }
         typeInHierarchy = typeInHierarchy.getPropertyResourceValue( RDFS.subClassOf );
      }
      return false;
   }

   /**
    * Creates a {@link Value} from a given constant value in the RDF model. This can be either a scalar, a collection or an Entity.
    * What is constructed can depend on the type of RDF node, but also on the Characteristic of the Property this value is used for.
    *
    * @param node the RDF node that represents the value
    * @param characteristicResource the resources that represents the Characteristic that describes the value. This can be empty for the values of collections
    *                               that have no samm-c:elementCharacterisic set
    * @param type the type that describes the value
    * @return a value instance
    */
   protected Value buildValue( final RDFNode node, final Optional<Resource> characteristicResource, final Type type ) {
      // Literals
      if ( node.isLiteral() ) {
         return buildScalarValue( node.asLiteral() );
      }

      // Collections
      if ( characteristicResource.isPresent() ) {
         final Resource characteristic = characteristicResource.get();
         final Optional<Resource> elementCharacteristic = optionalAttributeValue( characteristic, sammc.elementCharacteristic() ).map( Statement::getResource );
         CollectionValue.CollectionType collectionType = null;
         if ( isTypeOfOrSubtypeOf( characteristic, sammc.Set() ) ) {
            collectionType = CollectionValue.CollectionType.SET;
         } else if ( isTypeOfOrSubtypeOf( characteristic, sammc.SortedSet() ) ) {
            collectionType = CollectionValue.CollectionType.SORTEDSET;
         } else if ( isTypeOfOrSubtypeOf( characteristic, sammc.List() ) ) {
            collectionType = CollectionValue.CollectionType.LIST;
         } else if ( isTypeOfOrSubtypeOf( characteristic, sammc.Collection() ) ) {
            collectionType = CollectionValue.CollectionType.COLLECTION;
         }
         if ( collectionType != null ) {
            return buildCollectionValue( node.as( RDFList.class ), collectionType, elementCharacteristic, type );
         }
      }

      // This could happen if an entity instance should be constructed for an AbstractEntity type
      if ( !type.is( Entity.class ) ) {
         throw new AspectLoadingException( "Expected type of value " + node + " to be samm:Entity, but it is not" );
      }

      // Entities
      final Resource resource = node.asResource();
      return buildEntityInstance( resource, (Entity) type );
   }

   private ScalarValue buildScalarValue( final Literal literal ) {
      // rdf:langString needs special handling here:
      // 1. A custom parser for rdf:langString values can not be registered with Jena, because it would only receive from Jena during parsing
      //    the lexical representation of the value without the language tag (this is handled specially in Jena).
      // 2. This means that a Literal we receive here which has a type URI of rdf:langString will be of type org.apache.jena.datatypes.xsd.impl.RDFLangString
      //    but _not_ org.eclipse.esmf.metamodel.datatypes.LangString as we would like to.
      // 3. So we construct an instance of LangString here from the RDFLangString.
      if ( literal.getDatatypeURI().equals( RDF.langString.getURI() ) ) {
         return buildLanguageString( literal );
      }

      return Stream.concat( ExtendedXsdDataType.supportedXsdTypes.stream(), Stream.of( curieDataType ) )
            .filter( type -> type.getURI().equals( literal.getDatatypeURI() ) )
            .map( type -> type.parse( literal.getLexicalForm() ) )
            .map( value -> new DefaultScalarValue( value, new DefaultScalar( literal.getDatatypeURI(), metaModelVersion ) ) )
            .findAny()
            .orElseThrow( () -> new AspectLoadingException( "Literal can not be parsed: " + literal ) );
   }

   protected ScalarValue buildLanguageString( final Literal literal ) {
      final LangString langString = new LangString( literal.getString(), Locale.forLanguageTag( literal.getLanguage() ) );
      final Scalar type = new DefaultScalar( RDF.langString.getURI(), metaModelVersion );
      return new DefaultScalarValue( langString, type );
   }

   private CollectionValue buildCollectionValue( final RDFList list, final CollectionValue.CollectionType collectionType,
         final Optional<Resource> elementCharacteristic, final Type elementType ) {
      final java.util.Collection<Value> values = createEmptyCollectionForType( collectionType );
      list.asJavaList().forEach( element -> values.add( buildValue( element, elementCharacteristic, elementType ) ) );
      return new DefaultCollectionValue( values, collectionType, elementType );
   }

   protected EntityInstance buildEntityInstance( final Resource entityInstance, final Entity type ) {
      final Map<Property, Value> assertions = new HashMap<>();
      type.getAllProperties().forEach( property -> {
         final AspectModelUrn propertyUrn = property.getAspectModelUrn()
               .orElseThrow( () -> new AspectLoadingException( "Invalid Property without a URN found" ) );
         final org.apache.jena.rdf.model.Property rdfProperty = model.createProperty( propertyUrn.getUrn().toASCIIString() );
         final Statement statement = entityInstance.getProperty( rdfProperty );
         if ( statement == null ) {
            if ( property.isOptional() ) {
               return;
            }
            throw new AspectLoadingException( "Mandatory Property " + property + " not found in Entity instance " + entityInstance );
         }
         final RDFNode rdfValue = entityInstance.getProperty( rdfProperty ).getObject();
         final Type propertyType = property.getDataType().orElseThrow( () -> new AspectLoadingException( "Invalid Property without a dataType found" ) );
         final Resource characteristic = attributeValue( rdfProperty, samm.characteristic() ).getResource();
         final Value value = buildValue( rdfValue, Optional.of( characteristic ), propertyType );
         assertions.put( property, value );
      } );
      final MetaModelBaseAttributes baseAttributes = buildBaseAttributes( entityInstance );
      return new DefaultEntityInstance( baseAttributes, assertions, type );
   }

   private java.util.Collection<Value> createEmptyCollectionForType( final CollectionValue.CollectionType collectionType ) {
      if ( collectionType == CollectionValue.CollectionType.SORTEDSET ) {
         return new LinkedHashSet<>();
      }
      if ( collectionType == CollectionValue.CollectionType.SET ) {
         return new HashSet<>();
      }
      return new ArrayList<>();
   }
}
