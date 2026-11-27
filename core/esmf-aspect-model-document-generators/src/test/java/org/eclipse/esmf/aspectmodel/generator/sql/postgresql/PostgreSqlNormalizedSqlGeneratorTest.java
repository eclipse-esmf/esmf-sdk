/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.sql.postgresql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.sql.NormalizedSqlGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings( "checkstyle:LineLength" )
class PostgreSqlNormalizedSqlGeneratorTest extends PostgreSqlTestBase {
   @Override
   protected String applyGenerator( final Aspect aspect, final PostgreSqlGenerationConfig config ) {
      return new NormalizedSqlGenerator<>( config, new PostgreSqlDialect( config ) ).apply( aspect ).getContent();
   }

   @Test
   void testAspect() {
      assertThat( sql( TestAspect.ASPECT ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#Aspect
         """ );
   }

   @Test
   void testAspectWithAbstractEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS extending_test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           entity_property TEXT NOT NULL,
           abstract_test_property NUMERIC(10) NOT NULL
         );
         COMMENT ON TABLE extending_test_entity IS 'This is a test entity';
         COMMENT ON COLUMN extending_test_entity.entity_property IS 'This is a property for the test entity.';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#ExtendingTestEntity

         CREATE TABLE IF NOT EXISTS extending_test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_abstract_entity (_id),
           _id_target BIGINT NOT NULL REFERENCES extending_test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_abstract_entity (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect_with_abstract_entity IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAbstractEntity
         """ );
   }

   @Test
   void testAspectWithAllBaseAttributes() {
      assertThat( sql( TestAspect.ASPECT_WITH_ALL_BASE_ATTRIBUTES ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_all_base_attributes (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_boolean BOOLEAN NOT NULL
         );
         COMMENT ON TABLE aspect_with_all_base_attributes IS 'Test Description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAllBaseAttributes
         """ );
   }

   @Test
   void testAspectWithCollection() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTION ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_collection__test_property (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_collection (_id),
           value TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_collection (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect_with_collection IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollection
         """ );
   }

   @Test
   void testAspectWithCollectionsWithElementCharacteristicAndSimpleDataType() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTIONS_WITH_ELEMENT_CHARACTERISTIC_AND_SIMPLE_DATA_TYPE ) ).isEqualTo(
            """
               CREATE TABLE IF NOT EXISTS aspect_with_collections_with_element_characteristic_and_simple_data_type__test_property (
                 _id_source BIGINT NOT NULL REFERENCES aspect_with_collections_with_element_characteristic_and_simple_data_type (_id),
                 value TEXT NOT NULL
               );

               CREATE TABLE IF NOT EXISTS aspect_with_collections_with_element_characteristic_and_simple_data_type__test_property_two (
                 _id_source BIGINT NOT NULL REFERENCES aspect_with_collections_with_element_characteristic_and_simple_data_type (_id),
                 value TEXT NOT NULL
               );

               CREATE TABLE IF NOT EXISTS aspect_with_collections_with_element_characteristic_and_simple_data_type (
                 _id BIGINT NOT NULL PRIMARY KEY
               );
               -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionsWithElementCharacteristicAndSimpleDataType
               """ );
   }

   @Test
   void testAspectWithCollectionAndElementCharacteristic() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTION_AND_ELEMENT_CHARACTERISTIC ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_property TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_collection_and_element_characteristic (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_collection_and_element_characteristic (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionAndElementCharacteristic
         """ );
   }

   @Test
   void testAspectWithCollectionOfSimpleType() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE ) ).isEqualTo(
            """
               CREATE TABLE IF NOT EXISTS aspect_with_collection_of_simple_type__test_list (
                 _id_source BIGINT NOT NULL REFERENCES aspect_with_collection_of_simple_type (_id),
                 value INTEGER NOT NULL
               );

               CREATE TABLE IF NOT EXISTS aspect_with_collection_of_simple_type (
                 _id BIGINT NOT NULL PRIMARY KEY
               );
               -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionOfSimpleType
               """ );
   }

   @Test
   void testAspectWithComplexCollectionEnum() {
      assertThat( sql( TestAspect.ASPECT_WITH_COMPLEX_COLLECTION_ENUM ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS my_entity_one__entity_property_one (
           _id_source BIGINT NOT NULL REFERENCES my_entity_one (_id),
           value TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS my_entity_one (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#MyEntityOne

         CREATE TABLE IF NOT EXISTS my_entity_one___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_complex_collection_enum (_id),
           _id_target BIGINT NOT NULL REFERENCES my_entity_one (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS my_entity_two__entity_property_two (
           _id_source BIGINT NOT NULL REFERENCES my_entity_two (_id),
           value TEXT NOT NULL CONSTRAINT u_value UNIQUE
         );

         CREATE TABLE IF NOT EXISTS my_entity_two (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#MyEntityTwo

         CREATE TABLE IF NOT EXISTS my_entity_two___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_complex_collection_enum (_id),
           _id_target BIGINT NOT NULL REFERENCES my_entity_two (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS my_entity_three__entity_property_three (
           _id_source BIGINT NOT NULL REFERENCES my_entity_three (_id),
           value TEXT NOT NULL CONSTRAINT u_value UNIQUE
         );

         CREATE TABLE IF NOT EXISTS my_entity_three (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#MyEntityThree

         CREATE TABLE IF NOT EXISTS my_entity_three___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_complex_collection_enum (_id),
           _id_target BIGINT NOT NULL REFERENCES my_entity_three (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS my_entity_four__entity_property_four (
           _id_source BIGINT NOT NULL REFERENCES my_entity_four (_id),
           value TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS my_entity_four (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#MyEntityFour

         CREATE TABLE IF NOT EXISTS my_entity_four___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_complex_collection_enum (_id),
           _id_target BIGINT NOT NULL REFERENCES my_entity_four (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_complex_collection_enum (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexCollectionEnum
         """ );
   }

   @Test
   void testAspectWithComplexEntityCollectionEnum() {
      assertThat( sql( TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS my_entity_two (
           _id BIGINT NOT NULL PRIMARY KEY,
           entity_property_two TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#MyEntityTwo

         CREATE TABLE IF NOT EXISTS my_entity_two___references (
           _id_source BIGINT NOT NULL REFERENCES my_entity_one (_id),
           _id_target BIGINT NOT NULL REFERENCES my_entity_two (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS my_entity_one (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#MyEntityOne

         CREATE TABLE IF NOT EXISTS my_entity_one___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_complex_entity_collection_enum (_id),
           _id_target BIGINT NOT NULL REFERENCES my_entity_one (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_complex_entity_collection_enum (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexEntityCollectionEnum
         """ );
   }

   @Test
   void testAspectWithComplexEnumInclOptional() {
      assertThat( sql( TestAspect.ASPECT_WITH_COMPLEX_ENUM_INCL_OPTIONAL ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS evaluation_result (
           _id BIGINT NOT NULL PRIMARY KEY,
           numeric_code SMALLINT,
           description TEXT
         );
         COMMENT ON TABLE evaluation_result IS 'Possible values for the evaluation of a process';
         COMMENT ON COLUMN evaluation_result.numeric_code IS 'Numeric code for the evaluation result';
         COMMENT ON COLUMN evaluation_result.description IS 'Human-readable description of the process result code';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#EvaluationResult

         CREATE TABLE IF NOT EXISTS evaluation_result___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_complex_enum_incl_optional (_id),
           _id_target BIGINT NOT NULL REFERENCES evaluation_result (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_complex_enum_incl_optional (
           _id BIGINT NOT NULL PRIMARY KEY,
           simple_result TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexEnumInclOptional
         """ );
   }

   @Test
   void testAspectWithCurie() {
      assertThat( sql( TestAspect.ASPECT_WITH_CURIE ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_curie (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_curie TEXT NOT NULL,
           test_curie_without_example_value TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCurie
         """ );
   }

   @Test
   void testAspectWithEither() {
      assertThat( sql( TestAspect.ASPECT_WITH_EITHER ) ).isEqualTo(
            """
               CREATE TABLE IF NOT EXISTS aspect_with_either (
                 _id BIGINT NOT NULL PRIMARY KEY,
                 test_property__left TEXT,
                 test_property__right BOOLEAN
               );
               COMMENT ON COLUMN aspect_with_either.test_property__left IS 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.';
               COMMENT ON COLUMN aspect_with_either.test_property__right IS 'Represents a boolean value (i.e. a "flag").';
               -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEither
               """ );
   }

   @Test
   void testAspectWithEitherWithComplexTypes() {
      assertThat( sql( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS left_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           result TEXT NOT NULL
         );
         COMMENT ON COLUMN left_entity.result IS 'Left Type Characteristic';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#LeftEntity

         CREATE TABLE IF NOT EXISTS left_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_either_with_complex_types (_id),
           _id_target BIGINT NOT NULL REFERENCES left_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS right_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           error TEXT NOT NULL
         );
         COMMENT ON COLUMN right_entity.error IS 'Right Type Characteristic';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#RightEntity

         CREATE TABLE IF NOT EXISTS right_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_either_with_complex_types (_id),
           _id_target BIGINT NOT NULL REFERENCES right_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_either_with_complex_types (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithComplexTypes
         """ );
   }

   @Test
   void testAspectWithEnglishAndGermanDescription() {
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder()
            .includeTableComment( true )
            .includeColumnComments( true )
            .commentLanguage( Locale.GERMAN ).build();
      assertThat( sql( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION, config ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_english_and_german_description (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL
         );
         COMMENT ON TABLE aspect_with_english_and_german_description IS 'Aspekt mit mehrsprachigen Beschreibungen';
         COMMENT ON COLUMN aspect_with_english_and_german_description.test_string IS 'Es ist ein Test-String';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnglishAndGermanDescription
         """ );
   }

   @Test
   void testAspectWithEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_ENTITY ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           entity_property TEXT NOT NULL
         );
         COMMENT ON TABLE test_entity IS 'This is a test entity';
         COMMENT ON COLUMN test_entity.entity_property IS 'This is a property for the test entity.';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_entity (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_entity (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect_with_entity IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntity
         """ );
   }

   @Test
   void testAspectWithEntityInstanceWithScalarListProperty() {
      assertThat( sql( TestAspect.ASPECT_WITH_ENTITY_INSTANCE_WITH_SCALAR_LIST_PROPERTY ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity__test_list (
           _id_source BIGINT NOT NULL REFERENCES test_entity (_id),
           value NUMERIC(10) NOT NULL
         );

         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           code SMALLINT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_entity_instance_with_scalar_list_property (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_entity_instance_with_scalar_list_property (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityInstanceWithScalarListProperty
         """ );
   }

   @Test
   void testAspectWithEntityList() {
      assertThat( sql( TestAspect.ASPECT_WITH_ENTITY_LIST ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL,
           test_int INTEGER NOT NULL,
           test_float REAL NOT NULL,
           test_local_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
           random_value TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_entity_list (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_entity_list (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityList
         """ );
   }

   @Test
   void testAspectWithEntityWithNestedEntityListProperty() {
      assertThat( sql( TestAspect.ASPECT_WITH_ENTITY_WITH_NESTED_ENTITY_LIST_PROPERTY ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS nested_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           nested_entity_property TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#NestedEntity

         CREATE TABLE IF NOT EXISTS nested_entity___references (
           _id_source BIGINT NOT NULL REFERENCES entity (_id),
           _id_target BIGINT NOT NULL REFERENCES nested_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           code SMALLINT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#Entity

         CREATE TABLE IF NOT EXISTS entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_entity_with_nested_entity_list_property (_id),
           _id_target BIGINT NOT NULL REFERENCES entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_entity_with_nested_entity_list_property (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityWithNestedEntityListProperty
         """ );
   }

   @Test
   void testAspectWithExtendedEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_EXTENDED_ENTITY ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           parent_string TEXT NOT NULL,
           parent_of_parent_string TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_extended_entity (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_extended_entity (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExtendedEntity
         """ );
   }

   @Test
   void testAspectWithList() {
      assertThat( sql( TestAspect.ASPECT_WITH_LIST ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_list__test_property (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_list (_id),
           value TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_list (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect_with_list IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithList
         """ );
   }

   @Test
   void testAspectWithMultipleEntities() {
      assertThat( sql( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL,
           test_int INTEGER NOT NULL,
           test_float REAL NOT NULL,
           test_local_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
           random_value TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_multiple_entities (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_multiple_entities (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntities
         """ );
   }

   @Test
   void testAspectWithMultipleEntitiesAndEither() {
      assertThat( sql( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_AND_EITHER ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL,
           test_int INTEGER NOT NULL,
           test_float REAL NOT NULL,
           test_local_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
           random_value TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_multiple_entities_and_either (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_multiple_entities_and_either (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesAndEither
         """ );
   }

   @Test
   void testAspectWithMultipleEntitiesOnMultipleLevels() {
      assertThat( sql( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_ON_MULTIPLE_LEVELS ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS third_test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL,
           test_float REAL NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#ThirdTestEntity

         CREATE TABLE IF NOT EXISTS third_test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES test_entity (_id),
           _id_target BIGINT NOT NULL REFERENCES third_test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_local_date_time TIMESTAMP NOT NULL,
           random_value TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_multiple_entities_on_multiple_levels (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS second_test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_int INTEGER NOT NULL,
           test_float REAL NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#SecondTestEntity

         CREATE TABLE IF NOT EXISTS second_test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_multiple_entities_on_multiple_levels (_id),
           _id_target BIGINT NOT NULL REFERENCES second_test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_multiple_entities_on_multiple_levels (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesOnMultipleLevels
         """ );
   }

   @Test
   void testAspectWithMultipleEntityCollections() {
      assertThat( sql( TestAspect.ASPECT_WITH_MULTIPLE_ENTITY_COLLECTIONS ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL,
           test_int INTEGER NOT NULL,
           test_float REAL NOT NULL,
           test_local_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
           random_value TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_multiple_entity_collections (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_multiple_entity_collections (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntityCollections
         """ );
   }

   @Test
   void testAspectWithNestedEntityList() {
      assertThat( sql( TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_second_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_local_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
           random_value TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestSecondEntity

         CREATE TABLE IF NOT EXISTS test_second_entity___references (
           _id_source BIGINT NOT NULL REFERENCES test_first_entity (_id),
           _id_target BIGINT NOT NULL REFERENCES test_second_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS test_first_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL,
           test_int INTEGER NOT NULL,
           test_float REAL NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestFirstEntity

         CREATE TABLE IF NOT EXISTS test_first_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_nested_entity_list (_id),
           _id_target BIGINT NOT NULL REFERENCES test_first_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_nested_entity_list (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityList
         """ );
   }

   @Test
   void testAspectWithNestedEntityEnumerationWithNotInPayload() {
      assertThat( sql( TestAspect.ASPECT_WITH_NESTED_ENTITY_ENUMERATION_WITH_NOT_IN_PAYLOAD ) ).isEqualTo(
            """
               CREATE TABLE IF NOT EXISTS test_entity (
                 _id BIGINT NOT NULL PRIMARY KEY,
                 entity_property TEXT NOT NULL
               );
               -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

               CREATE TABLE IF NOT EXISTS test_entity___references (
                 _id_source BIGINT NOT NULL REFERENCES aspect_with_nested_entity_enumeration_with_not_in_payload (_id),
                 _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
                 property_name TEXT NOT NULL
               );

               CREATE TABLE IF NOT EXISTS aspect_with_nested_entity_enumeration_with_not_in_payload (
                 _id BIGINT NOT NULL PRIMARY KEY
               );
               -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityEnumerationWithNotInPayload
               """ );
   }

   @Test
   void testAspectWithOperation() {
      assertThat( sql( TestAspect.ASPECT_WITH_OPERATION ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_operation (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOperation
         """ );
   }

   @Test
   void testAspectWithOptionalProperties() {
      assertThat( sql( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_optional_properties (
           _id BIGINT NOT NULL PRIMARY KEY,
           number_property NUMERIC(20),
           timestamp_property TIMESTAMP NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalProperties
         """ );
   }

   @Test
   void testAspectWithOptionalPropertiesWithEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES_WITH_ENTITY ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_entity__test_int_list (
           _id_source BIGINT NOT NULL REFERENCES test_entity (_id),
           value INTEGER NOT NULL
         );

         CREATE TABLE IF NOT EXISTS test_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           code_property INTEGER NOT NULL,
           test_second_string TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

         CREATE TABLE IF NOT EXISTS test_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_optional_properties_with_entity (_id),
           _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_optional_properties_with_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_string TEXT NOT NULL,
           test_optional_string TEXT
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertiesWithEntity
         """ );
   }

   @Test
   void testAspectWithPropertyWithPayloadName() {
      assertThat( sql( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_property_with_payload_name (
           _id BIGINT NOT NULL PRIMARY KEY,
           test TEXT NOT NULL
         );
         COMMENT ON TABLE aspect_with_property_with_payload_name IS 'This is a test description';
         COMMENT ON COLUMN aspect_with_property_with_payload_name.test IS 'This is a test property.';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithPayloadName
         """ );
   }

   @Test
   void testAspectWithRecursivePropertyWithOptional() {
      assertThat( sql( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL_AND_ENTITY_PROPERTY ) )
            .contains( "_id_source BIGINT NOT NULL REFERENCES test_entity (_id)" )
            .contains( "_id_target BIGINT NOT NULL REFERENCES test_entity (_id)" );
   }

   @Test
   void testAspectWithScriptTags() {
      assertThat( sql( TestAspect.ASPECT_WITH_SCRIPT_TAGS ) ).isEqualTo(
            """
               CREATE TABLE IF NOT EXISTS test_entity (
                 _id BIGINT NOT NULL PRIMARY KEY,
                 test_string TEXT NOT NULL,
                 test_int INTEGER NOT NULL,
                 test_float REAL NOT NULL,
                 test_local_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
                 random_value TEXT NOT NULL
               );
               COMMENT ON TABLE test_entity IS E'Test description with script: <script>alert(\\'Should not be alerted\\');</script>';
               COMMENT ON COLUMN test_entity.test_string IS E'Test description with script: <script>alert(\\'Should not be alerted\\');</script>';
               COMMENT ON COLUMN test_entity.test_int IS E'Test description with script: <script>alert(\\'Should not be alerted\\');</script>';
               COMMENT ON COLUMN test_entity.test_float IS E'Test description with script: <script>alert(\\'Should not be alerted\\');</script>';
               COMMENT ON COLUMN test_entity.random_value IS E'Test description with script: <script>alert(\\'Should not be alerted\\');</script>';
               -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity

               CREATE TABLE IF NOT EXISTS test_entity___references (
                 _id_source BIGINT NOT NULL REFERENCES aspect_with_script_tags (_id),
                 _id_target BIGINT NOT NULL REFERENCES test_entity (_id),
                 property_name TEXT NOT NULL
               );

               CREATE TABLE IF NOT EXISTS aspect_with_script_tags (
                 _id BIGINT NOT NULL PRIMARY KEY
               );
               -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithScriptTags
               """ );
   }

   @Test
   void testAspectWithSet() {
      assertThat( sql( TestAspect.ASPECT_WITH_SET ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_set__test_property (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_set (_id),
           value TEXT NOT NULL CONSTRAINT u_value UNIQUE
         );

         CREATE TABLE IF NOT EXISTS aspect_with_set (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect_with_set IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSet
         """ );
   }

   @Test
   void testAspectWithSimpleTypes() {
      assertThat( sql( TestAspect.ASPECT_WITH_SIMPLE_TYPES ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_simple_types (
           _id BIGINT NOT NULL PRIMARY KEY,
           any_uri_property TEXT NOT NULL,
           base64_binary_property BYTEA NOT NULL,
           boolean_property BOOLEAN NOT NULL,
           byte_property SMALLINT NOT NULL,
           curie_property TEXT NOT NULL,
           date_property TEXT NOT NULL,
           date_time_property TIMESTAMP NOT NULL,
           date_time_stamp_property TIMESTAMP WITH TIME ZONE NOT NULL,
           day_time_duration TEXT NOT NULL,
           decimal_property NUMERIC(10,0) NOT NULL,
           double_property DOUBLE PRECISION NOT NULL,
           duration_property TEXT NOT NULL,
           float_property REAL NOT NULL,
           g_day_property TEXT NOT NULL,
           g_month_day_property TEXT NOT NULL,
           g_month_property TEXT NOT NULL,
           g_year_month_property TEXT NOT NULL,
           g_year_property TEXT NOT NULL,
           hex_binary_property BYTEA NOT NULL,
           int_property INTEGER NOT NULL,
           integer_property NUMERIC(10) NOT NULL,
           lang_string_property TEXT NOT NULL,
           long_property BIGINT NOT NULL,
           negative_integer_property NUMERIC(10) NOT NULL,
           non_negative_integer_property NUMERIC(10) NOT NULL,
           non_positive_integer NUMERIC(10) NOT NULL,
           positive_integer_property NUMERIC(10) NOT NULL,
           short_property SMALLINT NOT NULL,
           string_property TEXT NOT NULL,
           time_property TEXT NOT NULL,
           unsigned_byte_property SMALLINT NOT NULL,
           unsigned_int_property BIGINT NOT NULL,
           unsigned_long_property NUMERIC(20) NOT NULL,
           unsigned_short_property INTEGER NOT NULL,
           year_month_duration_property TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimpleTypes
         """ );
   }

   @ParameterizedTest
   @ValueSource( ints = { 10, 20, 38 } )
   void testUnsignedLongPrecisionIsIndependentOfDecimalConfig( final int precision ) {
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder()
            .decimalPrecision( precision )
            .decimalScale( 2 )
            .build();

      assertThat( sql( TestAspect.ASPECT_WITH_SIMPLE_TYPES, config ) )
            .contains( "unsigned_long_property NUMERIC(20) NOT NULL" )
            .contains( "decimal_property NUMERIC(%d,2) NOT NULL".formatted( precision ) )
            .contains( "integer_property NUMERIC(%d) NOT NULL".formatted( precision ) );
   }

   @Test
   void testAspectWithSortedSet() {
      assertThat( sql( TestAspect.ASPECT_WITH_SORTED_SET ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_sorted_set__test_property (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_sorted_set (_id),
           value TEXT NOT NULL CONSTRAINT u_value UNIQUE
         );

         CREATE TABLE IF NOT EXISTS aspect_with_sorted_set (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect_with_sorted_set IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSortedSet
         """ );
   }

   @Test
   void testAspectWithTimeSeries() {
      assertThat( sql( TestAspect.ASPECT_WITH_TIME_SERIES ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS test_time_series_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           value TEXT NOT NULL,
           timestamp TIMESTAMP NOT NULL
         );
         COMMENT ON TABLE test_time_series_entity IS 'This is a test time series entity.';
         COMMENT ON COLUMN test_time_series_entity.value IS 'The value that was recorded and is part of a time series.';
         COMMENT ON COLUMN test_time_series_entity.timestamp IS 'The specific point in time when the corresponding value was recorded.';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#TestTimeSeriesEntity

         CREATE TABLE IF NOT EXISTS test_time_series_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_time_series (_id),
           _id_target BIGINT NOT NULL REFERENCES test_time_series_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_time_series (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect_with_time_series IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTimeSeries
         """ );
   }

   @Test
   void testAspectWithComplexSet() {
      assertThat( sql( TestAspect.ASPECT_WITH_COMPLEX_SET ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS id (
           _id BIGINT NOT NULL PRIMARY KEY,
           product_id TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#Id

         CREATE TABLE IF NOT EXISTS id___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_complex_set (_id),
           _id_target BIGINT NOT NULL REFERENCES id (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_complex_set (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         COMMENT ON TABLE aspect_with_complex_set IS 'This is a test description';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexSet
         """ );
   }

   @Test
   void testTableConstraints() {
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder()
            .tableConstraintsPerTable( Map.of( "aspect_with_property_with_payload_name",
                  Stream.of( "PRIMARY KEY (test)", "UNIQUE (test)", "CHECK (test <> 'x')" )
                        .<PostgreSqlConstraint>map( PostgreSqlConstraint.GenericConstraint::new ).toList() ) )
            .build();
      assertThat( sql( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME, config ) )
            .contains( "PRIMARY KEY (test),\n  UNIQUE (test),\n  CHECK (test <> 'x')\n)" );
   }

   @Test
   void testAspectWithCustomColumn() {
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder()
            .includeTableComment( true )
            .includeColumnComments( true )
            .customColumnsPerTable( Map.of( "aspect_with_property_with_payload_name", List.of(
                  PostgreSqlColumnDefinitionBuilder.builder()
                        .name( "custom" )
                        .type( new PostgreSqlType.PostgreSqlArray( PostgreSqlType.TEXT ) )
                        .nullable( false )
                        .comment( Optional.of( "Custom column" ) )
                        .build()
            ) ) )
            .build();
      assertThat( sql( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME, config ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_property_with_payload_name (
           _id BIGINT NOT NULL PRIMARY KEY,
           test TEXT NOT NULL,
           custom TEXT[] NOT NULL
         );
         COMMENT ON TABLE aspect_with_property_with_payload_name IS 'This is a test description';
         COMMENT ON COLUMN aspect_with_property_with_payload_name.test IS 'This is a test property.';
         COMMENT ON COLUMN aspect_with_property_with_payload_name.custom IS 'Custom column';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithPayloadName
         """ );
   }

   @Test
   void testAspectWithUpwardTransitionInNestedEntity() {
      assertThat( sql( TestAspect.ASPECT_WITH_UPWARD_TRANSITION_IN_NESTED_ENTITY ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS second_level_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           third_level_property TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#SecondLevelEntity

         CREATE TABLE IF NOT EXISTS second_level_entity___references (
           _id_source BIGINT NOT NULL REFERENCES first_level_entity (_id),
           _id_target BIGINT NOT NULL REFERENCES second_level_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS first_level_entity (
           _id BIGINT NOT NULL PRIMARY KEY,
           second_level_property2 TEXT NOT NULL
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#FirstLevelEntity

         CREATE TABLE IF NOT EXISTS first_level_entity___references (
           _id_source BIGINT NOT NULL REFERENCES aspect_with_upward_transition_in_nested_entity (_id),
           _id_target BIGINT NOT NULL REFERENCES first_level_entity (_id),
           property_name TEXT NOT NULL
         );

         CREATE TABLE IF NOT EXISTS aspect_with_upward_transition_in_nested_entity (
           _id BIGINT NOT NULL PRIMARY KEY
         );
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUpwardTransitionInNestedEntity
         """ );
   }

   @Test
   void testFlattenScalarCollections() {
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder()
            .flattenScalarCollections( true )
            .build();
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTION, config ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_collection (
           _id BIGINT NOT NULL PRIMARY KEY,
           test_property TEXT[] NOT NULL
         );
         COMMENT ON TABLE aspect_with_collection IS 'This is a test description';
         COMMENT ON COLUMN aspect_with_collection.test_property IS 'This is a test property.';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollection
         """ );
   }
}
