/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

package org.eclipse.esmf.aspectmodel.generator.protobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.metamodel.DataTypes.xsd;
import static org.eclipse.esmf.metamodel.Elements.samm_c;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.aspect;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.either;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.list;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.property;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AspectModelProtobufGeneratorTest {
   private String generate( final TestAspect model ) {
      final Aspect aspect = TestResources.load( model ).aspect();
      final AspectModelProtobufGenerator generator = new AspectModelProtobufGenerator( aspect );
      final String schema = generator.getContent();
      assertThat( generator.getContent() ).isEqualTo( schema );
      return schema;
   }

   @Test
   void generatesSingleNamedArtifactWithExplicitConfig() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY ).aspect();
      final ProtobufGenerationConfig config = ProtobufGenerationConfigBuilder.builder().build();
      final AspectModelProtobufGenerator generator = new AspectModelProtobufGenerator( aspect, config );
      assertThat( generator.getConfig() ).isSameAs( config );
      assertThat( generator.generate() ).singleElement().satisfies( artifact -> {
         assertThat( artifact.getId() ).isEqualTo( "AspectWithEntity.proto" );
         assertThat( artifact.getContent() ).isEqualTo( generate( TestAspect.ASPECT_WITH_ENTITY ) );
         assertThat( artifact.serialize() ).isEqualTo( artifact.getContent().getBytes( StandardCharsets.UTF_8 ) );
      } );
   }

   @Test
   void writesArtifactThroughGeneratorOutputMapping() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY ).aspect();
      final AspectModelProtobufGenerator generator = new AspectModelProtobufGenerator( aspect );
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      assertThat( generator.getConfig() ).isSameAs( AspectModelProtobufGenerator.DEFAULT_CONFIG );
      generator.generate( name -> {
         assertThat( name ).isEqualTo( "AspectWithEntity.proto" );
         return output;
      } );
      assertThat( output.toString( StandardCharsets.UTF_8 ) ).isEqualTo( generator.getContent() );
   }

   @Test
   void generatesEntityMessages() {
      assertThat( generate( TestAspect.ASPECT_WITH_ENTITY ) ).isEqualTo( """
         syntax = "proto3";

         package org.eclipse.esmf.test.v1_0_0;

         message AspectWithEntity {
           .org.eclipse.esmf.test.v1_0_0.TestEntity testProperty = 1;
         }

         message TestEntity {
           string entityProperty = 1;
         }
         """ );
   }

   @ParameterizedTest
   @CsvSource( {
         "org.example.sensors, org.example.sensors",
         "org.example.sensor-model, org.example.sensor_model",
         "org.example.123.message, org.example.samm_123.samm_message"
   } )
   void derivesPackageFromNamespace( final String namespace, final String expectedPackage ) {
      final Aspect aspect = aspect( AspectModelUrn.fromUrn( "urn:samm:" + namespace + ":1.2.3#Sensor" ) ).build();
      assertThat( new AspectModelProtobufGenerator( aspect ).getContent() ).isEqualTo(
            "syntax = \"proto3\";\n\npackage " + expectedPackage + ".v1_2_3;\n\nmessage Sensor {\n}\n" );
   }

   @Test
   void qualifiesForeignEntityReferencesWithAspectPackage() {
      final Aspect source = TestResources.load( TestAspect.ASPECT_WITH_ENTITY ).aspect();
      final Aspect aspect = aspect( AspectModelUrn.fromUrn( "urn:samm:org.example.sensor-model:1.0.0#Sensor" ) )
            .property( source.getProperties().getFirst() ).build();
      assertThat( new AspectModelProtobufGenerator( aspect ).getContent() )
            .contains( "package org.example.sensor_model.v1_0_0;", ".org.example.sensor_model.v1_0_0.TestEntity testProperty = 1;",
                  "message TestEntity {" )
            .doesNotContain( ".org.eclipse.esmf.test.v1_0_0.TestEntity" );
   }

   @ParameterizedTest
   @CsvSource( {
         "1.0.0, v1_0_0",
         "1.1.0, v1_1_0",
         "1.1.1, v1_1_1",
         "2.0.0, v2_0_0",
         "12.34.56, v12_34_56"
   } )
   void includesFullModelVersionInPackageAndReferences( final String version, final String versionSegment ) {
      final Aspect source = TestResources.load( TestAspect.ASPECT_WITH_ENTITY ).aspect();
      final Aspect aspect = aspect( AspectModelUrn.fromUrn( "urn:samm:org.example.sensors:" + version + "#Sensor" ) )
            .property( source.getProperties().getFirst() ).build();
      final String packageName = "org.example.sensors." + versionSegment;
      assertThat( new AspectModelProtobufGenerator( aspect ).getContent() )
            .contains( "package " + packageName + ";", "." + packageName + ".TestEntity testProperty = 1;" )
            .doesNotContain( ".org.eclipse.esmf.test.v1_0_0.TestEntity" );
   }

   @Test
   void mapsScalarTypesWithoutLosingNumericRange() {
      final String schema = generate( TestAspect.ASPECT_WITH_SIMPLE_TYPES );
      assertThat( schema ).contains( "bytes base64BinaryProperty =", "bytes hexBinaryProperty =", "bool booleanProperty =",
            "int32 byteProperty =", "int32 shortProperty =", "int32 intProperty =", "int64 longProperty =",
            "uint32 unsignedByteProperty =", "uint32 unsignedShortProperty =", "uint32 unsignedIntProperty =",
            "uint64 unsignedLongProperty =", "string integerProperty =", "string decimalProperty =",
            "float floatProperty =", "double doubleProperty =", "string dateTimeProperty =", "string dateProperty =" );
   }

   @Test
   void usesOptionalAndPayloadNames() {
      assertThat( generate( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY_WITH_PAYLOAD_NAME ) ).contains( "optional string test = 1;" );
   }

   @Test
   void generatesRepeatedFields() {
      assertThat( generate( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE ) ).contains( "repeated int32 testList = 1;" );
   }

   @Test
   void generatesOneofForEither() {
      assertThat( generate( TestAspect.ASPECT_WITH_EITHER ) ).contains( ".org.eclipse.esmf.test.v1_0_0.TestEither testProperty = 1;",
            "message TestEither {", "oneof value {", "string left = 1;", "bool right = 2;" );
   }

   @Test
   void emitsRecursiveEntityOnlyOnce() {
      assertThat( generate( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL ) )
            .containsOnlyOnce( "message TestEntity {" ).contains( "optional .org.eclipse.esmf.test.v1_0_0.TestEntity testProperty = 1;" );
   }

   @Test
   void excludesPropertiesOutsidePayload() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES ).aspect();
      final String schema = new AspectModelProtobufGenerator( aspect ).getContent();
      assertThat( schema ).doesNotContain( " description =" );
      assertThat( schema ).contains( "message " + aspect.getName() );
   }

   @Test
   void wrapsCollectionAlternativesInOneof() {
      final AspectModelUrn namespace = TestAspect.ASPECT.getUrn();
      final Aspect aspect = aspect( namespace )
            .property( property( namespace.withName( "choice" ) )
                  .characteristic( either( namespace.withName( "Choice" ) )
                        .left( list( namespace.withName( "Items" ) ).dataType( xsd.int_ ).build() )
                        .right( samm_c.Text ).build() )
                  .build() )
            .build();
      assertThat( new AspectModelProtobufGenerator( aspect ).getContent() )
            .contains( ".org.eclipse.esmf.test.v1_0_0.Items left = 1;", "message Items {", "repeated int32 values = 1;" );
   }

   @Test
   void normalizesReservedIdentifiers() {
      final AspectModelUrn namespace = TestAspect.ASPECT.getUrn();
      final Aspect aspect = aspect( namespace.withName( "message" ) )
            .property( property( namespace.withName( "string" ) ).characteristic( samm_c.Text ).build() ).build();
      assertThat( new AspectModelProtobufGenerator( aspect ).getContent() )
            .contains( "message samm_message {", "string samm_string = 1;" );
   }

}
