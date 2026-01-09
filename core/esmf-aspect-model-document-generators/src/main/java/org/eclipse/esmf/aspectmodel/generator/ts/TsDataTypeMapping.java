/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.ts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.metamodel.characteristic.Duration;

public class TsDataTypeMapping {

   private static final Map<Class<?>, TsType> MAPPING = new HashMap<>();

   public enum TsType {
      NUMBER( "number" ),
      BOOLEAN( "boolean" ),
      STRING( "string" ),
      DATE( "Date" ),
      ANY( "any" ),
      VOID( "void" ),
      ARRAY( "Array" );

      private final String typeName;

      TsType( String typeName ) {
         this.typeName = typeName;
      }

      public String getTypeName() {
         return typeName;
      }
   }

   static {
      MAPPING.put( Byte.class, TsType.NUMBER );
      MAPPING.put( byte.class, TsType.NUMBER );
      MAPPING.put( Short.class, TsType.NUMBER );
      MAPPING.put( short.class, TsType.NUMBER );
      MAPPING.put( Integer.class, TsType.NUMBER );
      MAPPING.put( int.class, TsType.NUMBER );
      MAPPING.put( Long.class, TsType.NUMBER );
      MAPPING.put( long.class, TsType.NUMBER );
      MAPPING.put( Float.class, TsType.NUMBER );
      MAPPING.put( float.class, TsType.NUMBER );
      MAPPING.put( Double.class, TsType.NUMBER );
      MAPPING.put( double.class, TsType.NUMBER );
      MAPPING.put( Number.class, TsType.NUMBER );

      MAPPING.put( Boolean.class, TsType.BOOLEAN );
      MAPPING.put( boolean.class, TsType.BOOLEAN );

      MAPPING.put( String.class, TsType.STRING );
      MAPPING.put( BigInteger.class, TsType.STRING );
      MAPPING.put( BigDecimal.class, TsType.STRING );
      MAPPING.put( Character.class, TsType.STRING );
      MAPPING.put( UUID.class, TsType.STRING );
      MAPPING.put( Calendar.class, TsType.STRING );
      MAPPING.put( Temporal.class, TsType.STRING );
      MAPPING.put( ZonedDateTime.class, TsType.STRING );
      MAPPING.put( XMLGregorianCalendar.class, TsType.STRING );
      MAPPING.put( Duration.class, TsType.STRING );

      MAPPING.put( Date.class, TsType.DATE );
      MAPPING.put( LocalDate.class, TsType.DATE );
      MAPPING.put( LocalDateTime.class, TsType.DATE );

      MAPPING.put( Object.class, TsType.ANY );

      MAPPING.put( void.class, TsType.VOID );
      MAPPING.put( Void.class, TsType.VOID );

      MAPPING.put( Collection.class, TsType.ARRAY );
      MAPPING.put( Iterable.class, TsType.ARRAY );
      MAPPING.put( List.class, TsType.ARRAY );
      MAPPING.put( Set.class, TsType.ARRAY );
   }

   public static String resolveType( Class<?> type ) {
      // direct match
      if ( MAPPING.containsKey( type ) ) {
         return MAPPING.get( type ).getTypeName();
      }
      // assignable match
      return MAPPING.keySet().stream()
            .filter( cls -> cls.isAssignableFrom( type ) )
            .findFirst()
            .map( cls -> MAPPING.get( cls ).getTypeName() )
            .orElseThrow( () -> new CodeGenerationException( "Can't resolve mapping for type %s".formatted( type.getName() ) ) );
   }

   public static boolean isTsType( String tsType ) {
      return Arrays.stream( TsType.values() ).anyMatch( type -> type.getTypeName().equals( tsType ) );
   }
}