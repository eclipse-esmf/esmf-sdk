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

import org.eclipse.esmf.samm.KnownVersion;

/**
 * Returns the set of bamm:// URLs that comprise the meta model definition files for a given meta model version
 */
@SuppressWarnings( "squid:S2112" ) // URL is specifically required here
public class ClassPathMetaModelUrnResolver implements Function<KnownVersion, Set<URL>> {
   @Override
   public Set<URL> apply( final KnownVersion metaModelVersion ) {
      return Stream.of(
            MetaModelUrls.url( "meta-model", metaModelVersion, "aspect-meta-model-definitions.ttl" ),
            MetaModelUrls.url( "meta-model", metaModelVersion, "type-conversions.ttl" ),
            MetaModelUrls.url( "characteristic", metaModelVersion, "characteristic-definitions.ttl" ),
            MetaModelUrls.url( "characteristic", metaModelVersion, "characteristic-instances.ttl" ),
            MetaModelUrls.url( "entity", metaModelVersion, "TimeSeriesEntity.ttl" ),
            MetaModelUrls.url( "entity", metaModelVersion, "FileResource.ttl" ),
            MetaModelUrls.url( "entity", metaModelVersion, "Point3d.ttl" ),
            MetaModelUrls.url( "unit", metaModelVersion, "units.ttl" ),
            Optional.<URL> empty()
      ).filter( Optional::isPresent ).map( Optional::get ).collect( Collectors.toSet() );
   }
}
