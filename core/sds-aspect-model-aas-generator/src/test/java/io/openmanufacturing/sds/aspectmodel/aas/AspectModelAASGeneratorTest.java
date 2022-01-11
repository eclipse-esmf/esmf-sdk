package io.openmanufacturing.sds.aspectmodel.aas;

import io.adminshell.aas.v3.dataformat.SerializationException;
import io.adminshell.aas.v3.dataformat.aasx.InMemoryFile;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;
import org.apache.jena.rdf.model.Model;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class AspectModelAASGeneratorTest {

    private static final String XML_PATH = "aasx/xml/content.xml";

    private final SdsAspectMetaModelResourceResolver aspectMetaModelResourceResolver = new SdsAspectMetaModelResourceResolver();

    @Test
    void generateOutput() throws IOException, SerializationException {
        final VersionedModel model = TestResources.getModel( TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY, KnownVersion.BAMM_1_0_0 ).get();

        Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked(model);

        AspectModelAASGenerator generator = new AspectModelAASGenerator();

        ByteArrayOutputStream out = generator.generateOutput(aspect);
        validateAASX(out);

    }

    // TODO adjust taken from java-serializer AAS lib
    private void validateAASX(ByteArrayOutputStream byteStream) throws IOException {
        ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
        ZipEntry zipEntry = null;

        ArrayList<String> filePaths = new ArrayList<>();

        while ((zipEntry = in.getNextEntry()) != null) {
            if (zipEntry.getName().equals(XML_PATH)) {

                // Read the first 5 bytes of the XML file to make sure it is in fact XML file
                // No further test of XML file necessary as XML-Converter is tested separately
                byte[] buf = new byte[5];
                in.read(buf);
                assertEquals("<?xml", new String(buf));

            }

            // Write the paths of all files contained in the .aasx into filePaths
            filePaths.add(zipEntry.getName());
        }
    }
}
