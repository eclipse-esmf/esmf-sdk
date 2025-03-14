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
import org.eclipse.esmf.staticmetamodel.ContainerProperty;

/**
 * Extends {@link StaticConstraintProperty} to represent container or wrapper types like {@code Collection} or {@code
 * Optional} and carries type information about the contained type and includes a constraint
 */
public abstract class StaticConstraintContainerProperty<E, R, T, C extends Characteristic>
      extends StaticConstraintProperty<E, T, C> implements ContainerProperty<R> {

   public StaticConstraintContainerProperty(
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
