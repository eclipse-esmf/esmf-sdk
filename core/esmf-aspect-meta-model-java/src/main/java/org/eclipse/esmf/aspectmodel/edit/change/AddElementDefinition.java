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

public class AddElementDefinition extends EditAspectModel {
   private final AspectModelUrn elementUrn;
   private final Model definition;
   private final AspectModelFile targetFile;

   public AddElementDefinition( final AspectModelUrn elementUrn, final Model definition, final AspectModelFile targetFile ) {
      this.elementUrn = elementUrn;
      this.definition = definition;
      this.targetFile = targetFile;
   }

   @Override
   protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
      return aspectModelFile.equals( targetFile )
            ? new ModelChanges( definition, ModelFactory.createDefaultModel(),
            "Add definition of " + elementUrn )
            : ModelChanges.NONE;
   }

   @Override
   public Change reverse() {
      return new EditAspectModel() {
         @Override
         protected ModelChanges calculateChangesForFile( final AspectModelFile aspectModelFile ) {
            return aspectModelFile.sourceLocation().equals( targetFile.sourceLocation() )
                  ? new ModelChanges( ModelFactory.createDefaultModel(), definition,
                  "Remove definition of " + elementUrn )
                  : ModelChanges.NONE;
         }

         @Override
         public Change reverse() {
            return AddElementDefinition.this;
         }
      };
   }
}
