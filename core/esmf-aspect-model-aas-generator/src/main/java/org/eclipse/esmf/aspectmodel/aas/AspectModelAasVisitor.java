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
package org.eclipse.esmf.aspectmodel.aas;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.characteristic.Code;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.Duration;
import org.eclipse.esmf.characteristic.Either;
import org.eclipse.esmf.characteristic.Enumeration;
import org.eclipse.esmf.characteristic.Measurement;
import org.eclipse.esmf.characteristic.Quantifiable;
import org.eclipse.esmf.characteristic.SingleEntity;
import org.eclipse.esmf.characteristic.SortedSet;
import org.eclipse.esmf.characteristic.State;
import org.eclipse.esmf.characteristic.StructuredValue;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.digitaltwin.aas4j.v3.model.AasSubmodelElements;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.ValueList;
import org.eclipse.digitaltwin.aas4j.v3.model.ValueReferencePair;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultDataSpecificationIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultValueList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultValueReferencePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectModelAasVisitor implements AspectVisitor<Environment, Context> {
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelAasVisitor.class );
   private static final ValueSerializer VALUE_SERIALIZER = new ValueSerializer();

   public static final String ADMIN_SHELL_NAME = "defaultAdminShell";
   public static final String DEFAULT_LOCALE = "en";
   public static final String CONCEPT_DESCRIPTION_CATEGORY = "APPLICATION_CLASS";
   public static final String CONCEPT_DESCRIPTION_DATA_SPECIFICATION_URL =
         "https://admin-shell.io/DataSpecificationTemplates/DataSpecificationIec61360/3/0";

   /**
    * Maps Aspect types to DataTypeIEC61360 Schema types, with no explicit mapping defaulting to
    * string
    */
   private static final Map<Resource, DataTypeIec61360> TYPE_MAP =
         ImmutableMap.<Resource, DataTypeIec61360> builder()
               .put( XSD.xboolean, DataTypeIec61360.BOOLEAN )
               .put( XSD.decimal, DataTypeIec61360.INTEGER_MEASURE )
               .put( XSD.integer, DataTypeIec61360.INTEGER_MEASURE )
               .put( XSD.xfloat, DataTypeIec61360.REAL_MEASURE )
               .put( XSD.xdouble, DataTypeIec61360.REAL_MEASURE )
               .put( XSD.xbyte, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.xshort, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.xint, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.xlong, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.unsignedByte, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.unsignedShort, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.unsignedInt, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.unsignedLong, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.positiveInteger, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.nonPositiveInteger, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.negativeInteger, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.nonNegativeInteger, DataTypeIec61360.INTEGER_COUNT )
               .put( RDF.langString, DataTypeIec61360.STRING )
               .build();

   private interface SubmodelElementBuilder {
      SubmodelElement build( Property property );
   }

   private final Set<Property> recursiveProperty = new HashSet<>();

   private static final DefaultPropertyMapper DEFAULT_MAPPER = new DefaultPropertyMapper();

   private final List<PropertyMapper<?>> customPropertyMappers = new ArrayList<>();

   public AspectModelAasVisitor withPropertyMapper( final PropertyMapper<?> propertyMapper ) {
      customPropertyMappers.add( propertyMapper );

      return this;
   }

   @SuppressWarnings( "unchecked" )
   protected <T extends SubmodelElement> PropertyMapper<T> findPropertyMapper( final Property property ) {
      return (PropertyMapper<T>) getCustomPropertyMappers().stream()
            .filter( mapper -> mapper.canHandle( property ) )
            .findAny()
            .orElse( DEFAULT_MAPPER );
   }

   protected List<PropertyMapper<?>> getCustomPropertyMappers() {
      return customPropertyMappers;
   }

   @Override
   public Environment visitBase( final ModelElement base, final Context context ) {
      return context.getEnvironment();
   }

   protected List<Reference> buildGlobalReferenceForSeeReferences( final NamedElement modelElement ) {
      return modelElement.getSee().stream().map( seeReference -> (Reference) new DefaultReference.Builder()
                  .type( ReferenceTypes.EXTERNAL_REFERENCE )
                  .keys( new DefaultKey.Builder()
                        .type( KeyTypes.GLOBAL_REFERENCE )
                        .value( seeReference.startsWith( "urn:irdi:" ) ? seeReference.substring( 9 ) : seeReference )
                        .build() )
                  .build() )
            .toList();
   }

   private List<Reference> updateGlobalReferenceWithSeeReferences( final SubmodelElement submodelElement,
         final NamedElement modelElement ) {
      final List<Reference> newReferences = buildGlobalReferenceForSeeReferences( modelElement );
      final List<Reference> supplementalSemanticIds = submodelElement.getSupplementalSemanticIds();
      if ( supplementalSemanticIds == null ) {
         return newReferences;
      }
      return CollectionUtils.union( supplementalSemanticIds, newReferences ).stream().distinct().toList();
   }

   @Override
   public Environment visitAspect( final Aspect aspect, final Context context ) {
      Context usedContext = context;
      if ( usedContext == null ) {
         final Submodel submodel = new DefaultSubmodel.Builder().build();
         final Environment environment = new DefaultEnvironment.Builder().submodels( Collections.singletonList( submodel ) ).build();
         usedContext = new Context( environment, submodel );
         usedContext.setEnvironment( environment );
      }

      final String submodelId = aspect.getAspectModelUrn().get().getUrn().toString() + "/submodel";

      final Submodel submodel = usedContext.getSubmodel();
      submodel.setIdShort( aspect.getName() );
      submodel.setId( submodelId );
      submodel.setSemanticId( buildReferenceToConceptDescription( aspect ) );
      submodel.setSupplementalSemanticIds( buildGlobalReferenceForSeeReferences( aspect ) );
      submodel.setDescription( LangStringMapper.TEXT.map( aspect.getDescriptions() ) );
      submodel.setKind( usedContext.getModelingKind() );
      submodel.setAdministration( new DefaultAdministrativeInformation.Builder().build() );

      createConceptDescription( aspect, usedContext );

      final AssetAdministrationShell administrationShell =
            new DefaultAssetAdministrationShell.Builder()
                  .id( DEFAULT_MAPPER.determineIdentifierFor( aspect ) )
                  .idShort( ADMIN_SHELL_NAME )
                  .description( LangStringMapper.TEXT.createLangString( ADMIN_SHELL_NAME, "en" ) )
                  .administration( new DefaultAdministrativeInformation.Builder().build() )
                  .assetInformation( new DefaultAssetInformation.Builder()
                        .assetKind( usedContext.getAssetKind() )
                        .build() )
                  .submodels( buildReferenceForSubmodel( submodelId ) )
                  .build();
      usedContext.getEnvironment()
            .setAssetAdministrationShells( Collections.singletonList( administrationShell ) );

      usedContext.appendToSubModelElements( visitProperties( aspect.getProperties(), usedContext ) );
      usedContext.appendToSubModelElements( visitOperations( aspect.getOperations(), usedContext ) );

      return usedContext.getEnvironment();
   }

   private List<SubmodelElement> visitOperations(
         final List<org.eclipse.esmf.metamodel.Operation> elements, final Context context ) {
      return elements.stream().map( i -> mapText( i, context ) ).collect( Collectors.toList() );
   }

   private List<SubmodelElement> visitProperties( final List<Property> elements, final Context context ) {
      return elements.stream().map( i -> mapText( i, context ) )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .collect( Collectors.toList() );
   }

   private Optional<SubmodelElement> mapText( final Property property, final Context context ) {
      final Optional<SubmodelElement> defaultResultForProperty = context.getSubmodel()
            .getSubmodelElements().stream()
            .filter( i -> i.getIdShort().equals( property.getName() ) )
            .findFirst();
      if ( recursiveProperty.contains( property ) ) {
         // The guard checks for recursion in properties. If a recursion happens, the respective
         // property will be excluded from generation.
         recursiveProperty.remove( property );
         if ( property.isOptional() ) {
            LOG.warn( String.format( "Having a recursive Property %s which is optional. Will be excluded from AAS mapping.",
                  property.getAspectModelUrn().map( AspectModelUrn::toString ).orElse( "(unknown)" ) ) );
            return defaultResultForProperty;
         } else {
            LOG.error( String.format(
                  "Having a recursive property: %s which is not optional is not valid. Check the model. Property will be excluded from "
                        + "AAS mapping.",
                  property.getAspectModelUrn().map( AspectModelUrn::toString ).orElse( "(unknown)" ) ) );
         }
         return defaultResultForProperty;
      }
      recursiveProperty.add( property );

      if ( property.getCharacteristic().isEmpty() || property.isAbstract() ) {
         LOG.warn( "Having an abstract property. Will be excluded from AAS mapping." );
         return Optional.empty();
      }

      // Characteristic defines how the property is mapped to SubmodelElement
      final Characteristic characteristic = property.getCharacteristic().get();

      context.setProperty( property );
      characteristic.accept( this, context );
      final SubmodelElement element = context.getPropertyResult();
      element.setSupplementalSemanticIds( updateGlobalReferenceWithSeeReferences( element, property ) );

      recursiveProperty.remove( property );

      return Optional.of( element );
   }

   private SubmodelElement decideOnMapping( final Property property, final Context context ) {
      if ( property.getCharacteristic().flatMap( Characteristic::getDataType ).isEmpty() ) {
         return new DefaultProperty.Builder().build();
      }

      final Type type = property.getCharacteristic().get().getDataType().orElseThrow();
      return decideOnMapping( type, property, context );
   }

   private SubmodelElement decideOnMapping( final Type type, final Property property, final Context context ) {
      if ( type instanceof Entity ) {
         return mapToAasSubModelElementCollection( (Entity) type, context );
      } else {
         return findPropertyMapper( property ).mapToAasProperty( type, property, context );
      }
   }

   private SubmodelElementCollection mapToAasSubModelElementCollection( final Entity entity, final Context context ) {
      final List<SubmodelElement> submodelElements =
            visitProperties( entity.getAllProperties(), context );
      return new DefaultSubmodelElementCollection.Builder()
            .idShort( entity.getName() )
            .displayName( LangStringMapper.NAME.map( entity.getPreferredNames() ) )
            .description( LangStringMapper.TEXT.map( entity.getDescriptions() ) )
            .value( submodelElements )
            .supplementalSemanticIds( buildGlobalReferenceForSeeReferences( entity ) )
            .build();
   }

   private Operation mapText( final org.eclipse.esmf.metamodel.Operation operation, final Context context ) {
      createConceptDescription( operation, context );
      return new DefaultOperation.Builder()
            .displayName( LangStringMapper.NAME.map( operation.getPreferredNames() ) )
            .description( LangStringMapper.TEXT.map( operation.getDescriptions() ) )
            .semanticId( buildReferenceToOperation( operation ) )
            .idShort( operation.getName() )
            .inputVariables( operation.getInput().stream()
                  .map( i -> mapOperationVariable( i, context ) )
                  .collect( Collectors.toList() ) )
            .outputVariables( operation.getOutput().stream()
                  .map( i -> mapOperationVariable( i, context ) )
                  .collect( Collectors.toList() ) )
            .supplementalSemanticIds( buildGlobalReferenceForSeeReferences( operation ) )
            .build();
   }

   private Reference buildReferenceToOperation( final org.eclipse.esmf.metamodel.Operation operation ) {
      final Key key = new DefaultKey.Builder()
            .type( KeyTypes.OPERATION )
            .value( DEFAULT_MAPPER.determineIdentifierFor( operation ) )
            .build();
      return new DefaultReference.Builder().type( ReferenceTypes.MODEL_REFERENCE ).keys( key ).build();
   }

   private OperationVariable mapOperationVariable( final Property property, final Context context ) {
      return new DefaultOperationVariable.Builder().value( mapText( property, context ).orElseThrow() ).build();
   }

   private Reference buildReferenceToEnumValue( final Enumeration enumeration, final String value ) {
      final Key key = new DefaultKey.Builder()
            .type( KeyTypes.DATA_ELEMENT )
            .value( DEFAULT_MAPPER.determineIdentifierFor( enumeration ) + ":" + value )
            .build();
      return new DefaultReference.Builder().type( ReferenceTypes.MODEL_REFERENCE ).keys( key ).build();
   }

   private Reference buildReferenceToConceptDescription( final Aspect aspect ) {
      final Key key = new DefaultKey.Builder()
            .type( KeyTypes.CONCEPT_DESCRIPTION )
            .value( DEFAULT_MAPPER.determineIdentifierFor( aspect ) )
            .build();
      return new DefaultReference.Builder().type( ReferenceTypes.MODEL_REFERENCE ).keys( key ).build();
   }

   private Reference buildReferenceForSeeElement( final String seeReference ) {
      final Key key = new DefaultKey.Builder()
            .type( KeyTypes.GLOBAL_REFERENCE )
            .value( seeReference )
            .build();
      return new DefaultReference.Builder()
            .type( ReferenceTypes.EXTERNAL_REFERENCE )
            .keys( key )
            .build();
   }

   private Reference buildReferenceForSubmodel( final String submodelId ) {
      final Key key = new DefaultKey.Builder()
            .type( KeyTypes.SUBMODEL )
            .value( submodelId )
            .build();
      return new DefaultReference.Builder()
            .type( ReferenceTypes.MODEL_REFERENCE )
            .keys( key )
            .build();
   }

   private Reference buildReferenceForCollection( final String submodelId ) {
      final Key key = new DefaultKey.Builder()
            .type( KeyTypes.GLOBAL_REFERENCE )
            .value( submodelId )
            .build();
      return new DefaultReference.Builder()
            .type( ReferenceTypes.EXTERNAL_REFERENCE )
            .keys( key )
            .build();
   }

   private void createConceptDescription( final Property property, final Context context ) {
      if ( property.getCharacteristic().isEmpty() ) {
         return;
      }
      // check if the concept description is already created. If not create a new one.
      if ( !context.hasEnvironmentConceptDescription( property.getAspectModelUrn().toString() ) ) {
         final ConceptDescription conceptDescription =
               new DefaultConceptDescription.Builder()
                     .idShort( property.getName() )
                     .displayName( LangStringMapper.NAME.map( property.getPreferredNames() ) )
                     .embeddedDataSpecifications( extractEmbeddedDataSpecification( property ) )
                     .id( DEFAULT_MAPPER.determineIdentifierFor( property ) )
                     .build();
         context.getEnvironment().getConceptDescriptions().add( conceptDescription );
      }
   }

   private void createConceptDescription( final org.eclipse.esmf.metamodel.Operation operation, final Context context ) {
      // check if the concept description is already created. If not create a new one.
      if ( !context.hasEnvironmentConceptDescription( operation.getAspectModelUrn().toString() ) ) {
         final ConceptDescription conceptDescription =
               new DefaultConceptDescription.Builder()
                     .idShort( operation.getName() )
                     .displayName( LangStringMapper.NAME.map( operation.getPreferredNames() ) )
                     .embeddedDataSpecifications( extractEmbeddedDataSpecification( operation ) )
                     .id( DEFAULT_MAPPER.determineIdentifierFor( operation ) )
                     .build();
         context.getEnvironment().getConceptDescriptions().add( conceptDescription );
      }
   }

   private void createConceptDescription( final Aspect aspect, final Context context ) {
      // check if the concept description is already created. If not create a new one.
      if ( !context.hasEnvironmentConceptDescription( aspect.getAspectModelUrn().toString() ) ) {
         final ConceptDescription conceptDescription =
               new DefaultConceptDescription.Builder()
                     .idShort( aspect.getName() )
                     .displayName( LangStringMapper.NAME.map( aspect.getPreferredNames() ) )
                     .embeddedDataSpecifications( extractEmbeddedDataSpecification( aspect ) )
                     .id( DEFAULT_MAPPER.determineIdentifierFor( aspect ) )
                     .description( LangStringMapper.TEXT.map( aspect.getDescriptions() ) )
                     .category( CONCEPT_DESCRIPTION_CATEGORY )
                     .build();
         context.getEnvironment().getConceptDescriptions().add( conceptDescription );
      }
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification( final Property property ) {
      return new DefaultEmbeddedDataSpecification.Builder()
            .dataSpecification( buildReferenceForSeeElement( CONCEPT_DESCRIPTION_DATA_SPECIFICATION_URL ) )
            .dataSpecificationContent( extractDataSpecificationContent( property ) )
            .build();
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification( final org.eclipse.esmf.metamodel.Operation operation ) {
      return new DefaultEmbeddedDataSpecification.Builder()
            .dataSpecification( buildReferenceForSeeElement( CONCEPT_DESCRIPTION_DATA_SPECIFICATION_URL ) )
            .dataSpecificationContent( extractDataSpecificationContent( operation ) )
            .build();
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification( final Aspect aspect ) {
      return new DefaultEmbeddedDataSpecification.Builder()
            .dataSpecification( buildReferenceForSeeElement( CONCEPT_DESCRIPTION_DATA_SPECIFICATION_URL ) )
            .dataSpecificationContent( extractDataSpecificationContent( aspect ) )
            .build();
   }

   private DataSpecificationIec61360 extractDataSpecificationContent( final Property property ) {
      final List<LangStringDefinitionTypeIec61360> definitionsProperty = property.getDescriptions().stream()
            .map( LangStringMapper.DEFINITION::map ).toList();

      final List<LangStringPreferredNameTypeIec61360> preferredNames = property.getPreferredNames().isEmpty()
            ? Collections.singletonList( LangStringMapper.PREFERRED_NAME.createLangString( property.getName(), DEFAULT_LOCALE ) )
            : property.getPreferredNames().stream().map( LangStringMapper.PREFERRED_NAME::map ).collect( Collectors.toList() );

      return new DefaultDataSpecificationIec61360.Builder()
            .definition( definitionsProperty )
            .preferredName( preferredNames )
            .shortName( LangStringMapper.SHORT_NAME.createLangString( property.getName(), DEFAULT_LOCALE ) )
            .dataType( mapIec61360DataType( property.getCharacteristic() ) )
            .build();
   }

   private DataSpecificationIec61360 extractDataSpecificationContent( final org.eclipse.esmf.metamodel.Operation operation ) {
      final List<LangStringPreferredNameTypeIec61360> preferredNames = operation.getPreferredNames().isEmpty()
            ? Collections.singletonList( LangStringMapper.PREFERRED_NAME.createLangString( operation.getName(), DEFAULT_LOCALE ) )
            : operation.getPreferredNames().stream().map( LangStringMapper.PREFERRED_NAME::map ).collect( Collectors.toList() );

      return new DefaultDataSpecificationIec61360.Builder()
            .definition( operation.getDescriptions().stream().map( LangStringMapper.DEFINITION::map ).collect( Collectors.toList() ) )
            .preferredName( preferredNames )
            .shortName( LangStringMapper.SHORT_NAME.createLangString( operation.getName(), DEFAULT_LOCALE ) )
            .build();
   }

   private DataSpecificationIec61360 extractDataSpecificationContent( final Aspect aspect ) {
      final List<LangStringPreferredNameTypeIec61360> preferredNames = aspect.getPreferredNames().isEmpty()
            ? Collections.singletonList( LangStringMapper.PREFERRED_NAME.createLangString( aspect.getName(), DEFAULT_LOCALE ) )
            : aspect.getPreferredNames().stream().map( LangStringMapper.PREFERRED_NAME::map ).collect( Collectors.toList() );

      return new DefaultDataSpecificationIec61360.Builder()
            .definition( aspect.getDescriptions().stream().map( LangStringMapper.DEFINITION::map ).collect( Collectors.toList() ) )
            .preferredName( preferredNames )
            .shortName( LangStringMapper.SHORT_NAME.createLangString( aspect.getName(), DEFAULT_LOCALE ) )
            .build();
   }

   private DataTypeIec61360 mapIec61360DataType( final Optional<Characteristic> characteristic ) {
      return mapIec61360DataType(
            characteristic.flatMap( Characteristic::getDataType ).map( Type::getUrn ).orElse( RDF.langString.getURI() ) );
   }

   private DataTypeIec61360 mapIec61360DataType( final Characteristic characteristic ) {
      return mapIec61360DataType( Optional.of( characteristic ) );
   }

   private DataTypeIec61360 mapIec61360DataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return TYPE_MAP.getOrDefault( resource, DataTypeIec61360.STRING );
   }

   private void createSubmodelElement( final SubmodelElementBuilder op, final Context context ) {
      final Property property = context.getProperty();
      final SubmodelElement submodelElement = op.build( property );
      context.setPropertyResult( submodelElement );
      createConceptDescription( property, context );
   }

   @Override
   public Environment visitCharacteristic( final Characteristic characteristic, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );
      return context.getEnvironment();
   }

   @Override
   public Environment visitCollection( final Collection collection, final Context context ) {
      return visitCollectionProperty( collection, context );
   }

   @Override
   public Environment visitList( final org.eclipse.esmf.characteristic.List list, final Context context ) {
      return visitCollectionProperty( list, context );
   }

   @Override
   public Environment visitSet( final org.eclipse.esmf.characteristic.Set set, final Context context ) {
      return visitCollectionProperty( set, context );
      // this type is not available in AAS4J
   }

   @Override
   public Environment visitSortedSet( final SortedSet sortedSet, final Context context ) {
      return visitCollectionProperty( sortedSet, context );
   }

   private <T extends Collection> Environment visitCollectionProperty( final T collection, final Context context ) {
      final SubmodelElementBuilder builder = property -> {
         DefaultSubmodelElementList.Builder submodelBuilder = new DefaultSubmodelElementList.Builder()
               .idShort( property.getName() )
               .typeValueListElement( AasSubmodelElements.DATA_ELEMENT )
               .displayName( LangStringMapper.NAME.map( property.getPreferredNames() ) )
               .description( LangStringMapper.TEXT.map( property.getDescriptions() ) )
               .value( List.of( decideOnMapping( property, context ) ) )
               .typeValueListElement( AasSubmodelElements.SUBMODEL_ELEMENT )
               .supplementalSemanticIds( buildGlobalReferenceForSeeReferences( collection ) );

         if ( collection.getAspectModelUrn().isPresent() ) {
            submodelBuilder.semanticId( buildReferenceForCollection( collection.getAspectModelUrn().get().getUrn().toString() ) );
         }

         return submodelBuilder.build();
      };

      final Optional<JsonNode> rawValue = context.getRawPropertyValue();
      return rawValue.map( node -> {
         if ( node instanceof final ArrayNode arrayNode ) {
            final SubmodelElementBuilder listBuilder = property -> {
               final List<SubmodelElement> values = getValues( collection, property, context, arrayNode );
               return new DefaultSubmodelElementList.Builder()
                     .idShort( property.getName() )
                     .displayName( LangStringMapper.NAME.map( property.getPreferredNames() ) )
                     .description( LangStringMapper.TEXT.map( property.getDescriptions() ) )
                     .value( values )
                     .typeValueListElement( AasSubmodelElements.SUBMODEL_ELEMENT )
                     .build();
            };
            createSubmodelElement( listBuilder, context );
            return context.getEnvironment();
         }
         createSubmodelElement( builder, context );
         return context.getEnvironment();
      } ).orElseGet( () -> {
         createSubmodelElement( builder, context );
         return context.getEnvironment();
      } );
   }

   private <T extends Collection> List<SubmodelElement> getValues( final T collection, final Property property, final Context context,
         final ArrayNode arrayNode ) {
      return collection.getDataType()
            .map( dataType -> {
               if ( Scalar.class.isAssignableFrom( dataType.getClass() ) ) {
                  return List.of( (SubmodelElement) new DefaultBlob.Builder().value( StreamSupport.stream( arrayNode.spliterator(), false )
                        .map( JsonNode::asText )
                        .collect( Collectors.joining( "," ) )
                        .getBytes( StandardCharsets.UTF_8 ) ).build() );
               } else {
                  final List<SubmodelElement> values = StreamSupport.stream( arrayNode.spliterator(), false )
                        .map( n -> {
                           context.iterate( property );
                           return decideOnMapping( property, context );
                        } )
                        .toList();
                  context.finishIteration( property );
                  return values;
               }
            } ).orElseGet( List::of );
   }

   // Either will be mapped by adding both the left and the right side to the SubmodelTemplate.
   // No possibilities to mark the SubmodelElements as optional. So both either options are
   // in the result and have to be manually selected.
   // When generating Submodels where data is given however only the present side is added.
   @Override
   public Environment visitEither( final Either either, final Context context ) {
      final List<SubmodelElement> submodelElements = new ArrayList<>();
      final Property property = context.getProperty();
      either.getLeft().getDataType()
            .flatMap( dataType -> handleEitherField( "left", either.getLeft(), property, context ) )
            .ifPresent( submodelElements::add );
      either.getRight().getDataType()
            .flatMap( dataType -> handleEitherField( "right", either.getRight(), property, context ) )
            .ifPresent( submodelElements::add );

      final SubmodelElementList eitherSubModelElements =
            new DefaultSubmodelElementList.Builder()
                  .idShort( either.getName() )
                  .typeValueListElement( AasSubmodelElements.DATA_ELEMENT )
                  .displayName( LangStringMapper.NAME.map( either.getPreferredNames() ) )
                  .description( LangStringMapper.TEXT.map( either.getDescriptions() ) )
                  .value( submodelElements )
                  .supplementalSemanticIds( buildGlobalReferenceForSeeReferences( either ) )
                  .build();
      context.setPropertyResult( eitherSubModelElements );
      return context.environment;
   }

   /**
    * Handles one {@code Either} field depending on whether a submodel template or a submodel is generated. In the latter case, a synthetic
    * property is used to access the serialized data and retrieve the value to be added.
    *
    * @param field the name of the {@code Either} field ({@code left} or {@code right})
    * @param fieldCharacteristic the characteristic of the {@code Either} field
    * @param eitherProperty the {@code Either} property itself
    * @param context the current visitor context
    * @return the submodel element for the {@code Either} field
    */
   private Optional<SubmodelElement> handleEitherField( final String field, final Characteristic fieldCharacteristic,
         final Property eitherProperty, final Context context ) {
      Optional<SubmodelElement> result = Optional.empty();
      if ( context.getModelingKind().equals( ModellingKind.INSTANCE ) ) {
         final Property fieldProperty = createProperty( eitherProperty.getMetaModelVersion(), field, fieldCharacteristic );
         context.setProperty( fieldProperty );
         if ( context.getRawPropertyValue().isPresent() ) {
            result = fieldCharacteristic.getDataType().map( dataType -> decideOnMapping( dataType, context.getProperty(), context ) );
         }
         context.getPropertyResult();
      } else {
         result = fieldCharacteristic.getDataType().map( dataType -> decideOnMapping( dataType, context.getProperty(), context ) );
      }

      return result;
   }

   private Property createProperty( final KnownVersion modelVersion, final String propertyName, final Characteristic characteristic ) {
      final MetaModelBaseAttributes propertyAttributes =
            MetaModelBaseAttributes.from( modelVersion, AspectModelUrn.fromUrn( new SAMM( modelVersion ).Property().getURI() ),
                  propertyName );
      return new org.eclipse.esmf.metamodel.impl.DefaultProperty( propertyAttributes, Optional.of( characteristic ), Optional.empty(), true,
            false,
            Optional.empty(), false, Optional.empty() );
   }

   @Override
   public Environment visitQuantifiable( final Quantifiable quantifiable, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );

      if ( quantifiable.getUnit().isPresent() ) {
         final ConceptDescription conceptDescription =
               context.getConceptDescription( DEFAULT_MAPPER.determineIdentifierFor( context.getProperty() ) );
         final List<EmbeddedDataSpecification> embeddedDataSpecification =
               conceptDescription.getEmbeddedDataSpecifications();
         if ( embeddedDataSpecification.stream().findFirst().isPresent() ) {
            final DataSpecificationIec61360 dataSpecificationContent =
                  (DataSpecificationIec61360) embeddedDataSpecification.stream().findFirst().get().getDataSpecificationContent();
            dataSpecificationContent.setUnit( quantifiable.getUnit().get().getName() );
         }
      }
      return context.environment;
   }

   @Override
   public Environment visitMeasurement( final Measurement measurement, final Context context ) {
      // No special handling required can use Quantifiable mapping implementation
      return visitQuantifiable( measurement, context );
   }

   @Override
   public Environment visitDuration( final Duration duration, final Context context ) {
      // No special handling required can use Quantifiable mapping implementation
      return visitQuantifiable( duration, context );
   }

   @Override
   public Environment visitEnumeration( final Enumeration enumeration, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );

      final ConceptDescription conceptDescription =
            context.getConceptDescription( DEFAULT_MAPPER.determineIdentifierFor( context.getProperty() ) );
      final List<EmbeddedDataSpecification> embeddedDataSpecification =
            conceptDescription.getEmbeddedDataSpecifications();
      if ( embeddedDataSpecification.stream().findFirst().isPresent() ) {
         final DataSpecificationIec61360 dataSpecificationContent =
               (DataSpecificationIec61360) embeddedDataSpecification.stream().findFirst().get().getDataSpecificationContent();
         dataSpecificationContent.setDataType( mapIec61360DataType( enumeration ) );
         final List<ValueReferencePair> valueReferencePairs =
               enumeration.getValues().stream()
                     .map( enumerationValue -> {
                        final String value = enumerationValue.accept( VALUE_SERIALIZER, enumeration );
                        return new DefaultValueReferencePair.Builder()
                              .value( value )
                              .valueId( buildReferenceToEnumValue( enumeration, value ) )
                              .build();
                     } )
                     .collect( Collectors.toList() );

         final ValueList valueList = new DefaultValueList.Builder().valueReferencePairs( valueReferencePairs ).build();
         dataSpecificationContent.setValueList( valueList );
      }

      return context.environment;
   }

   @Override
   public Environment visitState( final State state, final Context context ) {
      // Same handling as with enumerations
      return visitEnumeration( state, context );
   }

   @Override
   public Environment visitSingleEntity( final SingleEntity singleEntity, final Context context ) {
      // Same handling as characteristics
      return visitCharacteristic( singleEntity, context );
   }

   @Override
   public Environment visitStructuredValue( final StructuredValue structuredValue, final Context context ) {
      // https://eclipse-esmf.github.io/samm-specification/snapshot/characteristics.html#structured-value-characteristic
      // AAS cannot handle structuredValues, so we can handle them as ordinary Characteristics
      return visitCharacteristic( structuredValue, context );
   }

   @Override
   public Environment visitCode( final Code code, final Context context ) {
      // is handled similar to a Characteristic. Therefore, no special handling implemented
      return visitCharacteristic( code, context );
   }

   @Override
   public Environment visitTrait( final Trait trait, final Context context ) {
      // AAS submodel templates do not support the specification of constraints for SubmodelElements.
      // Hence, they will be
      // ignored and have to be deduced by resolving a SAMM model referenced by its semanticID
      return visitCharacteristic( trait.getBaseCharacteristic(), context );
   }

   public static class ValueSerializer implements AspectVisitor<String, ModelElement> {
      @Override
      public String visitBase( final ModelElement modelElement, final ModelElement context ) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String visitScalarValue( final ScalarValue value, final ModelElement context ) {
         return context.is( Characteristic.class )
               ? value.getValue().toString()
               : "\"" + value.getValue().toString() + "\"";
      }

      @Override
      public String visitEntityInstance( final EntityInstance instance, final ModelElement context ) {
         return instance.getAssertions().entrySet().stream().map( entry ->
                     String.format( "\"%s\":%s", entry.getKey().getName(), entry.getValue().accept( this, instance ) ) )
               .collect( Collectors.joining( ",", "{", "}" ) );
      }

      @Override
      public String visitCollectionValue( final CollectionValue value, final ModelElement context ) {
         return value.getValues().stream().map( collectionValue ->
                     collectionValue.accept( this, value ) )
               .collect( Collectors.joining( ",", "[", "]" ) );
      }
   }
}
