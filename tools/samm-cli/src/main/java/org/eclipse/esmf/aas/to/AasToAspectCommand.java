package org.eclipse.esmf.aas.to;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aas.AasToCommand;
import org.eclipse.esmf.aspectmodel.aas.AasToAspectModelGenerator;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(
      name = AasToAspectCommand.COMMAND_NAME,
      description = "Generate an Aspect Model from an Asset Administration Shell (AAS)",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true )
public class AasToAspectCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "aspect";
   private static final Logger LOG = LoggerFactory.getLogger( AasToAspectCommand.class );

   @CommandLine.ParentCommand
   private AasToCommand parentCommand;

   @CommandLine.Option( names = { "--output-directory", "-d" }, description = "Output directory to write files to" )
   private String outputPath = ".";

   @CommandLine.Option( names = { "--submodel-template",
         "-s" }, description = "Select the submodel template(s) to include, as returned by the aas list command" )
   private List<Integer> selectedOptions = new ArrayList<>();

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @Override
   public void run() {
      final String path = parentCommand.parentCommand.getInput();
      final String extension = FilenameUtils.getExtension( path );
      if ( !extension.equals( "xml" ) && !extension.equals( "json" ) && !extension.equals( "aasx" ) ) {
         throw new CommandException( "Input file name must be an .xml, .aasx or .json file" );
      }
      generateAspects( AasToAspectModelGenerator.fromFile( new File( path ) ) );
   }

   private void generateAspects( final AasToAspectModelGenerator generator ) {
      final StructuredModelsRoot modelsRoot = new StructuredModelsRoot( Path.of( outputPath ) );
      final List<Aspect> generatedAspects = generator.generateAspects();

      final List<Aspect> filteredAspects = this.selectedOptions.isEmpty() ? generatedAspects :
            IntStream.range( 0, generatedAspects.size() )
                  .filter( index -> selectedOptions.contains( index + 1 ) )
                  .mapToObj( generatedAspects::get )
                  .toList();

      for ( final Aspect aspect : filteredAspects ) {
         final String aspectString = AspectSerializer.INSTANCE.aspectToString( aspect );
         final File targetFile = modelsRoot.determineAspectModelFile( aspect.urn() );
         LOG.info( "Writing {}", targetFile.getAbsolutePath() );
         final File directory = targetFile.getParentFile();
         if ( !directory.exists() && !directory.mkdirs() ) {
            throw new CommandException( "Could not create directory: " + directory.getAbsolutePath() );
         }
         try ( final Writer writer = new BufferedWriter( new FileWriter( targetFile ) ) ) {
            writer.write( aspectString );
         } catch ( final IOException exception ) {
            throw new CommandException( "Could not write file: " + targetFile.getAbsolutePath(), exception );
         }
      }
   }
}
