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

package org.eclipse.esmf.aspectmodel.java.rangeconstraint;

import org.eclipse.esmf.metamodel.BoundDefinition;

/**
 * Provides the functionality to build/concat the annotation fields.
 */
public class AnnotationFieldBuilder {

   private final StringBuilder builder;

   public AnnotationFieldBuilder() {
      builder = new StringBuilder( 10 );
   }

   public AnnotationFieldBuilder setValue( final Object value ) {
      builder.append( " value = " ).append( value );
      return this;
   }

   public AnnotationFieldBuilder setValueWithQuotesForAnnotation( final Object value ) {
      builder.append( " value =" ).append( "\"" ).append( value ).append( "\"" );
      return this;
   }

   public AnnotationFieldBuilder setBoundDefinition( final BoundDefinition boundDefinition ) {
      builder.append( ", boundDefinition = " + "BoundDefinition." ).append( boundDefinition.name() );
      return this;
   }

   public AnnotationFieldBuilder setNoBoundDefinitionTag( final BoundDefinition boundDefinition ) {
      if ( boundDefinition.equals( BoundDefinition.GREATER_THAN ) || boundDefinition
            .equals( BoundDefinition.LESS_THAN ) ) {
         builder.append( ", inclusive = false" );
      }
      return this;
   }

   public String build() {
      return builder.toString();
   }
}
