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

package org.eclipse.esmf.aspectmodel.resolver.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;
import org.eclipse.esmf.treesitterturtle.TreeSitterTurtle;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

import org.junit.jupiter.api.Test;
import org.treesitter.TSParser;
import org.treesitter.TSTree;

import io.vavr.control.Try;

public class TurtleLoaderTest {
   private static final String MODEL = """
        @prefix : <urn:samm:com.example:1.2.0#> .
        :x a ([ a aPrefix:c]) .
      """;

   @Test
   void turtleLoaderFailsWithNullPointerIfPrefixIsNotDefined() {
      assertThatCode( () -> {
         final Try<Model> tryModel = TurtleLoader.loadTurtle( new ByteArrayInputStream( MODEL.getBytes( StandardCharsets.UTF_8 ) ) );
         assertThat( tryModel.isFailure() ).isTrue();
         assertThat( tryModel.getCause() ).isInstanceOfSatisfying( ParserException.class, parserException -> {
            assertThat( parserException.getMessage() ).contains( "Undefined prefix: aPrefix" );
         } );
      } ).doesNotThrowAnyException();
   }

   @Test
   void jenaReaderSucceedsWhenPrefixIsNotDefined() throws IOException {
      final Model streamModel = ModelFactory.createDefaultModel();
      try ( final InputStream turtleInputStream = new ByteArrayInputStream( MODEL.getBytes( StandardCharsets.UTF_8 ) ) ) {
         assertThatThrownBy( () -> streamModel.read( turtleInputStream, "", RDFLanguages.TURTLE.getName() ) )
               .isInstanceOf( RiotException.class )
               .hasMessageContaining( "[line: 2, col: 13] Undefined prefix: aPrefix" );
      }
   }

   @Test
   void loadModelUsingTreeSitter() throws IOException {
      final String turtle =
            new String( TestResources.testModelSource( TestAspect.ASPECT_WITH_BINARY ).readAllBytes(), StandardCharsets.UTF_8 );
      try ( final TSParser parser = new TSParser() ) {
         parser.setLanguage( new TreeSitterTurtle() );
         final TSTree tsTree = parser.parseString( null, turtle );
         final TurtleSyntaxTree turtleSyntaxTree = TurtleSyntaxTree.fromConcreteSyntaxTree( tsTree, turtle );
         assertThatCode( () -> {
            final Try<Model> tryModel = TurtleLoader.loadTurtle( turtleSyntaxTree, buildArtificialUri( turtle, "model" ) );
            assertThat( tryModel.isFailure() ).isFalse();
            final Model model = tryModel.get();
            assertThat( model.listStatements().toList() ).isNotEmpty();
         } ).doesNotThrowAnyException();
      }
   }

   @Test
   void loadModelWithSyntaxErrorUsingTreeSitter() throws IOException {
      final String turtle =
            new String( TestResources.testModelSource( InvalidTestAspect.INVALID_SYNTAX ).readAllBytes(), StandardCharsets.UTF_8 );
      try ( final TSParser parser = new TSParser() ) {
         parser.setLanguage( new TreeSitterTurtle() );
         final TSTree tsTree = parser.parseString( null, turtle );
         final TurtleSyntaxTree turtleSyntaxTree = TurtleSyntaxTree.fromConcreteSyntaxTree( tsTree, turtle );
         final Try<Model> tryModel = TurtleLoader.loadTurtle( turtleSyntaxTree, buildArtificialUri( turtle, "model" ) );
         assertThat( tryModel.isFailure() ).isTrue();
      }
   }

   private static URI buildArtificialUri( final Object object, final String objectType ) {
      return URI.create( "inmemory:%s:%s".formatted( objectType, object.hashCode() ) );
   }
}
