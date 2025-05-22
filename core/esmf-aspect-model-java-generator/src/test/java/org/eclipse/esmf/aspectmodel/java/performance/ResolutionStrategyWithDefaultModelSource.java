/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.java.performance;

import java.net.URI;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

public interface ResolutionStrategyWithDefaultModelSource extends ResolutionStrategy {

   @Override
   public default Stream<URI> listContents() {
      return Stream.empty();
   }

   @Override
   default Stream<URI> listContentsForNamespace( final AspectModelUrn aspectModelUrn ) {
      return Stream.empty();
   }

   @Override
   default Stream<AspectModelFile> loadContents() {
      return Stream.empty();
   }

   @Override
   default Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn aspectModelUrn ) {
      return Stream.empty();
   }
}
