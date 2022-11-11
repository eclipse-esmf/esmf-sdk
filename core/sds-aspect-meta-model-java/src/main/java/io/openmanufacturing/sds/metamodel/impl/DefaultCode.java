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

package io.openmanufacturing.sds.metamodel.impl;

import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.characteristic.Code;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultCode extends DefaultCharacteristic implements Code {

   public DefaultCode( final MetaModelBaseAttributes metaModelBaseAttributes, final Type dataType ) {
      super( metaModelBaseAttributes, Optional.of( dataType ) );
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
      return visitor.visitCode( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultCode.class.getSimpleName() + "[", "]" )
            .toString();
   }
}
