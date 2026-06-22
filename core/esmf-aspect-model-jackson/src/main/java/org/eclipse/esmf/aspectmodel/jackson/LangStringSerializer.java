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

import org.eclipse.esmf.metamodel.datatype.LangString;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class LangStringSerializer extends ValueSerializer<LangString> {
   public static final LangStringSerializer INSTANCE = new LangStringSerializer();

   @Override
   public void serialize( final LangString value, final JsonGenerator generator, final SerializationContext context )
         throws JacksonException {
      generator.writeStartObject();
      generator.writeStringProperty( value.getLanguageTag().toLanguageTag(), value.getValue() );
      generator.writeEndObject();
   }
}
