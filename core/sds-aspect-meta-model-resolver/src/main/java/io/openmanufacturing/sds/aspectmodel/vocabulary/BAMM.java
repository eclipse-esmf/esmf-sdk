/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.aspectmodel.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

/**
 * The RDF jena vocabulary of the aspect meta model.
 */
public class BAMM implements Namespace {
   final KnownVersion bammVersion;
   private static final String BASE_URI = "urn:bamm:io.openmanufacturing:";

   public BAMM( final KnownVersion bammVersion ) {
      this.bammVersion = bammVersion;
   }

   public String getBaseUri() {
      return BASE_URI;
   }

   @Override
   public String getUri() {
      return getBaseUri() + "meta-model:" + bammVersion.toVersionString();
   }

   @Override
   public String getNamespace() {
      return getUri() + "#";
   }

   public Property listType() {
      return property( "listType" );
   }

   public Property input() {
      return property( "input" );
   }

   public Property output() {
      return property( "output" );
   }

   public Property curie() {
      return property( "curie" );
   }

   public Property description() {
      return property( "description" );
   }

   public Property preferredName() {
      return property( "preferredName" );
   }

   /**
    * @return a {@link Resource} for the bamm:Property RDF Class definition.
    */
   @SuppressWarnings( { "squid:S00100", "squid:S1845" } ) // Method name should match model element
   public Resource Property() {
      return resource( "Property" );
   }

   @SuppressWarnings( { "squid:S00100", "squid:S1845" } ) // Method name should match model element
   public Resource AbstractProperty() {
      return resource( "AbstractProperty" );
   }

   @SuppressWarnings( { "squid:S00100", "squid:S1845" } ) // Method name should match model element
   public Resource Characteristic() {
      return resource( "Characteristic" );
   }

   @SuppressWarnings( { "squid:S1845" } ) // Method name should match model element
   public Property characteristic() {
      return property( "characteristic" );
   }

   public Property baseCharacteristic() {
      return property( "baseCharacteristic" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource Constraint() {
      return resource( "Constraint" );
   }

   public Property dataType() {
      return property( "dataType" );
   }

   public Property exampleValue() {
      return property( "exampleValue" );
   }

   public Property optional() {
      return property( "optional" );
   }

   public Property notInPayload() {
      return property( "notInPayload" );
   }

   public Property payloadName() {
      return property( "payloadName" );
   }

   public Property see() {
      return property( "see" );
   }

   /**
    * @return a {@link Property} for the bamm:property RDF Property definition
    */
   @SuppressWarnings( "squid:S1845" ) // Method name should match model element
   public Property property() {
      return property( "property" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource Aspect() {
      return resource( "Aspect" );
   }

   public Property properties() {
      return property( "properties" );
   }

   public Property parameters() {
      return property( "parameters" );
   }

   public Property operations() {
      return property( "operations" );
   }

   public Property events() {
      return property( "events" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource Operation() {
      return resource( "Operation" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource Event() {
      return resource( "Event" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource Entity() {
      return resource( "Entity" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource AbstractEntity() {
      return resource( "AbstractEntity" );
   }

   public Property _extends() {
      return property( "extends" );
   }

   public Property value() {
      return property( "value" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource Unit() {
      return resource( "Unit" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource QuantityKind() {
      return resource( "QuantityKind" );
   }

   @SuppressWarnings( "squid:S1845" ) // Method name should match model element
   public Property quantityKind() {
      return property( "quantityKind" );
   }

   public Property referenceUnit() {
      return property( "referenceUnit" );
   }

   public Property commonCode() {
      return property( "commonCode" );
   }

   public Property conversionFactor() {
      return property( "conversionFactor" );
   }

   public Property numericConversionFactor() {
      return property( "numericConversionFactor" );
   }

   public Property symbol() {
      return property( "symbol" );
   }
}
