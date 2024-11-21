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
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;
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
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class AspectModelJsonSchemaGeneratorTest {
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

   private JsonNode generatePayload( final Aspect aspect ) {
      final AspectModelJsonPayloadGenerator payloadGenerator = new AspectModelJsonPayloadGenerator( aspect );
      return parseJson( payloadGenerator.generateJson() );
   }

   private JsonNode buildJsonSchema( final Aspect aspect ) {
      final JsonSchemaGenerationConfig config = JsonSchemaGenerationConfigBuilder.builder()
            .locale( Locale.ENGLISH )
            .useExtendedTypes( true )
            .build();
      return new AspectModelJsonSchemaGenerator( aspect, config ).getContent();
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
         fail( throwable );
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
         "MODEL_WITH_BROKEN_CYCLES" // contains cycles, but all of them should be "breakable", need to be investigated
   } )
   public void testGeneration( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['$schema']" ) )
            .isEqualTo( AspectModelJsonSchemaVisitor.JSON_SCHEMA_VERSION );
      assertThat( context.<String> read( "$['type']" ) ).isEqualTo( "object" );
      assertPayloadIsValid( schema, aspect );
   }

   @Test
   public void testSchemaNameClash() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY ).aspect();
      final JsonSchemaGenerationConfig config = JsonSchemaGenerationConfigBuilder.builder()
            .locale( Locale.ENGLISH )
            .reservedSchemaNames( List.of( "TestEntity" ) )
            .build();
      final JsonNode schema = new AspectModelJsonSchemaGenerator( aspect, config ).getContent();
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['$schema']" ) ).isEqualTo( AspectModelJsonSchemaVisitor.JSON_SCHEMA_VERSION );
      assertThat( context.<String> read( "$['type']" ) ).isEqualTo( "object" );
      assertPayloadIsValid( schema, aspect );
      assertThat( context.<String> read( "$['components']['schemas']['TestEntity0']['type']" ) ).isEqualTo( "object" );
   }

   @Test
   public void testTypeMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_SIMPLE_TYPES ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      showJson( schema );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );

      final String booleanName = SammNs.SAMMC.Boolean().getLocalName();

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

      assertThat( context.<String> read( "$['properties']['booleanProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + booleanName );
      assertThat( context.<String> read( "$['components']['schemas']['" + booleanName + "']['type']" ) )
            .isEqualTo( "boolean" );

      characteristicReference = context.<String> read( "$['properties']['byteProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/BytePropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['description']" ) )
            .isEqualTo( "This is a byteProperty characteristic." );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "number" );

      final String unitReference = SammNs.SAMMC.UnitReference().getLocalName();
      assertThat( context.<String> read( "$['properties']['curieProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + unitReference );
      assertThat( context.<String> read( "$['components']['schemas']['" + unitReference + "']['type']" ) )
            .isEqualTo( "string" );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + unitReference + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.UnitReference().getURI() );

      characteristicReference = context.<String> read( "$['properties']['dateProperty']['$ref']" );
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/DatePropertyCharacteristic" );
      characteristicName = characteristicReference.substring( characteristicReference.lastIndexOf( "/" ) + 1 );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['" + characteristicName + "']['format']" ) ).isEqualTo( "date" );

      final String timestamp = SammNs.SAMMC.Timestamp().getLocalName();
      assertThat( context.<String> read( "$['properties']['dateTimeProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + timestamp );
      assertThat( context.<String> read( "$['components']['schemas']['" + timestamp + "']['type']" ) )
            .isEqualTo( "string" );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + timestamp + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.Timestamp().getURI() );

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

      final String multiLanguageText = SammNs.SAMMC.MultiLanguageText().getLocalName();
      assertThat( context.<String> read( "$['properties']['langStringProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + multiLanguageText );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['" + multiLanguageText + "']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + multiLanguageText + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.MultiLanguageText().getURI() );

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

      final String text = SammNs.SAMMC.Text().getLocalName();
      assertThat( context.<String> read( "$['properties']['stringProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + text );
      assertThat( context.<String> read( "$['components']['schemas']['" + text + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + text + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.Text().getURI() );

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
      assertThat( characteristicReference ).isEqualTo( "#/components/schemas/UnsignedLongPropertyCharacteristic" );
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

   @Test
   public void testOptionalPropertyMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      final String text = SammNs.SAMMC.Text().getLocalName();

      assertThat( context.<String> read( "$['components']['schemas']['" + text + "']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + text + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.Text().getURI() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + text );
      assertThat( context.<List<String>> read( "$['required']" ) ).isNull();
   }

   @Test
   public void testAspectWithRecursivePropertyWithOptional() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      showJson( schema );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['components']['schemas']['TestEntity']['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestItemCharacteristic" );
      assertThat(
            context.<String> read(
                  "$['components']['schemas']['TestItemCharacteristic']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestItemCharacteristic" );
      assertThat( context.<List<String>> read( "$['components']['schemas']['TestItemCharacteristic']['required']" ) ).isNull();
      assertThat( context.<List<String>> read( "$['required']" ).stream().findFirst().get() ).isEqualTo( "testProperty" );
   }

   @Test
   public void testNotInPayloadPropertyMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<Map<?, ?>> read( "$['properties']['description']" ) ).isNull();

      assertThat( context.<String> read( "$['components']['schemas']['SystemStateCharacteristic']['description']" ) )
            .isEqualTo( "Defines which states the system may have." );
   }

   @Test
   public void testCollectionWithElementConstraint() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION_WITH_ELEMENT_CONSTRAINT ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );

      assertThat( context.<String> read( "$['components']['schemas']['TestCollection']['description']" ) )
            .isEqualTo( "This is a test collection." );
      assertThat( context.<String> read( "$['components']['schemas']['TestCollection']['items']['type']" ) )
            .isEqualTo( "number" );
      assertThat( context.<String> read(
            "$['components']['schemas']['TestCollection']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestCollection" );
      assertThat( context.<Double> read( "$['components']['schemas']['TestCollection']['items']['minimum']" ) )
            .isCloseTo( 2.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']['TestCollection']['items']['maximum']" ) )
            .isCloseTo( 10.5d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']['TestCollection']['items']['exclusiveMaximum']" ) )
            .isEqualTo( false );
      assertThat( context.<Boolean> read( "$['components']['schemas']['TestCollection']['items']['exclusiveMinimum']" ) )
            .isEqualTo( false );
   }

   @Test
   public void testEntityMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      showJson( schema );
      final DocumentContext context = JsonPath.parse( schema.toString() );

      final String text = SammNs.SAMMC.Text().getLocalName();

      assertThat( context.<String> read( "$['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/EntityCharacteristic" );
      assertThat( context.<String> read( "$['components']['schemas']['TestEntity']['description']" ) )
            .isEqualTo( "This is a test entity" );
      assertThat( context.<String> read( "$['components']['schemas']['TestEntity']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']['TestEntity']['properties']['entityProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + text );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + text + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.Text().getURI() );
      assertThat( context.<List<String>> read( "$['components']['schemas']['TestEntity']['required']" ) )
            .isEqualTo( List.of( "entityProperty" ) );
      assertThat( context.<List<String>> read( "$['required']" ) ).isEqualTo( List.of( "testProperty" ) );
   }

   @Test
   public void testLengthConstraintForStringMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_LENGTH_CONSTRAINT ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestLengthConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']['TestLengthConstraint']['description']" ) )
            .isEqualTo( "This is a test length constraint." );
      assertThat( context.<String> read( "$['components']['schemas']['TestLengthConstraint']['type']" ) ).isEqualTo( "string" );
      assertThat(
            context.<String> read(
                  "$['components']['schemas']['TestLengthConstraint']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestLengthConstraint" );
      assertThat( context.<Integer> read( "$['components']['schemas']['TestLengthConstraint']['maxLength']" ) ).isEqualTo( 10 );
      assertThat( context.<Integer> read( "$['components']['schemas']['TestLengthConstraint']['minLength']" ) ).isEqualTo( 5 );
   }

   /**
    * Verify that the json schema generated from the given aspect model contains descriptions as per the chosen language.
    */
   @Test
   public void testMultilingualDescriptions() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION ).aspect();
      final JsonNode schemaEnglish = new AspectModelJsonSchemaGenerator( aspect,
            JsonSchemaGenerationConfigBuilder.builder().locale( Locale.ENGLISH ).build() ).getContent();

      assertThat( schemaEnglish.get( "description" ).asText() )
            .isEqualTo( "Aspect With Multilingual Descriptions" );
      assertThat( schemaEnglish.at( "/properties/testString/description" ).asText() )
            .isEqualTo( "This is a test string" );

      final JsonNode schemaGerman = new AspectModelJsonSchemaGenerator( aspect,
            JsonSchemaGenerationConfigBuilder.builder().locale( Locale.GERMAN ).build() ).getContent();
      assertThat( schemaGerman.get( "description" ).asText() )
            .isEqualTo( "Aspekt mit mehrsprachigen Beschreibungen" );
      assertThat( schemaGerman.at( "/properties/testString/description" ).asText() )
            .isEqualTo( "Es ist ein Test-String" );
   }

   @Test
   public void testLengthConstraintForListMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_LIST_WITH_LENGTH_CONSTRAINT ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read(
            "$['properties']['testPropertyCollectionLengthConstraint']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestLengthConstraintWithCollection" );
      assertThat( context.<String> read( "$['components']['schemas']['TestLengthConstraintWithCollection']['description']" ) )
            .isEqualTo( "Test Length Constraint with collection" );
      assertThat(
            context.<String> read(
                  "$['components']['schemas']['TestLengthConstraintWithCollection']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION
                        + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestLengthConstraintWithCollection" );
      assertThat( context.<String> read( "$['components']['schemas']['TestLengthConstraintWithCollection']['type']" ) )
            .isEqualTo( "array" );
      assertThat( context.<Integer> read( "$['components']['schemas']['TestLengthConstraintWithCollection']['maxItems']" ) )
            .isEqualTo( 10 );
      assertThat( context.<Integer> read( "$['components']['schemas']['TestLengthConstraintWithCollection']['minItems']" ) )
            .isEqualTo( 1 );
      assertThat( context.<String> read( "$['components']['schemas']['TestLengthConstraintWithCollection']['items']['type']" ) )
            .isEqualTo( "number" );

      final ObjectNode payload = JsonNodeFactory.instance.objectNode();
      final ArrayNode array = JsonNodeFactory.instance.arrayNode();
      array.add( 42 );
      payload.set( "testPropertyCollectionLengthConstraint", array );
      assertPayloadIsValid( schema, payload );
   }

   @Test
   public void testRangeConstraintMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestRangeConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']['TestRangeConstraint']['description']" ) )
            .isEqualTo( "This is a test range constraint." );
      assertThat( context.<String> read( "$['components']['schemas']['TestRangeConstraint']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<String> read(
            "$['components']['schemas']['TestRangeConstraint']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestRangeConstraint" );
      assertThat( context.<Double> read( "$['components']['schemas']['TestRangeConstraint']['minimum']" ) )
            .isCloseTo( 2.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']['TestRangeConstraint']['maximum']" ) )
            .isCloseTo( 10.5d, Percentage.withPercentage( 1.0d ) );
   }

   @Test
   public void testRangeConstraintOnConstrainedNumericType() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_ON_CONSTRAINED_NUMERIC_TYPE ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestRangeConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']['TestRangeConstraint']['description']" ) )
            .isEqualTo( "This is a test range constraint." );
      assertThat( context.<String> read( "$['components']['schemas']['TestRangeConstraint']['type']" ) ).isEqualTo( "number" );
      assertThat(
            context.<String> read(
                  "$['components']['schemas']['TestRangeConstraint']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestRangeConstraint" );
      assertThat( context.<Integer> read( "$['components']['schemas']['TestRangeConstraint']['minimum']" ) ).isEqualTo( 5 );
      assertThat( context.<Integer> read( "$['components']['schemas']['TestRangeConstraint']['maximum']" ) )
            .isEqualTo( Short.MAX_VALUE );
   }

   @Test
   public void testRangeConstraintWithBoundsMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_EXCLUSIVE_RANGE_CONSTRAINT ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['floatProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/FloatRange" );
      assertThat( context.<String> read( "$['components']['schemas']['FloatRange']['description']" ) )
            .isEqualTo( "This is a floating range constraint" );
      assertThat(
            context.<String> read( "$['components']['schemas']['FloatRange']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "FloatRange" );
      assertThat( context.<String> read( "$['components']['schemas']['FloatRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']['FloatRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']['FloatRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']['FloatRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']['FloatRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['doubleProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/DoubleRange" );
      assertThat( context.<String> read( "$['components']['schemas']['DoubleRange']['description']" ) )
            .isEqualTo( "This is a double range constraint" );
      assertThat(
            context.<String> read( "$['components']['schemas']['DoubleRange']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "DoubleRange" );
      assertThat( context.<String> read( "$['components']['schemas']['DoubleRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']['DoubleRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']['DoubleRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']['DoubleRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']['DoubleRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['decimalProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/DecimalRange" );
      assertThat( context.<String> read( "$['components']['schemas']['DecimalRange']['description']" ) )
            .isEqualTo( "This is a decimal range constraint" );
      assertThat(
            context.<String> read( "$['components']['schemas']['DecimalRange']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "DecimalRange" );
      assertThat( context.<String> read( "$['components']['schemas']['DecimalRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']['DecimalRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']['DecimalRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']['DecimalRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']['DecimalRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['integerProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/IntegerRange" );
      assertThat( context.<String> read( "$['components']['schemas']['IntegerRange']['description']" ) )
            .isEqualTo( "This is a integer range constraint" );
      assertThat(
            context.<String> read( "$['components']['schemas']['IntegerRange']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "IntegerRange" );
      assertThat( context.<String> read( "$['components']['schemas']['IntegerRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Integer> read( "$['components']['schemas']['IntegerRange']['minimum']" ) ).isEqualTo( 12 );
      assertThat( context.<Integer> read( "$['components']['schemas']['IntegerRange']['maximum']" ) ).isEqualTo( 23 );
      assertThat( context.<Boolean> read( "$['components']['schemas']['IntegerRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']['IntegerRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['intProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/IntRange" );
      assertThat(
            context.<String> read( "$['components']['schemas']['IntRange']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "IntRange" );
      assertThat( context.<String> read( "$['components']['schemas']['IntRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Integer> read( "$['components']['schemas']['IntRange']['minimum']" ) ).isEqualTo( 12 );
      assertThat( context.<Integer> read( "$['components']['schemas']['IntRange']['maximum']" ) ).isEqualTo( 23 );
      assertThat( context.<Boolean> read( "$['components']['schemas']['IntRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']['IntRange']['exclusiveMinimum']" ) ).isTrue();
   }

   @Test
   public void testCollectionMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_LIST ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );

      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestList" );

      assertThat( context.<String> read( "$['components']['schemas']['TestList']['description']" ) )
            .isEqualTo( "This is a test list." );
      assertThat( context.<String> read( "$['components']['schemas']['TestList']['type']" ) ).isEqualTo( "array" );
      assertThat( context.<String> read( "$['components']['schemas']['TestList']['items']['type']" ) ).isEqualTo( "string" );
   }

   @Test
   public void testSetMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_SET ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      showJson( schema );
      final DocumentContext context = JsonPath.parse( schema.toString() );

      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) ).isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) ).isEqualTo( "#/components/schemas/TestSet" );
      assertThat( context.<String> read( "$['components']['schemas']['TestSet']['type']" ) ).isEqualTo( "array" );
      assertThat(
            context.<String> read( "$['components']['schemas']['TestSet']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestSet" );
      assertThat( context.<String> read( "$['components']['schemas']['TestSet']['description']" ) ).isEqualTo( "This is a test set." );
      assertThat( context.<Boolean> read( "$['components']['schemas']['TestSet']['uniqueItems']" ) ).isTrue();
      assertThat( context.<String> read( "$['components']['schemas']['TestSet']['items']['type']" ) ).isEqualTo( "string" );
   }

   @Test
   public void testSortedSetSetMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_SORTED_SET ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );

      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestSortedSet" );
      assertThat( context.<String> read( "$['components']['schemas']['TestSortedSet']['type']" ) ).isEqualTo( "array" );
      assertThat(
            context.<String> read(
                  "$['components']['schemas']['TestSortedSet']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestSortedSet" );
      assertThat( context.<String> read( "$['components']['schemas']['TestSortedSet']['description']" ) )
            .isEqualTo( "This is a test sorted set." );
      assertThat( context.<Boolean> read( "$['components']['schemas']['TestSortedSet']['uniqueItems']" ) ).isTrue();
      assertThat( context.<String> read( "$['components']['schemas']['TestSortedSet']['items']['type']" ) ).isEqualTo( "string" );
   }

   @Test
   public void testLangStringMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      showJson( schema );
      final String multiLanguageText = SammNs.SAMMC.MultiLanguageText().getLocalName();

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['prop']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + multiLanguageText );
      assertThat( context.<String> read( "$['components']['schemas']['" + multiLanguageText + "']['type']" ) )
            .isEqualTo( "object" );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + multiLanguageText + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.MultiLanguageText().getURI() );
      assertThat( context.<String> read( "$['components']['schemas']['" + multiLanguageText + "']['description']" ) )
            .isEqualTo( "Describes a Property which contains plain text in multiple "
                  + "languages. This is intended exclusively for human readable strings, not for "
                  + "identifiers, measurement values, etc." );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + multiLanguageText + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.MultiLanguageText().getURI() );
      assertThat( context.<String> read( "$['components']['schemas']['" + multiLanguageText + "']['patternProperties']"
            + "['^.*$']['type']" ) ).isEqualTo( "string" );

      final ObjectNode payload = JsonNodeFactory.instance.objectNode();
      final ObjectNode propertyValue = JsonNodeFactory.instance.objectNode();
      propertyValue.put( "en", "foo" );
      propertyValue.put( "de", "bar" );
      payload.set( "prop", propertyValue );
      assertPayloadIsValid( schema, payload );
   }

   @Test
   public void testEitherMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_EITHER ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      showJson( schema );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestEither" );
      assertThat( context.<String> read( "$['components']['schemas']['TestEither']['description']" ) )
            .isEqualTo( "This is a test Either." );
      assertThat(
            context.<String> read( "$['components']['schemas']['TestEither']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestEither" );
      assertThat( context.<String> read( "$['components']['schemas']['TestEither']['properties']['left']['type']" ) )
            .isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['TestEither']['properties']['right']['type']" ) )
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

   @Test
   public void testEnumScalarMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENUMERATION ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestEnumeration" );
      assertThat( context.<String> read( "$['components']['schemas']['TestEnumeration']['description']" ) )
            .isEqualTo( "This is a test for enumeration." );
      assertThat( context.<String> read( "$['components']['schemas']['TestEnumeration']['type']" ) ).isEqualTo( "number" );
      assertThat(
            context.<String> read(
                  "$['components']['schemas']['TestEnumeration']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      assertThat( context.<List<Integer>> read( "$['components']['schemas']['TestEnumeration']['enum']" ) )
            .containsExactly( 1, 2, 3 );
   }

   @Test
   public void testEnumComplexMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENUM_HAVING_NESTED_ENTITIES ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['result']['$ref']" ) )
            .isEqualTo( "#/components/schemas/EvaluationResults" );

      assertThat( context.<String> read( "$['components']['schemas']['EvaluationResults']['description']" ) )
            .isEqualTo( "Possible values for the evaluation of a process" );
      assertThat( context.<String> read( "$['components']['schemas']['EvaluationResults']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read(
            "$['components']['schemas']['EvaluationResults']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "EvaluationResults" );
      assertThat( context.<String> read( "$['components']['schemas']['EvaluationResults']['oneOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/ResultGood" );
      assertThat( context.<String> read( "$['components']['schemas']['EvaluationResults']['oneOf'][1]['$ref']" ) )
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

   @Test
   public void testEnumComplexWithNotInPayloadMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_EXTENDED_ENUMS_WITH_NOT_IN_PAYLOAD_PROPERTY ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['result']['$ref']" ) )
            .isEqualTo( "#/components/schemas/EvaluationResults" );
      assertThat(
            context.<String> read(
                  "$['components']['schemas']['EvaluationResults']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "EvaluationResults" );

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

   @Test
   public void testEnumWithLangStringMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestEnumeration" );

      assertThat( context.<String> read( "$['components']['schemas']['TestEnumeration']['description']" ) )
            .isEqualTo( "This is a test for enumeration." );
      assertThat( context.<String> read(
            "$['components']['schemas']['TestEnumeration']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestEnumeration" );

      assertThat( context.<String> read( "$['components']['schemas']['entityInstance']['type']" ) )
            .isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']['entityInstance']['properties']['entityProperty']"
            + "['enum'][0]['en']" ) ).isEqualTo( "This is a test." );
   }

   @Test
   public void testRegularExpressionConstraintMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_REGULAR_EXPRESSION_CONSTRAINT ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['description']" ) )
            .isEqualTo( "This is a test property." );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/TestRegularExpressionConstraint" );
      assertThat(
            context.<String> read(
                  "$['components']['schemas']['TestRegularExpressionConstraint']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION
                        + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "TestRegularExpressionConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']['TestRegularExpressionConstraint']['description']" ) )
            .isEqualTo( "This is a test regular expression constraint." );
      assertThat( context.<String> read( "$['components']['schemas']['TestRegularExpressionConstraint']['type']" ) )
            .isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']['TestRegularExpressionConstraint']['pattern']" ) )
            .isEqualTo( "^[0-9]*$" );
   }

   @Test
   public void testComplexEntityCollectionEnum() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      showJson( schema );
      assertThat( context.<String> read( "$['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['properties']['myPropertyOne']['$ref']" ) )
            .isEqualTo( "#/components/schemas/MyEnumerationOne" );

      assertThat( context.<String> read( "$['components']['schemas']['MyEnumerationOne']['description']" ) )
            .isEqualTo( "This is my enumeration one" );
      assertThat( context.<String> read( "$['components']['schemas']['MyEnumerationOne']['type']" ) )
            .isEqualTo( "object" );
      assertThat( context.<String> read(
            "$['components']['schemas']['MyEnumerationOne']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "MyEnumerationOne" );
      assertThat( context.<String> read( "$['components']['schemas']['MyEnumerationOne']['oneOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/entityInstanceOne" );

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

   @Test
   public void testAspectWithAbstractSingleEntity() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      showJson( schema );

      final String text = SammNs.SAMMC.Text().getLocalName();

      assertThat( context.<String> read( "$['components']['schemas']['ExtendingTestEntity']['allOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']['ExtendingTestEntity']['properties']['entityProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + text );
      assertThat( context.<String> read(
            "$['components']['schemas']['ExtendingTestEntity']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "ExtendingTestEntity" );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + text + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.Text().getURI() );
      assertThat( context.<String> read( "$['components']['schemas']['AbstractTestEntity']['description']" ) )
            .isEqualTo( "This is an abstract test entity" );
      assertThat(
            context.<String> read( "$['components']['schemas']['AbstractTestEntity']['properties']['abstractTestProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestPropertyCharacteristic" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/EntityCharacteristic" );
   }

   @Test
   public void testAspectWithAbstractEntity() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      showJson( schema );

      final String text = SammNs.SAMMC.Text().getLocalName();

      assertThat( context.<String> read( "$['components']['schemas']['ExtendingTestEntity']['description']" ) )
            .isEqualTo( "This is a test entity" );
      assertThat( context.<String> read(
            "$['components']['schemas']['ExtendingTestEntity']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "ExtendingTestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']['ExtendingTestEntity']['allOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']['ExtendingTestEntity']['properties']['entityProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + text );
      assertThat( context.<String> read(
            "$['components']['schemas']['" + text + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( SammNs.SAMMC.Text().getURI() );
      assertThat( context.<String> read( "$['components']['schemas']['AbstractTestEntity']['description']" ) )
            .isEqualTo( "This is a abstract test entity" );
      assertThat(
            context.<String> read( "$['components']['schemas']['AbstractTestEntity']['properties']['abstractTestProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestPropertyCharacteristic" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/EntityCharacteristic" );
   }

   /**
    * Test to validate the json schema generated from the given aspect model containing an abstract property.
    */
   @Test
   public void testAspectWithAbstractProperty() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ABSTRACT_PROPERTY ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final String text = SammNs.SAMMC.Text().getLocalName();

      assertThat( schema.at( "/components/schemas/ExtendingTestEntity/description" ).asText() )
            .isEqualTo( "This is a test entity" );
      assertThat( schema.at( "/components/schemas/ExtendingTestEntity/properties/abstractTestProperty/description" ).asText() )
            .isEqualTo( "This is an abstract test property" );
      assertThat( schema.at( "/components/schemas/ExtendingTestEntity/properties/abstractTestProperty/$ref" ).asText() )
            .isEqualTo( "#/components/schemas/" + text );
   }

   @Test
   public void testAspectWithCollectionWithAbstractEntity() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY ).aspect();
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      showJson( schema );

      assertThat( context.<String> read( "$['components']['schemas']['ExtendingTestEntity']['allOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']['AbstractTestEntity']['description']" ) )
            .isEqualTo( "This is an abstract test entity" );
      assertThat( context.<String> read(
            "$['components']['schemas']['AbstractTestEntity']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "AbstractTestEntity" );
      assertThat( context.<String> read( "$['components']['schemas']['ExtendingTestEntity']['properties']['entityProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/Text" );
      assertThat( context.<String> read(
            "$['components']['schemas']['AbstractTestEntity']['properties']['abstractTestProperty']['description']" ) )
            .isEqualTo( "This is an abstract test property" );
      assertThat(
            context.<String> read( "$['components']['schemas']['AbstractTestEntity']['properties']['abstractTestProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestPropertyCharacteristic" );
      assertThat( context.<String> read( "$['components']['schemas']['EntityCollectionCharacteristic']['description']" ) )
            .isEqualTo( "This is an entity collection characteristic" );
      assertThat( context.<String> read(
            "$['components']['schemas']['EntityCollectionCharacteristic']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) )
            .isEqualTo( TestModel.TEST_NAMESPACE + "EntityCollectionCharacteristic" );
      assertThat( context.<String> read( "$['components']['schemas']['EntityCollectionCharacteristic']['items']['$ref']" ) )
            .isEqualTo( "#/components/schemas/AbstractTestEntity" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/EntityCollectionCharacteristic" );
   }
}
