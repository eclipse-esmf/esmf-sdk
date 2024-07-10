package org.eclipse.esmf.aspect;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.ExternalResolverMixin;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.metamodel.AspectModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command( name = AspectCopyrightAndLicenceCommand.COMMAND_NAME,
      description = "Get copyright and license of a Model",
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true
)
public class AspectCopyrightAndLicenceCommand extends AbstractCommand {

   public static final String COMMAND_NAME = "licence";

   private static final Logger LOG = LoggerFactory.getLogger( AspectCopyrightAndLicenceCommand.class );

   @CommandLine.ParentCommand
   private AspectCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ExternalResolverMixin customResolver;

   @Override
   public void run() {
      final AspectModel aspectModel = loadAspectModelOrFail( parentCommand.getInput(), customResolver );
      for ( final String line : aspectModel.files().get( 0 ).headerComment() ) {
         System.out.println( line );
      }
   }
}
