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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;

@Target( { FIELD, TYPE_USE } )
@Retention( RUNTIME )
@Constraint( validatedBy = FloatMaxValidator.class )
public @interface FloatMax {

   String message() default "{io.openmanufacturing.aspectmodel.java.customconstraint.message}";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};

   /**
    * The {@code String} representation of the max value according to the
    * {@code Float} string representation.
    *
    * @return value the element must be lower or equal to
    */
   String value();

   /**
    * The definition used to determine whether the given {@link #value()} is inclusive or exclusive.
    */
   BoundDefinition boundDefinition();
}
