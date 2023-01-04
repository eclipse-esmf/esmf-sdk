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

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import guru.nidi.graphviz.engine.AbstractJavascriptEngine;
import guru.nidi.graphviz.engine.GraphvizException;
import guru.nidi.graphviz.engine.ResultHandler;

/**
 * This class is a copy of <code>guru.nidi.graphviz.engine.GraalJavascriptEngine</code>, which needs to be instantiated from
 * the {@link GraalVmJsGraphvizEngine}, because the original class is not public.
 */
public class GraalJavascriptEngine extends AbstractJavascriptEngine {
   private final ResultHandler resultHandler = new ResultHandler();
   private final Context context = Context.newBuilder( "js" ).allowAllAccess( true ).build();

   GraalJavascriptEngine() {
      context.getPolyglotBindings().putMember( "handler", resultHandler );
      eval( "function result(r){ Polyglot.import('handler').setResult(r); }"
            + "function error(r){ Polyglot.import('handler').setError(r); }"
            + "function log(r){ Polyglot.import('handler').log(r); }" );
   }

   @Override
   protected String execute( final String js ) {
      try {
         eval( js );
         return resultHandler.waitFor();
      } catch ( final PolyglotException e ) {
         throw new GraphvizException( "Problem executing javascript", e );
      }
   }

   private void eval( final String code ) {
      context.eval( "js", code );
   }
}
