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

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.metamodel.Namespace;

/**
 * Creates a copy of a namespace with a new version, including copies of all files in the namespace with updated URNs
 */
public class CopyNamespaceWithIncreasedVersion extends StructuralChange {
   private Change changes;

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
      final VersionNumber nextVersion = increaseVersion.apply( currentVersion );
      final List<Change> fileChanges = namespaceFiles.stream()
            .<Change> map( file -> new CopyFileWithIncreasedNamespaceVersion( file, increaseVersion ) )
            .toList();
      changes = new ChangeGroup( "Copy namespace " + namespace.urn() + " to new version " + nextVersion, fileChanges );
      return changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }
}
