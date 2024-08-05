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

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class MoveElementToOtherFile extends StructuralChange {
   private final AspectModelUrn elementUrn;
   private final Predicate<AspectModelFile> targetFileSelector;
   private ChangeGroup changes = null;

   public MoveElementToOtherFile( final AspectModelUrn elementUrn, final Predicate<AspectModelFile> targetFileSelector ) {
      this.elementUrn = elementUrn;
      this.targetFileSelector = targetFileSelector;
   }

   private void prepareChanges( final ChangeContext changeContext ) {
      if ( changes != null ) {
         return;
      }

      final List<AspectModelFile> targetFiles = changeContext.aspectModel().files().stream().filter( targetFileSelector ).toList();
      if ( targetFiles.size() > 1 ) {
         throw new ModelChangeException( "Can not determine target file to move element" );
      }
      if ( targetFiles.isEmpty() ) {
         return;
      }
      final AspectModelFile targetFile = targetFiles.get( 0 );

      // Find source file with element definition
      final AspectModelFile sourceFile = sourceFile( changeContext, elementUrn );
      if ( sourceFile == targetFile ) {
         return;
      }
      final Resource elementResource = sourceFile.sourceModel().createResource( elementUrn.toString() );
      final Model definition = RdfUtil.getModelElementDefinition( elementResource );

      // Perform move of element definition
      changes = new ChangeGroup(
            new RemoveElementDefinition( elementUrn ),
            new AddElementDefinition( elementUrn, definition, targetFile )
      );
   }

   @Override
   public void fire( final ChangeContext changeContext ) {
      prepareChanges( changeContext );
      changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }

   @Override
   public ChangeReport report( final ChangeContext changeContext ) {
      prepareChanges( changeContext );
      return changes.report( changeContext );
   }
}
