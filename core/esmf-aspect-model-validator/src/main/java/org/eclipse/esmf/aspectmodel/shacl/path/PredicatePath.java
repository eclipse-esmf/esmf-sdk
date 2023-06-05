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

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#property-path-predicate">Predicate Path</a>
 * @param predicate the predicate
 */
public record PredicatePath(Property predicate) implements Path {
   @Override
   public <T> T accept( final Resource resource, final Visitor<T> visitor ) {
      return visitor.visitPredicatePath( resource, this );
   }

   @Override
   public String toString() {
      return predicate.getModel().shortForm( predicate.getURI() );
   }
}
