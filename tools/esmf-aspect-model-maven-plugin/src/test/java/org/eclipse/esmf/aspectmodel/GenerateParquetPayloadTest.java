/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

import java.io.IOException;
import java.nio.file.Path;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.LocalInputFile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@MojoTest
public class GenerateParquetPayloadTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
      goal = GenerateParquetPayload.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml" )
   public void testGenerateParquetPayload( final GenerateParquetPayload generateParquetPayload ) throws IOException {
      assertThatCode( generateParquetPayload::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.parquet" ) ).exists();
      assertCompressionCodec( "Aspect.parquet", CompressionCodecName.SNAPPY );
   }

   @Test
   @InjectMojo(
      goal = GenerateParquetPayload.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-parquet-options/pom.xml" )
   public void testGenerateParquetPayloadWithPayloadGenerationOptions(
         final GenerateParquetPayload generateParquetPayload ) throws IOException {
      assertThatCode( generateParquetPayload::execute ).doesNotThrowAnyException();
      final Path generatedFile = generatedFilePath( "AspectWithSimpleTypes.parquet" );
      assertThat( generatedFile ).exists().isNotEmptyFile();
      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( generatedFile ) ) ) {
         assertThat( reader.getRecordCount() ).isEqualTo( 3 );
         assertThat( reader.getFooter().getBlocks().getFirst().getColumns().getFirst().getCodec() )
               .isEqualTo( CompressionCodecName.GZIP );
      }
   }

   private void assertCompressionCodec( final String fileName, final CompressionCodecName expectedCodec ) throws IOException {
      try ( final ParquetFileReader reader = ParquetFileReader.open( new LocalInputFile( generatedFilePath( fileName ) ) ) ) {
         assertThat( reader.getFooter().getBlocks().getFirst().getColumns().getFirst().getCodec() ).isEqualTo( expectedCodec );
      }
   }
}
