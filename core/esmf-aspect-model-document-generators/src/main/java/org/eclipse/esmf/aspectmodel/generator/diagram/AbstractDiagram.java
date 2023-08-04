/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class AbstractDiagram {
   private final Box focusBox;
   private final Set<Box> boxes;
   private final Set<Edge> edges;

   // Used for the special case where a value element is rendered as a string (as part of a parent's attribute)
   private String scalarValue = null;

   public AbstractDiagram( final Box focusBox ) {
      this.focusBox = focusBox;
      boxes = new HashSet<>();
      addBox( focusBox );
      edges = new HashSet<>();
   }

   static final AbstractDiagram EMPTY = new AbstractDiagram( null );

   public void addBox( final Box box ) {
      if ( box != null ) {
         boxes.add( box );
      }
   }

   public void addEdge( final Edge edge ) {
      edges.add( edge );
   }

   public void addBoxes( final Collection<Box> boxes ) {
      for ( final Box box : boxes ) {
         addBox( box );
      }
   }

   public void addEdges( final Collection<Edge> edges ) {
      this.edges.addAll( edges );
   }

   public void add( final AbstractDiagram abstractDiagram ) {
      addBoxes( abstractDiagram.getBoxes() );
      addEdges( abstractDiagram.getEdges() );
   }

   public void setScalarValue( final String value ) {
      scalarValue = value;
   }

   public Set<Box> getBoxes() {
      return boxes;
   }

   public Set<Edge> getEdges() {
      return edges;
   }

   public Box getFocusBox() {
      return focusBox;
   }

   public String getScalarValue() {
      return scalarValue;
   }
}
