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

public interface Violation {
   /**
    * The code identifying this violation
    *
    * @return the code
    */
   Violation.Code code();

   /**
    * The human-readable message for this violation
    *
    * @return the message
    */
   String message();

   /**
    * The severity of this violation
    *
    * @return the severity
    */
   default Violation.Severity severity() {
      return Violation.Severity.ERROR;
   }

   /**
    * Identifies the kind of problem
    */
   interface Code {
      /**
       * The code identifying a violation
       */
      String code();

      /**
       * A short description of the violation code
       */
      default String description() {
         return code();
      }

      default Optional<String> href() {
         return Optional.empty();
      }
   }

   enum Severity {
      ERROR,
      WARNING,
      INFO,
      HINT
   }
}
