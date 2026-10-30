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

package org.eclipse.esmf.metamodel.vocabulary;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

// Since the class is an RDF vocabulary, naming rules for the class and for several methods (which
// should be named identically
// to the corresponding model elements) are suppressed.
@SuppressWarnings( { "checkstyle:AbbreviationAsWordInName", "NewMethodNamingConvention" } )
public class SAMME implements RdfNamespace {
   private final KnownVersion metaModelVersion;
   private final SAMM samm;

   public SAMME( final KnownVersion metaModelVersion, final SAMM samm ) {
      this.metaModelVersion = metaModelVersion;
      this.samm = samm;
   }

   @Override
   public String getShortForm() {
      return "samm-e";
   }

   @Override
   public String getUri() {
      return samm.getBaseUri() + "entity:" + metaModelVersion.toVersionString();
   }

   /*
    * Resource constants
    */
   public static final String TIME_SERIES_ENTITY = "TimeSeriesEntity";
   public static final String POINT_3D = "Point3d";
   public static final String FILE_RESOURCE = "FileResource";
   public static final String QUANTITY = "Quantity";

   /*
    * Property constants
    */
   public static final String RESOURCE = "resource";
   public static final String MIME_TYPE = "mimeType";
   public static final String TIMESTAMP = "timestamp";
   public static final String VALUE = "value";
   public static final String X = "x";
   public static final String Y = "y";
   public static final String Z = "z";
   public static final String UNIT = "unit";

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource TimeSeriesEntity() {
      return resource( TIME_SERIES_ENTITY );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Point3d() {
      return resource( POINT_3D );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource FileResource() {
      return resource( FILE_RESOURCE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Quantity() {
      return resource( QUANTITY );
   }

   public Property resource() {
      return property( RESOURCE );
   }

   public Property mimeType() {
      return property( MIME_TYPE );
   }

   public Property timestamp() {
      return property( TIMESTAMP );
   }

   public Property value() {
      return property( VALUE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property x() {
      return property( X );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property y() {
      return property( Y );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property z() {
      return property( Z );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property unit() {
      return property( UNIT );
   }

   public Stream<Resource> allEntities() {
      return allResources().stream();
   }

   @Override
   public List<Resource> allResources() {
      return List.of( TimeSeriesEntity(), Point3d(), FileResource(), Quantity() );
   }

   @Override
   public List<Property> allProperties() {
      return List.of( resource(), mimeType(), timestamp(), value(), x(), y(), z(), unit() );
   }
}
