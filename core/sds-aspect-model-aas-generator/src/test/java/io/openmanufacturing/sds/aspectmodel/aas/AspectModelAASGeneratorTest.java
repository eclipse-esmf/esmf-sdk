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

import io.adminshell.aas.v3.dataformat.DeserializationException;
import io.adminshell.aas.v3.dataformat.xml.XmlDeserializer;
import io.adminshell.aas.v3.model.*;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectModelAASGeneratorTest {

   AspectModelAASGenerator generator = new AspectModelAASGenerator();

   @Test
   void test_generate_aasx_from_bamm_aspect_with_list_and_additional_property() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY );
      assertEquals( 3, env.getConceptDescriptions().size());
      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 2, env.getSubmodels().get( 0 ).getSubmodelElements().size() );

      Set<String> semanticIds = Stream.of(
            "urn:bamm:io.openmanufacturing.test:1.0.0#testProperty",
            "urn:bamm:io.openmanufacturing.test:1.0.0#testPropertyTwo")
            .collect(Collectors.toCollection(HashSet::new));

      checkDataSpecificationIEC61360(semanticIds, env);
   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_entity() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in Submodel." );
      assertTrue( env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 ) instanceof SubmodelElementCollection,
            "SubmodelElement is not a SubmodelElementCollection." );
      SubmodelElementCollection collection = (SubmodelElementCollection) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( 1, collection.getValues().size(), "Not exactly one Element in SubmodelElementCollection" );
      assertEquals( "entityProperty", collection.getValues().stream().findFirst().get().getIdShort() );

      DataSpecificationIEC61360 dataSpecificationContent =
            getDataSpecificationIEC61360( "EntityCharacteristic", env );
   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_collection() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_COLLECTION );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in AAS." );
      SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue( submodelElement instanceof SubmodelElementCollection, "SubmodelElement is not a SubmodelElementCollection" );
      assertEquals( "testProperty", submodelElement.getIdShort() );

      DataSpecificationIEC61360 dataSpecificationContent =
            getDataSpecificationIEC61360( "TestCollection", env );
   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_list() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_LIST );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in AAS." );
      SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue( submodelElement instanceof SubmodelElementCollection, "SubmodelElement is not a SubmodelElementCollection" );
      assertEquals( "testProperty", submodelElement.getIdShort() );
      assertTrue( ((SubmodelElementCollection) submodelElement).getOrdered(), "List is not ordered." );

      DataSpecificationIEC61360 dataSpecificationContent =
            getDataSpecificationIEC61360( "TestList", env );
   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_set() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_SET );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in AAS." );
      SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue( submodelElement instanceof SubmodelElementCollection, "SubmodelElement is not a SubmodelElementCollection" );
      assertEquals( "testProperty", submodelElement.getIdShort() );
      assertFalse( ((SubmodelElementCollection) submodelElement).getOrdered(), "Set is ordered." );
      assertFalse( ((SubmodelElementCollection) submodelElement).getAllowDuplicates(), "Set allows duplicates." );

      DataSpecificationIEC61360 dataSpecificationContent =
            getDataSpecificationIEC61360( "TestSet", env );
   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_sorted_set() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_SORTED_SET );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in AAS." );
      SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue( submodelElement instanceof SubmodelElementCollection, "SubmodelElement is not a SubmodelElementCollection" );
      assertEquals( "testProperty", submodelElement.getIdShort() );
      assertTrue( ((SubmodelElementCollection) submodelElement).getOrdered(), "Sorted Set is not ordered." );
      assertFalse( ((SubmodelElementCollection) submodelElement).getAllowDuplicates(), "Sorted Set allows duplicates." );

      DataSpecificationIEC61360 dataSpecificationContent =
            getDataSpecificationIEC61360( "TestSortedSet", env );
   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_either_with_complex_types() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), 1, "Not exactly one Element in SubmodelElements." );
      SubmodelElementCollection elementCollection = ((SubmodelElementCollection) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 ));
      Set<String> testValues = Stream.of( "RightEntity", "LeftEntity" ).collect( Collectors.toCollection( HashSet::new ) );
      assertTrue( elementCollection.getValues().stream().anyMatch( x -> testValues.contains( x.getIdShort() ) ), "Neither left nor right entity contained." );

      Set<String> semanticIds = Stream.of(
            "urn:bamm:io.openmanufacturing.test:1.0.0#result",
            "urn:bamm:io.openmanufacturing.test:1.0.0#error")
            .collect(Collectors.toCollection(HashSet::new));

      checkDataSpecificationIEC61360(semanticIds, env);
   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_quantifiable() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITH_UNIT );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), 1, "Not exactly one Element in SubmodelElements." );
      SubmodelElement element = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( "testProperty", element.getIdShort() );

      DataSpecificationIEC61360 dataSpecificationContent =
            getDataSpecificationIEC61360( "TestQuantifiable", env );

      assertEquals( "percent", dataSpecificationContent.getUnit(), "Unit is not percent" );
   }

   @Test
   void test_generate_aasx_from_bamm_with_constraint() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_CONSTRAINT );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), 6, "Not exactly six Elements in SubmodelElements." );
      SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().stream().
            filter( x -> x.getIdShort().equals( "stringLcProperty" ) ).findFirst().orElseThrow();
      assertEquals( "stringLcProperty", submodelElement.getIdShort() );


      Set<String> semanticIds = Stream.of(
            "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectstringLcProperty",
            "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectdoubleRcProperty",
            "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectintRcProperty",
            "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectbigIntRcProperty",
            "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectfloatRcProperty",
            "urn:bamm:io.openmanufacturing.test:1.0.0#TestAspectstringRegexcProperty")
            .collect(Collectors.toCollection(HashSet::new));

      checkDataSpecificationIEC61360(semanticIds, env);

   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_recursive_property_with_optional() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL );

      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_code() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_CODE );
      assertEquals( 2, env.getConceptDescriptions().size() );
      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size() );
      Property submodelElement = (Property) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( "http://www.w3.org/2001/XMLSchema#int", submodelElement.getValueType(), "Value type not int" );

      DataSpecificationIEC61360 dataSpecificationContent =
            getDataSpecificationIEC61360( "TestCode", env );

   }

   @Test
   void test_generate_aasx_from_bamm_aspect_with_enumeration() throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENUMERATION );

      assertEquals( 2, env.getConceptDescriptions().size() );

      DataSpecificationIEC61360 dataSpecificationContent = (DataSpecificationIEC61360)
            env.getConceptDescriptions().stream().filter( x -> x.getIdShort().equals( "TestEnumeration" ) ).findFirst().get()
                  .getEmbeddedDataSpecifications().stream().findFirst().get( )
                  .getDataSpecificationContent();
      assertEquals( 3, dataSpecificationContent.getValueList().getValueReferencePairTypes().size());

      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size() );
      Property submodelElement = (Property) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( "http://www.w3.org/2001/XMLSchema#integer", submodelElement.getValueType(), "Value type not int" );
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         "ASPECT_WITH_ABSTRACT_ENTITY",
         // TODO io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException: java.io.FileNotFoundException: The AspectModel: urn:bamm:io.openmanufacturing.test:1.0.0#AspectWithAbstractEntity could not be found in directory: valid/bamm_1_0_0/io.openmanufacturing.test/1.0.0
         "ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY",
         // TODO io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException: java.io.FileNotFoundException: The AspectModel: urn:bamm:io.openmanufacturing.test:1.0.0#AspectWithCollectionWithAbstractEntity could not be found in directory: valid/bamm_1_0_0/io.openmanufacturing.test/1.0.0
         "ASPECT_WITH_ABSTRACT_SINGLE_ENTITY",
         // TODO io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException: java.io.FileNotFoundException: The AspectModel: urn:bamm:io.openmanufacturing.test:1.0.0#AspectWithAbstractSingleEntity could not be found in directory: valid/bamm_1_0_0/io.openmanufacturing.test/1.0.0
         "ASPECT_WITH_STRING_ENUMERATION",//TODO io.adminshell.aas.v3.dataformat.DeserializationException: deserialization failed,
         "ASPECT_WITH_EVENT",
         "ASPECT_WITHOUT_PROPERTIES_AND_OPERATIONS"
   } )
   public void testGeneration( final TestAspect testAspect ) throws IOException, DeserializationException {
      AssetAdministrationShellEnvironment env = getAssetAdministrationShellFromAspect( testAspect );
      assertTrue( env.getSubmodels().size() >= 1, "No Submodel in AAS present." );
   }

   private void checkDataSpecificationIEC61360(Set<String> semanticIds, AssetAdministrationShellEnvironment env){
      semanticIds.stream().map( x -> getDataSpecificationIEC61360( x, env ) );
   }

   private DataSpecificationIEC61360 getDataSpecificationIEC61360( String semanticId, AssetAdministrationShellEnvironment env ) {
      List<ConceptDescription> conceptDescriptions = env.getConceptDescriptions();
      List<ConceptDescription> filteredConceptDescriptions =
            conceptDescriptions.stream().filter( x -> x.getIdShort().equals( semanticId ) ).collect( Collectors.toList() );
      assertEquals( 1, filteredConceptDescriptions.size(), "Not exactly 1 ConceptDescripton for semanticId. " + semanticId );

      List<EmbeddedDataSpecification> embeddedDataSpecifications = filteredConceptDescriptions.get( 0 ).getEmbeddedDataSpecifications();
      assertEquals( 1, embeddedDataSpecifications.size(), "Not exactly 1 EmbeddedDataSpecification for semanticId. " + semanticId );

      assertTrue( embeddedDataSpecifications.stream().findFirst().isPresent(), "There is no EmbeddedDataSpecification" );
      DataSpecificationIEC61360 dataSpecificationContent =
            (DataSpecificationIEC61360) embeddedDataSpecifications.stream().findFirst().get().getDataSpecificationContent();
      return dataSpecificationContent;
   }

   private void checkDataSpecificationIEC61360( String[] propertyUrns, AssetAdministrationShellEnvironment env ) {
      List<ConceptDescription> conceptDescriptions = env.getConceptDescriptions();
      Arrays.stream( propertyUrns ).map( x -> getDataSpecificationIEC61360(x, env) );
   }

   private AssetAdministrationShellEnvironment getAssetAdministrationShellFromAspect( TestAspect testAspect ) throws DeserializationException, IOException {
      final Aspect aspect = loadAspect( testAspect );
      ByteArrayOutputStream out = generator.generateXmlOutput( aspect );
      AssetAdministrationShellEnvironment env = loadAASX( out );
      return env;
   }

   private Aspect loadAspect( TestAspect testAspect ) {
      final VersionedModel model = TestResources.getModel( testAspect, KnownVersion.BAMM_1_0_0 ).get();
      Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked( model );
      return aspect;
   }

   private AssetAdministrationShellEnvironment loadAASX( ByteArrayOutputStream byteStream ) throws DeserializationException {
      XmlDeserializer deserializer = new XmlDeserializer();
      AssetAdministrationShellEnvironment env = deserializer.read( new ByteArrayInputStream( byteStream.toByteArray() ) );
      return env;
   }
}
