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

package org.eclipse.esmf.metamodel.vocabulary;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.Test;

class SammTest {
   private final SAMM samm = SammNs.SAMM;

   @Test
   void allResourcesContainsOnlyTheSammClasses() {
      assertThat( samm.allResources() ).extracting( Resource::getLocalName ).containsExactly(
            "Namespace", "Property", "AbstractProperty", "Characteristic", "Constraint", "Aspect", "Operation", "Event",
            "Entity", "AbstractEntity", "Value", "Unit", "QuantityKind" );
   }

   @Test
   void encodingsAreNotPartOfAllResources() {
      assertThat( samm.allEncodings() ).extracting( Resource::getLocalName )
            .containsExactly( "UTF-8", "US-ASCII", "ISO-8859-1", "UTF-16", "UTF-16BE", "UTF-16LE" );
      assertThat( samm.allResources() ).doesNotContainAnyElementsOf( samm.allEncodings() );
   }
}
