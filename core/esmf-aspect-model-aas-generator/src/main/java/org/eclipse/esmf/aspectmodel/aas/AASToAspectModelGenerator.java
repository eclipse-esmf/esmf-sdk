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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMMC;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.impl.DefaultList;
import org.eclipse.esmf.characteristic.impl.DefaultSingleEntity;
import org.eclipse.esmf.characteristic.impl.DefaultTrait;
import org.eclipse.esmf.constraint.RangeConstraint;
import org.eclipse.esmf.constraint.impl.DefaultRangeConstraint;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.impl.BoundDefinition;
import org.eclipse.esmf.metamodel.impl.DefaultAspect;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.impl.DefaultEvent;
import org.eclipse.esmf.metamodel.impl.DefaultOperation;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ValueInstantiator;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.base.CaseFormat;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AasSubmodelElements;
import org.eclipse.digitaltwin.aas4j.v3.model.AbstractLangString;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.Capability;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.EventElement;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.HasSemantics;
import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AASToAspectModelGenerator {
   private static final Logger LOG = LoggerFactory.getLogger( AASToAspectModelGenerator.class );
   private final Environment aasEnvironment;
   private final Map<org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement, Property> properties = new HashMap<>();
   private final SAMMC SAMMC = new SAMMC( KnownVersion.getLatest() );
   private final ValueInstantiator valueInstantiator = new ValueInstantiator( KnownVersion.getLatest() );
   private AspectModelUrn aspectUrn;

   private record ElementName( String name, boolean isSynthetic ) {}

   private AASToAspectModelGenerator( final Environment aasEnvironment ) {
      this.aasEnvironment = aasEnvironment;
   }

   public static AASToAspectModelGenerator fromAasXml( final InputStream inputStream ) {
      try {
         return from( new XmlDeserializer().read( inputStream ) );
      } catch ( final DeserializationException exception ) {
         throw new AspectModelGenerationException( exception );
      }
   }

   public static AASToAspectModelGenerator fromAasx( final InputStream inputStream ) {
      try {
         final AASXDeserializer deserializer = new AASXDeserializer( inputStream );
         return from( new XmlDeserializer().read( deserializer.getXMLResourceString() ) );
      } catch ( final InvalidFormatException | IOException | DeserializationException exception ) {
         throw new AspectModelGenerationException( exception );
      }
   }

   public static AASToAspectModelGenerator from( final Environment environment ) {
      return new AASToAspectModelGenerator( environment );
   }

   public List<Aspect> generateAspects() {
      return aasEnvironment.getSubmodels()
            .stream()
            .filter( submodel -> submodel.getKind().equals( ModellingKind.TEMPLATE ) )
            .map( this::submodelToAspect )
            .toList();
   }

   protected String escapeUrnNamespacePart( final String namespacePart ) {
      if ( namespacePart.matches( AspectModelUrn.NAMESPACE_REGEX ) ) {
         return namespacePart;
      }
      throw new AspectModelGenerationException( "Encountered URI with invalid namespace part: " + namespacePart );
   }

   private <T> Collector<T, ArrayDeque<T>, ArrayDeque<T>> reverseOrder() {
      return Collector.of( ArrayDeque::new, ArrayDeque::addFirst, ( deque1, deque2 ) -> {
         deque2.addAll( deque1 );
         return deque2;
      } );
   }

   private String iriToReversedHostNameNotation( final IRI iri ) {
      final URI uri = URI.create( iri.toString().contains( "://" ) ? iri.toString() : "https://" + iri );
      return Stream.concat(
                  Arrays.stream( uri.getHost().split( "\\." ) ).collect( reverseOrder() ).stream(),
                  Arrays.stream( uri.getPath().split( "/" ) ) )
            .filter( StringUtils::isNotBlank )
            .map( this::escapeUrnNamespacePart )
            .collect( Collectors.joining( "." ) );
   }

   private Optional<IRI> iri( final String lexicalRepresentation ) {
      final IRI iri = IRIFactory.iriImplementation().create( lexicalRepresentation );
      return iri.hasViolation( true ) ? Optional.empty() : Optional.of( iri );
   }

   private VersionNumber determineAspectModelUrnVersion( final Submodel submodel ) {
      return Optional.ofNullable( submodel.getAdministration() )
            .flatMap( administration -> Optional.ofNullable( administration.getVersion() ) )
            .flatMap( versionString -> {
               final Optional<VersionNumber> parsedVersion = VersionNumber.tryParse( versionString ).toJavaOptional();
               if ( parsedVersion.isEmpty() ) {
                  LOG.warn( "Found version number {} for {}, but it's no semantic version (X.Y.Z). Defaulting to 1.0.0 instead.",
                        versionString, submodel.getId() );
               }
               return parsedVersion;
            } )
            .orElseGet( () -> VersionNumber.parse( "1.0.0" ) );
   }

   private String determineAspectModelUrnNamespace( final Submodel element ) {
      return iri( element.getId() )
            .map( this::iriToReversedHostNameNotation )
            .or( () -> Optional.ofNullable( element.getSemanticId() ).flatMap( semanticId -> semanticId.getKeys().stream()
                  .filter( key -> key.getType() == KeyTypes.CONCEPT_DESCRIPTION || key.getType() == KeyTypes.GLOBAL_REFERENCE )
                  .map( Key::getValue )
                  .flatMap( value -> iri( value ).stream() )
                  .map( this::iriToReversedHostNameNotation )
                  .findFirst() ) )
            .orElseGet( () -> {
               LOG.warn( "Did not find any id, ConceptDescription or GlobalReference with a valid IRI, defaulting to com.example" );
               return "com.example";
            } );
   }

   private String randomElementName( final Object object ) {
      return DigestUtils.sha1Hex( object.toString() ).substring( 0, 10 );
   }

   private ElementName determineSubmodelName( final Submodel submodel ) {
      // idShort present? Great.
      if ( submodel.getIdShort() != null ) {
         return new ElementName( sanitizeAspectModelElementName( submodel.getIdShort() ), false );
      }
      // We only have an ID. Is it an IRDI? If it is, use its printable parts as element name, with a hash
      // suffix to prevent name clashes.
      final String id = submodel.getId();
      LOG.warn( "Submodel with id {} has no idShort", id );
      final Optional<String> nameFromIrdi = IRDI.from( id ).map( irdi ->
            sanitizeAspectModelElementName( irdi.toString() ) + DigestUtils.sha1Hex( id ).substring( 0, 8 ) );
      // Fallback: Hash the id
      return new ElementName( nameFromIrdi.orElseGet( () -> randomElementName( id ) ), true );
   }

   private ElementName determineSubmodelElementName( final SubmodelElement submodelElement, final String namePrefix ) {
      if ( submodelElement.getIdShort() == null ) {
         return new ElementName( namePrefix + StringUtils.capitalize( randomElementName( submodelElement ) ), true );
      }
      final String idPart = namePrefix.isEmpty() ? submodelElement.getIdShort() : StringUtils.capitalize( submodelElement.getIdShort() );
      return new ElementName( sanitizeAspectModelElementName( namePrefix + idPart ), false );
   }

   private String sanitizeAspectModelElementName( final String potentialIdentifier ) {
      return potentialIdentifier.chars()
            .dropWhile( character -> !Character.isJavaIdentifierStart( character ) )
            .filter( Character::isJavaIdentifierPart )
            .mapToObj( character -> String.valueOf( (char) character ) )
            .collect( Collectors.joining() );
   }

   private Optional<AspectModelUrn> aspectModelUrnFromId( final Identifiable element ) {
      return AspectModelUrn.from( element.getId() ).toJavaOptional();
   }

   private Optional<AspectModelUrn> aspectModelUrnFromSemanticId( final HasSemantics element ) {
      return Optional.ofNullable( element.getSemanticId() ).flatMap( semanticId ->
            semanticId.getKeys().stream()
                  .filter( key -> key.getType() == KeyTypes.CONCEPT_DESCRIPTION || key.getType() == KeyTypes.GLOBAL_REFERENCE )
                  .flatMap( key -> AspectModelUrn.from( key.getValue() ).toJavaOptional().stream() )
                  .findFirst() );
   }

   private boolean impliesCollectionAspect( final List<Property> properties ) {
      if ( properties.size() != 1 ) {
         return false;
      }
      final Property property = properties.get( 0 );
      return property.getName().equals( "items" ) && property.getCharacteristic().map( characteristic ->
            characteristic.is( Collection.class ) ).orElse( false );
   }

   private Aspect submodelToAspect( final Submodel submodel ) {
      final ElementName aspectName = determineSubmodelName( submodel );
      aspectUrn = aspectModelUrnFromId( submodel )
            .or( () -> aspectModelUrnFromSemanticId( submodel ) )
            .orElseGet( () -> AspectModelUrn.fromUrn( String.format( "%s:%s:%s:%s#%s",
                  AspectModelUrn.VALID_PROTOCOL, AspectModelUrn.VALID_NAMESPACE_IDENTIFIER,
                  determineAspectModelUrnNamespace( submodel ),
                  determineAspectModelUrnVersion( submodel ),
                  aspectName.name() ) ) );

      final MetaModelBaseAttributes aspectMetaModelBaseAttributes = new MetaModelBaseAttributes(
            KnownVersion.getLatest(), aspectUrn, aspectUrn.getName(),
            langStringSet( submodel.getDisplayName() ),
            langStringSet( submodel.getDescription() ),
            seeReferences( submodel ),
            aspectName.isSynthetic() );

      final List<Property> properties = createProperties( submodel );
      return new DefaultAspect( aspectMetaModelBaseAttributes,
            properties, createOperations( submodel ), createEvents( submodel ), impliesCollectionAspect( properties ) );
   }

   private List<Property> createProperties( final Submodel submodel ) {
      return submodel.getSubmodelElements().stream()
            .filter( submodelElement ->
                  !submodelElement.getClass().isAssignableFrom( org.eclipse.digitaltwin.aas4j.v3.model.Operation.class )
                        && !submodelElement.getClass().isAssignableFrom( EventElement.class ) )
            .map( this::createProperty ).toList();
   }

   private List<Operation> createOperations( final Submodel submodel ) {
      return submodel.getSubmodelElements().stream()
            .filter( org.eclipse.digitaltwin.aas4j.v3.model.Operation.class::isInstance )
            .map( org.eclipse.digitaltwin.aas4j.v3.model.Operation.class::cast )
            .map( this::createOperation )
            .toList();
   }

   private List<Event> createEvents( final Submodel submodel ) {
      return submodel.getSubmodelElements().stream()
            .filter( org.eclipse.digitaltwin.aas4j.v3.model.EventElement.class::isInstance )
            .map( org.eclipse.digitaltwin.aas4j.v3.model.EventElement.class::cast )
            .map( this::createEvent )
            .toList();
   }

   private Optional<ScalarValue> exampleValueForProperty( final String lexicalRepresentation, final Optional<Type> targetType ) {
      return targetType
            .flatMap( type -> {
               if ( type instanceof final Scalar scalarType ) {
                  // We can set languageTag to null, because AAS does not use rdf:langString, property values
                  // can never provide a language tag, therefore we'll never create a ScalarValue with rdf:langString type.
                  // https://aas-core-works.github.io/aas-core-meta/v3/DataTypeDefXsd.html
                  final Optional<ScalarValue> exampleValue =
                        valueInstantiator.buildScalarValue( lexicalRepresentation, null, scalarType.getUrn() );
                  if ( exampleValue.isEmpty() ) {
                     LOG.warn( "Example value {} can not be parsed as {}", lexicalRepresentation, type );
                  }
                  return exampleValue;
               } else {
                  LOG.warn( "Can not use example value {} of non-scalar type {}", lexicalRepresentation, type );
                  return Optional.empty();
               }
            } );
   }

   private List<String> seeReferences( final SubmodelElement element ) {
      return Optional.ofNullable( element.getSemanticId() ).stream()
            .flatMap( semanticId -> semanticId.getKeys().stream() )
            .filter( key -> key.getType() == KeyTypes.CONCEPT_DESCRIPTION || key.getType() == KeyTypes.GLOBAL_REFERENCE )
            .map( Key::getValue )
            .flatMap( value -> validIrdiOrUri( value ).stream() )
            .toList();
   }

   private List<String> seeReferences( final Submodel submodel ) {
      return validIrdiOrUri( submodel.getId() ).stream().toList();
   }

   private sealed interface ElementNamingStrategy permits DetermineAutomatically, UseGivenUrn {}

   private record DetermineAutomatically( String namePrefix ) implements ElementNamingStrategy {
      private DetermineAutomatically() {
         this( "" );
      }
   }

   private record UseGivenUrn( AspectModelUrn aspectModelUrn ) implements ElementNamingStrategy {}

   private MetaModelBaseAttributes baseAttributes( final SubmodelElement element, final ElementNamingStrategy elementNamingStrategy ) {
      final ElementName elementName;
      final AspectModelUrn urn;
      if ( elementNamingStrategy instanceof final DetermineAutomatically automatically ) {
         elementName = determineSubmodelElementName( element, automatically.namePrefix() );
         urn = aspectModelUrnFromSemanticId( element )
               .orElseGet( () -> aspectUrn.withName( elementName.name() ) );
      } else if ( elementNamingStrategy instanceof final UseGivenUrn givenUrn ) {
         elementName = determineSubmodelElementName( element, "" );
         urn = givenUrn.aspectModelUrn();
      } else {
         throw new AspectModelGenerationException( "Unknown ElementNamingStrategy" );
      }

      return new MetaModelBaseAttributes( KnownVersion.getLatest(),
            urn, elementName.name(),
            langStringSet( element.getDisplayName() ),
            langStringSet( element.getDescription() ),
            seeReferences( element ),
            elementName.isSynthetic() );
   }

   private Property createProperty( final org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement submodelElement ) {
      final Property existingProperty = properties.get( submodelElement );
      if ( existingProperty != null ) {
         return existingProperty;
      }

      final MetaModelBaseAttributes metaModelBaseAttributes = baseAttributes( submodelElement, new DetermineAutomatically() );
      final Characteristic characteristic = createCharacteristic( submodelElement, metaModelBaseAttributes.getUrn()
            .orElseThrow( () -> new AspectModelGenerationException( "Encountered property without URN" ) ) );
      final Optional<ScalarValue> exampleValue =
            submodelElement instanceof final org.eclipse.digitaltwin.aas4j.v3.model.Property property
                  ? Optional.ofNullable( property.getValue() )
                  .flatMap( lexicalRepresentation ->
                        exampleValueForProperty( lexicalRepresentation, characteristic.getDataType() ) )
                  : Optional.empty();
      final Property result = new DefaultProperty(
            metaModelBaseAttributes,
            Optional.of( characteristic ),
            exampleValue,
            false,
            false,
            Optional.empty(),
            false,
            Optional.empty() );
      properties.put( submodelElement, result );
      return result;
   }

   private Operation createOperation( final org.eclipse.digitaltwin.aas4j.v3.model.Operation operation ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = baseAttributes( operation, new DetermineAutomatically() );
      final List<OperationVariable> potentialOutputs = Stream.concat( operation.getOutputVariables().stream(),
            operation.getInoutputVariables().stream() ).toList();
      if ( potentialOutputs.size() > 1 ) {
         LOG.warn( "Operation {} has more than one output, will only use one", operation.getIdShort() );
      }

      return new DefaultOperation( metaModelBaseAttributes,
            Stream.concat( operation.getInputVariables().stream(), operation.getInoutputVariables().stream() )
                  .flatMap( operationVariable -> determineOperationProperty( operationVariable ).stream() )
                  .toList(),
            potentialOutputs.stream().findFirst().flatMap( this::determineOperationProperty )
      );
   }

   private Optional<Property> determineOperationProperty( final OperationVariable variable ) {
      if ( variable.getValue() instanceof final org.eclipse.digitaltwin.aas4j.v3.model.Property property ) {
         final Property existingProperty = properties.get( property );
         if ( existingProperty != null ) {
            return Optional.of( existingProperty );
         }
         final Property result = createProperty( property );
         properties.put( property, result );
         return Optional.of( result );
      }
      LOG.warn( "OperationVariable {} refers to SubmodelElement {} which is no property, ignoring", variable, variable.getValue() );
      return Optional.empty();
   }

   private Event createEvent( final EventElement event ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = baseAttributes( event, new DetermineAutomatically() );
      // Since an AAS EventElement/BasicEvent does not have Properties but only info about the broker, we can't create anything
      // meaningful here
      LOG.warn( "Creating event {} with empty list of properties", metaModelBaseAttributes.getName() );
      return new DefaultEvent( metaModelBaseAttributes, List.of() );
   }

   private static Set<LangString> langStringSet( final List<? extends AbstractLangString> stringList ) {
      return stringList.stream()
            .map( abstractLangString -> new LangString( abstractLangString.getText(),
                  Locale.forLanguageTag( abstractLangString.getLanguage() ) ) )
            .collect( Collectors.toSet() );
   }

   private Optional<String> validIrdiOrUri( final String input ) {
      return IRDI.from( input ).map( irdi -> "urn:irdi:" + irdi )
            .or( () -> {
               if ( input.startsWith( "http:" ) || input.startsWith( "https:" ) ) {
                  return Optional.of( input );
               } else if ( input.startsWith( "www." ) ) {
                  return Optional.of( "https://" + input );
               }
               return Optional.empty();
            } );
   }

   private Characteristic createCharacteristicFromRelationShipElement( final RelationshipElement relationshipElement,
         final AspectModelUrn propertyUrn ) {
      final Function<Reference, String> describeReference = ref ->
            switch ( ref.getType() ) {
               case MODEL_REFERENCE -> "Model reference ";
               case EXTERNAL_REFERENCE -> "External reference ";
            } + ref.getKeys().stream().map( Key::getValue ).collect( Collectors.joining( "/" ) );

      final Function<Reference, Optional<String>> referenceToUri = ref ->
            ref.getKeys().isEmpty()
                  ? Optional.empty()
                  : validIrdiOrUri( ref.getKeys().get( ref.getKeys().size() - 1 ).getValue() );

      final String characteristicDescription = "First reference: %s, second reference: %s".formatted(
            describeReference.apply( relationshipElement.getFirst() ), describeReference.apply( relationshipElement.getSecond() ) );

      final ElementName elementName = determineSubmodelElementName( relationshipElement, propertyUrn.getName() + "RelationshipElement" );
      final AspectModelUrn urn = aspectModelUrnFromSemanticId( relationshipElement )
            .orElseGet( () -> aspectUrn.withName( elementName.name() ) );

      final Stream<String> relationShipSeeReferences = Stream.of( relationshipElement.getFirst(), relationshipElement.getSecond() )
            .map( referenceToUri )
            .flatMap( Optional::stream );
      final List<String> seeReferences = Stream.concat( seeReferences( relationshipElement ).stream(), relationShipSeeReferences ).toList();

      final MetaModelBaseAttributes metaModelBaseAttributes = new MetaModelBaseAttributes( KnownVersion.getLatest(),
            urn, elementName.name(),
            langStringSet( relationshipElement.getDisplayName() ),
            Set.of( new LangString( characteristicDescription, Locale.ENGLISH ) ),
            seeReferences,
            elementName.isSynthetic() );

      // The RelationShipElement Characteristic dataType is pinned to string for now, see discussion
      // here https://github.com/eclipse-esmf/esmf-semantic-aspect-meta-model/issues/133
      return new DefaultCharacteristic( metaModelBaseAttributes,
            Optional.of( new DefaultScalar( XSD.xstring.getURI(), KnownVersion.getLatest() ) ) );
   }

   private Characteristic createCharacteristicFromBlob( final Blob blob, final AspectModelUrn propertyUrn ) {
      return createDefaultScalarCharacteristic( blob, XSD.base64Binary.getURI(),
            new DetermineAutomatically( propertyUrn.getName() + "Blob" ) );
   }

   private Characteristic createCharacteristicFromFile( final File file ) {
      return createDefaultScalarCharacteristic( file, XSD.anyURI.getURI(),
            new UseGivenUrn( AspectModelUrn.fromUrn( SAMMC.ResourcePath().getURI() ) ) );
   }

   private Characteristic createCharacteristicFromMultiLanguageProperty( final MultiLanguageProperty multiLanguageProperty ) {
      return createDefaultScalarCharacteristic( multiLanguageProperty, RDF.langString.getURI(),
            new UseGivenUrn( AspectModelUrn.fromUrn( SAMMC.MultiLanguageText().getURI() ) ) );
   }

   private Characteristic createCharacteristicFromProperty( final org.eclipse.digitaltwin.aas4j.v3.model.Property property,
         final AspectModelUrn propertyUrn ) {
      final String dataTypeUri = AasDataTypeMapper.mapAASXSDataTypeToAspectType( property.getValueType() ).getURI();
      final ElementNamingStrategy elementNamingStrategy;
      if ( dataTypeUri.equals( XSD.xboolean.getURI() ) ) {
         elementNamingStrategy = new UseGivenUrn( AspectModelUrn.fromUrn( SAMMC.Boolean().getURI() ) );
      } else if ( dataTypeUri.equals( XSD.dateTime.getURI() ) ) {
         elementNamingStrategy = new UseGivenUrn( AspectModelUrn.fromUrn( SAMMC.Timestamp().getURI() ) );
      } else {
         elementNamingStrategy = new DetermineAutomatically( propertyUrn.getName() + "Property" );
      }
      return createDefaultScalarCharacteristic( property, dataTypeUri, elementNamingStrategy );
   }

   private Characteristic createCharacteristicFromRange( final Range range, final AspectModelUrn propertyUrn ) {
      final String dataTypeUri = AasDataTypeMapper.mapAASXSDataTypeToAspectType( range.getValueType() ).getURI();
      final Optional<ScalarValue> maxValue = Optional.ofNullable( range.getMax() )
            .flatMap( maxLexical -> valueInstantiator.buildScalarValue( maxLexical, null, dataTypeUri ) );
      final Optional<ScalarValue> minValue = Optional.ofNullable( range.getMin() )
            .flatMap( minLexical -> valueInstantiator.buildScalarValue( minLexical, null, dataTypeUri ) );

      final MetaModelBaseAttributes constraintMetaModelBaseAttributes = new MetaModelBaseAttributes( KnownVersion.getLatest(),
            null, "RangeConstraint" + randomElementName( range ), Set.of(), Set.of(), List.of(), true );
      final RangeConstraint constraint = new DefaultRangeConstraint( constraintMetaModelBaseAttributes, minValue, maxValue,
            BoundDefinition.AT_LEAST, BoundDefinition.AT_MOST );

      final MetaModelBaseAttributes baseCharacteristicBaseAttributes = new MetaModelBaseAttributes( KnownVersion.getLatest(),
            null, "BaseCharacteristic" + randomElementName( range ), Set.of(), Set.of(), List.of(), true );
      final Characteristic baseCharacteristic = new DefaultCharacteristic( baseCharacteristicBaseAttributes,
            Optional.of( new DefaultScalar( dataTypeUri, KnownVersion.getLatest() ) ) );

      final MetaModelBaseAttributes traitMetaModelBaseAttributes = baseAttributes( range, new DetermineAutomatically(
            propertyUrn.getName() + "Trait" ) );
      return new DefaultTrait( traitMetaModelBaseAttributes, baseCharacteristic, List.of( constraint ) );
   }

   private Characteristic createCharacteristicFromReferenceElement( final ReferenceElement referenceElement,
         final AspectModelUrn propertyUrn ) {
      return createDefaultScalarCharacteristic( referenceElement, XSD.xstring.getURI(),
            new DetermineAutomatically( propertyUrn.getName() + "Reference" ) );
   }

   private Characteristic createCharacteristicFromCapability( final Capability capability, final AspectModelUrn propertyUrn ) {
      return createDefaultScalarCharacteristic( capability, XSD.xstring.getURI(),
            new DetermineAutomatically( propertyUrn.getName() + "Capability" ) );
   }

   private Characteristic createCharacteristicFromEntity( final org.eclipse.digitaltwin.aas4j.v3.model.Entity entity,
         final AspectModelUrn propertyUrn ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = baseAttributes( entity,
            new DetermineAutomatically( propertyUrn.getName() + "Entity" ) );
      final List<Property> properties = entity.getStatements().stream()
            .map( this::createProperty )
            .toList();
      final Entity entityType = new DefaultEntity( metaModelBaseAttributes, properties );
      return new DefaultSingleEntity( metaModelBaseAttributes, entityType );
   }

   private Characteristic createCharacteristicFromSubmodelElementCollection( final SubmodelElementCollection submodelElementCollection ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = baseAttributes( submodelElementCollection,
            new DetermineAutomatically( "ElementCollection" + randomElementName( submodelElementCollection ) ) );
      final List<Property> properties = submodelElementCollection.getValue().stream()
            .map( this::createProperty )
            .toList();
      final Entity entityType = new DefaultEntity( metaModelBaseAttributes, properties );
      return new DefaultSingleEntity( metaModelBaseAttributes, entityType );
   }

   private Characteristic createCharacteristicFromSubmodelElementList( final SubmodelElementList submodelElementList,
         final AspectModelUrn propertyUrn ) {
      final AasSubmodelElements type = submodelElementList.getTypeValueListElement();
      final Characteristic elementCharacteristic = createCharacteristic( type, submodelElementList, propertyUrn );
      final MetaModelBaseAttributes metaModelBaseAttributes = baseAttributes( submodelElementList,
            new DetermineAutomatically( propertyUrn.getName() + "List" ) );
      return new DefaultList( metaModelBaseAttributes, Optional.empty(), Optional.of( elementCharacteristic ) );
   }

   private AasSubmodelElements submodelElementType( final SubmodelElement element ) {
      return Arrays.stream( AasSubmodelElements.values() )
            .filter( entry -> {
               try {
                  final Class<?> correspondingClass = Class.forName( "org.eclipse.digitaltwin.aas4j.v3.model." +
                        CaseFormat.UPPER_UNDERSCORE.to( CaseFormat.UPPER_CAMEL, entry.toString() ) );
                  return correspondingClass.isAssignableFrom( element.getClass() );
               } catch ( final ClassNotFoundException exception ) {
                  return false;
               }
            } )
            .findFirst()
            .orElseThrow( () ->
                  new AspectModelGenerationException(
                        "Encountered unsupported SubmodelElement type " + element.getClass().getSimpleName() ) );
   }

   private Characteristic createDefaultScalarCharacteristic( final SubmodelElement submodelElement, final String dataTypeUri,
         final ElementNamingStrategy elementNamingStrategy ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = baseAttributes( submodelElement, elementNamingStrategy );
      return new DefaultCharacteristic( metaModelBaseAttributes,
            Optional.of( new DefaultScalar( dataTypeUri, KnownVersion.getLatest() ) ) );
   }

   private Characteristic createCharacteristic( final SubmodelElement element, final AspectModelUrn propertyUrn ) {
      return createCharacteristic( submodelElementType( element ), element, propertyUrn );
   }

   private Characteristic createCharacteristic( final AasSubmodelElements type, final SubmodelElement element,
         final AspectModelUrn propertyUrn ) {
      return switch ( type ) {
         case RELATIONSHIP_ELEMENT -> createCharacteristicFromRelationShipElement( (RelationshipElement) element, propertyUrn );
         case BLOB -> createCharacteristicFromBlob( (Blob) element, propertyUrn );
         case FILE -> createCharacteristicFromFile( (File) element );
         case MULTI_LANGUAGE_PROPERTY -> createCharacteristicFromMultiLanguageProperty( (MultiLanguageProperty) element );
         case PROPERTY -> createCharacteristicFromProperty( (org.eclipse.digitaltwin.aas4j.v3.model.Property) element, propertyUrn );
         case RANGE -> createCharacteristicFromRange( (Range) element, propertyUrn );
         case REFERENCE_ELEMENT -> createCharacteristicFromReferenceElement( (ReferenceElement) element, propertyUrn );
         case CAPABILITY -> createCharacteristicFromCapability( (Capability) element, propertyUrn );
         case ENTITY -> createCharacteristicFromEntity( (org.eclipse.digitaltwin.aas4j.v3.model.Entity) element, propertyUrn );
         case SUBMODEL_ELEMENT_COLLECTION -> createCharacteristicFromSubmodelElementCollection( (SubmodelElementCollection) element );
         case SUBMODEL_ELEMENT_LIST -> createCharacteristicFromSubmodelElementList( (SubmodelElementList) element, propertyUrn );
         default -> {
            LOG.warn( "Encountered unsupported SubmodelElement type {}", element.getClass().getSimpleName() );
            yield createDefaultScalarCharacteristic( element, XSD.xstring.getURI(),
                  new DetermineAutomatically( element.getClass().getSimpleName() ) );
         }
      };
   }
}
