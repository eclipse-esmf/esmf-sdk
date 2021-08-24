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

public class FloatAnnotation extends ConstraintAnnotation implements AnnotationExpression {

   public FloatAnnotation( final Class<?> type ) {
      targetAnnotation = type;
   }

   @Override
   public Class<?> getTargetAnnotation() {
      return targetAnnotation;
   }
}
