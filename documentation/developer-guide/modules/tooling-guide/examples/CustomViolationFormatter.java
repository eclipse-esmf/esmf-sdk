/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package examples;

// tag::imports[]
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxCountViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
// end::imports[]

public class CustomViolationFormatter implements Violation.Visitor<String> {

   // tag::custom-formatter[]
   // Implement other visitor methods for different violation types
   @Override
   public String visit(Violation violation) {
      // Generic fallback for unhandled violation types
      return String.format("Validation error: %s", violation.message());
   }
   // end::custom-formatter[]

   // tag::error-aggregation[]
   public void aggregateErrors(List<Violation> violations) {
       // Group violations by error code for batch processing
       Map<String, List<Violation>> errorsByCode = violations.stream()
           .collect(Collectors.groupingBy(Violation::errorCode));

       // Handle each error type separately
       List<Violation> dataTypeErrors = errorsByCode.get("ERR_TYPE");
       List<Violation> cardinalityErrors = errorsByCode.get("ERR_MAX_COUNT");
       
       // Process each error type with specific handling logic
       if (dataTypeErrors != null && !dataTypeErrors.isEmpty()) {
           System.out.println("Found " + dataTypeErrors.size() + " data type errors");
           // Handle data type errors...
       }
       
       if (cardinalityErrors != null && !cardinalityErrors.isEmpty()) {
           System.out.println("Found " + cardinalityErrors.size() + " cardinality errors");
           // Handle cardinality errors...
       }
   }
   // end::error-aggregation[]
}