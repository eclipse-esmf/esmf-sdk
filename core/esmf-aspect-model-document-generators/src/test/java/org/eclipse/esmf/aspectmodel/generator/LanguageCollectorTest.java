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

package org.eclipse.esmf.aspectmodel.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class LanguageCollectorTest {
   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class,
         mode = EnumSource.Mode.EXCLUDE,
         names = {
               // Contains a language tag on an element that does not count to the collected languages, so exclude it
               "ASPECT_WITH_MULTILANGUAGE_EXAMPLE_VALUE"
         } )
   public void testCollectLanguagesExpectSuccess( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final Model model = aspectModel.mergedModel();

      final Set<Locale> langTagsInModel = Streams.stream( model.listStatements() )
            .filter( statement -> statement.getObject().isLiteral() )
            .map( Statement::getLiteral )
            .map( Literal::getLanguage )
            .filter( language -> !language.isEmpty() )
            .map( Locale::forLanguageTag )
            .collect( Collectors.toSet() );

      final Set<Locale> localesFromAspect = LanguageCollector.collectUsedLanguages( aspectModel.aspect() );
      // Sets are not necessarily identical; e.g., if a model refers to samm-c:Text, the language collector will find
      // samm-c:Text's description's language, but the simple string matcher will find nothing.
      assertThat( localesFromAspect ).containsAll( langTagsInModel );
   }

   @Test
   public void testAspectModelEnglishAndGermanInProperty() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_DESCRIPTION_IN_PROPERTY );
      final Aspect aspect = aspectModel.aspect();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( aspect );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }

   @Test
   public void testAspectModelOnlyEnglish() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_ENGLISH_DESCRIPTION );
      final Aspect aspect = aspectModel.aspect();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( aspect );
      assertThat( languages ).containsExactly( Locale.forLanguageTag( "en" ) );
   }

   @Test
   public void testAspectModelEnglishAndGerman() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION );
      final Aspect aspect = aspectModel.aspect();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( aspect );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }

   @Test
   public void testAspectModelWithBlankNodeEnglishAndGerman() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION );
      final Aspect aspect = aspectModel.aspect();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( aspect );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }
}
