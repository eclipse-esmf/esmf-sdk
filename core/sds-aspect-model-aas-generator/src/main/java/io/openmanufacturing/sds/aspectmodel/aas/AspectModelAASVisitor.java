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
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.DataSpecificationIEC61360;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
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
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.ValueList;
import org.eclipse.digitaltwin.aas4j.v3.model.ValueReferencePair;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultDataSpecificationIEC61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;
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

import com.google.common.collect.ImmutableMap;

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
import io.openmanufacturing.sds.metamodel.NamedElement;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Type;
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

   /**
    * Maps Aspect types to DataTypeDefXsd Schema types, with no explicit mapping defaulting to
    * string
    */
   private static final Map<Resource, DataTypeDefXsd> AAS_XSD_TYPE_MAP =
         ImmutableMap.<Resource, DataTypeDefXsd> builder()
               .put( XSD.anyURI, DataTypeDefXsd.ANY_URI )
               .put( XSD.yearMonthDuration, DataTypeDefXsd.YEAR_MONTH_DURATION )
               .put( XSD.xboolean, DataTypeDefXsd.BOOLEAN )
               .put( XSD.xbyte, DataTypeDefXsd.BYTE )
               .put( XSD.date, DataTypeDefXsd.DATE )
               .put( XSD.dateTime, DataTypeDefXsd.DATE_TIME )
               .put( XSD.dateTimeStamp, DataTypeDefXsd.DATE_TIME_STAMP )
               .put( XSD.dayTimeDuration, DataTypeDefXsd.DAY_TIME_DURATION )
               .put( XSD.decimal, DataTypeDefXsd.DECIMAL )
               .put( XSD.xdouble, DataTypeDefXsd.DOUBLE )
               .put( XSD.duration, DataTypeDefXsd.DURATION )
               .put( XSD.xfloat, DataTypeDefXsd.FLOAT )
               .put( XSD.gMonth, DataTypeDefXsd.GMONTH )
               .put( XSD.gMonthDay, DataTypeDefXsd.GMONTH_DAY )
               .put( XSD.gYear, DataTypeDefXsd.GYEAR )
               .put( XSD.gYearMonth, DataTypeDefXsd.GYEAR_MONTH )
               .put( XSD.hexBinary, DataTypeDefXsd.HEX_BINARY )
               .put( XSD.xint, DataTypeDefXsd.INT )
               .put( XSD.integer, DataTypeDefXsd.INTEGER )
               .put( XSD.xlong, DataTypeDefXsd.LONG )
               .put( XSD.negativeInteger, DataTypeDefXsd.NEGATIVE_INTEGER)
               .put( XSD.nonNegativeInteger, DataTypeDefXsd.NON_NEGATIVE_INTEGER )
               .put( XSD.positiveInteger, DataTypeDefXsd.POSITIVE_INTEGER )
               .put( XSD.xshort, DataTypeDefXsd.SHORT )
               .put( XSD.normalizedString, DataTypeDefXsd.STRING )
               .put( XSD.time, DataTypeDefXsd.TIME )
               .put( XSD.unsignedByte, DataTypeDefXsd.UNSIGNED_BYTE )
               .put( XSD.unsignedInt, DataTypeDefXsd.UNSIGNED_INT )
               .put( XSD.unsignedLong, DataTypeDefXsd.UNSIGNED_LONG )
               .put( XSD.unsignedShort, DataTypeDefXsd.UNSIGNED_SHORT )
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

         context = new Context(environment, submodel );
         context.setEnvironment(environment);
      }

      final Submodel submodel = context.getSubmodel();

      submodel.setIdShort( aspect.getName() );
      submodel.setSemanticId( buildReferenceToConceptDescription( aspect ) );
      submodel.setDescription( map( aspect.getDescriptions() ) );
      submodel.setKind( ModelingKind.TEMPLATE );

      createConceptDescription( aspect, context );

      final AssetAdministrationShell administrationShell =
            new DefaultAssetAdministrationShell.Builder().idShort( ADMIN_SHELL_NAME ).build();
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
      return elements.stream().map( i -> map( i, context ) ).collect( Collectors.toList() );
   }

   private SubmodelElement map( final Property property, final Context context ) {
      final Supplier<SubmodelElement> defaultResultForProperty = () -> context.getSubmodel().getSubmodelElements().stream()
            .filter( i -> i.getIdShort().equals( property.getName() ) )
            .findFirst()
            .orElse( new DefaultProperty.Builder().build() );
      if ( recursiveProperty.contains( property ) ) {
         // The guard checks for recursion in properties. If a recursion happens, the respective
         // property will be excluded from generation.
         recursiveProperty.remove( property );
         if ( property.isOptional() ) {
            LOG.warn( String.format( "Having a recursive Property %s which is optional. Will be excluded from AAS mapping.", property ) );
         } else {
            LOG.error( String.format( "Having a recursive Property: %s which is not optional is not valid. Check the model. Property will be excluded from AAS mapping.", property ) );
         }
         return defaultResultForProperty.get();
      }
      recursiveProperty.add( property );

      if ( property.getCharacteristic().isEmpty() ) {
         LOG.warn( String.format( "Having an Abstract Property. Will be excluded from AAS mapping." ) );
         return defaultResultForProperty.get();
      }

      // Characteristic defines how the property is mapped to SubmodelElement
      final Characteristic characteristic = property.getCharacteristic().get();

      context.setProperty( property );
      characteristic.accept( this, context );
      final SubmodelElement element = context.getPropertyResult();

      recursiveProperty.remove( property );
      return element;
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
         return mapToAasProperty( property );
      }
   }

   private SubmodelElementCollection mapToAasSubModelElementCollection(
         final Entity entity, final Context context ) {
      final List<SubmodelElement> submodelElements =
            visitProperties( entity.getAllProperties(), context );
      return new DefaultSubmodelElementCollection.Builder()
            .idShort( entity.getName() )
            .displayName( map( entity.getPreferredNames() ) )
            .description( map( entity.getDescriptions() ) )
            .value( submodelElements )
            .build();
   }

   private org.eclipse.digitaltwin.aas4j.v3.model.Property mapToAasProperty( final Property property ) {
      return new DefaultProperty.Builder()
            .idShort( property.getName() )
            .kind( ModelingKind.TEMPLATE )
            .valueType( mapAASXSDataType( property.getCharacteristic().flatMap( Characteristic::getDataType ).map( this::mapType ).orElse( UNKNOWN_TYPE ) )  ) // TODO this might not work and a proper mapping implementation is required
            .displayName( map( property.getPreferredNames() ) )
            .value( property.getExampleValue().map( Object::toString ).orElse( UNKNOWN_EXAMPLE ) )
            .description( map( property.getDescriptions() ) )
            .semanticId( buildReferenceToConceptDescription( property ) ) // this is the link to the conceptDescription containing the details for
            // the Characteristic
            .build();
   }

   private String extractIdentifier( final NamedElement element ) {
      return  determineIdentifierFor( element );
   }

   private String mapType( final Type type ) {
      return type.getUrn();
   }

   private Operation map(
         final io.openmanufacturing.sds.metamodel.Operation operation, final Context context ) {
      return new DefaultOperation.Builder()
            .displayName( map( operation.getPreferredNames() ) )
            .description( map( operation.getDescriptions() ) )
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

   private List<LangString> map( final Set<io.openmanufacturing.sds.metamodel.datatypes.LangString> localizedStrings ) {
      return localizedStrings.stream()
            .map( ( entry ) -> map( entry.getLanguageTag(), entry.getValue() ) )
            .collect( Collectors.toList() );
   }

   private LangString map( final io.openmanufacturing.sds.metamodel.datatypes.LangString langString ) {
      return map( langString.getLanguageTag(), langString.getValue() );
   }

   private LangString map( final Locale locale, final String value ) {
      return createLangString( value, locale.getLanguage() );
   }

   private Reference buildReferenceToEnumValue( final Enumeration enumeration, final Object value ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.DATA_ELEMENT )
                  .value( determineIdentifierFor( enumeration ) + ":" + value.toString() )
                  .build();
      return new DefaultReference.Builder().keys( key ).build();
   }

   private Reference buildReferenceToConceptDescription( final Aspect aspect ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.CONCEPT_DESCRIPTION )
                  .value( extractIdentifier( aspect ) )
                  .build();
      return new DefaultReference.Builder().keys( key ).build();
   }

   private Reference buildReferenceToConceptDescription( final Property property ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.CONCEPT_DESCRIPTION )
                  .value( extractIdentifier( property ) )
                  .build();
      return new DefaultReference.Builder().keys( key ).build();
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
                     .idShort( characteristic.getName() )
                     .displayName( map( characteristic.getPreferredNames() ) )
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
                     .idShort( aspect.getName() )
                     .displayName( map( aspect.getPreferredNames() ) )
                     .embeddedDataSpecifications( extractEmbeddedDataSpecification( aspect ) )
                     .id( extractIdentifier( aspect ) )
                     .description( map( aspect.getDescriptions() ) )
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

   private DataSpecificationIEC61360 extractDataSpecificationContent( final Property property ) {
      final List<LangString> definitions = property.getCharacteristic().stream().flatMap( characteristic ->
                  characteristic.getDescriptions().stream() )
            .map( this::map )
            .collect( Collectors.toList() );

      return new DefaultDataSpecificationIEC61360.Builder()
            .definition( definitions )
            .preferredName( map( property.getPreferredNames() ) )
            .shortName( createLangString( property.getName(), DEFAULT_LOCALE ) )
            .dataType( mapIEC61360DataType( property.getCharacteristic() ) )
            .build();
   }

   private LangString createLangString(String text, String locale){
      return new DefaultLangString.Builder().language( locale ).text( text ).build();
   }

   private DataSpecificationIEC61360 extractDataSpecificationContent( final Aspect aspect ) {
      final List<LangString> definitions = map( aspect.getDescriptions() );

      return new DefaultDataSpecificationIEC61360.Builder()
            .definition( definitions )
            .preferredName( map( aspect.getPreferredNames() ) )
            .shortName( createLangString( aspect.getName(), DEFAULT_LOCALE ) )
            .build();
   }

   private void createSubmodelElement( final SubmodelElementBuilder op, final Context context ) {
      final Property property = context.getProperty();
      final SubmodelElement submodelElement = op.build( property );
      context.setPropertyResult( submodelElement );
      createConceptDescription( property, context );
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

   private DataTypeDefXsd mapAASXSDataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return AAS_XSD_TYPE_MAP.getOrDefault( resource, DataTypeDefXsd.STRING );
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
                        .idShort( property.getName() )
                        .displayName( map( property.getPreferredNames() ) )
                        .description( map( property.getDescriptions() ) )
                        .value( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .build();

      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   @Override
   public Environment visitList(
         final io.openmanufacturing.sds.characteristic.List list, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementList.Builder()
                        .idShort( property.getName() )
                        .displayName( map( property.getPreferredNames() ) )
                        .description( map( property.getDescriptions() ) )
                        .value( Collections.singletonList( decideOnMapping( property, context ) ) )
                        .build();
      createSubmodelElement( builder, context );
      return context.getEnvironment();
   }

   @Override
   public Environment visitSet(
         final io.openmanufacturing.sds.characteristic.Set set, final Context context ) {
      final SubmodelElementBuilder builder =
            ( property ) ->
                  new DefaultSubmodelElementCollection.Builder() //TODO according to the standard document this should be SubmodelEleementStruct. However, this type is not available in AAS4J
                        .idShort( property.getName() )
                        .displayName( map( property.getPreferredNames() ) )
                        .description( map( property.getDescriptions() ) )
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
                        .idShort( property.getName() )
                        .displayName( map( property.getPreferredNames() ) )
                        .description( map( property.getDescriptions() ) )
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
                  .idShort( either.getName() )
                  .displayName( map( either.getPreferredNames() ) )
                  .description( map( either.getDescriptions() ) )
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
            context.getConceptDescription( determineIdentifierFor( context.getProperty() ) );
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
