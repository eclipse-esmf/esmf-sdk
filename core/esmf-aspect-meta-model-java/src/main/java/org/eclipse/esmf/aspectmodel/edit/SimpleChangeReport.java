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

package org.eclipse.esmf.aspectmodel.edit;

import java.io.StringWriter;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;

import org.apache.jena.rdf.model.Model;

public class SimpleChangeReport implements Change.Visitor<String> {
   @Override
   public String visitChangeGroup( final ModelRefactoring.ChangeGroup changeGroup ) {
      return changeGroup.changes().stream().map( change -> change.accept( this ) )
            .collect( Collectors.joining( "\n", "- ", "" ) );
   }

   @Override
   public String visitRenameElement( final ModelRefactoring.RenameElement renameElement ) {
      return renameElement.changesPerFile().entrySet().stream().map( entry -> {
         final AspectModelFile file = entry.getKey();
         final ModelRefactoring.EditAspectModel.ModelChanges changes = entry.getValue();

         final String add = indent( modelToString( changes.add() ) );
         final String remove = indent( modelToString( changes.remove() ) );
         return String.format( "In file %s:%nAdd model:%n  ---%n%s%n  ---%nRemove model:%n  ---%n%s%n  ---%n", file.toString(), add, remove );
      } ).collect( Collectors.joining() );
   }

   private String indent( final String s ) {
      return s.lines().map( line -> "  " + line ).collect( Collectors.joining( "\n" ) );
   }

   private String modelToString( final Model model ) {
      final StringWriter stringWriter = new StringWriter();
      model.write( stringWriter, "TURTLE" );
      return stringWriter.toString();
   }

   @Override
   public String visitRenameUrn( final ModelRefactoring.RenameUrn renameUrn ) {
      return renameUrn.changesPerFile().entrySet().stream().map( entry -> {
         final AspectModelFile file = entry.getKey();
         final ModelRefactoring.EditAspectModel.ModelChanges changes = entry.getValue();

         final String add = indent( modelToString( changes.add() ) );
         final String remove = indent( modelToString( changes.remove() ) );
         return String.format( "In file %s:%nAdd model:%n  ---%n%s%nRemove model:%n  ---%n%s%n", file.toString(), add, remove );
      } ).collect( Collectors.joining() );
   }
}
