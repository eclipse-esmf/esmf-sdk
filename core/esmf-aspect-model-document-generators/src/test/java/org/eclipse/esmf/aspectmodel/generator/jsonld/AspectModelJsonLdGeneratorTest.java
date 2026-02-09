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
package org.eclipse.esmf.aspectmodel.generator.jsonld;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;

class AspectModelJsonLdGeneratorTest {
   @Test
   void generateTest() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY_LIST ).aspect();
      final AspectModelToJsonLdGenerator jsonGenerator = new AspectModelToJsonLdGenerator( aspect );
      final String generatedJsonLd = jsonGenerator.generateJson();

      assertJsonLdMeta( generatedJsonLd, aspect.urn() );
   }

   private void assertJsonLdMeta( final String generatedJsonLd, final AspectModelUrn aspectModelUrn ) {
      assertThat( generatedJsonLd ).contains( "\"@graph\" : [" );
      assertThat( generatedJsonLd ).contains( "\"xsd\" : \"http://www.w3.org/2001/XMLSchema#\"," );
      assertThat( generatedJsonLd ).contains(
            "\"samm-c\" : \"urn:samm:org.eclipse.esmf.samm:characteristic:" + KnownVersion.getLatest().toVersionString() + "#\"" );
      assertThat( generatedJsonLd ).contains(
            "\"samm\" : \"urn:samm:org.eclipse.esmf.samm:meta-model:" + KnownVersion.getLatest().toVersionString() + "#\"" );
      assertThat( generatedJsonLd ).contains( "\"@vocab\" : \"urn:samm:org.eclipse.esmf.test:1.0.0#\"" );
      assertThat( generatedJsonLd ).contains( String.format( "\"@id\" : \"%s\"", aspectModelUrn.toString() ) );
   }
}
