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

import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

// Since the class is an RDF vocabulary, naming rules for the class and for several methods (which should be named identically
// to the corresponding model elements) are suppressed.
@SuppressWarnings( { "checkstyle:AbbreviationAsWordInName", "NewMethodNamingConvention" } )
public class SAMME implements Namespace {
   private final KnownVersion metaModelVersion;
   private final SAMM samm;

   public SAMME( final KnownVersion metaModelVersion, final SAMM samm ) {
      this.metaModelVersion = metaModelVersion;
      this.samm = samm;
   }

   @Override
   public String getUri() {
      return samm.getBaseUri() + "entity:" + metaModelVersion.toVersionString();
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource TimeSeriesEntity() {
      return resource( "TimeSeriesEntity" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ThreeDimensionalPosition() {
      return resource( "ThreeDimensionalPosition" );
   }

   public Property timestamp() {
      return property( "timestamp" );
   }

   public Property value() {
      return property( "value" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property x() {
      return property( "x" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property y() {
      return property( "y" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property z() {
      return property( "z" );
   }

   public Stream<Resource> allEntities() {
      return Stream.of( TimeSeriesEntity(), ThreeDimensionalPosition() );
   }
}
