package org.eclipse.esmf.aas.to;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.aas.AasToCommand;
import org.eclipse.esmf.aspectmodel.aas.Context;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectContext;

import com.fasterxml.jackson.databind.JsonNode;

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

   @CommandLine.ParentCommand
   private AasToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @Override
   public void run() {
      String path = parentCommand.parentCommand.getInput();

         try {
            if (path.contains( ".xml" )) {
               Environment environment = new XmlDeserializer().read(new FileInputStream(path));
               Submodel submodel = environment.getSubmodels().get( 0 );
               VersionedModel versionedModel;
               String aspectName = submodel.getIdShort();
               String aspectModernUrlString = submodel.getId().split( "/submodel" )[0];
               Reference aspectSemanticId = submodel.getSemanticID();
               ModellingKind modelingKind = submodel.getKind();

               Context context = new Context( environment, submodel );

               Aspect aspect;

//               environment.getAssetAdministrationShells().get( 0 ).getEmbeddedDataSpecifications();

               System.out.println(environment);
            }

            if (path.contains( ".aasx" )) {
               AASXDeserializer deserializer = new AASXDeserializer(new FileInputStream(path));
               Environment environment = new XmlDeserializer().read(deserializer.getXMLResourceString());
            }



         } catch ( InvalidFormatException | DeserializationException | IOException e ) {
            throw new RuntimeException( e );
         }
      System.out.println("WAS here after run this command:: " + path);
   }
}
