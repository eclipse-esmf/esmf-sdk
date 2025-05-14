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

package org.eclipse.esmf.metamodel;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.metamodel.datatype.LangString;

public interface HasDescription {
   String getName();

   /**
    * @return a {@link java.util.List} of links to an external taxonomy/ontology.
    */
   default List<String> getSee() {
      return Collections.emptyList();
   }

   /**
    * @return a {@link Set} containing language specific names for the Aspect Model element.
    */
   default Set<LangString> getPreferredNames() {
      return Collections.emptySet();
   }

   /**
    * @return a {@link Set} containing language specific descriptions for the Aspect Model element.
    */
   default Set<LangString> getDescriptions() {
      return Collections.emptySet();
   }

   /**
    * A language specific name for the Element. There may be multiple preferred names.
    *
    * @param locale of the specific text
    * @return the language specific text.
    */
   default String getPreferredName( final Locale locale ) {
      return getPreferredNames().stream()
            .filter( preferredName -> preferredName.getLanguageTag().equals( locale ) )
            .map( LangString::getValue )
            .findAny()
            .orElse( getName() );
   }

   /**
    * Gets the description for the Aspect Model element for a specific language, if the language is present.
    * If the language is not present, the description in English is returned. If there is also not description
    * in English, returns null.
    *
    * @param locale of the specific text
    * @return the language specific text or null
    */
   default String getDescription( final Locale locale ) {
      return getDescriptions().stream()
            .filter( description -> description.getLanguageTag().equals( locale ) )
            .map( LangString::getValue )
            .findAny()
            .orElseGet( () -> {
               if ( locale.equals( Locale.ENGLISH ) ) {
                  return null;
               }
               return getDescription( Locale.ENGLISH );
            } );
   }

   default Set<String> getDescriptions( final Locale locale ) {
      return getDescriptions().stream()
            .filter( description -> description.getLanguageTag().equals( locale ) )
            .map( LangString::getValue )
            .collect( Collectors.toSet() );
   }
}
