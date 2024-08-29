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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

class AspectModelUrnTest {
   private final String baseUri = "urn:samm:org.eclipse.esmf.test:";
   private final String sammBaseUri = "urn:samm:org.eclipse.esmf.samm:";

   @Test
   void createFromValidUrn() throws URISyntaxException {
      URI validUrn = new URI( baseUri + "1.0.0#Errors" );
      AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn( validUrn );

      assertModelElementUrn( aspectModelUrn, "Errors", "org.eclipse.esmf.test" );

      validUrn = new URI( baseUri + "1.0.0#E2" );
      aspectModelUrn = AspectModelUrn.fromUrn( validUrn );

      assertModelElementUrn( aspectModelUrn, "E2", "org.eclipse.esmf.test" );
   }

   @Test
   void createFromValidUrnMaxLength() throws URISyntaxException {
      final String namespace = Strings.repeat( "x", 62 );
      final URI validUrn = new URI(
            "urn:samm:" + namespace + ".test:1.0.0#Errors" );
      final AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn( validUrn );

      assertModelElementUrn( aspectModelUrn, "Errors", namespace + ".test" );
   }

   @Test
   void createFromNull() {
      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( (URI) null ) )
            .withMessage( UrnSyntaxException.URN_IS_NULL_MESSAGE );
   }

   @Test
   void createFromUrnMissingSections() throws URISyntaxException {
      final URI invalidUrn = new URI( baseUri );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( invalidUrn ) )
            .withMessage( UrnSyntaxException.URN_IS_MISSING_SECTIONS_MESSAGE );
   }

   @Test
   void createFromUrnInvalidProtocol() throws URISyntaxException {
      final String errorMessage = MessageFormat
            .format( UrnSyntaxException.URN_INVALID_PROTOCOL_MESSAGE, AspectModelUrn.VALID_PROTOCOL );

      final URI invalidUrn = new URI( "http:samm:org.eclipse.esmf.test:aspect-model:Errors:1.0.0" );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( invalidUrn ) )
            .withMessage( errorMessage );
   }

   @Test
   void createFromUrnInvalidNamespaceIdentifier() throws URISyntaxException {
      final String errorMessage = MessageFormat
            .format( UrnSyntaxException.URN_INVALID_NAMESPACE_IDENTIFIER_MESSAGE,
                  AspectModelUrn.VALID_NAMESPACE_IDENTIFIER );

      final URI invalidUrn = new URI( "urn:invalid-identifier:org.eclipse.esmf.test:aspect-model:Errors:1.0.0" );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( invalidUrn ) )
            .withMessage( errorMessage );
   }

   @Test
   void createFromUrnInvalidNamespace() throws URISyntaxException {
      final String errorMessage = MessageFormat
            .format( UrnSyntaxException.URN_INVALID_NAMESPACE_MESSAGE, AspectModelUrn.NAMESPACE_REGEX );

      final URI invalidUrn = new URI( "urn:samm:org.eclipse.esmf.samm.tes$t:aspect-model:Errors:1.0.0" );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( invalidUrn ) )
            .withMessage( errorMessage );
   }

   @Test
   void createFromUrnInvalidAspectName() throws URISyntaxException {
      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( sammBaseUri + "meta-model:1.0.0#Aspe?ct" ) ) )
            .withMessage( "The meta model element name must match \\p{Alpha}\\p{Alnum}*: Aspe?ct" );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( sammBaseUri + "characteristic:1.0.0#Eit?her" ) ) )
            .withMessage( "The characteristic name must match \\p{Alpha}\\p{Alnum}*: Eit?her" );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( sammBaseUri + "entity:1.0.0#Time?Series" ) ) )
            .withMessage( "The entity name must match \\p{Alpha}\\p{Alnum}*: Time?Series" );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "unit:1.0.0#lit?re" ) ) )
            .withMessage( "The unit name must match \\p{Alpha}\\p{Alnum}*: lit?re" );
   }

   @Test
   void createFromUrnEmptyString() throws URISyntaxException {
      final String errorMessage = MessageFormat
            .format( UrnSyntaxException.URN_IS_MISSING_SECTIONS_MESSAGE, AspectModelUrn.NAMESPACE_REGEX );
      final URI emptyUrn = new URI( "" );
      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( emptyUrn ) )
            .withMessage( errorMessage );
   }

   @Test
   void createFromUrnTooLongString() throws URISyntaxException {
      final String errorMessage = MessageFormat
            .format( MessageFormat.format( UrnSyntaxException.URN_IS_TOO_LONG, AspectModelUrn.MAX_URN_LENGTH ),
                  AspectModelUrn.NAMESPACE_REGEX );

      final String tooLongString = Strings.repeat( "x", 500 );
      final URI tooLongUrn = new URI(
            "urn:samm:org.eclipse.esmf.samm" + tooLongString + ":aspect-model:Errors:1.0.0" );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( tooLongUrn ) )
            .withMessage( errorMessage );
   }

   @Test
   void createFromUrnInvalidElementType() throws URISyntaxException {
      final URI invalidUrn = new URI( baseUri + "some-model:Errors:1.0.0" );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( invalidUrn ) )
            .withMessage( MessageFormat.format( UrnSyntaxException.URN_INVALID_VERSION, "some-model" ) );
   }

   @Test
   void createFromUrnWithInvalidVersion() {
      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "aspect-model:Errors:1.b.0" ) ) )
            .withMessage( "Invalid version in URN: 1.b.0." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "aspect-model:Errors:1.1.0.0" ) ) )
            .withMessage( "Invalid version in URN: 1.1.0.0." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "aspect-model:Errors:version" ) ) )
            .withMessage( "Invalid version in URN: version." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "aspect-model:Errors:1.0" ) ) )
            .withMessage( "Invalid version in URN: 1.0." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "characteristic:TestCharacteristic:1.0" ) ) )
            .withMessage( "Invalid version in URN: 1.0." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( sammBaseUri + "characteristic:1.0#Either" ) ) )
            .withMessage( "Invalid version in URN: 1.0." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "entity:TestEntity:1.0" ) ) )
            .withMessage( "Invalid version in URN: 1.0." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( sammBaseUri + "entity:1.0#TimeSeries" ) ) )
            .withMessage( "Invalid version in URN: 1.0." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( sammBaseUri + "meta-model:1.0#Aspect" ) ) )
            .withMessage( "Invalid version in URN: 1.0." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "unit:1.1#litre" ) ) )
            .withMessage( "Invalid version in URN: 1.1." );

      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( new URI( baseUri + "unit:litre:1.1" ) ) )
            .withMessage( "Invalid version in URN: 1.1." );
   }

   private void assertModelElementUrn( final AspectModelUrn aspectModelUrn, final String name,
         final String nameSpace ) {
      assertThat( aspectModelUrn.getName() ).isEqualTo( name );
      assertThat( aspectModelUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( aspectModelUrn.getNamespaceMainPart() ).isEqualTo( nameSpace );
   }

   @Test
   void createUrnForModelElement() throws URISyntaxException {
      final URI validUrn = new URI( baseUri + "aspect-model:Errors:1.0.0#property" );
      final AspectModelUrn elementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( elementUrn.getName() ).isEqualTo( "property" );
      assertThat( elementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( elementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.test" );
      assertThat( elementUrn.getElementType() ).isEqualTo( ElementType.ASPECT_MODEL_ELEMENT );
      assertThat( elementUrn.isSammUrn() ).isFalse();
   }

   @Test
   void createUrnForSammCharacteristic() throws URISyntaxException {
      final URI validUrn = new URI( sammBaseUri + "characteristic:1.0.0#Either" );
      final AspectModelUrn metaModelElementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( metaModelElementUrn.getName() ).isEqualTo( "Either" );
      assertThat( metaModelElementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( metaModelElementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.samm" );
      assertThat( metaModelElementUrn.getElementType() ).isEqualTo( ElementType.CHARACTERISTIC );
      assertThat( metaModelElementUrn.isSammUrn() ).isTrue();
   }

   @Test
   void createUrnForCharacteristic() throws URISyntaxException {
      final URI validUrn = new URI( baseUri + "characteristic:1.0.0#TestCharacteristic" );
      final AspectModelUrn elementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( elementUrn.getName() ).isEqualTo( "TestCharacteristic" );
      assertThat( elementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( elementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.test" );
      assertThat( elementUrn.getElementType() ).isEqualTo( ElementType.CHARACTERISTIC );
      assertThat( elementUrn.isSammUrn() ).isTrue();
   }

   @Test
   void createUrnForCharacteristicModelElement() throws URISyntaxException {
      final URI validUrn = new URI( baseUri + "characteristic:EitherTestCharacteristic:1.0.0#RightCharacteristic" );
      final AspectModelUrn elementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( elementUrn.getName() ).isEqualTo( "RightCharacteristic" );
      assertThat( elementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( elementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.test" );
      assertThat( elementUrn.getElementType() ).isEqualTo( ElementType.CHARACTERISTIC_MODEL_ELEMENT );
      assertThat( elementUrn.isSammUrn() ).isFalse();
   }

   @Test
   void createUrnForSammEntity() throws URISyntaxException {
      final URI validUrn = new URI( sammBaseUri + "entity:1.0.0#TimeSeriesEntity" );
      final AspectModelUrn metaModelElementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( metaModelElementUrn.getName() ).isEqualTo( "TimeSeriesEntity" );
      assertThat( metaModelElementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( metaModelElementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.samm" );
      assertThat( metaModelElementUrn.getElementType() ).isEqualTo( ElementType.ENTITY );
      assertThat( metaModelElementUrn.isSammUrn() ).isTrue();
   }

   @Test
   void createUrnForSammEntityProperty() throws URISyntaxException {
      final URI validUrn = new URI( sammBaseUri + "entity:1.0.0#value" );
      final AspectModelUrn metaModelElementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( metaModelElementUrn.getName() ).isEqualTo( "value" );
      assertThat( metaModelElementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( metaModelElementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.samm" );
      assertThat( metaModelElementUrn.getElementType() ).isEqualTo( ElementType.ENTITY );
      assertThat( metaModelElementUrn.isSammUrn() ).isTrue();
   }

   @Test
   void createUrnForEntity() throws URISyntaxException {
      final URI validUrn = new URI( baseUri + "entity:1.0.0#TestEntity" );
      final AspectModelUrn elementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( elementUrn.getName() ).isEqualTo( "TestEntity" );
      assertThat( elementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( elementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.test" );
      assertThat( elementUrn.getElementType() ).isEqualTo( ElementType.ENTITY );
      assertThat( elementUrn.isSammUrn() ).isTrue();
   }

   @Test
   void createUrnForEntityProperty() throws URISyntaxException {
      final URI validUrn = new URI( baseUri + "entity:TestEntity:1.0.0#property" );
      final AspectModelUrn elementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( elementUrn.getName() ).isEqualTo( "property" );
      assertThat( elementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( elementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.test" );
      assertThat( elementUrn.getElementType() ).isEqualTo( ElementType.ENTITY_MODEL_ELEMENT );
      assertThat( elementUrn.isSammUrn() ).isFalse();
   }

   @Test
   void createUrnForSammUnit() throws URISyntaxException {
      final URI validUrn = new URI( sammBaseUri + "unit:1.0.0#litre" );
      final AspectModelUrn metaModelElementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( metaModelElementUrn.getName() ).isEqualTo( "litre" );
      assertThat( metaModelElementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( metaModelElementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.samm" );
      assertThat( metaModelElementUrn.getElementType() ).isEqualTo( ElementType.UNIT );
      assertThat( metaModelElementUrn.isSammUrn() ).isTrue();
   }

   @Test
   void createUrnForUnit() throws URISyntaxException {
      final URI validUrn = new URI( baseUri + "unit:litre:1.0.0" );
      final AspectModelUrn elementUrn = AspectModelUrn.fromUrn( validUrn );

      assertThat( elementUrn.getName() ).isEqualTo( "litre" );
      assertThat( elementUrn.getVersion() ).isEqualTo( "1.0.0" );
      assertThat( elementUrn.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf.test" );
      assertThat( elementUrn.getElementType() ).isEqualTo( ElementType.UNIT );
      assertThat( elementUrn.isSammUrn() ).isFalse();
   }

   @Test
   void validNamespaceTest() throws URISyntaxException {
      final URI validNamespaceUrnUnderscore = new URI( "urn:samm:org.eclipse.esmf_test:0.0.1#TestAspect" );
      final AspectModelUrn elementUrnWithUnderscore = AspectModelUrn.fromUrn( validNamespaceUrnUnderscore );
      assertThat( elementUrnWithUnderscore.getNamespaceMainPart() ).isNotEmpty();
      assertThat( elementUrnWithUnderscore.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf_test" );

      final URI invalidNamespaceUrnDash = new URI( "urn:samm:org.eclipse.esmf-test:0.0.1#TestAspect" );
      final AspectModelUrn elementUrnWithDash = AspectModelUrn.fromUrn( invalidNamespaceUrnDash );
      assertThat( elementUrnWithDash.getNamespaceMainPart() ).isNotEmpty();
      assertThat( elementUrnWithDash.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf-test" );
   }

   @Test
   void invalidNamespaceTest() throws URISyntaxException {
      final URI validNamespaceUrnUnderscore = new URI( "urn:samm:com.bosch.nexeed.digitaltwin:aspect-model:Er?ors:1.1.0#TestAspect" );
      final AspectModelUrn elementUrnWithUnderscore = AspectModelUrn.fromUrn( validNamespaceUrnUnderscore );
      assertThat( elementUrnWithUnderscore.getNamespaceMainPart() ).isNotEmpty();
      assertThat( elementUrnWithUnderscore.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf_test" );

      final URI invalidNamespaceUrnDash = new URI( "urn:samm:org.eclipse.esmf-test:0.0.1#TestAspect" );
      final AspectModelUrn elementUrnWithDash = AspectModelUrn.fromUrn( invalidNamespaceUrnDash );
      assertThat( elementUrnWithDash.getNamespaceMainPart() ).isNotEmpty();
      assertThat( elementUrnWithDash.getNamespaceMainPart() ).isEqualTo( "org.eclipse.esmf-test" );
   }

   @Test
   void invalidModelElementNameTest() throws URISyntaxException {
      final URI invalidRootModelElementName = new URI( sammBaseUri + "aspect-model:Er?ors:1.1.0#TestAspect" );
      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( invalidRootModelElementName ) )
            .withMessage( "The model element name must match \\p{Alpha}\\p{Alnum}*: Er?ors" );

      final URI invalidModelElementName = new URI( sammBaseUri + "aspect-model:Er?ors:1.1.0:dummy#TestAspect" );
      assertThatExceptionOfType( UrnSyntaxException.class )
            .isThrownBy( () -> AspectModelUrn.fromUrn( invalidModelElementName ) )
            .withMessage( "The model element name must match \\p{Alpha}\\p{Alnum}*: Er?ors" );
   }
}
