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

import org.apache.jena.rdf.model.Resource;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#property-paths">SHACL Property Paths</a>.
 */
public interface Path {
   <T> T accept( Resource resource, Visitor<T> visitor );

   interface Visitor<T> {
      T visit( Resource resource, Path path );

      default T visitPredicatePath( final Resource resource, final PredicatePath path ) {
         return visit( resource, path );
      }

      default T visitSequencePath( final Resource resource, final SequencePath path ) {
         return visit( resource, path );
      }

      default T visitAlternativePath( final Resource resource, final AlternativePath path ) {
         return visit( resource, path );
      }

      default T visitInversePath( final Resource resource, final InversePath path ) {
         return visit( resource, path );
      }

      default T visitZeroOrMorePath( final Resource resource, final ZeroOrMorePath path ) {
         return visit( resource, path );
      }

      default T visitOneOrMorePath( final Resource resource, final OneOrMorePath path ) {
         return visit( resource, path );
      }

      default T visitZeroOrOnePath( Resource resource, final ZeroOrOnePath path ) {
         return visit( resource, path );
      }
   }
}

