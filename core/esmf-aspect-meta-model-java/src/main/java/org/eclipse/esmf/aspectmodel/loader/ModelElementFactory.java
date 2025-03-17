/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectLoadingException;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.instantiator.AbstractEntityInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.AspectInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.CharacteristicInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.CodeInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.CollectionInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.ConstraintInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.DurationInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.EitherInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.EncodingConstraintInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.EntityInstanceInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.EntityInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.EnumerationInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.EventInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.FixedPointConstraintInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.LanguageConstraintInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.LengthConstraintInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.ListInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.LocaleConstraintInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.MeasurementInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.OperationInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.PropertyInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.QuantifiableInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.RangeConstraintInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.RegularExpressionConstraintInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.ScalarValueInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.SetInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.SingleEntityInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.SortedSetInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.StateInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.StructuredValueInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.TimeSeriesInstantiator;
import org.eclipse.esmf.aspectmodel.loader.instantiator.TraitInstantiator;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.QuantityKinds;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Units;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.impl.DefaultQuantityKind;
import org.eclipse.esmf.metamodel.impl.DefaultUnit;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.Streams;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * Used as part of the loading process in the {@link AspectModelLoader}, it creates instance for the the {@link ModelElement}s
 * in an AspectModel.
 */
public class ModelElementFactory extends AttributeValueRetriever {
   private final Model model;
   private final Map<Resource, Instantiator<?>> instantiators = new HashMap<>();
   private final Map<Resource, ModelElement> loadedElements = new HashMap<>();
   private Set<Namespace> namespaces;
   private final Function<Resource, AspectModelFile> sourceLocator;

   public ModelElementFactory( final Model model, final Map<Resource, Instantiator<?>> additionalInstantiators,
         final Function<Resource, AspectModelFile> sourceLocator ) {
      this.model = model;
      this.sourceLocator = sourceLocator;

      registerInstantiator( SammNs.SAMM.AbstractEntity(), new AbstractEntityInstantiator( this ) );
      registerInstantiator( SammNs.SAMM.AbstractProperty(), new PropertyInstantiator( this ) );
      registerInstantiator( SammNs.SAMM.Aspect(), new AspectInstantiator( this ) );
      registerInstantiator( SammNs.SAMM.Characteristic(), new CharacteristicInstantiator( this ) );
      registerInstantiator( SammNs.SAMM.Constraint(), new ConstraintInstantiator( this ) );
      registerInstantiator( SammNs.SAMM.Entity(), new EntityInstantiator( this ) );
      registerInstantiator( SammNs.SAMM.Event(), new EventInstantiator( this ) );
      registerInstantiator( SammNs.SAMM.Operation(), new OperationInstantiator( this ) );
      registerInstantiator( SammNs.SAMM.Property(), new PropertyInstantiator( this ) );

      /*
       * Registers an instantiator for the {@link Value} type.
       * In aspect-meta-model, {@link Value} corresponds to {@link ScalarValueInstantiator} in the SDK.
       */
      registerInstantiator( SammNs.SAMM.Value(), new ScalarValueInstantiator( this ) );

      registerInstantiator( SammNs.SAMMC.Code(), new CodeInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.Collection(), new CollectionInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.Duration(), new DurationInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.Either(), new EitherInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.EncodingConstraint(), new EncodingConstraintInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.Enumeration(), new EnumerationInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.FixedPointConstraint(), new FixedPointConstraintInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.LanguageConstraint(), new LanguageConstraintInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.LengthConstraint(), new LengthConstraintInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.List(), new ListInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.LocaleConstraint(), new LocaleConstraintInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.Measurement(), new MeasurementInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.Quantifiable(), new QuantifiableInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.RangeConstraint(), new RangeConstraintInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.RegularExpressionConstraint(), new RegularExpressionConstraintInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.Set(), new SetInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.SingleEntity(), new SingleEntityInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.SortedSet(), new SortedSetInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.State(), new StateInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.StructuredValue(), new StructuredValueInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.TimeSeries(), new TimeSeriesInstantiator( this ) );
      registerInstantiator( SammNs.SAMMC.Trait(), new TraitInstantiator( this ) );

      instantiators.putAll( additionalInstantiators );
   }

   private void registerInstantiator( final Resource resource, final Instantiator<?> instantiator ) {
      instantiators.put( resource, instantiator );
   }

   @SuppressWarnings( "unchecked" )
   public <T extends ModelElement> T create( final Class<T> clazz, final Resource modelElement ) {
      ModelElement element = loadedElements.get( modelElement );
      if ( element != null ) {
         return (T) element;
      }
      final Resource targetType = resourceType( modelElement );
      if ( SammNs.SAMM.Unit().equals( targetType ) ) {
         return (T) findOrCreateUnit( modelElement );
      }
      if ( SammNs.SAMM.QuantityKind().equals( targetType ) ) {
         return (T) findOrCreateQuantityKind( modelElement );
      }
      final Instantiator<T> instantiator = (Instantiator<T>) instantiators.get( targetType );
      if ( instantiator != null ) {
         element = instantiator.apply( modelElement );
         loadedElements.put( modelElement, element );
         return (T) element;
      }

      // No generic instantiator could be found. This means the element is an entity instance
      if ( !model.contains( targetType, RDF.type, (RDFNode) null ) ) {
         throw new AspectLoadingException( "Could not load " + modelElement + ": Unknown type " + targetType );
      }
      final Entity entity = create( Entity.class, targetType );
      if ( entity == null ) {
         throw new AspectLoadingException( "Could not load " + modelElement + ": Expected " + targetType + " to be an Entity" );
      }
      return (T) new EntityInstanceInstantiator( this, entity ).apply( modelElement );
   }

   public QuantityKind findOrCreateQuantityKind( final Resource quantityKindResource ) {
      final Optional<QuantityKind> predefinedQuantityKind = QuantityKinds.fromName( quantityKindResource.getLocalName() );
      return predefinedQuantityKind.orElseGet( () -> new DefaultQuantityKind(
            createBaseAttributes( quantityKindResource ),
            attributeValue( quantityKindResource, SammNs.SAMM.preferredName() ).getLiteral().getLexicalForm() ) );
   }

   public Unit findOrCreateUnit( final Resource unitResource ) {
      if ( SammNs.UNIT.getNamespace().equals( unitResource.getNameSpace() ) ) {
         final AspectModelUrn unitUrn = AspectModelUrn.fromUrn( unitResource.getURI() );
         return Units.fromName( unitUrn.getName() ).orElseThrow( () ->
               new AspectLoadingException( "Unit definition for " + unitUrn + " is invalid" ) );
      }

      final Set<QuantityKind> quantityKinds = Streams.stream(
                  model.listStatements( unitResource, SammNs.SAMM.quantityKind(), (RDFNode) null ) )
            .map( quantityKindStatement -> findOrCreateQuantityKind( quantityKindStatement.getObject().asResource() ) )
            .collect( Collectors.toSet() );
      return new DefaultUnit(
            createBaseAttributes( unitResource ),
            optionalAttributeValue( unitResource, SammNs.SAMM.symbol() ).map( Statement::getString ),
            optionalAttributeValue( unitResource, SammNs.SAMM.commonCode() ).map( Statement::getString ),
            optionalAttributeValue( unitResource, SammNs.SAMM.referenceUnit() ).map( Statement::getResource ).map( Resource::getLocalName ),
            optionalAttributeValue( unitResource, SammNs.SAMM.conversionFactor() ).map( Statement::getString ),
            quantityKinds );
   }

   private Resource resourceType( final Resource resource ) {
      final Supplier<Optional<Resource>> directType = () ->
            optionalAttributeValue( resource, RDF.type ).map( Statement::getResource );
      final Supplier<Optional<Resource>> propertyUsageType = () ->
            optionalAttributeValue( resource, SammNs.SAMM.property() ).map( statement -> resourceType( statement.getResource() ) );
      final Supplier<Optional<Resource>> subClassType = () ->
            optionalAttributeValue( resource, RDFS.subClassOf ).map( Statement::getResource ).map( this::resourceType );
      final Supplier<Optional<Resource>> extendsType = () ->
            optionalAttributeValue( resource, SammNs.SAMM._extends() ).map( Statement::getResource ).map( this::resourceType );

      return Stream.of( directType, propertyUsageType, subClassType, extendsType )
            .map( Supplier::get )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .findFirst()
            .orElseThrow( () -> new AspectLoadingException( "Resource " + resource + " has no type" ) );
   }

   protected Model getModel() {
      return model;
   }

   public List<ComplexType> getExtendingElements( final List<AspectModelUrn> extendingElements ) {
      return extendingElements.stream().map( urn -> getModel().createResource( urn.toString() ) )
            .map( loadedElements::get )
            .filter( Objects::nonNull )
            .map( ComplexType.class::cast )
            .collect( Collectors.toList() );
   }

   /**
    * Creates an instance for a specific Meta Model element.
    *
    * @param modelElement the Aspect model element to be processed.
    * @return the newly created instance
    */
   public MetaModelBaseAttributes createBaseAttributes( final Resource modelElement ) {
      final AttributeValueRetriever valueRetriever = new AttributeValueRetriever();
      final Optional<AspectModelUrn> urn = getUrn( modelElement );
      final Set<LangString> preferredNames = getLanguages( modelElement, SammNs.SAMM.preferredName(), valueRetriever );
      final Set<LangString> descriptions = getLanguages( modelElement, SammNs.SAMM.description(), valueRetriever );
      final List<String> seeValues = getSeeValues( modelElement, valueRetriever );
      return MetaModelBaseAttributes.builder()
            .withOptionalUrn( urn )
            .withPreferredNames( preferredNames )
            .withDescriptions( descriptions )
            .withSee( seeValues )
            .withSourceFile( getSourceLocation( modelElement ) )
            .build();
   }

   private static Optional<AspectModelUrn> getUrn( final Resource modelElement ) {
      if ( modelElement.isAnon() ) {
         final Statement propertyStatement = modelElement.getProperty( SammNs.SAMM.property() );
         if ( propertyStatement != null ) {
            return getUrn( propertyStatement.getObject().asResource() );
         }
         return Optional.empty();
      }
      return modelElement.getURI().endsWith( "#" )
            ? Optional.of( AspectModelUrn.fromUrn( modelElement.getURI().replace( "#", "" ) ) )
            : Optional.of( AspectModelUrn.fromUrn( modelElement.getURI() ) );
   }

   /**
    * @param modelElement the RDF {@link Resource} representing the Aspect Model element to be processed
    * @param attribute the RDF {@link org.apache.jena.rdf.model.Property} for which the values will be retrieved
    * @param valueRetriever the {@link AttributeValueRetriever} used to retrieve the attribute values
    * @return a {@link List} containing all values for the given Property in the given Aspect Model element
    */
   private static Set<LangString> getLanguages( final Resource modelElement,
         final org.apache.jena.rdf.model.Property attribute, final AttributeValueRetriever valueRetriever ) {
      return valueRetriever.attributeValues( modelElement, attribute ).stream()
            .filter( languageStatement -> !"und".equals( Locale.forLanguageTag( languageStatement.getLanguage() ).toLanguageTag() ) )
            .map( statement -> new LangString( statement.getString(), Locale.forLanguageTag( statement.getLanguage() ) ) )
            .collect( Collectors.toSet() );
   }

   /**
    * @param resource the RDF {@link Resource} representing the Aspect Model element to be processed
    * @param valueRetriever the {@link AttributeValueRetriever} used to retrieve the attribute values
    * @return a {@link List} containing all {@link SAMM#see()} values for a Aspect Model element
    */
   private static List<String> getSeeValues( final Resource resource, final AttributeValueRetriever valueRetriever ) {
      return valueRetriever.attributeValues( resource, SammNs.SAMM.see() ).stream()
            .map( statement -> statement.getObject().toString() )
            .sorted()
            .collect( Collectors.toList() );
   }

   private static String getSyntheticName( final Resource modelElement ) {
      final Resource namedParent = getNamedParent( modelElement, modelElement.getModel() );
      if ( namedParent == null ) {
         throw new AspectLoadingException( "At least one anonymous node in the model does not have a parent with a regular name." );
      }
      final String parentModelElementUri = namedParent.getURI();
      final String parentModelElementName = AspectModelUrn.from( parentModelElementUri )
            .toJavaOptional()
            .map( AspectModelUrn::getName )
            .map( StringUtils::capitalize )
            .orElse( "" );

      final Resource modelElementType = getModelElementType( modelElement );
      final String modelElementTypeUri = modelElementType.getURI();
      final String modelElementTypeName = AspectModelUrn.from( modelElementTypeUri )
            .toJavaOptional()
            .map( AspectModelUrn::getName )
            .orElse( "" );

      return parentModelElementName + modelElementTypeName;
   }

   // We have to be careful when searching for the parent nodes with a regular name - the "listStatements" API returns the matching nodes
   // in no particular order; with some very specific models this could lead to non-deterministic behavior.
   // In the following very simplified example we are looking for ":NumberList" as the parent of "_:blankNode", but could get the
   // anonymous node [] instead.
   // [
   //  aux:contains _:blankNode ;
   // ] .
   // :NumberList a samm-c:List ;
   //    samm-c:elementCharacteristic _:blankNode .
   // _:blankNode a samm-c:Trait ;
   private static Resource getNamedParent( final Resource modelElement, final Model model ) {
      final StmtIterator elements = model.listStatements( null, null, modelElement );
      while ( elements.hasNext() ) {
         final Resource parentModelElement = elements.next().getSubject();
         if ( parentModelElement.isAnon() ) {
            final Resource grandParent = getNamedParent( parentModelElement, model );
            if ( null != grandParent ) {
               return grandParent;
            }
         } else {
            return parentModelElement;
         }
      }
      return null; // element has no named parent
   }

   private static Resource getModelElementType( final Resource modelElement ) {
      final Statement typeStatement = modelElement.getProperty( RDF.type );
      if ( typeStatement != null ) {
         return typeStatement.getObject().asResource();
      }

      // If the model element is a Property reference, the actual type will be found when we follow samm:property
      final Statement propertyStatement = modelElement.getProperty( SammNs.SAMM.property() );
      if ( propertyStatement != null ) {
         return getModelElementType( propertyStatement.getObject().asResource() );
      }

      // This model element has no type, but maybe it extends another element
      final Statement extendsStatement = modelElement.getProperty( SammNs.SAMM._extends() );
      if ( extendsStatement == null ) {
         throw new AspectLoadingException( "Model element has no type and does not extend another type: " + modelElement );
      }

      final Resource superElement = extendsStatement.getObject().asResource();
      return getModelElementType( superElement );
   }

   public AspectModelFile getSourceLocation( final Resource modelElement ) {
      if ( modelElement.isURIResource() ) {
         for ( final MetaModelFile metaModelFile : MetaModelFile.values() ) {
            if ( metaModelFile.getMetaModelFileType() == MetaModelFile.MetaModelFileType.ELEMENT_DEFINITION
                  && modelElement.getURI().startsWith( metaModelFile.getRdfNamespace().getUri() ) ) {
               return metaModelFile;
            }
         }
      }

      return sourceLocator.apply( modelElement );
   }
}
