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

package org.eclipse.esmf.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.assertj.core.api.AbstractAssert;

/**
 * Assert for {@link AspectModelUrn}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class AspectModelUrnAssert<SELF extends AspectModelUrnAssert<SELF, ACTUAL>, ACTUAL extends AspectModelUrn>
      extends AbstractAssert<SELF, ACTUAL> {
   public AspectModelUrnAssert( final ACTUAL aspectModelUrn ) {
      super( aspectModelUrn, AspectModelUrnAssert.class );
   }

   public SELF hasName( final String name ) {
      assertThat( actual.getName() ).isEqualTo( name );
      return myself;
   }

   public SELF hasVersion( final String version ) {
      assertThat( actual.getVersion() ).isEqualTo( version );
      return myself;
   }

   public SELF hasVersion( final VersionNumber version ) {
      return hasVersion( version.toString() );
   }

   public SELF hasNamespaceMainPart( final String namespacemainPart ) {
      assertThat( actual.getNamespaceMainPart() ).isEqualTo( namespacemainPart );
      return myself;
   }
}
