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

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprTransform;
import org.apache.jena.sparql.expr.ExprTransformer;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransform;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformSubst;
import org.apache.jena.sparql.syntax.syntaxtransform.ExprTransformNodeElement;
import org.apache.jena.sparql.syntax.syntaxtransform.QueryTransformOps;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#sparql-constraints">sh:sparql</a>
 *
 * @param message the message returned by the SPARQL query
 * @param query the query
 */
public record SparqlConstraint( String message, Query query ) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final Map<Var, ? extends RDFNode> substitutions = Map.of( Var.alloc( "this" ), context.element() );
      final Query query1 = substituteVariablesInQuery( query, substitutions );

      final List<Violation> results = new ArrayList<>();
      try ( final QueryExecution queryExecution = QueryExecutionFactory.create( query1, context.resolvedModel() ) ) {
         final ResultSet resultSet = queryExecution.execSelect();
         while ( resultSet.hasNext() ) {
            final QuerySolution solution = resultSet.next();
            final Map<String, RDFNode> bindings = resultSet.getResultVars().stream()
                  .filter( resultVar -> solution.get( resultVar ) != null )
                  .collect( Collectors.toMap( Function.identity(), solution::get ) );
            results.add( new SparqlConstraintViolation( context, message, bindings ) );
         }
      }
      return results;
   }

   @Override
   public String name() {
      return "sh:sparql";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitSparqlConstraint( this );
   }

   /**
    * Perform proper query substitutions; unfortunately the substitutions done by {@see org.apache.jena.query.ParameterizedSparqlString} are
    * not always correct.
    *
    * @param query the query
    * @param substitutions the map of substitutions to perform
    * @return the updated query
    */
   private Query substituteVariablesInQuery( final Query query, final Map<Var, ? extends RDFNode> substitutions ) {
      final Map<String, ? extends RDFNode> varNameRdfNodeMap = substitutions.entrySet().stream()
            .map( entry -> Map.entry( entry.getKey().getVarName(), entry.getValue() ) )
            .collect( asMap() );
      final Query result = QueryTransformOps.queryReplaceVars( query, varNameRdfNodeMap );

      final Map<Var, Node> varNodeMap = substitutions.entrySet().stream()
            .map( entry -> Map.entry( entry.getKey(), entry.getValue().asNode() ) )
            .collect( asMap() );
      if ( result.hasHaving() ) {
         final ElementTransform elementTransform = new ElementTransformSubst( varNodeMap );
         final ExprTransform exprTransform = new ExprTransformNodeElement( node -> varNodeMap.getOrDefault( node, node ),
               elementTransform );
         final List<Expr> havingExpressions = result.getHavingExprs();
         for ( int i = 0; i < havingExpressions.size(); i++ ) {
            final Expr expression = havingExpressions.get( i );
            final Expr newExpression = ExprTransformer.transform( exprTransform, expression );
            if ( newExpression != expression ) {
               havingExpressions.set( i, newExpression );
            }
         }
      }
      return result;
   }
}
