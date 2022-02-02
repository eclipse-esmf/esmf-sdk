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

package io.openmanufacturing.sds.aspectmodel.java.rangeconstraint;

import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;

public class ShortAnnotation extends ConstraintAnnotation implements AnnotationExpression {

   public ShortAnnotation( final Class<?> type ) {
      targetAnnotation = type;
   }

   @Override
   public String apply( final Object value, final BoundDefinition boundDefinition ) {
      return buildConstraintAnnotation( targetAnnotation,
            new AnnotationFieldBuilder().setValue( value ).setBoundDefinition( boundDefinition ).build() );
   }

   @Override
   public Class<?> getTargetAnnotation() {
      return targetAnnotation;
   }
}
