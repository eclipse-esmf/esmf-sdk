/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectLoadingException;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectModelLoaderTest {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
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
            .hasMessage( "No datatype is defined on the Characteristic instance 'Characteristic1: '." );
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
            .isInstanceOfSatisfying( ParserException.class, parserException -> {
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
         org.assertj.core.api.Assertions.assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get )
               .isSameAs( abstractEntity );
      } );
      assertThat( entities ).extracting( "testEntityTwo" ).isInstanceOfSatisfying( ComplexType.class, type ->
            org.assertj.core.api.Assertions.assertThat( type ).extracting( ComplexType::getExtends ).extracting( Optional::get )
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
      final AspectModelFile file1 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).files().iterator().next();
      final AspectModelFile file2 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).files().iterator().next();
      final AspectModel aspectModel = new AspectModelLoader().loadAspectModelFiles( List.of( file1, file2 ) );
      final Resource aspect = aspectModel.mergedModel().createResource( TestAspect.ASPECT_WITH_PROPERTY.getUrn().toString() );
      final List<Statement> propertiesAssertions = aspectModel.mergedModel()
            .listStatements( aspect, SammNs.SAMM.properties(), (RDFNode) null ).toList();
      assertThat( propertiesAssertions ).hasSize( 1 );
   }
}