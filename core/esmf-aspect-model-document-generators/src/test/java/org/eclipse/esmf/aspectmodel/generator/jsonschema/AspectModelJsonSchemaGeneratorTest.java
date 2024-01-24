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

package org.eclipse.esmf.aspectmodel.generator.jsonschema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

public class AspectModelJsonSchemaGeneratorTest extends MetaModelVersions {
   private final ObjectMapper objectMapper = new ObjectMapper();
   final Configuration config = Configuration.defaultConfiguration().addOptions( Option.SUPPRESS_EXCEPTIONS );

   private JsonNode parseJson( final String json ) {
      try {
         return objectMapper.readTree( json );
      } catch ( final JsonProcessingException e ) {
         e.printStackTrace();
         fail();
      }
      return null;
   }

   private void showJson( final JsonNode node ) {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
         objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, node );
      } catch ( final IOException e ) {
         e.printStackTrace();
         fail();
      }
      System.out.println( out );
   }

   private Aspect loadAspect( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModel( testAspect, metaModelVersion ).get();
      return AspectModelLoader.getSingleAspect( versionedModel ).get();
   }

   private JsonNode generatePayload( final Aspect aspect ) {
      final AspectModelJsonPayloadGenerator payloadGenerator = new AspectModelJsonPayloadGenerator( aspect );
      try {
         return parseJson( payloadGenerator.generateJson() );
      } catch ( final IOException e ) {
         e.printStackTrace();
         fail();
      }
      return null;
   }

   private JsonNode buildJsonSchema( final Aspect aspect ) {
      return new AspectModelJsonSchemaGenerator().apply( aspect, Locale.ENGLISH );
   }

   private JsonSchema parseSchema( final JsonNode jsonSchema ) {
      final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
      try {
         return factory.getJsonSchema( jsonSchema );
      } catch ( final ProcessingException e ) {
         e.printStackTrace();
         fail();
      }
      return null;
   }

   private void assertPayloadIsValid( final JsonNode schema, final JsonNode payload ) {
      try {
         final ProcessingReport report = parseSchema( schema ).validate( payload );
         if ( !report.isSuccess() ) {
            System.out.println( report );
         }
         assertThat( report.isSuccess() ).isTrue();
      } catch ( final Throwable throwable ) {
         System.out.println( "Payload:" );
         showJson( payload );
         System.out.println( "Schema:" );
         showJson( schema );
         fail();
      }
   }

   private void assertPayloadIsValid( final JsonNode schema, final Aspect aspect ) {
      assertPayloadIsValid( schema, generatePayload( aspect ) );
   }

   /**
    * This test is parmaterized with all available test models and tests the round trip generation
    * of both the Sample JSON Payload and the JSON schema and checks that the schema successfully
    * validates the payload. The models with known payload generation issues are excluded.
    *
    * @param testAspect the test aspect enum field
    */
   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         "ASPECT_WITH_CONSTRAINED_COLLECTION", // Broken model
         "ASPECT_WITH_ENUMERATION_WITHOUT_SCALAR_VARIABLE", //Invalid Aspect Model
         "MODEL_WITH_BROKEN_CYCLES" // contains cycles, but all of them should be "breakable", need to be investigated
   } )
   public void testGeneration( final TestAspect testAspect ) {
      final Aspect aspect = loadAspect( testAspect, KnownVersion.getLatest() );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['$schema']" ) )
            .isEqualTo( AspectModelJsonSchemaVisitor.JSON_SCHEMA_VERSION );
      assertThat( context.<String> read( "$['type']" ) ).isEqualTo( "object" );
      assertPayloadIsValid( schema, aspect );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testTypeMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SIMPLE_TYPES, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );

      String characteristicReference = context.<String> read( "$['properties']['anyUriProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/AnyUriPropertyCharacteristic" );
      String characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['description']" ) )
            .isEqualTo( "This is an anyURI characteristic." );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['format']" ) ).isEqualTo( "uri" );

      characteristicReference = context.<String> read( "$['properties']['base64BinaryProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/Base64BinaryPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['description']" ) )
            .isEqualTo( "This is a base64Binary characteristic." );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['contentEncoding']" ) ).isEqualTo(
            "base64" );

      final String booleanUrn = String.format( "urn_samm_org.eclipse.esmf.samm_characteristic_%s_Boolean",
            KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['booleanProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + booleanUrn );
      assertThat( context.<String> read( "$['components']['schemas']['" + booleanUrn + "']['type']" ) )
            .isEqualTo( "boolean" );

      characteristicReference = context.<String> read( "$['properties']['byteProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/BytePropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['description']" ) )
            .isEqualTo( "This is a byteProperty characteristic." );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      final String unitReferenceUrn = String
            .format( "urn_samm_org.eclipse.esmf.samm_characteristic_%s_UnitReference",
                  KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['curieProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + unitReferenceUrn );
      assertThat( context.<String> read( "$['components']['schemas']['" + unitReferenceUrn + "']['type']" ) )
            .isEqualTo( "string" );

      characteristicReference = context.<String> read( "$['properties']['dateProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/DatePropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['format']" ) ).isEqualTo( "date" );

      final String timestampUrn = String.format( "urn_samm_org.eclipse.esmf.samm_characteristic_%s_Timestamp",
            KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['dateTimeProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + timestampUrn );
      assertThat( context.<String> read( "$['components']['schemas']['" + timestampUrn + "']['type']" ) )
            .isEqualTo( "string" );

      characteristicReference = context.<String> read( "$['properties']['dateTimeStampProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/DateTimeStampPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['format']" ) ).isEqualTo( "date-time" );

      characteristicReference = context.<String> read( "$['properties']['dayTimeDuration']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/DayTimeDurationCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );

      characteristicReference = context.<String> read( "$['properties']['decimalProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/DecimalPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['doubleProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/DoublePropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['durationProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/DurationPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['format']" ) ).isEqualTo( "duration" );

      characteristicReference = context.<String> read( "$['properties']['floatProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/FloatPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['gMonthDayProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/GMonthDayPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );

      characteristicReference = context.<String> read( "$['properties']['gMonthProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/GMonthPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );

      characteristicReference = context.<String> read( "$['properties']['gYearMonthProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/GYearMonthPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );

      characteristicReference = context.<String> read( "$['properties']['gYearProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/GYearPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );

      characteristicReference = context.<String> read( "$['properties']['hexBinaryProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/HexBinaryPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['pattern']" ) ).isEqualTo(
            "([0-9a-fA-F])([0-9a-fA-F])*" );

      characteristicReference = context.<String> read( "$['properties']['intProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/IntPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['integerProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/IntegerPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      final String multiLanguageTextUrn = String
            .format( "urn_samm_org.eclipse.esmf.samm_characteristic_%s_MultiLanguageText",
                  KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['langStringProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + multiLanguageTextUrn );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['" + multiLanguageTextUrn + "']['type']" ) ).isEqualTo( "object" );

      characteristicReference = context.<String> read( "$['properties']['longProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/LongPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['negativeIntegerProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/NegativeIntegerPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['nonNegativeIntegerProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/NonNegativeIntegerPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['nonPositiveInteger']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/NonPositiveIntegerCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['positiveIntegerProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/PositiveIntegerPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['shortProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/ShortPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      final String textUrn = String.format( "urn_samm_org.eclipse.esmf.samm_characteristic_%s_Text",
            KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['stringProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + textUrn );
      assertThat( context.<String> read( "$['components']['schemas']['" + textUrn + "']['type']" ) ).isEqualTo( "string" );

      characteristicReference = context.<String> read( "$['properties']['timeProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/TimePropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['format']" ) ).isEqualTo( "time" );

      characteristicReference = context.<String> read( "$['properties']['unsignedByteProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/UnsignedBytePropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['unsignedIntProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/UnsignedIntPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['unsignedLongProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/UnsignedLongPropertyQuantifiable" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['unsignedShortProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/UnsignedShortPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      characteristicReference = context.<String> read( "$['properties']['yearMonthDurationProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/YearMonthDurationPropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOptionalPropertyMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      final String textUrn = String.format( "urn_samm_org.eclipse.esmf.samm_characteristic_%s_Text",
            KnownVersion.getLatest().toVersionString() );

      assertThat( context.<String> read( "$['components']['schemas']['" + textUrn + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + textUrn );
      assertThat( context.<List<String>> read( "$['required']" ) ).isNull();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithRecursivePropertyWithOptional( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      showJson( schema );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEntity']['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestEntity" );
      assertThat( context.<List<String>> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_testItemCharacteristic']['required']" ) ).isNull();
      assertThat( context.<List<String>> read( "$['required']" ).stream().findFirst().get() ).isEqualTo( "testProperty" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testNotInPayloadPropertyMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES,
            metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<Map<?, ?>> read( "$['properties']['description']" ) ).isNull();

      assertThat( context.<String> read( "$['components']['schemas']['SystemStateEnumeration']['description']" ) )
            .isEqualTo( "Defines which states the system may have." );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionWithElementConstraint( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ELEMENT_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );

      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestCollection']['description']" ) )
            .isEqualTo( "This is a test collection." );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestCollection']['items']['type']" ) )
            .isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestCollection']['items']['minimum']" ) )
            .isCloseTo( 2.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestCollection']['items']['maximum']" ) )
            .isCloseTo( 10.5d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestCollection']['items']['exclusiveMaximum']" ) )
            .isEqualTo( false );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestCollection']['items']['exclusiveMinimum']" ) )
            .isEqualTo( false );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      showJson( schema );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      final String textUrn = String.format( "urn_samm_org.eclipse.esmf.samm_characteristic_%s_Text",
            KnownVersion.getLatest().toVersionString() );

      assertThat( context.<String> read( "$['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEntity']['description']" ) )
            .isEqualTo( "This is a test Entity Characteristic" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEntity']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEntity']['properties']"
            + "['entityProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + textUrn );
      assertThat( context.<List<String>> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEntity']['required']" ) )
            .isEqualTo( List.of( "entityProperty" ) );
      assertThat( context.<List<String>> read( "$['required']" ) ).isEqualTo( List.of( "testProperty" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLengthConstraintForStringMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LENGTH_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraint']['description']" ) )
            .isEqualTo( "This is a test length constraint." );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraint']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraint']['maxLength']" ) ).isEqualTo( 10 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraint']['minLength']" ) ).isEqualTo( 5 );
   }

   /**
    * Verify that the json schema generated from the given aspect model contains descriptions as per the chosen language.
    *
    * @param metaModelVersion the used meta model version.
    */
   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMultilingualDescriptions( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION, metaModelVersion );
      final JsonNode schemaEnglish = new AspectModelJsonSchemaGenerator().apply( aspect, Locale.ENGLISH );

      assertThat( schemaEnglish.get( "description" ).asText() )
            .isEqualTo( "Aspect With Multilingual Descriptions" );
      assertThat( schemaEnglish.at( "/properties/testString/description" ).asText() )
            .isEqualTo( "This is a test string" );

      final JsonNode schemaGerman = new AspectModelJsonSchemaGenerator().apply( aspect, Locale.GERMAN );
      assertThat( schemaGerman.get( "description" ).asText() )
            .isEqualTo( "Aspekt mit mehrsprachigen Beschreibungen" );
      assertThat( schemaGerman.at( "/properties/testString/description" ).asText() )
            .isEqualTo( "Es ist ein Test-String" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLengthConstraintForListMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST_WITH_LENGTH_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read(
            "$['properties']['testPropertyCollectionLengthConstraint']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraintWithCollection" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraintWithCollection']['description']" ) )
            .isEqualTo( "Test Length Constraint with collection" );
      assertThat( context.<String> read(
            "$['components']['schemas']['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraintWithCollection']['type']" ) ).isEqualTo(
            "array" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraintWithCollection']['maxItems']" ) )
            .isEqualTo( 10 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraintWithCollection']['minItems']" ) )
            .isEqualTo( 1 );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestLengthConstraintWithCollection']['items']['type']" ) )
            .isEqualTo( "number" );

      final ObjectNode payload = JsonNodeFactory.instance.objectNode();
      final ArrayNode array = JsonNodeFactory.instance.arrayNode();
      array.add( 42 );
      payload.set( "testPropertyCollectionLengthConstraint", array );
      assertPayloadIsValid( schema, payload );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint']['description']" ) )
            .isEqualTo( "This is a test range constraint." );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint']['minimum']" ) )
            .isCloseTo( 2.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint']['maximum']" ) )
            .isCloseTo( 10.5d, Percentage.withPercentage( 1.0d ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintOnConstrainedNumericType( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_ON_CONSTRAINED_NUMERIC_TYPE,
            metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint']['description']" ) )
            .isEqualTo( "This is a test range constraint." );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint']['minimum']" ) ).isEqualTo( 5 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRangeConstraint']['maximum']" ) )
            .isEqualTo( Short.MAX_VALUE );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintWithBoundsMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_EXCLUSIVE_RANGE_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['floatProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_FloatRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_FloatRange']['description']" ) )
            .isEqualTo( "This is a floating range constraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_FloatRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_FloatRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_FloatRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_FloatRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_FloatRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['doubleProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_DoubleRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DoubleRange']['description']" ) )
            .isEqualTo( "This is a double range constraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DoubleRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DoubleRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DoubleRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DoubleRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DoubleRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['decimalProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_DecimalRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DecimalRange']['description']" ) )
            .isEqualTo( "This is a decimal range constraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DecimalRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DecimalRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DecimalRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DecimalRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_DecimalRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['integerProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_IntegerRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntegerRange']['description']" ) )
            .isEqualTo( "This is a integer range constraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntegerRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntegerRange']['minimum']" ) ).isEqualTo( 12 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntegerRange']['maximum']" ) ).isEqualTo( 23 );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntegerRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntegerRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['intProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_IntRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntRange']['minimum']" ) ).isEqualTo( 12 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntRange']['maximum']" ) ).isEqualTo( 23 );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_IntRange']['exclusiveMinimum']" ) ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );

      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestList" );

      assertThat( context.<String> read( "$['components']['schemas']['urn_samm_org.eclipse.esmf.test_1.0.0_TestList']['description']" ) )
            .isEqualTo( "This is a test list." );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestList']['type']" ) ).isEqualTo( "array" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestList']['items']['type']" ) ).isEqualTo( "string" );
   }

   @Test
   public void testSetMapping() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SET, KnownVersion.getLatest() );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );

      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestSet" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestSet']['type']" ) ).isEqualTo( "array" );
      assertThat( context.<String> read( "$['components']['schemas']['urn_samm_org.eclipse.esmf.test_1.0.0_TestSet']['description']" ) )
            .isEqualTo( "This is a test set." );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestSet']['uniqueItems']" ) ).isTrue();
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestSet']['items']['type']" ) ).isEqualTo( "string" );
   }

   @Test
   public void testSortedSetSetMapping() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SORTED_SET, KnownVersion.getLatest() );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );

      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestSortedSet" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestSortedSet']['type']" ) ).isEqualTo( "array" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestSortedSet']['description']" ) )
            .isEqualTo( "This is a test sorted set." );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestSortedSet']['uniqueItems']" ) ).isTrue();
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestSortedSet']['items']['type']" ) ).isEqualTo( "string" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLangStringMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final String multiLanguageTextUrn = String
            .format( "urn_samm_org.eclipse.esmf.samm_characteristic_%s_MultiLanguageText",
                  KnownVersion.getLatest().toVersionString() );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['prop']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + multiLanguageTextUrn );
      assertThat( context.<String> read( "$['components']['schemas']['" + multiLanguageTextUrn + "']['type']" ) )
            .isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']['" + multiLanguageTextUrn + "']['description']" ) )
            .isEqualTo( "Describes a Property which contains plain text in multiple "
                  + "languages. This is intended exclusively for human readable strings, not for "
                  + "identifiers, measurement values, etc." );
      assertThat( context.<String> read( "$['components']['schemas']['" + multiLanguageTextUrn + "']['patternProperties']"
            + "['^.*$']['type']" ) ).isEqualTo( "string" );

      final ObjectNode payload = JsonNodeFactory.instance.objectNode();
      final ObjectNode propertyValue = JsonNodeFactory.instance.objectNode();
      propertyValue.put( "en", "foo" );
      propertyValue.put( "de", "bar" );
      payload.set( "prop", propertyValue );
      assertPayloadIsValid( schema, payload );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEitherMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_EITHER, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestEither" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEither']['description']" ) )
            .isEqualTo( "This is a test Either." );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEither']['properties']['left']['type']" ) )
            .isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEither']['properties']['right']['type']" ) )
            .isEqualTo( "boolean" );

      final ObjectNode leftPayload = JsonNodeFactory.instance.objectNode();
      final ObjectNode leftProperty = JsonNodeFactory.instance.objectNode();
      leftProperty.put( "left", "foo" );
      leftPayload.set( "testProperty", leftProperty );
      assertPayloadIsValid( schema, leftPayload );

      final ObjectNode rightPayload = JsonNodeFactory.instance.objectNode();
      final ObjectNode rightTestProperty = JsonNodeFactory.instance.objectNode();
      rightTestProperty.put( "right", true );
      rightPayload.set( "testProperty", rightTestProperty );
      assertPayloadIsValid( schema, rightPayload );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumScalarMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENUMERATION, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestEnumeration" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEnumeration']['description']" ) )
            .isEqualTo( "This is a test for enumeration." );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEnumeration']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<List<Integer>> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestEnumeration']['enum']" ) )
            .containsExactly( 1, 2, 3 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumComplexMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENUM_HAVING_NESTED_ENTITIES, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['result']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_EvaluationResults" );

      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_EvaluationResults']['description']" ) )
            .isEqualTo( "Possible values for the evaluation of a process" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_EvaluationResults']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']"
            + "['schemas']['urn_samm_org.eclipse.esmf.test_1.0.0_EvaluationResults']['oneOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/ResultGood" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_EvaluationResults']['oneOf'][1]['$ref']" ) )
            .isEqualTo( "#/components/schemas/ResultBad" );

      assertThat( context.<String> read( "$['components']['schemas']['ResultGood']['properties']['details']"
            + "['properties']['description']['description']" ) )
            .isEqualTo( "Human-readable description of the process result code" );
      assertThat( context.<String> read( "$['components']['schemas']['ResultGood']['properties']['details']"
            + "['properties']['description']['enum'][0]" ) ).isEqualTo( "Result succeeded" );
      assertThat( context.<String> read( "$['components']['schemas']['ResultGood']['properties']['details']"
            + "['properties']['message']['enum'][0]" ) ).isEqualTo( "Evaluation succeeded." );
      assertThat( context.<Integer> read( "$['components']['schemas']['ResultGood']['properties']['details']"
            + "['properties']['numericCode']['enum'][0]" ) ).isEqualTo( 10 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumComplexWithNotInPayloadMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_EXTENDED_ENUMS_WITH_NOT_IN_PAYLOAD_PROPERTY,
            metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['result']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_EvaluationResults" );

      assertThat( context.<String> read( "$['components']['schemas']['ResultNoStatus']['properties']['average']"
            + "['description']" ) ).isEqualTo( "Some artifical average value" );
      assertThat( context.<Integer> read( "$['components']['schemas']['ResultNoStatus']['properties']['average']"
            + "['enum'][0]" ) ).isEqualTo( 3 );
      assertThat( context.<String> read( "$['components']['schemas']['ResultNoStatus']['properties']['numericCode']"
            + "['description']" ) ).isEqualTo( "Numeric code for the evaluation result" );
      assertThat( context.<Integer> read( "$['components']['schemas']['ResultNoStatus']['properties']['numericCode']"
            + "['enum'][0]" ) ).isEqualTo( -1 );
      assertThat( context.<String> read( "$['components']['schemas']['ResultNoStatus']['properties']['description']"
            + "['enum'][0]" ) ).isNull();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumWithLangStringMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestEnumeration" );

      assertThat( context.<String> read(
            "$['components']['schemas']['urn_samm_org.eclipse.esmf.test_1.0.0_TestEnumeration']['description']" ) )
            .isEqualTo( "This is a test for enumeration." );

      assertThat( context.<String> read( "$['components']['schemas']['entityInstance']['type']" ) )
            .isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']['entityInstance']['properties']['entityProperty']"
            + "['enum'][0]['en']" ) ).isEqualTo( "This is a test." );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRegularExpressionConstraintMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_REGULAR_EXPRESSION_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_TestRegularExpressionConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRegularExpressionConstraint']['description']" ) )
            .isEqualTo( "This is a test regular expression constraint." );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRegularExpressionConstraint']['type']" ) )
            .isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_TestRegularExpressionConstraint']['pattern']" ) )
            .isEqualTo( "^[0-9]*$" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testComplexEntityCollectionEnum( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      showJson( schema );
      assertThat( context.<String> read( "$['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['properties']['myPropertyOne']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_MyEnumerationOne" );

      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_MyEnumerationOne']['description']" ) )
            .isEqualTo( "This is my enumeration one" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_MyEnumerationOne']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_MyEnumerationOne']['oneOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/entityInstanceOne" );

      assertThat( context.<String> read( "$['components']['schemas']['entityInstanceOne']['type']" ) )
            .isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']['entityInstanceOne']['required'][0]" ) )
            .isEqualTo( "entityPropertyOne" );
      assertThat( context.<String> read( "$['components']['schemas']['entityInstanceOne']['properties']"
            + "['entityPropertyOne']['type']" ) ).isEqualTo( "array" );
      assertThat( context.<Integer> read( "$['components']['schemas']['entityInstanceOne']['properties']"
            + "['entityPropertyOne']['minItems']" ) ).isEqualTo( 1 );
      assertThat( context.<Integer> read( "$['components']['schemas']['entityInstanceOne']['properties']"
            + "['entityPropertyOne']['maxItems']" ) ).isEqualTo( 1 );
      assertThat( context.<String> read( "$['components']['schemas']['entityInstanceOne']['properties']"
            + "['entityPropertyOne']['items']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']['entityInstanceOne']['properties']"
            + "['entityPropertyOne']['items']['enum'][0]['entityPropertyTwo']" ) ).isEqualTo( "foo" );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractSingleEntity( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      showJson( schema );

      assertThatExceptionOfType( PathNotFoundException.class )
            .isThrownBy( () -> context.<String> read( "$['components']['schemas']['ExtendingTestEntity']" ) );
      assertThatExceptionOfType( PathNotFoundException.class )
            .isThrownBy( () -> context.<String> read( "$['components']['schemas']['AbstractTestEntity']" ) );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity']['allOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity']['properties']['entityProperty']['$ref']" ) )
            .isEqualTo(
                  "#/components/schemas/urn_samm_org.eclipse.esmf.samm_characteristic_" + KnownVersion.getLatest().toVersionString()
                        + "_Text" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity']['description']" ) )
            .isEqualTo( "This is an abstract test entity" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity']['properties']['abstractTestProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestPropertyCharacteristic" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity" );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractEntity( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      showJson( schema );

      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity']['description']" ) )
            .isEqualTo( "This is a test Entity Characteristic" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity']['allOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity']['properties']['entityProperty']['$ref']" ) )
            .isEqualTo(
                  "#/components/schemas/urn_samm_org.eclipse.esmf.samm_characteristic_" + KnownVersion.getLatest().toVersionString()
                        + "_Text" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity']['description']" ) )
            .isEqualTo( "This is a abstract test entity" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity']['properties']['abstractTestProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestPropertyCharacteristic" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity" );
   }

   /**
    * Test to validate the json schema generated from the given aspect model containing an abstract property.
    *
    * @param metaModelVersion the used meta model version.
    */
   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractProperty( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ABSTRACT_PROPERTY, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      assertThat( schema.at( "/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity/"
            + "description" ).asText() )
            .isEqualTo( "This is a test entity" );
      assertThat( schema.at( "/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity/"
            + "properties/abstractTestProperty/description" ).asText() )
            .isEqualTo( "This is an abstract test property" );
      assertThat( schema.at( "/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity/"
            + "properties/abstractTestProperty/$ref" ).asText() )
            .isEqualTo(
                  "#/components/schemas/urn_samm_org.eclipse.esmf.samm_characteristic_" + KnownVersion.getLatest().toVersionString()
                        + "_Text" );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAspectWithCollectionWithAbstractEntity( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      showJson( schema );

      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity']['allOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity']['description']" ) )
            .isEqualTo( "This is an abstract test entity" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_ExtendingTestEntity']['properties']['entityProperty']['$ref']" ) )
            .isEqualTo(
                  "#/components/schemas/urn_samm_org.eclipse.esmf.samm_characteristic_" + KnownVersion.getLatest().toVersionString()
                        + "_Text" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity']['properties']['abstractTestProperty']['description']" ) )
            .isEqualTo( "This is an abstract test property" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity']['properties']['abstractTestProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestPropertyCharacteristic" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_EntityCollectionCharacteristic']['description']" ) )
            .isEqualTo( "This is an entity collection characteristic" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_samm_org.eclipse.esmf.test_1.0.0_EntityCollectionCharacteristic']['items']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_AbstractTestEntity" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_samm_org.eclipse.esmf.test_1.0.0_EntityCollectionCharacteristic" );
   }
}
