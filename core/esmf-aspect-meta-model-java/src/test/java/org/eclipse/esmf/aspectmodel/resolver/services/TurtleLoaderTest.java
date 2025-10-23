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
import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.Test;

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
}
