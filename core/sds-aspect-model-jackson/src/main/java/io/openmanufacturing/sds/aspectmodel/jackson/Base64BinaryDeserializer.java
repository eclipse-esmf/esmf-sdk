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
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import io.openmanufacturing.sds.aspectmodel.resolver.services.ExtendedXsdDataType;

public class Base64BinaryDeserializer extends StdDeserializer<byte[]> {
   private static final long serialVersionUID = 6637893688907290484L;
   public static final Base64BinaryDeserializer INSTANCE = new Base64BinaryDeserializer();

   private Base64BinaryDeserializer() {
      super( byte[].class );
   }

   @Override
   public byte[] deserialize( final JsonParser parser, final DeserializationContext context ) throws IOException {
      if ( parser.getCurrentTokenId() == JsonTokenId.ID_STRING ) {
         final Optional<byte[]> value = ExtendedXsdDataType.BASE64_BINARY.parseTyped( parser.getText() );
         if ( value.isPresent() ) {
            return value.get();
         }
      }

      return (byte[]) context.handleUnexpectedToken( byte[].class, parser );
   }
}
