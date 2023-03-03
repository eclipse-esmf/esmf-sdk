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

package io.openmanufacturing.sds.substitution;

import guru.nidi.graphviz.engine.AbstractJsGraphvizEngine;
import guru.nidi.graphviz.engine.JavascriptEngine;
import guru.nidi.graphviz.engine.MissingDependencyException;

/**
 * Custom graphviz-java {@link JavascriptEngine} implementation based on the GraalVM JavaScript engine
 */
public final class GraalVmJsGraphvizEngine extends AbstractJsGraphvizEngine {
   public GraalVmJsGraphvizEngine() {
      super( true, GraalVmJsGraphvizEngine::buildEngine );
      super.doInit();
   }

   @SuppressWarnings( "squid:S1166" )
   // Can't retrow exception, because logic (including throwing MissingDependencyException) is adapted from graphivz-java core
   private static JavascriptEngine buildEngine() {
      try {
         return new GraalJavascriptEngine();
      } catch ( final ExceptionInInitializerError | NoClassDefFoundError | IllegalStateException exception ) {
         throw new MissingDependencyException( "GraalVM JavaScript engine is not available.", "org.graalvm.js:js" );
      }
   }
}
