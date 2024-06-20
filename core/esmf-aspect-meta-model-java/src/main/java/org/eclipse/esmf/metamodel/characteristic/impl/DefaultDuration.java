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

import org.eclipse.esmf.metamodel.characteristic.Duration;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;

public class DefaultDuration extends DefaultQuantifiable implements Duration {

   public DefaultDuration( final MetaModelBaseAttributes metaModelBaseAttributes, final Type dataType, final Optional<Unit> unit ) {
      super( metaModelBaseAttributes, dataType, unit );
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
      return visitor.visitDuration( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultDuration.class.getSimpleName() + "[", "]" )
            .toString();
   }
}
