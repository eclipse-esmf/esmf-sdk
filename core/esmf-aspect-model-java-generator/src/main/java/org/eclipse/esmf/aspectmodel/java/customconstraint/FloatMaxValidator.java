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

package org.eclipse.esmf.aspectmodel.java.customconstraint;

import org.eclipse.esmf.metamodel.BoundDefinition;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates assigned values of type {@link Float}, which must be below or equal to this limit depending on the
 * provided {@link BoundDefinition}.
 */
public class FloatMaxValidator implements ConstraintValidator<FloatMax, Float> {

   private Float floatMax;
   private BoundDefinition boundDefinition;

   @Override
   public void initialize( final FloatMax floatMax ) {
      this.floatMax = Float.parseFloat( floatMax.value() );
      this.boundDefinition = floatMax.boundDefinition();
   }

   @Override
   public boolean isValid( final Float floatValue, final ConstraintValidatorContext context ) {
      if ( floatValue == null ) {
         return true;
      }
      return boundDefinition.isValid( floatValue, floatMax );
   }
}
