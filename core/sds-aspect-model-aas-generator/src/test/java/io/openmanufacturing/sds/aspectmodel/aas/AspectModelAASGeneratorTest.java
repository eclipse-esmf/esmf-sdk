/*
 * Copyright (c) 2021, 2022 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.aspectmodel.aas;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.adminshell.aas.v3.dataformat.DeserializationException;
import io.adminshell.aas.v3.dataformat.xml.XmlDeserializer;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.ConceptDescription;
import io.adminshell.aas.v3.model.DataSpecificationIEC61360;
import io.adminshell.aas.v3.model.EmbeddedDataSpecification;
import io.adminshell.aas.v3.model.Property;
import io.adminshell.aas.v3.model.SubmodelElement;
import io.adminshell.aas.v3.model.SubmodelElementCollection;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;

class AspectModelAASGeneratorTest {

   AspectModelAASGenerator generator = new AspectModelAASGenerator();

   @Test
   void testGenerateAasxFromBammAspectWithListAndAdditionalProperty()
         throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY );
      assertEquals( 3, env.getConceptDescriptions().size() );
      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 2, env.getSubmodels().get( 0 ).getSubmodelElements().size() );

      final Set<String> semanticIds =
            Set.of( "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty",
                  "urn:bamm:io.openmanufacturing.test:1.0.0#testPropertyTwo" );

      checkDataSpecificationIEC61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithEntity() throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals(
            1,
            env.getSubmodels().get( 0 ).getSubmodelElements().size(),
            "Not exactly one SubmodelElement in Submodel." );
      assertTrue(
            env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 ) instanceof SubmodelElementCollection,
            "SubmodelElement is not a SubmodelElementCollection." );
      SubmodelElementCollection collection =
            (SubmodelElementCollection) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals(
            1, collection.getValues().size(), "Not exactly one Element in SubmodelElementCollection" );
      assertEquals( "entityProperty", collection.getValues().stream().findFirst().get().getIdShort() );

      getDataSpecificationIEC61360( "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithCollection() throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_COLLECTION );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals(
            1,
            env.getSubmodels().get( 0 ).getSubmodelElements().size(),
            "Not exactly one SubmodelElement in AAS." );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue(
            submodelElement instanceof SubmodelElementCollection,
            "SubmodelElement is not a SubmodelElementCollection" );
      assertEquals( "testProperty", submodelElement.getIdShort() );

      getDataSpecificationIEC61360( "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithList() throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_LIST );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals(
            1,
            env.getSubmodels().get( 0 ).getSubmodelElements().size(),
            "Not exactly one SubmodelElement in AAS." );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue(
            submodelElement instanceof SubmodelElementCollection,
            "SubmodelElement is not a SubmodelElementCollection" );
      assertEquals( "testProperty", submodelElement.getIdShort() );
      assertTrue( ((SubmodelElementCollection) submodelElement).getOrdered(), "List is not ordered." );

      getDataSpecificationIEC61360( "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithSet() throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_SET );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals(
            1,
            env.getSubmodels().get( 0 ).getSubmodelElements().size(),
            "Not exactly one SubmodelElement in AAS." );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue(
            submodelElement instanceof SubmodelElementCollection,
            "SubmodelElement is not a SubmodelElementCollection" );
      assertEquals( "testProperty", submodelElement.getIdShort() );
      assertFalse( ((SubmodelElementCollection) submodelElement).getOrdered(), "Set is ordered." );
      assertFalse(
            ((SubmodelElementCollection) submodelElement).getAllowDuplicates(),
            "Set allows duplicates." );

      getDataSpecificationIEC61360( "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithSortedSet() throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_SORTED_SET );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals(
            1,
            env.getSubmodels().get( 0 ).getSubmodelElements().size(),
            "Not exactly one SubmodelElement in AAS." );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue(
            submodelElement instanceof SubmodelElementCollection,
            "SubmodelElement is not a SubmodelElementCollection" );
      assertEquals( "testProperty", submodelElement.getIdShort() );
      assertTrue(
            ((SubmodelElementCollection) submodelElement).getOrdered(), "Sorted Set is not ordered." );
      assertFalse(
            ((SubmodelElementCollection) submodelElement).getAllowDuplicates(),
            "Sorted Set allows duplicates." );

      getDataSpecificationIEC61360( "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithEitherWithComplexTypes()
         throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals(
            1,
            env.getSubmodels().get( 0 ).getSubmodelElements().size(),
            1,
            "Not exactly one Element in SubmodelElements." );
      final SubmodelElementCollection elementCollection =
            ((SubmodelElementCollection) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 ));
      final Set<String> testValues =
            Set.of( "RightEntity", "LeftEntity" );
      assertTrue(
            elementCollection.getValues().stream().anyMatch( x -> testValues.contains( x.getIdShort() ) ),
            "Neither left nor right entity contained." );

      final Set<String> semanticIds =
            Set.of( "urn:bamm:io.openmanufacturing.test:1.0.0#result",
                  "urn:bamm:io.openmanufacturing.test:1.0.0#error" );

      checkDataSpecificationIEC61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithQuantifiable()
         throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITH_UNIT );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals(
            1,
            env.getSubmodels().get( 0 ).getSubmodelElements().size(),
            1,
            "Not exactly one Element in SubmodelElements." );
      final SubmodelElement element = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( "testProperty", element.getIdShort() );

      final DataSpecificationIEC61360 dataSpecificationContent =
            getDataSpecificationIEC61360( "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty", env );

      assertEquals( "percent", dataSpecificationContent.getUnit(), "Unit is not percent" );
   }

   @Test
   void testGenerateAasxFromBammWithConstraint() throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_CONSTRAINT );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals(
            1,
            env.getSubmodels().get( 0 ).getSubmodelElements().size(),
            6,
            "Not exactly six Elements in SubmodelElements." );
      final SubmodelElement submodelElement =
            env.getSubmodels().get( 0 ).getSubmodelElements().stream()
                  .filter( x -> x.getIdShort().equals( "stringLcProperty" ) )
                  .findFirst()
                  .orElseThrow();
      assertEquals( "stringLcProperty", submodelElement.getIdShort() );

      final Set<String> semanticIds =
            Set.of( "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectstringLcProperty",
                  "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectdoubleRcProperty",
                  "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectintRcProperty",
                  "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectbigIntRcProperty",
                  "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectfloatRcProperty",
                  "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectstringRegexcProperty" );

      checkDataSpecificationIEC61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithRecursivePropertyWithOptional()
         throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect(
                  TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
   }

   @Test
   void testGenerateAasxFromBammAspectWithCode() throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_CODE );
      assertEquals( 2, env.getConceptDescriptions().size() );
      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size() );
      Property submodelElement = (Property) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals(
            "http://www.w3.org/2001/XMLSchema#int",
            submodelElement.getValueType(),
            "Value type not int" );

      getDataSpecificationIEC61360( "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromBammAspectWithEnumeration()
         throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENUMERATION );

      assertEquals( 2, env.getConceptDescriptions().size() );

      final DataSpecificationIEC61360 dataSpecificationContent =
            (DataSpecificationIEC61360)
                  env.getConceptDescriptions().stream()
                        .filter( x -> x.getIdShort().equals( "TestEnumeration" ) )
                        .findFirst()
                        .get()
                        .getEmbeddedDataSpecifications()
                        .stream()
                        .findFirst()
                        .get()
                        .getDataSpecificationContent();
      assertEquals( 3, dataSpecificationContent.getValueList().getValueReferencePairTypes().size() );

      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size() );
      final Property submodelElement =
            (Property) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals(
            "http://www.w3.org/2001/XMLSchema#integer",
            submodelElement.getValueType(),
            "Value type not int" );
   }

   @ParameterizedTest
   @EnumSource(
         value = TestAspect.class,
         mode = EnumSource.Mode.EXCLUDE,
         names = {
               "ASPECT_WITH_STRING_ENUMERATION"
         } )
   // anonymous enumeration in test has no urn for enum values but is required for Concept
   // Description referencing
   public void testGeneration( final TestAspect testAspect )
         throws IOException, DeserializationException {
      final AssetAdministrationShellEnvironment env =
            getAssetAdministrationShellFromAspect( testAspect );
      assertTrue( env.getSubmodels().size() >= 1, "No Submodel in AAS present." );
   }

   private void checkDataSpecificationIEC61360(
         final Set<String> semanticIds, final AssetAdministrationShellEnvironment env ) {
      semanticIds.forEach( x -> getDataSpecificationIEC61360( x, env ) );
   }

   private DataSpecificationIEC61360 getDataSpecificationIEC61360(
         final String semanticId, final AssetAdministrationShellEnvironment env ) {
      final List<ConceptDescription> conceptDescriptions = env.getConceptDescriptions();
      final List<ConceptDescription> filteredConceptDescriptions =
            conceptDescriptions.stream()
                  .filter( x -> x.getIdentification().getIdentifier().equals( semanticId ) )
                  .collect( Collectors.toList() );
      assertEquals(
            1,
            filteredConceptDescriptions.size(),
            "Not exactly 1 ConceptDescription for semanticId. " + semanticId );

      final List<EmbeddedDataSpecification> embeddedDataSpecifications =
            filteredConceptDescriptions.get( 0 ).getEmbeddedDataSpecifications();
      assertEquals(
            1,
            embeddedDataSpecifications.size(),
            "Not exactly 1 EmbeddedDataSpecification for semanticId. " + semanticId );

      assertTrue(
            embeddedDataSpecifications.stream().findFirst().isPresent(),
            "There is no EmbeddedDataSpecification" );
      return (DataSpecificationIEC61360)
            embeddedDataSpecifications.stream().findFirst().get().getDataSpecificationContent();
   }

   private AssetAdministrationShellEnvironment getAssetAdministrationShellFromAspect(
         TestAspect testAspect ) throws DeserializationException, IOException {
      final Aspect aspect = loadAspect( testAspect );
      final ByteArrayOutputStream out = generator.generateXmlOutput( aspect );
      return loadAASX( out );
   }

   private Aspect loadAspect( TestAspect testAspect ) {
      final VersionedModel model = TestResources.getModel( testAspect, KnownVersion.getLatest() ).get();
      return AspectModelLoader.fromVersionedModelUnchecked( model );
   }

   private AssetAdministrationShellEnvironment loadAASX( ByteArrayOutputStream byteStream )
         throws DeserializationException {
      final XmlDeserializer deserializer = new XmlDeserializer();
      return deserializer.read( new ByteArrayInputStream( byteStream.toByteArray() ) );
   }
}
