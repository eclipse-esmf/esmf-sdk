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

import org.eclipse.esmf.characteristic.Quantifiable;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;

/**
 * Extends the {@link StaticProperty} definition with a {@link Unit}.
 *
 * Only {@link Quantifiable} properties that actually carry a {@code Unit} will be represented by a {@code
 * StaticUnitProperty}, however in practice this will be the case for most quantifiables.
 */
public abstract class StaticUnitProperty<C, T> extends StaticProperty<C, T> implements UnitProperty {

   public StaticUnitProperty(
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
