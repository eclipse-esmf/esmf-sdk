/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator.DiagramBoxLimitExceededException;

import org.junit.jupiter.api.Test;

class AspectModelDiagramBoxLimitTest {
   @Test
   void publicStringEntriesRemainMutableViewOfStructuralRows() {
      final Diagram.Box box = box( 0 );
      box.addEntry( java.util.List.of( "first" ) );

      box.getEntries().add( "second" );
      box.getEntries().set( 0, "changed" );

      assertThat( box.getEntries() ).containsExactly( "changed", "second" );
      assertThat( box.getEntryRows() ).extracting( Diagram.Box.EntryRow::text ).containsExactly( "changed", "second" );
      assertThat( box.getEntryRows() ).allMatch( row -> row.navigation().isEmpty() );
   }

   @Test
   void exactlyOneThousandBoxesPassTheProductionBoundary() {
      final Diagram diagram = diagramWithBoxes( 1_000 );

      assertThatCode( () -> DiagramBoxLimit.maximum( 1_000 ).validate( diagram ) ).doesNotThrowAnyException();
      assertThat( diagram.getBoxes() ).hasSize( 1_000 );
   }

   @Test
   void oneThousandOneBoxesFailTheProductionBoundary() {
      final Diagram diagram = diagramWithBoxes( 1_001 );

      assertThatThrownBy( () -> DiagramBoxLimit.maximum( 1_000 ).validate( diagram ) )
            .isInstanceOf( DiagramBoxLimitExceededException.class )
            .hasMessage( "Diagram contains 1001 boxes; maximum is 1000" );
   }

   private static Diagram diagramWithBoxes( final int boxCount ) {
      final Diagram diagram = new Diagram( box( 0 ) );
      for ( int index = 1; index < boxCount; index++ ) {
         diagram.addBox( box( index ) );
      }
      return diagram;
   }

   private static Diagram.Box box( final int index ) {
      return new Diagram.Box( "Aspect", "Box" + index, Diagram.Color.ASPECT );
   }
}
