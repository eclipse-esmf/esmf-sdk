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
import org.eclipse.esmf.aspectmodel.vocabulary.SAMMC;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMME;
import org.eclipse.esmf.samm.KnownVersion;

public abstract class PropertyBasedTest implements SammArbitraries {
   private final DatatypeFactory datatypeFactory;
   private final Map<KnownVersion, SAMM> sammVersions;
   private final Map<KnownVersion, SAMMC> sammcVersions;
   private final Map<KnownVersion, SAMME> sammeVersions;

   public PropertyBasedTest() {
      sammVersions = KnownVersion.getVersions().stream().collect( Collectors.toMap( Function.identity(), SAMM::new ) );
      sammcVersions = KnownVersion.getVersions().stream()
            .collect( Collectors.toMap( Function.identity(), SAMMC::new ) );
      sammeVersions = KnownVersion.getVersions().stream().collect( Collectors.toMap( Function.identity(),
            version -> new SAMME( version, sammVersions.get( version ) ) ) );
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch ( final DatatypeConfigurationException exception ) {
         fail( "Could not instantiate DatatypeFactory", exception );
         throw new RuntimeException( exception );
      }
   }

   @Override
   public SAMM samm( final KnownVersion metaModelVersion ) {
      return sammVersions.get( metaModelVersion );
   }

   @Override
   public SAMMC sammc( final KnownVersion metaModelVersion ) {
      return sammcVersions.get( metaModelVersion );
   }

   @Override
   public SAMME samme( final KnownVersion metaModelVersion ) {
      return sammeVersions.get( metaModelVersion );
   }

   @Override
   public DatatypeFactory getDatatypeFactory() {
      return datatypeFactory;
   }
}
