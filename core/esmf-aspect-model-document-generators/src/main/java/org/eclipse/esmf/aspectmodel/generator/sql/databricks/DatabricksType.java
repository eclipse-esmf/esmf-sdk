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

import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlType;

import org.jspecify.annotations.NonNull;

/**
 * Databricks SQL types.
 */
public sealed interface DatabricksType extends SqlType {
   DatabricksType BIGINT = new SimpleDatabricksType( "BIGINT" );
   DatabricksType BINARY = new SimpleDatabricksType( "BINARY" );
   DatabricksType BOOLEAN = new SimpleDatabricksType( "BOOLEAN" );
   DatabricksType DATE = new SimpleDatabricksType( "DATE" );
   DatabricksType DOUBLE = new SimpleDatabricksType( "DOUBLE" );
   DatabricksType FLOAT = new SimpleDatabricksType( "FLOAT" );
   DatabricksType INT = new SimpleDatabricksType( "INT" );
   DatabricksType SMALLINT = new SimpleDatabricksType( "SMALLINT" );
   DatabricksType STRING = new SimpleDatabricksType( "STRING" );
   DatabricksType TIMESTAMP = new SimpleDatabricksType( "TIMESTAMP" );
   DatabricksType TIMESTAMP_NTZ = new SimpleDatabricksType( "TIMESTAMP_NTZ" );
   DatabricksType TINYINT = new SimpleDatabricksType( "TINYINT" );

   record DatabricksDecimal(
         Optional<Integer> precision,
         Optional<Integer> scale
   ) implements DatabricksType {
      public DatabricksDecimal( final Optional<Integer> precision ) {
         this( precision, Optional.empty() );
      }

      public DatabricksDecimal() {
         this( Optional.empty(), Optional.empty() );
      }

      @Override
      public @NonNull String toString() {
         return "DECIMAL" + precision().map(
               precision -> scale().map(
                     scale -> "(" + precision + "," + scale + ")" ).orElse( "(" + precision + ")" ) )
               .orElse( "" );
      }
   }

   record DatabricksArray(
         DatabricksType elementType
   ) implements DatabricksType {
      @Override
      public @NonNull String toString() {
         return "ARRAY<" + elementType() + ">";
      }
   }

   record DatabricksMap(
         DatabricksType keyType,
         DatabricksType valueType
   ) implements DatabricksType {
      public DatabricksMap {
         if ( keyType instanceof DatabricksMap ) {
            throw new DocumentGenerationException( "Key type cannot be MAP" );
         }
      }

      @Override
      public @NonNull String toString() {
         return "MAP<" + keyType() + "," + valueType() + ">";
      }
   }

   record DatabricksStructEntry(
         String name, DatabricksType type, boolean nullable, Optional<String> comment
   ) {
      @Override
      public @NonNull String toString() {
         return name + ": " + type + ( nullable ? "" : " NOT NULL" )
               + comment.map( theComment -> " " + new DatabricksCommentDefinition( theComment ) ).orElse( "" );
      }
   }

   record DatabricksStruct(
         List<DatabricksStructEntry> entries
   ) implements DatabricksType {
      @Override
      public @NonNull String toString() {
         return "STRUCT<" + String.join( ", ", entries.stream().map( DatabricksStructEntry::toString ).toList() ) + ">";
      }
   }

   record SimpleDatabricksType(
         String name
   ) implements DatabricksType {
      @Override
      public @NonNull String toString() {
         return name;
      }
   }
}
