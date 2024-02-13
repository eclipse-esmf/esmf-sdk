package org.eclipse.esmf.aas;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.exception.SubCommandException;

import picocli.CommandLine;

@CommandLine.Command( name = AasCommand.COMMAND_NAME,
      description = "Validate and transform AAS Models",
      subcommands = {
            CommandLine.HelpCommand.class,
            AasToCommand.class,
            AasListSubmodelsCommand.class
      },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AasCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "aas";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Parameters( paramLabel = "INPUT", description = "Input file name of the AAS Model .aasx, .json .xml file", arity = "1",
         index = "0" )
   private String input;

   public String getInput() {
      return input;
   }

   @Override
   public void run() {
      throw new SubCommandException( COMMAND_NAME );
   }
}
