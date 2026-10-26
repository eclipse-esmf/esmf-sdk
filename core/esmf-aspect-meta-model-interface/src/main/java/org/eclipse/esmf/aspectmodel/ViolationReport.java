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

import java.util.List;

import com.google.common.collect.Streams;

public record ViolationReport(
      List<Violation> violations
) {
   public static final ViolationReport EMPTY = new ViolationReport( List.of() );

   public ViolationReport( final Violation diagnostic ) {
      this( List.of( diagnostic ) );
   }

   /**
    * Create a new DiagnosticsReport from this and another
    *
    * @param violationReport the other report
    * @return the new merged report
    */
   public ViolationReport merge( final ViolationReport violationReport ) {
      return new ViolationReport( Streams.concat( violations.stream(), violationReport.violations().stream() ).toList() );
   }

   public boolean isEmpty() {
      return violations.isEmpty();
   }
}
