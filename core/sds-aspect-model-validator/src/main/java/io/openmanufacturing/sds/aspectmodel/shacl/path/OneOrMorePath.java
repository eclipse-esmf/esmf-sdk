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

package io.openmanufacturing.sds.aspectmodel.shacl.path;

import org.apache.jena.rdf.model.Resource;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#property-path-one-or-more">One-Or-More Path</a>
 * @param path the path which can be repeated zero or more times
 */
public record OneOrMorePath(Path path) implements Path {
   @Override
   public <T> T accept( final Resource resource, final Visitor<T> visitor ) {
      return visitor.visitOneOrMorePath( resource, this );
   }

   @Override
   public String toString() {
      final String p = path.toString();
      return String.format( "%s/%s+", p, p );
   }
}
