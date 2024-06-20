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

package org.eclipse.esmf.aspectmodel.versionupdate.migrator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.UNIT;
import org.eclipse.esmf.samm.KnownVersion;

/**
 * Migrates references to meta model elements and attributes in the unit: namespace to use the samm: namespace instead.
 */
public class UnitInSammNamespaceMigrator extends AbstractUriRewriter {
   private static final KnownVersion OLD = KnownVersion.SAMM_1_0_0;
   private static final KnownVersion NEW = KnownVersion.SAMM_2_0_0;
   private static final SAMM SAMM = new SAMM( NEW );
   private static final UNIT UNIT = new UNIT( NEW, SAMM );

   private final List<String> movedElements = List.of(
         "Unit", "QuantityKind", "quantityKind", "referenceUnit", "commonCode", "conversionFactor", "numericConversionFactor", "symbol" );

   public UnitInSammNamespaceMigrator() {
      super( OLD, NEW );
   }

   @Override
   protected Optional<String> rewriteUri( final String oldUri, final Map<String, String> oldToNewNamespaces ) {
      if ( oldUri.startsWith( UNIT.getUri() ) ) {
         return movedElements.stream().filter( oldUri::endsWith ).findFirst().map( movedElement -> SAMM.getNamespace() + movedElement );
      }
      return Optional.empty();
   }
}
