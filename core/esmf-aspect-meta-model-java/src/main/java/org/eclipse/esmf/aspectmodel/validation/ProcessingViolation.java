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

package org.eclipse.esmf.aspectmodel.validation;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.ProjectInfo;
import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.ViolationCode;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Generic violation: Loading or validation was unsuccessful, without any more specific details
 *
 * @param message the detailed message
 * @param cause the cause
 */
@RecordBuilder
public record ProcessingViolation(
      String message,
      Optional<Throwable> cause
) implements Violation {
   public static final String ERROR_CODE = "ERR_PROCESSING";

   public ProcessingViolation( final String message ) {
      this( message, Optional.empty() );
   }

   public ProcessingViolation( final String message, final Throwable cause ) {
      this( message, Optional.of( cause ) );
   }

   @SuppressWarnings( "OptionalAssignedToNull" )
   public ProcessingViolation {
      if ( cause == null ) {
         cause = Optional.empty();
      }
   }

   @Override
   public Code code() {
      return new ViolationCode( ERROR_CODE, ProjectInfo.esmfErrorCodeUrl( ERROR_CODE ) );
   }
}
