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

package org.eclipse.esmf.metamodel;

import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

/**
 * The Base interface provides all facilities that all Aspect Model elements have.
 */
public interface ModelElement {
   /**
    * @return the version of the Aspect Meta Model on which the Aspect Model is based.
    */
   KnownVersion getMetaModelVersion();

   <T, C> T accept( AspectVisitor<T, C> visitor, C context );

   default <T extends ModelElement> boolean is( final Class<T> clazz ) {
      return clazz.isAssignableFrom( getClass() );
   }

   default <T extends ModelElement> T as( final Class<T> clazz ) {
      return clazz.cast( this );
   }
}
