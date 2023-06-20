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

package org.eclipse.esmf.aspectmodel.shacl.violation;

/**
 * Meta violation: Syntax error in source file
 *
 * @param violationSpecificMessage the message for this violation
 * @param line the line in the source file
 * @param column the column in the source file
 */
public record InvalidSyntaxViolation( String violationSpecificMessage, String source, long line, long column ) implements Violation {
   public static final String ERROR_CODE = "ERR_SYNTAX";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public EvaluationContext context() {
      return null;
   }

   @Override
   public String message() {
      return violationSpecificMessage;
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitInvalidSyntaxViolation( this );
   }
}
