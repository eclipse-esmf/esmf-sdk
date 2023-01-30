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

package io.openmanufacturing.sds.aspectmodel.generator.diagram;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.jena.graph.Node;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.sparql.function.FunctionFactory;
import org.junit.jupiter.api.Test;

import io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader;

public class SparqlExecutorTest {

   // function creating synthetic names starting with the given prefix for anonymous nodes
   record CustomFunctionFactory(String prefix) implements FunctionFactory {
      @Override
      public Function create( final String s ) {
         return new FunctionBase1() {
            @Override
            public NodeValue exec( final NodeValue v ) {
               final Node node = v.asNode();
               if ( node.isBlank() ) {
                  return NodeValue.makeString( prefix + "_" + node );
               }
               return null;
            }
         };
      }
   }

   @Test
   void testCustomFunctionIsUniqueForContext() {
      final String modelSource = """
               @prefix : <urn:bamm:dummy#>.
               [
                 a :Object ;
               ]
            """;

      final String querySource = """
               prefix : <urn:bamm:dummy#>
               prefix func: <urn:bamm:function:2.0.0#>
               
               select ?anonName
               where {
                 {
                   ?anon a :Object .
                 }
                 bind( func:getElementName( ?anon ) as ?anonName )
               }
            """;

      final Model model = TurtleLoader.loadTurtle( new ByteArrayInputStream( modelSource.getBytes( StandardCharsets.UTF_8 ) ) ).get();
      final SparqlExecutor executor1 = new SparqlExecutor().useCustomFunction( "urn:bamm:function:2.0.0#getElementName",
            new CustomFunctionFactory( "executor1" ) );
      final SparqlExecutor executor2 = new SparqlExecutor().useCustomFunction( "urn:bamm:function:2.0.0#getElementName",
            new CustomFunctionFactory( "executor2" ) );
      final String result1 = executor1.executeQuery( model, QueryFactory.create( querySource ), "anonName" );
      assertTrue( result1.startsWith( "executor1_" ) );
      final String result2 = executor2.executeQuery( model, QueryFactory.create( querySource ), "anonName" );
      assertTrue( result2.startsWith( "executor2_" ) );
   }
}
