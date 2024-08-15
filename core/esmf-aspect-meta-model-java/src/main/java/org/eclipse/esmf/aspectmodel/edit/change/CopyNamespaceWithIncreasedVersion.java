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
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Namespace;

/**
 * Creates a copy of a namespace with a new version, including copies of all files in the namespace with updated URNs
 */
public class CopyNamespaceWithIncreasedVersion extends StructuralChange {
   private Change changes;

   public enum IncreaseVersion {
      MAJOR,
      MINOR,
      MICRO
   }

   private final IncreaseVersion increaseVersion;
   private final Namespace namespace;

   public CopyNamespaceWithIncreasedVersion( final Namespace namespace, final IncreaseVersion increaseVersion ) {
      this.increaseVersion = increaseVersion;
      this.namespace = namespace;
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      final List<AspectModelFile> namespaceFiles = changeContext.aspectModelFiles()
            .filter( file -> file.namespace().equals( namespace ) )
            .toList();

      final VersionNumber currentVersion = namespace.version();
      final VersionNumber nextVersion = increaseVersion( currentVersion );

      final List<Change> fileChanges = namespaceFiles.stream()
            .flatMap( file -> {
               if ( file.sourceLocation().isEmpty() ) {
                  throw new ModelChangeException(
                        "Namespace version can only be increased for namespaces with files that have source locations." );
               }
               final String sourceLocation = file.sourceLocation().get().toString();
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

               final AspectModelUrn newUrn = AspectModelUrn.fromUrn(
                     String.format( "urn:samm:%s:%s", namespace.namespaceMainPart(), nextVersion ) );
               return Stream.<Change> of(
                     new CopyAspectModelFile( file, newLocation ),
                     new RenameNamespace( newLocation, namespace.urn(), newUrn ),
                     new ChangeRdfPrefixes( newLocation, Map.of( "", newUrn.getUrnPrefix() ) )
               );
            } ).toList();

      changes = new ChangeGroup( "Copy namespace " + namespace.urn() + " to new version " + nextVersion, fileChanges );
      return changes.fire( changeContext );
   }

   private VersionNumber increaseVersion( final VersionNumber previous ) {
      return switch ( increaseVersion ) {
         case MAJOR -> previous.nextMajor();
         case MINOR -> previous.nextMinor();
         case MICRO -> previous.nextMicro();
      };
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }
}
