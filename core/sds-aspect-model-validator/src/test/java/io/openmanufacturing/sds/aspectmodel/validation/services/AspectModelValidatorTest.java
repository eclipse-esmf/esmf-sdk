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

package io.openmanufacturing.sds.aspectmodel.validation.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.topbraid.shacl.vocabulary.SH;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationError;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.test.InvalidTestAspect;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;
import io.openmanufacturing.sds.test.TestResources;
import io.vavr.control.Try;

public class AspectModelValidatorTest extends MetaModelVersions {
   private static final String MISSING_PROPERTY_MESSAGE = "Property needs to have at least 1 values, but found 0";

   private final AspectModelValidator service = new AspectModelValidator();

   private String getMetaModelUrn( final KnownVersion version ) {
      return "urn:bamm:io.openmanufacturing:meta-model:" + version.toVersionString() + "#";
   }

   private String getNameUrn( final KnownVersion version ) {
      return getMetaModelUrn( version ) + "name";
   }

   private String getPropertiesUrn( final KnownVersion version ) {
      return getMetaModelUrn( version ) + "properties";
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidAspect( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> validAspectModel = TestResources.getModel( TestAspect.ASPECT, metaModelVersion );
      final ValidationReport result = service.validate( validAspectModel );
      assertThat( result.conforms() ).isTrue();
      assertThat( result.getValidationErrors() ).hasSize( 0 );
   }

   /**
    * Test for a model that passes validation via BAMM's SHACL shapes but is actually invalid and can not be loaded using
    * {@link io.openmanufacturing.sds.metamodel.loader.AspectModelLoader#fromVersionedModel(VersionedModel)}.
    * This method and the corresponding test model should be removed once
    * <a href="https://github.com/OpenManufacturingPlatform/sds-bamm-aspect-meta-model/issues/173">BAMM-173</a> has been addressed
    * @param metaModelVersion the meta model version
    */
   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testFalsePositiveValidation( final KnownVersion metaModelVersion ) {
      final TestModel testModel = InvalidTestAspect.ASPECT_WITH_FALSE_POSITIVE_VALIDATION;
      final Try<VersionedModel> aspectModel = TestResources.getModel( testModel, metaModelVersion );
      final ValidationReport result = service.validate( aspectModel );

      System.out.println( result.getValidationErrors().iterator().next() );
      assertThat( result.conforms() ).isFalse();
      assertThat( result.getValidationErrors().iterator().next() ).isOfAnyClassIn( ValidationError.Processing.class );
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         "ASPECT_WITH_CONSTRAINTS",
         // uses bamm-c:OPEN which is not specified, see https://github.com/OpenManufacturingPlatform/sds-bamm-aspect-meta-model/issues/174
         "ASPECT_WITH_FIXED_POINT",
         "ASPECT_WITH_FIXED_POINT_CONSTRAINT",
         "ASPECT_WITH_LANGUAGE_CONSTRAINT",
         "ASPECT_WITH_RANGE_CONSTRAINT_ON_CONSTRAINED_NUMERIC_TYPE", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_DOUBLE_VALUE", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_INTEGER_VALUE", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND_INCL_BOUND_DEFINITION", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_MIN_VALUE", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND_INCL_BOUND_DEFINITION" // uses bamm-c:OPEN
   } )
   public void testValidateTestAspectModel( final TestAspect testAspect ) {
      final Try<VersionedModel> tryModel = TestResources.getModel( testAspect, KnownVersion.getLatest() );
      final VersionedModel versionedModel = tryModel.get();

      final ValidationReport report = service.validate( tryModel );
      if ( !report.conforms() ) {
         report.getValidationErrors().forEach( System.out::println );
      }
      assertThat( report.conforms() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding1_0_0" )
   public void testInvalidAspectMissingProperties( final KnownVersion metaModelVersion ) {
      final TestModel testModel = InvalidTestAspect.ASPECT_MISSING_PROPERTIES;
      final Try<VersionedModel> validAspectModel = TestResources.getModel( testModel, metaModelVersion );
      final ValidationReport report = service.validate( validAspectModel );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();

      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            assertThat( error.getFocusNode() ).isEqualTo( testModel.getUrn().toString() );
            assertThat( error.getResultPath() ).isEqualTo( getPropertiesUrn( metaModelVersion ) );
            assertThat( error.getResultMessage() ).isEqualTo( MISSING_PROPERTY_MESSAGE );
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            fail();
            return null;
         }
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding1_0_0" )
   public void testInvalidAspectMissingNameAndProperties( final KnownVersion metaModelVersion ) {
      final TestModel testModel = InvalidTestAspect.ASPECT_MISSING_NAME_AND_PROPERTIES;
      final Try<VersionedModel> validAspectModel = TestResources.getModel( testModel, metaModelVersion );
      final ValidationReport report = service.validate( validAspectModel );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 2 );

      final ValidationError missingPropertiesFailure = new ValidationError.Semantic(
            MISSING_PROPERTY_MESSAGE, testModel.getUrn().toString(), getPropertiesUrn( metaModelVersion ),
            SH.Violation.toString(), "" );
      assertThat( errors.contains( missingPropertiesFailure ) ).isTrue();

      final ValidationError.Semantic missingNameFailure = new ValidationError.Semantic(
            MISSING_PROPERTY_MESSAGE, testModel.getUrn().toString(), getNameUrn( metaModelVersion ),
            SH.Violation.toString(), "" );
      assertThat( errors.contains( missingNameFailure ) ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testInvalidTurtleSyntax( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources
            .getModel( InvalidTestAspect.INVALID_SYNTAX, metaModelVersion );
      final ValidationReport report = service.validate( invalidTurtleSyntax );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();
      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            assertThat( error.getLineNumber() ).isEqualTo( 17 );
            assertThat( error.getColumnNumber() ).isEqualTo( 2 );
            assertThat( error.getOriginalExceptionMessage() )
                  .isEqualTo( "[line: 17, col: 2 ] Triples not terminated by DOT" );
            assertThat( error.toString() )
                  .isEqualTo( "The Aspect Model contains invalid syntax at line number 17 and column number 2." );
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            fail();
            return null;
         }
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testNonTurtleFile( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources
            .getModel( InvalidTestAspect.ACTUALLY_JSON, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isEqualTo( true );

      final ValidationReport report = service.validate( invalidTurtleSyntax );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();
      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            assertThat( error.getLineNumber() ).isEqualTo( 12 );
            assertThat( error.getColumnNumber() ).isEqualTo( 1 );
            assertThat( error.getOriginalExceptionMessage() )
                  .isEqualTo( "[line: 12, col: 1 ] Not implemented (formulae, graph literals)" );
            assertThat( error.toString() )
                  .isEqualTo( "The Aspect Model contains invalid syntax at line number 12 and column number 1." );
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            fail();
            return null;
         }
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithInvalidMetaModelVersion( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources
            .getModel( InvalidTestAspect.ASPECT_WITH_INVALID_VERSION, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isEqualTo( true );

      final ValidationReport report = service.validate( invalidTurtleSyntax );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();
      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            assertThat( error.getMessage() ).contains( ValidationError.MESSAGE_MODEL_RESOLUTION_ERROR );
            assertThat( error.getMessage() ).startsWith( "Model could not be resolved entirely" );
            return null;
         }
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMissingAspectDeclaration( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> missingAspect = TestResources
            .getModel( InvalidTestAspect.MISSING_ASPECT_DECLARATION, metaModelVersion );
      assertThat( missingAspect.isFailure() ).isEqualTo( true );

      final ValidationReport report = service.validate( missingAspect );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();
      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            assertThat( error.getMessage() ).contains( ValidationError.MESSAGE_MODEL_RESOLUTION_ERROR );
            return null;
         }
      } );
   }
}
