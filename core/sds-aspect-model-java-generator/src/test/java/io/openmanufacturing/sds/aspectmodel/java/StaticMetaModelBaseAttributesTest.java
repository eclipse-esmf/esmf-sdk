/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.java;

import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.test.TestAspect;

public class StaticMetaModelBaseAttributesTest extends StaticMetaModelGeneratorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMetaModelBaseAttributesOfGeneratedProperty( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BOOLEAN;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final ImmutableSet<String> expectedArguments = ImmutableSet.<String> builder()
            .add( "KnownVersion." + KnownVersion.getLatest().toString() )
            .add( "AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\")" )
            .add( "\"testBoolean\"" )
            .build();
      final ImmutableMap<String, Set<String>> expectedMetaModelBaseAttributeArguments = ImmutableMap.<String, Set<String>> builder()
            .put( "TEST_BOOLEAN", expectedArguments ).build();

      result.assertMetaModelBaseAttributesForProperties( "MetaAspectWithBoolean",
            expectedMetaModelBaseAttributeArguments );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMetaModelBaseAttributesOfGeneratedPropertyWithAllAttributes( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_ALL_BASE_ATTRIBUTES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String expectedMetaModelBaseAttributeBuilderCall = "MetaModelBaseAttributes.builderFor(\"testBoolean\")"
            + ".withMetaModelVersion(KnownVersion." + KnownVersion.getLatest() + ")"
            + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\"))"
            + ".withPreferredName(Locale.forLanguageTag(\"de\"), \"Test Boolean\")"
            + ".withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Boolean\")"
            + ".withDescription(Locale.forLanguageTag(\"de\"), \"Test Beschreibung\")"
            + ".withDescription(Locale.forLanguageTag(\"en\"), \"Test Description\")"
            + ".withSee(\"http://example.com/me\")"
            + ".withSee(\"http://example.com/omp\")"
            + ".build()";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithAllBaseAttributes",
            ImmutableMap.<String, String> builder().put( "TEST_BOOLEAN", expectedMetaModelBaseAttributeBuilderCall ).build(), 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMetaModelBaseAttributesOfGeneratedPropertyWithPreferredNames( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_PREFERRED_NAMES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String expectedMetaModelBaseAttributeBuilderCall = "MetaModelBaseAttributes.builderFor(\"testBoolean\")"
            + ".withMetaModelVersion(KnownVersion." + KnownVersion.getLatest() + ")"
            + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\"))"
            + ".withPreferredName(Locale.forLanguageTag(\"de\"), \"Test Boolean\")"
            + ".withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Boolean\")"
            + ".build()";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithPreferredNames",
            ImmutableMap.<String, String> builder().put( "TEST_BOOLEAN", expectedMetaModelBaseAttributeBuilderCall ).build(), 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMetaModelBaseAttributesOfGeneratedPropertyWithDescriptions( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_DESCRIPTIONS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String expectedMetaModelBaseAttributeBuilderCall = "MetaModelBaseAttributes.builderFor(\"testBoolean\")"
            + ".withMetaModelVersion(KnownVersion." + KnownVersion.getLatest() + ")"
            + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\"))"
            + ".withDescription(Locale.forLanguageTag(\"de\"), \"Test Beschreibung\")"
            + ".withDescription(Locale.forLanguageTag(\"en\"), \"Test Description\")"
            + ".build()";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithDescriptions",
            ImmutableMap.<String, String> builder().put( "TEST_BOOLEAN", expectedMetaModelBaseAttributeBuilderCall ).build(), 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMetaModelBaseAttributesOfGeneratedPropertyWithSee( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_SEE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String expectedMetaModelBaseAttributeBuilderCall = "MetaModelBaseAttributes.builderFor(\"testBoolean\")"
            + ".withMetaModelVersion(KnownVersion." + KnownVersion.getLatest() + ")"
            + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\"))"
            + ".withSee(\"http://example.com/me\")"
            + ".withSee(\"http://example.com/omp\")"
            + ".build()";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithSee",
            ImmutableMap.<String, String> builder().put( "TEST_BOOLEAN", expectedMetaModelBaseAttributeBuilderCall ).build(), 0 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGeneratedMetaModelContainsRequiredMethods( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BOOLEAN;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final ImmutableMap<String, String> expectedMethodBodies = ImmutableMap.<String, String> builder()
            .put( "getModelClass", "return AspectWithBoolean.class;" )
            .put( "getAspectModelUrn", "return AspectModelUrn.fromUrn(MODEL_ELEMENT_URN);" )
            .put( "getMetaModelVersion", "return KnownVersion." + KnownVersion.getLatest().toString() + ";" )
            .put( "getName", "return \"AspectWithBoolean\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getPropertyType", "return Boolean.class;" )
            .build();

      result.assertMethods( "MetaAspectWithBoolean", expectedMethodBodies );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGeneratedMetaModelContainsOptionalMethods( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ALL_BASE_ATTRIBUTES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String getPreferredNamesBody = "return new HashSet<>() {\n"
            + "\n"
            + "    {\n"
            + "        add(new LangString(\"Aspekt Mit Boolean\", Locale.forLanguageTag(\"de\")));\n"
            + "        add(new LangString(\"Aspect With Boolean\", Locale.forLanguageTag(\"en\")));\n"
            + "    }\n"
            + "};";

      final String getDescriptionsBody = "return new HashSet<>() {\n"
            + "\n"
            + "    {\n"
            + "        add(new LangString(\"Test Beschreibung\", Locale.forLanguageTag(\"de\")));\n"
            + "        add(new LangString(\"Test Description\", Locale.forLanguageTag(\"en\")));\n"
            + "    }\n"
            + "};";

      final ImmutableMap<String, String> expectedMethodBodies = ImmutableMap.<String, String> builder()
            .put( "getModelClass", "return AspectWithAllBaseAttributes.class;" )
            .put( "getAspectModelUrn", "return AspectModelUrn.fromUrn(MODEL_ELEMENT_URN);" )
            .put( "getMetaModelVersion", "return KnownVersion." + KnownVersion.getLatest().toString() + ";" )
            .put( "getName", "return \"AspectWithAllBaseAttributes\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getSee", "return Arrays.asList(\"http://example.com/omp\");" )
            .put( "getPreferredNames", getPreferredNamesBody )
            .put( "getDescriptions", getDescriptionsBody )
            .put( "getPropertyType", "return Boolean.class;" )
            .build();

      result.assertMethods( "MetaAspectWithAllBaseAttributes", expectedMethodBodies );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGeneratedMetaModelContainsGetPreferredNamesMethod( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PREFERRED_NAMES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String getPreferredNamesBody = "return new HashSet<>() {\n"
            + "\n"
            + "    {\n"
            + "        add(new LangString(\"Aspekt Mit Boolean\", Locale.forLanguageTag(\"de\")));\n"
            + "        add(new LangString(\"Aspect With Boolean\", Locale.forLanguageTag(\"en\")));\n"
            + "    }\n"
            + "};";

      final ImmutableMap<String, String> expectedMethodBodies = ImmutableMap.<String, String> builder()
            .put( "getModelClass", "return AspectWithPreferredNames.class;" )
            .put( "getAspectModelUrn", "return AspectModelUrn.fromUrn(MODEL_ELEMENT_URN);" )
            .put( "getMetaModelVersion", "return KnownVersion." + KnownVersion.getLatest().toString() + ";" )
            .put( "getName", "return \"AspectWithPreferredNames\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getPreferredNames", getPreferredNamesBody )
            .put( "getPropertyType", "return Boolean.class;" )
            .build();

      result.assertMethods( "MetaAspectWithPreferredNames", expectedMethodBodies );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGeneratedMetaModelContainsGetDescriptionsMethod( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_DESCRIPTIONS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String getDescriptionsBody = "return new HashSet<>() {\n"
            + "\n"
            + "    {\n"
            + "        add(new LangString(\"Test Beschreibung\", Locale.forLanguageTag(\"de\")));\n"
            + "        add(new LangString(\"Test Description\", Locale.forLanguageTag(\"en\")));\n"
            + "    }\n"
            + "};";

      final ImmutableMap<String, String> expectedMethodBodies = ImmutableMap.<String, String> builder()
            .put( "getModelClass", "return AspectWithDescriptions.class;" )
            .put( "getAspectModelUrn", "return AspectModelUrn.fromUrn(MODEL_ELEMENT_URN);" )
            .put( "getMetaModelVersion", "return KnownVersion." + KnownVersion.getLatest().toString() + ";" )
            .put( "getName", "return \"AspectWithDescriptions\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getDescriptions", getDescriptionsBody )
            .put( "getPropertyType", "return Boolean.class;" )
            .build();

      result.assertMethods( "MetaAspectWithDescriptions", expectedMethodBodies );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGeneratedMetaModelContainsGetSeeMethod( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_SEE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final ImmutableMap<String, String> expectedMethodBodies = ImmutableMap.<String, String> builder()
            .put( "getModelClass", "return AspectWithPropertyWithSee.class;" )
            .put( "getAspectModelUrn", "return AspectModelUrn.fromUrn(MODEL_ELEMENT_URN);" )
            .put( "getMetaModelVersion", "return KnownVersion." + KnownVersion.getLatest().toString() + ";" )
            .put( "getName", "return \"AspectWithPropertyWithSee\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getPropertyType", "return Boolean.class;" )
            .build();

      result.assertMethods( "MetaAspectWithPropertyWithSee", expectedMethodBodies );
   }
}
