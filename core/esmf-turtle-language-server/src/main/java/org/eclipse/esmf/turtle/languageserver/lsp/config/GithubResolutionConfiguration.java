/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.lsp.config;

import java.util.List;

import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfig;


public class GithubResolutionConfiguration {
   private volatile List<GithubModelSourceConfig> repositories = List.of();

   public void update( final List<GithubModelSourceConfig> repositories ) {
      this.repositories = repositories == null ? List.of() : List.copyOf( repositories );
   }

   public List<GithubModelSourceConfig> repositories() {
      return repositories;
   }

   public boolean isEmpty() {
      return repositories.isEmpty();
   }
}
