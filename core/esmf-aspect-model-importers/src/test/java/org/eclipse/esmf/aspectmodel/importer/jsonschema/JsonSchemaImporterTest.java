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
import static org.eclipse.esmf.metamodel.DataTypes.xsd;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.value;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.generator.AspectArtifact;
import org.eclipse.esmf.aspectmodel.generator.EntityArtifact;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.serializer.RdfModelCreatorVisitor;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.vocabulary.SimpleRdfNamespace;
import org.eclipse.esmf.test.TestModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import io.vavr.control.Either;
import org.apache.jena.rdf.model.Model;
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
   final AspectModelUrn testUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "Test" );

   private JsonNode loadSchema( final String schemaName ) {
      try {
         final String resourcePath = "/" + SCHEMAS_LOCATION + "/" + schemaName.replace( "https://example.com/", "" );
         final URL resource = JsonSchemaImporterTest.class.getResource( resourcePath );
         final File file = new File( resource.toURI() );
         return objectMapper.readTree( file );
      } catch ( final Exception exception ) {
         return null;
      }
   }

   private AspectModel loadAsAspectModel( final ModelElement modelElement ) {
      final String modelSource = AspectSerializer.INSTANCE.modelElementToString( modelElement );
      //      System.out.println( modelSource );

      final AspectModelFile file = AspectModelFileLoader.load( modelSource, URI.create( "inmemory" ) );
      final Either<List<Violation>, AspectModel> loadingResult =
            new AspectModelLoader().withValidation( new AspectModelValidator() ).loadAspectModelFiles( List.of( file ) );
      return loadingResult.mapLeft( violations -> new ViolationFormatter().apply( loadingResult.getLeft() ) )
            .getOrElseThrow( report -> {
               throw new RuntimeException( report );
            } );
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
      final JsonSchemaImporterConfig config = JsonSchemaImporterConfigBuilder.builder()
            .aspectModelUrn( testUrn )
            .customRefResolver( ref -> switch ( ref ) {
               case "spdx.schema.json" -> spdx;
               case "jsf-0.82.schema.json#/definitions/signature" -> jsfDefs.get( "signature" );
               case "#/definitions/signer" -> jsfDefs.get( "signer" );
               case "#/definitions/keyType" -> jsfDefs.get( "keyType" );
               case "#/definitions/publicKey" -> jsfDefs.get( "publicKey" );
               default -> null;
            } )
            .build();
      final JsonSchemaToAspect jsonSchemaToAspect = new JsonSchemaToAspect( jsonNode, config );
      final AspectArtifact aspectArtifact = jsonSchemaToAspect.generate().findFirst().orElseThrow();

      final AspectModel aspectModel = loadAsAspectModel( aspectArtifact.getContent() );
      assertThat( aspectModel ).aspects().hasSize( 1 );
      final Aspect aspect = aspectModel.aspect();

      // Base attributes
      assertThat( aspect ).operations().isEmpty();
      assertThat( aspect ).hasPreferredName( "CycloneDX Bill of Materials Standard" );
      assertThat( aspect ).hasDescription( "CycloneDX JSON schema is published under the terms of the Apache License 2.0." );
      // Test name handling name clashes
      assertThat( aspect ).propertyByName( "services0" )
            .hasValueSatisfying( property -> assertThat( property.getPayloadName() ).isEqualTo( "services" ) );
      // Property optionality
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "LicenseEntity" ) )
            .isEntityThat().propertyByName( "id" ).hasValueSatisfying( property -> assertThat( property ).isOptional() );
      // Property example value
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "author" ) )
            .isPropertyThat().hasExampleValue( value( "Acme Inc" ) );
      // Base characteristic
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "VersionRange" ) )
            .isCharacteristicThat().hasDataType( xsd.string );
      // Sorted sets
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "formulation" ) )
            .isPropertyThat().hasCharacteristicThat().isSortedSetThat().hasPreferredName( "Formulation" );
      // Lists
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "aliases" ) )
            .isPropertyThat().hasCharacteristicThat().isListThat().elementCharacteristic().hasDataType( xsd.string );
      // SingleEntity
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "source" ) )
            .isPropertyThat().hasCharacteristicThat().isSingleEntityThat().dataType().isEntityThat().hasName( "SourceEntity" );
      // Enumeration with samm:Values with descriptions
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "DataFlowDirection" ) )
            .isCharacteristicThat().isEnumerationThat().hasDataType( xsd.string ).values().hasSize( 4 ).allSatisfy( value -> {
               assertThat( value ).descriptions().isNotEmpty();
               assertThat( value ).isAnonymous();
            } );
      // Enumeration with string literals
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "state" ) )
            .isPropertyThat().hasCharacteristicThat()
            .isEnumerationThat().hasDataType( xsd.string ).values().hasSize( 6 ).allSatisfy( value -> {
               assertThat( value ).descriptions().isEmpty();
               assertThat( value ).isAnonymous();
            } );
      // Either
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "vulnerabilitiesElementTools" ) )
            .isPropertyThat().hasCharacteristicThat().isEitherThat().satisfies( either -> {
               assertThat( either ).left().isSingleEntity();
               assertThat( either ).right().isList();
            } );
      // Range constraints with integer type
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "nistQuantumSecurityLevel" ) ).isPropertyThat()
            .hasCharacteristicThat().isTraitThat()
            .hasDataType( xsd.integer )
            .hasSingleConstraintThat().isRangeConstraintThat()
            .hasMinValue( value( 0, xsd.integer ) )
            .hasMaxValue( value( 6, xsd.integer ) );
      // Range constraints with double type
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "confidence" ) ).isPropertyThat()
            .hasCharacteristicThat().isTraitThat()
            .hasDataType( xsd.double_ )
            .hasSingleConstraintThat().isRangeConstraintThat()
            .hasMinValue( value( 0.0d ) )
            .hasMaxValue( value( 1.0d ) );
      // Length constraint
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "VersionRangeTrait" ) )
            .isCharacteristicThat().isTraitThat()
            .hasDataType( xsd.string )
            .hasSingleConstraintThat()
            .isLengthConstraintThat()
            .hasMinValue( BigInteger.valueOf( 1 ) )
            .hasMaxValue( BigInteger.valueOf( 4096 ) );
      // Regular expression constraints
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "LocaleTypeTrait" ) )
            .isCharacteristicThat().isTraitThat().hasSingleConstraintThat().isRegularExpressionConstraint();
      // "format" that leads to xsd:anyURI
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "references" ) )
            .isPropertyThat().hasCharacteristicThat().isListThat().elementCharacteristic().hasDataType( xsd.anyURI );
      // "format" that leads to xsd:dataTimeStamp
      assertThat( aspectModel ).modelElementByUrn( testUrn.withName( "created" ) )
            .isPropertyThat().hasCharacteristicThat().hasDataType( xsd.dateTimeStamp );
   }

   private Entity translateJsonSchema( final String schemaName ) {
      final JsonNode jsonSchema = loadSchema( schemaName );
      assertThat( jsonSchema ).isNotNull();
      final JsonSchemaImporterConfig config = JsonSchemaImporterConfigBuilder.builder()
            .aspectModelUrn( AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "Test" ) )
            .customRefResolver( this::loadSchema )
            .addTodo( true )
            .build();
      final JsonSchemaToEntity jsonSchemaToEntity = new JsonSchemaToEntity( jsonSchema, config );
      final EntityArtifact entityArtifact = jsonSchemaToEntity.generate().findFirst().orElseThrow();
      return entityArtifact.getContent();
   }

   @ParameterizedTest
   @MethodSource( value = "schemaNames" )
   void testTranslateJsonSchema( final String schemaName ) throws IOException {
      final Entity entity = translateJsonSchema( schemaName );
      loadAsAspectModel( entity );
   }

   @Test
   void testReuseProperties() {
      final Entity entity = translateJsonSchema( "reuse-properties.schema.json" );
      final List<String> propertyNames = entity.getProperties().stream().map( ModelElement::getName ).toList();
      assertThat( propertyNames ).contains( "name", "address" );
   }

   @Test
   void testPreventNameClashes() {
      final Entity entity = translateJsonSchema( "duplicate-properties.schema.json" );
      final Model model = createModelForElement( entity );
      final List<String> propertyNames = Streams.stream( model.listStatements( null, RDF.type, SammNs.SAMM.Property() ) )
            .map( Statement::getSubject )
            .map( Resource::getURI )
            .map( AspectModelUrn::fromUrn )
            .map( AspectModelUrn::getName )
            .toList();
      assertThat( propertyNames ).contains( "name", "address", "addressName" );
   }

   @Test
   void testTodoComments() {
      final Entity entity = translateJsonSchema( "ecommerce.schema.json" );
      final AspectModel model = loadAsAspectModel( entity );
      assertThat( entity.getDescription( Locale.ENGLISH ) ).isEqualTo( "TODO" );
      assertThat( model.getElementByUrn( testUrn.withName( "ProductEntity" ) )
            .getDescription( Locale.ENGLISH ) ).isEqualTo( "TODO" );
      assertThat( model.getElementByUrn( testUrn.withName( "product" ) )
            .getDescription( Locale.ENGLISH ) ).isEqualTo( "TODO" );
      assertThat( model.getElementByUrn( testUrn.withName( "orderId" ) )
            .getDescription( Locale.ENGLISH ) ).isEqualTo( "TODO" );
      assertThat( model.getElementByUrn( testUrn.withName( "order" ) )
            .getDescription( Locale.ENGLISH ) ).isEqualTo( "TODO" );
   }

   @Test
   void testOptionalProperties() {
      final Entity entity = translateJsonSchema( "address.schema.json" );
      final AspectModel model = loadAsAspectModel( entity );
      assertThat( model.getElementByUrn( testUrn ) ).isEntityThat()
            .propertyByName( "postOfficeBox" ).hasValueSatisfying( property -> assertThat( property ).isOptional() );
      assertThat( model.getElementByUrn( testUrn ) ).isEntityThat()
            .propertyByName( "locality" ).hasValueSatisfying( property -> assertThat( property ).isNotOptional() );
   }

   private Model createModelForElement( final ModelElement element ) {
      final RdfNamespace namespace = new SimpleRdfNamespace( "", element.urn().getUrnPrefix() );
      final Model rdfModel = element.accept( new RdfModelCreatorVisitor( namespace ), null ).model();
      RdfUtil.cleanPrefixes( rdfModel );
      return rdfModel;
   }

   static Stream<Arguments> schemaNames() throws URISyntaxException, IOException {
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
               .map( file -> Arguments.of( Named.of( file.getName(), file.getName() ) ) )
               .toList();
         return list.stream();
      }
   }
}
