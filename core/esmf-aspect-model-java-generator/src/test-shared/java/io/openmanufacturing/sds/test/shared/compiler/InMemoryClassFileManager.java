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

import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * A {@link javax.tools.JavaFileManager} to be used by the compiler that creates in-memory {@link CompilerOutput}s as
 * File objects.
 */
public class InMemoryClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
   private final Map<String, CompilerOutput> output = new HashMap<>();

   public InMemoryClassFileManager( final StandardJavaFileManager fileManager ) {
      super( fileManager );
   }

   @Override
   public JavaFileObject getJavaFileForOutput( final Location location, final String className, final JavaFileObject.Kind kind, final FileObject sibling ) {
      final CompilerOutput compilerOutput = new CompilerOutput( className, kind );
      output.put( className, compilerOutput );
      return compilerOutput;
   }

   public CompilerOutput getOutput( final String name ) {
      return output.get( name );
   }
}
