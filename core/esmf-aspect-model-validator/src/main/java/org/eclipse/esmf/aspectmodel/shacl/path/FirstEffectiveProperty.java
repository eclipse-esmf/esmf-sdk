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

import java.util.List;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * From a {@link Path}, retrieves the first reachable RDF property. In certain cases (e.g. for an {@link AlternativePath}),
 * this can actually mean more than one property, which is why the result is a list.
 */
public class FirstEffectiveProperty implements Path.Visitor<List<Property>> {
   @Override
   public List<Property> visit( final Resource resource, final Path path ) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<Property> visitPredicatePath( final Resource resource, final PredicatePath path ) {
      return List.of( path.predicate() );
   }

   @Override
   public List<Property> visitSequencePath( final Resource resource, final SequencePath path ) {
      return path.subPaths().get( 0 ).accept( resource, this );
   }

   @Override
   public List<Property> visitAlternativePath( final Resource resource, final AlternativePath path ) {
      return Stream.concat(
            path.path1().accept( resource, this ).stream(),
            path.path2().accept( resource, this ).stream() ).toList();
   }

   @Override
   public List<Property> visitInversePath( final Resource resource, final InversePath path ) {
      return path.path().accept( resource, this );
   }

   @Override
   public List<Property> visitZeroOrMorePath( final Resource resource, final ZeroOrMorePath path ) {
      return path.path().accept( resource, this );
   }

   @Override
   public List<Property> visitOneOrMorePath( final Resource resource, final OneOrMorePath path ) {
      return path.path().accept( resource, this );
   }

   @Override
   public List<Property> visitZeroOrOnePath( final Resource resource, final ZeroOrOnePath path ) {
      return path.path().accept( resource, this );
   }
}
