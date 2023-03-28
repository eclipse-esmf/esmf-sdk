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
package io.openmanufacturing.sds.aspectmodel.aas;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;

/**
 * Default implementation of multiple ways to map Aspect Model {@code LangString}s to AAS4J {@link LangString}s.
 */
public class LangStringMapper {
   public List<LangString> map( final Set<io.openmanufacturing.sds.metamodel.datatypes.LangString> localizedStrings ) {
      return localizedStrings.stream()
            .map( ( entry ) -> map( entry.getLanguageTag(), entry.getValue() ) )
            .collect( Collectors.toList() );
   }

   public LangString map( final io.openmanufacturing.sds.metamodel.datatypes.LangString langString ) {
      return map( langString.getLanguageTag(), langString.getValue() );
   }

   public LangString map( final Locale locale, final String value ) {
      return createLangString( value, locale.getLanguage() );
   }

   public LangString createLangString( final String text, final String locale ) {
      return new DefaultLangString.Builder().language( locale ).text( text ).build();
   }
}
