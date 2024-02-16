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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.aspectmodel.resolver.parser.ReaderRiotTurtle;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.junit.jupiter.api.Test;

public class RustLikeFormatterTest {

   private final String namespace = "http://example.com#";

   private final RustLikeFormatter formatter = new RustLikeFormatter();

   @Test
   void testMiddleStatement() {
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
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
      final Model dataModel = model( """
            @prefix : <http://example.com#> .

            :Foo a :TestClass;:property 1.:Bar a :TestClass;:property 2.
            """ );

      final RDFNode property = dataModel.listStatements( ResourceFactory.createResource( namespace + "Foo" ),
            ResourceFactory.createProperty( namespace, "property" ), (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( property, "" );
      assertCorrectFormatting( message, ":Foo a :TestClass;:property 1.:Bar a :TestClass;:property 2 ." );
   }

   private void assertCorrectFormatting( final String messageText, final String expectedLine ) {
      final String lineWithSourceText = messageText.lines().toList().get( 2 );
      final String reconstructedLine = lineWithSourceText.substring( lineWithSourceText.indexOf( '|' ) + 1 );
      assertThat( expectedLine ).isEqualTo( reconstructedLine.trim() );
   }

   private Model model( final String ttlRepresentation ) {
      final Model model = ModelFactory.createDefaultModel();
      final InputStream in = new ByteArrayInputStream( ttlRepresentation.getBytes( StandardCharsets.UTF_8 ) );
      RDFParserRegistry.registerLangTriples( Lang.TURTLE, ReaderRiotTurtle.factory );
      model.read( in, "", RDFLanguages.strLangTurtle );
      return model;
   }
}
