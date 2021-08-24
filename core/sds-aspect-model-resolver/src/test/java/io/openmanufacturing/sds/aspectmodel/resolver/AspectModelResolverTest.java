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

package io.openmanufacturing.sds.aspectmodel.resolver;

import static org.assertj.vavr.api.VavrAssertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Streams;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.vavr.control.Try;

public class AspectModelResolverTest extends MetaModelVersions {
   private final AspectModelResolver resolver = new AspectModelResolver();

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLoadDataModelExpectSuccess( final KnownVersion metaModelVersion ) {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                                         .getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing.test:1.0.0#Test" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isSuccess();

      final Resource aspect = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#Test" );
      final Resource bammAspect = ResourceFactory
            .createResource(
                  "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                        + "#Aspect" );
      org.assertj.core.api.Assertions
            .assertThat( result.get().getModel().listStatements( aspect, RDF.type, bammAspect ).nextOptional() )
            .isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLoadModelWithVersionEqualToUnsupportedMetaModelVersionExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                                         .getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing.test:1.1.0#Test" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isSuccess();

      final Resource aspect = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.1.0#Test" );
      final Resource bammAspect = ResourceFactory
            .createResource(
                  "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                        + "#Aspect" );
      org.assertj.core.api.Assertions
            .assertThat( result.get().getModel().listStatements( aspect, RDF.type, bammAspect ).nextOptional() )
            .isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolveReferencedModelFromMemoryExpectSuccess( final KnownVersion metaModelVersion ) {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final AspectModelUrn inputUrn = AspectModelUrn
            .fromUrn( "urn:bamm:io.openmanufacturing.test:1.0.0#AnotherTest" );
      final Model model = TurtleLoader.loadTurtle(
            AspectModelResolverTest.class.getResourceAsStream(
                  "/" + metaModelVersion.toString().toLowerCase()
                        + "/io.openmanufacturing.test/1.0.0/Test.ttl" ) ).get();
      final ResolutionStrategy inMemoryStrategy = anyUrn -> Try.success( model );
      final EitherStrategy inMemoryResolutionStrategy = new EitherStrategy( urnStrategy, inMemoryStrategy );

      final Try<VersionedModel> result = resolver.resolveAspectModel( inMemoryResolutionStrategy, inputUrn );
      assertThat( result ).isSuccess();

      final Resource aspect = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#AnotherTest" );
      final Resource bammAspect = ResourceFactory
            .createResource(
                  "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                        + "#Aspect" );
      org.assertj.core.api.Assertions
            .assertThat( result.get().getModel().listStatements( aspect, RDF.type, bammAspect ).nextOptional() )
            .isNotEmpty();

      final Resource propertyFromReferencedAspect = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#foo" );
      final Resource bammProperty = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                  + "#Property" );
      org.assertj.core.api.Assertions
            .assertThat(
                  result.get().getModel().listStatements( propertyFromReferencedAspect, RDF.type, bammProperty )
                        .nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolveReferencedModelExpectSuccess( final KnownVersion metaModelVersion ) {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( "urn:bamm:io.openmanufacturing.test:aspect-model:AnotherTest:1.0.0" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );

      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isSuccess();

      final Resource aspect = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#AnotherTest" );
      final Resource bammAspect = ResourceFactory
            .createResource(
                  "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                        + "#Aspect" );
      org.assertj.core.api.Assertions
            .assertThat( result.get().getModel().listStatements( aspect, RDF.type, bammAspect ).nextOptional() )
            .isNotEmpty();

      final Resource propertyFromReferencedAspect = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#foo" );
      final Resource bammProperty = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                  + "#Property" );
      org.assertj.core.api.Assertions
            .assertThat(
                  result.get().getModel().listStatements( propertyFromReferencedAspect, RDF.type, bammProperty )
                        .nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolutionMissingAspectExpectFailure( final KnownVersion metaModelVersion ) {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                                         .getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( "urn:bamm:io.openmanufacturing.test:1.0.0#AnotherFailingTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isFailure();
      assertThat( result ).failBecauseOf( ModelResolutionException.class );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolutionMissingModelElementExpectFailure( final KnownVersion metaModelVersion ) throws Throwable {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( "urn:bamm:io.openmanufacturing.test:1.0.0#FailingTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isFailure();
      assertThat( result ).failBecauseOf( ModelResolutionException.class );
      org.assertj.core.api.Assertions
            .assertThat( result.getCause().getCause() ).isExactlyInstanceOf( FileNotFoundException.class );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolutionReferencedCharacteristicExpectSuccess( final KnownVersion metaModelVersion ) {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( "urn:bamm:io.openmanufacturing.test:1.0.0#ReferenceCharacteristicTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy(
            aspectModelsRootDirectory.toPath() );

      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isSuccess();

      final Resource aspect = ResourceFactory
            .createResource(
                  "urn:bamm:io.openmanufacturing.test:1.0.0#ReferenceCharacteristicTest" );
      final Resource bammAspect = ResourceFactory
            .createResource(
                  "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                        + "#Aspect" );
      org.assertj.core.api.Assertions
            .assertThat( result.get().getModel().listStatements( aspect, RDF.type, bammAspect ).nextOptional() )
            .isNotEmpty();

      final Resource referencedCharacteristic = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#TestCharacteristic" );
      final Resource bammCharacteristic = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                  + "#Characteristic" );
      org.assertj.core.api.Assertions
            .assertThat(
                  result.get().getModel().listStatements( referencedCharacteristic, RDF.type, bammCharacteristic )
                        .nextOptional() ).isNotEmpty();
   }

   /**
    * This test checks that if the same shared resource (in this case: the shared TestCharacteristic) is
    * transitively imported on multiple paths through the dependency graph, it is still only added once to the
    * final merged model, so for example the Statement x:testCharacteristic bamm:name "testCharacteristic" is
    * only present once in the model. Here, TransitiveReferenceTest references both the Test Characteristic
    * and a second Aspect model, ReferenceCharacteristicTest, which in turn also references the Test Characteristic.
    *
    * @throws Throwable if one of the resources is not found
    */
   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testTransitiveReferenceExpectSuccess( final KnownVersion metaModelVersion ) throws Throwable {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( "urn:bamm:io.openmanufacturing.test:1.0.0#TransitiveReferenceTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isSuccess();

      final Model model = result.get().getModel();
      final Resource testCharacteristic = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#TestCharacteristic" );
      final BAMM bamm = new BAMM( metaModelVersion );
      org.assertj.core.api.Assertions.assertThat(
            Streams.stream( model.listStatements( testCharacteristic, bamm.name(), (RDFNode) null ) ).count() )
                                     .isEqualTo( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolutionReferencedEntity( final KnownVersion metaModelVersion ) {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( "urn:bamm:io.openmanufacturing.test:1.0.0#ReferenceEntityTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy(
            aspectModelsRootDirectory.toPath() );

      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isSuccess();

      final Resource aspect = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#ReferenceEntityTest" );
      final Resource bammAspect = ResourceFactory
            .createResource(
                  "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                        + "#Aspect" );
      org.assertj.core.api.Assertions
            .assertThat( result.get().getModel().listStatements( aspect, RDF.type, bammAspect ).nextOptional() )
            .isNotEmpty();

      final Resource referencedEntity = ResourceFactory
            .createResource( "urn:bamm:io.openmanufacturing.test:1.0.0#TestEntity" );
      final Resource bammEntity = ResourceFactory
            .createResource(
                  "urn:bamm:io.openmanufacturing:meta-model:" + metaModelVersion.toVersionString()
                        + "#Entity" );
      org.assertj.core.api.Assertions
            .assertThat(
                  result.get().getModel().listStatements( referencedEntity, RDF.type, bammEntity )
                        .nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectReferencingAnotherAspectExpectSuccess( final KnownVersion metaModelVersion ) {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                                         .getPath() );

      final String aspectUrn = "urn:bamm:io.openmanufacturing.test:2.0.0#Test";
      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( aspectUrn );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result ).isSuccess();

      final Model model = result.get().getModel();
      final BAMM bamm = new BAMM( metaModelVersion );
      org.assertj.core.api.Assertions.assertThat(
            Streams.stream( model.listStatements( null, RDF.type, bamm.Aspect() ) ).count() ).isEqualTo( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testClassPathResolution( final KnownVersion metaModelVersion ) {
      final String aspectUrn = "urn:bamm:io.openmanufacturing.test:1.0.0#Test";
      final AspectModelResolver resolver = new AspectModelResolver();
      final ClasspathStrategy strategy = new ClasspathStrategy( metaModelVersion.toString().toLowerCase() );
      final Try<VersionedModel> result = resolver
            .resolveAspectModel( strategy, AspectModelUrn.fromUrn( aspectUrn ) );
      Assertions.assertThat( result.isSuccess() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolveAspectContainingRefinedProperty( final KnownVersion metaModelVersion ) {
      final String aspectUrn = "urn:bamm:io.openmanufacturing.test:1.0.0#TestPropertyRefiningPropertyWithoutCharacteristic";
      final AspectModelResolver resolver = new AspectModelResolver();
      final ClasspathStrategy strategy = new ClasspathStrategy( metaModelVersion.toString().toLowerCase() );
      final Try<VersionedModel> result = resolver
            .resolveAspectModel( strategy, AspectModelUrn.fromUrn( aspectUrn ) );
      Assertions.assertThat( result.isSuccess() ).describedAs( "Resolution of refined Property failed." ).isTrue();
   }
}
