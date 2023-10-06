package org.eclipse.esmf.aas.to;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aas.AasToCommand;
import org.eclipse.esmf.aspectmodel.aspect.AASModelAspectGenerator;

import picocli.CommandLine;

@CommandLine.Command(
      name = AasToAspectCommand.COMMAND_NAME,
      description = "Generate Asset Administration Shell (AAS) submodel template for an Aspect Model",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      mixinStandardHelpOptions = true )
public class AasToAspectCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "aspect";
   public static final String TTL = "ttl";

   @CommandLine.ParentCommand
   private AasToCommand parentCommand;

   @CommandLine.Option(
           names = { "--output", "-o" },
           description = "Output file path" )
   private String outputFilePath = "-";

   @CommandLine.Option(
           names = { "--format", "-f" },
           description = "The file format the AAS is to be generated. Valid options are \"" + TTL + "\". Default is \"" + TTL + "\"." )
   private String format = TTL;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @Override
   public void run() {
      String path = parentCommand.parentCommand.getInput();

         Environment environment = null;

         try {
            if (path.contains( ".xml" )) {
               environment = new XmlDeserializer().read(new FileInputStream(path));
            }

            if (path.contains( ".aasx" )) {
               AASXDeserializer deserializer = new AASXDeserializer(new FileInputStream(path));
               environment = new XmlDeserializer().read(deserializer.getXMLResourceString());
            }

            AASModelAspectGenerator aasModelAspectGenerator = new AASModelAspectGenerator();
            aasModelAspectGenerator.generateTtlFile(environment, name -> getStreamForFile(outputFilePath));

         } catch ( InvalidFormatException | DeserializationException | IOException e ) {
            throw new RuntimeException( e );
         }
   }
}
