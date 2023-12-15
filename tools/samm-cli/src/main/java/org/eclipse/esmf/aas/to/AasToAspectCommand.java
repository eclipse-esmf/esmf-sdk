package org.eclipse.esmf.aas.to;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aas.AasToCommand;
import org.eclipse.esmf.aspectmodel.aas.AASToAspectModelGenerator;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;

import fs.ModelsRoot;
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

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @Override
   public void run() {
      final String path = parentCommand.parentCommand.getInput();
      final String invalidFileTypeMessage = "Input file name must be an .xml, .aasx or .json file";
      if ( !path.contains( "." ) ) {
         throw new CommandException( invalidFileTypeMessage );
      }

      try ( final FileInputStream input = new FileInputStream( path ) ) {
         final AASToAspectModelGenerator generator = switch ( path.substring( path.indexOf( "." ) ) ) {
            case ".xml" -> AASToAspectModelGenerator.fromAasXml( input );
            case ".aasx" -> AASToAspectModelGenerator.fromAasx( input );
            default -> throw new CommandException( invalidFileTypeMessage );
         };
         generateAspects( generator );
      } catch ( final IOException exception ) {
         throw new CommandException( exception );
      }
   }

   private void generateAspects( final AASToAspectModelGenerator generator ) throws IOException {
      final ModelsRoot modelsRoot = new ModelsRoot( Path.of( outputPath ) );
      for ( final Aspect aspect : generator.generateAspects() ) {
         final String aspectString = AspectSerializer.INSTANCE.apply( aspect );
         final File targetFile = modelsRoot.determineOutputFile( aspect.getAspectModelUrn().get() );
         LOG.info( "Writing {}", targetFile.getAbsolutePath() );
         System.out.println( aspectString );
//         final BufferedWriter writer = new BufferedWriter( new FileWriter( targetFile ) );
//         writer.write( aspectString );
//         writer.close();
      }
   }
}
