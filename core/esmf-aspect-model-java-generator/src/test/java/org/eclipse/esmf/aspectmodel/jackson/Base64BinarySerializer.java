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

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/*
 * Dummy Class - this exists only here to be present in the classpath during compilation tests.
 * A dependency to the module containing the real class (esmf-aspect-model-jackson) can not be
 * added due to cyclic dependencies.
 */
public class Base64BinarySerializer extends ValueSerializer<byte[]> {
   @Override
   public void serialize( final byte[] value, final JsonGenerator generator, final SerializationContext context ) throws JacksonException {
      throw new UnsupportedOperationException( "This is a dummy class only intended for tests" );
   }
}
