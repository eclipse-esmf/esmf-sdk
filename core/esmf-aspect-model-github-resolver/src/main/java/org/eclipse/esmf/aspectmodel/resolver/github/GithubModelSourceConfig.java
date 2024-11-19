/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver.github;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.resolver.GithubRepository;
import org.eclipse.esmf.aspectmodel.resolver.ProxyConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record GithubModelSourceConfig(
      GithubRepository repository,
      String directory,
      ProxyConfig proxyConfig,
      String token
) {
   public GithubModelSourceConfig {
      directory = Optional.ofNullable( directory ).map( d -> d.endsWith( "/" ) ? d.substring( 0, d.length() - 1 ) : d ).orElse( "" );
      proxyConfig = Optional.ofNullable( proxyConfig ).orElse( ProxyConfig.detectProxySettings() );
   }
}
