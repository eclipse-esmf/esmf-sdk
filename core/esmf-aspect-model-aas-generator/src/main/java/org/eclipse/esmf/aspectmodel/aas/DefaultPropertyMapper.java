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

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.Type;

/**
 * The default mapper used for all properties.
 */
public class DefaultPropertyMapper implements PropertyMapper<Property> {
   @Override
   public Property mapToAasProperty( final Type type, final org.eclipse.esmf.metamodel.Property property, final Context context ) {
      return new DefaultProperty.Builder()
            .idShort( context.getPropertyShortId() )
            .valueType( mapAASXSDataType( mapType( type ) ) )
            .displayName( LangStringMapper.NAME.map( property.getPreferredNames() ) )
            .value( context.getPropertyValue( UNKNOWN_EXAMPLE ) )
            .description( LangStringMapper.TEXT.map( property.getDescriptions() ) )
            .semanticID( buildReferenceToConceptDescription( property ) )
            .build();
   }

   private String mapType( final Type type ) {
      return type.getUrn();
   }
}
