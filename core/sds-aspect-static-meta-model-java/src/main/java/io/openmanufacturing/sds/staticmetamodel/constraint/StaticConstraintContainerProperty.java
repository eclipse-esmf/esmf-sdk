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

package io.openmanufacturing.sds.staticmetamodel.constraint;

import java.util.Optional;

import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.staticmetamodel.ContainerProperty;

/**
 * Extends {@link StaticConstraintProperty} to represent container or wrapper types like {@code Collection} or {@code
 * Optional} and carries type information about the contained type and includes a constraint
 */
public abstract class StaticConstraintContainerProperty<R, T, C extends Constraint, C2 extends Characteristic>
      extends StaticConstraintProperty<T, C, C2> implements ContainerProperty<R> {

   public StaticConstraintContainerProperty(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final Characteristic characteristic,
         final Optional<Value> exampleValue,
         final boolean optional,
         final boolean notInPayload,
         final Optional<String> payloadName ) {
      super( metaModelBaseAttributes, characteristic, exampleValue, optional, notInPayload, payloadName );
   }
}
