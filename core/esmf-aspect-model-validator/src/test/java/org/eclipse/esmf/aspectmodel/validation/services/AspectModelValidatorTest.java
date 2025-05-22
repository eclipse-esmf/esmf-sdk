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

package org.eclipse.esmf.aspectmodel.validation.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestProperty;
import org.eclipse.esmf.test.TestResources;

import io.vavr.control.Either;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectModelValidatorTest {
   AspectModelValidator service = new AspectModelValidator();

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testValidateTestAspectModel( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final List<Violation> violations = service.validateModel( aspectModel );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( value = TestProperty.class )
   void testValidateProperty( final TestProperty testProperty ) {
      final AspectModel aspectModel = TestResources.load( testProperty );
      final List<Violation> violations = service.validateModel( aspectModel );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( value = InvalidTestAspect.class, mode = EnumSource.Mode.EXCLUDE,
         names = {
               "ASPECT_MISSING_NAME_AND_PROPERTIES",
               "ASPECT_MISSING_PROPERTIES",
               "MISSING_ASPECT_DECLARATION"
         } )
   void testValidateInvalidTestAspectModel( final InvalidTestAspect testModel ) {
      final Supplier<AspectModel> invalidAspectModel = () -> TestResources.load( testModel );
      final List<Violation> violations = service.validateModel( invalidAspectModel );
      assertThat( violations ).isNotEmpty();
   }

   @Test
   void testGetFixForInvalidTestAspectModel() {
      final Supplier<AspectModel> invalidAspectModel = () -> TestResources.load( InvalidTestAspect.INVALID_PREFERRED_NAME_DATATYPE );
      final List<Violation> violations = service.validateModel( invalidAspectModel );
      assertThat( violations ).isNotEmpty();
      final DatatypeViolation violation = (DatatypeViolation) violations.get( 0 );
      assertThat( violation.fixes() ).isNotEmpty();
      final Fix fix = violation.fixes().get( 0 );
      assertThat( fix.description() ).isEqualTo( "Add default @en language tag to value" );
   }

   @Test
   void testValidateValidModelElement() {
      final AspectModel testModel = TestResources.load( TestAspect.ASPECT_WITH_BOOLEAN );
      final Resource element = testModel.mergedModel().createResource( TestAspect.TEST_NAMESPACE + "BooleanTestCharacteristic" );
      final List<Violation> violations = service.validateElement( element );
      assertThat( violations ).isEmpty();
   }

   @Test
   void testValidateInvalidModelElement() {
      final AspectModel testModel = TestResources.load( InvalidTestAspect.INVALID_EXAMPLE_VALUE_DATATYPE );
      final Resource element = testModel.mergedModel().createResource( TestAspect.TEST_NAMESPACE + "stringProperty" );
      final List<Violation> violations = service.validateElement( element );
      assertThat( violations ).hasSize( 1 );
      final SparqlConstraintViolation violation = (SparqlConstraintViolation) violations.get( 0 );
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().property() ).contains( SammNs.SAMM.exampleValue() );
      assertThat( violation.bindings().get( "value" ).asResource().getURI() ).isEqualTo( XSD.xint.getURI() );
   }

   @Test
   void testInvalidTurtleSyntax() {
      final Supplier<AspectModel> invalidTurtleSyntax = () -> TestResources.load( InvalidTestAspect.INVALID_SYNTAX );
      final List<Violation> violations = service.validateModel( invalidTurtleSyntax );
      assertThat( violations ).hasSize( 1 );
      final InvalidSyntaxViolation violation = (InvalidSyntaxViolation) violations.get( 0 );
      assertThat( violation.line() ).isEqualTo( 17 );
      assertThat( violation.column() ).isEqualTo( 2 );
      assertThat( violation.message() ).contains( "Triples not terminated by DOT" );
   }

   @Test
   void testNonTurtleFile() {
      final Supplier<AspectModel> invalidTurtleSyntax = () -> TestResources.load( InvalidTestAspect.ACTUALLY_JSON );
      final List<Violation> violations = service.validateModel( invalidTurtleSyntax );
      assertThat( violations ).hasSize( 1 );
      final InvalidSyntaxViolation violation = (InvalidSyntaxViolation) violations.get( 0 );
      assertThat( violation.line() ).isEqualTo( 12 );
      assertThat( violation.column() ).isEqualTo( 1 );
      assertThat( violation.message() ).contains( "Not implemented (formulae, graph literals)" );
   }

   /**
    * Verify that validation of the given aspect model containing a unit not specified in the unit catalog
    * and referred with samm namespace fails.
    */
   @Test
   void testAspectWithSammNamespaceForCustomUnit() {
      final Supplier<AspectModel> invalidAspectModel = () -> TestResources
            .load( InvalidTestAspect.ASPECT_WITH_SAMM_NAMESPACE_FOR_CUSTOM_UNIT );

      final List<Violation> errors = service.validateModel( invalidAspectModel );
      assertThat( errors ).hasSize( 1 );
      assertThat( errors.get( 0 ) ).isOfAnyClassIn( ProcessingViolation.class );
   }

   @Test
   void testAspectWithInvalidMetaModelVersion() {
      final Supplier<AspectModel> invalidTurtleSyntax = () -> TestResources.load( InvalidTestAspect.ASPECT_WITH_INVALID_VERSION );
      final List<Violation> violations = service.validateModel( invalidTurtleSyntax );
      assertThat( violations ).hasSize( 1 );
      final ProcessingViolation violation = (ProcessingViolation) violations.get( 0 );
      assertThat( violation.message() ).contains( "is not supported" );
   }

   @Test
   void testEmptyModel() {
      final Supplier<AspectModel> missingAspect = () -> TestResources.load( InvalidTestAspect.MISSING_ASPECT_DECLARATION );
      final List<Violation> violations = service.validateModel( missingAspect );
      assertThat( violations ).isEmpty();
   }

   @Test
   void testValidationWithMultipleAspects() {
      final AspectModel model = TestResources.load( TestAspect.ASPECT );
      final AspectModel model2 = TestResources.load( TestAspect.ASPECT_WITH_SIMPLE_TYPES );

      final Model merged = ModelFactory.createDefaultModel();
      merged.add( model.mergedModel() );
      merged.add( model2.mergedModel() );
      merged.add( MetaModelFile.metaModelDefinitions() );

      final List<Violation> violations = service.validateModel( merged );
      assertThat( violations ).isEmpty();
   }

   @Test
   void testCycleDetection() {
      final Supplier<AspectModel> versionedModel = () -> TestResources.load( InvalidTestAspect.MODEL_WITH_CYCLES );
      final List<Violation> report = service.validateModel( versionedModel );
      assertThat( report ).hasSize( 7 );
      assertThat( report ).containsAll( cycles(
            ":a -> :b -> :a",
            ":e -> :f -> :g -> :e",
            ":h -> :h",
            ":h -> :i -> :h",
            ":l -> :l",
            ":n -> :NTimeSeriesEntity|samm-e:value -> :n",
            ":p -> :q -> :r -> :q" ) );
   }

   @Test
   void testCycleDetectionWithCycleBreakers() {
      final AspectModel aspectModel = TestResources.load( TestAspect.MODEL_WITH_BROKEN_CYCLES );
      final List<Violation> report = service.validateModel( aspectModel );
      assertThat( report ).isEmpty();
   }

   @Test
   void testLoadWithValidation() {
      final Supplier<AspectModel> versionedModel = () -> TestResources.load( TestAspect.ASPECT_WITH_ENTITY );
      final Either<List<Violation>, AspectModel> model = service.loadModel( versionedModel );
      if ( model.isLeft() ) {
         final List<Violation> violations = model.getLeft();
         final String report = new DetailedViolationFormatter().apply( violations );
         System.out.println( report );
      }
      assertThat( model.isRight() ).isTrue();
   }

   @Test
   void testValidateInvalidLiteralValue() {
      final Either<List<Violation>, AspectModel> result = service.loadModel( () -> TestResources.load( InvalidTestAspect.INVALID_URI ) );
      assertThat( result.isLeft() ).isTrue();
      final List<Violation> violations = result.getLeft();
      assertThat( violations ).hasSize( 1 );
      assertThat( violations.getFirst().message() ).contains( "is no valid value for type" );
   }

   private List<Violation> cycles( final String... cycles ) {
      final List<Violation> errors = new ArrayList<>();
      Arrays.stream( cycles ).forEach(
            cycle -> errors.add( new ProcessingViolation( String.format( ModelCycleDetector.ERR_CYCLE_DETECTED, cycle ), null ) ) );
      return errors;
   }
}
