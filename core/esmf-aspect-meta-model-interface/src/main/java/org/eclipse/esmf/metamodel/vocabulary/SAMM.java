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

import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * The RDF jena vocabulary of the aspect meta model.
 */
// Since the class is an RDF vocabulary, naming rules for the class and for several methods (which
// should be named identically
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

   /*
    * Resource constants (classes, encodings)
    */
   public static final String NAMESPACE = "Namespace";
   public static final String ASPECT = "Aspect";
   public static final String OPERATION = "Operation";
   public static final String EVENT = "Event";
   public static final String ENTITY = "Entity";
   public static final String ABSTRACT_ENTITY = "AbstractEntity";
   public static final String VALUE_CLASS = "Value";
   public static final String UNIT = "Unit";
   public static final String QUANTITY_KIND_CLASS = "QuantityKind";
   public static final String UTF_8 = "UTF-8";
   public static final String US_ASCII = "US-ASCII";
   public static final String ISO_8859_1 = "ISO-8859-1";
   public static final String UTF_16 = "UTF-16";
   public static final String UTF_16BE = "UTF-16BE";
   public static final String UTF_16LE = "UTF-16LE";

   /*
    * Disambiguated resource classes where property with same name exists
    */
   public static final String PROPERTY_CLASS = "Property";
   public static final String ABSTRACT_PROPERTY = "AbstractProperty";
   public static final String CHARACTERISTIC_CLASS = "Characteristic";
   public static final String CONSTRAINT_CLASS = "Constraint";

   /*
    * Property constants (predicates)
    */
   public static final String CHARACTERISTIC = "characteristic";
   public static final String PROPERTY = "property";
   public static final String DATA_TYPE = "dataType";
   public static final String OPTIONAL = "optional";
   public static final String NOT_IN_PAYLOAD = "notInPayload";
   public static final String VALUE = "value";
   public static final String EXTENDS = "extends";
   public static final String PROPERTIES = "properties";
   public static final String OPERATIONS = "operations";
   public static final String EVENTS = "events";
   public static final String QUANTITY_KIND = "quantityKind";
   public static final String REFERENCE_UNIT = "referenceUnit";
   public static final String PREFERRED_NAME = "preferredName";
   public static final String DESCRIPTION = "description";
   public static final String CURIE = "curie";
   public static final String INPUT = "input";
   public static final String OUTPUT = "output";
   public static final String PARAMETERS = "parameters";
   public static final String PAYLOAD_NAME = "payloadName";
   public static final String SEE = "see";
   public static final String LIST_TYPE = "listType";
   public static final String EXAMPLE_VALUE = "exampleValue";
   public static final String COMMON_CODE = "commonCode";
   public static final String CONVERSION_FACTOR = "conversionFactor";
   public static final String NUMERIC_CONVERSION_FACTOR = "numericConversionFactor";
   public static final String SYMBOL = "symbol";

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Namespace() {
      return resource( NAMESPACE );
   }

   public Property listType() {
      return property( LIST_TYPE );
   }

   public Property input() {
      return property( INPUT );
   }

   public Property output() {
      return property( OUTPUT );
   }

   public Property curie() {
      return property( CURIE );
   }

   public Property description() {
      return property( DESCRIPTION );
   }

   public Property preferredName() {
      return property( PREFERRED_NAME );
   }

   /**
    * @return a {@link Resource} for the samm:Property RDF Class definition.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Property() {
      return resource( PROPERTY_CLASS );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource AbstractProperty() {
      return resource( ABSTRACT_PROPERTY );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Characteristic() {
      return resource( CHARACTERISTIC_CLASS );
   }

   public Property characteristic() {
      return property( CHARACTERISTIC );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Constraint() {
      return resource( CONSTRAINT_CLASS );
   }

   public Property dataType() {
      return property( DATA_TYPE );
   }

   public Property exampleValue() {
      return property( EXAMPLE_VALUE );
   }

   public Property optional() {
      return property( OPTIONAL );
   }

   public Property notInPayload() {
      return property( NOT_IN_PAYLOAD );
   }

   public Property payloadName() {
      return property( PAYLOAD_NAME );
   }

   public Property see() {
      return property( SEE );
   }

   /**
    * @return a {@link Property} for the samm:property RDF Property definition
    */
   public Property property() {
      return property( PROPERTY );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Aspect() {
      return resource( ASPECT );
   }

   public Property properties() {
      return property( PROPERTIES );
   }

   public Property parameters() {
      return property( PARAMETERS );
   }

   public Property operations() {
      return property( OPERATIONS );
   }

   public Property events() {
      return property( EVENTS );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Operation() {
      return resource( OPERATION );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Event() {
      return resource( EVENT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Entity() {
      return resource( ENTITY );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource AbstractEntity() {
      return resource( ABSTRACT_ENTITY );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Value() {
      return resource( VALUE_CLASS );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Property _extends() {
      return property( EXTENDS );
   }

   public Property value() {
      return property( VALUE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Unit() {
      return resource( UNIT );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QuantityKind() {
      return resource( QUANTITY_KIND_CLASS );
   }

   public Property quantityKind() {
      return property( QUANTITY_KIND );
   }

   public Property referenceUnit() {
      return property( REFERENCE_UNIT );
   }

   public Property commonCode() {
      return property( COMMON_CODE );
   }

   public Property conversionFactor() {
      return property( CONVERSION_FACTOR );
   }

   public Property numericConversionFactor() {
      return property( NUMERIC_CONVERSION_FACTOR );
   }

   public Property symbol() {
      return property( SYMBOL );
   }

   /*
    * Encodings
    */

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource UTF_8() {
      return resource( UTF_8 );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource US_ASCII() {
      return resource( US_ASCII );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ISO_8859_1() {
      return resource( ISO_8859_1 );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource UTF_16() {
      return resource( UTF_16 );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource UTF_16BE() {
      return resource( UTF_16BE );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource UTF_16LE() {
      return resource( UTF_16LE );
   }

   public List<Resource> allEncodings() {
      return List.of( UTF_8(), US_ASCII(), ISO_8859_1(), UTF_16(), UTF_16BE(), UTF_16LE() );
   }

   @Override
   public List<Resource> allResources() {
      return List.of( Namespace(), Property(), AbstractProperty(), Characteristic(), Constraint(), Aspect(), Operation(), Event(),
            Entity(), AbstractEntity(), Value(), Unit(), QuantityKind(), UTF_8(), US_ASCII(), ISO_8859_1(), UTF_16(), UTF_16BE(),
            UTF_16LE() );
   }

   @Override
   public List<Property> allProperties() {
      return List.of( listType(), input(), output(), curie(), description(), preferredName(), characteristic(), dataType(),
            exampleValue(), optional(), notInPayload(), payloadName(), see(), property(), properties(), parameters(),
            operations(), events(), _extends(), value(), quantityKind(), referenceUnit(), commonCode(), conversionFactor(),
            numericConversionFactor(), symbol() );
   }
}
