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

package org.eclipse.esmf.buildtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class GenerateParserTokenType extends BuildTimeTool {
   private static final String CLASS_NAME = "ParserTokenType";

   public void generate( final File nodeTypesJson, final Path srcGen ) throws IOException {
      final Path packagePath = srcGen.resolve( "main" ).resolve( "java" )
            .resolve( "org" ).resolve( "eclipse" ).resolve( "esmf" ).resolve( "treesitterturtle" );
      mkdir( packagePath );

      final ObjectMapper objectMapper = new ObjectMapper();
      final Set<String> tokens = getNodeTypes( objectMapper.readTree( nodeTypesJson ) )
            .map( String::toLowerCase )
            .collect( Collectors.toSet() );
      final String declarations = javaConstantsForTokens( tokens ).entrySet().stream()
            .sorted( Map.Entry.comparingByKey() )
            .map( entry -> "   public static final String %s = \"%s\";%n".formatted( entry.getKey(), entry.getValue() ) )
            .collect( Collectors.joining() );
      final String classContent = """
         package org.eclipse.esmf.treesitterturtle;

         import javax.annotation.processing.Generated;

         @Generated( "org.eclipse.esmf.buildtime.GenerateParserTokenType" )
         public class %s {
         %s
         }
         """.formatted( CLASS_NAME, declarations );
      write( packagePath.resolve( CLASS_NAME + ".java" ).toFile(), classContent );
   }

   private Map<String, String> javaConstantsForTokens( final Collection<String> tokens ) {
      return tokens.stream().collect( Collectors.toMap( token -> {
         if ( token.length() == 1 && !Character.isLetterOrDigit( token.charAt( 0 ) ) ) {
            return "SYMBOL_" + Character.getName( token.charAt( 0 ) )
                  .replace( '-', '_' )
                  .replace( ' ', '_' ).toUpperCase();
         }
         return switch ( token ) {
            case "\"\"" -> "SYMBOL_DOUBLE_QUOTE";
            case "\"\"\"" -> "SYMBOL_TRIPLE_QUOTE";
            case "''" -> "SYMBOL_DOUBLE_SINGLE_QUOTE";
            case "'''" -> "SYMBOL_TRIPLE_SINGLE_QUOTE";
            case "@base" -> "AT_BASE";
            case "@prefix" -> "AT_PREFIX";
            case "^^" -> "SYMBOL_DOUBLE_CARET";
            case "_:" -> "BLANK_NODE_PREFIX";
            default -> token.toUpperCase();
         };
      }, token -> switch ( token ) {
         case "\"" -> "\\\"";
         case "\"\"" -> "\\\"\\\"";
         case "\"\"\"" -> "\\\"\\\"\\\"";
         default -> token;
      } ) );
   }

   private Stream<String> getNodeTypes( final JsonNode node ) {
      return node.isArray()
            ? node.values().stream().flatMap( this::getNodeTypes )
            : Optional.ofNullable( node.get( "type" ) ).map( JsonNode::asString ).stream();
   }

   static void main( final String[] args ) {
      try {
         new GenerateParserTokenType().generate( new File( args[0] ), Path.of( args[1] ) );
      } catch ( final Exception exception ) {
         throw new BuildTimeException( exception );
      }
   }

}
