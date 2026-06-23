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

package org.eclipse.esmf.aspectmodel.generator.parquet;

import java.io.IOException;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.aspectmodel.generator.ParquetArtifact;
import org.eclipse.esmf.metamodel.Aspect;

/**
 * Generator for Apache Parquet payload files from Aspect Models.
 *
 * <p>
 * This generator creates sample Parquet files with data that conforms to the Aspect Model
 * structure.
 * It delegates to the following components:
 * </p>
 * <ul>
 * <li>{@link ParquetPayloadGenerator} — visitor-based payload generation (analogous to
 * {@link org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadGenerator})</li>
 * <li>{@link ParquetFileWriter} — data extraction, flattening, and Parquet file writing</li>
 * <li>{@link ParquetSchemaMapper} — XSD-to-Parquet type mapping</li>
 * </ul>
 *
 * @see org.eclipse.esmf.aspectmodel.generator.json.AspectModelJsonPayloadGenerator
 */
public class AspectModelParquetPayloadGenerator extends AspectGenerator<String, byte[], ParquetGenerationConfig, ParquetArtifact> {

   public static final ParquetGenerationConfig DEFAULT_CONFIG = ParquetGenerationConfigBuilder.builder().build();

   public AspectModelParquetPayloadGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelParquetPayloadGenerator( final Aspect aspect, final ParquetGenerationConfig config ) {
      super( aspect, config );
   }

   @Override
   public Stream<ParquetArtifact> generate() {
      final Aspect aspect = aspect();
      final ParquetExampleValueGenerator exampleValueGenerator = new ParquetExampleValueGenerator( config.randomStrategy() );
      final ParquetFileWriter fileWriter = new ParquetFileWriter( exampleValueGenerator );

      try {
         // Generate the Parquet file
         final byte[] parquetData = fileWriter.generateParquetFile( aspect );
         return Stream.of( new ParquetArtifact( aspect.getName() + ".parquet", parquetData ) );
      } catch ( final IOException e ) {
         throw new DocumentGenerationException( e );
      }
   }
}
