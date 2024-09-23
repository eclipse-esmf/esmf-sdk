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
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;

/**
 * Refactoring operation: Moves a model element to a new file in another namespace.
 */
public class MoveElementToOtherNamespaceNewFile extends StructuralChange {
   private final List<String> headerComment;
   private final URI targetLocation;
   private final AspectModelUrn elementUrn;
   private final Namespace targetNamespace;
   private ChangeGroup changes = null;

   /**
    * Move a model element to a new file with a given location in another namespace
    *
    * @param modelElement the model element
    * @param targetNamespace the target namespace
    * @param targetLocation the location for the new file
    */
   public MoveElementToOtherNamespaceNewFile( final ModelElement modelElement, final Namespace targetNamespace, final URI targetLocation ) {
      this( modelElement, targetNamespace, null, targetLocation );
   }

   /**
    * Move a model element to a new file with a given location in another namespace
    *
    * @param modelElement the model element
    * @param targetNamespace the target namespace
    * @param headerComment the header comment to insert at the top of the file
    * @param targetLocation the location for the new file
    */
   public MoveElementToOtherNamespaceNewFile( final ModelElement modelElement, final Namespace targetNamespace,
         final List<String> headerComment, final URI targetLocation ) {
      this( modelElement.urn(), targetNamespace, headerComment, targetLocation );
      if ( modelElement.isAnonymous() ) {
         throw new ModelChangeException( "Can not move anonymous model element" );
      }
   }

   /**
    * Move a model element to a new file with a given location in another namespace
    *
    * @param elementUrn the URN of the element to move
    * @param targetNamespace the target namespace
    * @param targetLocation the location for the new file
    */
   public MoveElementToOtherNamespaceNewFile( final AspectModelUrn elementUrn, final Namespace targetNamespace, final URI targetLocation ) {
      this( elementUrn, targetNamespace, null, targetLocation );
   }

   /**
    * Move a model element to a new file with a given location in another namespace
    *
    * @param elementUrn the URN of the element to move
    * @param targetNamespace the target namespace
    * @param headerComment the header comment to insert at the top of the file
    * @param targetLocation the location for the new file
    */
   public MoveElementToOtherNamespaceNewFile( final AspectModelUrn elementUrn, final Namespace targetNamespace,
         final List<String> headerComment, final URI targetLocation ) {
      this.elementUrn = elementUrn;
      this.targetNamespace = targetNamespace;
      this.headerComment = headerComment;
      this.targetLocation = targetLocation;
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      final List<String> fileHeader = Optional.ofNullable( headerComment )
            .or( () -> Optional.ofNullable( changeContext.config().defaultFileHeader() ) )
            .orElse( List.of() );
      changes = new ChangeGroup(
            "Move element " + elementUrn + " to new file " + show( targetLocation )
                  + " in namespace " + targetNamespace.elementUrnPrefix(),
            new MoveElementToNewFile( elementUrn, fileHeader, targetLocation ),
            new RenameUrn( elementUrn, AspectModelUrn.fromUrn( targetNamespace.elementUrnPrefix() + elementUrn.getName() ) )
      );
      return changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }
}
