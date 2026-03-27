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

package org.eclipse.esmf.metamodel.builder;

import static org.eclipse.esmf.metamodel.DataTypes.xsd;
import static org.eclipse.esmf.metamodel.Elements.samm_c;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.aspect;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.entity;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.entityInstance;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.enumeration;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.property;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.value;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.values;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Property;

import org.junit.jupiter.api.Test;

public class SammBuilderTest {
   @Test
   void testBuilder() {
      final AspectModelUrn namespace = AspectModelUrn.fromUrn( "urn:samm:org.example:1.0.0" );
      aspect( namespace.withName( "Aspect" ) )
            .preferredName( "My Aspect" )
            .property( property( namespace.withName( "property" ) )
                  .preferredName( "My Property" )
                  .characteristic( samm_c.Text )
                  .build() )
            .build();

      enumeration()
            .dataType( xsd.int_ )
            .preferredName( "my enum" )
            .values( values( xsd.int_, 1, 2, 3 ) )
            .build();
      final Property entityProperty1 = property( namespace.withName( "entityProperty1" ) )
            .characteristic( samm_c.Text )
            .build();
      final Property entityProperty2 = property( namespace.withName( "entityProperty1" ) )
            .characteristic( samm_c.UnitReference )
            .build();
      final Entity entity = entity( namespace.withName( "MyEntity" ) )
            .preferredName( "My Entity" )
            .property( entityProperty1 )
            .property( entityProperty2 )
            .build();

      enumeration()
            .dataType( entity )
            .value( entityInstance( namespace.withName( "value1" ) )
                  .dataType( entity )
                  .propertyAssertion( entityProperty1, value( "hello world" ) )
                  .propertyAssertion( entityProperty2, value( "unit:metre" ) )
                  .build() )
            .value( entityInstance( namespace.withName( "value2" ) )
                  .dataType( entity )
                  .propertyAssertion( entityProperty1, value( "hello universe" ) )
                  .propertyAssertion( entityProperty2, value( "unit:kilometre" ) )
                  .build() )
            .build();
   }
}
