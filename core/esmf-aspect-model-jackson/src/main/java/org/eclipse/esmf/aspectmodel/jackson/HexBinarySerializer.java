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

import org.eclipse.esmf.metamodel.datatype.SammType;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class HexBinarySerializer extends ValueSerializer<byte[]> {
   public static final HexBinarySerializer INSTANCE = new HexBinarySerializer();

   @Override
   public void serialize( final byte[] value, final JsonGenerator generator, final SerializationContext context ) throws JacksonException {
      generator.writeString( SammType.HEX_BINARY.serialize( value ) );
   }
}
