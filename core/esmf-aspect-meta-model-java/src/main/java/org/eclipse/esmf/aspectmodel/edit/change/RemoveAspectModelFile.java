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

import java.util.Map;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;

/**
 * Refactoring operation: Removes an AspectModelFile from an Aspect Model
 */
public class RemoveAspectModelFile extends AbstractChange {
   private final AspectModelFile fileToRemove;

   public RemoveAspectModelFile( final AspectModelFile fileToRemove ) {
      this.fileToRemove = fileToRemove;
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      changeContext.indicateFileIsRemoved( fileToRemove );
      return new ChangeReport.EntryWithDetails( "Remove file " + show( fileToRemove ),
            Map.of( "model content", fileToRemove.sourceModel() ) );
   }

   @Override
   public Change reverse() {
      return new AddAspectModelFile( fileToRemove );
   }
}
