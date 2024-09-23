/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.edit;

import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

/**
 * A structured representation of a number of {@link Change}s
 */
public sealed interface ChangeReport {
   ChangeReport NO_CHANGES = new NoChanges();

   /**
    * Indicates that no changes are performed
    */
   record NoChanges( ) implements ChangeReport {
      @Override
      public <T, C> T accept( final Visitor<T, C> visitor, final C context ) {
         return visitor.visitNoChanges( this, context );
      }
   }

   /**
    * Simple textual description of a change
    *
    * @param text the description text
    */
   record SimpleEntry( String text ) implements ChangeReport {
      @Override
      public <T, C> T accept( final Visitor<T, C> visitor, final C context ) {
         return visitor.visitSimpleEntry( this, context );
      }
   }

   /**
    * Description of adding/removing parts of RDF models
    *
    * @param summary the summary
    * @param details detailed descriptions of models used for changes
    */
   record EntryWithDetails( String summary, Map<String, Model> details ) implements ChangeReport {
      @Override
      public <T, C> T accept( final Visitor<T, C> visitor, final C context ) {
         return visitor.visitEntryWithDetails( this, context );
      }
   }

   /**
    * Structures multiple changes with an optional summary
    *
    * @param summary the summary
    * @param entries the nested reports
    */
   record MultipleEntries( String summary, List<ChangeReport> entries ) implements ChangeReport {
      public MultipleEntries( final List<ChangeReport> entries ) {
         this( null, entries );
      }

      @Override
      public <T, C> T accept( final Visitor<T, C> visitor, final C context ) {
         return visitor.visitMultipleEntries( this, context );
      }
   }

   <T, C> T accept( Visitor<T, C> visitor, C context );

   interface Visitor<T, C> {
      T visitNoChanges( NoChanges noChanges, C context );

      T visitSimpleEntry( SimpleEntry simpleEntry, C context );

      T visitEntryWithDetails( EntryWithDetails entryWithDetails, C context );

      T visitMultipleEntries( MultipleEntries multipleEntries, C context );
   }
}
