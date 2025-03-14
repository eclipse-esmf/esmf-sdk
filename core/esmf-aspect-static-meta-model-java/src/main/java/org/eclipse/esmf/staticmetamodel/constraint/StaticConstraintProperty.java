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

package org.eclipse.esmf.staticmetamodel.constraint;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.staticmetamodel.StaticProperty;

/**
 * Extends the SAMM {@link DefaultProperty} definition with a concrete type and a constraint.
 */
public abstract class StaticConstraintProperty<E, T, C extends Characteristic>
      extends StaticProperty<E, T> implements ConstraintProperty<C> {

   public StaticConstraintProperty(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final Characteristic characteristic,
         final Optional<Object> exampleValue,
         final boolean optional,
         final boolean notInPayload,
         final Optional<String> payloadName,
         final boolean isAbstract,
         @SuppressWarnings( { "checkstyle:ParameterName", "MethodParameterNamingConvention" } ) final Optional<Property> extends_ ) {
      super( metaModelBaseAttributes, characteristic, exampleValue, optional, notInPayload, payloadName, isAbstract, extends_ );
   }
}
