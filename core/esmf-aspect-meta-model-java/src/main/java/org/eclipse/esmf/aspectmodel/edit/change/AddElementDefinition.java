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

/**
 * Adds the definition of a model element to an AspectModelFile. The definition is given as a set of RDF statements (a {@link Model}).
 */
public class AddElementDefinition extends EditAspectModel {
   private final AspectModelUrn elementUrn;
   private final Model definition;
   private final URI targetLocation;

   /**
    * Adds an element definition in a given file
    *
    * @param elementUrn the element to be defined
    * @param definition the definition for the element
    * @param targetFile the target file
    */
   public AddElementDefinition( final AspectModelUrn elementUrn, final Model definition, final AspectModelFile targetFile ) {
      this( elementUrn, definition, targetFile.sourceLocation().orElseThrow( () ->
            new ModelChangeException( "Can add definition only to named file" ) ) );
   }

   /**
    * Adds an element definition in a given file
    *
    * @param elementUrn the element to be defined
    * @param definition the definition for the element
    * @param targetLocation the location of the target file
    */
   public AddElementDefinition( final AspectModelUrn elementUrn, final Model definition, final URI targetLocation ) {
      this.elementUrn = elementUrn;
      this.definition = definition;
      this.targetLocation = targetLocation;
   }

   @Override
   protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
      return aspectModelFile.sourceLocation()
            .filter( location -> location.equals( targetLocation ) )
            .map( location -> new ModelChanges( "Add definition of " + elementUrn + " in " + location,
                  definition, ModelFactory.createDefaultModel() ) )
            .orElse( ModelChanges.NONE );
   }

   @Override
   public Change reverse() {
      return new EditAspectModel() {
         @Override
         protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
            return aspectModelFile.sourceLocation()
                  .filter( location -> location.equals( targetLocation ) )
                  .map( location -> new ModelChanges( "Remove definition of " + elementUrn,
                        ModelFactory.createDefaultModel(), definition ) )
                  .orElse( ModelChanges.NONE );
         }

         @Override
         public Change reverse() {
            return AddElementDefinition.this;
         }
      };
   }
}
