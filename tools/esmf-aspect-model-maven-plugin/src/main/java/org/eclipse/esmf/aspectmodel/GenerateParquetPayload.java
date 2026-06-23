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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.esmf.aspectmodel.generator.ParquetArtifact;
import org.eclipse.esmf.aspectmodel.generator.parquet.AspectModelParquetPayloadGenerator;
import org.eclipse.esmf.aspectmodel.generator.parquet.ParquetGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.parquet.ParquetGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.Set;

@Mojo( name = GenerateParquetPayload.MAVEN_GOAL,
   defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateParquetPayload extends AspectModelMojo {
   public static final String MAVEN_GOAL = "generateParquetPayload";
   private static final Logger LOG = LoggerFactory.getLogger( GenerateParquetPayload.class );

   @Parameter( defaultValue = "false" )
   private boolean addTypeAttribute;

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<Aspect> aspects = loadAspects();
      for ( final Aspect context : aspects ) {
         final ParquetGenerationConfig config = ParquetGenerationConfigBuilder.builder()
               .addTypeAttributeForEntityInheritance( addTypeAttribute )
               .build();
         final AspectModelParquetPayloadGenerator generator = new AspectModelParquetPayloadGenerator( context, config );
         for ( final ParquetArtifact artifact : generator.generate().toList() ) {
            try ( final OutputStream output = getOutputStreamForFile( context.getName() + ".parquet", outputDirectory ) ) {
               output.write( artifact.serialize() );
               output.flush();
            } catch ( final Throwable exception ) {
               throw new MojoExecutionException( "Could not write to output " + outputDirectory, exception );
            }
         }
      }
      LOG.info( "Successfully generated example Parquet payloads for Aspect Models." );
   }
}
