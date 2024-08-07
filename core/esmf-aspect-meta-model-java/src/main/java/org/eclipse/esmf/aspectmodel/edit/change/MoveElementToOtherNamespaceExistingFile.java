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
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;

public class MoveElementToOtherNamespaceExistingFile extends StructuralChange {
   private final AspectModelUrn elementUrn;
   private final AspectModelFile targetFile;
   private final Namespace targetNamespace;
   private ChangeGroup changes = null;

   public MoveElementToOtherNamespaceExistingFile( final ModelElement modelElement, final AspectModelFile targetFile,
         final Namespace targetNamespace ) {
      this( modelElement.urn(), targetFile, targetNamespace );
      if ( modelElement.isAnonymous() ) {
         throw new ModelChangeException( "Can not move anonymous model element" );
      }
   }

   public MoveElementToOtherNamespaceExistingFile( final AspectModelUrn elementUrn, final AspectModelFile targetFile,
         final Namespace targetNamespace ) {
      this.elementUrn = elementUrn;
      this.targetFile = targetFile;
      this.targetNamespace = targetNamespace;
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      changes = new ChangeGroup(
            "Move element " + elementUrn + " to file " + show( targetFile ) + " in namespace " + targetNamespace.elementUrnPrefix(),
            new MoveElementToExistingFile( elementUrn, targetFile ),
            new RenameUrn( elementUrn, AspectModelUrn.fromUrn( targetNamespace.elementUrnPrefix() + elementUrn.getName() ) )
      );
      return changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }
}
