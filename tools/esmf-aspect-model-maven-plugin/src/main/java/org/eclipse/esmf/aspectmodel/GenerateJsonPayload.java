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

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadArtifact;
import org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = GenerateJsonPayload.MAVEN_GOAL,
   defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateJsonPayload extends AspectModelMojo {
   public static final String MAVEN_GOAL = "generateJsonPayload";
   private static final Logger LOG = LoggerFactory.getLogger( GenerateJsonPayload.class );

   @Parameter( defaultValue = "false" )
   private boolean addTypeAttribute;

   @Parameter( defaultValue = "false" )
   private boolean currentTimestamp;

   @Parameter( defaultValue = "false" )
   private boolean ignoreExampleValue;

   @Parameter( defaultValue = "false" )
   private boolean randomTimestamp;

   @Parameter
   private Integer count;

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      if ( count != null && count < 1 ) {
         throw new MojoExecutionException( "The value for count must be greater than zero." );
      }

      final Set<Aspect> aspects = loadAspects();
      for ( final Aspect context : aspects ) {
         final JsonPayloadGenerationConfig config = JsonPayloadGenerationConfigBuilder.builder()
               .addTypeAttributeForEntityInheritance( addTypeAttribute )
               .currentTimestamp( currentTimestamp )
               .ignoreExampleValue( ignoreExampleValue )
               .randomTimestamp( randomTimestamp )
               .build();
         final AspectModelJsonPayloadGenerator generator = new AspectModelJsonPayloadGenerator( context, config );
         if ( count == null ) {
            writePrettyPrintedPayload( context, generator );
            continue;
         }

         try ( final OutputStream output = getOutputStreamForFile( context.getName() + ".json", outputDirectory ) ) {
            for ( int i = 0; i < count; i++ ) {
               for ( final JsonPayloadArtifact artifact : generator.generate().toList() ) {
                  output.write( artifact.getContent().toString().getBytes( StandardCharsets.UTF_8 ) );
                  output.write( '\n' );
               }
            }
            output.flush();
         } catch ( final Throwable exception ) {
            throw new MojoExecutionException( "Could not write to output " + outputDirectory, exception );
         }
      }
      LOG.info( "Successfully generated example JSON payloads for Aspect Models." );
   }

   private void writePrettyPrintedPayload( final Aspect context, final AspectModelJsonPayloadGenerator generator )
         throws MojoExecutionException {
      for ( final JsonPayloadArtifact artifact : generator.generate().toList() ) {
         try ( final OutputStream output = getOutputStreamForFile( context.getName() + ".json", outputDirectory ) ) {
            output.write( artifact.serialize() );
            output.flush();
         } catch ( final Throwable exception ) {
            throw new MojoExecutionException( "Could not write to output " + outputDirectory, exception );
         }
      }
   }
}
