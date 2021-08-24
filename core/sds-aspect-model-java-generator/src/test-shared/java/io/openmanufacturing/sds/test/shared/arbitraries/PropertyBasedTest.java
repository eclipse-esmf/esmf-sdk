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

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMMC;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMME;

public abstract class PropertyBasedTest implements BammArbitraries {
   private final DatatypeFactory datatypeFactory;
   private final Map<KnownVersion, BAMM> BAMM_VERSIONS;
   private final Map<KnownVersion, BAMMC> BAMMC_VERSIONS;
   private final Map<KnownVersion, BAMME> BAMME_VERSIONS;

   public PropertyBasedTest() {
      BAMM_VERSIONS = KnownVersion.getVersions().stream().collect( Collectors.toMap( Function.identity(), BAMM::new ) );
      BAMMC_VERSIONS = KnownVersion.getVersions().stream()
                                   .collect( Collectors.toMap( Function.identity(), BAMMC::new ) );
      BAMME_VERSIONS = KnownVersion.getVersions().stream().collect( Collectors.toMap( Function.identity(),
            version -> new BAMME( version, BAMM_VERSIONS.get( version ) ) ) );
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch ( final DatatypeConfigurationException exception ) {
         fail( "Could not instantiate DatatypeFactory", exception );
         throw new RuntimeException( exception );
      }
   }

   @Override
   public BAMM bamm( final KnownVersion metaModelVersion ) {
      return BAMM_VERSIONS.get( metaModelVersion );
   }

   @Override
   public BAMMC bammc( final KnownVersion metaModelVersion ) {
      return BAMMC_VERSIONS.get( metaModelVersion );
   }

   @Override
   public BAMME bamme( final KnownVersion metaModelVersion ) {
      return BAMME_VERSIONS.get( metaModelVersion );
   }

   @Override
   public DatatypeFactory getDatatypeFactory() {
      return datatypeFactory;
   }
}
