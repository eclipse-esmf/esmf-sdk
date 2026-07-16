/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.aspect.diagnostic;

import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

public record TestViolation(
      String errorCode, String message
) implements Violation {
   @Override
   public EvaluationContext context() {
      return null;
   }

   @Override
   public String violationSpecificMessage() {
      return message;
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visit( this );
   }
}
