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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AbstractLangString;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.DataSpecificationContent;
import org.eclipse.digitaltwin.aas4j.v3.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.xml.sax.SAXException;

class AspectModelAASGeneratorTest {

   // The AAS XML Schema is also present in the AAS4j library for testing purposes. So we can read
   // the file from the classpath
   public static final String XML_XSD_AAS_SCHEMA_LOCATION = "/AAS.xsd";

   AspectModelAASGenerator generator = new AspectModelAASGenerator();

   @Test
   void generateAasxWithAspectDataForMultilanguageText() throws IOException, DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspectWithData( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT );
      assertThat( env.getSubmodels() )
            .singleElement()
            .satisfies( subModel -> assertThat( subModel.getSubmodelElements() )
                  .singleElement()
                  .satisfies( property -> {
                     assertThat( property ).asInstanceOf( type( MultiLanguageProperty.class ) )
                           .extracting( MultiLanguageProperty::getValue )
                           .asList()
                           .hasSize( 2 )
                           .allSatisfy( langString -> {
                              assertThat( List.of( "en", "de" ) ).contains( ((AbstractLangString) langString).getLanguage() );
                           } );
                  } ) );
   }

   @Test
   void generateAasxWithAspectDataForEitherWithEntity() throws IOException, DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspectWithData( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES );
      assertThat( env.getSubmodels() )
            .singleElement()
            .satisfies( subModel -> assertThat( subModel.getSubmodelElements() )
                  .anySatisfy( sme -> {
                     assertThat( sme ).asInstanceOf( type( SubmodelElementList.class ) )
                           .extracting( SubmodelElementList::getValue )
                           .asList()
                           .anySatisfy( entity -> {
                              assertThat( entity ).asInstanceOf( type( SubmodelElementCollection.class ) )
                                    .extracting( SubmodelElementCollection::getValue )
                                    .asList()
                                    .singleElement( type( Property.class ) )
                                    .extracting( Property::getValue )
                                    .isEqualTo( "The result" );
                           } );
                  } ) );
   }

   @Test
   void generateAasxWithAspectDataForNestedEntityLists() throws IOException, DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspectWithData( TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST );
      assertThat( env.getSubmodels() )
            .singleElement()
            .satisfies( subModel -> assertThat( subModel.getSubmodelElements() )
                  .anySatisfy( sme -> {
                     assertThat( sme ).asInstanceOf( type( SubmodelElementList.class ) )
                           .extracting( SubmodelElementList::getValue )
                           .asList()
                           .anySatisfy( entity -> {
                              assertThat( entity ).asInstanceOf( type( SubmodelElementCollection.class ) )
                                    .extracting( SubmodelElementCollection::getValue )
                                    .asList()
                                    .anySatisfy( property -> {
                                       assertThat( property ).asInstanceOf( type( Property.class ) )
                                             .extracting( Property::getValue )
                                             .isEqualTo( "2.25" );
                                    } );
                           } );
                  } ) );
   }

   @Test
   void testGenerateAasxFromAspectModelWithListAndAdditionalProperty() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY );
      assertEquals( 3, env.getConceptDescriptions().size() );
      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 2, env.getSubmodels().get( 0 ).getSubmodelElements().size() );

      final Set<String> semanticIds =
            Set.of( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#testPropertyTwo" );

      checkDataSpecificationIEC61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithEntity() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENTITY );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in Submodel." );
      assertTrue( env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 ) instanceof SubmodelElementCollection,
            "SubmodelElement is not a SubmodelElementCollection." );
      final SubmodelElementCollection collection = (SubmodelElementCollection) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( 1, collection.getValue().size(), "Not exactly one Element in SubmodelElementCollection" );
      assertEquals( "entityProperty", collection.getValue().stream().findFirst().get().getIdShort() );

      getDataSpecificationIEC61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithCollection() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_COLLECTION );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in AAS." );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue( submodelElement instanceof SubmodelElementList, "SubmodelElement is not a SubmodelElementList" );
      assertEquals( "testProperty", submodelElement.getIdShort() );

      getDataSpecificationIEC61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithList() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_LIST );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in AAS." );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue( submodelElement instanceof SubmodelElementList, "SubmodelElement is not a SubmodelElementList" );
      assertEquals( "testProperty", submodelElement.getIdShort() );

      getDataSpecificationIEC61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithSet() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_SET );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in AAS." );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue( submodelElement instanceof SubmodelElementList, "SubmodelElement is not a SubmodelElementList" );
      assertEquals( "testProperty", submodelElement.getIdShort() );

      getDataSpecificationIEC61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithSortedSet() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_SORTED_SET );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), "Not exactly one SubmodelElement in AAS." );
      final SubmodelElement submodelElement = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertTrue( submodelElement instanceof SubmodelElementList, "SubmodelElement is not a SubmodelElementList" );
      assertEquals( "testProperty", submodelElement.getIdShort() );

      getDataSpecificationIEC61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithEitherWithComplexTypes() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), 1, "Not exactly one Element in SubmodelElements." );
      final SubmodelElementList elementCollection = ((SubmodelElementList) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 ));
      final Set<String> testValues = Set.of( "RightEntity", "LeftEntity" );
      assertTrue( elementCollection.getValue().stream().anyMatch( x -> testValues.contains( x.getIdShort() ) ),
            "Neither left nor right entity contained." );

      final Set<String> semanticIds =
            Set.of( "urn:samm:org.eclipse.esmf.test:1.0.0#result",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#error" );

      checkDataSpecificationIEC61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithQuantifiable() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITH_UNIT );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), 1, "Not exactly one Element in SubmodelElements." );
      final SubmodelElement element = env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( "testProperty", element.getIdShort() );

      final DataSpecificationContent dataSpecificationContent = getDataSpecificationIEC61360(
            "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );

      assertEquals( "percent", ((DataSpecificationIec61360) dataSpecificationContent).getUnit(), "Unit is not percent" );
   }

   @Test
   void testGenerateAasxFromBammWithConstraint() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_CONSTRAINT );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size(), 6, "Not exactly six Elements in SubmodelElements." );
      final SubmodelElement submodelElement =
            env.getSubmodels().get( 0 ).getSubmodelElements().stream()
                  .filter( x -> x.getIdShort().equals( "stringLcProperty" ) )
                  .findFirst()
                  .orElseThrow();
      assertEquals( "stringLcProperty", submodelElement.getIdShort() );

      final Set<String> semanticIds =
            Set.of( "urn:samm:org.eclipse.esmf.test:1.0.0#stringLcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#doubleRcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#intRcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#bigIntRcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#floatRcProperty",
                  "urn:samm:org.eclipse.esmf.test:1.0.0#stringRegexcProperty" );

      checkDataSpecificationIEC61360( semanticIds, env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithRecursivePropertyWithOptional() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL );
      assertEquals( 1, env.getSubmodels().size(), "Not exactly one Submodel in AAS." );
   }

   @Test
   void testGenerateAasxFromAspectModelWithCode() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_CODE );
      assertEquals( 2, env.getConceptDescriptions().size() );
      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size() );
      final Property submodelElement = (Property) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( DataTypeDefXsd.INT, submodelElement.getValueType(), "Value type not int" );

      getDataSpecificationIEC61360( "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty", env );
   }

   @Test
   void testGenerateAasxFromAspectModelWithEnumeration() throws DeserializationException {
      final Environment env = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_ENUMERATION );

      assertEquals( 2, env.getConceptDescriptions().size() );

      final DataSpecificationIec61360 dataSpecificationContent = (DataSpecificationIec61360) env.getConceptDescriptions().stream()
            .filter( conceptDescription -> conceptDescription.getIdShort().equals( "testProperty" ) )
            .findFirst()
            .get()
            .getEmbeddedDataSpecifications()
            .stream()
            .findFirst()
            .get()
            .getDataSpecificationContent();
      assertEquals( 3, dataSpecificationContent.getValueList().getValueReferencePairs().size() );

      assertEquals( 1, env.getSubmodels().size() );
      assertEquals( 1, env.getSubmodels().get( 0 ).getSubmodelElements().size() );
      final Property submodelElement = (Property) env.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );
      assertEquals( DataTypeDefXsd.INTEGER, submodelElement.getValueType(), "Value type not int" );
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   // anonymous enumeration in test has no urn for enum values but is required for Concept
   // Description referencing
   public void testGeneration( final TestAspect testAspect ) throws DeserializationException {
      final String aspectAsString = aspectToString( testAspect );
      final byte[] xmlFile = aspectAsString.getBytes();

      final String aasXml = new String( xmlFile );
      assertThat( aasXml ).doesNotContain( "DefaultScalarValue[" );
      assertThat( aasXml ).doesNotContain( "DefaultEntity[" );
      assertThat( aasXml ).doesNotContain( "Optional[" );

      final Environment env = loadAASX( new ByteArrayInputStream( xmlFile ) );
      assertFalse( env.getSubmodels().isEmpty(), "No Submodel in AAS present." );
      validate( new ByteArrayInputStream( xmlFile ) );
   }

   @Test
   void testGenerateAasxFromAspectModelWithOperations () throws IOException, DeserializationException {
      final Environment environment = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_OPERATION );

      List<SubmodelElement> operations = environment.getSubmodels().get( 0 ).getSubmodelElements();
      DefaultOperation operation1 = (DefaultOperation) operations.get( 0 );
      assertThat( operation1.getSemanticId() ).isNotNull();
      assertThat( environment.getConceptDescriptions().stream().filter( cd -> cd.getIdShort().equals( operation1.getIdShort() ) ) ).isNotNull();

      DefaultOperation operation2 = (DefaultOperation) operations.get( 0 );
      assertThat( operation2.getSemanticId() ).isNotNull();
      assertThat( environment.getConceptDescriptions().stream().filter( cd -> cd.getIdShort().equals( operation2.getIdShort() ) ) ).isNotNull();

      assertEquals( 7, environment.getConceptDescriptions().size() );
   }

   @Test
   void testGeneratedAasxFromAspectModelWithPropertiesWithDescriptions () throws IOException, DeserializationException {
      final Environment environment = getAssetAdministrationShellFromAspect( TestAspect.ASPECT_WITH_PROPERTY_WITH_DESCRIPTIONS );

      final Property property = (Property) environment.getSubmodels().get( 0 ).getSubmodelElements().get( 0 );

      assertEquals( 1, environment.getSubmodels().get( 0 ).getSubmodelElements().size() );
      assertEquals( 2, environment.getConceptDescriptions().size() );
      assertEquals( 1, environment.getConceptDescriptions().get( 1 ).getEmbeddedDataSpecifications().size() );

      final DataSpecificationIec61360 dataSpecificationIec61360 =
            (DataSpecificationIec61360) environment.getConceptDescriptions().get( 1 ).getEmbeddedDataSpecifications().get( 0 ).getDataSpecificationContent();

      assertThat( dataSpecificationIec61360.getDefinition().get( 1 ).getText() ).isEqualTo( "Test Description" );

      assertThat( property.getDescription() ).isEmpty();

   }

   private void checkDataSpecificationIEC61360( final Set<String> semanticIds, final Environment env ) {
      semanticIds.forEach( x -> getDataSpecificationIEC61360( x, env ) );
   }

   private DataSpecificationContent getDataSpecificationIEC61360( final String semanticId, final Environment env ) {
      final List<ConceptDescription> conceptDescriptions = env.getConceptDescriptions();
      final List<ConceptDescription> filteredConceptDescriptions =
            conceptDescriptions.stream()
                  .filter( x -> x.getId().equals( semanticId ) )
                  .toList();
      assertEquals( 1, filteredConceptDescriptions.size(), "Not exactly 1 ConceptDescription for semanticId. " + semanticId );

      final List<EmbeddedDataSpecification> embeddedDataSpecifications = filteredConceptDescriptions.get( 0 )
            .getEmbeddedDataSpecifications();
      assertEquals( 1, embeddedDataSpecifications.size(), "Not exactly 1 EmbeddedDataSpecification for semanticId. " + semanticId );

      assertTrue( embeddedDataSpecifications.stream().findFirst().isPresent(), "There is no EmbeddedDataSpecification" );
      return embeddedDataSpecifications.stream().findFirst().get().getDataSpecificationContent();
   }

   private Environment getAssetAdministrationShellFromAspect( final TestAspect testAspect ) throws DeserializationException {
      final Aspect aspect = loadAspect( testAspect );
      final String out = generator.generateXmlOutput( aspect );
      return loadAASX( out.getBytes() );
   }

   private Environment getAssetAdministrationShellFromAspectWithData( final TestAspect testAspect )
         throws DeserializationException, IOException {
      final Aspect aspect = loadAspect( testAspect );
      final JsonNode aspectData = loadPayload( testAspect );
      final String out = generator.generateXmlOutput( Map.of( aspect, aspectData ) );
      final var data = out.getBytes();
      return loadAASX( data );
   }

   private String aspectToString( final TestAspect testAspect ) {
      final Aspect aspect = loadAspect( testAspect );
      return generator.generateXmlOutput( aspect );
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

   private Aspect loadAspect( final TestAspect testAspect ) {
      final VersionedModel model = TestResources.getModel( testAspect, KnownVersion.getLatest() ).get();
      return AspectModelLoader.getSingleAspectUnchecked( model );
   }

   private JsonNode loadPayload( final TestAspect testAspect ) {
      return TestResources.getPayload( testAspect, KnownVersion.getLatest() ).get();
   }

   private Environment loadAASX( final ByteArrayInputStream byteStream ) throws DeserializationException {
      final XmlDeserializer deserializer = new XmlDeserializer();
      return deserializer.read( byteStream );
   }

   private Environment loadAASX( final byte[] data ) throws DeserializationException {
      final XmlDeserializer deserializer = new XmlDeserializer();
      return deserializer.read( new ByteArrayInputStream( data ) );
   }
}
