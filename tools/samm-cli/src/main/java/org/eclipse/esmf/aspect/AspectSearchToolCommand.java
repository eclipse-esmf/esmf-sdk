package org.eclipse.esmf.aspect;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;

import picocli.CommandLine;

@CommandLine.Command( name = AspectSearchToolCommand.COMMAND_NAME,
      description = "Search separate Properties files used by Aspect Model",
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectSearchToolCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "search";

   @CommandLine.Option( names = { "--model-path", "-mp" }, description = "Path to Model file" )
   private String pathToModelFile = "-";

   @CommandLine.Option( names = { "--models-path", "-mp" }, description = "Path to Models, where have to search" )
   private String pathToModels = "-";

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @Override
   public void run() {
      //Properties url of github, root directory
      System.out.println("TEST new Search command!");

      final Path modelPath = Paths.get( pathToModelFile );

      final FileSystemStrategy fileSystemStrategy = new FileSystemStrategy( modelPath );


   }
}
