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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.function.FunctionFactory;
import org.apache.jena.sparql.function.FunctionRegistry;

/**
 * Utility class to help execute SPARQL queries, wrapping some tricky implementation details of the library used (Jena).
 */
public class SparqlExecutor {

   private FunctionRegistry customFunctions;

   public SparqlExecutor() {
      // by default, use the global function registry (the same for all contexts)
      customFunctions = FunctionRegistry.get();
   }

   /**
    * Use a custom function when executing queries, the function will be unique to this executor instance.
    *
    * @param name Name of the function
    * @param functionFactory Function factory
    * @return this to allow fluent registration of more than one function
    */
   public SparqlExecutor useCustomFunction( final String name, final FunctionFactory functionFactory ) {
      ensureContextSpecificFunctionRegistry();
      customFunctions.put( name, functionFactory );
      return this;
   }

   // Workaround: current Jena implementation uses a single global function registry for all contexts;
   // so as we want to register a context-specific function, we need also a context-specific function registry
   private void ensureContextSpecificFunctionRegistry() {
      final FunctionRegistry global = FunctionRegistry.get();
      if ( customFunctions == global ) {
         customFunctions = new FunctionRegistry();
         global.keys().forEachRemaining( uri -> customFunctions.put( uri, global.get( uri ) ) );
      }
   }

   /**
    * Executes a SPARQL query that is assumed to generate a flat list of literals
    *
    * @param model The model to query against
    * @param query The query
    * @return The string resulting by concatenating the result list in to a multi line string
    */
   @SuppressWarnings( "squid:S1905" )
   public String executeQuery( final Model model, final Query query, final String literalName ) {
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, model ) ) {
         FunctionRegistry.set( qexec.getContext(), customFunctions );
         return StreamSupport.stream( ((Iterable<QuerySolution>) (qexec::execSelect)).spliterator(), false )
               .map( solution -> solution.getLiteral( literalName ) )
               .map( Literal::toString )
               .collect( Collectors.joining( "\n" ) );
      }
   }

   /**
    * Executes a SPARQL construct query.
    *
    * @param model The model to query against
    * @param query The query
    * @param targetModel The result of the operation
    */
   public void executeConstruct( final Model model, final Query query, final Model targetModel ) {
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, model ) ) {
         FunctionRegistry.set( qexec.getContext(), customFunctions );
         qexec.execConstruct( targetModel );
      }
   }
}
