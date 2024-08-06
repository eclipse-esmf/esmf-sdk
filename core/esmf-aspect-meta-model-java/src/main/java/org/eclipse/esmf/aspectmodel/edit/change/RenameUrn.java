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

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

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
   protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
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
            updatedTriples++;
         }
      }

      return updatedTriples > 0
            ? new ModelChanges( addModel, removeModel, changeDescription() )
            : ModelChanges.NONE;
   }

   protected String changeDescription() {
      return "Rename " + from() + " to " + to();
   }

   @Override
   public Change reverse() {
      return new RenameUrn( to, from );
   }
}
