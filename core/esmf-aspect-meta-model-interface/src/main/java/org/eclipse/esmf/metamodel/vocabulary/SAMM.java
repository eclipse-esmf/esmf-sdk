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

import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * The RDF jena vocabulary of the aspect meta model.
 */
// Since the class is an RDF vocabulary, naming rules for the class and for several methods (which should be named identically
// to the corresponding model elements) are suppressed.
@SuppressWarnings( { "checkstyle:AbbreviationAsWordInName", "NewMethodNamingConvention" } )
public class SAMM implements RdfNamespace {
   final KnownVersion metaModelVersion;
   private static final String BASE_URI = "urn:samm:org.eclipse.esmf.samm:";

   public SAMM( final KnownVersion metaModelVersion ) {
      this.metaModelVersion = metaModelVersion;
   }

   @Override
   public String getShortForm() {
      return "samm";
   }

   public String getBaseUri() {
      return BASE_URI;
   }

   @Override
   public String getUri() {
      return getBaseUri() + "meta-model:" + metaModelVersion.toVersionString();
   }

   @Override
   public String getNamespace() {
      return getUri() + "#";
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Namespace() {
      return resource( "Namespace" );
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
    * @return a {@link Resource} for the samm:Property RDF Class definition.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Property() {
      return resource( "Property" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource AbstractProperty() {
      return resource( "AbstractProperty" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Characteristic() {
      return resource( "Characteristic" );
   }

   public Property characteristic() {
      return property( "characteristic" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
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
    * @return a {@link Property} for the samm:property RDF Property definition
    */
   public Property property() {
      return property( "property" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
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

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Operation() {
      return resource( "Operation" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Event() {
      return resource( "Event" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Entity() {
      return resource( "Entity" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource AbstractEntity() {
      return resource( "AbstractEntity" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property _extends() {
      return property( "extends" );
   }

   public Property value() {
      return property( "value" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Unit() {
      return resource( "Unit" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QuantityKind() {
      return resource( "QuantityKind" );
   }

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
