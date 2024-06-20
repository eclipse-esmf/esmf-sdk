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

package org.eclipse.esmf.aspectmodel.resolver;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.esmf.aspectmodel.resolver.modelfile.ModelFiles;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;

import com.google.common.collect.Streams;
import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class AspectModelResolverTest extends MetaModelVersions {
   private final AspectModelResolver resolver = new AspectModelResolver();
   private static final String TEST_NAMESPACE = "urn:samm:org.eclipse.esmf.test:1.0.0#";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLoadDataModelExpectSuccess( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TEST_NAMESPACE + "Test" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final Resource aspect = createResource( TEST_NAMESPACE + "Test" );
      final Resource sammAspect = SammNs.SAMM.Aspect();
      assertThat( result.get().getModel().listStatements( aspect, RDF.type, sammAspect ).nextOptional() ).isNotEmpty();
   }

   @Test
   public void testLoadLegacyBammModelWithoutPrefixesExpectSuccess() throws URISyntaxException {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TEST_NAMESPACE + "BammAspectWithoutPrefixes" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final SAMM samm = SammNs.SAMM;
      assertThat( result.get().getModel().listStatements( null, RDF.type, samm.Aspect() ).nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding2_0_0" )
   public void testLoadLegacyBammModelExpectSuccess( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:bamm:org.eclipse.esmf.test:2.0.0#BammTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final SAMM samm = SammNs.SAMM;
      assertThat( result.get().getModel().listStatements( null, RDF.type, samm.Aspect() ).nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding2_0_0" )
   public void testLoadLegacyBammModelFromFileExpectSuccess( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelResolver.fileToUrn(
            Paths.get( aspectModelsRootDirectory.toString(), "org.eclipse.esmf.test", "2.0.0", "BammTest.ttl" ).toFile() ).get();

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final SAMM samm = SammNs.SAMM;
      assertThat( result.get().getModel().listStatements( null, RDF.type, samm.Aspect() ).nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testLoadModelWithVersionEqualToUnsupportedMetaModelVersionExpectSuccess(
         final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.1.0#Test" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final Resource aspect = createResource( "urn:samm:org.eclipse.esmf.test:1.1.0#Test" );
      final SAMM samm = SammNs.SAMM;
      assertThat( result.get().getModel().listStatements( aspect, RDF.type, samm.Aspect() ).nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolveReferencedModelFromMemoryExpectSuccess( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final AspectModelUrn inputUrn = AspectModelUrn
            .fromUrn( TEST_NAMESPACE + "AnotherTest" );
      final Model model = TurtleLoader.loadTurtle(
            AspectModelResolverTest.class.getResourceAsStream(
                  "/" + metaModelVersion.toString().toLowerCase()
                        + "/org.eclipse.esmf.test/1.0.0/Test.ttl" ) ).get();
      final ResolutionStrategy inMemoryStrategy = anyUrn -> Try.success( ModelFiles.fromModel( model ) );
      final EitherStrategy inMemoryResolutionStrategy = new EitherStrategy( urnStrategy, inMemoryStrategy );

      final Try<VersionedModel> result = resolver.resolveAspectModel( inMemoryResolutionStrategy, inputUrn );
      assertThat( result.isSuccess() ).isTrue();

      final SAMM samm = SammNs.SAMM;
      final Resource aspect = createResource( TEST_NAMESPACE + "AnotherTest" );
      assertThat( result.get().getModel().listStatements( aspect, RDF.type, samm.Aspect() ).nextOptional() ).isNotEmpty();

      final Resource propertyFromReferencedAspect = createResource( TEST_NAMESPACE + "foo" );
      assertThat(
            result.get().getModel().listStatements( propertyFromReferencedAspect, RDF.type, samm.Property() ).nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolveReferencedModelExpectSuccess( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( TEST_NAMESPACE + "AnotherTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );

      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final SAMM samm = SammNs.SAMM;
      final Resource aspect = createResource( TEST_NAMESPACE + "AnotherTest" );
      assertThat( result.get().getModel().listStatements( aspect, RDF.type, samm.Aspect() ).nextOptional() ).isNotEmpty();

      final Resource propertyFromReferencedAspect = createResource( TEST_NAMESPACE + "foo" );
      assertThat(
            result.get().getModel().listStatements( propertyFromReferencedAspect, RDF.type, samm.Property() ).nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolutionMissingAspectExpectFailure( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( TEST_NAMESPACE + "AnotherFailingTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isFailure() ).isTrue();
      assertThat( result.getCause() ).isInstanceOf( ModelResolutionException.class );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolutionMissingModelElementExpectFailure( final KnownVersion metaModelVersion ) throws Throwable {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( TEST_NAMESPACE + "FailingTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isFailure() ).isTrue();
      assertThat( result.getCause() ).isInstanceOf( ModelResolutionException.class );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolutionReferencedCharacteristicExpectSuccess( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( TEST_NAMESPACE + "ReferenceCharacteristicTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy(
            aspectModelsRootDirectory.toPath() );

      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final Resource aspect = createResource(
            TEST_NAMESPACE + "ReferenceCharacteristicTest" );
      final SAMM samm = SammNs.SAMM;
      assertThat( result.get().getModel().listStatements( aspect, RDF.type, samm.Aspect() ).nextOptional() ).isNotEmpty();

      final Resource referencedCharacteristic = createResource( TEST_NAMESPACE + "TestCharacteristic" );
      assertThat( result.get().getModel().listStatements( referencedCharacteristic, RDF.type, samm.Characteristic() )
            .nextOptional() ).isNotEmpty();
   }

   /**
    * This test checks that if the same shared resource (in this case: the shared TestCharacteristic) is
    * transitively imported on multiple paths through the dependency graph, it is still only added once to the
    * final merged model, so for example the Statement x:testCharacteristic samm:name "testCharacteristic" is
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
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( TEST_NAMESPACE + "TransitiveReferenceTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final Model model = result.get().getModel();
      final Resource testCharacteristic = createResource( TEST_NAMESPACE + "TestCharacteristic" );
      assertThat( Streams.stream( model.listStatements( testCharacteristic, RDF.type, (RDFNode) null ) ).count() ).isEqualTo( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolutionReferencedEntity( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn
            .fromUrn( TEST_NAMESPACE + "ReferenceEntityTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy(
            aspectModelsRootDirectory.toPath() );

      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final Resource aspect = createResource( TEST_NAMESPACE + "ReferenceEntityTest" );
      final SAMM samm = SammNs.SAMM;
      assertThat( result.get().getModel().listStatements( aspect, RDF.type, samm.Aspect() ).nextOptional() ).isNotEmpty();

      final Resource referencedEntity = createResource( TEST_NAMESPACE + "TestEntity" );
      assertThat( result.get().getModel().listStatements( referencedEntity, RDF.type, samm.Entity() ).nextOptional() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectReferencingAnotherAspectExpectSuccess( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final String aspectUrn = "urn:samm:org.eclipse.esmf.test:2.0.0#Test";
      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( aspectUrn );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      final Model model = result.get().getModel();
      final SAMM samm = SammNs.SAMM;
      assertThat( Streams.stream( model.listStatements( null, RDF.type, samm.Aspect() ) ).count() ).isEqualTo( 2 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testClassPathResolution( final KnownVersion metaModelVersion ) {
      final String aspectUrn = TEST_NAMESPACE + "Test";
      final AspectModelResolver resolver = new AspectModelResolver();
      final ClasspathStrategy strategy = new ClasspathStrategy( metaModelVersion.toString().toLowerCase() );
      final Try<VersionedModel> result = resolver
            .resolveAspectModel( strategy, AspectModelUrn.fromUrn( aspectUrn ) );
      assertThat( result.isSuccess() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testResolveAspectContainingRefinedProperty2( final KnownVersion metaModelVersion ) {
      final String aspectUrn = TEST_NAMESPACE + "ReferenceCharacteristicTest";
      final AspectModelResolver resolver = new AspectModelResolver();
      final ClasspathStrategy strategy = new ClasspathStrategy( metaModelVersion.toString().toLowerCase() );
      final Try<VersionedModel> result = resolver
            .resolveAspectModel( strategy, AspectModelUrn.fromUrn( aspectUrn ) );
      assertThat( result.isSuccess() ).describedAs( "Resolution of refined Property failed." ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testMergingModelsWithBlankNodeValues( final KnownVersion metaModelVersion ) {
      final String aspectUrn = TEST_NAMESPACE + "SecondaryAspect";
      final AspectModelResolver resolver = new AspectModelResolver();
      final ClasspathStrategy strategy = new ClasspathStrategy( metaModelVersion.toString().toLowerCase() );
      final Try<VersionedModel> result = resolver
            .resolveAspectModel( strategy, AspectModelUrn.fromUrn( aspectUrn ) );
      assertThat( result.isSuccess() ).isTrue();

      final Model model = result.get().getModel();
      final Resource primaryAspect = model.createResource( TEST_NAMESPACE + "PrimaryAspect" );
      final List<Statement> propertiesAssertions = model.listStatements( primaryAspect, SammNs.SAMM.properties(), (RDFNode) null ).toList();
      assertThat( propertiesAssertions.size() ).isEqualTo( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMultiReferenceSameSource( final KnownVersion metaModelVersion ) throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( metaModelVersion.toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TEST_NAMESPACE + "VehicleInstance" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      final Try<VersionedModel> result = resolver.resolveAspectModel( urnStrategy, testUrn );
      assertThat( result.isSuccess() ).isTrue();

      // make sure the source file for the definitions of ModelYear and ModelCode (ModelDef.ttl) is only loaded once
      final Model model = result.get().getModel();
      final Resource entity = model.createResource( TEST_NAMESPACE + "SomeOtherNonRelatedEntity" );
      final List<Statement> properties = model.listStatements( entity, SammNs.SAMM.properties(), (RDFNode) null ).toList();
      assertThat( properties.size() ).isEqualTo( 1 );
   }
}
