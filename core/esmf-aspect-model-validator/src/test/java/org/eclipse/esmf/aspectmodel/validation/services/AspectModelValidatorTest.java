/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.aspectmodel.MetaModelVersionException;
import org.eclipse.esmf.aspectmodel.ProjectInfo;
import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinCountViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.validation.CycleViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.MetaModelVersionViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestProperty;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.vavr.control.Either;

class AspectModelValidatorTest {
   AspectModelValidator validator = new AspectModelValidator();

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testValidateTestAspectModel( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final ViolationReport report = validator.validateModel( aspectModel );
      final List<Violation> violations = report.violations();
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( value = TestProperty.class )
   void testValidateProperty( final TestProperty testProperty ) {
      final AspectModel aspectModel = TestResources.load( testProperty );
      final ViolationReport report = validator.validateModel( aspectModel );
      final List<Violation> violations = report.violations();
      assertThat( violations ).isEmpty();
   }

   @Test
   void testValidateBlankStructuredValueWithInvalidElementsProperty() {
      final AspectModel aspectModel = TestResources.load( InvalidTestAspect.ASPECT_WITH_INVALID_BLANK_STRUCTURED_VALUE );
      final ViolationReport report = new AspectModelValidator().validateModel( aspectModel );
      final List<Violation> violations = report.violations();
      assertThat( violations ).hasExactlyElementsOfTypes( MinCountViolation.class );
   }

   @ParameterizedTest
   @EnumSource( value = InvalidTestAspect.class )
   void testValidateInvalidTestAspectModelLoadWithValidation( final InvalidTestAspect testModel ) {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation( testModel, validator );
      assertThat( result.isLeft() )
            .describedAs( "Validation result should be a list of violations" )
            .isTrue();
      final List<Violation> violations = result.getLeft().violations();
      assertThat( violations ).isNotEmpty();
      violations.forEach( violation -> {
         // Make sure the violation does not indicate that the test model can't be loaded
         assertThat( violation.message() ).doesNotContain( "inputStream" );
      } );
   }

   @ParameterizedTest
   @EnumSource( value = InvalidTestAspect.class )
   void testValidateInvalidTestAspectModelLoadThenValidate( final InvalidTestAspect testModel ) {
      final Either<ViolationReport, AspectModel> result = validator.loadModel( () -> TestResources.load( testModel ) )
            .flatMap( aspectModel -> {
               final ViolationReport violations = validator.validateModel( aspectModel );
               return violations.isEmpty() ? Either.right( aspectModel ) : Either.left( violations );
            } );
      final List<Violation> violations = result.getLeft().violations();
      assertThat( violations ).isNotEmpty();
      violations.forEach( violation -> {
         // Make sure the violation does not indicate that the test model can't be loaded
         assertThat( violation.message() ).doesNotContain( "inputStream" );
      } );
   }

   @Test
   void testValidateTermNotDefinedInDeclaredMetaModelVersion() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.TERM_NOT_IN_DECLARED_VERSION, validator );
      assertThat( result.isLeft() ).isTrue();
      assertThat( result.getLeft().violations() )
            .hasSize( 1 )
            .first()
            .satisfies( violation -> assertThat( violation ).isInstanceOfSatisfying( MetaModelVersionViolation.class,
                  metaModelVersionViolation -> {
                     assertThat( metaModelVersionViolation.code().code() ).isEqualTo( MetaModelVersionViolation.ERROR_CODE );
                     assertThat( metaModelVersionViolation.message() )
                           .contains( "meta-model:2.1.0#Value" )
                           .contains( "is not defined in SAMM 2.1.0" )
                           .contains( "introduced in 2.2.0" )
                           .contains( "at least 2.2.0" );
                  } ) );
   }

   @Test
   void testValidateEntityTermNotDefinedInDeclaredMetaModelVersion() {
      // The samm-e: namespace is checked just like samm: and samm-c:, and Quantity.ttl only exists from
      // SAMM 2.2.0 on, so the term is resolved against a version whose resource file is absent
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.TERM_NOT_IN_DECLARED_VERSION_ENTITY, validator );
      assertThat( result.isLeft() ).isTrue();
      assertThat( result.getLeft().violations() )
            .hasSize( 1 )
            .first()
            .satisfies( violation -> assertThat( violation ).isInstanceOfSatisfying( MetaModelVersionViolation.class,
                  metaModelVersionViolation -> assertThat( metaModelVersionViolation.message() )
                        .contains( "entity:2.1.0#Quantity" )
                        .contains( "is not defined in SAMM 2.1.0" )
                        .contains( "introduced in 2.2.0" ) ) );
   }

   @Test
   void testValidateMixedMetaModelVersions() {
      for ( final InvalidTestAspect testModel : List.of( InvalidTestAspect.MIXED_META_MODEL_VERSIONS_OLD_SAMM,
            InvalidTestAspect.MIXED_META_MODEL_VERSIONS_NEW_SAMM ) ) {
         final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation( testModel, validator );
         assertThat( result.isLeft() ).isTrue();
         assertThat( result.getLeft().violations() )
               .describedAs( "Expected a meta model version violation for %s", testModel )
               .allMatch( MetaModelVersionViolation.class::isInstance )
               .first()
               .satisfies( violation -> assertThat( violation.message() )
                     .contains( "All SAMM namespaces in a file must use the same meta model version" ) );
      }
   }

   @Test
   void testPlainLoadingIsRejectedOnMetaModelVersionMismatch() {
      // The check runs during migration, which every kind of load performs. A file that uses terms of a
      // newer meta model version is therefore rejected outright, not only when it is validated.
      assertThatThrownBy( () -> TestResources.load( InvalidTestAspect.TERM_NOT_IN_DECLARED_VERSION ) )
            .isInstanceOf( MetaModelVersionException.class )
            .satisfies( exception -> assertThat( ( (MetaModelVersionException) exception ).problems() )
                  .singleElement()
                  .asString()
                  .contains( "meta-model:2.1.0#Value" )
                  .contains( "introduced in 2.2.0" ) );
   }

   @Test
   void testMetaModelVersionViolationNamesTheFile() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.TERM_NOT_IN_DECLARED_VERSION, validator );
      assertThat( result.getLeft().violations() )
            .isNotEmpty().first().isInstanceOfSatisfying( MetaModelVersionViolation.class, violation -> {
               assertThat( violation.sourceDocument().toString() ).contains( "TermNotInDeclaredVersion.ttl" );
            } );
   }

   @Test
   void testMetaModelVersionViolationDoesNotReuseProcessingErrorCode() {
      // Downstream tools distinguish violations by error code rather than by type. In particular, the
      // Aspect Model Editor treats ERR_PROCESSING as a reason to refuse a model outright. This
      // violation has its own code so that tools can tell a version mismatch apart from a load failure.
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.TERM_NOT_IN_DECLARED_VERSION, validator );
      assertThat( result.getLeft().violations() )
            .isNotEmpty()
            .allSatisfy( violation -> {
               assertThat( violation.code().code() ).isEqualTo( MetaModelVersionViolation.ERROR_CODE );
               assertThat( violation.code().code() ).isNotEqualTo( ProcessingViolation.ERROR_CODE );
            } );
   }

   @Test
   void testValidateRecursiveModel() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.ASPECT_WITH_RECURSIVE_PROPERTY, validator );
      assertThat( result.isLeft() )
            .describedAs( "Validation result should be a list of violations" )
            .isTrue();
      final List<Violation> violations = result.getLeft().violations();

      assertThat( violations )
            .hasSize( 1 )
            .first()
            .satisfies( violation -> assertThat( violation ).isInstanceOfSatisfying( CycleViolation.class,
                  cycleViolation -> assertThat( cycleViolation.message() )
                        .contains( ":testProperty -> :testProperty" ) ) );
   }

   @ParameterizedTest
   @EnumSource( value = InvalidTestAspect.class )
   void testLoadWithValidation( final InvalidTestAspect testModel ) {
      final TestResources.IdentifiedInputStream input = TestResources.inputStream( testModel );
      final Either<ViolationReport, AspectModel> result = new AspectModelLoader().withValidation( validator )
            .load( input.inputStream(), input.location() );
      assertThat( result.isLeft() )
            .describedAs( "Validation result should be a list of violations" )
            .isTrue();
      assertThat( result.getLeft().violations() ).isNotEmpty();
   }

   @Test
   void testGetFixForInvalidTestAspectModel() {
      final Supplier<AspectModel> invalidAspectModel = () -> TestResources.load( InvalidTestAspect.INVALID_PREFERRED_NAME_DATATYPE );
      final List<Violation> violations = validator.validateModel( invalidAspectModel ).violations();
      assertThat( violations ).isNotEmpty();
      final DatatypeViolation violation = (DatatypeViolation) violations.getFirst();
      assertThat( violation.fixes() ).isNotEmpty();
      final Fix fix = violation.fixes().getFirst();
      assertThat( fix.description() ).isEqualTo( "Add default @en language tag to value" );
   }

   @Test
   void testValidateValidModelElement() {
      final AspectModel testModel = TestResources.load( TestAspect.ASPECT_WITH_BOOLEAN );
      final Resource element = testModel.mergedModel().createResource( TestAspect.TEST_NAMESPACE + "BooleanTestCharacteristic" );
      final List<Violation> violations = validator.validateElement( element );
      assertThat( violations ).isEmpty();
   }

   @Test
   void testValidateInvalidModelElement() {
      final AspectModel testModel = TestResources.load( InvalidTestAspect.INVALID_EXAMPLE_VALUE_DATATYPE );
      final Resource element = testModel.mergedModel().createResource( TestAspect.TEST_NAMESPACE + "stringProperty" );
      final List<Violation> violations = validator.validateElement( element );
      assertThat( violations ).hasSize( 1 );
      final SparqlConstraintViolation violation = (SparqlConstraintViolation) violations.getFirst();
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().property() ).contains( SammNs.SAMM.exampleValue() );

      final RDFNode value = violation.bindings().get( "value" );
      if ( value.isLiteral() ) {
         assertThat( value.asLiteral().getDatatypeURI() ).isEqualTo( XSD.xint.getURI() );
      } else if ( value.isResource() ) {
         assertThat( value.asResource().getURI() ).isEqualTo( XSD.xint.getURI() );
      }
   }

   @Test
   void testInvalidTurtleSyntax() {
      final Supplier<AspectModel> invalidTurtleSyntax = () -> TestResources.load( InvalidTestAspect.INVALID_SYNTAX );
      final List<Violation> violations = validator.validateModel( invalidTurtleSyntax ).violations();
      assertThat( violations ).hasSize( 1 );
      final InvalidSyntaxViolation violation = (InvalidSyntaxViolation) violations.getFirst();
      assertThat( violation.location().fromLine() ).isEqualTo( 16 );
      assertThat( violation.location().fromColumn() ).isEqualTo( 3 );
      assertThat( violation.message() ).contains( "Triples not terminated by DOT" );
   }

   @Test
   void testDocumentationLinkInDetailedMessage() {
      final Supplier<AspectModel> invalidTurtleSyntax = () -> TestResources.load( InvalidTestAspect.INVALID_SYNTAX );
      final List<Violation> violations = validator.validateModel( invalidTurtleSyntax ).violations();
      assertThat( violations.getFirst() ).isInstanceOfSatisfying( InvalidSyntaxViolation.class, violation -> {
         assertThat( violation.code().code() ).isEqualTo( InvalidSyntaxViolation.ERROR_CODE );
         assertThat( violation.code().href() ).contains( ProjectInfo.esmfErrorCodeUrl( InvalidSyntaxViolation.ERROR_CODE ) );
      } );
   }

   @Test
   void testNonTurtleFile() {
      final Supplier<AspectModel> invalidTurtleSyntax = () -> TestResources.load( InvalidTestAspect.ACTUALLY_JSON );
      final List<Violation> violations = validator.validateModel( invalidTurtleSyntax ).violations();
      assertThat( violations ).hasSize( 1 );
      final InvalidSyntaxViolation violation = (InvalidSyntaxViolation) violations.getFirst();
      assertThat( violation.location().fromLine() ).isEqualTo( 11 );
      assertThat( violation.location().fromColumn() ).isEqualTo( 0 );
      assertThat( violation.message() ).contains( "Not implemented (formulae, graph literals)" );
   }

   @Test
   void testAspectWithInvalidMetaModelVersion() {
      final Supplier<AspectModel> invalidTurtleSyntax = () -> TestResources.load( InvalidTestAspect.ASPECT_WITH_INVALID_VERSION );
      final List<Violation> violations = validator.validateModel( invalidTurtleSyntax ).violations();
      assertThat( violations )
            .hasSize( 1 )
            .first()
            .satisfies( violation -> assertThat( violation ).isInstanceOfSatisfying( ProcessingViolation.class,
                  processingViolation -> assertThat( processingViolation.message() ).contains( "is not supported" ) ) );
   }

   @Test
   void testValidationWithMultipleAspects() {
      final AspectModel model = TestResources.load( TestAspect.ASPECT );
      final AspectModel model2 = TestResources.load( TestAspect.ASPECT_WITH_SIMPLE_TYPES );

      final Model merged = ModelFactory.createDefaultModel();
      merged.add( model.mergedModel() );
      merged.add( model2.mergedModel() );
      merged.add( MetaModelFile.metaModelDefinitions() );

      final List<Violation> violations = validator.validateModel( merged ).violations();
      assertThat( violations ).isEmpty();
   }

   @Test
   void testCycleDetection() {
      final Supplier<AspectModel> versionedModel = () -> TestResources.load( InvalidTestAspect.MODEL_WITH_CYCLES );
      final List<Violation> report = validator.validateModel( versionedModel ).violations();
      assertThat( report ).hasSize( 7 );
      assertThat( report )
            .map( CycleViolation.class::cast )
            .map( violation -> violation.path().stream()
                  .map( p -> p.getModel().shortForm( p.getURI() ) )
                  .collect( Collectors.joining( " -> " ) ) )
            .containsExactly(
                  ":a -> :b -> :a",
                  ":e -> :f -> :g -> :e",
                  ":h -> :h",
                  ":h -> :i -> :h",
                  ":l -> :l",
                  ":n -> samm-e:value -> :n",
                  ":p -> :q -> :r -> :q" );
   }

   @Test
   void testCycleDetectionWithCycleBreakers() {
      final AspectModel aspectModel = TestResources.load( TestAspect.MODEL_WITH_BROKEN_CYCLES );
      final List<Violation> report = validator.validateModel( aspectModel ).violations();
      assertThat( report ).isEmpty();
   }

   @Test
   void testLoadWithValidation() {
      final Supplier<AspectModel> versionedModel = () -> TestResources.load( TestAspect.ASPECT_WITH_ENTITY );
      final Either<ViolationReport, AspectModel> model = validator.loadModel( versionedModel );
      if ( model.isLeft() ) {
         final ViolationReport violations = model.getLeft();
         final String report = new ViolationFormatter().apply( violations );
         System.out.println( report );
      }
      assertThat( model.isRight() ).isTrue();
   }

   @Test
   void testValidateInvalidLiteralValue() {
      final Either<ViolationReport, AspectModel> result = validator.loadModel( () -> TestResources.load( InvalidTestAspect.INVALID_URI ) );
      assertThat( result.isLeft() ).isTrue();
      final List<Violation> violations = result.getLeft().violations();
      assertThat( violations ).hasSize( 1 );
      assertThat( violations.getFirst().message() ).contains( "is no valid value for type" );
   }

   @Test
   void testValidateRegularExpressionExampleValueValidator() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.ASPECT_WITH_INVALID_REGEX_CONSTRAINT, validator );
      assertThat( result.isLeft() ).isTrue();
      final List<Violation> violations = result.getLeft().violations();
      assertThat( violations ).hasSize( 1 );
      assertThat( violations.getFirst().message() ).contains(
            "Regular expression on :TestRegularExpressionConstraint is invalid" );
   }

   @Test
   void testValidateFixedPointConstraintWithInvalidDatatypeInMigratedModel() {
      // The model uses an older SAMM version and is migrated during loading. Source location information
      // (TokenRegistry) must survive the migration, also for anonymous nodes such as the constraint.
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.FIXED_POINT_CONSTRAINT_WITH_INVALID_DATATYPE_OLD_META_MODEL_VERSION, validator );
      assertThat( result.isLeft() ).isTrue();
      final List<Violation> violations = result.getLeft().violations();
      assertThat( violations )
            .singleElement()
            .isInstanceOfSatisfying( SparqlConstraintViolation.class, violation -> {
               assertThat( violation.message() ).contains( "Fixed Point Constraint" ).contains( "xsd:decimal" );
               assertThat( violation.sourceDocument().toString() )
                     .contains( "FixedPointConstraintWithInvalidDatatypeOldMetaModelVersion.ttl" );
               assertThat( violation.location().fromLine() ).isEqualTo( 23 );
            } );
   }

   @Test
   void testValidateAnonymousRegularExpressionExampleValueValidator() {
      final Either<ViolationReport, AspectModel> result = TestResources.loadWithValidation(
            InvalidTestAspect.ASPECT_WITH_INVALID_ANONYMOUS_REGEX_CONSTRAINT, validator );
      assertThat( result.isLeft() ).isTrue();
      final List<Violation> violations = result.getLeft().violations();
      assertThat( violations ).hasSize( 1 );
      assertThat( violations.getFirst().message() ).contains(
            "Regular expression on anonymous element is invalid" );
   }
}
