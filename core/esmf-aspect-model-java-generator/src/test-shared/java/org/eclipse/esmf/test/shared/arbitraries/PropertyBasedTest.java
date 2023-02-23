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

package org.eclipse.esmf.test.shared.arbitraries;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMMC;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMME;

public abstract class PropertyBasedTest implements SammArbitraries {
   private final DatatypeFactory datatypeFactory;
   private final Map<KnownVersion, SAMM> SAMM_VERSIONS;
   private final Map<KnownVersion, SAMMC> SAMMC_VERSIONS;
   private final Map<KnownVersion, SAMME> SAMME_VERSIONS;

   public PropertyBasedTest() {
      SAMM_VERSIONS = KnownVersion.getVersions().stream().collect( Collectors.toMap( Function.identity(), SAMM::new ) );
      SAMMC_VERSIONS = KnownVersion.getVersions().stream()
            .collect( Collectors.toMap( Function.identity(), SAMMC::new ) );
      SAMME_VERSIONS = KnownVersion.getVersions().stream().collect( Collectors.toMap( Function.identity(),
            version -> new SAMME( version, SAMM_VERSIONS.get( version ) ) ) );
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch ( final DatatypeConfigurationException exception ) {
         fail( "Could not instantiate DatatypeFactory", exception );
         throw new RuntimeException( exception );
      }
   }

   @Override
   public SAMM samm( final KnownVersion metaModelVersion ) {
      return SAMM_VERSIONS.get( metaModelVersion );
   }

   @Override
   public SAMMC sammc( final KnownVersion metaModelVersion ) {
      return SAMMC_VERSIONS.get( metaModelVersion );
   }

   @Override
   public SAMME samme( final KnownVersion metaModelVersion ) {
      return SAMME_VERSIONS.get( metaModelVersion );
   }

   @Override
   public DatatypeFactory getDatatypeFactory() {
      return datatypeFactory;
   }
}
