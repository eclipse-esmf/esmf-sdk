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

import java.net.URI;
import java.util.List;

import org.eclipse.esmf.aspectmodel.MetaModelVersionException;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.samm.KnownVersion;

import org.junit.jupiter.api.Test;

class MetaModelVersionCheckTest {
   private static final String PREFIXES_210 = """
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
      @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.1.0#> .
      @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.1.0#> .
      @prefix unit: <urn:samm:org.eclipse.esmf.samm:unit:2.1.0#> .
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
      @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
      """;

   @Test
   void testTermMissingInDeclaredVersionIsReported() {
      final List<String> findings = check( PREFIXES_210 + """
         :TestValue a samm:Value ;
            samm:value "test" .
         """, KnownVersion.SAMM_2_1_0 );

      assertThat( findings ).singleElement().satisfies( finding -> {
         assertThat( finding ).contains( "urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#Value" );
         assertThat( finding )
               .contains( "is not defined in SAMM 2.1.0" )
               .contains( "introduced in 2.2.0" )
               .contains( "at least 2.2.0" );
      } );
   }

   @Test
   void testTermMissingInDeclaredVersionIsReportedForEntityNamespace() {
      final List<String> findings = check( PREFIXES_210 + """
         :TestCharacteristic a samm-c:SingleEntity ;
            samm:dataType samm-e:Quantity .
         """, KnownVersion.SAMM_2_1_0 );

      assertThat( findings ).singleElement()
            .satisfies( finding -> assertThat( finding ).contains( "urn:samm:org.eclipse.esmf.samm:entity:2.1.0#Quantity" ) );
   }

   @Test
   void testCorrectlyDeclaredModelProducesNoFindings() {
      assertThat( check( PREFIXES_210 + """
         :TestAspect a samm:Aspect ;
            samm:properties ( :testProperty ) ;
            samm:operations ( ) ;
            samm:events ( ) .

         :testProperty a samm:Property ;
            samm:characteristic :TestCharacteristic .

         :TestCharacteristic a samm-c:Text .
         """, KnownVersion.SAMM_2_1_0 ) ).isEmpty();
   }

   @Test
   void testUserNamespaceElementsAreNotChecked() {
      assertThat( check( PREFIXES_210 + """
         :SomethingThatDoesNotExistInTheMetaModel a samm:Property .
         """, KnownVersion.SAMM_2_1_0 ) ).isEmpty();
   }

   @Test
   void testTermUnknownToEveryVersionIsNotReported() {
      // A term no meta model version defines is either a typo, for which SHACL produces a more specific
      // message, or an element the model legitimately adds to a SAMM namespace.
      assertThat( check( PREFIXES_210 + """
         :TestCharacteristic a samm-c:StructuredValue ;
            samm-c:components ( :year ) .
         """, KnownVersion.SAMM_2_1_0 ) ).isEmpty();

      assertThat( check( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.2.0#> .

         samm-e:sharedProperty a samm:Property ;
            samm:characteristic samm-c:Text .
         """, KnownVersion.SAMM_2_2_0 ) ).isEmpty();
   }

   @Test
   void testTermRemovedInLaterVersionIsNotReported() {
      // samm:name exists in SAMM 1.0.0 and was removed afterwards. SammRemoveSammNameMigrator drops it
      // during migration, so reporting it here would both be wrong and make legacy BAMM models
      // unloadable. Only terms from a newer version indicate a wrong version declaration.
      assertThat( check( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

         :TestAspect a samm:Aspect ;
            samm:name "TestAspect" .
         """, KnownVersion.SAMM_2_2_0 ) ).isEmpty();
   }

   @Test
   void testUnitsAreNotChecked() {
      assertThat( check( PREFIXES_210 + """
         :TestCharacteristic a samm-c:Measurement ;
            samm-c:unit unit:aNameThatIsNotInTheInventory .
         """, KnownVersion.SAMM_2_1_0 ) ).isEmpty();
   }

   @Test
   void testMixedMetaModelVersionsAreReported() {
      final List<String> findings = check( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

         :TestCharacteristic a samm-c:Text .
         """, KnownVersion.SAMM_2_1_0 );

      assertThat( findings ).singleElement().satisfies( finding -> {
         assertThat( finding ).contains( "urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#Text" );
         assertThat( finding )
               .contains( "declares SAMM 2.1.0" )
               .contains( "from SAMM 2.2.0" );
      } );
   }

   @Test
   void testMixedMetaModelVersionsAreReportedWhenDeclaringLatest() {
      final List<String> findings = check( """
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.1.0#> .
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

         :TestCharacteristic a samm-c:Text .
         """, KnownVersion.SAMM_2_2_0 );

      assertThat( findings ).singleElement().satisfies(
            finding -> assertThat( finding ).contains( "urn:samm:org.eclipse.esmf.samm:characteristic:2.1.0#Text" ) );
   }

   @Test
   void testTermIsReportedOnlyOncePerElement() {
      final List<String> findings = check( PREFIXES_210 + """
         :FirstValue a samm:Value ;
            samm:value "one" .

         :SecondValue a samm:Value ;
            samm:value "two" .
         """, KnownVersion.SAMM_2_1_0 );

      assertThat( findings ).hasSize( 1 );
   }

   private List<String> check( final String turtle, final KnownVersion declaredVersion ) {
      return TurtleLoader.loadTurtle( turtle, URI.create( "file:Test.ttl" ) ).toJavaStream()
            .flatMap( model -> MetaModelVersionCheck.check( model, declaredVersion, URI.create( "file:Test.ttl" ) ).stream() )
            .map( MetaModelVersionException.Problem::message )
            .toList();
   }
}
