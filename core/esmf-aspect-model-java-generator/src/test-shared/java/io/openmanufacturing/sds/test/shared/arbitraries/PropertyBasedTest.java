/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.test.shared.arbitraries;

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

public abstract class PropertyBasedTest implements BammArbitraries {
   private final DatatypeFactory datatypeFactory;
   private final Map<KnownVersion, SAMM> BAMM_VERSIONS;
   private final Map<KnownVersion, SAMMC> BAMMC_VERSIONS;
   private final Map<KnownVersion, SAMME> BAMME_VERSIONS;

   public PropertyBasedTest() {
      BAMM_VERSIONS = KnownVersion.getVersions().stream().collect( Collectors.toMap( Function.identity(), SAMM::new ) );
      BAMMC_VERSIONS = KnownVersion.getVersions().stream()
            .collect( Collectors.toMap( Function.identity(), SAMMC::new ) );
      BAMME_VERSIONS = KnownVersion.getVersions().stream().collect( Collectors.toMap( Function.identity(),
            version -> new SAMME( version, BAMM_VERSIONS.get( version ) ) ) );
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch ( final DatatypeConfigurationException exception ) {
         fail( "Could not instantiate DatatypeFactory", exception );
         throw new RuntimeException( exception );
      }
   }

   @Override
   public SAMM bamm( final KnownVersion metaModelVersion ) {
      return BAMM_VERSIONS.get( metaModelVersion );
   }

   @Override
   public SAMMC bammc( final KnownVersion metaModelVersion ) {
      return BAMMC_VERSIONS.get( metaModelVersion );
   }

   @Override
   public SAMME bamme( final KnownVersion metaModelVersion ) {
      return BAMME_VERSIONS.get( metaModelVersion );
   }

   @Override
   public DatatypeFactory getDatatypeFactory() {
      return datatypeFactory;
   }
}
