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

package org.eclipse.esmf.aspectmodel.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.assertj.core.api.Assertions;

import org.eclipse.esmf.aspectmodel.AspectLoadingException;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.aspectmodel.resolver.parser.SmartToken;
import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.OrderingTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectModelLoaderTest {
   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( TestAspect.class )
   void testLoadAspectModelsSourceFilesArePresent( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      for ( final ModelElement element : aspectModel.elements() ) {
         assertThat( element.getSourceFile() )
               .describedAs( "Element %s has no source file", element ).isNotNull();
         assertThat( element.getSourceFile() )
               .describedAs( "Source file %s must contain defintion for %s", element.getSourceFile(), element.urn() )
               .elements().contains( element );
      }
   }

   @Test
   void loadAspectModelWithoutCharacteristicDatatype() {
      assertThatThrownBy( () -> TestResources.load( InvalidTestAspect.INVALID_CHARACTERISTIC_DATATYPE ) )
            .isInstanceOf( AspectLoadingException.class )
            .hasMessage( "No datatype is defined on the Characteristic instance 'Characteristic1'." );
   }

   @Test
   void testFileWithInvalidEncoding() {
      assertThatThrownBy( () -> TestResources.load( InvalidTestAspect.INVALID_ENCODING ) )
            .isInstanceOf( ModelResolutionException.class )
            .hasMessageContaining( "Encountered invalid encoding" );
   }

   @Test
   void testAspectModelWithInvalidUri() {
      assertThatThrownBy( () -> TestResources.load( InvalidTestAspect.INVALID_URI ) )
            .isInstanceOfSatisfying( ValueParsingException.class, parserException -> {
               assertThat( parserException.getMessage() ).contains( "invalid with spaces" );
               assertThat( parserException.getLine() ).isNotEqualTo( -1 ).isNotEqualTo( 0 );
               assertThat( parserException.getColumn() ).isNotEqualTo( -1 ).isNotEqualTo( 0 );
            } );
   }

   @Test
   void testOfAbstractEntityCyclomaticCreation() {
      final Map<String, ComplexType> entities =
            TestResources.load( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND ).elements().stream()
                  .filter( ComplexType.class::isInstance )
                  .map( ComplexType.class::cast )
                  .collect( Collectors.toMap( ComplexType::getName, Function.identity() ) );

      assertThat( entities ).extracting( "AbstractTestEntity" ).isInstanceOf( AbstractEntity.class );
      final AbstractEntity abstractEntity = (AbstractEntity) entities.get( "AbstractTestEntity" );
      assertThat( entities ).extracting( "testEntityOne" ).isInstanceOfSatisfying( ComplexType.class, type -> {
         Assertions.assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get )
               .isSameAs( abstractEntity );
      } );
      assertThat( entities ).extracting( "testEntityTwo" ).isInstanceOfSatisfying( ComplexType.class,
            type -> Assertions.assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get )
                  .isSameAs( abstractEntity ) );
   }

   @Test
   void testMergeAspectModels() {
      final AspectModel a1 = TestResources.load( TestAspect.ASPECT );
      final AspectModel a2 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      assertThat( a1 ).aspects().hasSize( 1 );
      assertThat( a2 ).aspects().hasSize( 1 );
      final AspectModel merged = new AspectModelLoader().merge( a1, a2 );
      assertThat( merged ).aspects().hasSize( 2 );
      assertThat( merged ).elements().size().isEqualTo( a1.elements().size() + a2.elements().size() );
   }

   @Test
   void testLoadMultipleFilesWithOverlappingRdfStatements() {
      final AspectModelFile rawFile1 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).files().getFirst();
      final AspectModelFile file1 = RawAspectModelFileBuilder.builder()
            .sourceUri( URI.create( rawFile1.sourceUri() + "-first-instance" ) )
            .sourceModel( rawFile1.sourceModel() )
            .headerComment( rawFile1.headerComment() )
            .build();
      final AspectModelFile rawFile2 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).files().getFirst();
      final AspectModelFile file2 = RawAspectModelFileBuilder.builder()
            .sourceUri( URI.create( rawFile2.sourceUri() + "-second-instance" ) )
            .sourceModel( rawFile2.sourceModel() )
            .headerComment( rawFile2.headerComment() )
            .build();
      assertThatThrownBy( () -> {
         new AspectModelLoader().loadAspectModelFiles( List.of( file1, file2 ) );
      } ).isInstanceOfSatisfying( AspectLoadingException.class, exception -> {
         assertThat( exception ).hasMessageContaining( "Duplicate definition" );
      } );
   }

   @RepeatedTest( 10 )
   void testAspectUploadOrdering() {
      final OrderingTestAspect aspectName = OrderingTestAspect.ASPECT;
      final AspectModel aspectModel = TestResources.load( aspectName );
      assertThat( aspectModel ).aspects()
            .filteredOn( aspect -> Objects.equals( aspectName.getName(), aspect.getName() ) )
            .first()
            .satisfies(
                  aspect -> assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:org.eclipse.esmf.test.ordering:1.0.0#Aspect" ) );
   }

   @Test
   void testJenaWrapAsResourceConsistency() {
      // Test consistency of 'wrapAsResource' (as used in AspectModelLoader), since it's marked as
      // "use only if necessary".
      // Contract: wrapAsResource must hand back the *identical* Node instance (==),
      // it must not create a new, merely equal one.
      // This is crucial for the TokenRegistry, which keys its node -> token map by object identity (Guava
      // MapMaker.weakKeys),
      // because two Node_URI instances for the same URI are equal but represent different occurrences in
      // different source files.
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Model sourceModel = aspectModel.files().getFirst().sourceModel();
      final Node aspectNode = sourceModel.getGraph().stream( null, RDF.type.asNode(), null )
            .map( Triple::getSubject )
            .filter( Node::isURI )
            .filter( node -> node.getURI().equals( "urn:samm:org.eclipse.esmf.test:1.0.0#Aspect" ) )
            .findFirst()
            .orElseThrow( () -> new AssertionError( "Could not find the Aspect node in the source model" ) );

      // Precondition: the node instance as it was created while parsing the source file carries token
      // information
      final SmartToken token = TokenRegistry.getToken( aspectNode )
            .orElseThrow( () -> new AssertionError( "The parsed Aspect node has no token information" ) );

      final Node wrappedAspectNode = aspectModel.mergedModel().wrapAsResource( aspectNode ).asNode();

      assertThat( wrappedAspectNode ).isSameAs( aspectNode );
      assertThat( TokenRegistry.getToken( wrappedAspectNode ) ).containsSame( token );
   }
}
