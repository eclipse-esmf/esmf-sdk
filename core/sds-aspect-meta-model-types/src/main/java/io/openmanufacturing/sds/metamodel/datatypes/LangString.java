/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.metamodel.datatypes;

import java.util.Locale;
import java.util.Objects;

/**
 * Java representation of an <a href="https://www.w3.org/TR/rdf11-concepts/#section-Graph-Literal">rdf:langString</a>.
 */
public class LangString implements Comparable<LangString> {
   private final String value;
   private final Locale languageTag;

   public LangString( final String value, final Locale languageTag ) {
      this.value = value;
      this.languageTag = languageTag;
   }

   public String getValue() {
      return value;
   }

   public Locale getLanguageTag() {
      return languageTag;
   }

   @Override
   public int compareTo( final LangString other ) {
      return getLanguageTag().toLanguageTag().compareTo( other.getLanguageTag().toLanguageTag() );
   }

   @Override
   public String toString() {
      return "\"" + value + "\"@" + languageTag.toLanguageTag();
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final LangString that = (LangString) o;
      return Objects.equals( value, that.value ) && Objects.equals( languageTag, that.languageTag );
   }

   @Override
   public int hashCode() {
      return Objects.hash( value, languageTag );
   }
}
