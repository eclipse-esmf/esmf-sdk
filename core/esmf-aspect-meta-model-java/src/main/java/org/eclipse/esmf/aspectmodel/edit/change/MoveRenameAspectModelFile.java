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
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;

/**
 * Refactoring operation: Renames/moves a file. This is done by changing its source location.
 */
public class MoveRenameAspectModelFile extends StructuralChange {
   private final URI sourceLocation;
   private final URI newLocation;
   private ChangeGroup changes = null;

   public MoveRenameAspectModelFile( final URI sourceLocation, final URI newLocation ) {
      this.sourceLocation = sourceLocation;
      this.newLocation = newLocation;
   }

   public MoveRenameAspectModelFile( final AspectModelFile file, final URI newLocation ) {
      this( file.sourceLocation().orElseThrow( () ->
            new ModelChangeException( "Can rename only a named file" ) ), newLocation );
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      final AspectModelFile sourceFile = sourceFile( changeContext, sourceLocation );
      final AspectModelFile replacementFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( newLocation ) )
            .headerComment( sourceFile.headerComment() )
            .sourceModel( sourceFile.sourceModel() )
            .build();
      changes = new ChangeGroup(
            new RemoveAspectModelFile( sourceFile ),
            new AddAspectModelFile( replacementFile )
      );
      return changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }
}
