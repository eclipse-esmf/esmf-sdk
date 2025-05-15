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

package org.eclipse.esmf;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.process.OsProcessLauncher;
import org.eclipse.esmf.aspectmodel.resolver.process.ProcessLauncher;

/**
 * A {@link ProcessLauncher} that executes the static main(String[] args) function of a given class by running it in a new JVM
 * process. The class path and JVM arguments are adapted from the currently running process.
 */
public class MainClassProcessLauncher extends OsProcessLauncher {
   /**
    * Constructor. Builds the process launcher from a given main class and JVM arguments.
    *
    * @param mainClass the main class to execute
    * @param additionalJvmArguments additional arguments to add to the JVM
    * @param keepJvmArgument predicate used to adjust the list of JVM arguments from the currently JVM: Only those arguments kept by this
    * filter are kept in the newly launched JVM
    */
   public MainClassProcessLauncher( final Class<?> mainClass, final List<String> additionalJvmArguments,
         final Predicate<String> keepJvmArgument ) {
      super( buildCommand( mainClass, additionalJvmArguments, keepJvmArgument ) );
   }

   private static List<String> buildCommand( final Class<?> mainClass, final List<String> additionalJvmArguments,
         final Predicate<String> keepJvmArgument ) {
      final List<String> jvmArguments = Stream.concat( ManagementFactory.getRuntimeMXBean().getInputArguments().stream(),
                  additionalJvmArguments.stream() )
            .filter( keepJvmArgument )
            .toList();

      final List<String> commandWithArguments = new ArrayList<>();
      commandWithArguments.add( ProcessHandle.current().info().command().orElse( "java" ) );
      commandWithArguments.addAll( jvmArguments );
      commandWithArguments.add( "--class-path" );
      commandWithArguments.add( System.getProperty( "java.class.path" ) );
      commandWithArguments.add( mainClass.getName() );
      return commandWithArguments;
   }
}
