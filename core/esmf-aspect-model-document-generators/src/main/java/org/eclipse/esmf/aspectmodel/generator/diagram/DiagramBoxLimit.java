/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.aspectmodel.generator.diagram;

import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator.DiagramBoxLimitExceededException;

/** Validates the production diagram-size boundary before Graphper layout begins. */
final class DiagramBoxLimit {
   private final int maximumBoxes;

   private DiagramBoxLimit( final int maximumBoxes ) {
      if ( maximumBoxes < 0 ) {
         throw new IllegalArgumentException( "The maximum diagram box count must not be negative" );
      }
      this.maximumBoxes = maximumBoxes;
   }

   static DiagramBoxLimit maximum( final int maximumBoxes ) {
      return new DiagramBoxLimit( maximumBoxes );
   }

   void validate( final Diagram diagram ) {
      final int actualBoxes = diagram.getBoxes().size();
      if ( actualBoxes > maximumBoxes ) {
         throw new DiagramBoxLimitExceededException( actualBoxes, maximumBoxes );
      }
   }
}
