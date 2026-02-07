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

package org.eclipse.esmf.aspectmodel.resolver;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestModel;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Comprehensive test suite for {@link FileSystemStrategy} that validates:
 * <ul>
 *   <li>Element resolution from dedicated files</li>
 *   <li>Element resolution from shared files (primary focus)</li>
 *   <li>Mixed resolution scenarios (shared + dedicated files)</li>
 *   <li>Transitive dependency resolution</li>
 *   <li>Error handling and messaging</li>
 *   <li>API correctness (apply, load, listing methods)</li>
 * </ul>
 *
 * <p>This test class specifically focuses on validating the fix for resolving elements
 * from shared files when they don't have dedicated files, which is critical for
 * flexible model organization.</p>
 */
class FileSystemStrategyTest {

   private ResolutionStrategy fileSystemStrategy;

   @BeforeEach
   void setUp() throws URISyntaxException {
      File aspectModelsRootDirectory = new File(
            FileSystemStrategyTest.class.getClassLoader()
                  .getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );
      fileSystemStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );
   }

   // ==================== Helper Methods ====================

   private AspectModelUrn urn( final String elementName ) {
      return AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + elementName );
   }

   private Resource resource( final String elementName ) {
      return createResource( TestModel.TEST_NAMESPACE + elementName );
   }

   private AspectModel loadModel( final AspectModelUrn urn ) {
      return new AspectModelLoader( fileSystemStrategy ).load( urn );
   }

   private void assertResourceExistsWithType( final AspectModel model, final String elementName, final Resource rdfType ) {
      final Resource resource = resource( elementName );
      assertThat( model.mergedModel().contains( resource, RDF.type, rdfType ) )
            .as( "Element '%s' should exist with type '%s'", elementName, rdfType )
            .isTrue();
   }

   private void assertResourceExists( final AspectModel model, final String elementName ) {
      final Resource resource = resource( elementName );
      assertThat( model.mergedModel().contains( resource, RDF.type, (RDFNode) null ) )
            .as( "Element '%s' should exist in model", elementName )
            .isTrue();
   }

   // ==================== Dedicated File Resolution Tests ====================

   @Test
   void testResolveElementFromItsOwnFileExpectSuccess() {
      final AspectModelUrn testUrn = urn( "Test" );

      assertThatCode( () -> {
         final AspectModel result = loadModel( testUrn );
         assertResourceExistsWithType( result, "Test", SammNs.SAMM.Aspect() );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testLegacyPropertyFromDedicatedFileExpectSuccess() {
      final AspectModelUrn legacyPropertyUrn = urn( "legacyProperty" );

      assertThatCode( () -> {
         final AspectModel result = loadModel( legacyPropertyUrn );
         assertResourceExists( result, "legacyProperty" );
      } ).doesNotThrowAnyException();
   }

   // ==================== Shared File Resolution Tests ====================

   @ParameterizedTest
   @CsvSource( {
         "sharedProperty, Property",
         "sharedCharacteristic, Characteristic",
         "SharedEntity, Entity"
   } )
   void testResolveElementFromSharedFileExpectSuccess( final String elementName, final String elementType ) {
      // Test resolving elements that don't have their own files but are defined in aspectelements_shared.ttl
      final AspectModelUrn elementUrn = urn( elementName );

      assertThatCode( () -> {
         final AspectModel result = loadModel( elementUrn );
         final Resource expectedType = getExpectedType( elementType );
         assertResourceExistsWithType( result, elementName, expectedType );
      } ).doesNotThrowAnyException();
   }

   private Resource getExpectedType( final String elementType ) {
      return switch ( elementType ) {
         case "Property" -> SammNs.SAMM.Property();
         case "Characteristic" -> SammNs.SAMM.Characteristic();
         case "Entity" -> SammNs.SAMM.Entity();
         default -> throw new IllegalArgumentException( "Unknown element type: " + elementType );
      };
   }

   @Test
   void testResolveAspectThatUsesSharedElementsExpectSuccess() {
      // Test that an Aspect using shared elements can be loaded and its dependencies resolved from aspectelements_shared.ttl
      final AspectModelUrn aspectUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "AspectUsingSharedElements" );

      assertThatCode( () -> {
         final AspectModel result = new AspectModelLoader( fileSystemStrategy ).load( aspectUrn );
         final Resource aspect = createResource( aspectUrn.toString() );
         assertThat( result.mergedModel().contains( aspect, RDF.type, SammNs.SAMM.Aspect() ) ).isTrue();

         // Verify that sharedProperty from aspectelements_shared.ttl is present
         final Resource sharedProperty = createResource( TestModel.TEST_NAMESPACE + "sharedProperty" );
         assertThat( result.mergedModel().contains( sharedProperty, RDF.type, (org.apache.jena.rdf.model.RDFNode) null ) ).isTrue();

         // Verify that legacyProperty from its dedicated file legacyProperty.ttl is present
         final Resource legacyProperty = createResource( TestModel.TEST_NAMESPACE + "legacyProperty" );
         assertThat( result.mergedModel().contains( legacyProperty, RDF.type, (org.apache.jena.rdf.model.RDFNode) null ) ).isTrue();
      } ).doesNotThrowAnyException();
   }

   @Test
   void testResolveNonExistentElementExpectException() {
      // Test that resolving a non-existent element throws ModelResolutionException
      final AspectModelUrn nonExistentUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "NonExistentElement" );

      assertThatThrownBy( () -> new AspectModelLoader( fileSystemStrategy ).load( nonExistentUrn ) )
            .isInstanceOf( ModelResolutionException.class )
            .hasMessageContaining( "File does not contain the element definition" );
   }


   @Test
   void testApplyWithElementInSharedFile() {
      // Test the apply method when the element doesn't have its own file but exists in a shared file
      final AspectModelUrn sharedPropertyUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "sharedProperty" );
      final ResolutionStrategySupport support = new AspectModelLoader( fileSystemStrategy );

      assertThatCode( () -> {
         final AspectModelFile result = fileSystemStrategy.apply( sharedPropertyUrn, support );
         assertThat( result ).isNotNull();
         assertThat( result.sourceLocation() ).isPresent();
         // Verify the file is aspectelements_shared.ttl, not sharedProperty.ttl
         assertThat( result.sourceLocation().get().toString() ).contains( "aspectelements_shared.ttl" );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testApplyWithNonExistentElement() {
      // Test that apply throws ModelResolutionException for non-existent elements
      final AspectModelUrn nonExistentUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "NonExistentElement" );
      final ResolutionStrategySupport support = new AspectModelLoader( fileSystemStrategy );

      assertThatThrownBy( () -> fileSystemStrategy.apply( nonExistentUrn, support ) )
            .isInstanceOf( ModelResolutionException.class );
   }

   @Test
   void testListContentsForNamespace() {
      // Test that listContentsForNamespace returns URIs for a specific namespace
      final AspectModelUrn namespaceUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "dummy" );

      assertThatCode( () -> {
         final long count = fileSystemStrategy.listContentsForNamespace( namespaceUrn ).count();
         assertThat( count ).isPositive();
      } ).doesNotThrowAnyException();
   }

   @Test
   void testLoadContentsForNamespace() {
      // Test that loadContentsForNamespace returns AspectModelFiles for a specific namespace
      final AspectModelUrn namespaceUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "dummy" );

      assertThatCode( () -> {
         final long count = fileSystemStrategy.loadContentsForNamespace( namespaceUrn ).count();
         assertThat( count ).isPositive();
      } ).doesNotThrowAnyException();
   }

   @Test
   void testToString() {
      final String result = fileSystemStrategy.toString();
      assertThat( result )
            .contains( FileSystemStrategy.class.getSimpleName() )
            .contains( "root=" + StructuredModelsRoot.class.getSimpleName() );
   }
}
