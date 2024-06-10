/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.sql.databricks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;

import com.google.common.collect.ImmutableMap;

/**
 * Parses Databricks column definitions
 */
public class DatabricksColumnDefinitionParser implements Supplier<DatabricksColumnDefinition> {
   private final Map<String, DatabricksType> standardTypes = ImmutableMap.<String, DatabricksType> builder()
         .put( "BIGINT", DatabricksType.BIGINT )
         .put( "BINARY", DatabricksType.BINARY )
         .put( "BOOLEAN", DatabricksType.BOOLEAN )
         .put( "DATE", DatabricksType.DATE )
         .put( "DOUBLE", DatabricksType.DOUBLE )
         .put( "FLOAT", DatabricksType.FLOAT )
         .put( "INT", DatabricksType.INT )
         .put( "SMALLINT", DatabricksType.SMALLINT )
         .put( "STRING", DatabricksType.STRING )
         .put( "TIMESTAMP", DatabricksType.TIMESTAMP )
         .put( "TIMESTAMP_NTZ", DatabricksType.TIMESTAMP_NTZ )
         .put( "TINYINT", DatabricksType.TINYINT )
         .build();

   private int index = 0;
   private final String source;

   public DatabricksColumnDefinitionParser( final String columnDefinition ) {
      source = columnDefinition;
   }

   private void eatChars( final String chars ) {
      while ( index < source.length() && chars.indexOf( source.charAt( index ) ) != -1 ) {
         index++;
      }
   }

   private void eatSpace() {
      eatChars( " " );
   }

   private String readToken() {
      return readToken( " >" );
   }

   private boolean consumeOptionalToken( final String token ) {
      final int oldIndex = index;
      eatSpace();
      if ( readToken( " ,>" ).equals( token ) ) {
         return true;
      }
      index = oldIndex;
      return false;
   }

   private String readToken( final String delim, final boolean keepSpace ) {
      if ( !keepSpace ) {
         eatSpace();
      }
      final int startIndex = index;
      while ( index < source.length() && delim.indexOf( source.charAt( index ) ) == -1 ) {
         if ( source.charAt( index ) == '\\' ) {
            index++;
         }
         index++;
      }
      return source.substring( startIndex, index );
   }

   private String readToken( final String delim ) {
      return readToken( delim, false );
   }

   private String parseColumnName() {
      return readToken( " :" );
   }

   private void expect( final char c ) {
      if ( currentCharacterIs( c ) ) {
         index++;
         return;
      }
      throw new DocumentGenerationException( "Did not find expected token '" + c + "'" );
   }

   private boolean currentCharacterIs( final char c ) {
      return index < source.length() && source.charAt( index ) == c;
   }

   private List<DatabricksType.DatabricksStructEntry> parseStructEntries() {
      final List<DatabricksType.DatabricksStructEntry> entries = new ArrayList<>();
      do {
         eatChars( "," );
         final String name = parseColumnName();
         eatChars( " :" );
         final DatabricksType columnType = parseType();
         final boolean isNotNullable = parseNullable();
         final Optional<String> comment = parseComment();
         entries.add( new DatabricksType.DatabricksStructEntry( name, columnType, !isNotNullable, comment ) );
      } while ( currentCharacterIs( ',' ) );
      return entries;
   }

   private DatabricksType parseType() {
      final String typeName = readToken( " <>()," );
      for ( final Map.Entry<String, DatabricksType> entry : standardTypes.entrySet() ) {
         if ( typeName.equals( entry.getKey() ) ) {
            return entry.getValue();
         }
      }

      if ( "ARRAY".equals( typeName ) ) {
         expect( '<' );
         final DatabricksType nestedType = parseType();
         expect( '>' );
         return new DatabricksType.DatabricksArray( nestedType );
      } else if ( "STRUCT".equals( typeName ) ) {
         expect( '<' );
         final List<DatabricksType.DatabricksStructEntry> entries = parseStructEntries();
         expect( '>' );
         return new DatabricksType.DatabricksStruct( entries );
      } else if ( typeName.startsWith( "DECIMAL" ) ) {
         final Optional<Integer> precision;
         final Optional<Integer> scale;
         if ( currentCharacterIs( '(' ) ) {
            expect( '(' );
            precision = Optional.of( Integer.parseInt( readToken( ",)" ) ) );
            if ( currentCharacterIs( ',' ) ) {
               expect( ',' );
               scale = Optional.of( Integer.parseInt( readToken( ")" ) ) );
            } else {
               scale = Optional.empty();
            }
            expect( ')' );
         } else {
            precision = Optional.empty();
            scale = Optional.empty();
         }
         return new DatabricksType.DatabricksDecimal( precision, scale );
      } else if ( "MAP".equals( typeName ) ) {
         expect( '<' );
         final DatabricksType keyType = parseType();
         expect( ',' );
         final DatabricksType valueType = parseType();
         expect( '>' );
         return new DatabricksType.DatabricksMap( keyType, valueType );
      }
      throw new DocumentGenerationException( "Could not parse databricks type" );
   }

   private boolean parseNullable() {
      return consumeOptionalToken( "NOT" ) && consumeOptionalToken( "NULL" );
   }

   private Optional<String> parseComment() {
      final int oldIndex = index;
      if ( "COMMENT".equals( readToken() ) ) {
         eatSpace();
         expect( '\'' );
         final String comment = readToken( "'", true ).replaceAll( "\\\\'", "'" );
         expect( '\'' );
         return Optional.of( comment );
      } else {
         index = oldIndex;
      }
      return Optional.empty();
   }

   @Override
   public DatabricksColumnDefinition get() {
      try {
         final String columnName = parseColumnName();
         final DatabricksType columnType = parseType();
         final boolean isNotNull = parseNullable();
         final Optional<String> comment = parseComment();
         return DatabricksColumnDefinitionBuilder.builder()
               .name( columnName )
               .type( columnType )
               .nullable( !isNotNull )
               .comment( comment )
               .build();
      } catch ( final DocumentGenerationException exception ) {
         throw exception;
      } catch ( final Exception exception ) {
         throw new DocumentGenerationException( "Could not parse column definition: " + source );
      }
   }
}

