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

package org.eclipse.esmf.aspectmodel.validation.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.aspectmodel.shacl.violation.ShaclViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.vavr.control.Either;

class ViolationFormatterTest {
   private final ViolationFormatter formatter = new ViolationFormatter();
   private final AspectModelValidator validator = new AspectModelValidator();

   @Test
   void testValidModelReturnsNoViolations() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            TestAspect.ASPECT_WITH_PROPERTY, validator );
      assertThat( result.isRight() ).isTrue();

      final ViolationReport emptyReport = new ViolationReport( List.of() );
      final String output = formatter.apply( emptyReport );
      assertThat( output ).contains( "Input model is valid" );
   }

   @Test
   void testInvalidSyntaxViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_SYNTAX, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).contains( InvalidSyntaxViolation.ERROR_CODE );
      assertThat( output ).contains( "testmodel:invalid/" );
   }

   @Test
   void testInvalidCharacteristicDatatypeViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_CHARACTERISTIC_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).contains( "testmodel:invalid/" );
      // Should contain error highlighting with ^ characters
      assertThat( output ).containsPattern( "\\^+" );
   }

   @Test
   void testRecursivePropertyViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.ASPECT_WITH_RECURSIVE_PROPERTY, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).contains( "testProperty" );
      assertThat( output ).containsPattern( "\\^+" );
   }

   @Test
   void testInvalidRegexConstraintViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.ASPECT_WITH_INVALID_REGEX_CONSTRAINT, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).contains( "testmodel:invalid/" );
   }

   @Test
   void testInvalidExampleValueDatatypeViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_EXAMPLE_VALUE_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).containsPattern( "\\^+" );
   }

   @Test
   void testInvalidUriViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_URI, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).contains( "testmodel:invalid/" );
   }

   @Test
   void testModelWithCyclesViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.MODEL_WITH_CYCLES, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "Validation errors were found" );
   }

   @Test
   void testFormattingSingleViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_SYNTAX, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      assertThat( report.violations() ).isNotEmpty();

      final Violation singleViolation = report.violations().getFirst();
      final String output = formatter.apply( singleViolation );

      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).isNotEmpty();
   }

   @Test
   void testFormattingMultipleViolationsGroupedByFile() {
      final AspectModelFile rawFile1 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).files().getFirst();
      final AspectModelFile file1 = RawAspectModelFileBuilder.builder()
            .sourceUri( URI.create( rawFile1.sourceUri() + "-first" ) )
            .sourceModel( rawFile1.sourceModel() )
            .headerComment( rawFile1.headerComment() )
            .build();

      final AspectModelFile rawFile2 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).files().getFirst();
      final AspectModelFile file2 = RawAspectModelFileBuilder.builder()
            .sourceUri( URI.create( rawFile2.sourceUri() + "-second" ) )
            .sourceModel( rawFile2.sourceModel() )
            .headerComment( rawFile2.headerComment() )
            .build();

      final Either<ViolationReport, AspectModel> result = new AspectModelLoader()
            .withValidation( validator )
            .loadAspectModelFiles( List.of( file1, file2 ) );

      assertThat( result.isLeft() ).isTrue();
      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).contains( "Duplicate definition" );
   }

   @Test
   void testViolationReportWithShaclViolations() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_PREFERRED_NAME_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      assertThat( report.violations() ).isNotEmpty();

      // Verify that at least one ShaclViolation exists
      final boolean hasShaclViolation = report.violations().stream()
            .anyMatch( ShaclViolation.class::isInstance );
      assertThat( hasShaclViolation ).isTrue();

      final String output = formatter.apply( report );
      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).isNotEmpty();
   }

   @ParameterizedTest
   @EnumSource( value = InvalidTestAspect.class,
      mode = EnumSource.Mode.INCLUDE,
      names = {
            "INVALID_SYNTAX",
            "ASPECT_WITH_INVALID_REGEX_CONSTRAINT",
            "ASPECT_WITH_RECURSIVE_PROPERTY",
            "INVALID_CHARACTERISTIC_DATATYPE",
            "INVALID_EXAMPLE_VALUE_DATATYPE",
            "MODEL_WITH_CYCLES"
      } )
   void testFormattingVariousInvalidModels( final InvalidTestAspect testModel ) {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation( testModel, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).isNotEmpty();
      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).doesNotContain( "Input model is valid" );
   }

   @Test
   void testViolationFormatterOutputContainsFileSection() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_SYNTAX, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      // Should contain file section marker
      assertThat( output ).containsPattern( "> In .+:" );
   }

   @Test
   void testEmptyViolationReport() {
      final ViolationReport emptyReport = new ViolationReport( List.of() );
      final String output = formatter.apply( emptyReport );

      assertThat( output ).contains( "Input model is valid" );
      assertThat( output ).doesNotContain( "Validation errors were found" );
   }

   @Test
   void testViolationFormatterOutputStructure() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_CHARACTERISTIC_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      // Verify output structure
      assertThat( output ).contains( "Validation errors were found" );
      assertThat( output ).contains( "testmodel:invalid/" );
      // Should contain line markers (e.g., "12 |")
      assertThat( output ).containsPattern( "\\d+ \\|" );
   }
}
