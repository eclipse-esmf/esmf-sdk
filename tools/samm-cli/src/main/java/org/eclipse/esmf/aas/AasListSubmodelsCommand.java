package org.eclipse.esmf.aas;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aspectmodel.aas.AasFileFormat;
import org.eclipse.esmf.aspectmodel.aas.AasToAspectModelGenerator;
import org.eclipse.esmf.exception.CommandException;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import picocli.CommandLine;

@CommandLine.Command(
      name = AasListSubmodelsCommand.COMMAND_NAME,
      description = "Get list of submodel templates of AAS input",
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

      if ( Arrays.stream( AasFileFormat.values() ).noneMatch( format -> format.toString().equals( extension ) ) ) {
         throw new CommandException( "Input file name must be one of " + AasFileFormat.allValues() );
      }

      final List<String> submodelNames = AasToAspectModelGenerator.fromFile( new File( path ) )
            .getFocus()
            .getSubmodels()
            .stream()
            .filter( submodel -> submodel.getKind().equals( ModellingKind.TEMPLATE ) )
            .map( Referable::getIdShort )
            .toList();

      for ( final String submodelName : submodelNames ) {
         final int index = submodelNames.indexOf( submodelName );
         System.out.printf( "%2s: %s%n", index + 1, submodelName );
      }
   }
}
