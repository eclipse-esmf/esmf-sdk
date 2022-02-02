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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import io.openmanufacturing.sds.aspectmodel.java.QualifiedName;

public class JavaCompiler {
   private static class DiagnosticListener implements javax.tools.DiagnosticListener<FileObject> {
      @Override
      public void report( final Diagnostic<? extends FileObject> diagnostic ) {
         System.out.println( diagnostic );
      }
   }

   public static Map<QualifiedName, Class<?>> compile( final List<QualifiedName> loadOrder,
         final Map<QualifiedName, String> sources, final List<String> predefinedClasses ) {
      final javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      final InMemoryClassFileManager manager = new InMemoryClassFileManager(
            compiler.getStandardFileManager( null, null, null ) );

      final List<JavaFileObject> compilerInput = loadOrder.stream()
            .map( key -> new CompilerInput( key.toString(), sources.get( key ) ) )
            .collect( Collectors.toList() );

      final List<String> compilerOptions = List.of( "-classpath", System.getProperty( "java.class.path" ) );
      compiler.getTask( null, manager, new DiagnosticListener(), compilerOptions, null, compilerInput ).call();

      return loadOrder.stream()
            .collect( Collectors.toMap( Function.identity(), qualifiedName -> defineAndLoad( qualifiedName, manager ) ) );
   }

   @SuppressWarnings( "unchecked" )
   private static <T> Class<T> defineAndLoad( final QualifiedName className, final InMemoryClassFileManager manager ) {
      try {
         return (Class<T>) new ClassLoader() {
            @Override
            protected Class<?> findClass( final String name ) {
               final byte[] classBytes = manager.getOutput( name ).getBytes();
               return defineClass( name, classBytes, 0, classBytes.length );
            }
         }.loadClass( className.toString() );
      } catch ( final ClassNotFoundException exception ) {
         throw new RuntimeException( exception );
      }
   }
}
