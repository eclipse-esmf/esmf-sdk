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

package org.eclipse.esmf.aspectmodel.shacl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.aspectmodel.RdfUtil.createModel;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.aspectmodel.resolver.parser.PlainTextFormatter;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import io.vavr.control.Either;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

public class RustLikeFormatterTest {
   private final String namespace = "http://example.com#";
   private final RustLikeFormatter formatter = new RustLikeFormatter();

   @Test
   void testMiddleStatement() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :firstProperty 1 ;
              :secondProperty 2 .
            """ );

      final RDFNode firstProperty = dataModel.listStatements( null, ResourceFactory.createProperty( namespace, "firstProperty" ),
            (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( firstProperty, "" );
      assertCorrectFormatting( message, ":firstProperty 1 ;" );
   }

   @Test
   void testLastStatement() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :firstProperty 1 ;
              :secondProperty 2 .
            """ );

      final RDFNode secondProperty = dataModel.listStatements( null, ResourceFactory.createProperty( namespace, "secondProperty" ),
            (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( secondProperty, "" );
      assertCorrectFormatting( message, ":secondProperty 2 ." );
   }

   @Test
   void testMultipleStatementsSameLine() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :firstProperty 1 ; :secondProperty 2 .
            """ );

      final RDFNode firstProperty = dataModel.listStatements( null, ResourceFactory.createProperty( namespace, "firstProperty" ),
            (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( firstProperty, "" );
      assertCorrectFormatting( message, ":firstProperty 1 ; :secondProperty 2 ." );
   }

   @Test
   void testMultiSubjectSameLine() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ; :property 1 . :Bar a :TestClass ; :property 2 .
            """ );

      final RDFNode property = dataModel.listStatements( ResourceFactory.createResource( namespace + "Foo" ),
            ResourceFactory.createProperty( namespace, "property" ), (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( property, "" );
      assertCorrectFormatting( message, ":Foo a :TestClass ; :property 1 . :Bar a :TestClass ; :property 2 ." );
   }

   @Test
   void testAnonymousNodes() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :testProperty [ a :MyType ] .
            """ );

      final RDFNode property = dataModel.listStatements( ResourceFactory.createResource( namespace + "Foo" ),
            ResourceFactory.createProperty( namespace, "testProperty" ), (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( property, "" );
      assertCorrectFormatting( message, ":testProperty [ a :MyType ] ." );
   }

   @Test
   void testMultilineAnonymousNode() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :prop1 [
                :prop2 23 ;
              ] .
            """ );

      final RDFNode property = dataModel.listStatements( ResourceFactory.createResource( namespace + "Foo" ),
            ResourceFactory.createProperty( namespace, "prop1" ), (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( property, "" );
      assertCorrectFormatting( message, ":prop1 [" );
   }

   @Test
   void testMultilineAnonymousNodeMiddlePart() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :prop1 [
                :prop2 23 ;
              ] .
            """ );

      final RDFNode property = dataModel.listStatements( null,
            ResourceFactory.createProperty( namespace, "prop2" ), (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( property, "" );
      assertCorrectFormatting( message, ":prop2 23" );
   }

   @Test
   void testEmptyList() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :listProperty () .
            """ );

      final RDFNode listProperty = dataModel.listStatements( null, ResourceFactory.createProperty( namespace, "listProperty" ),
            (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( listProperty, "" );
      assertCorrectFormatting( message, ":listProperty () ." );
   }

   @Test
   void testList() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :listProperty ( :firstValue :secondValue ) .
            """ );

      final RDFNode listProperty = dataModel.listStatements( null, ResourceFactory.createProperty( namespace, "listProperty" ),
            (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( listProperty, "" );
      assertCorrectFormatting( message, ":listProperty ( :firstValue :secondValue ) ." );
   }

   @Test
   void testMultilineListStarted() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :listProperty ( :firstValue
              :secondValue ) .
            """ );

      final RDFNode listProperty = dataModel.listStatements( null, ResourceFactory.createProperty( namespace, "listProperty" ),
            (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( listProperty, "" );
      assertCorrectFormatting( message, ":listProperty ( :firstValue" );
   }

   @Test
   void testMultilineListFinished() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :listProperty ( :firstValue
              :secondValue :thirdValue ) .
            """ );

      final RDFNode listElement = dataModel.listStatements( null, null,
            ResourceFactory.createResource( namespace + "secondValue" ) ).nextStatement().getObject();
      final String message = formatter.constructDetailedMessage( listElement, "" );
      assertCorrectFormatting( message, ":secondValue :thirdValue ) ." );
   }

   @Test
   void testListWithAnonymousNodes() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass ;
              :listProperty ( :firstValue [ :property :prop2; :name "givenName"; ] ) .
            """ );

      final RDFNode listElement = dataModel.listStatements( null, null,
            ResourceFactory.createResource( namespace + "prop2" ) ).nextStatement().getObject();
      final String message = formatter.constructDetailedMessage( listElement, "" );
      assertCorrectFormatting( message, ":listProperty ( :firstValue [ :property :prop2 ;:name \"givenName\" ] ) ." );
   }

   @Test
   void testDenseFormatting() {
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            
            :Foo a :TestClass;:property 1.:Bar a :TestClass;:property 2.
            """ );

      final RDFNode property = dataModel.listStatements( ResourceFactory.createResource( namespace + "Foo" ),
            ResourceFactory.createProperty( namespace, "property" ), (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( property, "" );
      assertCorrectFormatting( message, ":Foo a :TestClass;:property 1.:Bar a :TestClass;:property 2 ." );
   }

   /**
    * Verifies that the formatted test report actually contains the underlining ("^^^") under the expected tokens
    *
    * @param testModel the test model
    * @param highlightToken the element that should be highlighted by the formatter
    */
   @ParameterizedTest( name = "{index}: Check highlighting in {0} ({1})" )
   @CsvSource( textBlock = """
         ASPECT_WITH_RECURSIVE_PROPERTY, :testProperty
         INVALID_EXAMPLE_VALUE_DATATYPE, "1234"^^xsd:int
         INVALID_PREFERRED_NAME_DATATYPE, "Invalid PreferredName Datatype"
         INVALID_CHARACTERISTIC_DATATYPE, :Characteristic1
         INVALID_URI, invalid with spaces
         RANGE_CONSTRAINT_WITH_WRONG_TYPE, :RangeConstraintWithWrongType
         MODEL_WITH_CYCLES, :a
         """ )
   void testSemanticValidationFormatting( final InvalidTestAspect testModel, final String highlightToken ) {
      final Either<List<Violation>, AspectModel> result = TestResources.loadWithValidation( testModel, new AspectModelValidator() );
      assertThat( result.isLeft() ).isTrue();
      final List<Violation> violations = result.getLeft();
      final String report = new ViolationFormatter().apply( violations );
      // Report contains highlight marker
      assertThat( report ).contains( " " + "^".repeat( highlightToken.length() ) + " " );
      // Report contains source file location
      assertThat( report ).contains( "in testmodel:invalid/" );
   }

   @ParameterizedTest
   @EnumSource( value = InvalidTestAspect.class, mode = EnumSource.Mode.INCLUDE, names = {
         "ACTUALLY_JSON",
         "INVALID_SYNTAX"
   } )
   void testSyntaxErrorFormatting( final InvalidTestAspect testModel ) {
      final Either<List<Violation>, AspectModel> result = TestResources.loadWithValidation( testModel, new AspectModelValidator() );
      assertThat( result.isLeft() ).isTrue();
      final List<Violation> violations = result.getLeft();
      final String report = new ViolationFormatter( new PlainTextFormatter() ).apply( violations );
      assertThat( report ).contains( "Syntax" );
      // Report contains source file location
      assertThat( report ).contains( "in testmodel:invalid/" );
   }

   @Test
   void testFormattingForDuplicateDefinition() {
      final AspectModelFile rawFile1 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).files().iterator().next();
      final AspectModelFile file1 = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( rawFile1.sourceLocation().get() + "-first-instance" ) ) )
            .sourceModel( rawFile1.sourceModel() )
            .headerComment( rawFile1.headerComment() )
            .build();
      final AspectModelFile rawFile2 = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).files().iterator().next();
      final AspectModelFile file2 = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( rawFile2.sourceLocation().get() + "-second-instance" ) ) )
            .sourceModel( rawFile2.sourceModel() )
            .headerComment( rawFile2.headerComment() )
            .build();

      final Either<List<Violation>, AspectModel> result = new AspectModelLoader().withValidation( new AspectModelValidator() )
            .loadAspectModelFiles( List.of( file1, file2 ) );
      assertThat( result.isLeft() ).isTrue();
      final List<Violation> violations = result.getLeft();
      final String report = new ViolationFormatter().apply( violations );
      assertThat( report ).contains( "Duplicate definition" );
   }

   private void assertCorrectFormatting( final String messageText, final String expectedLine ) {
      final String lineWithSourceText = messageText.lines().toList().get( 2 );
      final String reconstructedLine = lineWithSourceText.substring( lineWithSourceText.indexOf( '|' ) + 1 );
      assertThat( expectedLine ).isEqualTo( reconstructedLine.trim() );
   }
}
