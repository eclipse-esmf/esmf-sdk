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
import java.io.Serial;
import java.util.Optional;

import org.eclipse.esmf.metamodel.datatype.SammType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class HexBinaryDeserializer extends StdDeserializer<byte[]> {
   @Serial
   private static final long serialVersionUID = 8408540591452927413L;
   public static final HexBinaryDeserializer INSTANCE = new HexBinaryDeserializer();

   private HexBinaryDeserializer() {
      super( byte[].class );
   }

   @Override
   public byte[] deserialize( final JsonParser parser, final DeserializationContext context ) throws IOException {
      if ( parser.currentTokenId() == JsonTokenId.ID_STRING ) {
         final Optional<byte[]> value = SammType.HEX_BINARY.parseTyped( parser.getText() );
         if ( value.isPresent() ) {
            return value.get();
         }
      }

      return (byte[]) context.handleUnexpectedToken( byte[].class, parser );
   }
}
