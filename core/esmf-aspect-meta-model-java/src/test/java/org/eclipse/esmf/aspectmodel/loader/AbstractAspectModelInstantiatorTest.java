/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

public abstract class AbstractAspectModelInstantiatorTest extends MetaModelVersions {
   Aspect loadAspect( final TestAspect aspectName ) {
      return TestResources.load( aspectName ).aspect();
   }

   void assertBaseAttributes( final ModelElement base, final AspectModelUrn expectedAspectModelUrn, final String expectedName,
         final String expectedPreferredName, final String expectedDescription, final String... expectedSee ) {
      assertThat( base.getName() ).isEqualTo( expectedName );
      assertThat( base.urn() ).isEqualTo( expectedAspectModelUrn );
      assertBaseAttributes( base, expectedPreferredName, expectedDescription, expectedSee );
   }

   void assertBaseAttributes( final ModelElement base, final String expectedPreferredName, final String expectedDescription,
         final String... expectedSee ) {
      if ( expectedPreferredName != null ) {
         assertThat( base.getPreferredName( Locale.ENGLISH ) ).isEqualTo( expectedPreferredName );
      }
      if ( expectedDescription != null ) {
         assertThat( base.getDescription( Locale.ENGLISH ) ).isEqualTo( expectedDescription );
      }
      assertThat( base.getSee() ).containsOnly( expectedSee );
   }
}
