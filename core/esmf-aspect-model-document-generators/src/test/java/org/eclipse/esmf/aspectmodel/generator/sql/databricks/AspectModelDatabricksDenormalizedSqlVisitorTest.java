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

package org.eclipse.esmf.aspectmodel.generator.sql.databricks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.api.Test;

@SuppressWarnings( "checkstyle:LineLength" )
class AspectModelDatabricksDenormalizedSqlVisitorTest extends DatabricksTestBase {
   @Test
   void testAspectWithAbstractEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_abstract_entity (
              test_property__entity_property STRING NOT NULL COMMENT 'This is a property for the test entity.'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAbstractEntity');
            """ );
   }

   @Test
   void testAspectWithAllBaseAttributes() {
      assertThat( sql( TestAspect.ASPECT_WITH_ALL_BASE_ATTRIBUTES ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_all_base_attributes (
              test_boolean BOOLEAN NOT NULL
            )
            COMMENT 'Test Description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAllBaseAttributes');
            """ );
   }

   @Test
   void testAspectWithCollection() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTION ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_collection (
              test_property ARRAY<STRING> NOT NULL COMMENT 'This is a test property.'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollection');
            """ );
   }

   @Test
   void testAspectWithCollectionsWithElementCharacteristicAndSimpleDataType() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTIONS_WITH_ELEMENT_CHARACTERISTIC_AND_SIMPLE_DATA_TYPE ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_collections_with_element_characteristic_and_simple_data_type (
              test_property ARRAY<STRING> NOT NULL,
              test_property_two ARRAY<STRING> NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionsWithElementCharacteristicAndSimpleDataType');
            """ );
   }

   @Test
   void testAspectWithCollectionAndElementCharacteristic() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTION_AND_ELEMENT_CHARACTERISTIC ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_collection_and_element_characteristic (
              test_property STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionAndElementCharacteristic');
            """ );
   }

   @Test
   void testAspectWithCollectionOfSimpleType() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_collection_of_simple_type (
              test_list ARRAY<INT> NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionOfSimpleType');
            """ );
   }

   @Test
   void testAspectWithComplexCollectionEnum() {
      assertThat( sql( TestAspect.ASPECT_WITH_COMPLEX_COLLECTION_ENUM ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_complex_collection_enum (
              my_property_one__entity_property_one ARRAY<STRING> NOT NULL,
              my_property_two__entity_property_two ARRAY<STRING> NOT NULL,
              my_property_three__entity_property_three ARRAY<STRING> NOT NULL,
              my_property_four__entity_property_four ARRAY<STRING> NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexCollectionEnum');
            """ );
   }

   @Test
   void testAspectWithComplexEntityCollectionEnum() {
      assertThat( sql( TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_complex_entity_collection_enum (
              my_property_one__entity_property_one__entity_property_two_id BIGINT NOT NULL,
              my_property_one__entity_property_one__entity_property_two STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexEntityCollectionEnum');
            """ );
   }

   @Test
   void testAspectWithComplexEnumInclOptional() {
      assertThat( sql( TestAspect.ASPECT_WITH_COMPLEX_ENUM_INCL_OPTIONAL ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_complex_enum_incl_optional (
              result__numeric_code SMALLINT COMMENT 'Numeric code for the evaluation result',
              result__description STRING COMMENT 'Human-readable description of the process result code',
              simple_result STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexEnumInclOptional');
            """ );
   }

   @Test
   void testAspectWithCurie() {
      assertThat( sql( TestAspect.ASPECT_WITH_CURIE ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_curie (
              test_curie STRING NOT NULL,
              test_curie_without_example_value STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCurie');
            """ );
   }

   @Test
   void testAspectWithEither() {
      assertThat( sql( TestAspect.ASPECT_WITH_EITHER ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_either (
              test_property__left STRING COMMENT 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.'
              test_property__right BOOLEAN COMMENT 'Represents a boolean value (i.e. a "flag").'
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEither');
            """ );
   }

   @Test
   void testAspectWithEitherWithComplexTypes() {
      assertThat( sql( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_either_with_complex_types (
              test_property__left__result STRING COMMENT 'Left Type Characteristic'
              test_property__right__error STRING COMMENT 'Right Type Characteristic'
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithComplexTypes');
            """ );
   }

   @Test
   void testAspectWithEnglishAndGermanDescription() {
      final DatabricksSqlGenerationConfig config = DatabricksSqlGenerationConfigBuilder.builder()
            .includeTableComment( true )
            .includeColumnComments( true )
            .commentLanguage( Locale.GERMAN ).build();
      assertThat( sql( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION, config ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_english_and_german_description (
              test_string STRING NOT NULL COMMENT 'Es ist ein Test-String'
            )
            COMMENT 'Aspekt mit mehrsprachigen Beschreibungen'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnglishAndGermanDescription');
            """ );
   }

   @Test
   void testAspectWithEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_ENTITY ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_entity (
              test_property__entity_property STRING NOT NULL COMMENT 'This is a property for the test entity.'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntity');
            """ );
   }

   @Test
   void testAspectWithEntityInstanceWithScalarListProperty() {
      assertThat( sql( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_SCALAR_LIST_PROPERTY ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_entity_instance_with_scalar_list_property (
              test_property__code SMALLINT NOT NULL,
              test_property__test_list ARRAY<DECIMAL(10)> NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityInstanceWithScalarListProperty');
            """ );
   }

   @Test
   void testAspectWithEntityList() {
      assertThat( sql( TestAspect.ASPECT_WITH_ENTITY_LIST ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_entity_list (
              test_list__test_string STRING NOT NULL,
              test_list__test_int INT NOT NULL,
              test_list__test_float FLOAT NOT NULL,
              test_list__test_local_date_time TIMESTAMP NOT NULL,
              test_list__random_value STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityList');
            """ );
   }

   @Test
   void testAspectWithEntityWithNestedEntityListProperty() {
      assertThat( sql( TestAspect.ASPECT_WITH_ENTITY_WITH_NESTED_ENTITY_LIST_PROPERTY ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_entity_with_nested_entity_list_property (
              test_property__code SMALLINT NOT NULL,
              test_property__test_list__nested_entity_property_id BIGINT NOT NULL,
              test_property__test_list__nested_entity_property STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityWithNestedEntityListProperty');
            """ );
   }

   @Test
   void testAspectWithExtendedEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_EXTENDED_ENTITY ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_extended_entity (
              parent_string STRING NOT NULL,
              parent_of_parent_string STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExtendedEntity');
            """ );
   }

   @Test
   void testAspectWithList() {
      assertThat( sql( TestAspect.ASPECT_WITH_LIST ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_list (
              test_property ARRAY<STRING> NOT NULL COMMENT 'This is a test property.'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithList');
            """ );
   }

   @Test
   void testAspectWithMultipleEntities() {
      assertThat( sql( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_multiple_entities (
              test_entity_one__test_string STRING NOT NULL,
              test_entity_one__test_int INT NOT NULL,
              test_entity_one__test_float FLOAT NOT NULL,
              test_entity_one__test_local_date_time TIMESTAMP NOT NULL,
              test_entity_one__random_value STRING NOT NULL,
              test_entity_two__test_string STRING NOT NULL,
              test_entity_two__test_int INT NOT NULL,
              test_entity_two__test_float FLOAT NOT NULL,
              test_entity_two__test_local_date_time TIMESTAMP NOT NULL,
              test_entity_two__random_value STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntities');
            """ );
   }

   @Test
   void testAspectWithMultipleEntitiesAndEither() {
      assertThat( sql( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_AND_EITHER ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_multiple_entities_and_either (
              test_entity_one__test_string STRING NOT NULL,
              test_entity_one__test_int INT NOT NULL,
              test_entity_one__test_float FLOAT NOT NULL,
              test_entity_one__test_local_date_time TIMESTAMP NOT NULL,
              test_entity_one__random_value STRING NOT NULL,
              test_entity_two__test_string STRING NOT NULL,
              test_entity_two__test_int INT NOT NULL,
              test_entity_two__test_float FLOAT NOT NULL,
              test_entity_two__test_local_date_time TIMESTAMP NOT NULL,
              test_entity_two__random_value STRING NOT NULL,
              test_either_property__left__test_string STRING COMMENT 'Left type Characteristic',
              test_either_property__left__test_int INT COMMENT 'Left type Characteristic',
              test_either_property__left__test_float FLOAT COMMENT 'Left type Characteristic',
              test_either_property__left__test_local_date_time TIMESTAMP COMMENT 'Left type Characteristic',
              test_either_property__left__random_value STRING COMMENT 'Left type Characteristic'
              test_either_property__right__test_string STRING COMMENT 'Right type Characteristic',
              test_either_property__right__test_int INT COMMENT 'Right type Characteristic',
              test_either_property__right__test_float FLOAT COMMENT 'Right type Characteristic',
              test_either_property__right__test_local_date_time TIMESTAMP COMMENT 'Right type Characteristic',
              test_either_property__right__random_value STRING COMMENT 'Right type Characteristic'
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesAndEither');
            """ );
   }

   @Test
   void testAspectWithMultipleEntitiesOnMultipleLevels() {
      assertThat( sql( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_ON_MULTIPLE_LEVELS ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_multiple_entities_on_multiple_levels (
              test_entity_one__test_local_date_time TIMESTAMP NOT NULL,
              test_entity_one__random_value STRING NOT NULL,
              test_entity_one__test_third_entity__test_string STRING NOT NULL,
              test_entity_one__test_third_entity__test_float FLOAT NOT NULL,
              test_entity_two__test_local_date_time TIMESTAMP NOT NULL,
              test_entity_two__random_value STRING NOT NULL,
              test_entity_two__test_third_entity__test_string STRING NOT NULL,
              test_entity_two__test_third_entity__test_float FLOAT NOT NULL,
              test_string STRING NOT NULL,
              test_second_entity__test_int INT NOT NULL,
              test_second_entity__test_float FLOAT NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesOnMultipleLevels');
            """ );
   }

   @Test
   void testAspectWithMultipleEntityCollections() {
      assertThat( sql( TestAspect.ASPECT_WITH_MULTIPLE_ENTITY_COLLECTIONS ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_multiple_entity_collections (
              test_list_one__test_string STRING NOT NULL,
              test_list_one__test_int INT NOT NULL,
              test_list_one__test_float FLOAT NOT NULL,
              test_list_one__test_local_date_time TIMESTAMP NOT NULL,
              test_list_one__random_value STRING NOT NULL,
              test_list_two__test_string STRING NOT NULL,
              test_list_two__test_int INT NOT NULL,
              test_list_two__test_float FLOAT NOT NULL,
              test_list_two__test_local_date_time TIMESTAMP NOT NULL,
              test_list_two__random_value STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntityCollections');
            """ );
   }

   @Test
   void testAspectWithNestedEntityList() {
      assertThat( sql( TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_nested_entity_list (
              test_list__test_string STRING NOT NULL,
              test_list__test_int INT NOT NULL,
              test_list__test_float FLOAT NOT NULL,
              test_list__test_second_list__test_local_date_time_id BIGINT NOT NULL,
              test_list__test_second_list__test_local_date_time TIMESTAMP NOT NULL,
              test_list__test_second_list__random_value_id BIGINT NOT NULL,
              test_list__test_second_list__random_value STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityList');
            """ );
   }

   @Test
   void testAspectWithNestedEntityEnumerationWithNotInPayload() {
      assertThat( sql( TestAspect.ASPECT_WITH_NESTED_ENTITY_ENUMERATION_WITH_NOT_IN_PAYLOAD ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_nested_entity_enumeration_with_not_in_payload (
              test_property__entity_property STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityEnumerationWithNotInPayload');
            """ );
   }

   @Test
   void testAspectWithOperation() {
      assertThat( sql( TestAspect.ASPECT_WITH_OPERATION ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_operation (
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOperation');
            """ );
   }

   @Test
   void testAspectWithOptionalProperties() {
      assertThat( sql( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_optional_properties (
              number_property DECIMAL(10),
              timestamp_property TIMESTAMP NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalProperties');
            """ );
   }

   @Test
   void testAspectWithOptionalPropertiesWithEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES_WITH_ENTITY ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_optional_properties_with_entity (
              test_string STRING NOT NULL,
              test_optional_string STRING,
              test_optional_entity__code_property INT,
              test_optional_entity__test_second_string STRING,
              test_optional_entity__test_int_list ARRAY<INT>
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertiesWithEntity');
            """ );
   }

   @Test
   void testAspectWithPropertyWithPayloadName() {
      assertThat( sql( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_property_with_payload_name (
              test STRING NOT NULL COMMENT 'This is a test property.'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithPayloadName');
            """ );
   }

   @Test
   void testAspectWithRecursivePropertyWithOptional() {
      assertThat( sql( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL_AND_ENTITY_PROPERTY ) )
            .contains( "test_property__test_property__test_property__test_property" )
            .contains( "test_property__test_property2" );
   }

   @Test
   void testAspectWithScriptTags() {
      assertThat( sql( TestAspect.ASPECT_WITH_SCRIPT_TAGS ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_script_tags (
              test_entity__test_string STRING NOT NULL COMMENT 'Test description with script: <script>alert(\\'Should not be alerted\\');</script>',
              test_entity__test_int INT NOT NULL COMMENT 'Test description with script: <script>alert(\\'Should not be alerted\\');</script>',
              test_entity__test_float FLOAT NOT NULL COMMENT 'Test description with script: <script>alert(\\'Should not be alerted\\');</script>',
              test_entity__test_local_date_time TIMESTAMP NOT NULL,
              test_entity__random_value STRING NOT NULL COMMENT 'Test description with script: <script>alert(\\'Should not be alerted\\');</script>'
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithScriptTags');
            """ );
   }

   @Test
   void testAspectWithSet() {
      assertThat( sql( TestAspect.ASPECT_WITH_SET ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_set (
              test_property ARRAY<STRING> NOT NULL COMMENT 'This is a test property.'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSet');
            """ );
   }

   @Test
   void testAspectWithSimpleTypes() {
      assertThat( sql( TestAspect.ASPECT_WITH_SIMPLE_TYPES ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_simple_types (
              any_uri_property STRING NOT NULL,
              base64_binary_property BINARY NOT NULL,
              boolean_property BOOLEAN NOT NULL,
              byte_property TINYINT NOT NULL,
              curie_property STRING NOT NULL,
              date_property STRING NOT NULL,
              date_time_property TIMESTAMP NOT NULL,
              date_time_stamp_property TIMESTAMP NOT NULL,
              day_time_duration STRING NOT NULL,
              decimal_property DECIMAL(10,0) NOT NULL,
              double_property DOUBLE NOT NULL,
              duration_property STRING NOT NULL,
              float_property FLOAT NOT NULL,
              g_day_property STRING NOT NULL,
              g_month_day_property STRING NOT NULL,
              g_month_property STRING NOT NULL,
              g_year_month_property STRING NOT NULL,
              g_year_property STRING NOT NULL,
              hex_binary_property BINARY NOT NULL,
              int_property INT NOT NULL,
              integer_property DECIMAL(10) NOT NULL,
              lang_string_property STRING NOT NULL,
              long_property BIGINT NOT NULL,
              negative_integer_property DECIMAL(10) NOT NULL,
              non_negative_integer_property DECIMAL(10) NOT NULL,
              non_positive_integer DECIMAL(10) NOT NULL,
              positive_integer_property DECIMAL(10) NOT NULL,
              short_property SMALLINT NOT NULL,
              string_property STRING NOT NULL,
              time_property STRING NOT NULL,
              unsigned_byte_property SMALLINT NOT NULL,
              unsigned_int_property BIGINT NOT NULL,
              unsigned_long_property DECIMAL(10) NOT NULL,
              unsigned_short_property INT NOT NULL,
              year_month_duration_property STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimpleTypes');
            """ );
   }

   @Test
   void testAspectWithSortedSet() {
      assertThat( sql( TestAspect.ASPECT_WITH_SORTED_SET ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_sorted_set (
              test_property ARRAY<STRING> NOT NULL COMMENT 'This is a test property.'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSortedSet');
            """ );
   }

   @Test
   void testAspectWithTimeSeries() {
      assertThat( sql( TestAspect.ASPECT_WITH_TIME_SERIES ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_time_series (
              value STRING NOT NULL COMMENT 'The value that was recorded and is part of a time series.',
              timestamp TIMESTAMP NOT NULL COMMENT 'The specific point in time when the corresponding value was recorded.'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTimeSeries');
            """ );
   }

   @Test
   void testAspectWithComplexSet() {
      assertThat( sql( TestAspect.ASPECT_WITH_COMPLEX_SET ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_complex_set (
              product_id STRING NOT NULL
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexSet');
            """ );
   }

   @Test
   void testAspectWithCustomColumn() {
      final DatabricksSqlGenerationConfig config = DatabricksSqlGenerationConfigBuilder.builder()
            .includeTableComment( true )
            .includeColumnComments( true )
            .customColumns( List.of(
                  DatabricksColumnDefinitionBuilder.builder()
                        .name( "custom" )
                        .type( new DatabricksType.DatabricksArray( DatabricksType.STRING ) )
                        .nullable( false )
                        .comment( Optional.of( "Custom column" ) )
                        .build()
            ) ).build();
      assertThat( sql( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME, config ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_property_with_payload_name (
              test STRING NOT NULL COMMENT 'This is a test property.',
              custom ARRAY<STRING> NOT NULL COMMENT 'Custom column'
            )
            COMMENT 'This is a test description'
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithPayloadName');
            """ );
   }

   @Test
   void testAspectWithUpwardTransitionInNestedEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_UPWARD_TRANSITION_IN_NESTED_ENTITY ) ).isEqualTo( """
            CREATE TABLE IF NOT EXISTS aspect_with_upward_transition_in_nested_entity (
              first_level_property__second_level_property1__third_level_property_id BIGINT NOT NULL,
              first_level_property__second_level_property1__third_level_property STRING NOT NULL,
              first_level_property__second_level_property2 STRING NOT NULL
            )
            TBLPROPERTIES ('x-samm-aspect-model-urn'='urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUpwardTransitionInNestedEntity');
            """ );
   }
}
