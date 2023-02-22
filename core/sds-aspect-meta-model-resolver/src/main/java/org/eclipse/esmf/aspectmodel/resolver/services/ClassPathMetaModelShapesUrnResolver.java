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

package org.eclipse.esmf.aspectmodel.resolver.services;

import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

/**
 * Returns the set of bamm:// URLs that comprise the meta model shape files for a given meta model version
 */
@SuppressWarnings( "squid:S2112" ) // URL is specifically required here
public class ClassPathMetaModelShapesUrnResolver implements Function<KnownVersion, Set<URL>> {
   @Override
   public Set<URL> apply( final KnownVersion metaModelVersion ) {
      return Stream.of(
            MetaModelUrls.url( "meta-model", metaModelVersion, "prefix-declarations.ttl" ),
            MetaModelUrls.url( "meta-model", metaModelVersion, "aspect-meta-model-shapes.ttl" ),
            MetaModelUrls.url( "characteristic", metaModelVersion, "characteristic-shapes.ttl" )
      ).filter( Optional::isPresent ).map( Optional::get ).collect( Collectors.toSet() );
   }
}
