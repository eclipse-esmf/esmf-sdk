/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package examples;

// tag::imports[]
import static org.eclipse.esmf.metamodel.DataTypes.*;
import static org.eclipse.esmf.metamodel.Elements.*;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.*;

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
// end::imports[]

import org.junit.jupiter.api.Test;

public class SammBuilderDemo {
   @Test
   void testSammBuilder() {
      // tag::sammBuilder[]
      final AspectModelUrn namespace = AspectModelUrn.fromUrn( "urn:samm:org.example:1.0.0" );
      final Aspect aspect = aspect( namespace.withName( "Aspect" ) )
            .preferredName( "My Aspect" )
            .property( property( namespace.withName( "property" ) )
                  .preferredName( "My Property" )
                  .description( "This is a test property" )
                  .characteristic( samm_c.Text )
                  .build() )
            .property( property( namespace.withName( "mySecondProperty" ) )
                  .preferredName( "My second Property", Locale.ENGLISH )
                  .description( "This is my second test property", Locale.ENGLISH )
                  .characteristic( trait( namespace.withName( "MyTrait" ) )
                        .preferredName( "My Trait" )
                        .baseCharacteristic( characteristic()
                              .preferredName( "Custom characteristic" )
                              .dataType( xsd.integer )
                              .build() )
                        .constraint( regularExpressionConstraint()
                              .value( "a.*b" )
                              .build() )
                        .build() )
                  .build() )
            .build();
      // end::sammBuilder[]
   }
}
