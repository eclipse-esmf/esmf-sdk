/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.turtle.completion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;

class SammShapeUtilsTest {

   private static final String DATA_TYPE = SammNs.SAMM.dataType().getURI();
   private static final String PROPERTIES = SammNs.SAMM.properties().getURI();

   @Test
   void dataTypeExpectsEntityOrAbstractEntity() {
      assertThat( expectedClassUris( DATA_TYPE ) )
            .containsExactlyInAnyOrder( SammNs.SAMM.Entity().getURI(), SammNs.SAMM.AbstractEntity().getURI() );
   }

   @Test
   void propertiesExpectsSammProperty() {
      assertThat( expectedClassUris( PROPERTIES ) ).containsExactly( SammNs.SAMM.Property().getURI() );
   }

   @Test
   void isSubclassOfAnyMatchesTypesOfExpectedClasses() {
      final Set<Resource> entityOrAbstractEntity = SammShapeUtils.EXPECTED_CLASSES.get( DATA_TYPE );

      assertThat( SammShapeUtils.isSubclassOfAny( SammNs.SAMM.Entity().getURI(), entityOrAbstractEntity ) ).isTrue();
      assertThat( SammShapeUtils.isSubclassOfAny( XSD.xstring.getURI(), entityOrAbstractEntity ) ).isFalse();
   }

   @Test
   void expectedSubjectsKeepsOnlySubjectsOfExpectedClasses() {
      final Map<String, Set<String>> subjectTypes = Map.of(
            "urn:example#entity", Set.of( SammNs.SAMM.Entity().getURI() ),
            "urn:example#literal", Set.of( XSD.xstring.getURI() ) );

      assertThat( SammShapeUtils.expectedSubjects( subjectTypes, Optional.of( DATA_TYPE ) ) )
            .contains( Set.of( "urn:example#entity" ) );
   }

   @Test
   void expectedSubjectsIsEmptyWithoutExpectedClasses() {
      final Map<String, Set<String>> subjectTypes = Map.of( "urn:example#entity", Set.of( SammNs.SAMM.Entity().getURI() ) );

      assertThat( SammShapeUtils.expectedSubjects( subjectTypes, Optional.empty() ) ).isEmpty();
      assertThat( SammShapeUtils.expectedSubjects( subjectTypes, Optional.of( "http://example.org/unknownPredicate" ) ) )
            .isEmpty();
   }

   @Test
   void predicatesExpectingReturnsPredicatesWhoseExpectedClassIsExactlyTheGivenClass() {
      assertThat( SammShapeUtils.predicatesExpecting( SammNs.SAMM.Property() ) ).contains( PROPERTIES );
   }

   private static Set<String> expectedClassUris( final String predicate ) {
      return SammShapeUtils.EXPECTED_CLASSES.getOrDefault( predicate, Set.of() ).stream()
            .map( Resource::getURI )
            .collect( Collectors.toSet() );
   }
}
