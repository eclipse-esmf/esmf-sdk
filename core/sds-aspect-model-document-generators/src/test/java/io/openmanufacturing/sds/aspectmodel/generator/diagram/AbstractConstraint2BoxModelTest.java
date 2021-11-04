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

package io.openmanufacturing.sds.aspectmodel.generator.diagram;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.test.MetaModelVersions;

public abstract class AbstractConstraint2BoxModelTest extends MetaModelVersions {

   static Map<KnownVersion, Integer> totalNumberOfExpectedEntriesPerBammVersion;

   final String sparqlQueryFileName = "constraint2boxmodel.sparql";

   protected String boxSelectorStatement( final KnownVersion metaModelVersion ) {
      return ":TestConstraintConstraint a :Box";
   }

   protected String entriesSelectorStatement( final KnownVersion metaModelVersion ) {
      return ":TestConstraintConstraint :entries *";
   }

   @BeforeAll
   public static void setup() {
      totalNumberOfExpectedEntriesPerBammVersion = new HashMap<>();
      totalNumberOfExpectedEntriesPerBammVersion.put( KnownVersion.BAMM_1_0_0, 10 );
      totalNumberOfExpectedEntriesPerBammVersion.put( KnownVersion.BAMM_2_0_0, 10 );
   }
}
