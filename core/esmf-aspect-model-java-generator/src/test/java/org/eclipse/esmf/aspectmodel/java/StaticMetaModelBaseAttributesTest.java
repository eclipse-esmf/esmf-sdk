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

package org.eclipse.esmf.aspectmodel.java;

import java.io.IOException;
import java.util.Set;

import org.eclipse.esmf.test.TestAspect;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

class StaticMetaModelBaseAttributesTest extends StaticMetaModelGeneratorTest {

   @Test
   void testMetaModelBaseAttributesOfGeneratedProperty() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BOOLEAN;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final ImmutableSet<String> expectedArguments = ImmutableSet.<String> builder()
            .add( "AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\")" )
            .build();
      final ImmutableMap<String, Set<String>> expectedMetaModelBaseAttributeArguments = ImmutableMap.<String, Set<String>> builder()
            .put( "TEST_BOOLEAN", expectedArguments ).build();

      result.assertMetaModelBaseAttributesForProperties( "MetaAspectWithBoolean", expectedMetaModelBaseAttributeArguments );
   }

   @Test
   void testMetaModelBaseAttributesOfGeneratedPropertyWithAllAttributes() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_ALL_BASE_ATTRIBUTES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final String expectedMetaModelBaseAttributeBuilderCall = "MetaModelBaseAttributes.builder()"
            + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\"))"
            + ".withPreferredName(Locale.forLanguageTag(\"de\"), \"Test Boolean\")"
            + ".withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Boolean\")"
            + ".withDescription(Locale.forLanguageTag(\"de\"), \"Test Beschreibung\")"
            + ".withDescription(Locale.forLanguageTag(\"en\"), \"Test Description\")"
            + ".withSee(\"http://example.com/\")"
            + ".withSee(\"http://example.com/me\")"
            + ".build()";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithAllBaseAttributes",
            ImmutableMap.<String, String> builder().put( "TEST_BOOLEAN", expectedMetaModelBaseAttributeBuilderCall ).build(), 0 );
   }

   @Test
   void testMetaModelBaseAttributesOfGeneratedPropertyWithPreferredNames() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_PREFERRED_NAMES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final String expectedMetaModelBaseAttributeBuilderCall = "MetaModelBaseAttributes.builder()"
            + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\"))"
            + ".withPreferredName(Locale.forLanguageTag(\"de\"), \"Test Boolean\")"
            + ".withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Boolean\")"
            + ".build()";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithPreferredNames",
            ImmutableMap.<String, String> builder().put( "TEST_BOOLEAN", expectedMetaModelBaseAttributeBuilderCall ).build(), 0 );
   }

   @Test
   void testMetaModelBaseAttributesOfGeneratedPropertyWithDescriptions() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_DESCRIPTIONS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final String expectedMetaModelBaseAttributeBuilderCall = "MetaModelBaseAttributes.builder()"
            + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\"))"
            + ".withDescription(Locale.forLanguageTag(\"de\"), \"Test Beschreibung\")"
            + ".withDescription(Locale.forLanguageTag(\"en\"), \"Test Description\")"
            + ".build()";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithDescriptions",
            ImmutableMap.<String, String> builder().put( "TEST_BOOLEAN", expectedMetaModelBaseAttributeBuilderCall ).build(), 0 );
   }

   @Test
   void testMetaModelBaseAttributesOfGeneratedPropertyWithSee() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_SEE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final String expectedMetaModelBaseAttributeBuilderCall = "MetaModelBaseAttributes.builder()"
            + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testBoolean\"))"
            + ".withSee(\"http://example.com/\")"
            + ".withSee(\"http://example.com/me\")"
            + ".build()";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithSee",
            ImmutableMap.<String, String> builder().put( "TEST_BOOLEAN", expectedMetaModelBaseAttributeBuilderCall ).build(), 0 );
   }

   @Test
   void testGeneratedMetaModelContainsRequiredMethods() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BOOLEAN;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final ImmutableMap<String, String> expectedMethodBodies = ImmutableMap.<String, String> builder()
            .put( "getModelClass", "return AspectWithBoolean.class;" )
            .put( "getAspectModelUrn", "return AspectModelUrn.fromUrn(MODEL_ELEMENT_URN);" )
            .put( "getMetaModelVersion", "return KnownVersion.getLatest();" )
            .put( "getName", "return \"AspectWithBoolean\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getPropertyType", "return Boolean.class;" )
            .put( "getValue", "return object.isTestBoolean();" )
            .put( "getContainingType", "return AspectWithBoolean.class;" )
            .build();

      result.assertMethods( "MetaAspectWithBoolean", expectedMethodBodies );
   }

   @Test
   void testGeneratedMetaModelContainsOptionalMethods() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ALL_BASE_ATTRIBUTES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect ) );
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
            .put( "getMetaModelVersion", "return KnownVersion.getLatest();" )
            .put( "getName", "return \"AspectWithAllBaseAttributes\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getSee", "return Arrays.asList(\"http://example.com/\");" )
            .put( "getPreferredNames", getPreferredNamesBody )
            .put( "getDescriptions", getDescriptionsBody )
            .put( "getPropertyType", "return Boolean.class;" )
            .put( "getValue", "return object.isTestBoolean();" )
            .put( "getContainingType", "return AspectWithAllBaseAttributes.class;" )
            .build();

      result.assertMethods( "MetaAspectWithAllBaseAttributes", expectedMethodBodies );
   }

   @Test
   void testGeneratedMetaModelContainsGetPreferredNamesMethod() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PREFERRED_NAMES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect ) );
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
            .put( "getMetaModelVersion", "return KnownVersion.getLatest();" )
            .put( "getName", "return \"AspectWithPreferredNames\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getPreferredNames", getPreferredNamesBody )
            .put( "getPropertyType", "return Boolean.class;" )
            .put( "getValue", "return object.isTestBoolean();" )
            .put( "getContainingType", "return AspectWithPreferredNames.class;" )
            .build();

      result.assertMethods( "MetaAspectWithPreferredNames", expectedMethodBodies );
   }

   @Test
   void testGeneratedMetaModelContainsGetDescriptionsMethod() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_DESCRIPTIONS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect ) );
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
            .put( "getMetaModelVersion", "return KnownVersion.getLatest();" )
            .put( "getName", "return \"AspectWithDescriptions\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getDescriptions", getDescriptionsBody )
            .put( "getPropertyType", "return Boolean.class;" )
            .put( "getValue", "return object.isTestBoolean();" )
            .put( "getContainingType", "return AspectWithDescriptions.class;" )
            .build();

      result.assertMethods( "MetaAspectWithDescriptions", expectedMethodBodies );
   }

   @Test
   void testGeneratedMetaModelContainsGetSeeMethod() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_SEE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final ImmutableMap<String, String> expectedMethodBodies = ImmutableMap.<String, String> builder()
            .put( "getModelClass", "return AspectWithPropertyWithSee.class;" )
            .put( "getAspectModelUrn", "return AspectModelUrn.fromUrn(MODEL_ELEMENT_URN);" )
            .put( "getMetaModelVersion", "return KnownVersion.getLatest();" )
            .put( "getName", "return \"AspectWithPropertyWithSee\";" )
            .put( "getProperties", "return Arrays.asList(TEST_BOOLEAN);" )
            .put( "getAllProperties", "return getProperties();" )
            .put( "getPropertyType", "return Boolean.class;" )
            .put( "getValue", "return object.isTestBoolean();" )
            .put( "getContainingType", "return AspectWithPropertyWithSee.class;" )
            .build();

      result.assertMethods( "MetaAspectWithPropertyWithSee", expectedMethodBodies );
   }
}
