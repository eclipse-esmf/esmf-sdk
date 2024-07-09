/*
 * Copyright (c) 2021-2023 Robert Bosch Manufacturing Solutions GmbH
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

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.metamodel.datatype.LangString;

import org.eclipse.digitaltwin.aas4j.v3.model.AbstractLangString;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;

/**
 * Default implementation of multiple ways to map Aspect Model {@link LangString}s to AAS4J {@link LangStringTextType}s.
 */
public final class LangStringMapper {
   public static final Mapper<LangStringTextType> TEXT = ( text, locale ) ->
         new DefaultLangStringTextType.Builder().text( text ).language( locale ).build();
   public static final Mapper<LangStringNameType> NAME = ( text, locale ) ->
         new DefaultLangStringNameType.Builder().text( text ).language( locale ).build();
   public static final Mapper<LangStringShortNameTypeIec61360> SHORT_NAME = ( text, locale ) ->
         new DefaultLangStringShortNameTypeIec61360.Builder().text( text ).language( locale ).build();
   public static final Mapper<LangStringPreferredNameTypeIec61360> PREFERRED_NAME = ( text, locale ) ->
         new DefaultLangStringPreferredNameTypeIec61360.Builder().text( text ).language( locale ).build();
   public static final Mapper<LangStringDefinitionTypeIec61360> DEFINITION = ( text, locale ) ->
         new DefaultLangStringDefinitionTypeIec61360.Builder().text( text ).language( locale ).build();

   public interface Mapper<T extends AbstractLangString> {
      default List<T> map( final Set<LangString> langStrings ) {
         return langStrings.stream()
               .map( ( entry ) -> map( entry.getLanguageTag(), entry.getValue() ) )
               .collect( Collectors.toList() );
      }

      default T map( final LangString langString ) {
         return map( langString.getLanguageTag(), langString.getValue() );
      }

      default T map( final Locale locale, final String text ) {
         return createLangString( text, locale.getLanguage() );
      }

      T createLangString( String text, String locale );
   }
}
