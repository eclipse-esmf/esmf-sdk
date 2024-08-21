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
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

/**
 * Refactoring operation: Moves a model element to another, existing file in the same namespace.
 */
public class MoveElementToExistingFile extends StructuralChange {
   private final AspectModelUrn elementUrn;
   private final Optional<URI> targetFileLocation;
   private ChangeGroup changes = null;

   public MoveElementToExistingFile( final ModelElement modelElement, final AspectModelFile targetFile ) {
      this( modelElement.urn(), targetFile );
      if ( modelElement.isAnonymous() ) {
         throw new ModelChangeException( "Can not move anonymous model element" );
      }
   }

   public MoveElementToExistingFile( final AspectModelUrn elementUrn, final AspectModelFile targetFile ) {
      this.elementUrn = elementUrn;
      targetFileLocation = targetFile.sourceLocation();
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      final AspectModelFile targetFile = changeContext.aspectModelFiles()
            .filter( file -> file.sourceLocation().equals( targetFileLocation ) )
            .findFirst()
            .orElseThrow( () -> new ModelChangeException( "Can not determine target file to move element" ) );

      // Find source file with element definition
      final AspectModelFile sourceFile = sourceFile( changeContext, elementUrn );
      if ( sourceFile == targetFile ) {
         return ChangeReport.NO_CHANGES;
      }
      final Resource elementResource = sourceFile.sourceModel().createResource( elementUrn.toString() );
      final Model definition = RdfUtil.getModelElementDefinition( elementResource );

      // Perform move of element definition
      changes = new ChangeGroup(
            "Move element " + elementUrn + " to file " + show( targetFile ),
            new RemoveElementDefinition( elementUrn ),
            new AddElementDefinition( elementUrn, definition, targetFile )
      );

      return changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }
}
