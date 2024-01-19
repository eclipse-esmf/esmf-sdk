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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An abstract representation of a diagram consisting of {@link Box}es and {@link Edge}s between them
 */
public class Diagram {
   private final Box focusBox;
   private final Set<Box> boxes;
   private final Set<Edge> edges;

   // Used for the special case where a value element is rendered as a string (as part of a parent's attribute)
   private String scalarValue = null;

   public Diagram( final Box focusBox ) {
      this.focusBox = focusBox;
      boxes = new HashSet<>();
      addBox( focusBox );
      edges = new HashSet<>();
   }

   static final Diagram EMPTY = new Diagram( null );

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

   public void add( final Diagram diagram ) {
      addBoxes( diagram.getBoxes() );
      addEdges( diagram.getEdges() );
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

   /**
    * A box in the diagram with a prototype, title and a list of entries
    */
   public static class Box {
      private String prototype;
      private final String title;
      private final List<String> entries = new ArrayList<>();

      public Box( final String prototype, final String title ) {
         this.prototype = prototype;
         this.title = title;
      }

      public void addEntry( final List<String> entry ) {
         entries.addAll( entry );
      }

      public void setPrototype( final String prototype ) {
         this.prototype = prototype;
      }

      public String getPrototype() {
         return prototype;
      }

      public String getTitle() {
         return title;
      }

      public List<String> getEntries() {
         return entries;
      }
   }

   /**
    * An edge between two {@link Box}es
    * @param from the source box
    * @param to the target box
    * @param label the label on the edge
    */
   public record Edge( Box from, Box to, String label ) {
   }
}
