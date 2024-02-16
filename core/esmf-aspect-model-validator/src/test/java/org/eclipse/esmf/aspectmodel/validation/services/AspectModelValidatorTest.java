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
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;
import org.eclipse.esmf.test.TestProperty;
import org.eclipse.esmf.test.TestResources;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class AspectModelValidatorTest extends MetaModelVersions {
   // One specific validator instance for each meta model version
   private final Map<KnownVersion, AspectModelValidator> service = Arrays.stream( KnownVersion.values() )
         .collect( Collectors.toMap( Function.identity(), AspectModelValidator::new ) );

   @Test
   void testValidAspect() {
      final Try<VersionedModel> validAspectModel = TestResources.getModel( TestAspect.ASPECT, KnownVersion.getLatest() );
      final List<Violation> violations = service.get( KnownVersion.getLatest() ).validateModel( validAspectModel );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testValidateTestAspectModel( final TestAspect testAspect ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final Try<VersionedModel> tryModel = TestResources.getModel( testAspect, metaModelVersion );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( tryModel );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( value = TestProperty.class )
   void testValidateProperty( final TestProperty testProperty ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final Try<VersionedModel> tryModel = TestResources.getModel( testProperty, metaModelVersion );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( tryModel );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( "invalidTestModels" )
   void testValidateInvalidTestAspectModel( final InvalidTestAspect testModel ) {
      assertThatCode( () -> {
         final Try<VersionedModel> invalidAspectModel = TestResources.getModel( testModel, KnownVersion.SAMM_2_1_0 );
         final List<Violation> violations = service.get( KnownVersion.SAMM_2_1_0 ).validateModel( invalidAspectModel );
         assertThat( violations ).isNotEmpty();
      } ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @MethodSource( "latestVersion" )
   void testGetFixForInvalidTestAspectModel( final KnownVersion metaModelVersion ) {
      final TestModel testModel = InvalidTestAspect.INVALID_PREFERRED_NAME_DATATYPE;
      final Try<VersionedModel> invalidAspectModel = TestResources.getModel( testModel, metaModelVersion );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidAspectModel );
      assertThat( violations ).isNotEmpty();
      final DatatypeViolation violation = (DatatypeViolation) violations.get( 0 );
      assertThat( violation.fixes() ).isNotEmpty();
      final Fix fix = violation.fixes().get( 0 );
      assertThat( fix.description() ).isEqualTo( "Add default @en language tag to value" );
   }

   private static Stream<Arguments> invalidTestModels() {
      return Arrays.stream( InvalidTestAspect.values() )
            .filter( invalidTestAspect ->
                  (!invalidTestAspect.equals( InvalidTestAspect.ASPECT_MISSING_NAME_AND_PROPERTIES )
                        && !invalidTestAspect.equals( InvalidTestAspect.ASPECT_MISSING_PROPERTIES )) )
            .map( Arguments::of );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   void testValidateValidModelElement( final KnownVersion metaModelVersion ) {
      final VersionedModel testModel = TestResources.getModel( TestAspect.ASPECT_WITH_BOOLEAN, metaModelVersion ).get();
      final Resource element = testModel.getModel().createResource( TestAspect.TEST_NAMESPACE + "BooleanTestCharacteristic" );
      final List<Violation> violations = service.get( metaModelVersion ).validateElement( element );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "latestVersion" )
   void testValidateInvalidModelElement( final KnownVersion metaModelVersion ) {
      final VersionedModel testModel = TestResources.getModel( InvalidTestAspect.INVALID_EXAMPLE_VALUE_DATATYPE, metaModelVersion ).get();
      final Resource element = testModel.getModel().createResource( TestAspect.TEST_NAMESPACE + "stringProperty" );
      final List<Violation> violations = service.get( metaModelVersion ).validateElement( element );
      assertThat( violations ).hasSize( 1 );
      final SparqlConstraintViolation violation = (SparqlConstraintViolation) violations.get( 0 );
      final SAMM samm = new SAMM( metaModelVersion );
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().property() ).contains( samm.exampleValue() );
      assertThat( violation.bindings().get( "value" ).asResource().getURI() ).isEqualTo( XSD.xint.getURI() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testInvalidTurtleSyntax( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources.getModel( InvalidTestAspect.INVALID_SYNTAX, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isTrue();
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidTurtleSyntax );
      assertThat( violations ).hasSize( 1 );
      final InvalidSyntaxViolation violation = (InvalidSyntaxViolation) violations.get( 0 );
      assertThat( violation.line() ).isEqualTo( 17 );
      assertThat( violation.column() ).isEqualTo( 2 );
      assertThat( violation.message() ).contains( "Triples not terminated by DOT" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testNonTurtleFile( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources.getModel( InvalidTestAspect.ACTUALLY_JSON, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isTrue();
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidTurtleSyntax );
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
   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testAspectWithSammNamespaceForCustomUnit( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidAspectModel = TestResources
            .getModel( InvalidTestAspect.ASPECT_WITH_SAMM_NAMESPACE_FOR_CUSTOM_UNIT, metaModelVersion );

      final List<Violation> errors = service.get( metaModelVersion ).validateModel( invalidAspectModel );
      assertThat( errors ).hasSize( 1 );
      assertThat( errors.get( 0 ) ).isOfAnyClassIn( ProcessingViolation.class );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testAspectWithInvalidMetaModelVersion( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources.getModel( InvalidTestAspect.ASPECT_WITH_INVALID_VERSION,
            metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isTrue();
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidTurtleSyntax );
      assertThat( violations ).hasSize( 1 );
      final ProcessingViolation violation = (ProcessingViolation) violations.get( 0 );
      assertThat( violation.message() ).contains( "does not contain element definition" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testMissingAspectDeclaration( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> missingAspect = TestResources
            .getModel( InvalidTestAspect.MISSING_ASPECT_DECLARATION, metaModelVersion );
      assertThat( missingAspect.isFailure() ).isTrue();
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( missingAspect );
      assertThat( violations ).hasSize( 1 );
      final ProcessingViolation violation = (ProcessingViolation) violations.get( 0 );
      assertThat( violation.message() ).contains( "does not contain element definition" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testValidationWithMultipleAspects( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> model = TestResources.getModel( TestAspect.ASPECT, metaModelVersion );
      model.forEach( versionedModel -> {
         final VersionedModel model2 = TestResources.getModel( TestAspect.ASPECT_WITH_SIMPLE_TYPES, metaModelVersion ).get();
         versionedModel.getModel().add( model2.getRawModel() );
         versionedModel.getRawModel().add( model2.getRawModel() );
      } );

      final List<Violation> violations = service.get( metaModelVersion ).validateModel( model );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testCycleDetection( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> versionedModel = TestResources.getModel( InvalidTestAspect.MODEL_WITH_CYCLES, metaModelVersion );
      final List<Violation> report = service.get( metaModelVersion ).validateModel( versionedModel );
      assertThat( report ).hasSize( 7 );
      assertThat( report ).containsAll( cycles(
            ":a -> :b -> :a",
            ":e -> :f -> :g -> :e",
            ":h -> :h",
            ":h -> :i -> :h",
            ":l -> :l",
            // TimeSeries are handled differently between v1 and v2 meta models.
            metaModelVersion.isOlderThan( KnownVersion.SAMM_2_0_0 )
                  ? ":n -> :refinedValue -> :n"
                  : ":n -> :NTimeSeriesEntity|samm-e:value -> :n",
            ":p -> :q -> :r -> :q" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testCycleDetectionWithCycleBreakers( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> versionedModel = TestResources.getModel( TestAspect.MODEL_WITH_BROKEN_CYCLES, metaModelVersion );
      final List<Violation> report = service.get( metaModelVersion ).validateModel( versionedModel );
      assertThat( report ).isEmpty();
   }

   private List<Violation> cycles( final String... cycles ) {
      final List<Violation> errors = new ArrayList<>();
      Arrays.stream( cycles ).forEach(
            cycle -> errors.add( new ProcessingViolation( String.format( ModelCycleDetector.ERR_CYCLE_DETECTED, cycle ), null ) ) );
      return errors;
   }
}
