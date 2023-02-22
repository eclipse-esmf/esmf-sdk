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

import org.eclipse.esmf.metamodel.impl.BoundDefinition;

public class TestAspect {

   @DoubleMin( value = "5", boundDefinition = BoundDefinition.AT_LEAST )
   private final double testMinDouble;

   @DoubleMax( value = "10", boundDefinition = BoundDefinition.LESS_THAN )
   private final double testMaxDouble;

   @FloatMin( value = "1.5", boundDefinition = BoundDefinition.GREATER_THAN )
   private final float testMinFloat;

   @FloatMax( value = "5.6", boundDefinition = BoundDefinition.AT_MOST )
   private final float testMaxFloat;

   @IntegerMin( value = 5, boundDefinition = BoundDefinition.AT_LEAST )
   private final int testMinInt;

   @IntegerMax( value = 10, boundDefinition = BoundDefinition.AT_MOST )
   private final int testMaxInt;

   public TestAspect( final double testMinDouble, final double testMaxDouble, final float testMinFloat,
         final float testMaxFloat, final int testMinInt, final int testMaxInt ) {
      this.testMinDouble = testMinDouble;
      this.testMaxDouble = testMaxDouble;
      this.testMinFloat = testMinFloat;
      this.testMaxFloat = testMaxFloat;
      this.testMinInt = testMinInt;
      this.testMaxInt = testMaxInt;
   }

   public double getTestMinDouble() {
      return testMinDouble;
   }

   public double getTestMaxDouble() {
      return testMaxDouble;
   }

   public float getTestMinFloat() {
      return testMinFloat;
   }

   public float getTestMaxFloat() {
      return testMaxFloat;
   }

   public int getTestMinInt() {
      return testMinInt;
   }

   public int getTestMaxInt() {
      return testMaxInt;
   }
}
