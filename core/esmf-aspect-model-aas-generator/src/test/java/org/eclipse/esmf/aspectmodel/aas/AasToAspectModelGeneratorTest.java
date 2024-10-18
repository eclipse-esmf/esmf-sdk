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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AasToAspectModelGeneratorTest {

   @Test
   void testTranslateDigitalNameplate() {
      final InputStream aasx = getIdtaModel(
            "ZVEI_Digital_Nameplate/1/0/Sample_ZVEI_Digital_Nameplate_V10.aasx" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( aasx );
      assertThatCode( aspectModelGenerator::generateAspects ).doesNotThrowAnyException();
   }

   @Test
   void testSeeReferences() {
      final InputStream inputStream = getIdtaModel(
            "Wireless Communication/1/0/IDTA 02022-1-0_Template_Wireless Communication.aasx" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromAasx( inputStream );
      final List<Aspect> aspects = aspectModelGenerator.generateAspects();

      aspects.stream()
            .flatMap( aspect -> aspect.getProperties().stream() )
            .flatMap( property -> property.getSee().stream() )
            .forEach( see -> {
               assertThat( see ).doesNotContain( "/ " );
            } );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   void testRoundtripConversion( final TestAspect testAspect ) throws DeserializationException {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final Consumer<AasToAspectModelGenerator> assertForValidator = aspectModelGenerator ->
            assertThatCode( () -> {
               final List<Aspect> aspects = aspectModelGenerator.generateAspects();
               assertThat( aspects ).singleElement().satisfies( generatedAspect ->
                     assertThat( generatedAspect.urn() ).isEqualTo( aspect.urn() ) );
            } ).doesNotThrowAnyException();

      final Environment aasEnvironmentFromXml = new XmlDeserializer().read(
            new ByteArrayInputStream( new AspectModelAasGenerator().generateAsByteArray( AasFileFormat.XML, aspect ) ) );
      assertForValidator.accept( AasToAspectModelGenerator.fromEnvironment( aasEnvironmentFromXml ) );

      final Environment aasEnvironmentFromJson = new JsonDeserializer().read(
            new ByteArrayInputStream( new AspectModelAasGenerator().generateAsByteArray( AasFileFormat.JSON, aspect ) ),
            Environment.class );
      assertForValidator.accept( AasToAspectModelGenerator.fromEnvironment( aasEnvironmentFromJson ) );
   }

   @Test
   void testGetAspectModelUrnFromSubmodelIdentifier() {
      // Submodel has an Aspect Model URN as identifier
      final Environment aasEnvironment = loadEnvironment( "SMTWithAspectModelUrnId.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generateAspects() ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testGetAspectModelUrnFromConceptDescription() {
      // Submodel has a Concept Description that points to an Aspect Model URN
      final Environment aasEnvironment = loadEnvironment( "SMTWithAspectModelUrnInConceptDescription.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generateAspects() ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn1() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has a version and an IRI identifier and an idShort
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction1.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generateAspects() ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example.www:1.2.3#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn2() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has a version and an IRDI identifier and an idShort
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction2.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generateAspects() ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.2.3#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn3() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has an IRDI identifier and an idShort, but no version
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction3.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generateAspects() ).singleElement().satisfies( aspect ->
            assertThat( aspect.urn().toString() ).isEqualTo( "urn:samm:com.example:1.0.0#Submodel1" ) );
   }

   @Test
   void testConstructAspectModelUrn4() {
      // Submodel has no Aspect Model URN identifier and no Concept Description.
      // It has an IRDI identifier, but no idShort and no version
      final Environment aasEnvironment = loadEnvironment( "SMTAspectModelUrnInConstruction4.aas.xml" );
      final AasToAspectModelGenerator aspectModelGenerator = AasToAspectModelGenerator.fromEnvironment( aasEnvironment );
      assertThat( aspectModelGenerator.generateAspects() ).singleElement().satisfies( aspect ->
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

   private InputStream getIdtaModel( final String path ) {
      try {
         final URL url = new URL( "https://github.com/admin-shell-io/submodel-templates/raw/refs/heads/main/published/" + path.replaceAll( " ", "%20" ) );
         return url.openStream();
      } catch ( Exception e ) {
         e.printStackTrace();
         return null;
      }
   }
}
