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
package io.openmanufacturing.sds.aspectmodel.aas;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

import io.openmanufacturing.sds.metamodel.Type;

/**
 * The default mapper used for all properties.
 */
public class DefaultPropertyMapper implements PropertyMapper<Property> {
   @Override
   public Property mapToAasProperty( final Type type, final io.openmanufacturing.sds.metamodel.Property property, final Context context ) {
      return new DefaultProperty.Builder()
            .idShort( context.getPropertyShortId() )
            .kind( context.getModelingKind() )
            .valueType( mapAASXSDataType( mapType( type ) ) )
            .displayName( LANG_STRING_MAPPER.map( property.getPreferredNames() ) )
            .value( context.getPropertyValue( UNKNOWN_EXAMPLE ) )
            .description( LANG_STRING_MAPPER.map( property.getDescriptions() ) )
            .semanticId( buildReferenceToConceptDescription( property ) )
            .build();
   }

   private String mapType( final Type type ) {
      return type.getUrn();
   }
}
