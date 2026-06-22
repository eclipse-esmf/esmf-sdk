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

import org.eclipse.esmf.metamodel.datatype.Curie;
import org.eclipse.esmf.metamodel.datatype.CurieType;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class CurieSerializer extends ValueSerializer<Curie> {
   public static final CurieSerializer INSTANCE = new CurieSerializer();

   @Override
   public void serialize( final Curie value, final JsonGenerator generator, final SerializationContext context ) throws JacksonException {
      generator.writeString( CurieType.INSTANCE.serialize( value ) );
   }
}
