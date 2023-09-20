package org.eclipse.esmf.aas;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aas.to.AasToAspectCommand;
import org.eclipse.esmf.exception.SubCommandException;

import picocli.CommandLine;

@CommandLine.Command( name = AasToCommand.COMMAND_NAME, description = "Transforms an Aspect Model into another format",
      subcommands = {
            CommandLine.HelpCommand.class,
            AasToAspectCommand.class
      },
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AasToCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "to";

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.ParentCommand
   public AasCommand parentCommand;

   @Override
   public void run() {
      throw new SubCommandException( COMMAND_NAME );
   }
}
