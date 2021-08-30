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

package io.openmanufacturing.sds.staticmetamodel;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Base interface for each Aspect Meta Model class.
 */
public interface StaticMetaClass<T> {

   /**
    * @return the Model {@link Class} for this Meta Model class
    */
   Class<T> getModelClass();

   /**
    * @return the URN which identifies an Aspect Model element represented by this class.
    */
   AspectModelUrn getAspectModelUrn();

   /**
    * @return the version of the Aspect Meta Model on which the Aspect Model is based.
    */
   KnownVersion getMetaModelVersion();

   /**
    * @return the name of the Aspect Model element represented by this class.
    */
   String getName();

   /**
    * @return a {@link List} of links to an external taxonomy/ontology.
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
