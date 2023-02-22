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

package org.eclipse.esmf.aspectmodel.vocabulary;

import java.util.stream.Stream;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import org.eclipse.esmf.samm.KnownVersion;

public class BAMME implements Namespace {
   private final KnownVersion bammVersion;
   private final BAMM bamm;

   public BAMME( final KnownVersion bammVersion, final BAMM bamm ) {
      this.bammVersion = bammVersion;
      this.bamm = bamm;
   }

   @Override
   public String getUri() {
      return bamm.getBaseUri() + "entity:" + bammVersion.toVersionString();
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource TimeSeriesEntity() {
      return resource( "TimeSeriesEntity" );
   }

   @SuppressWarnings( "squid:S00100" ) // Method name should match model element
   public Resource ThreeDimensionalPosition() {
      return resource( "ThreeDimensionalPosition" );
   }

   public Property timestamp() {
      return property( "timestamp" );
   }

   public Property value() {
      return property( "value" );
   }

   public Property x() {
      return property( "x" );
   }

   public Property y() {
      return property( "y" );
   }

   public Property z() {
      return property( "z" );
   }

   public Stream<Resource> allEntities() {
      return Stream.of( TimeSeriesEntity(), ThreeDimensionalPosition() );
   }
}
