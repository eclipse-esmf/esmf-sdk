/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.aspectmodel.java;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.generator.Artifact;
import io.openmanufacturing.sds.aspectmodel.generator.Generator;
import io.openmanufacturing.sds.metamodel.Aspect;

/**
 * Base class for all generators that want to create java sourcecode.
 */
public abstract class JavaGenerator extends Generator<QualifiedName, String> {
   private static final Logger LOG = LoggerFactory.getLogger( JavaGenerator.class );
   protected final JavaCodeGenerationConfig config;

   public JavaGenerator( final Aspect aspect, final JavaCodeGenerationConfig config ) {
      super( aspect );
      this.config = config;
   }

   @Override
   public void write( final Artifact<QualifiedName, String> artifact,
         final Function<QualifiedName, OutputStream> nameMapper ) {
      final QualifiedName qualifiedName = artifact.getId();
      final OutputStream outputStream = nameMapper.apply( qualifiedName );
      final String content = artifact.getContent();

      try {
         try ( final Writer writer = new OutputStreamWriter( outputStream, StandardCharsets.UTF_8 ) ) {
            for ( int i = 0; i < content.length(); i++ ) {
               writer.write( content.charAt( i ) );
            }
            writer.flush();
         }
      } catch ( final IOException e ) {
         LOG.error( "Failure during writing of generated code.", e );
      }
   }

   public JavaCodeGenerationConfig getConfig() {
      return config;
   }
}
