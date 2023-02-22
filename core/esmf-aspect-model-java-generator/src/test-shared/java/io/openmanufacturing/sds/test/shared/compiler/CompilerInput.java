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

/**
 * A {@link javax.tools.JavaFileObject} that abstracts Java source code backed by a buffer in memory.
 */
public class CompilerInput extends InMemoryJavaFileObject {
   private final String content;

   /**
    * Constructor for the construction of the file object representing Java source code.
    *
    * @param className The fully qualified name of the class
    * @param content The Java source code
    */
   public CompilerInput( final String className, final String content ) {
      super( className, Kind.SOURCE );
      this.content = content;
   }

   @Override
   public CharSequence getCharContent( final boolean ignoreEncodingErrors ) {
      return content;
   }
}
