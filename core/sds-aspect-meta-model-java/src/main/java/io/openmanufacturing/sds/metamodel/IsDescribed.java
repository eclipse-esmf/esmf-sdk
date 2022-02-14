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

package io.openmanufacturing.sds.metamodel;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.datatypes.LangString;

/**
 * Represents model elements that have human-readable names and descriptions
 */
public interface IsDescribed {
   /**
    * @return the URN which identifies an Aspect Model element.
    */
   Optional<AspectModelUrn> getAspectModelUrn();

   /**
    * @return the name of the Aspect Model element.
    */
   String getName();

   /**
    * Determines whether this model element has a generated name
    *
    * @return true if the name is synthetic (generated at load time), false if it is given in the model
    */
   default boolean hasSyntheticName() {
      return false;
   }

   /**
    * @return a {@link java.util.List} of links to an external taxonomy/ontology.
    */
   default List<String> getSee() {
      return Collections.emptyList();
   }

   /**
    * @return a {@link Map} containing language specific names for the Aspect Model element.
    */
   default Set<LangString> getPreferredNames() {
      return Collections.emptySet();
   }

   /**
    * @return a {@link Map} containing language specific descriptions for the Aspect Model element.
    */
   default Set<LangString> getDescriptions() {
      return Collections.emptySet();
   }

   /**
    * @return the preferred name for the Aspect Model element for a specific language if present, the element's name otherwise.
    */
   default String getPreferredName( final Locale locale ) {
      return getPreferredNames().stream()
            .filter( preferredName -> preferredName.getLanguageTag().equals( locale ) )
            .findAny()
            .map( LangString::getValue )
            .orElse( getName() );
   }

   /**
    * @return the description for the Aspect Model element for a specific language if present, an empty string otherwise.
    */
   default String getDescription( final Locale locale ) {
      return getDescriptions().stream()
            .filter( description -> description.getLanguageTag().equals( locale ) )
            .findAny()
            .map( LangString::getValue )
            .orElse( "" );
   }
}
