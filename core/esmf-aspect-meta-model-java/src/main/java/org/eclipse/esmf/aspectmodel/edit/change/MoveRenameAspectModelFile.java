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

public class MoveRenameAspectModelFile extends StructuralChange {
   private final AspectModelFile file;
   private final Optional<URI> newLocation;
   private ChangeGroup changes = null;

   public MoveRenameAspectModelFile( final AspectModelFile file, final URI newLocation ) {
      this( file, Optional.of( newLocation ) );
   }

   public MoveRenameAspectModelFile( final AspectModelFile file, final Optional<URI> newLocation ) {
      this.file = file;
      this.newLocation = newLocation;
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      final AspectModelFile targetFile = changeContext.aspectModelFiles()
            .filter( file -> file.sourceLocation().equals( file.sourceLocation() ) )
            .findFirst()
            .orElseThrow( () -> new ModelChangeException( "Can not find file to move/rename" ) );

      final AspectModelFile replacementFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( newLocation )
            .headerComment( targetFile.headerComment() )
            .sourceModel( targetFile.sourceModel() )
            .build();
      changes = new ChangeGroup(
            new RemoveAspectModelFile( targetFile ),
            new AddAspectModelFile( replacementFile )
      );
      return changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }
}
