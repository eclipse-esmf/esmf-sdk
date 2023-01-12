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

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import guru.nidi.graphviz.engine.GraphvizLoader;

/**
 * This is a GraalVM substitution class (see https://blog.frankel.ch/coping-incompatible-code-graalvm-compilation/#substitutions)
 * for {@link GraphvizLoader}.
 * Reason: The <pre>isOnClassPath</pre> method is used to check availability of dependencies at runtime using resource lookup. However, this won't work in
 * the native image build, instead, dependencies are wired at build time, so we can provide the relevant information statically.
 */
@TargetClass( GraphvizLoader.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101" // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
} )
public final class Target_guru_nidi_graphviz_engine_GraphvizLoader {
   @Substitute
   static boolean isOnClasspath( final String resource ) {
      return switch ( resource ) {
         case "org/apache/batik/transcoder/Transcoder.class" -> true;
         case "com/kitfox/svg/SVGDiagram.class" -> false;
         case "net/arnx/nashorn/lib/PromiseException.class" -> false;
         case "org/apache/commons/exec/CommandLine.class" -> false;
         case "com/eclipsesource/v8/V8.class" -> false;
         default -> Target_guru_nidi_graphviz_engine_GraphvizLoader.class.getClassLoader().getResource( resource ) != null;
      };
   }
}
