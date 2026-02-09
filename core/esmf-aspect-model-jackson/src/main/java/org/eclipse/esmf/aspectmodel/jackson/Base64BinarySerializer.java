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

import org.eclipse.esmf.metamodel.datatype.SammType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Base64BinarySerializer extends StdSerializer<byte[]> {
   private static final long serialVersionUID = 2028371627433200656L;
   public static final Base64BinarySerializer INSTANCE = new Base64BinarySerializer();

   private Base64BinarySerializer() {
      super( byte[].class );
   }

   @Override
   public void serialize( final byte[] value, final JsonGenerator generator, final SerializerProvider provider )
         throws IOException {
      generator.writeString( SammType.BASE64_BINARY.serialize( value ) );
   }
}
