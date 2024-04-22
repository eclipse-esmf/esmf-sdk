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

package org.eclipse.esmf.aspectmodel.urn;

import org.eclipse.esmf.test.shared.arbitraries.AspectModelUrnArbitraries;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

/**
 * Property-based tests for AspectModelUrn
 */
public class AspectModelUrnPropertyTest implements AspectModelUrnArbitraries {
   private boolean isValidUrn( final String urn ) {
      try {
         AspectModelUrn.fromUrn( urn );
      } catch ( final Exception e ) {
         return false;
      }
      return true;
   }

   @Property
   public boolean notAllStringsAreValidUrns( @ForAll final String string ) {
      return !isValidUrn( string );
   }

   @Property
   public boolean allValidModelStringsAreValidAspectModelUrns( @ForAll( "anyMetaModelElementUrn" ) final String aspectModelUrn ) {
      return isValidUrn( aspectModelUrn );
   }

   @Property
   public boolean allValidMetaModelStringsAreValidAspectModelUrns( @ForAll( "anyModelElementUrn" ) final String aspectModelUrn ) {
      return isValidUrn( aspectModelUrn );
   }
}
