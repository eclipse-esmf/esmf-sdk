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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.digitaltwin.aas4j.v3.model.AASSubmodelElements;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXSD;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
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
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultDataSpecificationIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultValueList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultValueReferencePair;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

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
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class AspectModelAASVisitor implements AspectVisitor<Environment, Context> {

   private static final Logger LOG = LoggerFactory.getLogger( AspectModelAASVisitor.class );

   public static final String ADMIN_SHELL_NAME = "defaultAdminShell";
   public static final String DEFAULT_LOCALE = "EN";
   public static final String CONCEPT_DESCRIPTION_CATEGORY = "APPLICATION_CLASS";
   public static final String ID_PREFIX = "id_";

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

   /**
    * Maps Aspect types to DataTypeDefXsd Schema types, with no explicit mapping defaulting to
    * string
    */
   private static final Map<Resource, DataTypeDefXSD> AAS_XSD_TYPE_MAP =
         ImmutableMap.<Resource, DataTypeDefXSD> builder()
               .put( XSD.anyURI, DataTypeDefXSD.ANY_URI )
               .put( XSD.yearMonthDuration, DataTypeDefXSD.DURATION )
               .put( XSD.xboolean, DataTypeDefXSD.BOOLEAN )
               .put( XSD.xbyte, DataTypeDefXSD.BYTE )
               .put( XSD.date, DataTypeDefXSD.DATE )
               .put( XSD.dateTime, DataTypeDefXSD.DATE_TIME )
               .put( XSD.dateTimeStamp, DataTypeDefXSD.DATE_TIME )
               .put( XSD.dayTimeDuration, DataTypeDefXSD.DURATION )
               .put( XSD.decimal, DataTypeDefXSD.DECIMAL )
               .put( XSD.xdouble, DataTypeDefXSD.DOUBLE )
               .put( XSD.duration, DataTypeDefXSD.DURATION )
               .put( XSD.xfloat, DataTypeDefXSD.FLOAT )
               .put( XSD.gMonth, DataTypeDefXSD.GMONTH )
               .put( XSD.gMonthDay, DataTypeDefXSD.GMONTH_DAY )
               .put( XSD.gYear, DataTypeDefXSD.GYEAR )
               .put( XSD.gYearMonth, DataTypeDefXSD.GYEAR_MONTH )
               .put( XSD.hexBinary, DataTypeDefXSD.HEX_BINARY )
               .put( XSD.xint, DataTypeDefXSD.INT )
               .put( XSD.integer, DataTypeDefXSD.INTEGER )
               .put( XSD.xlong, DataTypeDefXSD.LONG )
               .put( XSD.negativeInteger, DataTypeDefXSD.NEGATIVE_INTEGER )
               .put( XSD.nonNegativeInteger, DataTypeDefXSD.NON_NEGATIVE_INTEGER )
               .put( XSD.positiveInteger, DataTypeDefXSD.POSITIVE_INTEGER )
               .put( XSD.xshort, DataTypeDefXSD.SHORT )
               .put( XSD.normalizedString, DataTypeDefXSD.STRING )
               .put( XSD.time, DataTypeDefXSD.TIME )
               .put( XSD.unsignedByte, DataTypeDefXSD.UNSIGNED_BYTE )
               .put( XSD.unsignedInt, DataTypeDefXSD.UNSIGNED_INT )
               .put( XSD.unsignedLong, DataTypeDefXSD.UNSIGNED_LONG )
               .put( XSD.unsignedShort, DataTypeDefXSD.UNSIGNED_SHORT )
               .build();

   private interface SubmodelElementBuilder {
      SubmodelElement build( Property property );
   }

   public static final String UNKNOWN_TYPE = "Unknown";
   private static final String UNKNOWN_EXAMPLE = UNKNOWN_TYPE;

   private final Set<Property> recursiveProperty = new HashSet<>();

   @Override
   public Environment visitBase( final ModelElement base, final Context context ) {
      return context.getEnvironment();
   }

   @Override
   public Environment visitAspect( final Aspect aspect, Context context ) {
      if ( context == null ) {
         final Submodel submodel = new DefaultSubmodel.Builder().build();
         Environment environment = new DefaultEnvironment.Builder().submodels( Collections.singletonList( submodel ) ).build();
         context = new Context( environment, submodel );
         context.setEnvironment( environment );
      }

      final Submodel submodel = context.getSubmodel();
      submodel.setIdShort( aspect.getName() );
      submodel.setId( aspect.getAspectModelUrn().toString() + "/submodel" );
      submodel.setSemanticID( buildReferenceToConceptDescription( aspect ) );
      submodel.setDescription( mapText( aspect.getDescriptions() ) );
      submodel.setKind( ModellingKind.TEMPLATE );
      submodel.setAdministration( new DefaultAdministrativeInformation.Builder().build() );

      createConceptDescription( aspect, context );

      final AssetAdministrationShell administrationShell =
            new DefaultAssetAdministrationShell.Builder()
                  .idShort( ID_PREFIX + ADMIN_SHELL_NAME )
                  .description( createLangStringTextType( "en", ADMIN_SHELL_NAME ) )
                  .id( ADMIN_SHELL_NAME )
                  .administration( new DefaultAdministrativeInformation.Builder().build() )
                  .assetInformation( new DefaultAssetInformation.Builder().assetKind( AssetKind.INSTANCE ).build() )
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
         final List<org.eclipse.esmf.metamodel.Operation> elements, final Context context ) {
      return elements.stream().map( i -> mapText( i, context ) ).collect( Collectors.toList() );
   }

   private List<SubmodelElement> visitProperties(
         final List<Property> elements, final Context context ) {
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
                  "Having a recursive property: %s which is not optional is not valid. Check the model. Property will be excluded from AAS mapping.",
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
         return mapToAasProperty( property );
      }
   }

   private SubmodelElementCollection mapToAasSubModelElementCollection( final Entity entity, final Context context ) {
      final List<SubmodelElement> submodelElements =
            visitProperties( entity.getAllProperties(), context );
      return new DefaultSubmodelElementCollection.Builder()
            .idShort( ID_PREFIX + entity.getName() )
            .displayName( mapName( entity.getPreferredNames() ) )
            .description( mapText( entity.getDescriptions() ) )
            .value( submodelElements )
            .build();
   }

   private org.eclipse.digitaltwin.aas4j.v3.model.Property mapToAasProperty( final Property property ) {
      return new DefaultProperty.Builder()
            .idShort( ID_PREFIX + property.getName() )
            .valueType( mapAASXSDataType( property.getCharacteristic().flatMap( Characteristic::getDataType ).map( this::mapType ).orElse( UNKNOWN_TYPE ) ) )
            .displayName( mapName( property.getPreferredNames() ) )
            .value( property.getExampleValue().map( i -> i.getValue().toString() ).orElse( UNKNOWN_EXAMPLE ) )
            .description( mapText( property.getDescriptions() ) )
            .semanticID( buildReferenceToConceptDescription( property ) ) // link to the conceptDescription containing the details for the Characteristic
            .supplementalSemanticIds( buildReferencesForSeeElements( property.getSee() ) )
            .build();
   }

   private String extractIdentifier( final NamedElement element ) {
      return determineIdentifierFor( element );
   }

   private String mapType( final Type type ) {
      return type.getUrn();
   }

   private Operation mapText( final org.eclipse.esmf.metamodel.Operation operation, final Context context ) {
      return new DefaultOperation.Builder()
            .displayName( mapName( operation.getPreferredNames() ) )
            .description( mapText( operation.getDescriptions() ) )
            .idShort( ID_PREFIX + operation.getName() )
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
      return new DefaultOperationVariable.Builder().value( mapText( property, context ).orElseThrow() ).build();
   }

   private List<LangStringTextType> mapText( final Set<org.eclipse.esmf.metamodel.datatypes.LangString> localizedStrings ) {
      return localizedStrings.stream()
            .map( this::mapText )
            .collect( Collectors.toList() );
   }

   private List<LangStringNameType> mapName( final Set<org.eclipse.esmf.metamodel.datatypes.LangString> localizedStrings ) {
      return localizedStrings.stream()
            .map( this::mapName )
            .collect( Collectors.toList() );
   }

   private LangStringTextType mapText( final org.eclipse.esmf.metamodel.datatypes.LangString langString ) {
      return createLangStringTextType( langString.getLanguageTag().getLanguage(), langString.getValue() );
   }

   private LangStringNameType mapName( final org.eclipse.esmf.metamodel.datatypes.LangString langString ) {
      return createLangStringNameType( langString.getLanguageTag().getLanguage(), langString.getValue() );
   }

   private LangStringDefinitionTypeIec61360 mapDefinitionIec61360( final org.eclipse.esmf.metamodel.datatypes.LangString langString ) {
      return createLangStringDefinitionTypeIec61360( langString.getLanguageTag().getLanguage(), langString.getValue() );
   }

   private LangStringPreferredNameTypeIec61360 mapLangStringPreferredNameTypeIec61360( final org.eclipse.esmf.metamodel.datatypes.LangString langString ) {
      return createLangStringPreferredNameTypeIec61360( langString.getLanguageTag().getLanguage(), langString.getValue() );
   }

   private LangStringTextType createLangStringTextType( String locale, String text ) {
      return new DefaultLangStringTextType.Builder().language( locale ).text( text ).build();
   }

   private LangStringNameType createLangStringNameType( String locale, String text ) {
      return new DefaultLangStringNameType.Builder().language( locale ).text( text ).build();
   }

   private LangStringShortNameTypeIec61360 createLangStringShortNameTypeIec61360( String locale, String text ) {
      return new DefaultLangStringShortNameTypeIec61360.Builder().language( locale ).text( text ).build();
   }

   private LangStringDefinitionTypeIec61360 createLangStringDefinitionTypeIec61360( String locale, String text ) {
      return new DefaultLangStringDefinitionTypeIec61360.Builder().language( locale ).text( text ).build();
   }

   private LangStringPreferredNameTypeIec61360 createLangStringPreferredNameTypeIec61360( String locale, String text ) {
      return new DefaultLangStringPreferredNameTypeIec61360.Builder().language( locale ).text( text ).build();
   }

   private Reference buildReferenceToEnumValue( final Enumeration enumeration, final Object value ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.DATA_ELEMENT )
                  .value( determineIdentifierFor( enumeration ) + ":" + value.toString() )
                  .build();
      return new DefaultReference.Builder().type( ReferenceTypes.MODEL_REFERENCE ).keys( key ).build();
   }

   private Reference buildReferenceToConceptDescription( final Aspect aspect ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.CONCEPT_DESCRIPTION )
                  .value( extractIdentifier( aspect ) )
                  .build();
      return new DefaultReference.Builder().keys( key ).type( ReferenceTypes.MODEL_REFERENCE ).build();
   }

   private Reference buildReferenceToConceptDescription( final Property property ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.CONCEPT_DESCRIPTION )
                  .value( extractIdentifier( property ) )
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
            .type( ReferenceTypes.EXTERNAL_REFERENCE )
            .keys( key )
            .build();
   }

   private List<Reference> buildReferencesForSeeElements( final List<String> seeReferences ) {
      return seeReferences.stream().map( this::buildReferenceForSeeElement ).collect( Collectors.toList() );
   }

   private String determineIdentifierFor( final NamedElement isDescribed ) {
      if ( isDescribed.getAspectModelUrn().isPresent() ) {
         return isDescribed.getAspectModelUrn().get().toString();
      } else {
         return isDescribed.getName();
      }
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
                     .idShort( ID_PREFIX + characteristic.getName() )
                     .displayName( mapName( characteristic.getPreferredNames() ) )
                     .embeddedDataSpecifications( extractEmbeddedDataSpecification( property ) )
                     .id( extractIdentifier( property ) )
                     .build();
         context.getEnvironment().getConceptDescriptions().add( conceptDescription );
      }
   }

   private void createConceptDescription( final Aspect aspect, final Context context ) {
      // check if the concept description is already created. If not create a new one.
      if ( !context.hasEnvironmentConceptDescription( aspect.getAspectModelUrn().toString() ) ) {
         final ConceptDescription conceptDescription =
               new DefaultConceptDescription.Builder()
                     .idShort( ID_PREFIX + aspect.getName() )
                     .displayName( mapName( aspect.getPreferredNames() ) )
                     .embeddedDataSpecifications( extractEmbeddedDataSpecification( aspect ) )
                     .id( extractIdentifier( aspect ) )
                     .description( mapText( aspect.getDescriptions() ) )
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

   private DataSpecificationIec61360 extractDataSpecificationContent( final Property property ) {
      final List<LangStringDefinitionTypeIec61360> definitions = property.getCharacteristic().stream().flatMap( characteristic ->
                  characteristic.getDescriptions().stream() )
            .map( this::mapDefinitionIec61360 )
            .collect( Collectors.toList() );

      final List<LangStringPreferredNameTypeIec61360> preferredNames = property.getPreferredNames().size() > 0 ?
            property.getPreferredNames().stream().map( this::mapLangStringPreferredNameTypeIec61360 ).collect( Collectors.toList() ) :
            Collections.singletonList( createLangStringPreferredNameTypeIec61360( DEFAULT_LOCALE, property.getName() ) );

      return new DefaultDataSpecificationIec61360.Builder()
            .definition( definitions )
            .preferredName( preferredNames )
            .shortName( createLangStringShortNameTypeIec61360( DEFAULT_LOCALE, property.getName() ) )
            .dataType( mapIEC61360DataType( property.getCharacteristic() ) )
            .build();
   }

   private DataSpecificationIec61360 extractDataSpecificationContent( final Aspect aspect ) {
      List<LangStringPreferredNameTypeIec61360> preferredNames = aspect.getPreferredNames().size() > 0 ?
            aspect.getPreferredNames().stream().map( this::mapLangStringPreferredNameTypeIec61360 ).collect( Collectors.toList() ) :
            Collections.singletonList( createLangStringPreferredNameTypeIec61360( DEFAULT_LOCALE, aspect.getName() ) );

      return new DefaultDataSpecificationIec61360.Builder()
            .definition( aspect.getDescriptions().stream().map( this::mapDefinitionIec61360 ).collect( Collectors.toList() ) )
            .preferredName( preferredNames )
            .build();

   }

   private DataTypeIec61360 mapIEC61360DataType( final Optional<Characteristic> characteristic ) {
      return mapIEC61360DataType( characteristic.flatMap( Characteristic::getDataType ).map( Type::getUrn ).orElse( RDF.langString.getURI() ) );
   }

   private DataTypeIec61360 mapIEC61360DataType( final Characteristic characteristic ) {
      return mapIEC61360DataType( Optional.of( characteristic ) );
   }

   private DataTypeIec61360 mapIEC61360DataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return TYPE_MAP.getOrDefault( resource, DataTypeIec61360.STRING );
   }

   private DataTypeDefXSD mapAASXSDataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return AAS_XSD_TYPE_MAP.getOrDefault( resource, DataTypeDefXSD.STRING );
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
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementCollection.Builder()
                        .idShort( ID_PREFIX + property.getName() )
                        .displayName( mapName( property.getPreferredNames() ) )
                        .description( mapText( property.getDescriptions() ) )
                        .value( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .build();

      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   @Override
   public Environment visitList(
         final org.eclipse.esmf.characteristic.List list, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementList.Builder()
                        .idShort( ID_PREFIX + property.getName() )
                        .typeValueListElement( AASSubmodelElements.DATA_ELEMENT )
                        .displayName( mapName( property.getPreferredNames() ) )
                        .description( mapText( property.getDescriptions() ) )
                        .value( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .build();
      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   @Override
   public Environment visitSet(
         final org.eclipse.esmf.characteristic.Set set, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementCollection.Builder() //TODO according to the standard document this should be SubmodelEleementStruct. However,
                        // this type is not available in AAS4J
                        .idShort( ID_PREFIX + property.getName() )
                        .displayName( mapName( property.getPreferredNames() ) )
                        .description( mapText( property.getDescriptions() ) )
                        .value( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .build();
      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   @Override
   public Environment visitSortedSet(
         final SortedSet sortedSet, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementList.Builder()
                        .idShort( ID_PREFIX + property.getName() )
                        .typeValueListElement( AASSubmodelElements.DATA_ELEMENT ) // TODO check if more specific type info is reuired
                        .displayName( mapName( property.getPreferredNames() ) )
                        .description( mapText( property.getDescriptions() ) )
                        .value( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .build();
      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   // Either will be mapped by adding both the left and the right side to the SubmodelTemplate.
   // No possibilities to mark the SubmodelElements as optional. So both are either options are
   // in the result and have to be manually selected.
   @Override
   public Environment visitEither(
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
      final SubmodelElementList aasSubModelElementCollection =
            new DefaultSubmodelElementList.Builder()
                  .idShort( ID_PREFIX + either.getName() )
                  .typeValueListElement( AASSubmodelElements.DATA_ELEMENT ) // TODO check if more specific type info is rqujried
                  .displayName( mapName( either.getPreferredNames() ) )
                  .description( mapText( either.getDescriptions() ) )
                  .value( submodelElements )
                  .build();
      context.setPropertyResult( aasSubModelElementCollection );
      return context.environment;
   }

   @Override
   public Environment visitQuantifiable(
         final Quantifiable quantifiable, final Context context ) {
      createSubmodelElement( ( property ) -> decideOnMapping( property, context ), context );

      if ( quantifiable.getUnit().isPresent() ) {
         final ConceptDescription conceptDescription =
               context.getConceptDescription( determineIdentifierFor( context.getProperty() ) );
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
            context.getConceptDescription( determineIdentifierFor( context.getProperty() ) );
      final List<EmbeddedDataSpecification> embeddedDataSpecification =
            conceptDescription.getEmbeddedDataSpecifications();
      if ( embeddedDataSpecification.stream().findFirst().isPresent() ) {
         final DataSpecificationIec61360 dataSpecificationContent =
               (DataSpecificationIec61360) embeddedDataSpecification.stream().findFirst().get().getDataSpecificationContent();
         dataSpecificationContent.setDataType( mapIEC61360DataType( enumeration ) );
         final List<ValueReferencePair> valueReferencePairs =
               enumeration.getValues().stream()
                     .map(
                           x ->
                                 new DefaultValueReferencePair.Builder()
                                       .value( x.toString() )
                                       .valueID( buildReferenceToEnumValue( enumeration, x ) )
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
      // ignored and have to be deduced by resolving a SAMM model referenced by its semanticID
      return visitCharacteristic( trait.getBaseCharacteristic(), context );
   }
}
