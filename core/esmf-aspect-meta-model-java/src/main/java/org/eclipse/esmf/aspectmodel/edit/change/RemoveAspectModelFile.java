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
import java.util.Map;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;

/**
 * Refactoring operation: Removes an AspectModelFile from an Aspect Model
 */
public class RemoveAspectModelFile extends StructuralChange {
   private final URI locationOfFileToRemove;
   private AspectModelFile fileToRemove;

   public RemoveAspectModelFile( final URI locationOfFileToRemove ) {
      this.locationOfFileToRemove = locationOfFileToRemove;
   }

   public RemoveAspectModelFile( final AspectModelFile fileToRemove ) {
      this( fileToRemove.sourceLocation().orElseThrow( () ->
            new ModelChangeException( "Can remove only a named file" ) ) );
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      fileToRemove = sourceFile( changeContext, locationOfFileToRemove );
      changeContext.indicateFileIsRemoved( fileToRemove );
      return new ChangeReport.EntryWithDetails( "Remove file " + show( fileToRemove ),
            Map.of( "model content", fileToRemove.sourceModel() ) );
   }

   @Override
   public Change reverse() {
      return new AddAspectModelFile( fileToRemove );
   }
}
