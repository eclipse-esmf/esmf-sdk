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

package org.eclipse.esmf.staticmetamodel;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;

/**
 * Extends the SAMM {@link DefaultProperty} definition with a concrete type.
 */
public abstract class StaticProperty<C, T> extends DefaultProperty
      implements PropertyTypeInformation<C, T>, PropertyAccessor<C, T>, PropertyMutator<C, T> {

   public StaticProperty(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final Characteristic characteristic,
         final Optional<ScalarValue> exampleValue,
         final boolean optional,
         final boolean notInPayload,
         final Optional<String> payloadName,
         final boolean isAbstract,
         @SuppressWarnings( { "checkstyle:ParameterName", "MethodParameterNamingConvention" } ) final Optional<Property> extends_ ) {
      super( metaModelBaseAttributes, Optional.of( characteristic ), exampleValue, optional, notInPayload, payloadName, isAbstract,
            extends_ );
   }

   @Override
   public boolean isComplexType() {
      return getCharacteristic().flatMap( Characteristic::getDataType )
            .map( Type::isComplexType )
            .orElse( false );
   }

   @Override
   public void setValue( final C object, final T value ) {
      throw new UnsupportedOperationException( "No setter available." );
   }
}
