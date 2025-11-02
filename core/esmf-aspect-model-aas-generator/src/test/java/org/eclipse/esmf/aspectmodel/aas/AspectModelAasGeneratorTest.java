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
package org.eclipse.esmf.aspectmodel.aas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AasSubmodelElements;
import org.eclipse.digitaltwin.aas4j.v3.model.AbstractLangString;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.DataSpecificationContent;
import org.eclipse.digitaltwin.aas4j.v3.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.xml.sax.SAXException;

class AspectModelAasGeneratorTest {

   // The AAS XML Schema is also present in the AAS4j library for testing purposes. So we can read
   // the file from the classpath
   public static final String XML_XSD_AAS_SCHEMA_LOCATION = "/AAS.xsd";

   @Test
   void generateAasxWithAspectDataForMultilanguageText() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspectWithData( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT );
      assertThat( env.getSubmodels() )
            .singleElement()
            .satisfies( subModel -> assertThat( subModel.getSubmodelElements() )
                  .singleElement()
                  .satisfies( property ->
                        assertThat( property ).asInstanceOf( type( MultiLanguageProperty.class ) )
                              .extracting( MultiLanguageProperty::getValue )
                              .asInstanceOf( InstanceOfAssertFactories.LIST )
                              .hasSize( 2 )
                              .allSatisfy( langString ->
                                    assertThat( List.of( "en", "de" ) ).contains( ((AbstractLangString) langString).getLanguage() ) ) ) );
   }

   @Test
   void generateAasxWithAspectDataForEitherWithEntity() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspectWithData( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES );
      assertThat( env.getSubmodels() )
            .singleElement()
            .satisfies( subModel -> assertThat( subModel.getSubmodelElements() )
                  .anySatisfy( sme ->
                        assertThat( sme ).asInstanceOf( type( SubmodelElementList.class ) )
                              .extracting( SubmodelElementList::getValue )
                              .asInstanceOf( InstanceOfAssertFactories.LIST )
                              .anySatisfy( entity ->
                                    assertThat( entity ).asInstanceOf( type( SubmodelElementCollection.class ) )
                                          .extracting( SubmodelElementCollection::getValue )
                                          .asInstanceOf( InstanceOfAssertFactories.LIST )
                                          .singleElement( type( Property.class ) )
                                          .extracting( Property::getValue )
                                          .isEqualTo( "The result" ) ) ) );
   }

   @Test
   void generateAasxWithAspectDataForNestedEntityLists() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspectWithData( TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST );
      assertThat( env.getSubmodels() )
            .singleElement()
            .satisfies( subModel -> assertThat( subModel.getSubmodelElements() )
                  .anySatisfy( sme -> {
                           assertThat( sme ).asInstanceOf( type( SubmodelElementList.class ) )
                                 .extracting( SubmodelElementList::getValue )
                                 .asInstanceOf( InstanceOfAssertFactories.LIST )
                                 .anySatisfy( entity ->
                                       assertThat( entity ).asInstanceOf( type( SubmodelElementCollection.class ) )
                                             .extracting( SubmodelElementCollection::getValue )
                                             .asInstanceOf( InstanceOfAssertFactories.LIST )
                                             .anySatisfy( property ->
                                                   assertThat( property ).asInstanceOf( type( Property.class ) )
                                                         .extracting( Property::getValue )
                                                         .isEqualTo( "2.25" ) ) );
                           assertThat( ((SubmodelElementList) sme).getOrderRelevant() ).isFalse();
                        }
                  ) );
   }

   @Test
   void generateAasxWithAspectDataForCollectionProperty() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspectWithData( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE );
      assertThat( env.getSubmodels() )
            .singleElement()
            .satisfies( subModel -> assertThat( subModel.getSubmodelElements() )
                  .anySatisfy( sme ->
                        assertThat( sme ).asInstanceOf( type( SubmodelElementList.class ) )
                              .extracting( SubmodelElementList::getValue )
                              .asInstanceOf( InstanceOfAssertFactories.LIST )
                              .singleElement()
                              .satisfies( element ->
                                    assertThat( element ).asInstanceOf( type( Blob.class ) )
                                          .extracting( Blob::getValue )
                                          .satisfies( blobData -> assertThat( new String( blobData ) ).isEqualTo( "1,2,3,4,5,6" ) ) ) ) );
   }

   @Test
   void generateAasxWithAspectDataForCollectionPropertyWithCustomMapper() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspectWithData( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE,
            List.of( new IntegerCollectionMapper() ) );
      assertThat( env.getSubmodels() )
            .singleElement()
            .satisfies( subModel -> assertThat( subModel.getSubmodelElements() )
                  .anySatisfy( sme ->
                        assertThat( sme ).asInstanceOf( type( SubmodelElementList.class ) )
                              .extracting( SubmodelElementList::getValue )
                              .asInstanceOf( InstanceOfAssertFactories.LIST )
                              .allSatisfy( element ->
                                    assertThat( element ).asInstanceOf( type( DefaultProperty.class ) )
                                          .extracting( DefaultProperty::getValue )
                                          .satisfies(
                                                intString -> assertThat( Integer.parseInt( intString ) ).isBetween( 1, 6 ) ) ) ) );
   }

   @Test
   void testGenerateAasxFromAspectModelWithListAndAdditionalProperty() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY );
      assertThat( env.getConceptDescriptions() ).hasSize( 3 );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 2 );

      final Set<String> semanticIds =
            Set.of( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#testPropertyTwo" );

      checkDataSpecificationIec61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithEntity() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final Submodel submodel = env.getSubmodels().get( 0 );
      assertThat( submodel.getSubmodelElements().get( 0 ) ).isInstanceOfSatisfying( SubmodelElementCollection.class, collection -> {
         assertThat( collection.getIdShort() ).isEqualTo( "TestEntity" );

         final SubmodelElement property = collection.getValue().stream().findFirst().get();
         assertThat( property.getIdShort() ).isEqualTo( "entityProperty" );
         assertThat( submodel.getSupplementalSemanticIds() ).anySatisfy( ref -> {
            assertThat( ref.getType() ).isEqualTo( ReferenceTypes.EXTERNAL_REFERENCE );
            assertThat( ref.getKeys().get( 0 ).getType() ).isEqualTo( KeyTypes.GLOBAL_REFERENCE );
            assertThat( ref.getKeys().get( 0 ).getValue() ).isEqualTo( "http://example.com/" );
         } );
      } );

      getDataSpecificationIec61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithCollection() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_COLLECTION );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertThat( submodelElement ).asInstanceOf( type( SubmodelElementList.class ) )
            .satisfies( submodelElementList -> {
               assertThat( submodelElementList.getIdShort() ).isEqualTo( "testProperty" );
               assertThat( submodelElementList.getTypeValueListElement() ).isEqualTo( AasSubmodelElements.SUBMODEL_ELEMENT_COLLECTION );
               assertThat( (submodelElementList).getOrderRelevant() ).isFalse();
            } );

      assertThat( submodelElement.getSemanticId().getKeys().get( 0 ).getType() ).isEqualTo( KeyTypes.GLOBAL_REFERENCE );

      getDataSpecificationIec61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithList() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_LIST );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertThat( submodelElement ).asInstanceOf( type( SubmodelElementList.class ) )
            .satisfies( submodelElementList -> {
               assertThat( submodelElementList.getIdShort() ).isEqualTo( "testProperty" );
               assertThat( submodelElementList.getTypeValueListElement() ).isEqualTo( AasSubmodelElements.SUBMODEL_ELEMENT_COLLECTION );
               assertThat( (submodelElementList).getOrderRelevant() ).isFalse();
            } );

      assertThat( submodelElement.getSemanticId().getKeys().get( 0 ).getType() ).isEqualTo( KeyTypes.GLOBAL_REFERENCE );

      getDataSpecificationIec61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithSet() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_SET );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertThat( submodelElement ).asInstanceOf( type( SubmodelElementList.class ) )
            .satisfies( submodelElementList -> {
               assertThat( submodelElementList.getIdShort() ).isEqualTo( "testProperty" );
               assertThat( submodelElementList.getTypeValueListElement() ).isEqualTo( AasSubmodelElements.SUBMODEL_ELEMENT_COLLECTION );
               assertThat( (submodelElementList).getOrderRelevant() ).isFalse();
            } );
      assertThat( submodelElement.getSemanticId().getKeys().get( 0 ).getType() ).isEqualTo( KeyTypes.GLOBAL_REFERENCE );

      getDataSpecificationIec61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithSortedSet() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_SORTED_SET );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertThat( submodelElement ).as( "SubmodelElement is not a SubmodelElementList" ).isInstanceOf( SubmodelElementList.class );
      assertThat( (((SubmodelElementList) submodelElement)).getOrderRelevant() ).isFalse();
      assertThat( submodelElement.getIdShort() ).isEqualTo( "testProperty" );
      assertThat( submodelElement.getSemanticId().getKeys().get( 0 ).getType() ).isEqualTo( KeyTypes.GLOBAL_REFERENCE );

      getDataSpecificationIec61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithEitherWithComplexTypes() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final SubmodelElementList elementCollection = ((SubmodelElementList) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 ));
      final Set<String> testValues = Set.of( "testProperty", "result" );
      assertThat( elementCollection.getValue() ).as( "Neither left nor right entity contained." )
            .anyMatch( x -> testValues.contains( x.getIdShort() ) );

      final Set<String> semanticIds =
            Set.of( "urn:samm:org.eclipse.esmf.test:1.0.0#result",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#error" );

      checkDataSpecificationIec61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithQuantifiable() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITH_UNIT );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertThat( submodelElement.getIdShort() ).isEqualTo( "testProperty" );

      final DataSpecificationContent dataSpecificationContent = getDataSpecificationIec61360(
            "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );

      assertThat( ((DataSpecificationIec61360) dataSpecificationContent).getUnit() ).isEqualTo( "percent" );
   }

   @Test
   void testGenerateAasxFromBammWithConstraint() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_CONSTRAINT );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 6 );
      final SubmodelElement submodelElement =
            env.getSubmodels().get( 0 ).getSubmodelElements().stream()
                  .filter( x -> x.getIdShort().equals( "stringLcProperty" ) )
                  .findFirst()
                  .orElseThrow();
      assertThat( submodelElement.getIdShort() ).isEqualTo( "stringLcProperty" );

      final Set<String> semanticIds =
            Set.of( "urn:samm:org.eclipse.esmf.test:1.0.0#stringLcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#doubleRcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#intRcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#bigIntRcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#floatRcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#stringRegexcProperty" );

      checkDataSpecificationIec61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithRecursivePropertyWithOptional() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL );
      assertThat( env.getSubmodels() ).hasSize( 1 );
   }

   @Test
   void testGenerateAasxFromAspectModelWithCode() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_CODE );
      assertThat( env.getConceptDescriptions() ).hasSize( 2 );
      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final Property submodelElement = (Property) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertThat( submodelElement.getValueType() ).isEqualTo( DataTypeDefXsd.INT );

      getDataSpecificationIec61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithEnumeration() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENUMERATION );

      assertThat( env.getConceptDescriptions() ).hasSize( 2 );

      final DataSpecificationIec61360 dataSpecificationContent = (DataSpecificationIec61360) env.getConceptDescriptions().stream()
            .filter( conceptDescription -> conceptDescription.getIdShort().equals( "testProperty" ) )
            .findFirst()
            .get()
            .getEmbeddedDataSpecifications()
            .stream()
            .findFirst()
            .get()
            .getDataSpecificationContent();
      assertThat( dataSpecificationContent.getValueList().getValueReferencePairs() ).hasSize( 3 );

      assertThat( env.getSubmodels() ).hasSize( 1 );
      assertThat( env.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      final Property submodelElement = (Property) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertThat( submodelElement.getValueType() ).isEqualTo( DataTypeDefXsd.INTEGER );
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
      // anonymous enumeration in test has no urn for enum values but is required for Concept
      // Description referencing
   void testGeneration( final TestAspect testAspect ) throws DeserializationException {
      final String aasXmlString = aspectToAasXml( testAspect );
      final byte[] aasXmlInput = aasXmlString.getBytes();

      final String aasXml = new String( aasXmlInput );
      assertThat( aasXml ).doesNotContain( "DefaultScalarValue[" );
      assertThat( aasXml ).doesNotContain( "DefaultEntity[" );
      assertThat( aasXml ).doesNotContain( "Optional[" );

      final Environment env = loadAasx( new ByteArrayInputStream( aasXmlInput ) );
      assertThat( env.getSubmodels() ).isNotEmpty();
      validate( new ByteArrayInputStream( aasXmlInput ) );
   }

   @Test
   void testGenerateAasxFromAspectModelWithOperations() throws DeserializationException {
      final Environment environment = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_OPERATION );

      final List<SubmodelElement> operations = environment.getSubmodels().get( 0 ).getSubmodelElements();
      final DefaultOperation operation1 = (DefaultOperation) operations.get( 0 );
      assertThat( operation1.getSemanticId() ).isNotNull();
      assertThat(
            environment.getConceptDescriptions().stream().filter( cd -> cd.getIdShort().equals( operation1.getIdShort() ) ) ).isNotNull();

      final DefaultOperation operation2 = (DefaultOperation) operations.get( 0 );
      assertThat( operation2.getSemanticId() ).isNotNull();
      assertThat(
            environment.getConceptDescriptions().stream().filter( cd -> cd.getIdShort().equals( operation2.getIdShort() ) ) ).isNotNull();

      assertThat( environment.getConceptDescriptions() ).hasSizeGreaterThanOrEqualTo( 5 );
   }

   @Test
   void testGeneratedAasxFromAspectModelWithPropertiesWithDescriptions() throws DeserializationException {
      final Environment environment = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_PROPERTY_WITH_DESCRIPTIONS );

      final Property property = (Property) environment.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );

      assertThat( environment.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      assertThat( environment.getConceptDescriptions() ).hasSize( 2 );
      assertThat( environment.getConceptDescriptions().get( 1 ).getEmbeddedDataSpecifications() ).hasSize( 1 );

      final DataSpecificationIec61360 dataSpecificationIec61360 =
            (DataSpecificationIec61360) environment.getConceptDescriptions().get( 1 ).getEmbeddedDataSpecifications().get( 0 )
                  .getDataSpecificationContent();

      assertThat( dataSpecificationIec61360.getDefinition().get( 1 ).getText() ).isEqualTo( "Test Description" );
      assertThat( property.getDescription() ).isEmpty();
   }

   @Test
   void testGeneratedAasxFromAspectModelSemanticIdsAreGlobalReferences() throws DeserializationException {
      final Environment environment = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_PROPERTY_WITH_DESCRIPTIONS );

      final Property property = (Property) environment.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );

      assertThat( environment.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      assertThat( environment.getSubmodels().get( 0 ).getSemanticId().getKeys().get( 0 ).getType() ).isEqualTo( KeyTypes.GLOBAL_REFERENCE );
      assertThat( environment.getConceptDescriptions() ).hasSize( 2 );
      assertThat( environment.getConceptDescriptions().get( 1 ).getEmbeddedDataSpecifications() ).hasSize( 1 );
      assertThat( property.getDescription() ).isEmpty();
      assertThat( property.getSemanticId().getKeys().get( 0 ).getType() ).isEqualTo( KeyTypes.GLOBAL_REFERENCE );
   }

   @Test
   void testAspectWithPayloadName() throws DeserializationException {
      final Environment environment = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME );

      final Property property = (Property) environment.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertThat( environment.getSubmodels().get( 0 ).getSubmodelElements() ).hasSize( 1 );
      assertThat( environment.getSubmodels().get( 0 ).getSubmodelElements().get( 0 ).getIdShort() ).isEqualTo( property.getIdShort() );
   }

   @Test
   void testSubmodelListSemanticIdIsPropertyUrnEntityCollection() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY_COLLECTION );

      assertThat( env.getSubmodels() ).hasSize( 1 );
      final Submodel sm = env.getSubmodels().getFirst();
      assertThat( sm.getSubmodelElements() ).hasSize( 1 );
      assertThat( sm.getSubmodelElements().getFirst() ).isInstanceOf( SubmodelElementList.class );

      final SubmodelElementList sml = (SubmodelElementList) sm.getSubmodelElements().getFirst();

      assertThat( sml.getSemanticId() ).isNotNull();
      assertThat( sml.getSemanticId().getKeys() ).isNotEmpty();
      assertThat( sml.getSemanticId().getKeys().getFirst().getValue() )
            .isEqualTo( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty" );
      assertThat( sml.getSemanticId().getKeys().getFirst().getValue() )
            .isNotEqualTo( "urn:samm:org.eclipse.esmf.test:1.0.0#TestCollection" );
   }

   @Test
   void testSubmodelCollectionInsideSubmodelListHasNoSemanticId() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY_LIST );
      final Submodel sm = env.getSubmodels().getFirst();
      final SubmodelElementList sml = (SubmodelElementList) sm.getSubmodelElements().getFirst();

      final List<SubmodelElement> values = sml.getValue();
      assertThat( values ).isNotEmpty();
      assertThat( values.getFirst() ).isInstanceOf( SubmodelElementCollection.class );

      final SubmodelElementCollection smc = (SubmodelElementCollection) values.getFirst();

      assertThat( smc.getSemanticId() ).isNull();

      final List<Reference> supp = smc.getSupplementalSemanticIds();
      assertThat( supp == null || supp.isEmpty() ).isTrue();
   }

   @Test
   void testEntitySeeIsNormalizedToUrnIrdiAndNoFileScheme() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY );

      assertThat( env.getSubmodels() ).hasSize( 1 );
      final Submodel sm = env.getSubmodels().getFirst();

      assertThat( sm.getSubmodelElements() ).hasSize( 1 );
      final SubmodelElement sme = sm.getSubmodelElements().getFirst();
      assertThat( sme ).isInstanceOf( SubmodelElementCollection.class );

      final SubmodelElementCollection smc = (SubmodelElementCollection) sme;

      assertThat( smc.getSemanticId() ).isNull();

      final List<String> supp = smc.getSupplementalSemanticIds() == null
            ? List.of()
            : smc.getSupplementalSemanticIds().stream()
            .flatMap( r -> r.getKeys().stream() )
            .map( Key::getValue )
            .toList();

      assertThat( supp )
            .allMatch( v -> !v.startsWith( "file:" ) && !v.matches( "^[A-Za-z]:[\\\\/].*" ) )
            .doesNotContain( "0173-1%2302-ABG854%23003" )
            .contains( "urn:irdi:0173-1%2306-AAA999%23001", "urn:irdi:0173-1%2302-ABG854%23003" );
   }

   @Test
   void testEntitySeeContainsBothIrdiUrnsEvenWithOtherSeeFromProperty() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY );

      final SubmodelElementCollection smc = (SubmodelElementCollection) env.getSubmodels()
            .getFirst().getSubmodelElements().getFirst();

      final Set<String> supp = smc.getSupplementalSemanticIds() == null
            ? Set.of()
            : smc.getSupplementalSemanticIds().stream()
            .flatMap( r -> r.getKeys().stream() )
            .map( Key::getValue )
            .collect( Collectors.toSet() );

      assertThat( supp ).contains(
            "urn:irdi:0173-1%2302-ABG854%23003",
            "urn:irdi:0173-1%2306-AAA999%23001"
      );
   }

   @Test
   void testSubmodelListSemanticIdSsPropertyUrnAndSmlSmcIdShortAreEntityName() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY_COLLECTION );

      assertThat( env.getSubmodels() ).hasSize( 1 );
      final Submodel sm = env.getSubmodels().getFirst();
      assertThat( sm.getSubmodelElements() ).hasSize( 1 );
      assertThat( sm.getSubmodelElements().getFirst() ).isInstanceOf( SubmodelElementList.class );

      final SubmodelElementList sml = (SubmodelElementList) sm.getSubmodelElements().getFirst();

      // (1) Identifier of the SML = URN of the property (not the characteristic)
      assertThat( sml.getSemanticId() ).isNotNull();
      assertThat( sml.getSemanticId().getKeys() ).isNotEmpty();
      assertThat( sml.getSemanticId().getKeys().get( 0 ).getValue() )
            .isEqualTo( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty" );

      // (3) idShort of SML = Entity name
      assertThat( sml.getIdShort() ).isEqualTo( "TestEntity" );

      assertThat( sml.getValue() ).isNotEmpty();
      assertThat( sml.getValue().get( 0 ) ).isInstanceOf( SubmodelElementCollection.class );
      final SubmodelElementCollection smc = (SubmodelElementCollection) sml.getValue().get( 0 );

      // (3) idShort of SMC = idShort of SML = Entity name
      assertThat( smc.getIdShort() ).isEqualTo( "TestEntity" );
      assertThat( smc.getIdShort() ).isEqualTo( sml.getIdShort() );
   }

   private void checkDataSpecificationIec61360( final Set<String> semanticIds, final Environment env ) {
      semanticIds.forEach( semanticId -> getDataSpecificationIec61360( semanticId, env ) );
   }

   private DataSpecificationContent getDataSpecificationIec61360( final String semanticId, final Environment env ) {
      final List<ConceptDescription> filteredConceptDescriptions =
            env.getConceptDescriptions().stream()
                  .filter( conceptDescription -> conceptDescription.getId().equals( semanticId ) )
                  .toList();
      assertThat( filteredConceptDescriptions ).hasSize( 1 );

      final List<EmbeddedDataSpecification> embeddedDataSpecifications = filteredConceptDescriptions.get( 0 )
            .getEmbeddedDataSpecifications();
      assertThat( embeddedDataSpecifications ).hasSize( 1 );

      assertThat( embeddedDataSpecifications ).as( "There is no EmbeddedDataSpecification" ).isNotEmpty();
      return embeddedDataSpecifications.stream().findFirst().get().getDataSpecificationContent();
   }

   private Environment getAssetAdministrationShellFromAspect( final TestAspect testAspect ) throws DeserializationException {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final AasGenerationConfig config = AasGenerationConfigBuilder.builder()
            .format( AasFileFormat.XML )
            .build();
      return loadAasx( new AspectModelAasGenerator( aspect, config ).getContent() );
   }

   private Environment getAssetAdministrationShellFromAspectWithData( final TestAspect testAspect ) throws DeserializationException {
      return getAssetAdministrationShellFromAspectWithData( testAspect, List.of() );
   }

   private Environment getAssetAdministrationShellFromAspectWithData( final TestAspect testAspect,
         final List<PropertyMapper<?>> propertyMappers ) throws DeserializationException {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final Try<JsonNode> payload = TestResources.loadPayload( testAspect );
      final JsonNode aspectData = payload.getOrElseThrow( () -> new RuntimeException( payload.getCause() ) );
      final AasGenerationConfig config = AasGenerationConfigBuilder.builder()
            .format( AasFileFormat.XML )
            .propertyMappers( propertyMappers )
            .aspectData( aspectData )
            .build();
      return loadAasx( new AspectModelAasGenerator( aspect, config ).getContent() );
   }

   private String aspectToAasXml( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final AasGenerationConfig config = AasGenerationConfigBuilder.builder()
            .format( AasFileFormat.XML )
            .build();
      return new String( new AspectModelAasGenerator( aspect, config ).getContent(), StandardCharsets.UTF_8 );
   }

   private void validate( final ByteArrayInputStream xmlStream ) {
      try {
         final SchemaFactory factory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
         final Schema schema = factory.newSchema( new StreamSource( getClass().getResourceAsStream( XML_XSD_AAS_SCHEMA_LOCATION ) ) );
         final Validator validator = schema.newValidator();
         validator.validate( new StreamSource( xmlStream ), null );
      } catch ( final SAXException | IOException e ) {
         fail( e );
      }
   }

   private Environment loadAasx( final ByteArrayInputStream byteStream ) throws DeserializationException {
      final XmlDeserializer deserializer = new XmlDeserializer();
      return deserializer.read( byteStream );
   }

   private Environment loadAasx( final byte[] data ) throws DeserializationException {
      final XmlDeserializer deserializer = new XmlDeserializer();
      return deserializer.read( new ByteArrayInputStream( data ) );
   }
}
