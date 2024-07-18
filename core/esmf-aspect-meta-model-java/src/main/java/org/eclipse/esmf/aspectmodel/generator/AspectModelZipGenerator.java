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

package org.eclipse.esmf.aspectmodel.generator;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.metamodel.AspectModel;

public class AspectModelZipGenerator {

   private static final int BUFFER_SIZE = 4096;

   public static void generateZipAspectModelArchive( final AspectModel aspectModel, final String zipFilePath ) throws IOException {
      Path zipFile = Files.createFile( Paths.get( zipFilePath ) );
      try ( FileOutputStream fos = new FileOutputStream( zipFilePath );
            BufferedOutputStream bos = new BufferedOutputStream( fos );
            ZipOutputStream zos = new ZipOutputStream( bos ) ) {

         for ( final AspectModelFile aspectModelFile : aspectModel.files() ) {
            addFileToZip( aspectModelFile, zos );
         }
      }
   }

   private static void addFileToZip( final AspectModelFile file, final ZipOutputStream zos ) throws IOException {
      final String aspectString = file.aspect().toString();
//      final String aspectString = AspectSerializer.INSTANCE.apply( aspect );
      final String fileName = String.format( "%s/%s/%s.ttl", file.aspect().urn().getNamespace(), file.aspect().urn().getVersion(),
            file.aspect().getName() );
      final ZipEntry zipEntry = new ZipEntry( fileName );
      zos.putNextEntry( zipEntry );

      try ( Writer writer = new BufferedWriter( new OutputStreamWriter( zos ) ) ) {
         writer.write( aspectString );
      } catch ( IOException exception ) {
         throw new IOException( "Could not write to zip entry: " + fileName, exception );
      } finally {
         zos.closeEntry();
      }
   }
}
