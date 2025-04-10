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

package org.eclipse.esmf.aspectmodel.resolver;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration of proxy settings with optional {@link ProxySelector} and {@link Authenticator}
 *
 * @param proxy the proxy selector
 * @param authenticator the authenticator
 */
@RecordBuilder
public record ProxyConfig(
      Optional<ProxySelector> proxy,
      Optional<Authenticator> authenticator
) {
   private static final Logger LOG = LoggerFactory.getLogger( ProxyConfig.class );

   public static final ProxyConfig NO_PROXY = new ProxyConfig( Optional.empty(), Optional.empty() );

   public static ProxyConfig from( final String host, final int port ) {
      return new ProxyConfig( Optional.of( ProxySelector.of( new InetSocketAddress( host, port ) ) ), Optional.empty() );
   }

   public static ProxyConfig detectProxySettings() {
      final String envProxy = System.getenv( "http_proxy" );
      if ( envProxy != null && System.getProperty( "http.proxyHost" ) == null ) {
         final Pattern proxyPattern = Pattern.compile( "http://([^:]*):(\\d+)/?" );
         final Matcher matcher = proxyPattern.matcher( envProxy );
         if ( matcher.matches() ) {
            final String host = matcher.group( 1 );
            final String port = matcher.group( 2 );
            System.setProperty( "http.proxyHost", host );
            System.setProperty( "http.proxyPort", port );
         } else {
            LOG.debug( "The value of the 'http_proxy' environment variable is malformed, ignoring: {}", envProxy );
         }
      }

      final String host = System.getProperty( "http.proxyHost" );
      final String port = System.getProperty( "http.proxyPort" );
      if ( host != null && port != null ) {
         return new ProxyConfig( Optional.of( ProxySelector.of( new InetSocketAddress( host, Integer.parseInt( port ) ) ) ),
               Optional.empty() );
      }
      return NO_PROXY;
   }
}
