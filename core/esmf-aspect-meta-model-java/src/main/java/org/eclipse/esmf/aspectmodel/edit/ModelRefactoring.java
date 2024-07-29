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

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelBuilder;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.impl.DefaultAspectModel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class ModelRefactoring extends AspectModelBuilder {
   private final Deque<Change> undoStack = new ArrayDeque<>();
   private final Deque<Change> redoStack = new ArrayDeque<>();
   private final DefaultAspectModel aspectModel;

   public ModelRefactoring( final AspectModel aspectModel ) {
      if ( !( aspectModel instanceof DefaultAspectModel ) ) {
         throw new RuntimeException();
      }
      this.aspectModel = (DefaultAspectModel) aspectModel;
   }

   public synchronized void applyChange( final Change change ) {
      change.applyChange( aspectModel );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
   }

   private void updateAspectModelAfterChange() {
      final AspectModel updatedModel = buildAspectModel( aspectModel.files() );
      aspectModel.setMergedModel( updatedModel.mergedModel() );
      aspectModel.setElements( updatedModel.elements() );
   }

   public synchronized void undoChange() {
      if ( undoStack.isEmpty() ) {
         return;
      }
      final Change change = undoStack.pollLast();
      change.applyChange( aspectModel );
      updateAspectModelAfterChange();
      redoStack.offerLast( change.reverse() );
   }

   public synchronized void redoChange() {
      if ( redoStack.isEmpty() ) {
         return;
      }
      final Change change = redoStack.pollLast();
      change.applyChange( aspectModel );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
   }

   public class ChangeGroup implements Change {
      private final List<Change> changes;

      public ChangeGroup( final List<Change> changes ) {
         this.changes = changes;
      }

      @Override
      public void applyChange( final AspectModel aspectModel ) {
         changes().forEach( change -> change.applyChange( aspectModel ) );
      }

      @Override
      public Change reverse() {
         return new ChangeGroup( changes().stream().map( Change::reverse ).toList() );
      }

      public List<Change> changes() {
         return changes;
      }

      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visitChangeGroup( this );
      }
   }

   public abstract class EditAspectModel implements Change {
      protected record ModelChanges( Model add, Model remove ) {
      }

      private Map<AspectModelFile, ModelChanges> changesPerFile = null;

      synchronized protected Map<AspectModelFile, ModelChanges> changesPerFile() {
         if ( changesPerFile == null ) {
            changesPerFile = aspectModel.files().stream()
                  .map( file -> new AbstractMap.SimpleEntry<>( file, calculateChangesPerFile( file ) ) )
                  .collect( Collectors.toMap( AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue ) );
         }
         return changesPerFile;
      }

      abstract protected ModelChanges calculateChangesPerFile( AspectModelFile aspectModelFile );

      @Override
      public void applyChange( final AspectModel aspectModel ) {
         changesPerFile().forEach( ( file, modelChanges ) -> {
            if ( aspectModel.files().contains( file ) ) {
               file.sourceModel().add( modelChanges.add() );
               file.sourceModel().remove( modelChanges.remove() );
            }
         } );
      }
   }

   public class RenameUrn extends EditAspectModel {
      private final AspectModelUrn from;
      private final AspectModelUrn to;

      public RenameUrn( final AspectModelUrn from, final AspectModelUrn to ) {
         this.from = from;
         this.to = to;
      }

      public AspectModelUrn from() {
         return from;
      }

      public AspectModelUrn to() {
         return to;
      }

      @Override
      protected ModelChanges calculateChangesPerFile( final AspectModelFile aspectModelFile ) {
         final Model addModel = ModelFactory.createDefaultModel();
         final Model removeModel = ModelFactory.createDefaultModel();

         for ( final StmtIterator it = aspectModelFile.sourceModel().listStatements(); it.hasNext(); ) {
            final Statement statement = it.next();
            boolean updateTriple = false;
            final Resource addSubject;
            final Resource removeSubject;
            final Property predicate;
            final RDFNode addObject;
            final RDFNode removeObject;
            if ( statement.getSubject().isURIResource() ) {
               if ( statement.getSubject().getURI().equals( from.toString() ) ) {
                  addSubject = addModel.createResource( to.toString() );
                  removeSubject = removeModel.createResource( from.toString() );
                  updateTriple = true;
               } else {
                  addSubject = statement.getSubject();
                  removeSubject = statement.getSubject();
               }
            } else {
               addSubject = addModel.createResource( statement.getSubject().getId() );
               removeSubject = removeModel.createResource( statement.getSubject().getId() );
            }

            if ( statement.getPredicate().getURI().equals( from.toString() ) ) {
               predicate = addModel.createProperty( to.toString() );
               updateTriple = true;
            } else {
               predicate = statement.getPredicate();
            }
            if ( statement.getObject().isURIResource() && statement.getObject().asResource().getURI().equals( from.toString() ) ) {
               addObject = addModel.createResource( to.toString() );
               removeObject = removeModel.createResource( from.toString() );
               updateTriple = true;
            } else {
               if ( statement.getObject().isAnon() ) {
                  addObject = addModel.createResource( statement.getObject().asResource().getId() );
                  removeObject = removeModel.createResource( statement.getObject().asResource().getId() );
               } else {
                  addObject = statement.getObject();
                  removeObject = statement.getObject();
               }
            }
            if ( updateTriple ) {
               addModel.add( addSubject, predicate, addObject );
               removeModel.add( removeSubject, predicate, removeObject );
            }
         }

         return new ModelChanges( addModel, removeModel );
      }

      @Override
      public Change reverse() {
         return new RenameUrn( to, from );
      }

      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visitRenameUrn( this );
      }
   }

   public class RenameElement extends RenameUrn {
      public RenameElement( final AspectModelUrn urn, final String name ) {
         super( urn, AspectModelUrn.fromUrn( urn.getUrnPrefix() + name ) );
      }

      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visitRenameElement( this );
      }
   }
}
