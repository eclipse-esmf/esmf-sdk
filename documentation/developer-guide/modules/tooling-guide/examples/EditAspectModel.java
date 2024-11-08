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

package examples;

// tag::imports[]
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.esmf.aspectmodel.edit.AspectChangeManager;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfig;
import org.eclipse.esmf.aspectmodel.edit.AspectChangeManagerConfigBuilder;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeGroup;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.edit.ChangeReportFormatter;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToNewFile;
import org.eclipse.esmf.aspectmodel.edit.change.RenameElement;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;
// end::imports[]

import java.io.File;
import org.junit.jupiter.api.Test;

public class EditAspectModel {
   @Test
   void testEditAspectModel() {
      // tag::editModel[]
      final AspectModel aspectModel = new AspectModelLoader().load(
            // a File, InputStream or AspectModelUrn
            // end::editModel[]
            new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" )
            // tag::editModel[]
      );

      // All changes to an Aspect Model are done using an AspectChangeManager.
      // Optionally, you can pass a config object as first constructor argument:
      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( true )
            .defaultFileHeader( List.of( "Generated on "
                  + new SimpleDateFormat( "dd-MM-yyyy" ).format( new Date() ) ) )
            .build();
      final AspectChangeManager changeManager = new AspectChangeManager( config, aspectModel );

      // You can create a single change, or you can combine multiple changes into a group.
      // For all possible refactoring operations, see classes implementing the Change interface.
      final Change refactorModel = new ChangeGroup( List.of(
            // Rename an element. This works with with samm:Aspect and all other model elements.
            new RenameElement( aspectModel.aspect(), "MyAspect" ),
            // Move an element to a new Aspect Model file in the same namespace.
            new MoveElementToNewFile(
                  // The element to move.
                  aspectModel.aspect().getProperties().get( 0 ),
                  // If you intend to write the model to the file system, set the location
                  // for the newly created file to some sensible location  here.
                  URI.create( "file:///temp/out.ttl" ) )
      ) );

      // Apply the changes and get a report that summerizes the changes.
      final ChangeReport changeReport = changeManager.applyChange( refactorModel );

      // If you want to display the change report, you can serialize it to a string:
      ChangeReportFormatter.INSTANCE.apply( changeReport, config );

      // Alternatively, you can also get views on collections containing modified/
      // added/removed files for the last applied change.
      changeManager.createdFiles().forEach( file -> System.out.println( "Created: " + file ) );
      changeManager.modifiedFiles().forEach( file -> System.out.println( "Modified: " + file ) );
      changeManager.removedFiles().forEach( file -> System.out.println( "Removed: " + file ) );

      // At this point, you could use the AspectChangeManager's undo() method to revert
      // the last change (refactorModel in this case); afterwards you can also redo().
      // This functionality is mainly interesting when refactoring the Aspect Model
      // interactively.

      // If you want to write the model changes to the file system, use the AspectSerializer.
      // Each AspectModelFile will be written to its respective source location.
      // Alternatively, the AspectSerializer also provides a method to write an AspectModelFile
      // into a String.
      // end::editModel[]
      /*
      // tag::editModel[]
      AspectSerializer.INSTANCE.write( aspectModel );
      // end::editModel[]
      */
   }
}
