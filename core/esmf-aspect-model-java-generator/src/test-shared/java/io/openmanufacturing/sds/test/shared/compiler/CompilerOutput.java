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

package io.openmanufacturing.sds.test.shared.compiler;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * A {@link javax.tools.JavaFileObject} that lives in memory and can be written to by the compiler.
 */
public class CompilerOutput extends InMemoryJavaFileObject {
   private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

   protected CompilerOutput( final String name, final Kind kind ) {
      super( name, kind );
   }

   @Override
   public OutputStream openOutputStream() {
      return outputStream;
   }

   /**
    * Returns the bytes written by the compiler
    *
    * @return The bytes of this JavaFileObject
    */
   public byte[] getBytes() {
      return outputStream.toByteArray();
   }
}
