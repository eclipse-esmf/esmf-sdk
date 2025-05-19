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

/**
 * Databricks SQL types.
 */
public sealed interface DatabricksType {
   DatabricksType BIGINT = new DatabricksBigint();
   DatabricksType BINARY = new DatabricksBinary();
   DatabricksType BOOLEAN = new DatabricksBoolean();
   DatabricksType DATE = new DatabricksDate();
   DatabricksType DOUBLE = new DatabricksDouble();
   DatabricksType FLOAT = new DatabricksFloat();
   DatabricksType INT = new DatabricksInt();
   DatabricksType SMALLINT = new DatabricksSmallint();
   DatabricksType STRING = new DatabricksString();
   DatabricksType TIMESTAMP = new DatabricksTimestamp();
   DatabricksType TIMESTAMP_NTZ = new DatabricksTimestampNtz();
   DatabricksType TINYINT = new DatabricksTinyint();

   record DatabricksDecimal( Optional<Integer> precision, Optional<Integer> scale ) implements DatabricksType {
      public DatabricksDecimal( final Optional<Integer> precision ) {
         this( precision, Optional.empty() );
      }

      public DatabricksDecimal() {
         this( Optional.empty(), Optional.empty() );
      }

      @Override
      public String toString() {
         if ( precision.isPresent() ) {
            if ( scale().isPresent() ) {
               //noinspection OptionalGetWithoutIsPresent
               return "DECIMAL(" + precision().get() + "," + scale().get() + ")";
            }
            //noinspection OptionalGetWithoutIsPresent
            return "DECIMAL(" + precision().get() + ")";
         }
         return "DECIMAL";
      }
   }

   record DatabricksArray( DatabricksType elementType ) implements DatabricksType {
      @Override
      public String toString() {
         return "ARRAY<" + elementType() + ">";
      }
   }

   record DatabricksMap( DatabricksType keyType, DatabricksType valueType ) implements DatabricksType {
      public DatabricksMap {
         if ( keyType instanceof DatabricksMap ) {
            throw new RuntimeException( "Key type cannot be MAP" );
         }
      }

      @Override
      public String toString() {
         return "MAP<" + keyType() + "," + valueType() + ">";
      }
   }

   record DatabricksStructEntry( String name, DatabricksType type, boolean nullable, Optional<String> comment ) {
      @Override
      public String toString() {
         return name + ": " + type + ( nullable ? "" : " NOT NULL" )
               + comment.map( theComment -> " " + new DatabricksCommentDefinition( theComment ) ).orElse( "" );
      }
   }

   record DatabricksStruct( List<DatabricksStructEntry> entries ) implements DatabricksType {
      @Override
      public String toString() {
         return "STRUCT<" + String.join( ", ", entries.stream().map( DatabricksStructEntry::toString ).toList() ) + ">";
      }
   }

   final class DatabricksBigint implements DatabricksType {
      @Override
      public String toString() {
         return "BIGINT";
      }
   }

   final class DatabricksBinary implements DatabricksType {
      @Override
      public String toString() {
         return "BINARY";
      }
   }

   final class DatabricksBoolean implements DatabricksType {
      @Override
      public String toString() {
         return "BOOLEAN";
      }
   }

   final class DatabricksDate implements DatabricksType {
      @Override
      public String toString() {
         return "DATE";
      }
   }

   final class DatabricksDouble implements DatabricksType {
      @Override
      public String toString() {
         return "DOUBLE";
      }
   }

   final class DatabricksFloat implements DatabricksType {
      @Override
      public String toString() {
         return "FLOAT";
      }
   }

   final class DatabricksInt implements DatabricksType {
      @Override
      public String toString() {
         return "INT";
      }
   }

   final class DatabricksSmallint implements DatabricksType {
      @Override
      public String toString() {
         return "SMALLINT";
      }
   }

   final class DatabricksString implements DatabricksType {
      @Override
      public String toString() {
         return "STRING";
      }
   }

   final class DatabricksTimestamp implements DatabricksType {
      @Override
      public String toString() {
         return "TIMESTAMP";
      }
   }

   final class DatabricksTimestampNtz implements DatabricksType {
      @Override
      public String toString() {
         return "TIMESTAMP_NTZ";
      }
   }

   final class DatabricksTinyint implements DatabricksType {
      @Override
      public String toString() {
         return "TINYINT";
      }
   }
}
