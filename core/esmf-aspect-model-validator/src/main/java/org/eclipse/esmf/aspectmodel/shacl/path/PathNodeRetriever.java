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

package org.eclipse.esmf.aspectmodel.shacl.path;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class PathNodeRetriever implements Path.Visitor<List<Statement>> {
   @Override
   public List<Statement> visit( final Resource resource, final Path path ) {
      throw new UnsupportedOperationException();
   }

   /**
    * Returns a list of Statements with a path length of 1 for each property assertion
    *
    * @param resource the origin
    * @param path the predicate path
    * @return the list of concrete paths
    */
   @Override
   public List<Statement> visitPredicatePath( final Resource resource, final PredicatePath path ) {
      return resource.listProperties( path.predicate() ).toList();
   }

   @Override
   public List<Statement> visitSequencePath( final Resource resource, final SequencePath path ) {
      if ( path.subPaths().isEmpty() ) {
         return List.of();
      }
      final Path firstPath = path.subPaths().get( 0 );
      final List<Statement> pathResult = firstPath.accept( resource, this );
      final List<Path> restPaths = path.subPaths().subList( 1, path.subPaths().size() );
      if ( restPaths.isEmpty() ) {
         return pathResult;
      }
      return pathResult.stream()
            .map( Statement::getObject )
            .filter( RDFNode::isResource )
            .map( RDFNode::asResource )
            .flatMap( newResource -> visitSequencePath( newResource, new SequencePath( restPaths ) ).stream() )
            .toList();
   }

   @Override
   public List<Statement> visitAlternativePath( final Resource resource, final AlternativePath path ) {
      return Stream.concat(
            path.path1().accept( resource, this ).stream(),
            path.path2().accept( resource, this ).stream() ).toList();
   }

   @Override
   public List<Statement> visitInversePath( final Resource resource, final InversePath path ) {
      if ( path.path() instanceof final PredicatePath predicatePath ) {
         return resource.getModel().listStatements( null, predicatePath.predicate(), resource )
               .filterKeep( statement -> statement.getObject().isResource() )
               .mapWith( statement ->
                     resource.getModel()
                           .createStatement( statement.getObject().asResource(), statement.getPredicate(), statement.getSubject() ) )
               .toList();
      }
      throw new UnsupportedOperationException( "Inverse property path is only supported for named properties" );
   }

   @Override
   public List<Statement> visitZeroOrMorePath( final Resource resource, final ZeroOrMorePath path ) {
      final Statement zeroStatement = resource.getModel().createStatement(
            resource, resource.getModel().createProperty( "urn:internal" ), resource );

      final Deque<Statement> toProcess = new ArrayDeque<>();
      final Set<Resource> processedResources = new HashSet<>();
      final List<Statement> result = new ArrayList<>();
      toProcess.push( zeroStatement );

      while ( !toProcess.isEmpty() ) {
         final Statement currentStatement = toProcess.pop();
         result.add( currentStatement );
         final Resource originNode = currentStatement.getResource();
         final List<Statement> nodesAfterOneStep = path.path().accept( originNode, this );
         processedResources.add( originNode );

         for ( final Statement statement : nodesAfterOneStep ) {
            if ( statement.getObject().isResource() && !processedResources.contains( statement.getObject().asResource() ) ) {
               toProcess.push( statement );
            } else {
               result.add( statement );
            }
         }
      }
      return result;
   }

   @Override
   public List<Statement> visitOneOrMorePath( final Resource resource, final OneOrMorePath path ) {
      return path.path().accept( resource, this ).stream()
            .flatMap( statement -> statement.getObject().isResource()
                  ? visitZeroOrMorePath( statement.getResource(), new ZeroOrMorePath( path.path() ) ).stream()
                  : Stream.empty() )
            .toList();
   }

   @Override
   public List<Statement> visitZeroOrOnePath( final Resource resource, final ZeroOrOnePath path ) {
      final Statement zeroStatement = resource.getModel().createStatement(
            resource, resource.getModel().createProperty( "urn:internal" ), resource );
      return Stream.concat( Stream.of( zeroStatement ), path.path().accept( resource, this ).stream() ).toList();
   }
}
