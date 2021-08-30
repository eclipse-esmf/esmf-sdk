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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.openmanufacturing.sds.metamodel.Property;

/**
 * Tracks necessary Java imports during type resolution so that code generators can apply this information in order
 * to avoid statically bulk-importing all classes one might need.
 *
 * Code generators should use {@link #getUsedImportsWithoutJavaLang()} to generate a final list of imports.
 */
public class ImportTracker {
   private static final String GENERICS_START = "<";

   private static final String TYPE_BRACKETS_AND_WHITESPACE = "[<>\\s]";

   private final Set<String> usedImports = new HashSet<>();
   private final Set<String> usedStaticImports = new HashSet<>();

   /**
    * Resolves and tracks data type of the given meta model property.
    *
    * @param metaProperty the meta model property to resolve the data type for
    * @return the fully qualified class name (potentially including type parameters) of the resolved data type
    */
   public String getPropertyType( final Property metaProperty ) {
      final String propertyType = AspectModelJavaUtil
            .determinePropertyType( metaProperty.getCharacteristic(), false, this );
      trackPotentiallyParameterizedType( propertyType );
      if ( metaProperty.isOptional() ) {
         return AspectModelJavaUtil.containerType( Optional.class, propertyType, Optional.empty() );
      }
      return propertyType;
   }

   /**
    * Extracts and tracks the container type without any type parameters.
    *
    * @param parameterizedContainerType the fully qualified class name including type parameters
    * @return the raw container type
    */
   public String getRawContainerType( final String parameterizedContainerType ) {
      trackPotentiallyParameterizedType( parameterizedContainerType );
      return parameterizedContainerType.substring( 0, parameterizedContainerType.indexOf( GENERICS_START ) );
   }

   private void trackPotentiallyParameterizedType( final String potentiallyParameterizedType ) {
      if ( potentiallyParameterizedType.contains( GENERICS_START ) ) {
         final String rawContainerType = potentiallyParameterizedType.substring( 0,
               potentiallyParameterizedType.indexOf( GENERICS_START ) );
         final List<String> typeParameters = Arrays.asList( potentiallyParameterizedType
               .substring( potentiallyParameterizedType.indexOf( GENERICS_START ) + 1 )
               .replaceAll( TYPE_BRACKETS_AND_WHITESPACE, "" )
               .split( "," ) );
         usedImports.add( rawContainerType );
         usedImports.addAll( typeParameters );
      } else {
         usedImports.add( potentiallyParameterizedType );
      }
   }

   /**
    * Explicitly adds an import.
    *
    * @param clazz the class to add to the imported classes
    */
   public void importExplicit( final Class<?> clazz ) {
      usedImports.add( clazz.getName() );
   }

   /**
    * Explicitly adds an import. If the Class object is known, use {@link #importExplicit(Class)} instead.
    *
    * @param clazz the fully qualified class name to add to the imported classes
    */
   public void importExplicit( final String clazz ) {
      usedImports.add( clazz );
   }

   /**
    * Explicitly adds a static import.
    *
    * @param clazz the class to add to the imported classes
    */
   public void importStaticExplicit( final Class<?> clazz ) {
      usedStaticImports.add( clazz.getName() );
   }

   /**
    * Returns all used imports EXCEPT for imports of other generated classes as those will reside in the same target
    * package.
    *
    * @return the used imports
    */
   public List<String> getUsedImports() {
      return usedImports.stream().filter( usedImport -> usedImport.contains( "." ) ).collect( Collectors.toList() );
   }

   /**
    * Returns all used imports EXCEPT for imports of other generated classes and those from {@code java.lang} as they
    * are implicitly imported and don't need to be added to the list of imports.
    *
    * The imports are returned in natural order.
    *
    * @return the used imports without classes from {@code java.lang}
    */
   public List<String> getUsedImportsWithoutJavaLang() {
      return getUsedImports().stream().filter( usedImport -> !usedImport.startsWith( "java.lang." ) ).sorted()
                             .collect( Collectors.toList() );
   }

   /**
    * Returns all used static imports.
    *
    * @return the used static imports
    */
   public List<String> getUsedStaticImports() {
      return usedStaticImports.stream().sorted().collect( Collectors.toList() );
   }
}
