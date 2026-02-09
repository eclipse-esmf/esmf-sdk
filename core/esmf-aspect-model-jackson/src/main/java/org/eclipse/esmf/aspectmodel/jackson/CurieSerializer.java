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

import org.eclipse.esmf.metamodel.datatype.Curie;
import org.eclipse.esmf.metamodel.datatype.CurieType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CurieSerializer extends StdSerializer<Curie> {
   @Serial
   private static final long serialVersionUID = -6772194217411185019L;
   public static final CurieSerializer INSTANCE = new CurieSerializer();

   private CurieSerializer() {
      super( Curie.class );
   }

   @Override
   public void serialize( final Curie value, final JsonGenerator generator, final SerializerProvider provider ) throws IOException {
      generator.writeString( CurieType.INSTANCE.serialize( value ) );
   }
}
