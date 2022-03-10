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

package io.openmanufacturing.sds.staticmetamodel;

import java.util.Optional;

import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;

/**
 * Extends {@link StaticProperty} to represent container or wrapper types like {@code Collection} or {code @Optional}
 * and carries type information about the contained type.
 */
public abstract class StaticContainerProperty<C, T> extends StaticProperty<T> implements ContainerProperty<C> {

   public StaticContainerProperty(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final Characteristic characteristic,
         final Optional<Value> exampleValue,
         final boolean optional,
         final boolean notInPayload,
         final Optional<String> payloadName ) {
      super( metaModelBaseAttributes, characteristic, exampleValue, optional, notInPayload, payloadName );
   }
}
