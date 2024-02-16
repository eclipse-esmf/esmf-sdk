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

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;

/**
 * Extends the SAMM {@link DefaultProperty} definition with a concrete type.
 */
public abstract class StaticProperty<C, T> extends DefaultProperty {

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

   /**
    * @return the type of the Property represented as a class.
    */
   public abstract Class<T> getPropertyType();

   /**
    * @return the property value of the given instance.
    */
   public abstract T getValue( C object );
}
