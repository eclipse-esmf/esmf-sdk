/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.shacl.constraint;

import java.util.List;
import java.util.function.Predicate;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.JsLibrary;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.js.JsFactory;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.js.JsGraph;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.JsViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ProcessingViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

public class JsConstraint implements Constraint {
   private final ScriptEngine engine;
   private final String message;
   private final JsLibrary jsLibrary;
   private final String jsFunctionName;

   public JsConstraint( final String message, final JsLibrary jsLibrary, final String jsFunctionName ) {
      this.message = message;
      this.jsLibrary = jsLibrary;
      this.jsFunctionName = jsFunctionName;

      engine = new ScriptEngineManager().getEngineByName( "JavaScript" );
      final Bindings bindings = engine.getBindings( ScriptContext.ENGINE_SCOPE );
      bindings.put( "polyglot.js.allowHostAccess", true );
      bindings.put( "polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true );
      bindings.put( "TermFactory", null );
      try {
         engine.eval( jsLibrary.javaScriptCode() );
      } catch ( final ScriptException e ) {
         throw new RuntimeException( e );
      }
   }

   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final Bindings bindings = engine.getBindings( ScriptContext.ENGINE_SCOPE );
      bindings.put( "$data", new JsGraph( rdfNode.getModel().getGraph() ) );
      bindings.put( "$shapes", new JsGraph( context.validator().getShapesModel().getGraph() ) );

      try {
         final Object _$this = JsFactory.asJsTerm( context.element().asNode() );
         final Object _$value = JsFactory.asJsTerm( rdfNode.asNode() );
         final Object result = ((Invocable) engine).invokeFunction( jsFunctionName, _$this, _$value );
         if ( result instanceof Boolean booleanResult ) {
            if ( booleanResult ) {
               return List.of();
            }
            return List.of( new JsViolation( context, message(), jsLibrary(), jsFunctionName() ) );
         }
         // TODO: Handle { message: ..., value: ... } results from JavaScript functions
         throw new RuntimeException( "Unexpected result " + result );
      } catch ( final ScriptException | NoSuchMethodException exception ) {
         return List.of( new ProcessingViolation( "JavaScript evaluation failed", exception ) );
      }
   }

   @Override
   public String name() {
      return "sh:js";
   }

   public String message() {
      return message;
   }

   public JsLibrary jsLibrary() {
      return jsLibrary;
   }

   public String jsFunctionName() {
      return jsFunctionName;
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitJsConstraint( this );
   }
}
