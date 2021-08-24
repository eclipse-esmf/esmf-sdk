/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator.jsonschema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
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
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;

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
      return AspectModelLoader.fromVersionedModel( versionedModel ).get();
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
      return new AspectModelJsonSchemaGenerator().apply( aspect );
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
      System.out.println( "Payload:" );
      showJson( payload );
      System.out.println( "Schema:" );
      showJson( schema );
      assertThatCode( () -> {
         final ProcessingReport report = parseSchema( schema ).validate( payload );
         if ( !report.isSuccess() ) {
            System.out.println( report );
         }
         assertThat( report.isSuccess() ).isTrue();
      } ).doesNotThrowAnyException();
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
         "ASPECT_WITH_ENUMERATION_WITHOUT_SCALAR_VARIABLE" //Invalid Aspect Model
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

      assertThat( context.<String> read( "$['properties']['anyUriProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeAnyUri" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeAnyUri']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['base64BinaryProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/Base64BinaryValue" );
      assertThat( context.<String> read( "$['components']['schemas']['Base64BinaryValue']['type']" ) ).isEqualTo( "string" );

      final String booleanUrn = String.format( "urn_bamm_io.openmanufacturing_characteristic_%s_Boolean",
            KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['booleanProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + booleanUrn );
      assertThat( context.<String> read( "$['components']['schemas']['"+booleanUrn+"']['type']" ) )
            .isEqualTo( "boolean" );

      assertThat( context.<String> read( "$['properties']['byteProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeByte" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeByte']['type']" ) ).isEqualTo( "number" );

      final String unitReferenceUrn = String
            .format( "urn_bamm_io.openmanufacturing_characteristic_%s_UnitReference",
                  KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['curieProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + unitReferenceUrn );
      assertThat( context.<String> read( "$['components']['schemas']['"+unitReferenceUrn+"']['type']" ) )
            .isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['dateProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/DateValue" );
      assertThat( context.<String> read( "$['components']['schemas']['DateValue']['type']" ) ).isEqualTo( "string" );

      final String timestampUrn = String.format( "urn_bamm_io.openmanufacturing_characteristic_%s_Timestamp",
            KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['dateTimeProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + timestampUrn );
      assertThat( context.<String> read( "$['components']['schemas']['"+timestampUrn+"']['type']" ) )
            .isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['dateTimeStampProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeDateTimeStamp" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeDateTimeStamp']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['dayTimeDuration']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeDayTimeDuration" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeDayTimeDuration']['type']" ) )
            .isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['decimalProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeDecimal" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeDecimal']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['doubleProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeDouble" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeDouble']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['durationProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeDuration" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeDuration']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['floatProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeFloat" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeFloat']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['gMonthDayProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeGMonthDay" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeGMonthDay']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['gMonthProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeGMonth" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeGMonth']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['gYearMonthProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeGYearMonth" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeGYearMonth']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['gYearProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeGYear" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeGYear']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['hexBinaryProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/HexBinaryValue" );
      assertThat( context.<String> read( "$['components']['schemas']['HexBinaryValue']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['intProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeInt" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeInt']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['integerProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeInteger" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeInteger']['type']" ) ).isEqualTo( "number" );

      final String multiLanguageTextUrn = String
            .format( "urn_bamm_io.openmanufacturing_characteristic_%s_MultiLanguageText",
                  KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['langStringProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + multiLanguageTextUrn );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['"+multiLanguageTextUrn+"']['type']" ) ).isEqualTo( "object" );

      assertThat( context.<String> read( "$['properties']['longProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeLong" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeLong']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['negativeIntegerProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeNegativeInteger" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeNegativeInteger']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['nonNegativeIntegerProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeNonNegativeInteger" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeNonNegativeInteger']['type']" ) )
            .isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['nonPositiveInteger']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeNonPositiveInteger" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeNonPositiveInteger']['type']" ) )
            .isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['positiveIntegerProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomePositiveInteger" );
      assertThat( context.<String> read( "$['components']['schemas']['SomePositiveInteger']['type']" ) )
            .isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['shortProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeShort" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeShort']['type']" ) ).isEqualTo( "number" );

      final String textUrn = String.format( "urn_bamm_io.openmanufacturing_characteristic_%s_Text",
            KnownVersion.getLatest().toVersionString() );
      assertThat( context.<String> read( "$['properties']['stringProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + textUrn );
      assertThat( context.<String> read( "$['components']['schemas']['"+textUrn+"']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['timeProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeTime" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeTime']['type']" ) ).isEqualTo( "string" );

      assertThat( context.<String> read( "$['properties']['unsignedByteProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeUnsignedByte" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeUnsignedByte']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['unsignedIntProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeUnsignedInt" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeUnsignedInt']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['unsignedLongProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeLength" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeLength']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['unsignedShortProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeUnsignedShort" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeUnsignedShort']['type']" ) ).isEqualTo( "number" );

      assertThat( context.<String> read( "$['properties']['yearMonthDurationProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/SomeYearMonthDuration" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeYearMonthDuration']['type']" ) )
            .isEqualTo( "string" );

      assertThat( context.<String> read( "$['components']['schemas']['SomeDateTimeStamp']['format']" ) )
            .isEqualTo( "date-time" );
      assertThat( context.<String> read( "$['components']['schemas']['DateValue']['format']" ) ).isEqualTo( "date" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeTime']['format']" ) ).isEqualTo( "time" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeDuration']['format']" ) ).isEqualTo( "duration" );
      assertThat( context.<String> read( "$['components']['schemas']['SomeAnyUri']['format']" ) ).isEqualTo( "uri" );
      assertThat( context.<String> read( "$['components']['schemas']['Base64BinaryValue']['contentEncoding']" ) )
            .isEqualTo( "base64" );
      assertThat( context.<String> read( "$['components']['schemas']['HexBinaryValue']['pattern']" ) )
            .isEqualTo( "([0-9a-fA-F])([0-9a-fA-F])*" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOptionalPropertyMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      final String textUrn = String.format( "urn_bamm_io.openmanufacturing_characteristic_%s_Text",
                  KnownVersion.getLatest().toVersionString());

      assertThat( context.<String> read( "$['components']['schemas']['"+textUrn+"']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + textUrn );
      assertThat( context.<List<String>> read( "$['required']" ) ).isNull();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithRecursivePropertyWithOptional( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_testItemCharacteristic']['properties']['testProperty']"
            + "['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_testItemCharacteristic" );
      assertThat( context.<List<String>> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_testItemCharacteristic']['required']" ) ).isNull();
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
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionWithElementConstraint( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ELEMENT_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestCollection']['items']['type']" ) )
            .isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestCollection']['items']['minimum']" ) )
            .isCloseTo( 2.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestCollection']['items']['maximum']" ) )
            .isCloseTo( 10.5d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestCollection']['items']['exclusiveMaximum']" ) )
            .isEqualTo( false );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestCollection']['items']['exclusiveMinimum']" ) )
            .isEqualTo( false );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEntityMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      final String textUrn = String.format( "urn_bamm_io.openmanufacturing_characteristic_%s_Text",
            KnownVersion.getLatest().toVersionString() );

      assertThat( context.<String> read( "$['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_EntityCharacteristic" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_EntityCharacteristic']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_EntityCharacteristic']['properties']"
            + "['entityProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + textUrn );
      assertThat( context.<List<String>> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_EntityCharacteristic']['required']" ) )
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
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraint']['type']" ) ).isEqualTo( "string" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraint']['maxLength']" ) ).isEqualTo( 10 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraint']['minLength']" ) ).isEqualTo( 5 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLengthConstraintForListMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST_WITH_LENGTH_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read(
            "$['properties']['testPropertyCollectionLengthConstraint']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraintWithCollection" );
      assertThat( context.<String> read( "$['components']['schemas']['urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraintWithCollection']['type']" ) ).isEqualTo( "array" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraintWithCollection']['maxItems']" ) )
            .isEqualTo( 10 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraintWithCollection']['minItems']" ) )
            .isEqualTo( 1 );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestLengthConstraintWithCollection']['items']['type']" ) )
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
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestRangeConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestRangeConstraint']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestRangeConstraint']['minimum']" ) )
            .isCloseTo( 2.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestRangeConstraint']['maximum']" ) )
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
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestRangeConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestRangeConstraint']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestRangeConstraint']['minimum']" ) ).isEqualTo( 5 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestRangeConstraint']['maximum']" ) )
            .isEqualTo( Short.MAX_VALUE );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintWithBoundsMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_EXCLUSIVE_RANGE_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['floatProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_FloatRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_FloatRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_FloatRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_FloatRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_FloatRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_FloatRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['doubleProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_DoubleRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DoubleRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DoubleRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DoubleRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DoubleRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DoubleRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['decimalProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_DecimalRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DecimalRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DecimalRange']['minimum']" ) )
            .isCloseTo( 12.3d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Double> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DecimalRange']['maximum']" ) )
            .isCloseTo( 23.45d, Percentage.withPercentage( 1.0d ) );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DecimalRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_DecimalRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['integerProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_IntegerRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntegerRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntegerRange']['minimum']" ) ).isEqualTo( 12 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntegerRange']['maximum']" ) ).isEqualTo( 23 );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntegerRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntegerRange']['exclusiveMinimum']" ) ).isTrue();

      assertThat( context.<String> read( "$['properties']['intProp']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_IntRange" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntRange']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntRange']['minimum']" ) ).isEqualTo( 12 );
      assertThat( context.<Integer> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntRange']['maximum']" ) ).isEqualTo( 23 );
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntRange']['exclusiveMaximum']" ) ).isTrue();
      assertThat( context.<Boolean> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_IntRange']['exclusiveMinimum']" ) ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestList" );

      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestList']['type']" ) ).isEqualTo( "array" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestList']['items']['type']" ) ).isEqualTo( "string" );
   }

   @Test
   public void testSetMapping() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SET, KnownVersion.getLatest() );
      final JsonNode schema = buildJsonSchema( aspect );
      final DocumentContext context = JsonPath.parse( schema.toString() );

      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestSet" );
      assertThat( context.<String> read("$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestSet']['type']") ).isEqualTo( "array" );
      assertThat( context.<Boolean> read("$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestSet']['uniqueItems']") ).isTrue();
      assertThat( context.<String> read("$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestSet']['items']['type']") ).isEqualTo( "string" );
   }

   @Test
   public void testSortedSetSetMapping() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SORTED_SET, KnownVersion.getLatest() );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );

      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestSortedSet" );
      assertThat( context.<String> read("$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestSortedSet']['type']") ).isEqualTo( "array" );
      assertThat( context.<Boolean> read("$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestSortedSet']['uniqueItems']") ).isTrue();
      assertThat( context.<String> read("$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestSortedSet']['items']['type']") ).isEqualTo( "string" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLangStringMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );
      final String multiLanguageTextUrn = String
            .format( "urn_bamm_io.openmanufacturing_characteristic_%s_MultiLanguageText",
                  KnownVersion.getLatest().toVersionString());

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['prop']['$ref']" ) )
            .isEqualTo( "#/components/schemas/" + multiLanguageTextUrn );
      assertThat( context.<String> read( "$['components']['schemas']['"+multiLanguageTextUrn+"']['type']" ) )
            .isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']['"+multiLanguageTextUrn+"']['patternProperties']"
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
      assertThat( context.<String> read("$['properties']['testProperty']['$ref']") )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestEither" );
      assertThat( context.<String> read("$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestEither']['properties']['left']['type']") )
            .isEqualTo( "string" );
      assertThat( context.<String> read("$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestEither']['properties']['right']['type']") )
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
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestEnumeration" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestEnumeration']['type']" ) ).isEqualTo( "number" );
      assertThat( context.<List<Integer>> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestEnumeration']['enum']" ) )
            .containsExactly( 1, 2, 3 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumComplexMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENUM_HAVING_NESTED_ENTITIES, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['result']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_EvaluationResults" );

      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_EvaluationResults']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']"
            + "['schemas']['urn_bamm_io.openmanufacturing.test_1.0.0_EvaluationResults']['oneOf'][0]['$ref']" ) )
            .isEqualTo( "#/components/schemas/ResultGood" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_EvaluationResults']['oneOf'][1]['$ref']" ) )
            .isEqualTo( "#/components/schemas/ResultBad" );


      assertThat( context.<String> read( "$['components']['schemas']['ResultGood']['properties']['details']"
            + "['properties']['description']['enum'][0]" ) ).isEqualTo( "Result succeeded" );
      assertThat( context.<String> read( "$['components']['schemas']['ResultGood']['properties']['details']"
            + "['properties']['message']['enum'][0]" ) ).isEqualTo( "Evaluation succeeded." );
      assertThat( context.<Double> read( "$['components']['schemas']['ResultGood']['properties']['details']"
            + "['properties']['numericCode']['enum'][0]" ) ).isCloseTo( 10.0d, Percentage.withPercentage( 1.0d ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumComplexWithNotInPayloadMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_EXTENDED_ENUMS_WITH_NOT_IN_PAYLOAD_PROPERTY,
            metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['result']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_EvaluationResults" );


      assertThat( context.<Integer> read( "$['components']['schemas']['ResultNoStatus']['properties']['average']"
            + "['enum'][0]" ) ).isEqualTo( 3 );
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
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestEnumeration" );

      assertThat( context.<String> read( "$['components']['schemas']['entityInstance']['type']" ) ).isEqualTo( "object" );

      assertThat( context.<String> read( "$['components']['schemas']['entityInstance']['properties']['entityProperty']"
            + "['enum'][0]['de']" ) ).isEqualTo( "Dies ist ein Test." );

      assertThat( context.<String> read( "$['components']['schemas']['entityInstance']['properties']['entityProperty']"
            + "['enum'][0]['en']" ) ).isEqualTo( "This is a test." );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRegularExpressionConstraintMapping( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_REGULAR_EXPRESSION_CONSTRAINT, metaModelVersion );
      final JsonNode schema = buildJsonSchema( aspect );

      final DocumentContext context = JsonPath.using( config ).parse( schema.toString() );
      assertThat( context.<String> read( "$['properties']['testProperty']['$ref']" ) )
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_TestRegularExpressionConstraint" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestRegularExpressionConstraint']['type']" ) )
            .isEqualTo( "string" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_TestRegularExpressionConstraint']['pattern']" ) )
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
            .isEqualTo( "#/components/schemas/urn_bamm_io.openmanufacturing.test_1.0.0_MyEnumerationOne" );

      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_MyEnumerationOne']['type']" ) ).isEqualTo( "object" );
      assertThat( context.<String> read( "$['components']['schemas']"
            + "['urn_bamm_io.openmanufacturing.test_1.0.0_MyEnumerationOne']['oneOf'][0]['$ref']" ) )
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
}
