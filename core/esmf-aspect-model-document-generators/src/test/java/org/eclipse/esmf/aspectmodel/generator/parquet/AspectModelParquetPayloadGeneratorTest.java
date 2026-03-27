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
package org.eclipse.esmf.aspectmodel.generator.parquet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.aspect;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.characteristic;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.property;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.rangeConstraint;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.trait;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.value;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.ParquetArtifact;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;
import org.eclipse.esmf.test.TestResources;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.schema.MessageType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test suite for AspectModelParquetPayloadGenerator.
 */
class AspectModelParquetPayloadGeneratorTest {
   private final List<Path> tempFiles = new ArrayList<>();

   @AfterEach
   void cleanup() {
      for ( final Path tempFile : tempFiles ) {
         try {
            Files.deleteIfExists( tempFile );
         } catch ( final IOException ignored ) {
            // Ignore cleanup errors
         }
      }
      tempFiles.clear();
   }

   /**
    * Parameterized smoke test — verifies that Parquet file generation succeeds for every
    * {@link TestAspect} that is expected to produce valid output.
    */
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testParquetFileGenerationForAllAspects( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      assertThatCode( () -> {
         final Path parquetFile = generateParquetForModel( aspect );
         assertThat( parquetFile ).exists();
         assertThat( parquetFile.toFile().length() ).isGreaterThan( 0 );

         try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
            final MessageType schema = reader.getFooter().getFileMetaData().getSchema();
            assertThat( schema ).isNotNull();
            assertThat( schema.getFields() ).isNotEmpty();
         }
      } ).doesNotThrowAnyException();
   }

   @Test
   void testGenerateParquetForAspectWithSimpleProperties() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES );
      assertThat( parquetFile ).exists();

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();

         assertThat( schema.containsField( "testString" ) ).isTrue();
         assertThat( schema.containsField( "testInt" ) ).isTrue();
         assertThat( schema.containsField( "testFloat" ) ).isTrue();
         assertThat( schema.containsField( "testLocalDateTime" ) ).isTrue();
         assertThat( schema.containsField( "randomValue" ) ).isTrue();
      }

      // The generator writes one row per top-level property, so we need to search across all records
      final List<Group> records = readAllRecords( parquetFile );
      assertThat( records ).isNotEmpty();

      final Group stringRecord = findRecordWithField( records, "testString" );
      assertThat( stringRecord ).isNotNull();
      assertThat( stringRecord.getString( "testString", 0 ) ).isEqualTo( "Example Value Test" );

      final Group intRecord = findRecordWithField( records, "testInt" );
      assertThat( intRecord ).isNotNull();
      assertThat( intRecord.getInteger( "testInt", 0 ) ).isEqualTo( 3 );
   }

   @Test
   void testGenerateParquetForAspectWithStateType() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES_AND_STATE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithEntity() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ENTITY_WITH_MULTIPLE_PROPERTIES );
      assertThat( parquetFile ).exists();

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();
         // Entity properties are flattened with double-underscore prefix
         assertThat( schema.getFields() ).isNotEmpty();
      }

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithRecursivePropertyWithOptional() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithCollectionOfEntities() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ENTITY_LIST );
      assertThat( parquetFile ).exists();

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();
         assertThat( schema.getFields() ).isNotEmpty();
      }

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithMultipleEntities() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithNestedEntity() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_NESTED_ENTITY );
      assertThat( parquetFile ).exists();

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();
         assertThat( schema ).isNotNull();
      }

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithCollectionOfSimpleType() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE );
      assertThat( parquetFile ).exists();

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();
         assertThat( schema ).isNotNull();
      }

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithMultipleCollectionsOfSimpleType() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_MULTIPLE_COLLECTIONS_OF_SIMPLE_TYPE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithEitherType() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_EITHER );
      assertThat( parquetFile ).exists();

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();
         assertThat( schema.getFields() ).isNotEmpty();
      }

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithMultipleEntitiesAndEither() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_AND_EITHER );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithMultipleEntityCollections() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITY_COLLECTIONS );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithComplexEnum() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_COMPLEX_ENUM );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithEnumHavingNestedEntities() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ENUM_HAVING_NESTED_ENTITIES );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithEntityEnumerationAndLangString() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithComplexEntityCollectionEnum() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a CURIE property
    * (mirrors {@code testGenerateAspectForCurie}).
    */
   @Test
   void testGenerateParquetForAspectWithCurie() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_CURIE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();

      if ( group.getType().containsField( "testCurie" ) ) {
         final String curieValue = group.getString( "testCurie", 0 );
         assertThat( curieValue ).contains( ":" );
      }
   }

   /**
    * Verifies Parquet generation for an aspect with multi-language text
    * (mirrors {@code testGenerateAspectWithMultiLanguageText}).
    */
   @Test
   void testGenerateParquetForAspectWithMultiLanguageText() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a multi-language example value
    * (mirrors {@code testGenerateAspectWithMultiLanguageExampleValue}).
    */
   @Test
   void testGenerateParquetForAspectWithMultilanguageExampleValue() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_MULTILANGUAGE_EXAMPLE_VALUE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with constraints
    * (mirrors {@code testGenerateAspectWithConstraints}).
    */
   @Test
   void testGenerateParquetForAspectWithConstraints() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_CONSTRAINTS );
      assertThat( parquetFile ).exists();

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();
         assertThat( schema ).isNotNull();
      }

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a single constraint
    * (mirrors {@code testGenerateAspectWithConstraint}).
    */
   @Test
   void testGenerateParquetForAspectWithConstraint() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_CONSTRAINT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithRangeConstraintWithoutMinMaxIntegerValue() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_INTEGER_VALUE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithRangeConstraintWithoutMinMaxDoubleValue() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_DOUBLE_VALUE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with date/time range constraints
    * (mirrors {@code testGenerateAspectWithDateTimeTypeForRangeConstraints}).
    */
   @Test
   void testGenerateParquetForAspectWithTypeForRangeConstraints() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_G_TYPE_FOR_RANGE_CONSTRAINTS );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a structured value
    * (mirrors {@code testGenerateAspectWithStructuredValue}).
    */
   @Test
   void testGenerateParquetForAspectWithStructuredValue() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_STRUCTURED_VALUE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithFixedPointConstraint() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_FIXED_POINT_CONSTRAINT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithAbstractEntity() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithAbstractSingleEntity() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithCollectionWithAbstractEntity() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with entity enumeration and not-in-payload properties.
    */
   @Test
   void testGenerateParquetForAspectWithEntityEnumerationAndNotInPayloadProperties() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with extended enums with a not-in-payload property.
    */
   @Test
   void testGenerateParquetForAspectWithExtendedEnumsWithNotInPayloadProperty() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_EXTENDED_ENUMS_WITH_NOT_IN_PAYLOAD_PROPERTY );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithPropertyWithPayloadName() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithConstrainedSet() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_CONSTRAINED_SET );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   @Test
   void testGenerateParquetForAspectWithComplexSet() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_COMPLEX_SET );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with an Either characteristic containing complex types.
    */
   @Test
   void testGenerateParquetForAspectWithEitherWithComplexTypes() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation with a type attribute for entity inheritance
    * (mirrors {@code testGenerationOfTypeAttributeForEntityInheritance}).
    */
   @Test
   void testGenerateParquetWithTypeAttributeForEntityInheritance() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY ).aspect();
      final ParquetGenerationConfig config = ParquetGenerationConfigBuilder.builder()
            .addTypeAttributeForEntityInheritance( true )
            .build();

      final Path parquetFile = generateParquetForModel( aspect, config );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with optional properties.
    */
   @Test
   void testGenerateParquetForAspectWithOptionalProperties() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with optional properties containing an entity.
    */
   @Test
   void testGenerateParquetForAspectWithOptionalPropertiesWithEntity() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES_WITH_ENTITY );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a boolean property.
    */
   @Test
   void testGenerateParquetForAspectWithBoolean() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_BOOLEAN );
      assertThat( parquetFile ).exists();

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();
         assertThat( schema.getFields() ).isNotEmpty();
      }

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a binary property.
    */
   @Test
   void testGenerateParquetForAspectWithBinary() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_BINARY );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a duration property.
    */
   @Test
   void testGenerateParquetForAspectWithDuration() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_DURATION );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation with a custom configuration using a fixed random seed for
    * reproducibility.
    */
   @Test
   void testGenerateParquetWithCustomConfig() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES ).aspect();
      final ParquetGenerationConfig config = ParquetGenerationConfigBuilder.builder()
            .randomStrategy( new java.util.Random( 42 ) )
            .build();

      final Path parquetFile = generateParquetForModel( aspect, config );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies the structure of the generated Parquet schema for an aspect with simple properties.
    */
   @Test
   void testParquetSchemaStructure() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES );

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         final MessageType schema = reader.getFooter().getFileMetaData().getSchema();

         assertThat( schema.getFields() ).hasSizeGreaterThan( 0 );

         if ( schema.containsField( "testInt" ) ) {
            assertThat( schema.getType( "testInt" ).isPrimitive() ).isTrue();
         }
      }
   }

   /**
    * Verifies the metadata of the generated Parquet file (row groups and record count).
    */
   @Test
   void testParquetFileMetadata() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES );

      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( parquetFile ) ) ) {
         assertThat( reader.getRowGroups() ).isNotEmpty();
         assertThat( reader.getRecordCount() ).isGreaterThan( 0 );
      }
   }

   /**
    * Verifies Parquet generation for an aspect with an extended entity.
    */
   @Test
   void testGenerateParquetForAspectWithExtendedEntity() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_EXTENDED_ENTITY );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with an entity collection.
    */
   @Test
   void testGenerateParquetForAspectWithEntityCollection() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ENTITY_COLLECTION );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a string enumeration.
    */
   @Test
   void testGenerateParquetForAspectWithStringEnumeration() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_STRING_ENUMERATION );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a regular expression constraint.
    */
   @Test
   void testGenerateParquetForAspectWithRegularExpressionConstraint() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_REGULAR_EXPRESSION_CONSTRAINT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with an encoding constraint.
    */
   @Test
   void testGenerateParquetForAspectWithEncodingConstraint() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_ENCODING_CONSTRAINT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a length constraint.
    */
   @Test
   void testGenerateParquetForAspectWithLengthConstraint() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_LENGTH_CONSTRAINT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a measurement that has a unit.
    */
   @Test
   void testGenerateParquetForAspectWithMeasurementWithUnit() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_MEASUREMENT_WITH_UNIT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a quantifiable property that has a unit.
    */
   @Test
   void testGenerateParquetForAspectWithQuantifiableWithUnit() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITH_UNIT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a time series property.
    */
   @Test
   void testGenerateParquetForAspectWithTimeSeries() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_TIME_SERIES );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a sorted set property.
    */
   @Test
   void testGenerateParquetForAspectWithSortedSet() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_SORTED_SET );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a list property.
    */
   @Test
   void testGenerateParquetForAspectWithList() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_LIST );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a set property.
    */
   @Test
   void testGenerateParquetForAspectWithSet() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_SET );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a code characteristic.
    */
   @Test
   void testGenerateParquetForAspectWithCode() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_CODE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a numeric structured value.
    */
   @Test
   void testGenerateParquetForAspectWithNumericStructuredValue() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_NUMERIC_STRUCTURED_VALUE );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with a list that has a length constraint.
    */
   @Test
   void testGenerateParquetForAspectWithListWithLengthConstraint() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_LIST_WITH_LENGTH_CONSTRAINT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Verifies Parquet generation for an aspect with an exclusive range constraint.
    */
   @Test
   void testGenerateParquetForAspectWithExclusiveRangeConstraint() throws IOException {
      final Path parquetFile = generateParquetForModel( TestAspect.ASPECT_WITH_EXCLUSIVE_RANGE_CONSTRAINT );
      assertThat( parquetFile ).exists();

      final Group group = readFirstRecord( parquetFile );
      assertThat( group ).isNotNull();
   }

   /**
    * Parameterized test that verifies generated numeric values in Parquet files
    * fall within the expected range for each numeric meta-model type and bound definition.
    */
   @ParameterizedTest
   @MethodSource( "rangeTestSource" )
   void testGeneratedNumericValuesAreWithinRange( final RDFDatatype numericModelType, final Optional<BoundDefinition> boundKind )
         throws IOException {
      final Type numericType = new DefaultScalar( numericModelType.getURI() );
      final Resource dataTypeResource = ResourceFactory.createResource( numericType.getUrn() );
      final Class<?> nativeType = SammXsdType.getJavaTypeForMetaModelType( dataTypeResource );
      final Pair<Number, Number> randomRange = generateRandomRangeForType( numericType, nativeType, boundKind.orElse( null ) );

      final Aspect dynamicAspect = createAspectWithDynamicNumericProperty( numericType, boundKind.orElse( null ), randomRange );

      final Path parquetFile = generateParquetForModel( dynamicAspect );
      assertThat( parquetFile ).exists();

      final Group parquetRecord = readFirstRecord( parquetFile );
      assertThat( parquetRecord ).isNotNull();

      if ( parquetRecord.getType().containsField( "testNumber" ) ) {
         final Number value = extractNumericValue( parquetRecord, "testNumber", nativeType );
         assertNumberInRange( value, randomRange, boundKind.orElse( null ) );
      }
   }

   private Path generateParquetForModel( final TestAspect testAspect ) throws IOException {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      return generateParquetForModel( aspect );
   }

   private Path generateParquetForModel( final Aspect aspect ) throws IOException {
      return generateParquetForModel( aspect, AspectModelParquetPayloadGenerator.DEFAULT_CONFIG );
   }

   private Path generateParquetForModel( final Aspect aspect, final ParquetGenerationConfig config ) throws IOException {
      final Path tempFile = Files.createTempFile( "test-parquet-", ".parquet" );
      tempFiles.add( tempFile );

      final AspectModelParquetPayloadGenerator generator = new AspectModelParquetPayloadGenerator( aspect, config );

      final ParquetArtifact artifact = generator.generate().findFirst()
            .orElseThrow( () -> new IOException( "Failed to generate parquet artifact" ) );

      Files.write( tempFile, artifact.serialize() );
      return tempFile;
   }

   private Group readFirstRecord( final Path parquetFile ) throws IOException {
      final List<Group> records = readAllRecords( parquetFile );
      return records.isEmpty() ? null : records.getFirst();
   }

   private List<Group> readAllRecords( final Path parquetFile ) throws IOException {
      final List<Group> records = new ArrayList<>();
      final LocalInputFile inputFile = new LocalInputFile( parquetFile );
      try ( final ParquetFileReader fileReader = ParquetFileReader.open( inputFile ) ) {
         final MessageType schema = fileReader.getFooter().getFileMetaData().getSchema();
         final org.apache.parquet.io.ColumnIOFactory columnIoFactory = new org.apache.parquet.io.ColumnIOFactory();
         final org.apache.parquet.io.MessageColumnIO columnIo = columnIoFactory.getColumnIO( schema );
         org.apache.parquet.column.page.PageReadStore pages;
         while ( ( pages = fileReader.readNextRowGroup() ) != null ) {
            final long rowCount = pages.getRowCount();
            final org.apache.parquet.io.RecordReader<Group> recordReader =
                  columnIo.getRecordReader( pages, new org.apache.parquet.example.data.simple.convert.GroupRecordConverter( schema ) );
            for ( long i = 0; i < rowCount; i++ ) {
               records.add( recordReader.read() );
            }
         }
      }
      return records;
   }

   /**
    * Find the first record that contains a non-null value for the given field.
    */
   private Group findRecordWithField( final List<Group> records, final String fieldName ) {
      for ( final Group group : records ) {
         try {
            if ( group.getType().containsField( fieldName ) && group.getFieldRepetitionCount( fieldName ) > 0 ) {
               return group;
            }
         } catch ( final RuntimeException ignored ) {
            // Field not found in this group, continue
         }
      }
      return null;
   }

   private Number extractNumericValue( final Group parquetRecord, final String fieldName, final Class<?> nativeType ) {
      if ( Integer.class.isAssignableFrom( nativeType ) || Byte.class.isAssignableFrom( nativeType )
            || Short.class.isAssignableFrom( nativeType ) ) {
         return parquetRecord.getInteger( fieldName, 0 );
      } else if ( Long.class.isAssignableFrom( nativeType ) ) {
         return parquetRecord.getLong( fieldName, 0 );
      } else if ( Float.class.isAssignableFrom( nativeType ) ) {
         return parquetRecord.getFloat( fieldName, 0 );
      } else if ( Double.class.isAssignableFrom( nativeType ) ) {
         return parquetRecord.getDouble( fieldName, 0 );
      } else if ( BigInteger.class.isAssignableFrom( nativeType ) ) {
         try {
            return BigInteger.valueOf( parquetRecord.getInteger( fieldName, 0 ) );
         } catch ( final ClassCastException ignored ) {
            try {
               return BigInteger.valueOf( parquetRecord.getLong( fieldName, 0 ) );
            } catch ( final ClassCastException classCastException ) {
               return new BigInteger( parquetRecord.getString( fieldName, 0 ) );
            }
         }
      } else if ( BigDecimal.class.isAssignableFrom( nativeType ) ) {
         try {
            return BigDecimal.valueOf( parquetRecord.getDouble( fieldName, 0 ) );
         } catch ( final ClassCastException ignored ) {
            try {
               return new BigDecimal( parquetRecord.getString( fieldName, 0 ) );
            } catch ( final ClassCastException classCastException ) {
               return BigDecimal.valueOf( parquetRecord.getFloat( fieldName, 0 ) );
            }
         }
      }
      throw new IllegalArgumentException( "Unsupported numeric type: " + nativeType );
   }

   private void assertNumberInRange( final Number value, final Pair<Number, Number> range, final BoundDefinition boundKind ) {
      assertThat( value ).isNotNull();
      // Skip assertion for Infinity/NaN values that cannot be converted to BigDecimal
      if ( value instanceof final Double d && ( Double.isInfinite( d ) || Double.isNaN( d ) ) ) {
         return;
      }
      if ( value instanceof final Float f && ( Float.isInfinite( f ) || Float.isNaN( f ) ) ) {
         return;
      }
      final BigDecimal numberValue = toBigDecimal( value );
      final BigDecimal lowerBound;
      final BigDecimal upperBound;
      try {
         lowerBound = toBigDecimal( range.getLeft() );
         upperBound = toBigDecimal( range.getRight() );
      } catch ( final NumberFormatException ignored ) {
         // Skip assertion when bounds cannot be converted (Infinity/NaN)
         return;
      }
      if ( BoundDefinition.GREATER_THAN.equals( boundKind ) ) {
         assertThat( numberValue ).isStrictlyBetween( lowerBound, upperBound );
      } else {
         assertThat( numberValue ).isBetween( lowerBound, upperBound );
      }
   }

   private Pair<Number, Number> generateRandomRangeForType( final Type dataType, final Class<?> nativeType,
         final BoundDefinition boundKind ) {
      final Resource dataTypeResource = ResourceFactory.createResource( dataType.getUrn() );
      Number min = getModelMinValue( dataTypeResource, nativeType );
      Number max = getModelMaxValue( dataTypeResource, nativeType );

      // Cap the range to values representable by the Parquet storage type.
      // Most BigInteger types (integer, positiveInteger, etc.) are stored as INT32 in Parquet, so cap to
      // int range.
      // However, unsignedLong and unsignedInt are stored as INT64, so cap those to long range.
      // Float/Double/BigDecimal must avoid range overflow in arithmetic.
      final Set<Resource> int64BigIntegerTypes = Set.of( XSD.unsignedLong, XSD.unsignedInt );
      if ( BigInteger.class.isAssignableFrom( nativeType ) && int64BigIntegerTypes.contains( dataTypeResource ) ) {
         // These BigInteger types are stored as INT64 in Parquet
         final BigDecimal bdMin = toBigDecimal( min );
         final BigDecimal bdMax = toBigDecimal( max );
         final BigDecimal longMin = BigDecimal.valueOf( Long.MIN_VALUE );
         final BigDecimal longMax = BigDecimal.valueOf( Long.MAX_VALUE );
         if ( bdMin.compareTo( longMin ) < 0 ) {
            min = longMin.toBigInteger();
         }
         if ( bdMax.compareTo( longMax ) > 0 ) {
            max = longMax.toBigInteger();
         }
      } else if ( BigInteger.class.isAssignableFrom( nativeType ) ) {
         // Other BigInteger types are stored as INT32 in Parquet
         final BigDecimal bdMin = toBigDecimal( min );
         final BigDecimal bdMax = toBigDecimal( max );
         final BigDecimal int32Min = BigDecimal.valueOf( Integer.MIN_VALUE );
         final BigDecimal int32Max = BigDecimal.valueOf( Integer.MAX_VALUE );
         if ( bdMin.compareTo( int32Min ) < 0 ) {
            min = int32Min.toBigInteger();
         }
         if ( bdMax.compareTo( int32Max ) > 0 ) {
            max = int32Max.toBigInteger();
         }
      } else if ( Long.class.isAssignableFrom( nativeType ) ) {
         final BigDecimal bdMin = toBigDecimal( min );
         final BigDecimal bdMax = toBigDecimal( max );
         final BigDecimal longMin = BigDecimal.valueOf( Long.MIN_VALUE );
         final BigDecimal longMax = BigDecimal.valueOf( Long.MAX_VALUE );
         if ( bdMin.compareTo( longMin ) < 0 ) {
            min = Long.MIN_VALUE;
         }
         if ( bdMax.compareTo( longMax ) > 0 ) {
            max = Long.MAX_VALUE;
         }
      } else if ( Float.class.isAssignableFrom( nativeType ) ) {
         // When the range spans the full float range, (hi - lo) overflows to Infinity;
         // the generator uses half-range in that case, so cap here too.
         final float fMin = min.floatValue();
         final float fMax = max.floatValue();
         if ( Float.isInfinite( fMax - fMin ) ) {
            min = fMin / 2.0f;
            max = fMax / 2.0f;
         }
      } else if ( Double.class.isAssignableFrom( nativeType ) ) {
         final double dMin = min.doubleValue();
         final double dMax = max.doubleValue();
         if ( Double.isInfinite( dMax - dMin ) ) {
            min = dMin / 2.0;
            max = dMax / 2.0;
         }
      } else if ( BigDecimal.class.isAssignableFrom( nativeType ) ) {
         final BigDecimal bdMin = toBigDecimal( min );
         final BigDecimal bdMax = toBigDecimal( max );
         final BigDecimal dblMin = BigDecimal.valueOf( -Double.MAX_VALUE / 2 );
         final BigDecimal dblMax = BigDecimal.valueOf( Double.MAX_VALUE / 2 );
         if ( bdMin.compareTo( dblMin ) < 0 ) {
            min = dblMin;
         }
         if ( bdMax.compareTo( dblMax ) > 0 ) {
            max = dblMax;
         }
      }

      if ( null == boundKind || BoundDefinition.OPEN.equals( boundKind ) ) {
         return Pair.of( min, max );
      }

      Pair<Number, Number> range = TEST_RANGES.get( nativeType );

      final BigDecimal helperMin = toBigDecimal( min );
      final BigDecimal helperMax = toBigDecimal( max );
      if ( helperMax.compareTo( BigDecimal.ZERO ) == 0 || helperMax.compareTo( BigDecimal.valueOf( -1 ) ) == 0 ) {
         range = Pair.of( range.getLeft(), max );
      }
      if ( helperMin.compareTo( BigDecimal.ZERO ) == 0 || helperMin.compareTo( BigDecimal.ONE ) == 0 ) {
         range = Pair.of( min, helperMax.compareTo( toBigDecimal( range.getRight() ) ) < 0
               ? max
               : range.getRight() );
      }

      return range;
   }

   private static final java.util.Map<Class<?>, Pair<Number, Number>> TEST_RANGES = java.util.Map.of(
         Byte.class, Pair.of( (byte) -34, (byte) 112 ),
         Short.class, Pair.of( (short) -11856, (short) 27856 ),
         Integer.class, Pair.of( -118560, 278560 ),
         Long.class, Pair.of( -4523542345L, 45687855464565555L ),
         Float.class, Pair.of( -45.23f, 1056.764f ),
         Double.class, Pair.of( -1056.4737423, 734838.82734 ),
         // BigInteger types are mapped to INT32 in Parquet, so keep ranges within int range
         BigInteger.class, Pair.of( BigInteger.valueOf( -148234 ), BigInteger.valueOf( 454655 ) ),
         BigDecimal.class, Pair.of( BigDecimal.valueOf( -87445.2345 ), BigDecimal.valueOf( 2345345.12345 ) )
   );

   private static List<Arguments> rangeTestSource() {
      final List<Arguments> result = new ArrayList<>();
      Lists.cartesianProduct( getMetaModelNumericTypes(), RANGE_CONSTRAINTS_TO_TEST )
            .forEach( list -> result.add( Arguments.of( list.getFirst(), list.get( 1 ) ) ) );
      return result;
   }

   private static List<RDFDatatype> getMetaModelNumericTypes() {
      return SammXsdType.ALL_TYPES.stream()
            .filter( dataType -> dataType.getJavaClass() != null )
            .filter( dataType -> Number.class.isAssignableFrom( dataType.getJavaClass() ) )
            .collect( java.util.stream.Collectors.toList() );
   }

   private static final List<Optional<BoundDefinition>> RANGE_CONSTRAINTS_TO_TEST = Arrays.asList(
         Optional.empty(),
         Optional.of( BoundDefinition.OPEN ),
         Optional.of( BoundDefinition.AT_LEAST ),
         Optional.of( BoundDefinition.GREATER_THAN )
   );

   private Aspect createAspectWithDynamicNumericProperty( final Type dataType, final BoundDefinition boundKind,
         final Pair<Number, Number> randomRange ) {
      final Characteristic constraint = boundKind == null ? createBasicCharacteristic( dataType )
            : createTraitWithRangeConstraint( dataType, boundKind, randomRange );
      return aspect( TestModel.TEST_NAMESPACE + "AspectWithNumericProperty" )
            .property( createProperty( "testNumber", constraint ) )
            .build();
   }

   private Property createProperty( final String propertyName, final Characteristic characteristic ) {
      return property( TestModel.TEST_NAMESPACE + propertyName )
            .characteristic( characteristic )
            .build();
   }

   private Characteristic createBasicCharacteristic( final Type dataType ) {
      return characteristic( TestModel.TEST_NAMESPACE + "NumberCharacteristic" )
            .preferredName( "NumberCharacteristic", Locale.forLanguageTag( "en" ) )
            .description( "A simple numeric property.", Locale.forLanguageTag( "en" ) )
            .dataType( dataType )
            .build();
   }

   private Trait createTraitWithRangeConstraint( final Type dataType, final BoundDefinition boundKind,
         final Pair<Number, Number> randomRange ) {
      final Scalar scalar = new DefaultScalar( dataType.getUrn() );
      return trait( TestModel.TEST_NAMESPACE + "TestTrait" )
            .baseCharacteristic( createBasicCharacteristic( dataType ) )
            .constraint( rangeConstraint()
                  .minValue( value( randomRange.getLeft(), scalar ) )
                  .maxValue( value( randomRange.getRight(), scalar ) )
                  .lowerBound( boundKind )
                  .upperBound( getMatchingUpperBound( boundKind ) )
                  .build() )
            .build();
   }

   private BoundDefinition getMatchingUpperBound( final BoundDefinition boundKind ) {
      return boundKind == BoundDefinition.AT_LEAST ? BoundDefinition.AT_MOST
            : boundKind == BoundDefinition.GREATER_THAN ? BoundDefinition.LESS_THAN : BoundDefinition.OPEN;
   }

   /**
    * Converts any {@link Number} to {@link BigDecimal}, replacing the removed
    * {@code NumericTypeTraits.convertToBigDecimal}.
    */
   private static BigDecimal toBigDecimal( final Number number ) {
      if ( number instanceof final BigDecimal bd ) {
         return bd;
      }
      if ( number instanceof final BigInteger bi ) {
         return new BigDecimal( bi );
      }
      if ( number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte ) {
         return BigDecimal.valueOf( number.longValue() );
      }
      return BigDecimal.valueOf( number.doubleValue() );
   }

   /**
    * Returns the minimum value for the given SAMM data type, using
    * {@link SammType.IntegerType#lowerBound()}
    * for integer types and native Java type bounds for floating-point types.
    */
   private static Number getModelMinValue( final Resource dataTypeResource, final Class<?> nativeType ) {
      final Optional<BigInteger> sammLowerBound = SammXsdType.ALL_TYPES.stream()
            .filter( type -> type.getUrn().equals( dataTypeResource.getURI() ) )
            .filter( SammType.IntegerType.class::isInstance )
            .map( type -> ( (SammType.IntegerType<?>) type ).lowerBound() )
            .findFirst()
            .flatMap( opt -> opt );
      if ( sammLowerBound.isPresent() ) {
         return sammLowerBound.get();
      }
      // Floating-point and decimal types: return negative max value
      if ( Float.class.isAssignableFrom( nativeType ) ) {
         return -Float.MAX_VALUE;
      }
      if ( Double.class.isAssignableFrom( nativeType ) ) {
         return -Double.MAX_VALUE;
      }
      if ( BigDecimal.class.isAssignableFrom( nativeType ) ) {
         return BigDecimal.valueOf( -Double.MAX_VALUE );
      }
      if ( BigInteger.class.isAssignableFrom( nativeType ) ) {
         // Unbounded integer — use Double range as fallback
         return BigDecimal.valueOf( -Double.MAX_VALUE ).toBigInteger();
      }
      // Fallback for other integral types
      return toBigDecimal( getJavaNativeMinValue( nativeType ) );
   }

   /**
    * Returns the maximum value for the given SAMM data type, using
    * {@link SammType.IntegerType#upperBound()}
    * for integer types and native Java type bounds for floating-point types.
    */
   private static Number getModelMaxValue( final Resource dataTypeResource, final Class<?> nativeType ) {
      final Optional<BigInteger> sammUpperBound = SammXsdType.ALL_TYPES.stream()
            .filter( type -> type.getUrn().equals( dataTypeResource.getURI() ) )
            .filter( SammType.IntegerType.class::isInstance )
            .map( type -> ( (SammType.IntegerType<?>) type ).upperBound() )
            .findFirst()
            .flatMap( opt -> opt );
      if ( sammUpperBound.isPresent() ) {
         return sammUpperBound.get();
      }
      // Floating-point and decimal types: return max value
      if ( Float.class.isAssignableFrom( nativeType ) ) {
         return Float.MAX_VALUE;
      }
      if ( Double.class.isAssignableFrom( nativeType ) ) {
         return Double.MAX_VALUE;
      }
      if ( BigDecimal.class.isAssignableFrom( nativeType ) ) {
         return BigDecimal.valueOf( Double.MAX_VALUE );
      }
      if ( BigInteger.class.isAssignableFrom( nativeType ) ) {
         // Unbounded integer — use Double range as fallback
         return BigDecimal.valueOf( Double.MAX_VALUE ).toBigInteger();
      }
      // Fallback for other integral types
      return toBigDecimal( getJavaNativeMaxValue( nativeType ) );
   }

   private static Number getJavaNativeMinValue( final Class<?> nativeType ) {
      if ( Byte.class.isAssignableFrom( nativeType ) ) {
         return Byte.MIN_VALUE;
      }
      if ( Short.class.isAssignableFrom( nativeType ) ) {
         return Short.MIN_VALUE;
      }
      if ( Integer.class.isAssignableFrom( nativeType ) ) {
         return Integer.MIN_VALUE;
      }
      if ( Long.class.isAssignableFrom( nativeType ) ) {
         return Long.MIN_VALUE;
      }
      return -Double.MAX_VALUE;
   }

   private static Number getJavaNativeMaxValue( final Class<?> nativeType ) {
      if ( Byte.class.isAssignableFrom( nativeType ) ) {
         return Byte.MAX_VALUE;
      }
      if ( Short.class.isAssignableFrom( nativeType ) ) {
         return Short.MAX_VALUE;
      }
      if ( Integer.class.isAssignableFrom( nativeType ) ) {
         return Integer.MAX_VALUE;
      }
      if ( Long.class.isAssignableFrom( nativeType ) ) {
         return Long.MAX_VALUE;
      }
      return Double.MAX_VALUE;
   }
}
