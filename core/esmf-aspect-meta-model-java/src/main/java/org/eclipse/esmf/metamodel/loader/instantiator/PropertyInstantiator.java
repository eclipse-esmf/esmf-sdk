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

package org.eclipse.esmf.metamodel.loader.instantiator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.InvalidModelException;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.eclipse.esmf.metamodel.loader.DefaultPropertyWrapper;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;
import org.eclipse.esmf.samm.KnownVersion;

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
      final boolean isOptional = optionalAttributeValue( property, samm.optional() ).map( Statement::getBoolean ).orElse( false );
      final boolean isNotInPayload = optionalAttributeValue( property, samm.notInPayload() ).map( Statement::getBoolean ).orElse( false );
      final Optional<String> payloadName = optionalAttributeValue( property, samm.payloadName() ).map( Statement::getString );
      final Optional<Property> extends_ = optionalAttributeValue( property, samm._extends() )
            .map( Statement::getResource )
            .map( superElementResource -> modelElementFactory.create( Property.class, superElementResource ) );
      final boolean isAbstract = property.getModel().contains( property, RDF.type, samm.AbstractProperty() );

      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( property );
      final DefaultPropertyWrapper defaultPropertyWrapper = new DefaultPropertyWrapper( metaModelBaseAttributes );

      if ( resourcePropertyMap.containsKey( property ) ) {
         final Property propertyInstance = resourcePropertyMap.get( property );
         return propertyInstance;
      }
      resourcePropertyMap.put( property, defaultPropertyWrapper );
      final DefaultProperty defProperty;
      if ( isAbstract ) {
         defProperty = new DefaultProperty( metaModelBaseAttributes, Optional.of( fallbackCharacteristic ), Optional.empty(), isOptional,
               isNotInPayload, payloadName, isAbstract, extends_ );
      } else {
         final Resource characteristicResource = attributeValue( property, samm.characteristic() ).getResource();
         final Characteristic characteristic = modelElementFactory.create( Characteristic.class, characteristicResource );
         final Optional<ScalarValue> exampleValue = optionalAttributeValue( property, samm.exampleValue() )
               .flatMap( statement -> characteristic.getDataType()
                     .map( type -> {
                        if ( !type.is( Scalar.class ) ) {
                           throw new InvalidModelException( "Type of example value on Property " + property + " has incorrect type" );
                        }
                        return type.as( Scalar.class );
                     } )
                     .map( type -> buildScalarValue( statement.getLiteral(), type ) ) );

         defProperty = new DefaultProperty( metaModelBaseAttributes, Optional.of( characteristic ), exampleValue, isOptional,
               isNotInPayload, payloadName, isAbstract, extends_ );
      }
      defaultPropertyWrapper.setProperty( defProperty );
      return defaultPropertyWrapper;
   }

   private ScalarValue buildScalarValue( final Literal literal, final Scalar type ) {
      final Object literalValue = literal.getValue();
      if ( literalValue instanceof BaseDatatype.TypedValue ) {
         return new DefaultScalarValue( literal.getLexicalForm(), type );
      } else if ( literal.getDatatypeURI().equals( RDF.langString.getURI() ) ) {
         return buildLanguageString( literal );
      }
      return new DefaultScalarValue( literalValue, type );
   }
}
