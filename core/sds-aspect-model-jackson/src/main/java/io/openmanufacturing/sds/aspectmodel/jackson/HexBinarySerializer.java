/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.openmanufacturing.sds.aspectmodel.resolver.services.ExtendedXsdDataType;

public class HexBinarySerializer extends StdSerializer<byte[]> {
   private static final long serialVersionUID = 6817718561520140283L;
   public static final HexBinarySerializer INSTANCE = new HexBinarySerializer();

   private HexBinarySerializer() {
      super( byte[].class );
   }

   @Override
   public void serialize( final byte[] value, final JsonGenerator generator, final SerializerProvider provider )
         throws IOException {
      generator.writeString( ExtendedXsdDataType.HEX_BINARY.unparseTyped( value ) );
   }
}
