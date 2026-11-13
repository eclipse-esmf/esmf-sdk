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

package org.eclipse.esmf.aspectmodel;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.esmf.aspectmodel.generator.protobuf.AspectModelProtobufGenerator;
import org.eclipse.esmf.aspectmodel.generator.protobuf.ProtobufArtifact;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Generates a proto3 message schema for each configured Aspect Model.
 */
@Mojo( name = GenerateProtobufSchema.MAVEN_GOAL,
   defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateProtobufSchema extends AspectModelMojo {
   public static final String MAVEN_GOAL = "generateProtobufSchema";

   @Override
   public void executeGeneration() throws MojoExecutionException {
      validateParameters();

      for ( final Aspect aspect : loadAspects() ) {
         final ProtobufArtifact schema = new AspectModelProtobufGenerator( aspect ).singleResult();
         try ( final OutputStream out = getOutputStreamForFile( schema.getId(), outputDirectory ) ) {
            out.write( schema.serialize() );
         } catch ( final IOException exception ) {
            throw new MojoExecutionException( "Could not write Protobuf schema for " + aspect.getName(), exception );
         }
      }
      getLog().info( "Successfully generated Protobuf schemas for Aspect Models." );
   }
}
