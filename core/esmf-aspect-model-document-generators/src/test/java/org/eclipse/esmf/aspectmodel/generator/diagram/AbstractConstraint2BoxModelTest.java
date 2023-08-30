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

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;

import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractConstraint2BoxModelTest extends MetaModelVersions {

   static Map<KnownVersion, Integer> totalNumberOfExpectedEntriesPerMetaModelVersion;

   final String sparqlQueryFileName = "constraint2boxmodel.sparql";

   protected String boxSelectorStatement( final String constraintIdentifier,
         final boolean isConstraintAnonymous ) {
      final String ns = isConstraintAnonymous ? ":" : "test:";
      return String.format( "%s a :Box", constraintIdentifier.equals( "*" ) ? "*" : ns + constraintIdentifier );
   }

   protected String entriesSelectorStatement( final String constraintIdentifier,
         final boolean isConstraintAnonymous ) {
      final String ns = isConstraintAnonymous ? ":" : "test:";
      return String.format( "%s :entries *", constraintIdentifier.equals( "*" ) ? "*" : ns + constraintIdentifier );
   }

   @BeforeAll
   public static void setup() {
      totalNumberOfExpectedEntriesPerMetaModelVersion = new HashMap<>();
      totalNumberOfExpectedEntriesPerMetaModelVersion.put( KnownVersion.SAMM_1_0_0, 10 );
      totalNumberOfExpectedEntriesPerMetaModelVersion.put( KnownVersion.SAMM_2_0_0, 10 );
      totalNumberOfExpectedEntriesPerMetaModelVersion.put( KnownVersion.SAMM_2_1_0, 10 );
   }
}
