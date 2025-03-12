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

package org.eclipse.esmf.aspectmodel.shacl.constraint;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.esmf.aspectmodel.shacl.JsLibrary;
import org.eclipse.esmf.aspectmodel.shacl.ShaclValidationException;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsFactory;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsGraph;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsTerm;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.TermFactory;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.JsConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a SHACL JavaScript constraint as specified by the <a href="https://www.w3.org/TR/shacl-js/">SHACL JavaScript Extensions</a>.
 * The given JavaScript function can refer to the variables "TermFactory" (see {@link TermFactory}), "$data" (the data graph) and "$shapes"
 * (the shapes graph).
 * It can return either a boolean (with true indicating successfull validation and false a validation error) or a JavaScript object with
 * context information;
 * every key in the object must be a string.
 * The JavaScript object can contain a key "message" which, if present, overrides the sh:message defined in the shape.
 */
public class JsConstraint implements Constraint {
   private static final Logger LOG = LoggerFactory.getLogger( JsConstraint.class );
   private static boolean evaluateJavaScript = true;
   private final ScriptEngine engine;
   private final String message;
   private final JsLibrary jsLibrary;
   private final String jsFunctionName;

   public JsConstraint( final String message, final JsLibrary jsLibrary, final String jsFunctionName ) {
      this.message = message;
      this.jsLibrary = jsLibrary;
      this.jsFunctionName = jsFunctionName;

      if ( !evaluateJavaScript ) {
         engine = null;
         return;
      }

      // The guest application (i.e., the JavaScript) can only be compiled at runtime if either the host application runs via GraalVM
      // or, on a regular JVMCI-enabled JDK, the Graal Compiler is set up (additional JIT compilers are added). Otherwise, the script runs
      // in interpreted mode, which will be slower and print a corresponding warning. Since in the SHACL validation only very little
      // JavaScript
      // code is executed, this is not a problem. The following the property disables the corresponding warning.
      // See https://www.graalvm.org/22.3/reference-manual/js/FAQ/#warning-implementation-does-not-support-runtime-compilation
      System.setProperty( "polyglot.engine.WarnInterpreterOnly", "false" );
      engine = new ScriptEngineManager().getEngineByName( "JavaScript" );
      if ( engine == null ) {
         throw new ShaclValidationException( "Could not initialize JavaScript engine. Please make sure org.graalvm.js:js is "
               + "in the list of dependencies and/or the 'js' component is installed in your GraalVM runtime." );
      }
      final Bindings bindings = engine.getBindings( ScriptContext.ENGINE_SCOPE );
      // The following settings are required to allow the script to access methods and fields on the injected objects
      bindings.put( "polyglot.js.allowHostAccess", true );
      bindings.put( "polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true );
      bindings.put( "TermFactory", new TermFactory() );
      try {
         engine.eval( jsLibrary.javaScriptCode() );
      } catch ( final ScriptException exception ) {
         throw new ShaclValidationException( "Could not evaluate JavaScript constraint", exception );
      }
   }

   /**
    * Globally enables or disables evaluation of JavaScript constraints.
    *
    * @param doEvaluate configure whether to evaluate JavaScript or not
    */
   public static void setEvaluateJavaScript( final boolean doEvaluate ) {
      LOG.debug( String.format( "Globally %sabled JavaScript constraint evaluation", doEvaluate ? "en" : "dis" ) );
      JsConstraint.evaluateJavaScript = doEvaluate;
   }

   @SuppressWarnings( "LocalVariableNamingConvention" )  // use this_
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      if ( !evaluateJavaScript ) {
         return List.of();
      }

      final Bindings bindings = engine.getBindings( ScriptContext.ENGINE_SCOPE );
      bindings.put( "$data", new JsGraph( rdfNode.getModel().getGraph() ) );
      bindings.put( "$shapes", new JsGraph( context.validator().getShapesModel().getGraph() ) );

      try {
         final Object this_ = JsFactory.asJsTerm( context.element().asNode() );
         final Object value = JsFactory.asJsTerm( rdfNode.asNode() );
         final Object result = ( (Invocable) engine ).invokeFunction( jsFunctionName, this_, value );
         if ( result == null ) {
            return List.of( new JsConstraintViolation( context, "JavaScript evaluation of " + jsFunctionName() + " returned null",
                  jsLibrary(), jsFunctionName(), Collections.emptyMap() ) );
         }
         if ( result instanceof final Boolean booleanResult ) {
            if ( booleanResult ) {
               return List.of();
            }
            return List.of( new JsConstraintViolation( context, message(), jsLibrary(), jsFunctionName(), Collections.emptyMap() ) );
         }
         if ( result instanceof final Map<?, ?> map ) {
            final Map<String, Object> resultMap = map.entrySet().stream()
                  .filter( entry -> entry.getKey() instanceof String )
                  .collect( Collectors.toMap(
                        entry -> (String) entry.getKey(),
                        entry -> entry.getValue() instanceof final JsTerm term ? term.getNode() : entry.getValue() ) );
            return List.of( new JsConstraintViolation( context, message(), jsLibrary(), jsFunctionName(), resultMap ) );
         }

         LOG.debug( "JavaScript evaluation of {} returned invalid result: {}", jsFunctionName(), result );
         return List.of( new ProcessingViolation( "JavaScript evaluation of " + jsFunctionName() + " returned an invalid result",
               new IllegalArgumentException() ) );
      } catch ( final ScriptException exception ) {
         LOG.debug( "JavaScript evaluation of {} failed", jsFunctionName(), exception );
         return List.of( new ProcessingViolation( "JavaScript evaluation of " + jsFunctionName() + " failed", exception ) );
      } catch ( final NoSuchMethodException exception ) {
         return List.of( new ProcessingViolation( "JavaScript function " + jsFunctionName() + " was not found", exception ) );
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
