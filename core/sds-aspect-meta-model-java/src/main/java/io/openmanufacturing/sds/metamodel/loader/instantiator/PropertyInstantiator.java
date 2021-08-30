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

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.loader.DefaultPropertyWrapper;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class PropertyInstantiator extends Instantiator<Property> {
   private final Map<Resource, Property> resourcePropertyMap = new HashMap<>();

   public PropertyInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Property.class );
   }

   @Override
   public Property apply( final Resource property ) {
      Resource propertyResource = property;
      boolean isOptional = false;
      boolean isNotInPayload = false;
      Optional<String> payloadName = Optional.empty();
      if ( property.isAnon() ) {
         if ( property.hasProperty( bamm.optional() ) ) {
            isOptional = property.getProperty( bamm.optional() ).getBoolean();
         }
         if ( property.hasProperty( bamm.notInPayload() ) ) {
            isNotInPayload = property.getProperty( bamm.notInPayload() ).getBoolean();
         }
         if ( property.hasProperty( bamm.payloadName() ) ) {
            payloadName = Optional.of( property.getProperty( bamm.payloadName() ).getString() );
         }
         propertyResource = property.getProperty( bamm.property() ).getResource();
      }

      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( propertyResource );
      final DefaultPropertyWrapper defaultProperty = new DefaultPropertyWrapper( metaModelBaseAttributes );

      if ( resourcePropertyMap.containsKey( property ) ) {
         final Property propertyInstance = resourcePropertyMap.get( property );
         resourcePropertyMap.remove( property );
         return propertyInstance;
      }
      resourcePropertyMap.put( property, defaultProperty );

      final Resource characteristicResource = propertyValue( propertyResource, bamm.characteristic() ).getResource();
      final Characteristic characteristic = modelElementFactory.create( Characteristic.class, characteristicResource );
      defaultProperty.setCharacteristic( characteristic );

      final Optional<Object> exampleValue = optionalPropertyValue( propertyResource, bamm.exampleValue() )
            .map( statement -> statement.getLiteral().getValue() );

      final Optional<AspectModelUrn> refines = optionalPropertyValue( propertyResource, bamm.refines() )
            .map( statement -> statement.getObject().asResource().getURI() )
            .map( AspectModelUrn::fromUrn );

      defaultProperty.setExampleValue( exampleValue );
      defaultProperty.setRefines( refines );
      defaultProperty.setOptional( isOptional );
      defaultProperty.setPayloadName( payloadName );
      defaultProperty.setNotInPayload( isNotInPayload );

      return defaultProperty;
   }
}
