/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.metamodel.characteristic.impl;

import java.util.Optional;
import java.util.StringJoiner;

import org.eclipse.esmf.metamodel.characteristic.TimeSeries;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;

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
