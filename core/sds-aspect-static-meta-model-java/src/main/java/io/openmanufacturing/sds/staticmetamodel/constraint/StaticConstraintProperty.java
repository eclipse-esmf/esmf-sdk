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

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultProperty;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.staticmetamodel.StaticProperty;

/**
 * Extends the BAMM {@link DefaultProperty} definition with a concrete type and an constraint.
 */
public abstract class StaticConstraintProperty<T, C extends Constraint, C2 extends Characteristic>
      extends StaticProperty<T> implements ConstraintProperty<C, C2> {

   public StaticConstraintProperty(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final Characteristic characteristic,
         final Optional<Object> exampleValue,
         final Optional<AspectModelUrn> refines,
         final boolean optional,
         final boolean notInPayload,
         final Optional<String> payloadName ) {
      super( metaModelBaseAttributes, characteristic, exampleValue, refines, optional, notInPayload, payloadName );
   }
}
