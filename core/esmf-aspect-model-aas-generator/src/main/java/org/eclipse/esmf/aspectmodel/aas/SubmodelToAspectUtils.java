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

package org.eclipse.esmf.aspectmodel.aas;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.datatype.LangString;

import com.google.common.base.CaseFormat;
import org.eclipse.digitaltwin.aas4j.v3.model.AasSubmodelElements;
import org.eclipse.digitaltwin.aas4j.v3.model.AbstractLangString;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.slf4j.Logger;

final class SubmodelToAspectUtils {
   private SubmodelToAspectUtils() {}

   static Set<LangString> langStringSet( final List<? extends AbstractLangString> stringList, final Logger logger ) {
      final Map<Locale, String> stringsByLanguage = new LinkedHashMap<>();
      stringList.forEach( abstractLangString -> {
         final Locale locale = Locale.forLanguageTag( abstractLangString.getLanguage() );
         final String existing = stringsByLanguage.putIfAbsent( locale, abstractLangString.getText() );
         if ( existing != null ) {
            logger.warn( "Duplicate language tag '{}' detected. Keeping first value '{}' and ignoring duplicate '{}'.",
                  abstractLangString.getLanguage(), existing, abstractLangString.getText() );
         }
      } );
      return stringsByLanguage.entrySet().stream()
            .map( entry -> new LangString( entry.getValue(), entry.getKey() ) )
            .collect( Collectors.toSet() );
   }

   static List<String> seeReferences( final List<Reference> supplementalSemanticIds, final Reference semanticId, final Logger logger ) {
      final Stream<Reference> supplemental = Optional.ofNullable( supplementalSemanticIds ).orElseGet( List::of ).stream();
      return Stream.concat( supplemental, Optional.ofNullable( semanticId ).stream() )
            .flatMap( id -> id.getKeys().stream() )
            .filter( key -> key.getType() == KeyTypes.CONCEPT_DESCRIPTION || key.getType() == KeyTypes.GLOBAL_REFERENCE
                  || key.getType() == KeyTypes.SUBMODEL )
            .map( Key::getValue )
            .flatMap( value -> validIrdiOrUri( value, logger ).stream() )
            .map( SubmodelToAspectUtils::sanitizeValue )
            .toList();
   }

   static Optional<String> validIrdiOrUri( final String input, final Logger logger ) {
      final Optional<String> irdi = Irdi.from( input ).map( i -> "urn:irdi:" + i );
      final Optional<URI> uri = Optional.of( input )
            .map( String::trim )
            .flatMap( inputUri -> {
               if ( inputUri.startsWith( "http:" ) || inputUri.startsWith( "https:" ) ) {
                  return Optional.of( inputUri );
               } else if ( inputUri.startsWith( "www." ) ) {
                  return Optional.of( "https://" + inputUri );
               }
               return Optional.empty();
            } )
            .flatMap( inputUri -> {
               try {
                  return Optional.of( URI.create( inputUri ) );
               } catch ( final IllegalArgumentException exception ) {
                  return Optional.empty();
               }
            } );

      final Optional<String> result = irdi.or( () -> uri.map( URI::toString ) );
      if ( result.isEmpty() ) {
         logger.warn( "Neither valid IRDI or valid URI: {}", input );
      }
      return result;
   }

   static AasSubmodelElements submodelElementType( final SubmodelElement element ) {
      Class<?> clazz = element.getClass();
      while ( clazz != null ) {
         for ( Class<?> iface : clazz.getInterfaces() ) {
            final AasSubmodelElements match = findEnumForClass( iface );
            if ( match != null ) {
               return match;
            }
         }
         final AasSubmodelElements match = findEnumForClass( clazz );
         if ( match != null ) {
            return match;
         }
         clazz = clazz.getSuperclass();
      }
      throw new AspectModelGenerationException(
            "Encountered unsupported SubmodelElement type " + element.getClass().getSimpleName() );
   }

   private static String sanitizeValue( final String value ) {
      return value.replace( "/ ", "/" );
   }

   private static AasSubmodelElements findEnumForClass( final Class<?> clazz ) {
      final String className = clazz.getSimpleName();
      return Arrays.stream( AasSubmodelElements.values() )
            .filter( entry -> {
               final String enumClassName = CaseFormat.UPPER_UNDERSCORE.to( CaseFormat.UPPER_CAMEL, entry.toString() );
               return className.equals( enumClassName );
            } )
            .findFirst()
            .orElse( null );
   }
}
