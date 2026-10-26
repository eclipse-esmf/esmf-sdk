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

package org.eclipse.esmf.aspectmodel;

import java.util.Optional;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Convenience implementation of {@link Violation.Code}
 *
 * @param code the code string, e.g., ERR_SYTNAX
 * @param href the url pointing to corresponding documentation
 */
@RecordBuilder
public record ViolationCode(
      String code,
      Optional<String> href
) implements Violation.Code {
   public ViolationCode( final String code ) {
      this( code, Optional.empty() );
   }

   public ViolationCode( final String code, final String href ) {
      this( code, Optional.of( href ) );
   }

   @SuppressWarnings( "OptionalAssignedToNull" )
   public ViolationCode {
      if ( href == null ) {
         href = Optional.empty();
      }
   }
}
