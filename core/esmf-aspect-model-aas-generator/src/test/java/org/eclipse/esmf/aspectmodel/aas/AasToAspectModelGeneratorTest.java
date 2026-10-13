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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AasSubmodelElements;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;

import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.generator.AspectArtifact;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Set;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class AasToAspectModelGeneratorTest {
   @ParameterizedTest
   @MethodSource( "idtaSubmodelFiles" )
   void testIdtaAasxFilesCanBeTranslated( final File aasxFile ) {
      try {
         final Environment environment = loadAasxEnvironment( aasxFile );
         final java.util.Set<String> semanticIdDerivedEntityNames = submodelElementCollectionSemanticIdNames( environment );
         final List<Aspect> aspects;
         try ( final InputStream input = new FileInputStream( aasxFile ) ) {
            final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( input );
            aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();
         }
         if ( aspects.isEmpty() ) {
            fail( "Translation of " + aasxFile.getName() + " yielded no Aspects" );
         }
         final String result = AspectSerializer.INSTANCE.aspectToString( aspects.getFirst() );
         final AspectModel aspectModel = new AspectModelLoader().load( new ByteArrayInputStream( result.getBytes() ), aasxFile.toURI() );

         aspectModel.elements().forEach( element -> {
            if ( element instanceof Property || element instanceof Operation || element instanceof Unit ) {
               assertThat( element.getName().charAt( 0 ) )
                     .describedAs( element.getName() + " is a " + element.getClass().getSimpleName() + " and must be lower case" )
                     .isLowerCase();
            } else if ( element instanceof Entity && Character.isLowerCase( element.getName().charAt( 0 ) )
                  && semanticIdDerivedEntityNames.contains( element.getName() ) ) {
               assertThat( element.getName().charAt( 0 ) )
                     .describedAs( element.getName() + " is an Entity derived from a SubmodelElementCollection semanticId" )
                     .isLowerCase();
            } else {
               assertThat( element.getName().charAt( 0 ) )
                     .describedAs( element.getName() + " is a " + element.getClass().getSimpleName() + " and must be upper case" )
                     .isUpperCase();
            }
         } );

         final ViolationReport violations = new AspectModelValidator().validateModel( aspectModel );
         if ( !violations.isEmpty() ) {
            final String report = new ViolationFormatter().apply( violations );
            System.out.println( report );
            System.out.println( "====" );
            System.out.println( result );
            fail();
         }
      } catch ( final IOException | InvalidFormatException exception ) {
         fail( exception );
      } catch ( final DeserializationException exception ) {
         System.err.println( "Could not load AASX file: " + aasxFile.getName() + ". Consider reporting to IDTA or AAS4J project." );
      } catch ( final AspectModelGenerationException aspectModelGenerationException ) {
         if ( aspectModelGenerationException.getCause() instanceof DeserializationException ) {
            System.err.println( "Could not load AASX file: " + aasxFile.getName() + ". Consider reporting to IDTA or AAS4J project." );
         } else {
            fail( aspectModelGenerationException );
         }
      }
   }

   private static final List<String> IGNORED_AASX_FILES = List.of(
         // [Reason]: "kind": "Instance" not "Template"
         "IDTA 02004-2-0_Example_HandoverDocumentation.aasx",
         // [Reason]: "kind": "Instance" not "Template"
         "IDTA 02003_Sample_TechnicalData_forAASMetamodelV3.1.aasx",
         // [Reason]: "kind": "Instance" not "Template"
         "IDTA 02003_Sample_TechnicalData.aasx",
         // [Reason]: "value": "C:\\Windows\\Program Files\\Demo\\Firmware" for type URI.
         // Illegal character in opaque part at index 2: C:\Windows\Program Files\Demo\Firmware
         "IDTA 02007-1-0_Template_Software Nameplate.aasx",
         // [Reason]: Range property with type double. java.lang.NumberFormatException: For input string:
         // "[0;100]"
         "IDTA 02019-1-0_Template_PlantAssetManagement.aasx",
         // [Reason]: Range property with type positiveInteger has an invalid negative lower bound.
         "IDTA 02076_Template_EnergyFlexibilityDataModel.aasx"
   );

   protected static Stream<Arguments> idtaSubmodelFiles() throws URISyntaxException, IOException {
      final String submodelTemplatesMissing =
            "IDTA AASX files not found. Please make sure they are available; in the project root run: git submodule update --init "
                  + "--recursive";
      final URL resource = AasToAspectModelGeneratorTest.class.getResource( "/submodel-templates/published" );
      try ( final Stream<Path> stream = Files.walk( Paths.get( resource.toURI() ) ) ) {
         final List<Arguments> list = stream.filter( Files::isRegularFile )
               .filter( file -> file.getFileName().toString().endsWith( ".aasx" ) )
               .map( Path::toFile )
               .filter( file -> !IGNORED_AASX_FILES.contains( file.getName() ) )
               .map( file -> Arguments.of( Named.of( file.getName(), file ) ) )
               .toList();
         if ( list.isEmpty() ) {
            fail( submodelTemplatesMissing );
         }
         return list.stream();
      } catch ( final NullPointerException exception ) {
         fail( submodelTemplatesMissing );
         return Stream.empty();
      }
   }

   @Test
   void testGenerateAspectFromEmptySubmodelElementListDoesNotThrow() {
      final SubmodelElementList submodelElementList = new DefaultSubmodelElementList.Builder()
            .idShort( "emptyCollectionList" )
            .typeValueListElement( AasSubmodelElements.SUBMODEL_ELEMENT_COLLECTION )
            .value( List.of() )
            .build();
      final Submodel submodel = new DefaultSubmodel.Builder()
            .id( "https://example.com/submodel/empty-list/1.0.0" )
            .idShort( "EmptyListSubmodel" )
            .kind( ModellingKind.TEMPLATE )
            .submodelElements( List.of( submodelElementList ) )
            .build();
      final Environment environment = new DefaultEnvironment.Builder()
            .submodels( List.of( submodel ) )
            .build();
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( environment );

      assertThatCode( () -> aspectModelGenerator.generate().toList() ).doesNotThrowAnyException();
   }

   @Test
   void testDatePropertyWithExampleValueCanBeTranslatedWithoutTypeMappingSetup() {
      final org.eclipse.digitaltwin.aas4j.v3.model.Property dateProperty = new DefaultProperty.Builder()
            .idShort( "DateOfManufacture" )
            .valueType( DataTypeDefXsd.DATE )
            .value( "2022-01-01" )
            .build();
      final AasToAspectModelGenerator aspectModelGenerator =
            AasToAspectModelGenerator.fromEnvironment( buildTemplateEnvironment( dateProperty ) );

      assertThatCode( () -> aspectModelGenerator.generate().toList() ).doesNotThrowAnyException();

      final Property property = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList()
            .getFirst().getProperties().getFirst();

      assertThat( property.getExampleValue() ).isPresent()
            .get()
            .satisfies( exampleValue -> {
               assertThat( exampleValue.getType() ).isEqualTo( SammType.DATE );
               assertThat( exampleValue.getValue().toString() ).isEqualTo( "2022-01-01" );
            } );
      assertThat( property.getCharacteristic() )
            .flatMap( Characteristic::getDataType )
            .contains( SammType.DATE );
   }

   @Test
   void testXmlFixtureWithDateExampleValueCanBeTranslatedAndValidated() {
      final Environment environment = loadEnvironment( "DatePropertyWithExampleValue.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( environment );

      final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();

      assertThat( aspects ).isNotEmpty();
      assertValidSerializedAspect( aspects.getFirst(), URI.create( "urn:test:date-property-with-example-value" ) );
   }

   @Test
   void testOrderRelevantSubmodelElementListIsMappedToSammList() {
      final SubmodelElementList submodelElementList = buildSubmodelElementList( true );
      final AasToAspectModelGenerator aspectModelGenerator =
            AasToAspectModelGenerator.fromEnvironment( buildTemplateEnvironment( submodelElementList ) );

      final Property property = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList()
            .getFirst().getProperties().getFirst();

      assertThat( property.getCharacteristic() ).isPresent();
      assertThat( property.getCharacteristic().get() ).isInstanceOf( org.eclipse.esmf.metamodel.characteristic.List.class );
      assertThat( property.getCharacteristic().get().urn().getName() ).endsWith( "List" );
   }

   @Test
   void testXmlSubmodelElementListWithSelfClosingDisplayNameCanBeTranslated() {
      final Environment environment = loadEnvironment( "SubmodelElementListWithEmptyDisplayName.aas.xml" );
      final SubmodelElementList submodelElementList = (SubmodelElementList) environment.getSubmodels().getFirst()
            .getSubmodelElements().getFirst();
      final org.eclipse.digitaltwin.aas4j.v3.model.Property valueElement =
            (org.eclipse.digitaltwin.aas4j.v3.model.Property) submodelElementList.getValue().getFirst();
      assertThat( valueElement.getDisplayName() ).isNull();

      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( environment );

      assertThatCode( () -> aspectModelGenerator.generate().toList() ).doesNotThrowAnyException();
   }

   @Test
   void testNonOrderRelevantSubmodelElementListIsMappedToSammSet() {
      final SubmodelElementList submodelElementList = buildSubmodelElementList( false );
      final AasToAspectModelGenerator aspectModelGenerator =
            AasToAspectModelGenerator.fromEnvironment( buildTemplateEnvironment( submodelElementList ) );

      final Property property = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList()
            .getFirst().getProperties().getFirst();

      assertThat( property.getCharacteristic() ).isPresent();
      assertThat( property.getCharacteristic().get() ).isInstanceOf( Set.class );
      assertThat( property.getCharacteristic().get().urn().getName() ).endsWith( "Set" );
   }

   @Test
   void testSeeReferences() {
      final InputStream inputStream = AasToAspectModelGeneratorTest.class.getClassLoader().getResourceAsStream(
            "submodel-templates/published/Wireless Communication/1/0/IDTA 02022-1-0_Template_Wireless Communication.aasx" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( inputStream );
      final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();

      assertThatCode( aspectModelGenerator::generate ).doesNotThrowAnyException();

      aspects.stream()
            .flatMap( aspect -> aspect.getProperties().stream() )
            .flatMap( property -> property.getSee().stream() )
            .forEach( see -> assertThat( see ).doesNotContain( "/ " ) );
   }

   @Test
   void testDoNotGenerateSeeReferencesForCharacteristics() {
      final InputStream inputStream = AasToAspectModelGeneratorTest.class.getClassLoader().getResourceAsStream(
            "submodel-templates/published/Handover Documentation/2/0/IDTA 02004-2-0_Template_HandoverDocumentation.aasx" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( inputStream );
      final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();

      assertThatCode( aspectModelGenerator::generate ).doesNotThrowAnyException();

      aspects.stream()
            .flatMap( AasToAspectModelGeneratorTest::allCharacteristics )
            .forEach( characteristic -> assertThat( characteristic.getSee() ).isEmpty() );
   }

   @Test
   void testEntityDescriptionFallsBackToConceptDescriptionDefinition() {
      final String conceptDescriptionId = "https://example.com/concept-description/document-versions";
      final SubmodelElementCollection collection = new DefaultSubmodelElementCollection.Builder()
            .idShort( "DocumentVersions" )
            .semanticId( new DefaultReference.Builder()
                  .type( ReferenceTypes.EXTERNAL_REFERENCE )
                  .keys( List.of( new DefaultKey.Builder()
                        .type( KeyTypes.CONCEPT_DESCRIPTION )
                        .value( conceptDescriptionId )
                        .build() ) )
                  .build() )
            .build();
      final Submodel submodel = new DefaultSubmodel.Builder()
            .id( "https://example.com/submodel/document-versions/1.0.0" )
            .idShort( "DocumentVersionsSubmodel" )
            .kind( ModellingKind.TEMPLATE )
            .submodelElements( List.of( collection ) )
            .build();
      final DefaultConceptDescription conceptDescription = new DefaultConceptDescription.Builder()
            .id( conceptDescriptionId )
            .idShort( "DocumentVersions" )
            .description( new DefaultLangStringTextType.Builder()
                  .language( "en" )
                  .text( "Information elements of individual Document Version entities" )
                  .build()
            )
            .build();
      final Environment environment = new DefaultEnvironment.Builder()
            .submodels( List.of( submodel ) )
            .conceptDescriptions( List.of( conceptDescription ) )
            .build();

      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( environment );
      final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();

      assertThatCode( aspectModelGenerator::generate ).doesNotThrowAnyException();

      final List<String> entityDescriptions = aspects.stream()
            .flatMap( AasToAspectModelGeneratorTest::allCharacteristics )
            .map( Characteristic::getDataType )
            .flatMap( Optional::stream )
            .filter( Entity.class::isInstance )
            .map( Entity.class::cast )
            .flatMap( entity -> entity.getDescriptions().stream() )
            .map( LangString::getValue )
            .toList();

      assertThat( entityDescriptions )
            .contains( "Information elements of individual Document Version entities" );
   }

   @Test
   void testEntityPreferredNamesFallBackToConceptDescriptionDisplayName() {
      final String conceptDescriptionId = "https://example.com/concept-description/document-versions-display-name";
      final SubmodelElementCollection collection = new DefaultSubmodelElementCollection.Builder()
            .idShort( "DocumentVersions" )
            .semanticId( new DefaultReference.Builder()
                  .type( ReferenceTypes.EXTERNAL_REFERENCE )
                  .keys( List.of( new DefaultKey.Builder()
                        .type( KeyTypes.CONCEPT_DESCRIPTION )
                        .value( conceptDescriptionId )
                        .build() ) )
                  .build() )
            .build();
      final Submodel submodel = new DefaultSubmodel.Builder()
            .id( "https://example.com/submodel/document-versions-display-name/1.0.0" )
            .idShort( "DocumentVersionsSubmodelDisplayName" )
            .kind( ModellingKind.TEMPLATE )
            .submodelElements( List.of( collection ) )
            .build();
      final DefaultConceptDescription conceptDescription = new DefaultConceptDescription.Builder()
            .id( conceptDescriptionId )
            .idShort( "DocumentVersions" )
            .displayName( List.of( new DefaultLangStringNameType.Builder()
                  .language( "en" )
                  .text( "Document versions from concept description" )
                  .build() ) )
            .build();
      final Environment environment = new DefaultEnvironment.Builder()
            .submodels( List.of( submodel ) )
            .conceptDescriptions( List.of( conceptDescription ) )
            .build();

      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( environment );
      final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();

      assertThatCode( aspectModelGenerator::generate ).doesNotThrowAnyException();

      final List<String> entityPreferredNames = aspects.stream()
            .flatMap( AasToAspectModelGeneratorTest::allCharacteristics )
            .map( Characteristic::getDataType )
            .flatMap( Optional::stream )
            .filter( Entity.class::isInstance )
            .map( Entity.class::cast )
            .flatMap( entity -> entity.getPreferredNames().stream() )
            .map( LangString::getValue )
            .toList();

      assertThat( entityPreferredNames )
            .contains( "Document versions from concept description" );
   }

   @Test
   void testSubmodelElementCollectionSemanticIdIsUsedAsEntityTypeNotPropertyIdentifier() {
      final String entitySemanticId = "urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity";
      final SubmodelElementCollection collection = new DefaultSubmodelElementCollection.Builder()
            .idShort( "testProperty" )
            .semanticId( new DefaultReference.Builder()
                  .type( ReferenceTypes.EXTERNAL_REFERENCE )
                  .keys( List.of( new DefaultKey.Builder()
                        .type( KeyTypes.GLOBAL_REFERENCE )
                        .value( entitySemanticId )
                        .build() ) )
                  .build() )
            .value( List.of( new DefaultProperty.Builder()
                  .idShort( "entityProperty" )
                  .valueType( DataTypeDefXsd.STRING )
                  .build() ) )
            .build();
      final Submodel submodel = new DefaultSubmodel.Builder()
            .id( "urn:samm:org.eclipse.esmf.test:1.0.0#TestAspect" )
            .idShort( "TestAspect" )
            .kind( ModellingKind.TEMPLATE )
            .submodelElements( List.of( collection ) )
            .build();
      final Environment environment = new DefaultEnvironment.Builder()
            .submodels( List.of( submodel ) )
            .build();

      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( environment );
      final Aspect aspect = aspectModelGenerator.generate().map( AspectArtifact::getContent ).findFirst().orElseThrow();

      final Property property = aspect.getProperties().getFirst();
      assertThat( property.getName() ).isEqualTo( "testPropertyProperty" );
      assertThat( property.urn().toString() ).isEqualTo( "urn:samm:org.eclipse.esmf.test:1.0.0#testPropertyProperty" );

      final Entity entity = property.getCharacteristic()
            .flatMap( Characteristic::getDataType )
            .map( Entity.class::cast )
            .orElseThrow();
      assertThat( entity.getName() ).isEqualTo( "TestEntity" );
      assertThat( entity.urn().toString() ).isEqualTo( entitySemanticId );

      assertValidSerializedAspect( aspect, URI.create( "urn:samm:org.eclipse.esmf.test:1.0.0#TestAspect" ) );
   }

   @Test
   void testSubmodelElementCollectionPropertyAndEntityNamesDoNotCollide() {
      final String namespace = "io.admin-shell.idta.batterypass.product_condition";
      final String entitySemanticId = "urn:samm:%s:1.0.0#numberOfFullCycles".formatted( namespace );
      final SubmodelElementCollection collection = new DefaultSubmodelElementCollection.Builder()
            .idShort( "NumberOfFullCycles" )
            .semanticId( new DefaultReference.Builder()
                  .type( ReferenceTypes.EXTERNAL_REFERENCE )
                  .keys( List.of( new DefaultKey.Builder()
                        .type( KeyTypes.GLOBAL_REFERENCE )
                        .value( entitySemanticId )
                        .build() ) )
                  .build() )
            .value( List.of( new DefaultProperty.Builder()
                  .idShort( "cycleCount" )
                  .valueType( DataTypeDefXsd.INTEGER )
                  .build() ) )
            .build();
      final Submodel submodel = new DefaultSubmodel.Builder()
            .id( "urn:samm:%s:1.0.0#ProductCondition".formatted( namespace ) )
            .idShort( "ProductCondition" )
            .kind( ModellingKind.TEMPLATE )
            .submodelElements( List.of( collection ) )
            .build();
      final Environment environment = new DefaultEnvironment.Builder()
            .submodels( List.of( submodel ) )
            .build();

      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( environment );
      final Aspect aspect = aspectModelGenerator.generate().map( AspectArtifact::getContent ).findFirst().orElseThrow();

      final String result =
            assertValidSerializedAspect( aspect, URI.create( "urn:samm:%s:1.0.0#ProductCondition".formatted( namespace ) ) );
      assertThat( result ).contains( ":numberOfFullCyclesProperty a samm:Property" );
      assertThat( result ).contains( ":numberOfFullCycles a samm:Entity" );
      assertThat( result ).doesNotContain( ":numberOfFullCycles a samm:Property" );
   }

   @ParameterizedTest
   @MethodSource( "dbpRegressionAasxFiles" )
   void testDbpRegressionAasxFilesCanBeTranslatedAndValidated( final String aasxResource ) {
      assertAasxCanBeTranslatedAndValidated( aasxResource );
   }

   private static Stream<Arguments> dbpRegressionAasxFiles() {
      return Stream.of(
            "submodel-templates/published/Digital Battery Passport/"
                  + "2_Handover Documentation/1/0/IDTA 02035-2_DBP-Part-2_HandoverDocumentation.aasx",
            "submodel-templates/published/Digital Battery Passport/5_Product Condition/1/0/IDTA 02035-5_DBP-Part-5_ProductCondition.aasx",
            "submodel-templates/published/Digital Battery Passport/6_Material Composition/1/0/"
                  + "IDTA 02035-6_DBP-Part-6_MaterialComposition.aasx",
            "submodel-templates/published/Digital Battery Passport/7_Circularity/1/0/IDTA 02035-7_DBP-Part-7_Circularity.aasx"
      ).map( Arguments::of );
   }

   private static Environment buildTemplateEnvironment( final SubmodelElement submodelElement ) {
      final Submodel submodel = new DefaultSubmodel.Builder()
            .id( "https://example.com/submodel/list-or-set/1.0.0" )
            .idShort( "ListOrSetSubmodel" )
            .kind( ModellingKind.TEMPLATE )
            .submodelElements( List.of( submodelElement ) )
            .build();
      return new DefaultEnvironment.Builder()
            .submodels( List.of( submodel ) )
            .build();
   }

   private static Environment loadAasxEnvironment( final File aasxFile )
         throws IOException, InvalidFormatException, DeserializationException {
      try ( final InputStream input = new FileInputStream( aasxFile ) ) {
         final AASXDeserializer deserializer = new AASXDeserializer( input );
         return new XmlDeserializer().read( deserializer.getResourceString() );
      }
   }

   private static java.util.Set<String> submodelElementCollectionSemanticIdNames( final Environment environment ) {
      return Optional.ofNullable( environment.getSubmodels() ).orElseGet( List::of ).stream()
            .flatMap( submodel -> Optional.ofNullable( submodel.getSubmodelElements() ).orElseGet( List::of ).stream() )
            .flatMap( AasToAspectModelGeneratorTest::flattenSubmodelElement )
            .filter( SubmodelElementCollection.class::isInstance )
            .flatMap( element -> Optional.ofNullable( element.getSemanticId() ).stream() )
            .flatMap( SubmodelToAspectUtils::keys )
            .map( Key::getValue )
            .flatMap( value -> {
               try {
                  return Stream.of( AspectModelUrn.fromUrn( value ).getName() );
               } catch ( final RuntimeException exception ) {
                  return Stream.empty();
               }
            } )
            .collect( Collectors.toSet() );
   }

   private static Stream<SubmodelElement> flattenSubmodelElement( final SubmodelElement submodelElement ) {
      final Stream<SubmodelElement> nested;
      if ( submodelElement instanceof final SubmodelElementCollection collection ) {
         nested = Optional.ofNullable( collection.getValue() ).orElseGet( List::of ).stream()
               .flatMap( AasToAspectModelGeneratorTest::flattenSubmodelElement );
      } else if ( submodelElement instanceof final SubmodelElementList list ) {
         nested = Optional.ofNullable( list.getValue() ).orElseGet( List::of ).stream()
               .flatMap( AasToAspectModelGeneratorTest::flattenSubmodelElement );
      } else {
         nested = Stream.empty();
      }
      return Stream.concat( Stream.of( submodelElement ), nested );
   }

   private static SubmodelElementList buildSubmodelElementList( final boolean orderRelevant ) {
      final org.eclipse.digitaltwin.aas4j.v3.model.Property valueElement = new DefaultProperty.Builder()
            .idShort( "sampleValue" )
            .valueType( DataTypeDefXsd.STRING )
            .build();
      return new DefaultSubmodelElementList.Builder()
            .idShort( "testCollection" )
            .orderRelevant( orderRelevant )
            .typeValueListElement( AasSubmodelElements.PROPERTY )
            .value( List.of( valueElement ) )
            .build();
   }

   private static Stream<Characteristic> allCharacteristics( final Aspect aspect ) {
      return aspect.getProperties().stream()
            .flatMap( property -> property.getCharacteristic().stream() )
            .flatMap( AasToAspectModelGeneratorTest::allCharacteristics );
   }

   private static Stream<Characteristic> allCharacteristics( final Characteristic characteristic ) {
      final Stream<Characteristic> collectionCharacteristics = characteristic instanceof final Collection collection
            ? collection.getElementCharacteristic().stream().flatMap( AasToAspectModelGeneratorTest::allCharacteristics )
            : Stream.empty();
      final Stream<Characteristic> traitCharacteristics = characteristic instanceof final Trait trait
            ? allCharacteristics( trait.getBaseCharacteristic() )
            : Stream.empty();
      return Stream.concat( Stream.of( characteristic ), Stream.concat( collectionCharacteristics, traitCharacteristics ) );
   }

   private void assertAasxCanBeTranslatedAndValidated( final String aasxResource ) {
      try ( final InputStream inputStream = AasToAspectModelGeneratorTest.class.getClassLoader().getResourceAsStream( aasxResource ) ) {
         assertThat( inputStream ).as( aasxResource ).isNotNull();
         final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( inputStream );
         final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();

         assertThat( aspects ).as( aasxResource ).isNotEmpty();
         aspects.forEach( aspect -> assertValidSerializedAspect( aspect, URI.create( aspect.urn().toString() ) ) );
      } catch ( final IOException exception ) {
         fail( exception );
      }
   }

   private String assertValidSerializedAspect( final Aspect aspect, final URI sourceUri ) {
      final String result = AspectSerializer.INSTANCE.aspectToString( aspect );
      assertThat( result ).isNotBlank();
      final AspectModel aspectModel = new AspectModelLoader().load( new ByteArrayInputStream( result.getBytes() ), sourceUri );
      assertThat( new AspectModelValidator().validateModel( aspectModel ).violations() ).isEmpty();
      return result;
   }

   @Test
   void testGenerateSeeReferencesBasedOnSematicIdAndSupplementalSemanticIds() {
      final InputStream inputStream = AasToAspectModelGeneratorTest.class.getClassLoader().getResourceAsStream(
            "submodel-templates/published/Handover Documentation/2/0/IDTA 02004-2-0_Template_HandoverDocumentation.aasx" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( inputStream );
      final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();

      assertThatCode( aspectModelGenerator::generate ).doesNotThrowAnyException();

      // Check for 'entities' property
      aspects.stream()
            .flatMap( aspect -> aspect.getProperties().stream() )
            .filter( property -> property.getName().equals( "entities" ) )
            .findFirst()
            .ifPresentOrElse( property -> assertThat( property.getSee() )
                  .containsExactly( "https://admin-shell.io/vdi/2770/1/0/EntitiesForDocumentation" ),
                  () -> fail( "Property 'entities' not found" ) );

      // Check for 'documents' property
      aspects.stream()
            .flatMap( aspect -> aspect.getProperties().stream() )
            .filter( property -> property.getName().equals( "documents" ) )
            .findFirst()
            .ifPresentOrElse( property -> assertThat( property.getSee() )
                  .containsExactly(
                        "https://api.eclass-cdp.com/0173-1-02-ABI500-003",
                        "urn:irdi:0173-1#02-ABI500#003"
                  ),
                  () -> fail( "Property 'documents' not found" ) );
   }

   @Test
   void testGenerateAspectWithOptionalProperty() {
      final InputStream inputStream = AasToAspectModelGeneratorTest.class.getClassLoader().getResourceAsStream(
            "submodel-templates/published/Handover Documentation/2/0/IDTA 02004-2-0_Template_HandoverDocumentation.aasx" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( inputStream );
      final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();

      assertThatCode( aspectModelGenerator::generate ).doesNotThrowAnyException();

      // Check for 'entities' property
      aspects.stream()
            .flatMap( aspect -> aspect.getProperties().stream() )
            .filter( property -> property.getName().equals( "entities" ) )
            .findFirst()
            .ifPresentOrElse( property -> assertThat( property.isOptional() ).isTrue(),
                  () -> fail( "Property 'entities' not found" ) );
   }

   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( TestAspect.class )
   void testRoundtripConversion( final TestAspect testAspect ) throws DeserializationException {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final Consumer<AasToAspectModelGenerator> assertForValidator = aspectModelGenerator -> assertThatCode( () -> {
         final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();
         assertThat( aspects ).singleElement()
               .satisfies( generatedAspect -> assertThat( generatedAspect.urn() ).isEqualTo( aspect.urn() ) );
      } ).doesNotThrowAnyException();

      final byte[] content = new AspectModelAasGenerator( aspect,
            AasGenerationConfigBuilder.builder().format( AasFileFormat.XML ).build() ).getContent();
      assertThat( new String( content ) ).doesNotContain( "Optional[" );
      final Environment aasEnvironmentFromXml = new XmlDeserializer().read(
            new ByteArrayInputStream( content ) );
      assertForValidator.accept( AasToAspectModelGenerator.fromEnvironment( aasEnvironmentFromXml ) );

      final Environment aasEnvironmentFromJson = new JsonDeserializer().read(
            new ByteArrayInputStream( new AspectModelAasGenerator( aspect,
                  AasGenerationConfigBuilder.builder().format( AasFileFormat.JSON ).build() ).getContent() ),
            Environment.class );
      assertForValidator.accept( AasToAspectModelGenerator.fromEnvironment( aasEnvironmentFromJson ) );
   }

   @Test
   void testGetAspectModelUrnFromSubmodelIdentifier() {
      // Submodel has an Aspect Model URN as identifier
      final Environment aasEnvironment = loadEnvironment( "SMTWithAspectModelUrnId.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement()
            .satisfies( aspect -> assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testGetAspectModelUrnFromConceptDescription() {
      // Submodel has a Concept Description that points to an Aspect Model URN
      final Environment aasEnvironment = loadEnvironment( "SMTWithAspectModelUrnInConceptDescription.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement()
            .satisfies( aspect -> assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn1() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has a version and an IRI identifier and an idShort
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction1.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement()
            .satisfies( aspect -> assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example.www:1.2.3#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn2() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has a version and an IRDI identifier and an idShort
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction2.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement()
            .satisfies( aspect -> assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.2.3#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn3() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has an IRDI identifier and an idShort, but no version
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction3.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement()
            .satisfies( aspect -> assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn4() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has an IRDI identifier, but no idShort and no version
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction4.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement()
            .satisfies( aspect -> assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#AAAAAA000abf2fd07" ) );
   }

   private Environment loadEnvironment( final String name ) {
      try ( final InputStream inputStream = AasToAspectModelGeneratorTest.class.getClassLoader().getResourceAsStream( name ) ) {
         return new XmlDeserializer().read( inputStream );
      } catch ( final DeserializationException | IOException exception ) {
         fail( exception );
      }
      return null;
   }
}
