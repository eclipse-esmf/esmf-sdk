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

package org.eclipse.esmf.aspectmodel.edit.change;

import java.net.URI;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * RDF-level refactoring operation: Renames all occurences of a model element URN to something else. For model-level refactoring,
 * instead of this class, please use {@link RenameElement}, {@link MoveElementToOtherNamespaceExistingFile} or
 * {@link MoveElementToOtherNamespaceNewFile}.
 */
public class RenameUrn extends EditAspectModel {
   private final AspectModelUrn from;
   private final AspectModelUrn to;
   private final URI targetLocation;

   /**
    * Using this constructor implies the change should be applied only to the identified file
    *
    * @param targetLocation the location of the file to change
    * @param from the URN to change
    * @param to the URN to change it into
    */
   public RenameUrn( final URI targetLocation, final AspectModelUrn from, final AspectModelUrn to ) {
      this.from = from;
      this.to = to;
      this.targetLocation = targetLocation;
      if ( from.getName().isEmpty() || to.getName().isEmpty() ) {
         throw new ModelChangeException( "Source and target URNs must contain element names" );
      }
   }

   /**
    * Using this constructor implies the Change should be applied to all files.
    *
    * @param from the URN to change
    * @param to the URN to change it into
    */
   public RenameUrn( final AspectModelUrn from, final AspectModelUrn to ) {
      this( (URI) null, from, to );
   }

   /**
    * Using this constructor implies the change should be applied only to the given file.
    *
    * @param targetFile the target Aspect Model file
    * @param from the URN to change
    * @param to the URN to change it into
    */
   public RenameUrn( final AspectModelFile targetFile, final AspectModelUrn from, final AspectModelUrn to ) {
      this( targetFile.sourceLocation().orElseThrow(), from, to );
   }

   public AspectModelUrn from() {
      return from;
   }

   public AspectModelUrn to() {
      return to;
   }

   @Override
   protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
      if ( targetLocation != null && !aspectModelFile.sourceLocation().map( targetLocation::equals ).orElse( false ) ) {
         return ModelChanges.NONE;
      }

      final Model addModel = ModelFactory.createDefaultModel();
      final Model removeModel = ModelFactory.createDefaultModel();

      int updatedTriples = 0;
      for ( final StmtIterator it = aspectModelFile.sourceModel().listStatements(); it.hasNext(); ) {
         final Statement statement = it.next();
         boolean updateTriple = false;
         final Resource addSubject;
         final Resource removeSubject;
         final Property predicate;
         final RDFNode addObject;
         final RDFNode removeObject;

         // Handle subject
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

         // Handle predicate
         if ( statement.getPredicate().getURI().equals( from.toString() ) ) {
            predicate = addModel.createProperty( to.toString() );
            updateTriple = true;
         } else {
            predicate = statement.getPredicate();
         }

         // Handle object
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

         // Write new triple
         if ( updateTriple ) {
            addModel.add( addSubject, predicate, addObject );
            removeModel.add( removeSubject, predicate, removeObject );
            updatedTriples++;
         }
      }

      return updatedTriples > 0
            ? new ModelChanges( changeDescription(), addModel, removeModel )
            : ModelChanges.NONE;
   }

   protected String changeDescription() {
      return "Rename " + from() + " to " + to();
   }

   @Override
   public Change reverse() {
      return new RenameUrn( targetLocation, to, from );
   }
}
