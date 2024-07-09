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
 * Validates assigned values of type {@link Integer}, which must be above or equal to this limit depending on the
 * provided {@link BoundDefinition}.
 */
public class IntegerMinValidator implements ConstraintValidator<IntegerMin, Integer> {

   private int min;
   private BoundDefinition boundDefinition;

   @Override
   public void initialize( final IntegerMin integerMin ) {
      this.min = integerMin.value();
      this.boundDefinition = integerMin.boundDefinition();
   }

   @Override
   public boolean isValid( final Integer integerValue, final ConstraintValidatorContext context ) {
      if ( integerValue == null ) {
         return true;
      }
      return boundDefinition.isValid( integerValue, min );
   }
}
