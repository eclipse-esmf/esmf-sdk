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
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.characteristic.Quantifiable;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.staticmetamodel.UnitProperty;

/**
 * Extends the {@link StaticConstraintProperty} definition with a {@link Unit} and includes an constraint.
 *
 * Only {@link Quantifiable} properties that actually carry a {@code Unit} will be represented by a {@code
 * StaticConstraintUnitProperty}, however in practice this will be the case for most quantifiables.
 */
public abstract class StaticConstraintUnitProperty<T, C extends Characteristic>
      extends StaticConstraintProperty<T, C> implements UnitProperty {

   public StaticConstraintUnitProperty(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final Quantifiable characteristic,
         final Optional<ScalarValue> exampleValue,
         final boolean optional,
         final boolean notInPayload,
         final Optional<String> payloadName,
         final boolean isAbstract,
         final Optional<Property> extends_ ) {
      super( metaModelBaseAttributes, characteristic, exampleValue, optional, notInPayload, payloadName, isAbstract, extends_ );
   }
}
