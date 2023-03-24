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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;

public abstract class AbstractConstraint2BoxModelTest extends MetaModelVersions {

   static Map<KnownVersion, Integer> totalNumberOfExpectedEntriesPerMetaModelVersion;

   final String sparqlQueryFileName = "constraint2boxmodel.sparql";

   protected String boxSelectorStatement( final KnownVersion metaModelVersion, final String constraintIdentifier ) {
      if ( metaModelVersion.isNewerThan( KnownVersion.SAMM_1_0_0 ) ) {
         return String.format( "%s a :Box", constraintIdentifier.equals( "*" ) ? "*" : ":" + constraintIdentifier );
      }
      return ":TestConstraintConstraint a :Box";
   }

   protected String entriesSelectorStatement( final KnownVersion metaModelVersion, final String constraintIdentifier ) {
      if ( metaModelVersion.isNewerThan( KnownVersion.SAMM_1_0_0 ) ) {
         return String.format( "%s :entries *", constraintIdentifier.equals( "*" ) ? "*" : ":" + constraintIdentifier );
      }
      return ":TestConstraintConstraint :entries *";
   }

   @BeforeAll
   public static void setup() {
      totalNumberOfExpectedEntriesPerMetaModelVersion = new HashMap<>();
      totalNumberOfExpectedEntriesPerMetaModelVersion.put( KnownVersion.SAMM_1_0_0, 10 );
      totalNumberOfExpectedEntriesPerMetaModelVersion.put( KnownVersion.SAMM_2_0_0, 10 );
   }
}
