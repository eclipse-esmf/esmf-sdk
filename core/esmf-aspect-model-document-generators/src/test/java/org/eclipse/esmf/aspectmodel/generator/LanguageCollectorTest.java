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

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

public class LanguageCollectorTest extends MetaModelVersions {

   @ParameterizedTest
   @EnumSource( value = TestAspect.class,
         mode = EnumSource.Mode.EXCLUDE,
         names = {
               // Contains a language tag on an element that does not count to the collected languages, so exclude it
               "ASPECT_WITH_MULTILANGUAGE_EXAMPLE_VALUE"
         } )
   public void testCollectLanguagesExpectSuccess( final TestAspect testAspect ) {
      final Model model = TestResources.getModelWithoutResolution( testAspect, KnownVersion.getLatest() ).getModel();
      final Aspect aspect = AspectModelLoader.getSingleAspectUnchecked( TestResources.getModelWithoutResolution( testAspect,
            KnownVersion.getLatest() ) );

      final Set<Locale> langTagsInModel = Streams.stream( model.listStatements() )
            .filter( statement -> statement.getObject().isLiteral() )
            .map( Statement::getLiteral )
            .map( Literal::getLanguage )
            .filter( language -> !language.isEmpty() )
            .map( Locale::forLanguageTag )
            .collect( Collectors.toSet() );

      final Set<Locale> localesFromAspect = LanguageCollector.collectUsedLanguages( aspect );
      final Set<Locale> localesFromModel = LanguageCollector.collectUsedLanguages( model );
      // Sets are not necessarily identical; e.g., if a model refers to samm-c:Text, the language collector will find
      // samm-c:Text's description's language, but the simple string matcher will find nothing.
      assertThat( localesFromAspect ).isEqualTo( localesFromModel );
      assertThat( localesFromAspect ).containsAll( langTagsInModel );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRdfModelOnlyEnglish( final KnownVersion metaModelVersion ) {
      final Model model = TestResources.getModel( TestAspect.ASPECT_WITH_ENGLISH_DESCRIPTION, metaModelVersion )
            .get().getModel();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( model );
      assertThat( languages ).containsExactly( Locale.forLanguageTag( "en" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRdfModelEnglishAndGermanInProperty( final KnownVersion metaModelVersion ) {
      final Model model = TestResources.getModel( TestAspect.ASPECT_WITH_DESCRIPTION_IN_PROPERTY, metaModelVersion )
            .get().getModel();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( model );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectModelEnglishAndGermanInProperty( final KnownVersion metaModelVersion ) {
      final VersionedModel model = TestResources
            .getModel( TestAspect.ASPECT_WITH_DESCRIPTION_IN_PROPERTY, metaModelVersion ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspect( model ).get();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( aspect );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectModelOnlyEnglish( final KnownVersion metaModelVersion ) {
      final VersionedModel model = TestResources
            .getModel( TestAspect.ASPECT_WITH_ENGLISH_DESCRIPTION, metaModelVersion ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspect( model ).get();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( aspect );
      assertThat( languages ).containsExactly( Locale.forLanguageTag( "en" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRdfModelEnglishAndGerman( final KnownVersion metaModelVersion ) {
      final Model model = TestResources
            .getModel( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION, metaModelVersion ).get()
            .getModel();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( model );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectModelEnglishAndGerman( final KnownVersion metaModelVersion ) {
      final VersionedModel model = TestResources
            .getModel( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION, metaModelVersion ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspect( model ).get();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( aspect );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectModelWithBlankNodeEnglishAndGerman( final KnownVersion metaModelVersion ) {
      final VersionedModel model = TestResources.getModel( TestAspect.ASPECT_WITH_BLANK_NODE, metaModelVersion ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspect( model ).get();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( aspect );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRdfModelWithBlankNodeEnglishAndGerman( final KnownVersion metaModelVersion ) {
      final VersionedModel model = TestResources.getModel( TestAspect.ASPECT_WITH_BLANK_NODE, metaModelVersion ).get();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( model.getModel() );
      assertThat( languages )
            .containsExactlyInAnyOrder( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "de" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRdfModelWithoutLanguageTag( final KnownVersion metaModelVersion ) {
      final VersionedModel model = TestResources.getModel( TestAspect.ASPECT_WITH_BINARY, metaModelVersion ).get();
      final Set<Locale> languages = LanguageCollector.collectUsedLanguages( model.getModel() );
      assertThat( languages ).contains( Locale.forLanguageTag( "en" ) );
   }
}
