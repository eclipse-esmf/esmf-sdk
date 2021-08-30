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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;

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
   default Map<Locale, String> getPreferredNames() {
      return Collections.emptyMap();
   }

   /**
    * @return a {@link Map} containing language specific descriptions for the Aspect Model element.
    */
   default Map<Locale, String> getDescriptions() {
      return Collections.emptyMap();
   }

   /**
    * @return the preferred name for the Aspect Model element for a specific language
    */
   default String getPreferredName( final Locale locale ) {
      return getPreferredNames().getOrDefault( locale, getName() );
   }

   /**
    * @return the description for the Aspect Model element for a specific language
    */
   default String getDescription( final Locale locale ) {
      final String defaultDescription = getDescriptions().get( Locale.ENGLISH );
      return getDescriptions().getOrDefault( locale, defaultDescription );
   }
}
