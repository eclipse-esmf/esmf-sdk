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

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;

public class LanguageCollectorTest extends MetaModelVersions {

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
