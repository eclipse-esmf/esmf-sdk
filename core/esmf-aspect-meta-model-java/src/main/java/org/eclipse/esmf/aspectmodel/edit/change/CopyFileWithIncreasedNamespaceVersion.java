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
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Namespace;

public class CopyFileWithIncreasedNamespaceVersion extends StructuralChange {
   private final IncreaseVersion increaseVersion;
   private final URI targetLocation;
   private Change changes;

   public CopyFileWithIncreasedNamespaceVersion( final AspectModelFile file, final IncreaseVersion increaseVersion ) {
      this.increaseVersion = increaseVersion;
      targetLocation = file.sourceLocation().orElseThrow( () ->
            new ModelChangeException( "Version can only be increased for files that have source locations." ) );
   }

   public CopyFileWithIncreasedNamespaceVersion( final URI targetLocation, final IncreaseVersion increaseVersion ) {
      this.increaseVersion = increaseVersion;
      this.targetLocation = targetLocation;
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      final AspectModelFile file = sourceFile( changeContext, targetLocation );
      final Namespace namespace = file.namespace();

      final VersionNumber currentVersion = namespace.version();
      final VersionNumber nextVersion = increaseVersion.apply( currentVersion );
      final String sourceLocation = file.sourceLocation().orElseThrow().toString();
      final String locationPart = namespace.namespaceMainPart() + "/" + namespace.version().toString();
      final int locationIndex = sourceLocation.lastIndexOf( locationPart );
      if ( locationIndex == -1 ) {
         throw new ModelChangeException( "Don't know how to update Aspect Model file location: " + sourceLocation );
      }
      // Build new file location URI with the version replaced with the new one
      final URI newLocation = URI.create(
            sourceLocation.substring( 0, locationIndex )
                  + namespace.namespaceMainPart() + "/" + nextVersion.toString()
                  + sourceLocation.substring( locationIndex + locationPart.length() ) );

      final AspectModelUrn newUrn = AspectModelUrn.fromParts( namespace.namespaceMainPart(), nextVersion.toString() );

      changes = new ChangeGroup(
            "Copy file " + show( file ) + " to new version " + nextVersion,
            new CopyAspectModelFile( file, newLocation ),
            new RenameNamespace( newLocation, namespace.urn(), newUrn ),
            new ChangeRdfPrefixes( newLocation, Map.of( "", newUrn.getUrnPrefix() ) )
      );
      return changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }
}
