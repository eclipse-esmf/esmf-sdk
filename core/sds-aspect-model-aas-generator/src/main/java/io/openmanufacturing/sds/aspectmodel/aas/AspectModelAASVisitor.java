/*
 * Copyright (c) 2021, 2022 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.aspectmodel.aas;

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

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.DataSpecificationIEC61360;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeIEC61360;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.ModelingKind;
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
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultDataSpecificationIEC61360;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.characteristic.Code;
import io.openmanufacturing.sds.characteristic.Collection;
import io.openmanufacturing.sds.characteristic.Duration;
import io.openmanufacturing.sds.characteristic.Either;
import io.openmanufacturing.sds.characteristic.Enumeration;
import io.openmanufacturing.sds.characteristic.Measurement;
import io.openmanufacturing.sds.characteristic.Quantifiable;
import io.openmanufacturing.sds.characteristic.SingleEntity;
import io.openmanufacturing.sds.characteristic.SortedSet;
import io.openmanufacturing.sds.characteristic.State;
import io.openmanufacturing.sds.characteristic.StructuredValue;
import io.openmanufacturing.sds.characteristic.Trait;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.ModelElement;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class AspectModelAASVisitor implements AspectVisitor<Environment, Context> {

   private static final Logger LOG = LoggerFactory.getLogger( AspectModelAASVisitor.class );

   public static final String ADMIN_SHELL_NAME = "defaultAdminShell";
   public static final String DEFAULT_LOCALE = "EN";
   public static final String CONCEPT_DESCRIPTION_CATEGORY = "APPLICATION_CLASS";

   /**
    * Maps Aspect types to DataTypeIEC61360 Schema types, with no explicit mapping defaulting to
    * string
    */
   private static final Map<Resource, DataTypeIEC61360> TYPE_MAP =
         ImmutableMap.<Resource, DataTypeIEC61360> builder()
               .put( XSD.xboolean, DataTypeIEC61360.BOOLEAN )
               .put( XSD.decimal, DataTypeIEC61360.INTEGER_MEASURE )
               .put( XSD.integer, DataTypeIEC61360.INTEGER_MEASURE )
               .put( XSD.xfloat, DataTypeIEC61360.REAL_MEASURE )
               .put( XSD.xdouble, DataTypeIEC61360.REAL_MEASURE )
               .put( XSD.xbyte, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.xshort, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.xint, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.xlong, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.unsignedByte, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.unsignedShort, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.unsignedInt, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.unsignedLong, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.positiveInteger, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.nonPositiveInteger, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.negativeInteger, DataTypeIEC61360.INTEGER_COUNT )
               .put( XSD.nonNegativeInteger, DataTypeIEC61360.INTEGER_COUNT )
               .put( RDF.langString, DataTypeIEC61360.STRING )
               .build();

   private interface SubmodelElementBuilder {
      SubmodelElement build( Property property );
   }

   private final Set<Property> recursiveProperty = new HashSet<>();

   private static final DefaultPropertyMapper DEFAULT_MAPPER = new DefaultPropertyMapper();

   private final List<PropertyMapper<?>> customPropertyMappers = new ArrayList<>();

   private final LangStringMapper langStringMapper = new LangStringMapper();

   public AspectModelAASVisitor withPropertyMapper( final PropertyMapper propertyMapper ) {
      customPropertyMappers.add( propertyMapper );

      return this;
   }

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

   @Override
   public Environment visitAspect( final Aspect aspect, Context context ) {
      if ( context == null ) {
         final Submodel submodel = new DefaultSubmodel.Builder().build();
         final Environment environment = new DefaultEnvironment.Builder().submodels( Collections.singletonList( submodel ) ).build();
         context = new Context( environment, submodel );
         context.setEnvironment( environment );
      }

      final Submodel submodel = context.getSubmodel();
      submodel.setIdShort( aspect.getName() );
      submodel.setId( aspect.getAspectModelUrn().toString() + "/submodel" );
      submodel.setSemanticId( buildReferenceToConceptDescription( aspect ) );
      submodel.setDescription( langStringMapper.map( aspect.getDescriptions() ) );
      submodel.setKind( context.getModelingKind() );
      submodel.setAdministration( new DefaultAdministrativeInformation.Builder().build() );
      submodel.setChecksum( String.valueOf( aspect.hashCode() ) );

      createConceptDescription( aspect, context );

      final AssetAdministrationShell administrationShell =
            new DefaultAssetAdministrationShell.Builder()
                  .id( DEFAULT_MAPPER.determineIdentifierFor( aspect ) )
                  .idShort( ADMIN_SHELL_NAME )
                  .description( langStringMapper.createLangString( ADMIN_SHELL_NAME, "en" ) )
                  .checksum( "a checksum" )
                  .administration( new DefaultAdministrativeInformation.Builder().build() )
                  .assetInformation( new DefaultAssetInformation.Builder()
                        .assetKind( context.getAssetKind() )
                        .build() )
                  .embeddedDataSpecifications( extractEmbeddedDataSpecification( aspect ) )
                  .build();
      context
            .getEnvironment()
            .setAssetAdministrationShells( Collections.singletonList( administrationShell ) );

      context.appendToSubModelElements( visitProperties( aspect.getProperties(), context ) );
      context.appendToSubModelElements( visitOperations( aspect.getOperations(), context ) );

      return context.getEnvironment();
   }

   private List<SubmodelElement> visitOperations(
         final List<io.openmanufacturing.sds.metamodel.Operation> elements, final Context context ) {
      return elements.stream().map( i -> map( i, context ) ).collect( Collectors.toList() );
   }

   private List<SubmodelElement> visitProperties(
         final List<Property> elements, final Context context ) {
      return elements.stream().map( i -> map( i, context ) )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .collect( Collectors.toList() );
   }

   private Optional<SubmodelElement> map( final Property property, final Context context ) {
      final Optional<SubmodelElement> defaultResultForProperty = context.getSubmodel()
            .getSubmodelElements().stream()
            .filter( i -> i.getIdShort().equals( property.getName() ) )
            .findFirst();
      if ( recursiveProperty.contains( property ) ) {
         // The guard checks for recursion in properties. If a recursion happens, the respective
         // property will be excluded from generation.
         recursiveProperty.remove( property );
         if ( property.isOptional() ) {
            LOG.warn( String.format( "Having a recursive Property %s which is optional. Will be excluded from AAS mapping.", property ) );
         } else {
            LOG.error( String.format(
                  "Having a recursive Property: %s which is not optional is not valid. Check the model. Property will be excluded from AAS mapping.",
                  property ) );
         }
         return defaultResultForProperty;
      }
      recursiveProperty.add( property );

      if ( property.getCharacteristic().isEmpty() || property.isAbstract() ) {
         LOG.warn( String.format( "Having an Abstract Property. Will be excluded from AAS mapping." ) );
         return Optional.empty();
      }

      // Characteristic defines how the property is mapped to SubmodelElement
      final Characteristic characteristic = property.getCharacteristic().get();

      context.setProperty( property );
      characteristic.accept( this, context );
      final SubmodelElement element = context.getPropertyResult();

      recursiveProperty.remove( property );

      return Optional.of( element );
   }

   private SubmodelElement decideOnMapping( final Property property, final Context context ) {
      if ( property.getCharacteristic().flatMap( Characteristic::getDataType ).isEmpty() ) {
         return new DefaultProperty.Builder().build();
      }

      final Type type = property.getCharacteristic().get().getDataType().get();
      return decideOnMapping( type, property, context );
   }

   private SubmodelElement decideOnMapping(
         final Type type, final Property property, final Context context ) {
      if ( type instanceof Entity ) {
         return mapToAasSubModelElementCollection( (Entity) type, context );
      } else {
         return findPropertyMapper( property ).mapToAasProperty( type, property, context );
      }
   }

   private SubmodelElementCollection mapToAasSubModelElementCollection(
         final Entity entity, final Context context ) {
      final List<SubmodelElement> submodelElements =
            visitProperties( entity.getAllProperties(), context );
      return new DefaultSubmodelElementCollection.Builder()
            .idShort( entity.getName() )
            .displayName( langStringMapper.map( entity.getPreferredNames() ) )
            .description( langStringMapper.map( entity.getDescriptions() ) )
            .value( submodelElements )
            .kind( context.getModelingKind() )
            .build();
   }

   private Operation map( final io.openmanufacturing.sds.metamodel.Operation operation, final Context context ) {
      return new DefaultOperation.Builder()
            .displayName( langStringMapper.map( operation.getPreferredNames() ) )
            .description( langStringMapper.map( operation.getDescriptions() ) )
            .idShort( operation.getName() )
            .inputVariables(
                  operation.getInput().stream()
                        .map( i -> mapOperationVariable( i, context ) )
                        .collect( Collectors.toList() ) )
            .outputVariables(
                  operation.getOutput().stream()
                        .map( i -> mapOperationVariable( i, context ) )
                        .collect( Collectors.toList() ) )
            .build();
   }

   private OperationVariable mapOperationVariable( final Property property, final Context context ) {
      return new DefaultOperationVariable.Builder().value( map( property, context ).get() ).build();
   }

   private Reference buildReferenceToEnumValue( final Enumeration enumeration, final Object value ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.DATA_ELEMENT )
                  .value( DEFAULT_MAPPER.determineIdentifierFor( enumeration ) + ":" + value.toString() )
                  .build();
      return new DefaultReference.Builder().type( ReferenceTypes.MODEL_REFERENCE ).keys( key ).build();
   }

   private Reference buildReferenceToConceptDescription( final Aspect aspect ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.CONCEPT_DESCRIPTION )
                  .value( DEFAULT_MAPPER.determineIdentifierFor( aspect ) )
                  .build();
      return new DefaultReference.Builder().type( ReferenceTypes.MODEL_REFERENCE ).keys( key ).build();
   }

   private Reference buildReferenceForSeeElement( final String seeReference ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.GLOBAL_REFERENCE )
                  .value( seeReference )
                  .build();
      return new DefaultReference.Builder()
            .type( ReferenceTypes.GLOBAL_REFERENCE )
            .keys( key )
            .build();
   }

   private List<Reference> buildReferencesForSeeElements( final List<String> seeReferences ) {
      return seeReferences.stream().map( this::buildReferenceForSeeElement ).collect( Collectors.toList() );
   }

   private void createConceptDescription( final Property property, final Context context ) {
      if ( property.getCharacteristic().isEmpty() ) {
         return;
      }
      final Characteristic characteristic = property.getCharacteristic().get();
      // check if the concept description is already created. If not create a new one.
      if ( !context.hasEnvironmentConceptDescription( property.getAspectModelUrn().toString() ) ) {
         final ConceptDescription conceptDescription =
               new DefaultConceptDescription.Builder()
                     .idShort( characteristic.getName() )
                     .displayName( langStringMapper.map( characteristic.getPreferredNames() ) )
                     .embeddedDataSpecifications( extractEmbeddedDataSpecification( property ) )
                     .id( DEFAULT_MAPPER.determineIdentifierFor( property ) )
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
                     .displayName( langStringMapper.map( aspect.getPreferredNames() ) )
                     .embeddedDataSpecifications( extractEmbeddedDataSpecification( aspect ) )
                     .id( DEFAULT_MAPPER.determineIdentifierFor( aspect ) )
                     .description( langStringMapper.map( aspect.getDescriptions() ) )
                     .category( CONCEPT_DESCRIPTION_CATEGORY )
                     .build();
         context.getEnvironment().getConceptDescriptions().add( conceptDescription );
      }
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification( final Property property ) {
      return new DefaultEmbeddedDataSpecification.Builder()
            .dataSpecification( buildReferenceForSeeElement( property.getAspectModelUrn().toString() ) )
            .dataSpecificationContent( extractDataSpecificationContent( property ) )
            .build();
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification( final Aspect aspect ) {
      return new DefaultEmbeddedDataSpecification.Builder()
            .dataSpecification( buildReferenceForSeeElement( aspect.getAspectModelUrn().toString() ) )
            .dataSpecificationContent( extractDataSpecificationContent( aspect ) )
            .build();
   }

   private DataSpecificationIEC61360 extractDataSpecificationContent( final Property property ) {
      final List<LangString> definitions = property.getCharacteristic().stream().flatMap( characteristic ->
                  characteristic.getDescriptions().stream() )
            .map( langStringMapper::map )
            .collect( Collectors.toList() );

      return new DefaultDataSpecificationIEC61360.Builder()
            .definition( definitions )
            .preferredName( property.getPreferredNames().isEmpty() ?
                  Collections.singletonList( langStringMapper.createLangString( property.getName(), DEFAULT_LOCALE ) ) :
                  langStringMapper.map( property.getPreferredNames() ) )
            .shortName( langStringMapper.createLangString( property.getName(), DEFAULT_LOCALE ) )

            .dataType( mapIEC61360DataType( property.getCharacteristic() ) )
            .build();
   }

   private DataSpecificationIEC61360 extractDataSpecificationContent( final Aspect aspect ) {
      final List<LangString> definitions = langStringMapper.map( aspect.getDescriptions() );

      return new DefaultDataSpecificationIEC61360.Builder()
            .definition( definitions )
            .preferredName( aspect.getPreferredNames().isEmpty() ?
                  Collections.singletonList( langStringMapper.createLangString( aspect.getName(), DEFAULT_LOCALE ) ) :
                  langStringMapper.map( aspect.getPreferredNames() ) )
            .shortName( langStringMapper.createLangString( aspect.getName(), DEFAULT_LOCALE ) )
            .value( aspect.getName() )
            .build();
   }

   private DataTypeIEC61360 mapIEC61360DataType( final Optional<Characteristic> characteristic ) {
      return mapIEC61360DataType( characteristic.flatMap( Characteristic::getDataType ).map( Type::getUrn ).orElse( RDF.langString.getURI() ) );
   }

   private DataTypeIEC61360 mapIEC61360DataType( final Characteristic characteristic ) {
      return mapIEC61360DataType( Optional.of( characteristic ) );
   }

   private DataTypeIEC61360 mapIEC61360DataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return TYPE_MAP.getOrDefault( resource, DataTypeIEC61360.STRING );
   }

   private void createSubmodelElement( final SubmodelElementBuilder op, final Context context ) {
      final Property property = context.getProperty();
      final SubmodelElement submodelElement = op.build( property );
      context.setPropertyResult( submodelElement );
      createConceptDescription( property, context );
   }

   @Override
   public Environment visitCharacteristic(
         final Characteristic characteristic, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );
      return context.getEnvironment();
   }

   @Override
   public Environment visitCollection(
         final Collection collection, final Context context ) {
      return visitCollectionProperty( collection, context );
   }

   @Override
   public Environment visitList(
         final io.openmanufacturing.sds.characteristic.List list, final Context context ) {
      return visitCollectionProperty( list, context );
   }

   @Override
   public Environment visitSet(
         final io.openmanufacturing.sds.characteristic.Set set, final Context context ) {
      return visitCollectionProperty( set, context );
   }

   @Override
   public Environment visitSortedSet(
         final SortedSet sortedSet, final Context context ) {
      return visitCollectionProperty( sortedSet, context );
   }

   private <T extends Collection> Environment visitCollectionProperty(
         final T collection, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementList.Builder()
                        .idShort( property.getName() )
                        .displayName( langStringMapper.map( property.getPreferredNames() ) )
                        .description( langStringMapper.map( property.getDescriptions() ) )
                        .value( List.of( decideOnMapping( property, context ) ) )
                        .build();
      final Optional<JsonNode> rawValue = context.getRawPropertyValue();
      return rawValue.map( node -> {
         if ( node instanceof ArrayNode arrayNode ) {
            final SubmodelElementBuilder listBuilder =
                  ( property ) -> {
                     final var values = getValues( collection, property, context, arrayNode );
                     return new DefaultSubmodelElementList.Builder()
                           .idShort( property.getName() )
                           .displayName( langStringMapper.map( property.getPreferredNames() ) )
                           .description( langStringMapper.map( property.getDescriptions() ) )
                           .value( values )
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
                  final var values = StreamSupport.stream( arrayNode.spliterator(), false )
                        .map( n -> {
                           context.iterate( property );
                           return decideOnMapping( property, context );
                        } )
                        .toList();
                  context.finishIteration( property );
                  return values;
               }
            } ).orElseGet( () -> List.of() );
   }

   // Either will be mapped by adding both the left and the right side to the SubmodelTemplate.
   // No possibilities to mark the SubmodelElements as optional. So both either options are
   // in the result and have to be manually selected.
   // When generating Submodels where data is given however only the present side is added.
   @Override
   public Environment visitEither(
         final Either either, final Context context ) {
      final List<SubmodelElement> submodelElements = new ArrayList<>();
      final var property = context.getProperty();
      either.getLeft().getDataType()
            .flatMap( dataType -> handleEitherField( "left", either.getLeft(), property, context ) )
            .ifPresent( submodelElements::add );
      either.getRight().getDataType()
            .flatMap( dataType -> handleEitherField( "right", either.getRight(), property, context ) )
            .ifPresent( submodelElements::add );

      final SubmodelElementList eitherSubModelElements =
            new DefaultSubmodelElementList.Builder()
                  .idShort( either.getName() )
                  .displayName( langStringMapper.map( either.getPreferredNames() ) )
                  .description( langStringMapper.map( either.getDescriptions() ) )
                  .value( submodelElements )
                  .kind( context.getModelingKind() )
                  .build();
      context.setPropertyResult( eitherSubModelElements );
      return context.environment;
   }

   /**
    * Handles one {@code Either} field depending on whether a submodel template or a submodel is generated.
    *
    * In the latter case, a synthetic property is used to access the serialized data and retrieve the value to be added.
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
      if ( context.getModelingKind().equals( ModelingKind.INSTANCE ) ) {
         final var fieldProperty = createProperty( eitherProperty.getMetaModelVersion(), field, fieldCharacteristic );
         context.setProperty( fieldProperty );
         if ( context.getRawPropertyValue().isPresent() ) {
            result = Optional.ofNullable( decideOnMapping( fieldCharacteristic.getDataType().get(), context.getProperty(), context ) );
         }
         context.getPropertyResult();
      } else {
         result = Optional.ofNullable( decideOnMapping( fieldCharacteristic.getDataType().get(), context.getProperty(), context ) );
      }

      return result;
   }

   private Property createProperty( final KnownVersion modelVersion, final String propertyName, final Characteristic characteristic ) {
      final MetaModelBaseAttributes propertyAttributes =
            MetaModelBaseAttributes.from( modelVersion, AspectModelUrn.fromUrn( new BAMM( modelVersion ).Property().getURI() ), propertyName );
      return new io.openmanufacturing.sds.metamodel.impl.DefaultProperty( propertyAttributes, Optional.of( characteristic ), Optional.empty(), true, false,
            Optional.empty(), false, Optional.empty() );
   }

   @Override
   public Environment visitQuantifiable(
         final Quantifiable quantifiable, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );

      if ( quantifiable.getUnit().isPresent() ) {
         final ConceptDescription conceptDescription =
               context.getConceptDescription( DEFAULT_MAPPER.determineIdentifierFor( context.getProperty() ) );
         final List<EmbeddedDataSpecification> embeddedDataSpecification =
               conceptDescription.getEmbeddedDataSpecifications();
         if ( embeddedDataSpecification.stream().findFirst().isPresent() ) {
            final DataSpecificationIEC61360 dataSpecificationContent =
                  embeddedDataSpecification.stream().findFirst().get().getDataSpecificationContent();
            dataSpecificationContent.setUnit( quantifiable.getUnit().get().getName() );
         }
      }
      return context.environment;
   }

   @Override
   public Environment visitMeasurement(
         final Measurement measurement, final Context context ) {
      // No special handling required can use Quantifiable mapping implementation
      return visitQuantifiable( measurement, context );
   }

   @Override
   public Environment visitDuration(
         final Duration duration, final Context context ) {
      // No special handling required can use Quantifiable mapping implementation
      return visitQuantifiable( duration, context );
   }

   @Override
   public Environment visitEnumeration(
         final Enumeration enumeration, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );

      final ConceptDescription conceptDescription =
            context.getConceptDescription( DEFAULT_MAPPER.determineIdentifierFor( context.getProperty() ) );
      final List<EmbeddedDataSpecification> embeddedDataSpecification =
            conceptDescription.getEmbeddedDataSpecifications();
      if ( embeddedDataSpecification.stream().findFirst().isPresent() ) {
         final DataSpecificationIEC61360 dataSpecificationContent =
               embeddedDataSpecification.stream().findFirst().get().getDataSpecificationContent();
         dataSpecificationContent.setDataType( mapIEC61360DataType( enumeration ) );
         final List<ValueReferencePair> valueReferencePairs =
               enumeration.getValues().stream()
                     .map(
                           x ->
                                 new DefaultValueReferencePair.Builder()
                                       .value( x.toString() )
                                       .valueId( buildReferenceToEnumValue( enumeration, x ) )
                                       .build() )
                     .collect( Collectors.toList() );

         final ValueList valueList =
               new DefaultValueList.Builder().valueReferencePairs( valueReferencePairs ).build();

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
   public Environment visitSingleEntity(
         final SingleEntity singleEntity, final Context context ) {
      // Same handling as characteristics
      return visitCharacteristic( singleEntity, context );
   }

   @Override
   public Environment visitStructuredValue(
         final StructuredValue structuredValue, final Context context ) {
      // https://openmanufacturingplatform.github.io/sds-documentation/bamm-specification/v1.0.0/modeling-guidelines.html#declaring-structured-value
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
      // ignored and have to be deduced by resolving a BAMM model referenced by its semanticID
      return visitCharacteristic( trait.getBaseCharacteristic(), context );
   }
}
