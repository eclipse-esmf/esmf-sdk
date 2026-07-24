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
   /**
    * The code identifying this diagnostic
    *
    * @return the code
    */
   C code();

   /**
    * The human-readable message for this diagnostic
    *
    * @return the message
    */
   String message();

   /**
    * href to additional information about the diagnostic
    *
    * @return the href
    */
   default String href() {
      return "";
   }

   /**
    * The severity of this diagnostic
    *
    * @return the severity
    */
   Severity severity();

   /**
    * Indicates whether this diagnostic is related to a certain location in a document
    *
    * @return true if it is related to a certain location
    */
   default boolean hasLocation() {
      return false;
   }

   /**
    * Identifies the kind of problem.
    */
   interface Code {
      String code();

      String description();

      default String href() {
         return "";
      }
   }

   enum Severity {
      ERROR,
      WARNING,
      INFO,
      HINT
   }
}
