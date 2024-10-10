/*
 * Copyright (c) 2021-2023 Robert Bosch Manufacturing Solutions GmbH
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
package org.eclipse.esmf.aspectmodel.aas;

import org.eclipse.esmf.metamodel.Type;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

/**
 * The default mapper used for all properties.
 */
public class DefaultPropertyMapper implements PropertyMapper<Property> {
   @Override
   public Property mapToAasProperty( final Type type, final org.eclipse.esmf.metamodel.Property property, final Context context ) {
      final DefaultProperty defaultProperty = new DefaultProperty();
      defaultProperty.setIdShort( context.getPropertyShortId() );
      defaultProperty.setValueType( AasDataTypeMapper.mapAspectTypeToAasXsdDataType( mapType( type ) ) );
      defaultProperty.setDisplayName( LangStringMapper.NAME.map( property.getPreferredNames() ) );
      defaultProperty.setSemanticId( buildPropertyReferenceToGlobalReference( property ) );

      if ( !context.getPropertyValue( UNKNOWN_EXAMPLE ).equals( UNKNOWN_EXAMPLE ) ) {
         defaultProperty.setValue( context.getPropertyValue( UNKNOWN_EXAMPLE ) );
      }

      return defaultProperty;
   }

   private String mapType( final Type type ) {
      return type.getUrn();
   }
}
