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

import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

public class DefaultScalar implements Scalar {
   private final KnownVersion metaModelVersion;
   private final String urn;

   public DefaultScalar( final String urn, final KnownVersion metaModelVersion ) {
      this.urn = urn;
      this.metaModelVersion = metaModelVersion;
   }

   @Override
   public String getUrn() {
      return urn;
   }

   @Override
   public KnownVersion getMetaModelVersion() {
      return metaModelVersion;
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultScalar.class.getSimpleName() + "[", "]" )
            .add( "metaModelVersion=" + metaModelVersion )
            .add( "urn='" + urn + "'" )
            .toString();
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
      return visitor.visitScalar( this, context );
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final DefaultScalar that = (DefaultScalar) o;
      return metaModelVersion == that.metaModelVersion
            && Objects.equals( urn, that.urn );
   }

   @Override
   public int hashCode() {
      return Objects.hash( metaModelVersion, urn );
   }
}
