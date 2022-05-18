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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import io.adminshell.aas.v3.model.Asset;
import io.adminshell.aas.v3.model.AssetAdministrationShell;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.ConceptDescription;
import io.adminshell.aas.v3.model.DataSpecificationContent;
import io.adminshell.aas.v3.model.DataSpecificationIEC61360;
import io.adminshell.aas.v3.model.DataTypeIEC61360;
import io.adminshell.aas.v3.model.EmbeddedDataSpecification;
import io.adminshell.aas.v3.model.Identifier;
import io.adminshell.aas.v3.model.IdentifierType;
import io.adminshell.aas.v3.model.Key;
import io.adminshell.aas.v3.model.KeyElements;
import io.adminshell.aas.v3.model.KeyType;
import io.adminshell.aas.v3.model.LangString;
import io.adminshell.aas.v3.model.ModelingKind;
import io.adminshell.aas.v3.model.Operation;
import io.adminshell.aas.v3.model.OperationVariable;
import io.adminshell.aas.v3.model.Reference;
import io.adminshell.aas.v3.model.Submodel;
import io.adminshell.aas.v3.model.SubmodelElement;
import io.adminshell.aas.v3.model.SubmodelElementCollection;
import io.adminshell.aas.v3.model.ValueList;
import io.adminshell.aas.v3.model.ValueReferencePair;
import io.adminshell.aas.v3.model.impl.DefaultAsset;
import io.adminshell.aas.v3.model.impl.DefaultAssetAdministrationShell;
import io.adminshell.aas.v3.model.impl.DefaultAssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.impl.DefaultConceptDescription;
import io.adminshell.aas.v3.model.impl.DefaultDataSpecificationIEC61360;
import io.adminshell.aas.v3.model.impl.DefaultEmbeddedDataSpecification;
import io.adminshell.aas.v3.model.impl.DefaultIdentifier;
import io.adminshell.aas.v3.model.impl.DefaultKey;
import io.adminshell.aas.v3.model.impl.DefaultOperation;
import io.adminshell.aas.v3.model.impl.DefaultOperationVariable;
import io.adminshell.aas.v3.model.impl.DefaultProperty;
import io.adminshell.aas.v3.model.impl.DefaultReference;
import io.adminshell.aas.v3.model.impl.DefaultSubmodel;
import io.adminshell.aas.v3.model.impl.DefaultSubmodelElementCollection;
import io.adminshell.aas.v3.model.impl.DefaultValueList;
import io.adminshell.aas.v3.model.impl.DefaultValueReferencePair;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Code;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.Duration;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.metamodel.Measurement;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Quantifiable;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.State;
import io.openmanufacturing.sds.metamodel.StructuredValue;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;
import io.vavr.collection.Stream;

public class AspectModelAASVisitor
      implements AspectVisitor<AssetAdministrationShellEnvironment, Context> {

   private static final Logger LOG = LoggerFactory.getLogger( AspectModelAASVisitor.class );

   public static final String ADMIN_SHELL_NAME = "defaultAdminShell";
   public static final String ASSET_NAME = "defaultAsset";
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

   public static final String UNKNOWN_TYPE = "Unknown";
   private static final String UNKNOWN_EXAMPLE = UNKNOWN_TYPE;

   private final Set<Property> recursiveProperty = new HashSet<>();

   @Override
   public AssetAdministrationShellEnvironment visitBase( final Base base, final Context context ) {
      return new DefaultAssetAdministrationShellEnvironment.Builder().build();
   }

   @Override
   public AssetAdministrationShellEnvironment visitAspect( final Aspect aspect, Context context ) {

      if ( context == null ) {
         final Submodel submodel = new DefaultSubmodel.Builder().build();
         final AssetAdministrationShellEnvironment environment =
               new DefaultAssetAdministrationShellEnvironment.Builder().submodels( submodel ).build();
         context = new Context( environment, submodel );
      }

      final Submodel submodel = context.getSubmodel();

      submodel.setIdShort( aspect.getName() );
      submodel.setSemanticId( buildReferenceToConceptDescription( aspect ) );
      submodel.setDescriptions( map( aspect.getDescriptions() ) );
      submodel.setKind( ModelingKind.TEMPLATE );

      createConceptDescription( aspect, context );

      final AssetAdministrationShell administrationShell =
            new DefaultAssetAdministrationShell.Builder().idShort( ADMIN_SHELL_NAME ).build();
      context
            .getEnvironment()
            .setAssetAdministrationShells( Collections.singletonList( administrationShell ) );

      final Asset asset = new DefaultAsset.Builder().idShort( ASSET_NAME ).build();
      context.getEnvironment().setAssets( Collections.singletonList( asset ) );

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
      return elements.stream().map( i -> map( i, context ) ).collect( Collectors.toList() );
   }

   private SubmodelElement map( final Property property, final Context context ) {
      if ( recursiveProperty.contains( property ) ) {
         // The guard checks for recursion in properties. If a recursion happens, the respective
         // property will be excluded from generation.
         recursiveProperty.remove( property );
         if ( property.isOptional() ) {
            LOG.warn(
                  String.format(
                        "Having a recursive Property %s which is optional. Will be excluded from AAS mapping.",
                        property ) );
            return context.getSubmodel().getSubmodelElements().stream()
                  .filter( i -> i.getIdShort().equals( property.getName() ) )
                  .findFirst()
                  .orElse( new DefaultProperty.Builder().build() );
         } else {
            throw new IllegalArgumentException(
                  String.format(
                        "Having a recursive Property: %s which is not optional is not valid.", property ) );
         }
      }
      recursiveProperty.add( property );

      // Characteristic defines how the property is mapped to SubmodelElement
      final Characteristic characteristic = property.getCharacteristic();

      context.setProperty( property );
      characteristic.accept( this, context );
      SubmodelElement element = context.getPropertyResult();

      recursiveProperty.remove( property );
      return element;
   }

   private SubmodelElement decideOnMapping( final Property property, final Context context ) {
      if ( property.getCharacteristic().getDataType().isEmpty() ) {
         return new DefaultProperty.Builder().build();
      }

      final Type type = property.getCharacteristic().getDataType().get();
      return decideOnMapping( type, property, context );
   }

   private SubmodelElement decideOnMapping(
         final Type type, final Property property, final Context context ) {
      if ( type instanceof Entity ) {
         return mapToAasSubModelElementCollection( (Entity) type, context );
      } else {
         return mapToAasProperty( property );
      }
   }

   private SubmodelElementCollection mapToAasSubModelElementCollection(
         final Entity entity, final Context context ) {
      final List<SubmodelElement> submodelElements =
            visitProperties( entity.getAllProperties(), context );
      return new DefaultSubmodelElementCollection.Builder()
            .idShort( entity.getName() )
            .displayNames( map( entity.getPreferredNames() ) )
            .descriptions( map( entity.getDescriptions() ) )
            .values( submodelElements )
            .build();
   }

   private io.adminshell.aas.v3.model.Property mapToAasProperty( final Property property ) {
      return new DefaultProperty.Builder()
            .idShort( property.getName() )
            .kind( ModelingKind.TEMPLATE )
            .valueType(
                  property.getCharacteristic().getDataType().isPresent()
                        ? mapType( property.getCharacteristic().getDataType().get() )
                        : UNKNOWN_TYPE )
            .displayNames( map( property.getPreferredNames() ) )
            .value(
                  property.getExampleValue().isPresent()
                        ? property.getExampleValue().get().toString()
                        : UNKNOWN_EXAMPLE )
            .descriptions( map( property.getDescriptions() ) )
            .semanticId(
                  buildReferenceToConceptDescription(
                        property ) ) // this is the link to the conceptDescription containing the details for
            // the Characteristic
            .build();
   }

   private Identifier extractIdentifier( final IsDescribed element ) {
      return new DefaultIdentifier.Builder()
            .identifier( determineIdentifierFor( element ) )
            .idType( IdentifierType.CUSTOM )
            .build();
   }

   private String mapType( final Type type ) {
      return type.getUrn();
   }

   private Operation map(
         final io.openmanufacturing.sds.metamodel.Operation operation, final Context context ) {
      return new DefaultOperation.Builder()
            .displayNames( map( operation.getPreferredNames() ) )
            .descriptions( map( operation.getDescriptions() ) )
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
      return new DefaultOperationVariable.Builder().value( map( property, context ) ).build();
   }

   private List<LangString> map(
         final Set<io.openmanufacturing.sds.metamodel.datatypes.LangString> localizedStrings ) {
      return localizedStrings.stream()
            .map( ( entry ) -> map( entry.getLanguageTag(), entry.getValue() ) )
            .collect( Collectors.toList() );
   }

   private LangString map( final Locale locale, final String value ) {
      final LangString langString = new LangString();
      langString.setLanguage( locale.getLanguage() );
      langString.setValue( value );
      return langString;
   }

   private Reference buildReferenceToEnumValue( final Enumeration enumeration, final Object value ) {
      final Key key =
            new DefaultKey.Builder()
                  .idType( KeyType.CUSTOM )
                  .type( KeyElements.DATA_ELEMENT )
                  .value( determineIdentifierFor( enumeration ) + ":" + value.toString() )
                  .build();
      return new DefaultReference.Builder().key( key ).build();
   }

   private Reference buildReferenceToConceptDescription( Aspect aspect ) {
      final Key key =
            new DefaultKey.Builder()
                  .idType( KeyType.CUSTOM )
                  .type( KeyElements.CONCEPT_DESCRIPTION )
                  .value( extractIdentifier( aspect ).getIdentifier() )
                  .build();
      return new DefaultReference.Builder().key( key ).build();
   }

   private Reference buildReferenceToConceptDescription( final Property property ) {
      final Key key =
            new DefaultKey.Builder()
                  .idType( KeyType.CUSTOM )
                  .type( KeyElements.CONCEPT_DESCRIPTION )
                  .value( extractIdentifier( property ).getIdentifier() )
                  .build();
      return new DefaultReference.Builder().key( key ).build();
   }

   private String determineIdentifierFor( final IsDescribed isDescribed ) {
      if ( isDescribed.getAspectModelUrn().isPresent() ) {
         return isDescribed.getAspectModelUrn().get().toString();
      } else {
         return isDescribed.getName();
      }
   }

   private void createConceptDescription( final Property property, final Context context ) {
      final Characteristic characteristic = property.getCharacteristic();
      // check if the concept description is already created. If not create a new one.
      if ( !context.hasEnvironmentConceptDescription( property.getAspectModelUrn().toString() ) ) {
         final ConceptDescription conceptDescription =
               new DefaultConceptDescription.Builder()
                     .idShort( characteristic.getName() )
                     .displayNames( map( characteristic.getPreferredNames() ) )
                     .embeddedDataSpecification( extractEmbeddedDataSpecification( property ) )
                     .identification( extractIdentifier( property ) )
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
                     .displayNames( map( aspect.getPreferredNames() ) )
                     .embeddedDataSpecification( extractEmbeddedDataSpecification( aspect ) )
                     .identification( extractIdentifier( aspect ) )
                     .descriptions( map( aspect.getDescriptions() ) )
                     .category( CONCEPT_DESCRIPTION_CATEGORY )
                     .build();
         context.getEnvironment().getConceptDescriptions().add( conceptDescription );
      }
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification( final Property property ) {
      return new DefaultEmbeddedDataSpecification.Builder()
            .dataSpecificationContent( extractDataSpecificationContent( property ) )
            .build();
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification( final Aspect aspect ) {
      return new DefaultEmbeddedDataSpecification.Builder()
            .dataSpecificationContent( extractDataSpecificationContent( aspect ) )
            .build();
   }

   private DataSpecificationContent extractDataSpecificationContent( final Property property ) {

      final List<LangString> definitions = map( property.getCharacteristic().getDescriptions() );
      definitions.addAll( map( property.getDescriptions() ) );

      return new DefaultDataSpecificationIEC61360.Builder()
            .definitions( definitions )
            .preferredNames( map( property.getPreferredNames() ) )
            .shortName( new LangString( property.getName(), DEFAULT_LOCALE ) )
            .dataType( mapIEC61360DataType( property.getCharacteristic() ) )
            .build();
   }

   private DataSpecificationContent extractDataSpecificationContent( final Aspect aspect ) {

      final List<LangString> definitions = map( aspect.getDescriptions() );

      return new DefaultDataSpecificationIEC61360.Builder()
            .definitions( definitions )
            .preferredNames( map( aspect.getPreferredNames() ) )
            .shortName( new LangString( aspect.getName(), DEFAULT_LOCALE ) )
            .build();
   }

   private void createSubmodelElement( final SubmodelElementBuilder op, final Context context ) {
      final Property property = context.getProperty();
      final SubmodelElement submodelElement = op.build( property );
      context.setPropertyResult( submodelElement );
      createConceptDescription( property, context );
   }

   private DataTypeIEC61360 mapIEC61360DataType( final Characteristic characteristic ) {
      final String urn =
            characteristic.getDataType().isPresent()
                  ? characteristic.getDataType().get().getUrn()
                  : RDF.langString.getURI();
      return mapIEC61360DataType( urn );
   }

   private DataTypeIEC61360 mapIEC61360DataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return TYPE_MAP.getOrDefault( resource, DataTypeIEC61360.STRING );
   }

   @Override
   public AssetAdministrationShellEnvironment visitCharacteristic(
         final Characteristic characteristic, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );
      return context.environment;
   }

   @Override
   public AssetAdministrationShellEnvironment visitCollection(
         final Collection collection, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementCollection.Builder()
                        .idShort( property.getName() )
                        .displayNames( map( property.getPreferredNames() ) )
                        .descriptions( map( property.getDescriptions() ) )
                        .values( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .build();

      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   @Override
   public AssetAdministrationShellEnvironment visitList(
         final io.openmanufacturing.sds.metamodel.List list, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementCollection.Builder()
                        .idShort( property.getName() )
                        .displayNames( map( property.getPreferredNames() ) )
                        .descriptions( map( property.getDescriptions() ) )
                        .values( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .ordered( true )
                        .build();
      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   @Override
   public AssetAdministrationShellEnvironment visitSet(
         final io.openmanufacturing.sds.metamodel.Set set, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementCollection.Builder()
                        .idShort( property.getName() )
                        .displayNames( map( property.getPreferredNames() ) )
                        .descriptions( map( property.getDescriptions() ) )
                        .values( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .ordered( false )
                        .allowDuplicates( false )
                        .build();
      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   @Override
   public AssetAdministrationShellEnvironment visitSortedSet(
         final SortedSet sortedSet, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementCollection.Builder()
                        .idShort( property.getName() )
                        .displayNames( map( property.getPreferredNames() ) )
                        .descriptions( map( property.getDescriptions() ) )
                        .values( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .ordered( true )
                        .allowDuplicates( false )
                        .build();
      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   // Either will be mapped by adding both the left and the right side to the SubmodelTemplate.
   // No possibilities to mark the SubmodelElements as optional. So both are either options are
   // in the result and have to be manually selected.
   @Override
   public AssetAdministrationShellEnvironment visitEither(
         final Either either, final Context context ) {
      final List<SubmodelElement> submodelElements = new ArrayList<>();
      if ( either.getLeft().getDataType().isPresent() ) {
         submodelElements.add(
               decideOnMapping( either.getLeft().getDataType().get(), context.getProperty(), context ) );
      }
      if ( either.getRight().getDataType().isPresent() ) {
         submodelElements.add(
               decideOnMapping( either.getRight().getDataType().get(), context.getProperty(), context ) );
      }
      final SubmodelElementCollection aasSubModelElementCollection =
            new DefaultSubmodelElementCollection.Builder()
                  .idShort( either.getName() )
                  .displayNames( map( either.getPreferredNames() ) )
                  .descriptions( map( either.getDescriptions() ) )
                  .values( submodelElements )
                  .build();
      context.setPropertyResult( aasSubModelElementCollection );
      return context.environment;
   }

   @Override
   public AssetAdministrationShellEnvironment visitQuantifiable(
         final Quantifiable quantifiable, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );

      if ( quantifiable.getUnit().isPresent() ) {
         final ConceptDescription conceptDescription =
               context.getConceptDescription( determineIdentifierFor( context.getProperty() ) );
         final List<EmbeddedDataSpecification> embeddedDataSpecification =
               conceptDescription.getEmbeddedDataSpecifications();
         if ( embeddedDataSpecification.stream().findFirst().isPresent() ) {
            final DataSpecificationIEC61360 dataSpecificationContent =
                  (DataSpecificationIEC61360)
                        embeddedDataSpecification.stream().findFirst().get().getDataSpecificationContent();
            dataSpecificationContent.setUnit( quantifiable.getUnit().get().getName() );
         }
      }
      return context.environment;
   }

   @Override
   public AssetAdministrationShellEnvironment visitMeasurement(
         final Measurement measurement, final Context context ) {
      // No special handling required can use Quantifiable mapping implementation
      return visitQuantifiable( measurement, context );
   }

   @Override
   public AssetAdministrationShellEnvironment visitDuration(
         final Duration duration, final Context context ) {
      // No special handling required can use Quantifiable mapping implementation
      return visitQuantifiable( duration, context );
   }

   @Override
   public AssetAdministrationShellEnvironment visitEnumeration(
         final Enumeration enumeration, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );

      final ConceptDescription conceptDescription =
            context.getConceptDescription( determineIdentifierFor( context.getProperty() ) );
      final List<EmbeddedDataSpecification> embeddedDataSpecification =
            conceptDescription.getEmbeddedDataSpecifications();
      if ( embeddedDataSpecification.stream().findFirst().isPresent() ) {
         final DataSpecificationIEC61360 dataSpecificationContent =
               (DataSpecificationIEC61360)
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

         ValueList valueList =
               new DefaultValueList.Builder().valueReferencePairTypes( valueReferencePairs ).build();

         dataSpecificationContent.setValueList( valueList );
      }

      return context.environment;
   }

   @Override
   public AssetAdministrationShellEnvironment visitState( final State state, final Context context ) {
      // Same handling as with enumerations
      return visitEnumeration( state, context );
   }

   @Override
   public AssetAdministrationShellEnvironment visitSingleEntity(
         final SingleEntity singleEntity, final Context context ) {
      // Same handling as characteristics
      return visitCharacteristic( singleEntity, context );
   }

   @Override
   public AssetAdministrationShellEnvironment visitStructuredValue(
         final StructuredValue structuredValue, final Context context ) {
      // https://openmanufacturingplatform.github.io/sds-documentation/bamm-specification/v1.0.0/modeling-guidelines.html#declaring-structured-value
      // AAS cannot handle structuredValues, so we can handle them as ordinary Characteristics
      return visitCharacteristic( structuredValue, context );
   }

   @Override
   public AssetAdministrationShellEnvironment visitCode( final Code code, final Context context ) {
      // is handled similar to a Characteristic. Therefore, no special handling implemented
      return visitCharacteristic( code, context );
   }

   @Override
   public AssetAdministrationShellEnvironment visitTrait( final Trait trait, final Context context ) {
      // AAS submodel templates do not support the specification of constraints for SubmodelElements.
      // Hence, they will be
      // ignored and have to be deduced by resolving a BAMM model referenced by its semanticID
      return visitCharacteristic( trait.getBaseCharacteristic(), context );
   }
}
