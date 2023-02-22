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

import org.eclipse.esmf.metamodel.impl.BoundDefinition;

public interface AnnotationExpression {

   /**
    * @param value the min or max value.
    * @param boundDefinition the definition of the min or max bound
    * @return the annotation expression as string.
    */
   String apply( final Object value, BoundDefinition boundDefinition );

   /**
    * @return the target annotation of the annotation expression
    */
   Class<?> getTargetAnnotation();
}
