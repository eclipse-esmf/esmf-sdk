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
 * Implements <a href="https://www.w3.org/TR/shacl/#property-path-zero-or-more">Zero-Or-More Path</a>
 * @param path the path that can be repeated zero or more times
 */
public record ZeroOrMorePath(Path path) implements Path {
   @Override
   public <T> T accept( final Resource resource, final Visitor<T> visitor ) {
      return visitor.visitZeroOrMorePath( resource, this );
   }

   @Override
   public String toString() {
      return path + "*";
   }
}
