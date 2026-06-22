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

package org.eclipse.esmf.aspectmodel.jackson;

import java.util.Locale;

import org.eclipse.esmf.metamodel.datatype.LangString;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class LangStringDeserializer extends ValueDeserializer<LangString> {
   public static final LangStringDeserializer INSTANCE = new LangStringDeserializer();

   @Override
   public LangString deserialize( final JsonParser parser, final DeserializationContext context ) throws JacksonException {
      final JsonNode node = parser.readValueAsTree();
      final String languageTag = node.properties().iterator().next().getKey();
      final Locale locale = Locale.forLanguageTag( languageTag );
      final String text = node.get( languageTag ).asString();
      return new LangString( text, locale );
   }
}
