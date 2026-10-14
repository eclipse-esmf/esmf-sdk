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

package org.eclipse.esmf.aspectmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@MojoTest
public class GenerateJsonPayloadTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
      goal = GenerateJsonPayload.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml" )
   public void testGenerateJsonPayload( final GenerateJsonPayload generateJsonPayload ) {
      assertThatCode( generateJsonPayload::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.json" ) ).exists();
   }

   @Test
   @InjectMojo(
      goal = GenerateJsonPayload.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-json-payload-count/pom.xml" )
   public void testGenerateJsonPayloadWithCount( final GenerateJsonPayload generateJsonPayload ) throws IOException {
      assertThatCode( generateJsonPayload::execute ).doesNotThrowAnyException();
      final String output = Files.readString( generatedFilePath( "Aspect.json" ) );
      assertThat( output ).endsWith( "\n" );
      final List<String> records = output.lines().toList();
      assertThat( records ).hasSize( 3 );
      final JsonMapper jsonMapper = JsonMapper.builder().build();
      for ( final String record : records ) {
         assertThat( jsonMapper.readTree( record ).isObject() ).isTrue();
      }
   }

   @Test
   @InjectMojo(
      goal = GenerateJsonPayload.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-random-timestamp/pom.xml" )
   public void testGenerateJsonPayloadWithRandomTimestamp( final GenerateJsonPayload generateJsonPayload ) {
      assertThatCode( generateJsonPayload::execute ).doesNotThrowAnyException();
      final JsonNode payload = JsonMapper.builder().build()
            .readTree( generatedFilePath( "AspectWithSimpleTypes.json" ).toFile() );
      // While there is an extremely rare chance of failure theoretically,
      // running it again is supposed to result in success almost certainly.
      assertThat( payload.get( "dateTimeProperty" ).asString() ).isNotEqualTo( "1970-01-01T00:00:00.000Z" );
      assertThat( payload.get( "dateTimeStampProperty" ).asString() ).isNotEqualTo( "1970-01-01T00:00:00.000Z" );
   }
}
