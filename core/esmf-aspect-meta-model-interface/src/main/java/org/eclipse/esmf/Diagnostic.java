/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf;

/**
 * Generic diagnostic interface representing a problem found during document processing.
 *
 * @param <C> the concrete code type used to classify this diagnostic
 */
public interface Diagnostic<C extends Diagnostic.Code> {
   C code();

   String message();

   Severity severity();

   default boolean hasLocation() {
      return false;
   }

   /**
    * Identifies the kind of problem.
    */
   interface Code {
      String code();

      String description();
   }

   enum Severity {
      ERROR,
      WARNING,
      INFO,
      HINT
   }
}
