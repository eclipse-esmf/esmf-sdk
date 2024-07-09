/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.staticmetamodel;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.samm.KnownVersion;

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
    * @return the preferred name for the Aspect Model element for a specific language
    */
   default String getPreferredName( final Locale locale ) {
      return getPreferredNames().stream()
            .filter( preferredName -> preferredName.getLanguageTag().equals( locale ) )
            .findAny()
            .map( LangString::getValue )
            .orElse( getName() );
   }

   /**
    * @return the description for the Aspect Model element for a specific language
    */
   default String getDescription( final Locale locale ) {
      return getDescriptions().stream()
            .filter( description -> description.getLanguageTag().equals( locale ) )
            .findAny()
            .map( LangString::getValue )
            .orElse( "" );
   }
}
