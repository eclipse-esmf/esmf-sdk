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

package org.eclipse.esmf.buildtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomizeResourceConfig {
   private static final List<Predicate<JsonNode>> INCLUDES_TO_DELETE = List.of(
         // This include is deleted because it leads to "Class path contains multiple SLF4J bindings" warnings
         includeNode -> includeNode.asText().contains( "org/slf4j/impl/StaticLoggerBinder.class" )
   );

   public static void main( final String[] args ) throws IOException {
      final File file = new File( args[0] );
      if ( !file.exists() ) {
         System.err.println( "Warning: Native resource config " + file + " not found, skipping customizing" );
         System.exit( 0 );
      }
      final String content = Files.readString( file.toPath() );
      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode root = mapper.readTree( content );
      final JsonNode includes = root.get( "resources" ).get( "includes" );
      for ( final Iterator<JsonNode> i = includes.elements(); i.hasNext(); ) {
         final JsonNode include = i.next();
         for ( final Predicate<JsonNode> decideIfNodeShouldBeDeleted : INCLUDES_TO_DELETE ) {
            if ( decideIfNodeShouldBeDeleted.test( include.get( "pattern" ) ) ) {
               i.remove();
            }
         }
      }

      try ( final FileOutputStream out = new FileOutputStream( file ) ) {
         mapper.writerWithDefaultPrettyPrinter().writeValue( out, root );
      }
   }
}
