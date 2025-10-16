/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.importer.jsonschema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.generator.AspectArtifact;
import org.eclipse.esmf.aspectmodel.generator.EntityArtifact;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.serializer.RdfModelCreatorVisitor;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.vocabulary.SimpleRdfNamespace;
import org.eclipse.esmf.test.TestModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class JsonSchemaImporterTest {
   private static final String SCHEMAS_LOCATION = "schemas";
   final ObjectMapper objectMapper = new ObjectMapper();

   private JsonNode loadSchema( final String schemaName ) {
      try {
         final String resourcePath = "/" + SCHEMAS_LOCATION + "/" + schemaName.replace( "https://example.com/", "" );
         final URL resource = JsonSchemaImporterTest.class.getResource( resourcePath );
         final File file = new File( resource.toURI() );
         return objectMapper.readTree( file );
      } catch ( final Exception exception ) {
         fail();
         return null;
      }
   }

   @Test
   void testTranslateCycloneDxBom() {
      final JsonNode jsonNode = loadSchema( "cyclonedx-bom-1.6.schema.json" );
      assertThat( jsonNode ).isNotNull();
      final JsonNode spdx = loadSchema( "spdx.schema.json" );
      assertThat( spdx ).isNotNull();
      final JsonNode jsf = loadSchema( "jsf-0.82.schema.json" );
      assertThat( jsf ).isNotNull();
      final JsonNode jsfDefs = jsf.get( "definitions" );
      final AspectGenerationConfig config = AspectGenerationConfigBuilder.builder()
            .aspectModelUrn( AspectModelUrn.fromParts( "org.eclipse.esmf.example", "1.0.0", "TestAspect" ) )
            .customRefResolver( ref -> {
               return switch ( ref ) {
                  case "spdx.schema.json" -> spdx;
                  case "jsf-0.82.schema.json#/definitions/signature" -> jsfDefs.get( "signature" );
                  case "#/definitions/signer" -> jsfDefs.get( "signer" );
                  case "#/definitions/keyType" -> jsfDefs.get( "keyType" );
                  case "#/definitions/publicKey" -> jsfDefs.get( "publicKey" );
                  default -> null;
               };
            } )
            .build();
      final JsonSchemaToAspect jsonSchemaToAspect = new JsonSchemaToAspect( jsonNode, config );
      final AspectArtifact aspectArtifact = jsonSchemaToAspect.generate().findFirst().orElseThrow();
      final Aspect aspect = aspectArtifact.getContent();
      System.out.println( AspectSerializer.INSTANCE.aspectToString( aspect ) );
   }

   @ParameterizedTest
   @MethodSource( value = "schemaFiles" )
   void testLoadSchema( final File schemaFile ) throws IOException {
      final JsonNode jsonNode = objectMapper.readTree( schemaFile );
      final AspectGenerationConfig config = AspectGenerationConfigBuilder.builder()
            .aspectModelUrn( AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "Test" ) )
            .customRefResolver( this::loadSchema )
            .addTodo( true )
            .build();
      assertThatCode( () -> {
         final JsonSchemaToAspect jsonSchemaToAspect = new JsonSchemaToAspect( jsonNode, config );
         final AspectArtifact aspectArtifact = jsonSchemaToAspect.generate().findFirst().orElseThrow();
         final Aspect aspect = aspectArtifact.getContent();
      } ).doesNotThrowAnyException();

      assertThatCode( () -> {
         final JsonSchemaToEntity jsonSchemaToAspect = new JsonSchemaToEntity( jsonNode, config );
         final EntityArtifact entityArtifact = jsonSchemaToAspect.generate().findFirst().orElseThrow();
         final Entity entity = entityArtifact.getContent();
         final Model model = createModelForElement( entity );
         System.out.println( AspectSerializer.INSTANCE.modelElementToString( entity ) );
         Streams.stream( model.listStatements( null, RDF.type, SammNs.SAMM.Property() ) )
               .forEach( statement -> {
                  final Resource property = statement.getSubject();
                  final int numberOfCharacteristics = model.listStatements( property, SammNs.SAMM.characteristic(), (RDFNode) null )
                        .toList().size();
                  assertThat( numberOfCharacteristics ).as(
                        "Property " + property.getURI() + " must not have more than one Characteristic" ).isOne();
               } );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testReuseProperties() throws JsonProcessingException {
      final String schema = """
            {
              "$id": "https://example.com/duplicate-properties.schema.json",
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "title": "Person",
              "type": "object",
              "properties": {
                "name": {
                  "type": "string"
                },
                "address": {
                  "type": "object",
                  "properties": {
                    "name": {
                      "type": "string"
                    },
                    "required": [
                      "name"
                    ]
                  },
                  "required": [
                    "name",
                    "address"
                  ]
                }
              }
            }
            """;

      final JsonNode jsonNode = objectMapper.readTree( schema );
      final AspectGenerationConfig config = AspectGenerationConfigBuilder.builder()
            .aspectModelUrn( AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "Test" ) )
            .build();
      final JsonSchemaToAspect jsonSchemaToAspect = new JsonSchemaToAspect( jsonNode, config );
      final AspectArtifact aspectArtifact = jsonSchemaToAspect.generate().findFirst().orElseThrow();
      final Aspect aspect = aspectArtifact.getContent();

      final List<String> propertyNames = aspect.getProperties().stream().map( ModelElement::getName ).toList();
      assertThat( propertyNames ).contains( "name", "address" );
   }

   @Test
   void testPreventNameClashes() throws JsonProcessingException {
      final String schema = """
            {
              "$id": "https://example.com/duplicate-properties.schema.json",
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "title": "Person",
              "type": "object",
              "properties": {
                "name": {
                  "type": "string"
                },
                "address": {
                  "type": "object",
                  "properties": {
                    "name": {
                      "type": "integer"
                    }
                  },
                  "required": [
                    "name"
                  ]
                }
              },
              "required": [
                "name",
                "address"
              ]
            }
            """;

      final JsonNode jsonNode = objectMapper.readTree( schema );
      final AspectGenerationConfig config = AspectGenerationConfigBuilder.builder()
            .aspectModelUrn( AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "Test" ) )
            .build();
      final JsonSchemaToEntity jsonSchemaToAspect = new JsonSchemaToEntity( jsonNode, config );
      final EntityArtifact entityArtifact = jsonSchemaToAspect.generate().findFirst().orElseThrow();
      final Entity entity = entityArtifact.getContent();
      final Model model = createModelForElement( entity );
      System.out.println( AspectSerializer.INSTANCE.modelElementToString( entity ) );

      final List<String> propertyNames = Streams.stream( model.listStatements( null, RDF.type, SammNs.SAMM.Property() ) )
            .map( Statement::getSubject )
            .map( Resource::getURI )
            .map( AspectModelUrn::fromUrn )
            .map( AspectModelUrn::getName )
            .toList();
      assertThat( propertyNames ).contains( "name", "address", "addressName" );
   }

   private Model createModelForElement( final ModelElement element ) {
      final RdfNamespace namespace = new SimpleRdfNamespace( "", element.urn().getUrnPrefix() );
      final Model rdfModel = element.accept( new RdfModelCreatorVisitor( namespace ), null ).model();
      RdfUtil.cleanPrefixes( rdfModel );
      return rdfModel;
   }

   static Stream<Arguments> schemaFiles() throws URISyntaxException, IOException {
      final URL resource = JsonSchemaImporterTest.class.getResource( "/" + SCHEMAS_LOCATION );
      assertNotNull( resource );
      try ( final Stream<Path> stream = Files.walk( Paths.get( resource.toURI() ) ) ) {
         final List<Arguments> list = stream.filter( Files::isRegularFile )
               .filter( file -> file.getFileName().toString().endsWith( ".schema.json" ) )
               .filter( file -> {
                  // Exclude: This schema does not declare a type attribute
                  return !file.getFileName().toString().equals( "enumerated-values.schema.json" );
               } )
               .filter( file -> {
                  // Exclude: File is handled in separate test
                  return !file.getFileName().toString().contains( "cyclonedx-bom-1.6.schema.json" );
               } )
               .map( Path::toFile )
               .map( file -> Arguments.of( Named.of( file.getName(), file ) ) )
               .toList();
         return list.stream();
      }
   }
}
