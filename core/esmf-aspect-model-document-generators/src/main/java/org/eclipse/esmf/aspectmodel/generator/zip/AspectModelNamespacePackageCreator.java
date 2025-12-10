package org.eclipse.esmf.aspectmodel.generator.zip;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.generator.GenerationException;
import org.eclipse.esmf.aspectmodel.generator.Generator;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.metamodel.AspectModel;

/**
 * Generator for Namespace Packages as described in <a
 * href="https://github.com/eclipse-esmf/esmf-semantic-aspect-meta-model/blob/main/documentation/decisions/0009-namespace-packages.md">
 * ADR-0009</a>.
 */
public class AspectModelNamespacePackageCreator
      extends Generator<AspectModel, String, byte[], NamespacePackageGenerationConfig, NamespacePackageArtifact> {
   public static final NamespacePackageGenerationConfig DEFAULT_CONFIG = NamespacePackageGenerationConfigBuilder.builder().build();
   private static final String BASE_ARCHIVE_FORMAT_PATH = "aspect-models/";

   public AspectModelNamespacePackageCreator( final AspectModel aspectModel ) {
      this( aspectModel, DEFAULT_CONFIG );
   }

   public AspectModelNamespacePackageCreator( final AspectModel aspectModel, final NamespacePackageGenerationConfig config ) {
      super( aspectModel, config );
   }

   private AspectModel aspectModel() {
      return getFocus();
   }

   /**
    * Figures out a fitting name for the generated namespace package
    *
    * @return a name for the generated namespace package
    */
   private String packageName() {
      if ( aspectModel().aspects().size() == 1 ) {
         return aspectModel().aspect().getName() + ".zip";
      }
      return "package" + aspectModel().hashCode() + ".zip"; // 🤷
   }

   @Override
   public Stream<NamespacePackageArtifact> generate() {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try ( final BufferedOutputStream bos = new BufferedOutputStream( out );
            final ZipOutputStream zos = new ZipOutputStream( bos ) ) {
         for ( final AspectModelFile aspectModelFile : aspectModel().files() ) {
            addFileToArchive( aspectModelFile, zos, config.rootPath() );
         }
         out.close();
      } catch ( final IOException exception ) {
         throw new GenerationException( "Could not creat namespace package for Aspect Model", exception );
      }

      return Stream.of( new NamespacePackageArtifact( packageName(), out.toByteArray() ) );
   }

   private static void addFileToArchive( final AspectModelFile file, final ZipOutputStream zos, final String rootPath )
         throws IOException {
      final String aspectString = AspectSerializer.INSTANCE.aspectToString( file.aspect() );
      final String fileName = String.format( "%s/%s/%s/%s.ttl",
            !rootPath.isBlank() ? String.format( "%s/%s", rootPath, BASE_ARCHIVE_FORMAT_PATH ) : BASE_ARCHIVE_FORMAT_PATH,
            file.aspect().urn().getNamespaceMainPart(),
            file.aspect().urn().getVersion(),
            file.aspect().getName() );
      final ZipEntry zipEntry = new ZipEntry( fileName );
      zos.putNextEntry( zipEntry );

      final Writer writer = new OutputStreamWriter( zos );
      writer.write( aspectString );
      writer.flush();

      zos.closeEntry();
   }
}
