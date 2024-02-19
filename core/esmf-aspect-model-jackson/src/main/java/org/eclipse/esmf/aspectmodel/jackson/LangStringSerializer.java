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

import org.eclipse.esmf.metamodel.datatypes.LangString;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LangStringSerializer extends StdSerializer<LangString> {
   private static final long serialVersionUID = 963449916984100611L;
   public static final LangStringSerializer INSTANCE = new LangStringSerializer();

   private LangStringSerializer() {
      super( LangString.class );
   }

   @Override
   public void serialize( final LangString value, final JsonGenerator generator, final SerializerProvider provider ) throws IOException {
      generator.writeStartObject();
      generator.writeStringField( value.getLanguageTag().toLanguageTag(), value.getValue() );
      generator.writeEndObject();
   }
}
