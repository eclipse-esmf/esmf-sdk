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

package org.eclipse.esmf.aspectmodel.generator.sql.postgresql;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;

import com.google.common.collect.ImmutableMap;

/**
 * Parses PostgreSQL column definitions.
 */
public class PostgreSqlColumnDefinitionParser implements Supplier<PostgreSqlColumnDefinition> {
   private static final Pattern COMMENT = Pattern.compile( "(?s)\\s+COMMENT\\s+'((?:''|\\\\'|[^'])*)'\\s*$",
         Pattern.CASE_INSENSITIVE );
   private static final Pattern NOT_NULL = Pattern.compile( "(?i)\\s+NOT\\s+NULL\\s*$" );
   private static final Pattern NUMERIC = Pattern.compile( "(?i)NUMERIC(?:\\((\\d+)(?:\\s*,\\s*(\\d+))?\\))?" );
   private final Map<String, PostgreSqlType> standardTypes = ImmutableMap.<String, PostgreSqlType>builder()
         .put( "BIGINT", PostgreSqlType.BIGINT )
         .put( "BOOLEAN", PostgreSqlType.BOOLEAN )
         .put( "BYTEA", PostgreSqlType.BYTEA )
         .put( "DATE", PostgreSqlType.DATE )
         .put( "DOUBLE PRECISION", PostgreSqlType.DOUBLE_PRECISION )
         .put( "INTEGER", PostgreSqlType.INTEGER )
         .put( "REAL", PostgreSqlType.REAL )
         .put( "SMALLINT", PostgreSqlType.SMALLINT )
         .put( "TEXT", PostgreSqlType.TEXT )
         .put( "TIME", PostgreSqlType.TIME )
         .put( "TIMESTAMP", PostgreSqlType.TIMESTAMP )
         .put( "TIMESTAMP WITH TIME ZONE", PostgreSqlType.TIMESTAMP_WITH_TIME_ZONE )
         .build();
   private final String source;

   public PostgreSqlColumnDefinitionParser( final String columnDefinition ) {
      source = columnDefinition;
   }

   private PostgreSqlType parseType( final String sourceType ) {
      final String normalizedType = sourceType.strip().toUpperCase( Locale.ROOT );
      if ( normalizedType.endsWith( "[]" ) ) {
         return new PostgreSqlType.PostgreSqlArray( parseType( normalizedType.substring( 0, normalizedType.length() - 2 ) ) );
      }
      final PostgreSqlType standardType = standardTypes.get( normalizedType );
      if ( standardType != null ) {
         return standardType;
      }
      final Matcher numeric = NUMERIC.matcher( normalizedType );
      if ( numeric.matches() ) {
         final Optional<Integer> precision = Optional.ofNullable( numeric.group( 1 ) ).map( Integer::valueOf );
         final Optional<Integer> scale = Optional.ofNullable( numeric.group( 2 ) ).map( Integer::valueOf );
         return new PostgreSqlType.PostgreSqlNumeric( precision, scale );
      }
      throw new DocumentGenerationException( "Could not parse PostgreSQL type: " + sourceType );
   }

   @Override
   public PostgreSqlColumnDefinition get() {
      try {
         String definition = source.strip();
         final Matcher notNullMatcher = NOT_NULL.matcher( definition );
         final boolean nullable = !notNullMatcher.find();
         if ( !nullable ) {
            definition = definition.substring( 0, notNullMatcher.start() );
         }

         final int nameEnd = definition.indexOf( ' ' );
         if ( nameEnd <= 0 ) {
            throw new DocumentGenerationException( "Could not parse PostgreSQL column definition: " + source );
         }
         return PostgreSqlColumnDefinitionBuilder.builder()
               .name( definition.substring( 0, nameEnd ) )
               .type( parseType( definition.substring( nameEnd + 1 ) ) )
               .nullable( nullable )
               .build();
      } catch ( final DocumentGenerationException exception ) {
         throw exception;
      } catch ( final Exception exception ) {
         throw new DocumentGenerationException( "Could not parse PostgreSQL column definition: " + source );
      }
   }
}
