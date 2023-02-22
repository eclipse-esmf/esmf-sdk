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

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;

import org.eclipse.esmf.metamodel.datatypes.LangString;

public class LangStringDeserializer extends StdNodeBasedDeserializer<LangString> {
   private static final long serialVersionUID = 8007942189722606011L;
   public static final LangStringDeserializer INSTANCE = new LangStringDeserializer();

   private LangStringDeserializer() {
      super( LangString.class );
   }

   @Override
   public LangString convert( final JsonNode root, final DeserializationContext ctxt ) throws IOException {
      final String key = root.fieldNames().next();
      final Locale languageTag = Locale.forLanguageTag( key );
      return new LangString( root.get( key ).asText(), languageTag );
   }
}
