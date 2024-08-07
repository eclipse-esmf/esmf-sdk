package org.eclipse.esmf.aspectmodel.generator.zip;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.metamodel.AspectModel;

import org.apache.commons.lang3.function.TriConsumer;

public class AspectModelNamespacePackageCreator implements TriConsumer<AspectModel, OutputStream, String> {

   private static final String BASE_ARCHIVE_FORMAT_PATH = "aspect-models/";

   public static final AspectModelNamespacePackageCreator INSTANCE = new AspectModelNamespacePackageCreator();

   private AspectModelNamespacePackageCreator() {
   }

   @Override
   public void accept( final AspectModel aspectModel, final OutputStream outputStream, final String rootPath ) {
      try ( FileOutputStream fos = (FileOutputStream) outputStream;
            BufferedOutputStream bos = new BufferedOutputStream( fos );
            ZipOutputStream zos = new ZipOutputStream( bos ) ) {

         for ( final AspectModelFile aspectModelFile : aspectModel.files() ) {
            addFileToArchive( aspectModelFile, zos, rootPath );
         }
      } catch ( IOException e ) {
         try {
            throw new IOException( "Error creating zip archive!", e );
         } catch ( IOException ex ) {
            throw new RuntimeException( ex );
         }
      }
   }

   private static void addFileToArchive( final AspectModelFile file, final ZipOutputStream zos, final String rootPath )
         throws IOException {
      final String aspectString = AspectSerializer.INSTANCE.apply( file.aspect() );
      final String fileName = String.format( "%s/%s/%s/%s.ttl",
            !rootPath.isBlank() ? String.format( "%s/%s", rootPath, BASE_ARCHIVE_FORMAT_PATH ) : BASE_ARCHIVE_FORMAT_PATH,
            file.aspect().urn().getNamespaceMainPart(),
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
