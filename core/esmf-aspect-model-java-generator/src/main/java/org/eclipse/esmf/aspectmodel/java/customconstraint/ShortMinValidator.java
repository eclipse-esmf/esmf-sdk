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
 * Validates assigned values of type {@link Short}, which must be above or equal to this limit depending on the
 * provided {@link BoundDefinition}.
 */
public class ShortMinValidator implements ConstraintValidator<ShortMin, Short> {

   private short min;
   private BoundDefinition boundDefinition;

   @Override
   public void initialize( final ShortMin shortMin ) {
      this.min = shortMin.value();
      this.boundDefinition = shortMin.boundDefinition();
   }

   @Override
   public boolean isValid( final Short shortValue, final ConstraintValidatorContext context ) {
      if ( shortValue == null ) {
         return true;
      }
      return boundDefinition.isValid( shortValue, min );
   }
}
