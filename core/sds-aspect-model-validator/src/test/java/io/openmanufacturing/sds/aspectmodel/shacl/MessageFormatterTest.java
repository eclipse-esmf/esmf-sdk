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

package io.openmanufacturing.sds.aspectmodel.shacl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.junit.jupiter.api.Test;

import io.openmanufacturing.sds.aspectmodel.resolver.parser.ReaderRIOTTurtle;

public class MessageFormatterTest {

   private final String namespace = "http://example.com#";

   private final MessageFormatter formatter = new MessageFormatter();

   @Test
   void testMidleStatement() {
      final Model dataModel = model( """            
            @prefix : <http://example.com#> .
                        
            :Foo a :TestClass ;
              :firstProperty 1 ;
              :secondProperty 2 .
            """ );

      final RDFNode firstProperty = dataModel.listStatements( null, ResourceFactory.createProperty( namespace, "firstProperty" ),
            (RDFNode) null ).nextStatement().getPredicate();
      final String message = formatter.constructDetailedMessage( firstProperty, "" );
      assertTrue( message.contains( ":firstProperty 1 ;" ) );
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
      assertTrue( message.contains( ":secondProperty 2 ." ) );
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
      assertTrue( message.contains( " :firstProperty 1 ; :secondProperty 2 ." ) );
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
      assertTrue( message.contains( ":Foo a :TestClass ; :property 1 . :Bar a :TestClass ; :property 2 ." ) );
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
      assertTrue( message.contains( ":testProperty [ a :MyType ] ." ) );
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
      assertTrue( message.contains( ":prop1 [" ) );
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
      assertTrue( message.contains( ":prop2 23 ;" ) );
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
      assertTrue( message.contains( " :listProperty () ." ) );
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
      assertTrue( message.contains( " :listProperty ( :firstValue :secondValue ) ." ) );
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
      assertTrue( message.contains( " :listProperty ( :firstValue" ) );
   }

   @Test
   void testMultilineListFinished() {
      final Model dataModel = model( """            
            @prefix : <http://example.com#> .
                        
            :Foo a :TestClass ;
              :listProperty ( :firstValue 
              :secondValue ) .
            """ );

      final RDFNode listElement = dataModel.listStatements( null, null,
            ResourceFactory.createResource( namespace + "secondValue" ) ).nextStatement().getObject();
      final String message = formatter.constructDetailedMessage( listElement, "" );
      assertTrue( message.contains( " :secondValue ) ." ) );
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
      assertTrue( message.contains( " [ :property :prop2 ;:name \"givenName\" ] ) ." ) );
   }

   private Model model( final String ttlRepresentation ) {
      final Model model = ModelFactory.createDefaultModel();
      final InputStream in = new ByteArrayInputStream( ttlRepresentation.getBytes( StandardCharsets.UTF_8 ) );
      RDFParserRegistry.registerLangTriples( Lang.TURTLE, ReaderRIOTTurtle.factory );
      model.read( in, "", RDFLanguages.strLangTurtle );
      return model;
   }
}
