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
 * RDF-level refactoring operation: Renames all occurences of a namespace in model element URNs to something else.
 */
public class RenameNamespace extends EditAspectModel {
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
   public RenameNamespace( final URI targetLocation, final AspectModelUrn from, final AspectModelUrn to ) {
      if ( !from.getName().isEmpty() || !to.getName().isEmpty() ) {
         throw new ModelChangeException( "Source and target URNs must not contain element names" );
      }
      this.from = from;
      this.to = to;
      this.targetLocation = targetLocation;
   }

   /**
    * Using this constructor implies the Change should be applied to all files.
    *
    * @param from the URN to change
    * @param to the URN to change it into
    */
   public RenameNamespace( final AspectModelUrn from, final AspectModelUrn to ) {
      this( (URI) null, from, to );
   }

   /**
    * Using this constructor implies the change should be applied only to the given file.
    *
    * @param targetFile the target Aspect Model file
    * @param from the URN to change
    * @param to the URN to change it into
    */
   public RenameNamespace( final AspectModelFile targetFile, final AspectModelUrn from, final AspectModelUrn to ) {
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
            final String subjectUri = statement.getSubject().getURI();
            if ( subjectUri.startsWith( from.toString() + "#" ) ) {
               addSubject = addModel.createResource( to.toString() + subjectUri.substring( from.toString().length() ) );
               removeSubject = removeModel.createResource( subjectUri );
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
         final String predicateUri = statement.getPredicate().getURI();
         if ( predicateUri.startsWith( from.toString() + "#" ) ) {
            predicate = addModel.createProperty( to.toString() + predicateUri.substring( from.toString().length() ) );
            updateTriple = true;
         } else {
            predicate = statement.getPredicate();
         }

         // Handle object
         if ( statement.getObject().isURIResource() ) {
            final String objectUri = statement.getObject().asResource().getURI();
            if ( objectUri.startsWith( from.toString() + "#" ) ) {
               addObject = addModel.createResource( to.toString() + objectUri.substring( from.toString().length() ) );
               removeObject = addModel.createResource( objectUri );
            } else {
               addObject = statement.getObject();
               removeObject = statement.getObject();
            }
         } else if ( statement.getObject().isAnon() ) {
            addObject = addModel.createResource( statement.getObject().asResource().getId() );
            removeObject = removeModel.createResource( statement.getObject().asResource().getId() );
         } else {
            addObject = statement.getObject();
            removeObject = statement.getObject();
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
      return new RenameNamespace( targetLocation, to, from );
   }
}
