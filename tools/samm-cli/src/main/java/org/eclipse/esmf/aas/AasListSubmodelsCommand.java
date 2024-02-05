package org.eclipse.esmf.aas;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspectmodel.aas.AasToAspectModelGenerator;
import org.eclipse.esmf.exception.CommandException;

import picocli.CommandLine;

@CommandLine.Command( name = AasListSubmodelsCommand.COMMAND_NAME, description = "Get list submodel templates of AAS input.",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AasListSubmodelsCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "list";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.ParentCommand
   private AasCommand parentCommand;

   @Override
   public void run() {
      final String path = parentCommand.getInput();
      final String extension = FilenameUtils.getExtension( path );
      if ( !extension.equals( "xml" ) && !extension.equals( "json" ) && !extension.equals( "aasx" ) ) {
         throw new CommandException( "Input file name must be an .xml, .aasx or .json file" );
      }

      List<String> submodelNames = AasToAspectModelGenerator.fromFile( new File( path ) ).getSubmodelNames();

      for ( String submodelName : submodelNames ) {
         int count = submodelNames.indexOf( submodelName );
         if ( count < 10 ) {
            System.out.printf( " %s: %s%n", count + 1, submodelName );
         } else {
            System.out.printf( "%s: %s%n", count + 1, submodelName );
         }
      }
   }
}
