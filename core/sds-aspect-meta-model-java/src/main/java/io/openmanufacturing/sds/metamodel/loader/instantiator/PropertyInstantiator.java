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

package io.openmanufacturing.sds.metamodel.loader.instantiator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidModelException;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalarValue;
import io.openmanufacturing.sds.metamodel.loader.AspectLoadingException;
import io.openmanufacturing.sds.metamodel.loader.DefaultPropertyWrapper;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class PropertyInstantiator extends Instantiator<Property> {
   private final Map<Resource, Property> resourcePropertyMap = new HashMap<>();

   public PropertyInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Property.class );
   }

   @Override
   public Property apply( final Resource property ) {
      boolean isOptional = false;
      boolean isNotInPayload = false;
      boolean isAbstract = false;
      Optional<String> payloadName = Optional.empty();
      Optional<Property> extends_ = Optional.empty();

      if ( property.isAnon() ) {
         // Note the following code is particularly hard to read due to overloading of the
         // term "property". The variable "property" is an RDF resource an Entity or Aspect points
         // to from its "bamm:properties", i.e. a BAMM Property. getProperty() refers to the
         // RDF properties this particular RDF resource has. bamm.property() then refers to the
         // BAMM RDF vocabulary term "bamm:property", so we talk about this model construct:
         // :Foo a bamm:Aspect ;
         //   bamm:properties ( [ bamm:property :bar ; ... ] ) .
         final Statement propertyStatement = property.getProperty( bamm.property() );
         if ( propertyStatement == null ) {
            // The property reference does not contain "bamm:property", but it could contain
            // "bamm:extends"
            final Statement superPropertyStatement = property.getProperty( bamm._extends() );
            if ( superPropertyStatement == null ) {
               throw new AspectLoadingException( "A Property reference is missing both bamm:property and bamm:extends" );
            }

            final Resource superPropertyResource = superPropertyStatement.getResource();
            final Property superProperty = modelElementFactory.create( Property.class, superPropertyResource );
            extends_ = Optional.of( superProperty );
         }

         if ( property.hasProperty( bamm.optional() ) ) {
            isOptional = property.getProperty( bamm.optional() ).getBoolean();
         }
         if ( property.hasProperty( bamm.notInPayload() ) ) {
            isNotInPayload = property.getProperty( bamm.notInPayload() ).getBoolean();
         }
         if ( property.hasProperty( bamm.payloadName() ) ) {
            payloadName = Optional.of( property.getProperty( bamm.payloadName() ).getString() );
         }
      }

      if ( property.getModel().contains( property, RDF.type, bamm.AbstractProperty() ) ) {
         isAbstract = true;
      }

      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( property );
      final DefaultPropertyWrapper defaultProperty = new DefaultPropertyWrapper( metaModelBaseAttributes );

      if ( resourcePropertyMap.containsKey( property ) ) {
         final Property propertyInstance = resourcePropertyMap.get( property );
         resourcePropertyMap.remove( property );
         return propertyInstance;
      }
      resourcePropertyMap.put( property, defaultProperty );

      if ( !isAbstract ) {
         final Resource characteristicResource = propertyValueFromTypeTree( property, bamm.characteristic() ).getResource();
         final Characteristic characteristic = modelElementFactory.create( Characteristic.class, characteristicResource );
         defaultProperty.setCharacteristic( characteristic );

         final Optional<ScalarValue> exampleValue = optionalPropertyValue( property, bamm.exampleValue() )
               .flatMap( statement -> characteristic.getDataType()
                     .map( type -> {
                        if ( !type.is( Scalar.class ) ) {
                           throw new InvalidModelException( "Type of example value on Property " + property + " has incorrect type" );
                        }
                        return type.as( Scalar.class );
                     } )
                     .map( type -> {
                        final Object literal = statement.getLiteral().getValue();
                        final Object value = literal instanceof BaseDatatype.TypedValue
                              ? statement.getLiteral().getLexicalForm()
                              : literal;
                        return new DefaultScalarValue( value, type );
                     } ) );

         defaultProperty.setExampleValue( exampleValue );
         defaultProperty.setNotInPayload( isNotInPayload );
      }
      defaultProperty.setOptional( isOptional );
      defaultProperty.setPayloadName( payloadName );
      defaultProperty.setAbstract( isAbstract );
      defaultProperty.setExtends_( extends_ );

      return defaultProperty;
   }
}
