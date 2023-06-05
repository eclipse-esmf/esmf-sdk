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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.eclipse.esmf.metamodel.impl.BoundDefinition;

/**
 * Validates assigned values of type {@link Double}, which must be below or equal to this limit depending on the
 * provided {@link BoundDefinition}.
 */
public class DoubleMaxValidator implements ConstraintValidator<DoubleMax, Double> {

   private double doubleMax;
   private BoundDefinition boundDefinition;

   @Override
   public void initialize( final DoubleMax doubleMax ) {
      this.doubleMax = Double.parseDouble( doubleMax.value() );
      this.boundDefinition = doubleMax.boundDefinition();
   }

   @Override
   public boolean isValid( final Double doubleValue, final ConstraintValidatorContext context ) {
      if ( doubleValue == null ) {
         return true;
      }
      return boundDefinition.isValid( doubleValue, doubleMax );
   }
}
