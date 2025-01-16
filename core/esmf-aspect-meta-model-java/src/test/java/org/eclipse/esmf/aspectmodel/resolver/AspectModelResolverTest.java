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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.esmf.aspectmodel.AspectLoadingException;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestModel;
import org.eclipse.esmf.test.TestResources;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

class AspectModelResolverTest {
   @Test
   void testLoadDataModelExpectSuccess() throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "Test" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      assertThatCode( () -> {
         final AspectModel result = new AspectModelLoader( urnStrategy ).load( testUrn );
         final Resource aspect = createResource( TestModel.TEST_NAMESPACE + "Test" );
         assertThat( result.mergedModel().listStatements( aspect, RDF.type, SammNs.SAMM.Aspect() ).nextOptional() ).isNotEmpty();
         assertThat( result.files().get( 0 ).headerComment() ).isNotNull();
         assertThat( result.files().get( 0 ).headerComment().get( 0 ) ).contains(
               "Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH" );
         assertThat( result.files().get( 0 ).spdxLicenseIdentifier() ).contains( "MPL-2.0" );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testLoadLegacyBammModelWithoutPrefixesExpectSuccess() throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "BammAspectWithoutPrefixes" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      assertThatCode( () -> {
         final AspectModel result = new AspectModelLoader( urnStrategy ).load( testUrn );
         assertThat( result.mergedModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextOptional() ).isNotEmpty();
      } ).doesNotThrowAnyException();
   }

   @Test
   void testLoadLegacyBammModelExpectSuccess() throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:bamm:org.eclipse.esmf.test:2.0.0#BammTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      assertThatCode( () -> {
         final AspectModel result = new AspectModelLoader( urnStrategy ).load( testUrn );
         final Resource aspect = createResource( "urn:samm:org.eclipse.esmf.test:2.0.0#BammTest" );
         assertThat( result.mergedModel().listStatements( aspect, RDF.type, SammNs.SAMM.Aspect() ).nextOptional() ).isNotEmpty();
         assertThat( result.files().get( 0 ).headerComment() ).isNotNull();
         assertThat( result.files().get( 0 ).headerComment().get( 0 ) ).contains(
               "Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH" );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testLoadModelWithVersionEqualToUnsupportedMetaModelVersionExpectSuccess() throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.1.0#Test" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      assertThatCode( () -> {
         final AspectModel result = new AspectModelLoader( urnStrategy ).load( testUrn );
         final Resource aspect = createResource( "urn:samm:org.eclipse.esmf.test:1.1.0#Test" );
         assertThat( result.mergedModel().listStatements( aspect, RDF.type, SammNs.SAMM.Aspect() ).nextOptional() ).isNotEmpty();
      } ).doesNotThrowAnyException();
   }

   @Test
   void testResolveReferencedModelFromMemoryExpectSuccess() throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );

      final ResolutionStrategy inMemoryStrategy = new FromLoadedFileStrategy( AspectModelFileLoader.load(
            AspectModelResolverTest.class.getResourceAsStream(
                  "/" + KnownVersion.getLatest().toString().toLowerCase()
                        + "/org.eclipse.esmf.test/1.0.0/Test.ttl" ) ) );
      final EitherStrategy inMemoryResolutionStrategy = new EitherStrategy( urnStrategy, inMemoryStrategy );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "AnotherTest" );
      assertThatCode( () -> {
         final AspectModel result = new AspectModelLoader( inMemoryResolutionStrategy ).load( testUrn );
         final Resource aspect = createResource( testUrn.toString() );
         assertThat( result.mergedModel().listStatements( aspect, RDF.type, SammNs.SAMM.Aspect() ).nextOptional() ).isNotEmpty();
         assertThat( result.files().get( 0 ).headerComment() ).isNotNull();
         assertThat( result.files().get( 0 ).headerComment().get( 0 ) ).contains(
               "Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH" );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testResolveReferencedModelExpectSuccess() throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "AnotherTest" );
      assertThatCode( () -> {
         final AspectModel result = new AspectModelLoader( urnStrategy ).load( testUrn );
         final Resource aspect = createResource( testUrn.toString() );
         assertThat( result.mergedModel().listStatements( aspect, RDF.type, SammNs.SAMM.Aspect() ).nextOptional() ).isNotEmpty();
         assertThat( result.files().get( 0 ).headerComment() ).isNotNull();
         assertThat( result.files().get( 0 ).headerComment().get( 0 ) ).contains(
               "Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH" );

         final Resource propertyFromReferencedAspect = createResource( TestModel.TEST_NAMESPACE + "foo" );
         assertThat( result.mergedModel().listStatements( propertyFromReferencedAspect, RDF.type, SammNs.SAMM.Property() )
               .nextOptional() ).isNotEmpty();
      } ).doesNotThrowAnyException();
   }

   @Test
   void testResolutionMissingAspectExpectFailure() throws URISyntaxException {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader()
                  .getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "AnotherFailingTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      assertThatThrownBy( () -> {
         final AspectModel result = new AspectModelLoader( urnStrategy ).load( testUrn );
      } ).isInstanceOf( ModelResolutionException.class );
   }

   @Test
   void testResolutionMissingModelElementExpectFailure() throws Throwable {
      final File aspectModelsRootDirectory = new File(
            AspectModelResolverTest.class.getClassLoader().getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "FailingTest" );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
      assertThatThrownBy( () -> {
         final AspectModel result = new AspectModelLoader( urnStrategy ).load( testUrn );
      } ).isInstanceOf( ModelResolutionException.class );
   }

   @Test
   void getExceptionWhileLoadingModelWithTwoAspects() {
      assertThatThrownBy( () -> {
         TestResources.load( InvalidTestAspect.INVALID_ASPECT_WITH_TWO_ASPECTS );
      } )
            .isInstanceOf( AspectLoadingException.class )
            .hasMessageContaining(
                  "Aspect model file testmodel:invalid/org.eclipse.esmf.test/1.0.0/InvalidAspectWithTwoAspects.ttl contains 2 "
                        + "aspects, but may only contain one." );
   }
}
