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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class ImportTracker {

   private static final String GENERICS_START = "<";
   private static final String ARRAY = "[]";
   private static final String EMPTY_STRING = "";
   private static final String COMMA_STRING = ",";
   private static final String TYPE_BRACKETS_AND_WHITESPACE = "[<>\\s]";
   private static final String DEFAULT_PATH = "./";

   private final Map<String, QualifiedName> usedImports;
   private final String defaultDependencyPath;

   public ImportTracker() {
      usedImports = new HashMap<>();
      this.defaultDependencyPath = DEFAULT_PATH;
   }

   public ImportTracker( String defaultDependencyPath ) {
      usedImports = new HashMap<>();
      this.defaultDependencyPath = Optional.ofNullable( defaultDependencyPath )
            .filter( StringUtils::isNotBlank )
            .map( defaultPath -> defaultPath.endsWith( "/" ) ? defaultPath : defaultPath + "/" )
            .orElse( DEFAULT_PATH );
   }



   public void importExplicit( final String fileName, final String path ) {
      if ( !usedImports.containsKey( fileName ) ) {
         usedImports.putIfAbsent( fileName, new QualifiedName( fileName, path ) );
      }
   }

   public void importLibExplicit( final String fileName, final String path ) {
      if ( !usedImports.containsKey( fileName ) ) {
         usedImports.putIfAbsent( fileName, new QualifiedName( fileName, defaultDependencyPath + path ) );
      }
   }

   public void trackPotentiallyParameterizedType( final String potentiallyParameterizedType ) {
      if ( potentiallyParameterizedType.contains( GENERICS_START ) ) {
         Arrays.stream( potentiallyParameterizedType.split( GENERICS_START ) )
               .flatMap( substring -> Arrays.stream( substring.split( COMMA_STRING ) ) )
               .map( substring -> substring.replaceAll( TYPE_BRACKETS_AND_WHITESPACE, EMPTY_STRING ) )
               .filter( type -> !TsDataTypeMapping.isTsType( type ) )
               .filter( type -> !usedImports.containsKey( type ) )
               .forEach( type -> usedImports.putIfAbsent( type,
                     new QualifiedName( type, DEFAULT_PATH + type ) ) );
      } else if ( potentiallyParameterizedType.contains( ARRAY ) ) {
         final String type = potentiallyParameterizedType.replace( ARRAY, EMPTY_STRING );
         if ( !TsDataTypeMapping.isTsType( type ) && !usedImports.containsKey( type ) ) {
            usedImports.putIfAbsent( type, new QualifiedName( type, DEFAULT_PATH + type ) );
         }
      } else {
         if ( !TsDataTypeMapping.isTsType( potentiallyParameterizedType ) && !usedImports.containsKey( potentiallyParameterizedType ) ) {
            usedImports.putIfAbsent( potentiallyParameterizedType,
                  new QualifiedName( potentiallyParameterizedType, DEFAULT_PATH + potentiallyParameterizedType ) );
         }
      }
   }

   public List<String> getUsedTsLineImports() {
      return usedImports.values().stream()
            .collect( Collectors.groupingBy( QualifiedName::modulePath ) )
            .entrySet().stream()
            .map( entry -> {
               String modulePath = entry.getKey();
               String imports = entry.getValue().stream()
                     .map( QualifiedName::fileName )
                     .sorted()
                     .collect( Collectors.joining( "," ) );
               return "import { " + imports + ",} from '" + modulePath + "'";
            } )
            .sorted()
            .toList();
   }

   /**
    * Extracts and tracks the container type without any type parameters.
    *
    * @param parameterizedContainerType the fully qualified class name including type parameters
    * @return the raw container type
    */
   public String getRawContainerType( final String parameterizedContainerType ) {
      trackPotentiallyParameterizedType( parameterizedContainerType );
      if ( parameterizedContainerType.contains( ARRAY ) ) {
         return parameterizedContainerType.replace( ARRAY, EMPTY_STRING );
      }
      return parameterizedContainerType;
   }

   public void clear() {
      usedImports.clear();
   }
}
