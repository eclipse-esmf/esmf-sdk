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

import java.util.Locale;
import java.util.Optional;

import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.datatype.LangString;

import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ValueInstantiatorTest {

   @BeforeAll
   static void setupJena() {
      ResourceFactory.createResource();
   }

   @Test
   void testBuildLanguageStringEmptyLanguageTag() {
      // Arrange
      String lexicalRepresentation = "hello";
      String languageTag = "";
      String datatypeUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";
      ValueInstantiator valueInstantiator = new ValueInstantiator();

      // Act
      Optional<ScalarValue> result = valueInstantiator.buildScalarValue( lexicalRepresentation, languageTag, datatypeUri );

      // Assert
      assertThat( result ).isEmpty();
   }

   @Test
   void testBuildLanguageStringNonEmptyLanguageTag() {
      // Arrange
      String lexicalRepresentation = "hello";
      String languageTag = "en";
      String datatypeUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";
      ValueInstantiator valueInstantiator = new ValueInstantiator();

      // Act
      Optional<ScalarValue> result = valueInstantiator.buildScalarValue( lexicalRepresentation, languageTag, datatypeUri );

      // Assert
      assertThat( result ).isPresent();
      assertThat( result.get().getValue() ).isInstanceOf( LangString.class );
      assertThat( result.get().getValue() )
            .extracting( LangString.class::cast )
            .extracting( LangString::getLanguageTag )
            .isEqualTo( Locale.forLanguageTag( languageTag ) );
   }
}