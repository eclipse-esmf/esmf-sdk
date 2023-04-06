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

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinCountViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import org.eclipse.esmf.samm.KnownVersion;

import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;
import org.eclipse.esmf.test.TestProperty;
import org.eclipse.esmf.test.TestResources;
import io.vavr.control.Try;

public class AspectModelValidatorTest extends MetaModelVersions {
   // One specific validator instance for each meta model version
   private final Map<KnownVersion, AspectModelValidator> service = Arrays.stream( KnownVersion.values() )
         .collect( Collectors.toMap( Function.identity(), AspectModelValidator::new ) );

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidAspect( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> validAspectModel = TestResources.getModel( TestAspect.ASPECT, metaModelVersion );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( validAspectModel );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         "ASPECT_WITH_FIXED_POINT",
         "ASPECT_WITH_FIXED_POINT_CONSTRAINT",
         "ASPECT_WITH_LANGUAGE_CONSTRAINT",
         "MODEL_WITH_CYCLES",
         "MODEL_WITH_BROKEN_CYCLES"// contains cycles
   } )
   public void testValidateTestAspectModel( final TestAspect testAspect ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final Try<VersionedModel> tryModel = TestResources.getModel( testAspect, metaModelVersion );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( tryModel );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( value = TestProperty.class )
   public void testValidateProperty( final TestProperty testProperty ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final Try<VersionedModel> tryModel = TestResources.getModel( testProperty, metaModelVersion );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( tryModel );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( "invalidTestModels" )
   public void testValidateInvalidTestAspectModel( final InvalidTestAspect testModel, final KnownVersion metaModelVersion ) {
      assertThatCode( () -> {
         final Try<VersionedModel> invalidAspectModel = TestResources.getModel( testModel, metaModelVersion );
         final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidAspectModel );
         assertThat( violations ).isNotEmpty();
      } ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testGetFixForInvalidTestAspectModel( final KnownVersion metaModelVersion ) {
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
            .flatMap( testModel -> KnownVersion.getVersions().stream().flatMap( metaModelVersion -> {
               // Filter the arguments: Aspects missing samm:named and/or samm:properties is only invalid in SAMM 1.0.0
               if ( metaModelVersion.isNewerThan( KnownVersion.SAMM_1_0_0 )
                     && (testModel == InvalidTestAspect.ASPECT_MISSING_NAME_AND_PROPERTIES || testModel == InvalidTestAspect.ASPECT_MISSING_PROPERTIES) ) {
                  return Stream.of();
               }
               return Stream.of( Arguments.of( testModel, metaModelVersion ) );
            } ) );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testValidateValidModelElement( final KnownVersion metaModelVersion ) {
      final VersionedModel testModel = TestResources.getModel( TestAspect.ASPECT_WITH_BOOLEAN, metaModelVersion ).get();
      final Resource element = testModel.getModel().createResource( TestAspect.TEST_NAMESPACE + "BooleanTestCharacteristic" );
      final List<Violation> violations = service.get( metaModelVersion ).validateElement( element );
      assertThat( violations ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testValidateInvalidModelElement( final KnownVersion metaModelVersion ) {
      final VersionedModel testModel = TestResources.getModel( InvalidTestAspect.INVALID_EXAMPLE_VALUE_DATATYPE, metaModelVersion ).get();
      final Resource element = testModel.getModel().createResource( TestAspect.TEST_NAMESPACE + "stringProperty" );
      final List<Violation> violations = service.get( metaModelVersion ).validateElement( element );
      assertThat( violations ).hasSize( 1 );
      final SparqlConstraintViolation violation = (SparqlConstraintViolation) violations.get( 0 );
      final SAMM samm = new SAMM( metaModelVersion );
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().property().get() ).isEqualTo( samm.exampleValue() );
      assertThat( violation.bindings().get( "value" ).asResource().getURI() ).isEqualTo( XSD.xint.getURI() );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding1_0_0" )
   public void testInvalidAspectMissingProperties( final KnownVersion metaModelVersion ) {
      final SAMM samm = new SAMM( metaModelVersion );
      final TestModel testModel = InvalidTestAspect.ASPECT_MISSING_PROPERTIES;
      final Try<VersionedModel> invalidAspectModel = TestResources.getModel( testModel, metaModelVersion );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidAspectModel );
      assertThat( violations ).hasSize( 1 );
      final Violation violation = violations.get( 0 );
      assertThat( violation ).isInstanceOf( MinCountViolation.class );
      final MinCountViolation minCountViolation = (MinCountViolation) violation;
      assertThat( minCountViolation.context().property() ).hasValue( samm.properties() );
      assertThat( minCountViolation.context().element().getURI() ).isEqualTo( testModel.getUrn().toString() );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding1_0_0" )
   public void testInvalidAspectMissingNameAndProperties( final KnownVersion metaModelVersion ) {
      final SAMM samm = new SAMM( metaModelVersion );
      final TestModel testModel = InvalidTestAspect.ASPECT_MISSING_NAME_AND_PROPERTIES;
      final Try<VersionedModel> invalidAspectModel = TestResources.getModel( testModel, metaModelVersion );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidAspectModel );
      assertThat( violations ).hasSize( 2 );

      assertThat( violations )
            .anyMatch( v -> v instanceof MinCountViolation
                  && v.context().property().map( p -> p.equals( samm.properties() ) ).orElse( false )
                  && v.context().element().getURI().equals( testModel.getUrn().toString() ) )
            .anyMatch( v -> v instanceof MinCountViolation
                  && v.context().property().map( p -> p.equals( samm.property( "name" ) ) ).orElse( false )
                  && v.context().element().getURI().equals( testModel.getUrn().toString() ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testInvalidTurtleSyntax( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources.getModel( InvalidTestAspect.INVALID_SYNTAX, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isEqualTo( true );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidTurtleSyntax );
      assertThat( violations ).hasSize( 1 );
      final InvalidSyntaxViolation violation = (InvalidSyntaxViolation) violations.get( 0 );
      assertThat( violation.line() ).isEqualTo( 17 );
      assertThat( violation.column() ).isEqualTo( 2 );
      assertThat( violation.message() ).contains( "Triples not terminated by DOT" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testNonTurtleFile( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources.getModel( InvalidTestAspect.ACTUALLY_JSON, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isEqualTo( true );
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
   public void testAspectWithSammNamespaceForCustomUnit( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidAspectModel = TestResources
            .getModel( InvalidTestAspect.ASPECT_WITH_SAMM_NAMESPACE_FOR_CUSTOM_UNIT, metaModelVersion );

      final List<Violation> errors = service.get( metaModelVersion ).validateModel( invalidAspectModel );
      assertThat( errors ).hasSize( 1 );
      assertThat( errors.get( 0 ) ).isOfAnyClassIn( ProcessingViolation.class );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithInvalidMetaModelVersion( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources.getModel( InvalidTestAspect.ASPECT_WITH_INVALID_VERSION, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isEqualTo( true );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( invalidTurtleSyntax );
      assertThat( violations ).hasSize( 1 );
      final ProcessingViolation violation = (ProcessingViolation) violations.get( 0 );
      assertThat( violation.message() ).contains( "does not contain element definition" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMissingAspectDeclaration( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> missingAspect = TestResources
            .getModel( InvalidTestAspect.MISSING_ASPECT_DECLARATION, metaModelVersion );
      assertThat( missingAspect.isFailure() ).isEqualTo( true );
      final List<Violation> violations = service.get( metaModelVersion ).validateModel( missingAspect );
      assertThat( violations ).hasSize( 1 );
      final ProcessingViolation violation = (ProcessingViolation) violations.get( 0 );
      assertThat( violation.message() ).contains( "does not contain element definition" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidationWithMultipleAspects( final KnownVersion metaModelVersion ) {
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
      final Try<VersionedModel> versionedModel = TestResources.getModel( TestAspect.MODEL_WITH_CYCLES, metaModelVersion );
      final List<Violation> report = service.get( metaModelVersion ).validateModel( versionedModel );
      assertThat( report.size() ).isEqualTo( 6 );
      assertThat( report ).containsAll( cycles(
            ":a -> :b -> :a",
            ":e -> :f -> :g -> :e",
            ":h -> :h",
            ":h -> :i -> :h",
            ":l -> :l",
            // TimeSeries are handled differently between v1 and v2 meta models.
            metaModelVersion.isOlderThan( KnownVersion.SAMM_2_0_0 ) ? ":n -> :refinedValue -> :n" : ":n -> :NTimeSeriesEntity|samm-e:value -> :n" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testCycleDetectionWithCycleBreakers( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> versionedModel = TestResources.getModel( TestAspect.MODEL_WITH_BROKEN_CYCLES, metaModelVersion );
      final List<Violation> report = service.get( metaModelVersion ).validateModel( versionedModel );
      assertThat( report.isEmpty() ).isTrue();
   }

   private List<Violation> cycles( final String... cycles ) {
      final List<Violation> errors = new ArrayList<>();
      Arrays.stream( cycles ).forEach( cycle -> errors.add( new ProcessingViolation( String.format( ModelCycleDetector.ERR_CYCLE_DETECTED, cycle ), null ) ) );
      return errors;
   }
}
