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

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidModelException;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalarValue;
import io.openmanufacturing.sds.metamodel.loader.AspectLoadingException;
import io.openmanufacturing.sds.metamodel.loader.DefaultPropertyWrapper;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class PropertyInstantiator extends Instantiator<Property> {
   private final Characteristic fallbackCharacteristic;
   private final Map<Resource, Property> resourcePropertyMap = new HashMap<>();

   public PropertyInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Property.class );
      final MetaModelBaseAttributes characteristicBaseAttributes = MetaModelBaseAttributes.builderFor( "UnnamedCharacteristic" )
            .withMetaModelVersion( KnownVersion.getLatest() )
            .build();
      fallbackCharacteristic = new DefaultCharacteristic( characteristicBaseAttributes, Optional.empty() );
   }

   @Override
   public Property apply( final Resource property ) {
      final boolean isOptional = optionalAttributeValue( property, bamm.optional() ).map( Statement::getBoolean ).orElse( false );
      final boolean isNotInPayload = optionalAttributeValue( property, bamm.notInPayload() ).map( Statement::getBoolean ).orElse( false );
      final Optional<String> payloadName = optionalAttributeValue( property, bamm.payloadName() ).map( Statement::getString );
      final Optional<Property> extends_ = optionalAttributeValue( property, bamm._extends() )
            .map( Statement::getResource )
            .map( superElementResource -> modelElementFactory.create( Property.class, superElementResource ) );
      final boolean isAbstract = property.getModel().contains( property, RDF.type, bamm.AbstractProperty() );

      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( property );
      final DefaultPropertyWrapper defaultProperty = new DefaultPropertyWrapper( metaModelBaseAttributes );

      if ( resourcePropertyMap.containsKey( property ) ) {
         final Property propertyInstance = resourcePropertyMap.get( property );
         resourcePropertyMap.remove( property );
         return propertyInstance;
      }
      resourcePropertyMap.put( property, defaultProperty );

      if ( isAbstract ) {
         defaultProperty.setCharacteristic( fallbackCharacteristic );
      } else {
         final Resource characteristicResource = attributeValue( property, bamm.characteristic() ).getResource();
         final Characteristic characteristic = modelElementFactory.create( Characteristic.class, characteristicResource );
         defaultProperty.setCharacteristic( characteristic );

         final Optional<ScalarValue> exampleValue = optionalAttributeValue( property, bamm.exampleValue() )
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
