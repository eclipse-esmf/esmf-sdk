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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectArtifact;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
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
      try ( final InputStream input = new FileInputStream( aasxFile ) ) {
         final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( input );
         final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();
         if ( aspects.isEmpty() ) {
            fail( "Translation of " + aasxFile.getName() + " yielded no Aspects" );
         }
         final String result = AspectSerializer.INSTANCE.aspectToString( aspects.iterator().next() );
         final AspectModel aspectModel = new AspectModelLoader().load( new ByteArrayInputStream( result.getBytes() ), aasxFile.toURI() );

         aspectModel.elements().forEach( element -> {
            if ( element instanceof Property || element instanceof Operation || element instanceof Unit ) {
               assertThat( element.getName().charAt( 0 ) )
                     .describedAs( element.getName() + " is a " + element.getClass().getSimpleName() + " and must be lower case" )
                     .isLowerCase();
            } else {
               assertThat( element.getName().charAt( 0 ) )
                     .describedAs( element.getName() + " is a " + element.getClass().getSimpleName() + " and must be upper case" )
                     .isUpperCase();
            }
         } );

         final List<Violation> violations = new AspectModelValidator().validateModel( aspectModel );
         if ( !violations.isEmpty() ) {
            final String report = new ViolationFormatter().apply( violations );
            System.out.println( report );
            System.out.println( "====" );
            System.out.println( result );
            fail();
         }
      } catch ( final IOException exception ) {
         fail( exception );
      } catch ( final AspectModelGenerationException aspectModelGenerationException ) {
         if ( aspectModelGenerationException.getCause() instanceof final DeserializationException cause ) {
            System.err.println( "Could not load AASX file: " + aasxFile.getName() + ". Consider reporting to IDTA or AAS4J project." );
         } else {
            fail( aspectModelGenerationException );
         }
      }
   }

   private static final List<String> IGNORED_AASX_FILES = List.of(
         "IDTA 02004-2-0_Example_HandoverDocumentation.aasx",
         "IDTA 02011-1-0_Template_HierarchicalStructuresEnablingBoM.aasx",
         "IDTA 02011-1-1_Template_HSEBoM.aasx",
         "IDTA 02011-1-1-1_Template_HSEBoM.aasx",
         "IDTA 02011-1-1-1_Template_HSEBoM_forAASMetamodelV3.1.aasx",
         "IDTA 02011-1-1-1 _Template_BoM_ExtensionbasedonIEC81346.aasx",
         "IDTA 02011-1-1-1 _Template_BoM_ExtensionbasedonIEC81346_forAASMetamodelV3.1.aasx",
         "IDTA 02011-1_Template_BoM_ExtensionbasedonIEC81346.aasx",
         "IDTA 02003_Sample_TechnicalData_forAASMetamodelV3.1.aasx",
         "IDTA 02003_Sample_TechnicalData.aasx"
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

   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( TestAspect.class )
   void testRoundtripConversion( final TestAspect testAspect ) throws DeserializationException {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final Consumer<AasToAspectModelGenerator> assertForValidator = aspectModelGenerator ->
            assertThatCode( () -> {
               final List<Aspect> aspects = aspectModelGenerator.generate().map( AspectArtifact::getContent ).toList();
               assertThat( aspects ).singleElement().satisfies( generatedAspect ->
                     assertThat( generatedAspect.urn() ).isEqualTo( aspect.urn() ) );
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
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testGetAspectModelUrnFromConceptDescription() {
      // Submodel has a Concept Description that points to an Aspect Model URN
      final Environment aasEnvironment = loadEnvironment( "SMTWithAspectModelUrnInConceptDescription.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn1() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has a version and an IRI identifier and an idShort
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction1.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example.www:1.2.3#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn2() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has a version and an IRDI identifier and an idShort
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction2.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.2.3#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn3() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has an IRDI identifier and an idShort, but no version
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction3.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn4() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has an IRDI identifier, but no idShort and no version
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction4.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generate() ).map( AspectArtifact::getContent ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#AAAAAA000abf2fd07" ) );
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
