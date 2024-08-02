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
import org.eclipse.esmf.aspectmodel.edit.RdfUtil;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;

public class MoveElementToNewFile extends StructuralChange {
   private final List<String> headerComment;
   private final Optional<URI> sourceLocation;
   private final AspectModelUrn elementUrn;
   private ChangeGroup changes = null;

   public MoveElementToNewFile( final ModelElement modelElement, final List<String> headerComment, final Optional<URI> sourceLocation ) {
      this( modelElement.urn(), headerComment, sourceLocation );
      if ( modelElement.isAnonymous() ) {
         throw new ModelChangeException( "Can not move anonymous model element" );
      }
   }

   public MoveElementToNewFile( final AspectModelUrn elementUrn, final List<String> headerComment, final Optional<URI> sourceLocation ) {
      this.headerComment = headerComment;
      this.sourceLocation = sourceLocation;
      this.elementUrn = elementUrn;
   }

   private void prepareChanges( final ChangeContext changeContext ) {
      if ( changes != null ) {
         return;
      }

      // Prepare new file
      final RawAspectModelFile targetFile = RawAspectModelFileBuilder.builder()
            .headerComment( headerComment )
            .sourceLocation( sourceLocation )
            .build();
      final Model targetModel = targetFile.sourceModel();
      targetModel.setNsPrefix( SammNs.SAMM.getShortForm(), SammNs.SAMM.getNamespace() );
      targetModel.setNsPrefix( SammNs.SAMMC.getShortForm(), SammNs.SAMMC.getNamespace() );
      targetModel.setNsPrefix( SammNs.UNIT.getShortForm(), SammNs.UNIT.getNamespace() );
      targetModel.setNsPrefix( SammNs.SAMME.getShortForm(), SammNs.SAMME.getNamespace() );
      targetModel.setNsPrefix( "xsd", XSD.NS );
      targetModel.setNsPrefix( "", elementUrn.getUrnPrefix() );

      // Find source file with element definition
      final Model sourceModel = sourceFile( changeContext, elementUrn ).sourceModel();
      final Resource elementResource = sourceModel.createResource( elementUrn.toString() );
      final Model definition = RdfUtil.getModelElementDefinition( elementResource );

      // Perform move of element definition
      changes = new ChangeGroup(
            new RemoveElementDefinition( elementUrn ),
            new AddAspectModelFile( targetFile ),
            new AddElementDefinition( elementUrn, definition, targetFile )
      );
   }

   @Override
   public void fire( final ChangeContext changeContext ) {
      prepareChanges( changeContext );
      changes.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return changes.reverse();
   }

   @Override
   public ChangeReport report( final ChangeContext changeContext ) {
      prepareChanges( changeContext );
      return changes.report( changeContext );
   }
}
