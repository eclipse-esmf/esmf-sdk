/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.characteristic.impl;

import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.characteristic.TimeSeries;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultTimeSeries extends DefaultSortedSet implements TimeSeries {

   public DefaultTimeSeries( final MetaModelBaseAttributes metaModelBaseAttributes,
         final Optional<Type> dataType, final Optional<Characteristic> elementCharacteristic ) {
      super( metaModelBaseAttributes, dataType, elementCharacteristic );
   }

   /**
    * Accepts an Aspect visitor
    *
    * @param visitor The visitor to accept
    * @param <T> The result type of the traversal operation
    * @param <C> The context of the visitor traversal
    */
   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitTimeSeries( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultTimeSeries.class.getSimpleName() + "[", "]" )
            .toString();
   }
}
