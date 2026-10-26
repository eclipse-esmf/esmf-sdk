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
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.vavr.control.Either;

class DetailedValidationFormatterTest {
   private final DetailedViolationFormatter formatter = new DetailedViolationFormatter();
   private final AspectModelValidator validator = new AspectModelValidator();

   @Test
   void testValidModelReturnsNoViolations() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            TestAspect.ASPECT_WITH_PROPERTY, validator );
      assertThat( result.isRight() ).isTrue();

      final ViolationReport emptyReport = new ViolationReport( List.of() );
      final String output = formatter.apply( emptyReport );
      assertThat( output ).contains( "# Input model is valid" );
   }

   @Test
   void testInvalidSyntaxViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_SYNTAX, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "testmodel:invalid/" );
      assertThat( output ).contains( "processing-failure" );
   }

   @Test
   void testInvalidCharacteristicDatatypeViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_CHARACTERISTIC_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "context:" );
      assertThat( output ).contains( "violation-type:" );
      assertThat( output ).contains( "error-code:" );
      assertThat( output ).contains( "documentation:" );
   }

   @Test
   void testRecursivePropertyViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.ASPECT_WITH_RECURSIVE_PROPERTY, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "The cycle includes the following properties: :testProperty -> :testProperty" );
   }

   @Test
   void testInvalidRegexConstraintViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.ASPECT_WITH_INVALID_REGEX_CONSTRAINT, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "- message: Regular expression on :TestRegularExpressionConstraint is invalid" );
      assertThat( output ).contains( "- location:" );
   }

   @Test
   void testInvalidExampleValueDatatypeViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_EXAMPLE_VALUE_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "violation-type:" );
      assertThat( output ).contains( "documentation:" );
   }

   @Test
   void testInvalidUriViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_URI, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "testmodel:invalid/" );
   }

   @Test
   void testModelWithCyclesViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.MODEL_WITH_CYCLES, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "The cycle includes the following properties: :a -> :b -> :a" );
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

      assertThat( output ).contains( "# Validation errors were found" );
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

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "Duplicate definition" );
   }

   @Test
   void testViolationReportWithShaclViolations() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.RANGE_CONSTRAINT_WITH_WRONG_TYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      assertThat( report.violations() ).isNotEmpty();

      // Verify that at least one ShaclViolation exists
      final boolean hasShaclViolation = report.violations().stream()
            .anyMatch( ShaclViolation.class::isInstance );
      assertThat( hasShaclViolation ).isTrue();

      final String output = formatter.apply( report );
      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "violation-type:" );
      assertThat( output ).contains( "caused-by-shape:" );
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
      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).doesNotContain( "# Input model is valid" );
   }

   @Test
   void testViolationFormatterOutputContainsFileContext() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_SYNTAX, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      // Should contain file context marker
      assertThat( output ).containsPattern( "context: '.+'" );
   }

   @Test
   void testEmptyViolationReport() {
      final ViolationReport emptyReport = new ViolationReport( List.of() );
      final String output = formatter.apply( emptyReport );

      assertThat( output ).contains( "# Input model is valid" );
      assertThat( output ).doesNotContain( "# Validation errors were found" );
   }

   @Test
   void testDetailedViolationFormatterOutputStructure() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_CHARACTERISTIC_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      // Verify YAML-like output structure
      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "context:" );
      assertThat( output ).contains( "violation-type:" );
      assertThat( output ).contains( "error-code:" );
      assertThat( output ).contains( "description:" );
   }

   @Test
   void testDetailedFormatterIncludesShapeInformation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_CHARACTERISTIC_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      // Should include shape details for SHACL violations
      assertThat( output ).contains( "caused-by-shape:" );
      assertThat( output ).containsPattern( "uri:|severity:" );
   }

   @Test
   void testDetailedFormatterHandlesMaxLengthViolation() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_PREFERRED_NAME_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String output = formatter.apply( report );

      assertThat( output ).contains( "# Validation errors were found" );
      assertThat( output ).contains( "violation-type:" );
   }

   @Test
   void testDetailedFormatterDiffersFromBasicFormatter() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.INVALID_CHARACTERISTIC_DATATYPE, validator );
      assertThat( result.isLeft() ).isTrue();

      final ViolationReport report = result.getLeft();
      final String detailedOutput = formatter.apply( report );
      final String basicOutput = new ViolationFormatter().apply( report );

      // Detailed output should contain more information
      assertThat( detailedOutput ).contains( "violation-type:" );
      assertThat( detailedOutput ).contains( "caused-by-shape:" );
      assertThat( detailedOutput.length() ).isGreaterThan( basicOutput.length() );
   }
}
