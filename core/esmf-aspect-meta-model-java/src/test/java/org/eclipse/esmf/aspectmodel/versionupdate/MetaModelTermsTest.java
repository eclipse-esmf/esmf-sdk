/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.versionupdate;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.samm.KnownVersion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class MetaModelTermsTest {
   @ParameterizedTest
   @EnumSource( value = KnownVersion.class )
   void testEveryKnownVersionProvidesTerms( final KnownVersion version ) {
      assertThat( MetaModelTerms.definedTerms( version ) )
            .isNotEmpty()
            .contains( metaModelTerm( version, "meta-model", "Aspect" ),
                  metaModelTerm( version, "meta-model", "Property" ),
                  metaModelTerm( version, "characteristic", "Enumeration" ) );
   }

   @Test
   void testValueIsOnlyDefinedFromSamm220() {
      assertThat( MetaModelTerms.isDefinedIn( metaModelTerm( KnownVersion.SAMM_2_0_0, "meta-model", "Value" ),
            KnownVersion.SAMM_2_0_0 ) ).isFalse();
      assertThat( MetaModelTerms.isDefinedIn( metaModelTerm( KnownVersion.SAMM_2_1_0, "meta-model", "Value" ),
            KnownVersion.SAMM_2_1_0 ) ).isFalse();
      assertThat( MetaModelTerms.isDefinedIn( metaModelTerm( KnownVersion.SAMM_2_2_0, "meta-model", "Value" ),
            KnownVersion.SAMM_2_2_0 ) ).isTrue();
   }

   @Test
   void testQuantityEntityIsOnlyDefinedFromSamm220() {
      assertThat( MetaModelTerms.isDefinedIn( metaModelTerm( KnownVersion.SAMM_2_1_0, "entity", "Quantity" ),
            KnownVersion.SAMM_2_1_0 ) ).isFalse();
      assertThat( MetaModelTerms.isDefinedIn( metaModelTerm( KnownVersion.SAMM_2_2_0, "entity", "Quantity" ),
            KnownVersion.SAMM_2_2_0 ) ).isTrue();
   }

   @Test
   void testFirstVersionDefiningTerm() {
      // The URN is given in 2.1.0, where the term does not exist, and must still be found in 2.2.0
      assertThat( MetaModelTerms.firstVersionDefining( metaModelTerm( KnownVersion.SAMM_2_1_0, "meta-model", "Value" ) ) )
            .contains( KnownVersion.SAMM_2_2_0 );
      assertThat( MetaModelTerms.firstVersionDefining( metaModelTerm( KnownVersion.SAMM_2_2_0, "meta-model", "Aspect" ) ) )
            .contains( KnownVersion.SAMM_1_0_0 );
      assertThat( MetaModelTerms.firstVersionDefining( metaModelTerm( KnownVersion.SAMM_2_2_0, "meta-model", "DoesNotExist" ) ) )
            .isEmpty();
   }

   @Test
   void testUserNamespaceElementIsNotMetaModelTerm() {
      assertThat( MetaModelTerms.isMetaModelTerm( AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#TestAspect" ) ) )
            .isFalse();
   }

   private static AspectModelUrn metaModelTerm( final KnownVersion version, final String elementType, final String name ) {
      return AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.samm:%s:%s#%s".formatted( elementType, version.toVersionString(), name ) );
   }
}
