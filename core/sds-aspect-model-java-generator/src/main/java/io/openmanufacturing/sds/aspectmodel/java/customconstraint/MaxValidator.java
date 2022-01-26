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

package io.openmanufacturing.sds.aspectmodel.java.customconstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;

/**
 * Validates assigned values of type {@link Integer}, which must be below or equal to this limit depending on the
 * provided {@link BoundDefinition}.
 */
public class MaxValidator implements ConstraintValidator<IntegerMax, Integer> {

   private int max;
   private BoundDefinition boundDefinition;

   @Override
   public void initialize( final IntegerMax integerMax ) {
      this.max = integerMax.value();
      this.boundDefinition = integerMax.boundDefinition();
   }

   @Override
   public boolean isValid( final Integer integerValue, final ConstraintValidatorContext context ) {
      if ( integerValue == null ) {
         return true;
      }
      return boundDefinition.isValid( integerValue, max );
   }
}
