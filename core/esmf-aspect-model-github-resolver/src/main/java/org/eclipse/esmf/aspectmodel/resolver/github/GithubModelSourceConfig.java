/*
 * Copyright (c) 2024 Bosch Software Innovations GmbH. All rights reserved.
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
