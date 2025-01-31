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

package org.eclipse.esmf.test;

import java.util.Locale;
import java.util.function.Predicate;

import org.eclipse.esmf.metamodel.HasDescription;
import org.eclipse.esmf.metamodel.datatype.LangString;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ListAssert;

/**
 * Base class for {@link HasDescription} asserts
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public abstract class AbstractHasDescriptionAssert<SELF extends AbstractHasDescriptionAssert<SELF, ACTUAL>, ACTUAL extends HasDescription>
      extends AbstractAssert<SELF, ACTUAL> {
   protected AbstractHasDescriptionAssert( final ACTUAL actual, final Class<?> selfType ) {
      super( actual, selfType );
   }

   public SELF hasPreferredName( final LangString langString ) {
      return hasPreferredNameMatching( langString::equals );
   }

   public SELF hasPreferredName( final String text, final Locale languageTag ) {
      return hasPreferredName( new LangString( text, languageTag ) );
   }

   public SELF hasPreferredName( final String text ) {
      return hasPreferredName( text, Locale.ENGLISH );
   }

   public SELF hasPreferredNameMatching( final Predicate<LangString> predicate ) {
      preferredNames().anyMatch( predicate );
      return myself;
   }

   public SELF hasDescription( final LangString langString ) {
      return hasDescriptionMatching( langString::equals );
   }

   public SELF hasDescription( final String text, final Locale languageTag ) {
      return hasDescription( new LangString( text, languageTag ) );
   }

   public SELF hasDescription( final String text ) {
      return hasDescription( text, Locale.ENGLISH );
   }

   public SELF hasDescriptionMatching( final Predicate<LangString> predicate ) {
      descriptions().anyMatch( predicate );
      return myself;
   }

   public SELF hasSee( final String seeReference ) {
      return hasSeeMatching( seeReference::equals );
   }

   public SELF hasSeeMatching( final Predicate<String> predicate ) {
      see().anyMatch( predicate );
      return myself;
   }

   public ListAssert<LangString> descriptions() {
      return new ListAssert<>( actual.getDescriptions().stream().toList() );
   }

   public ListAssert<LangString> preferredNames() {
      return new ListAssert<>( actual.getPreferredNames().stream().toList() );
   }

   public ListAssert<String> see() {
      return new ListAssert<>( actual.getSee() );
   }
}
