package org.eclipse.esmf.aas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aas.to.AasToAspectCommand;
import org.eclipse.esmf.aspectmodel.aas.AasToAspectModelGenerator;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;

@CommandLine.Command( name = AasListSubmodelsCommand.COMMAND_NAME, description = "Get list submodel templates of AAS input.",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AasListSubmodelsCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "list";

   private static final Logger LOG = LoggerFactory.getLogger( AasToAspectCommand.class );

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.ParentCommand
   private AasCommand parentCommand;

   @CommandLine.Option( names = { "--output-directory", "-d" }, description = "Output directory to write files to" )
   private String outputPath = ".";

   @Override
   public void run() {
      final String path = parentCommand.getInput();
      final String extension = FilenameUtils.getExtension( path );
      if ( !extension.equals( "xml" ) && !extension.equals( "json" ) && !extension.equals( "aasx" ) ) {
         throw new CommandException( "Input file name must be an .xml, .aasx or .json file" );
      }

      List<String> submodelNames = AasToAspectModelGenerator.fromFile( new File( path ) ).getSubmodelNames();

      System.out.println( "Please, choose one option or a few options using comma separator (Example: 1, 2, 3)"
            + " for continue generating submodels from input AAS. Press 'Enter' for exit:" );
      Scanner scanner = new Scanner( System.in );
      showSubmodelNames( submodelNames );

      String input = scanner.nextLine();

      if ( input.isEmpty() ) {
         System.exit( 0 );
      }

      List<Integer> choices = Arrays.stream( input.split( "[,\\s]+" ) ).toList()
            .stream()
            .map( Integer::parseInt )
            .toList();

      if ( choices.size() > submodelNames.size() ) {
         throw new CommandException(
               String.format( "You wrote invalid numbers of submodels: %s. Maximum valid size is %s.%n", choices.size(), submodelNames.size() )
         );
      }

      generateAspects( AasToAspectModelGenerator.fromFile( new File( path ) ), submodelNames );
   }

   private void showSubmodelNames( List<String> submodelNames ) {
      for ( String submodelName : submodelNames ) {
         int count = submodelNames.indexOf( submodelName );
         if ( count < 10 ) {
            System.out.printf( " [%s]: %s;%n", count + 1, submodelName );
         } else {
            System.out.printf( "[%s]: %s;%n", count + 1, submodelName );
         }
      }
   }

   private void generateAspects( final AasToAspectModelGenerator generator, List<String> filteredSubmodels ) {
      final StructuredModelsRoot modelsRoot = new StructuredModelsRoot( Path.of( outputPath ) );
      for ( final Aspect aspect : generator.generateAspects() ) {
         if (filteredSubmodels.contains( aspect.getName() )) {
            final String aspectString = AspectSerializer.INSTANCE.apply( aspect );
            final File targetFile = modelsRoot.determineAspectModelFile( aspect.getAspectModelUrn().get() );
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
}
