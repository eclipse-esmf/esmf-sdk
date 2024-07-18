package org.eclipse.esmf.aspectmodel.generator.zip;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.metamodel.AspectModel;

public class AspectModelZipGenerator {

   public static void generateZipAspectModelArchive( final AspectModel aspectModel, final String zipFilePath ) throws IOException {
      final Path zipFile = Paths.get( zipFilePath );

      try ( FileOutputStream fos = new FileOutputStream( zipFile.toFile() );
            BufferedOutputStream bos = new BufferedOutputStream( fos );
            ZipOutputStream zos = new ZipOutputStream( bos ) ) {

         for ( final AspectModelFile aspectModelFile : aspectModel.files() ) {
            addFileToZip( aspectModelFile, zos );
         }
      } catch ( IOException e ) {
         throw new IOException( "Error creating zip archive: " + zipFilePath, e );
      }
   }

   private static void addFileToZip( final AspectModelFile file, ZipOutputStream zos ) throws IOException {
      final String aspectString = AspectSerializer.INSTANCE.apply( file.aspect() );
      final String fileName = String.format( "%s/%s/%s.ttl",
            file.aspect().urn().getNamespace(),
            file.aspect().urn().getVersion(),
            file.aspect().getName() );
      final ZipEntry zipEntry = new ZipEntry( fileName );
      zos.putNextEntry( zipEntry );

      Writer writer = new OutputStreamWriter( zos );
      writer.write( aspectString );
      writer.flush();

      zos.closeEntry();
   }
}
