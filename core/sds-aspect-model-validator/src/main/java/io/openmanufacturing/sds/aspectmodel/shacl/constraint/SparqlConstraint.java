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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.Var;
import org.topbraid.jenax.util.JenaUtil;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.SparqlConstraintViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

public record SparqlConstraint(String message, Query query) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final Model model = context.element().getModel();
      final Map<Var, Node> substitutions = Map.of( Var.alloc( "this" ), context.element().asNode() );
      final Query query1 = JenaUtil.queryWithSubstitutions( query, substitutions );

      final List<Violation> results = new ArrayList<>();
      try ( final QueryExecution queryExecution = QueryExecutionFactory.create( query1, model ) ) {
         final ResultSet resultSet = queryExecution.execSelect();
         while ( resultSet.hasNext() ) {
            final QuerySolution solution = resultSet.next();
            final Map<String, RDFNode> bindings = resultSet.getResultVars().stream().collect( Collectors.toMap( Function.identity(), solution::get ) );
            results.add( new SparqlConstraintViolation( context, message, bindings ) );
         }
      }
      return results;
   }
}
