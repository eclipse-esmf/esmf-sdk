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

import static org.eclipse.esmf.metamodel.builder.SammBuilder.*;
import static org.eclipse.esmf.test.AspectModelAsserts.assertThat;

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Elements;
import org.eclipse.esmf.metamodel.Property;

import org.junit.jupiter.api.Test;

public class AssertJ {
   @Test
   void testAssertJ() {
      final AspectModelUrn namespace = AspectModelUrn.fromUrn( "urn:samm:org.example:1.0.0" );
      final Aspect aspect = aspect( namespace.withName( "Aspect" ) )
            .preferredName( "My Aspect" )
            .description( "This is my Aspect" )
            .property( property( namespace.withName( "property" ) )
                  .preferredName( "My Property" )
                  .preferredName( "Meine Property", Locale.GERMAN )
                  .characteristic( singleEntity( namespace.withName( "MySingleEntity" ) )
                        .dataType( entity( namespace.withName( "MyEntity" ) )
                              .property( property( namespace.withName( "myEntityProperty" ) )
                                    .characteristic( Elements.samm_c.Text )
                                    .build() )
                              .build() )
                        .build() )
                  .build() )
            .build();

      // tag::assertj[]
      assertThat( aspect )
            .hasPreferredName( "My Aspect", Locale.ENGLISH )
            .hasDescriptionMatching( description -> description.getValue().contains( "is my" ) )
            .hasSinglePropertyThat()
            .hasSameNamespaceAs( aspect )
            .hasName( "property" )
            .hasPreferredName( "Meine Property", Locale.GERMAN );

      final Property property = aspect.getPropertyByName( "property" ).orElseThrow();
      assertThat( property )
            .characteristic()
            .hasSameNamespaceAs( aspect )
            .hasName( "MySingleEntity" )
            .isSingleEntityThat()
            .dataType()
            .isEntityThat()
            .hasName( "MyEntity" );
      // end::assertj[]
   }
}
