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
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Refactoring operation: Copy an existing Aspect Model file to a new location.
 * Note that this operation only makes sense with additional changes, such as removing/adding elements or adjusting the
 * default RDF namespace.
 */
public class CopyAspectModelFile extends StructuralChange {
   private final AspectModelFile source;
   private final URI sourceLocation;
   private final URI newLocation;
   private Change addFileChange;

   public CopyAspectModelFile( final URI sourceLocation, final URI newLocation ) {
      source = null;
      this.sourceLocation = sourceLocation;
      this.newLocation = newLocation;
   }

   public CopyAspectModelFile( final AspectModelFile source, final URI newLocation ) {
      this.source = source;
      sourceLocation = null;
      this.newLocation = newLocation;
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      final AspectModelFile sourceFile = source == null
            ? sourceFile( changeContext, sourceLocation )
            : source;

      final Model newSourceModel = ModelFactory.createDefaultModel();
      newSourceModel.add( sourceFile.sourceModel() );
      addFileChange = new AddAspectModelFile(
            RawAspectModelFileBuilder.builder()
                  .sourceModel( newSourceModel )
                  .sourceLocation( Optional.of( newLocation ) )
                  .headerComment( sourceFile.headerComment() )
                  .build() );
      return addFileChange.fire( changeContext );
   }

   @Override
   public Change reverse() {
      return addFileChange.reverse();
   }
}
