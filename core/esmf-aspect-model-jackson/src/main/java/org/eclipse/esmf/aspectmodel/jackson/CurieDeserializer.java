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

import java.util.Optional;

import org.eclipse.esmf.metamodel.datatype.Curie;
import org.eclipse.esmf.metamodel.datatype.CurieType;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonTokenId;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class CurieDeserializer extends ValueDeserializer<Curie> {
   public static final CurieDeserializer INSTANCE = new CurieDeserializer();

   @Override
   public Curie deserialize( final JsonParser parser, final DeserializationContext context ) throws JacksonException {
      if ( parser.currentTokenId() == JsonTokenId.ID_STRING ) {
         final Optional<Curie> value = CurieType.INSTANCE.parseTyped( parser.getString() );
         if ( value.isPresent() ) {
            return value.get();
         }
      }

      return (Curie) context.handleUnexpectedToken( Curie.class, parser );
   }
}
