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
package org.eclipse.esmf.metamodel.impl;

import java.util.StringJoiner;

import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;

public class DefaultConstraint extends ModelElementImpl implements Constraint {
   public DefaultConstraint( final MetaModelBaseAttributes metaModelBaseAttributes ) {
      super( metaModelBaseAttributes );
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
      return visitor.visitConstraint( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultConstraint.class.getSimpleName() + "[", "]" )
            .toString();
   }
}
