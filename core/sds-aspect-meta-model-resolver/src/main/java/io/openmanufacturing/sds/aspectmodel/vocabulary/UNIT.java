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

public class UNIT implements Namespace {
   private final KnownVersion bammVersion;
   private final BAMM bamm;

   public UNIT( final KnownVersion bammVersion, final BAMM bamm ) {
      this.bammVersion = bammVersion;
      this.bamm = bamm;
   }

   @Override
   public String getUri() {
      return bamm.getBaseUri() + "unit:" + bammVersion.toVersionString();
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
