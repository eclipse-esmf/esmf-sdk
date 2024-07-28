/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

import org.eclipse.esmf.metamodel.datatype.Curie;
import org.eclipse.esmf.metamodel.datatype.CurieType;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CurieDeserializer extends StdDeserializer<Curie> {
   @Serial
   private static final long serialVersionUID = 1285377288430239636L;
   public static final CurieDeserializer INSTANCE = new CurieDeserializer();

   private CurieDeserializer() {
      super( Curie.class );
   }

   @Override
   public Curie deserialize( final JsonParser parser, final DeserializationContext context ) throws IOException, JacksonException {
      if ( parser.currentTokenId() == JsonTokenId.ID_STRING ) {
         final Optional<Curie> value = CurieType.INSTANCE.parseTyped( parser.getText() );
         if ( value.isPresent() ) {
            return value.get();
         }
      }

      return (Curie) context.handleUnexpectedToken( Curie.class, parser );
   }
}
