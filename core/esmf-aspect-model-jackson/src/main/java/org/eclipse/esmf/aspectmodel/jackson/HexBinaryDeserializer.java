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

import java.util.Optional;

import org.eclipse.esmf.metamodel.datatype.SammType;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonTokenId;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class HexBinaryDeserializer extends ValueDeserializer<byte[]> {
   public static final HexBinaryDeserializer INSTANCE = new HexBinaryDeserializer();

   @Override
   public byte[] deserialize( final JsonParser parser, final DeserializationContext context ) {
      if ( parser.currentTokenId() == JsonTokenId.ID_STRING ) {
         final Optional<byte[]> value = SammType.HEX_BINARY.parseTyped( parser.getString() );
         if ( value.isPresent() ) {
            return value.get();
         }
      }

      return (byte[]) context.handleUnexpectedToken( byte[].class, parser );
   }
}
