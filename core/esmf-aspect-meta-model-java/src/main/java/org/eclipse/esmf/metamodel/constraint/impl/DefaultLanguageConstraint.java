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
package org.eclipse.esmf.metamodel.constraint.impl;

import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.impl.DefaultConstraint;

public class DefaultLanguageConstraint extends DefaultConstraint implements LanguageConstraint {
   private final Locale languageCode;

   public DefaultLanguageConstraint( final MetaModelBaseAttributes metaModelBaseAttributes, final Locale languageCode ) {
      super( metaModelBaseAttributes );
      this.languageCode = languageCode;
   }

   /**
    * An ISO 639-1 language code for the language of the value of the constrained Property
    *
    * @return the languageCode.
    */
   @Override
   public Locale getLanguageCode() {
      return languageCode;
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
      return visitor.visitLanguageConstraint( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultLanguageConstraint.class.getSimpleName() + "[", "]" )
            .add( "languageCode=" + languageCode )
            .toString();
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      if ( !super.equals( o ) ) {
         return false;
      }
      final DefaultLanguageConstraint that = (DefaultLanguageConstraint) o;
      return Objects.equals( languageCode, that.languageCode );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), languageCode );
   }
}
