package io.openmanufacturing.sds.aspectmodel.aas;

import io.adminshell.aas.v3.dataformat.DeserializationException;
import io.adminshell.aas.v3.dataformat.xml.XmlDeserializer;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.SubmodelElementCollection;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectModelAASGeneratorTest {

    AspectModelAASGenerator generator = new AspectModelAASGenerator();

    @Test
    void test_generate_aasx_from_bamm_aspect_with_list_and_additional_property() throws IOException, DeserializationException {
        Aspect aspect = loadAspect(TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY);

        ByteArrayOutputStream out = generator.generateOutput(aspect);

        AssetAdministrationShellEnvironment env = loadAASX(out);

        assertTrue(env.getConceptDescriptions().size() == 2);
        assertTrue(env.getSubmodels().size() == 1);
        assertTrue(env.getSubmodels().get(0).getSubmodelElements().size() == 2);
    }

    @Test
    void test_generate_aasx_from_bamm_aspect_with_entity() throws IOException, DeserializationException {
        Aspect aspect = loadAspect(TestAspect.ASPECT_WITH_ENTITY);

        ByteArrayOutputStream out = generator.generateOutput(aspect);

        AssetAdministrationShellEnvironment env = loadAASX(out);

        assertEquals(1, env.getSubmodels().size(), "Not exactly one Submodel in AAS.");
        assertEquals(1, env.getSubmodels().get(0).getSubmodelElements().size(), "Not exactly one SubmodelElement in Submodel.");
        assertTrue(env.getSubmodels().get(0).getSubmodelElements().get(0) instanceof SubmodelElementCollection, "SubmodelElement is not a SubmodelElementCollection.");

        SubmodelElementCollection collection = (SubmodelElementCollection) env.getSubmodels().get(0).getSubmodelElements().get(0);
        assertEquals(1, collection.getValues().size(), "Not exactly one Element in SubmodelElementCollection");
        assertEquals("entityProperty", collection.getValues().stream().findFirst().get().getIdShort());
    }

    @Test
    void test_generate_aasx_from_bamm_aspect_with_collection() throws IOException, DeserializationException {
        Aspect aspect = loadAspect(TestAspect.ASPECT_WITH_COLLECTION);

        ByteArrayOutputStream out = generator.generateOutput(aspect);

        AssetAdministrationShellEnvironment env = loadAASX(out);

        assertEquals(1, env.getSubmodels().size(), "Not exactly one Submodel in AAS.");
    }



    @ParameterizedTest
    @EnumSource( value = TestAspect.class)
    public void testGeneration( final TestAspect testAspect ) throws IOException, DeserializationException {
        final Aspect aspect = loadAspect( testAspect );

        ByteArrayOutputStream out = generator.generateOutput(aspect);
        AssetAdministrationShellEnvironment env = loadAASX(out);

        assertTrue(env.getSubmodels().size() >= 1, "No Submodel in AAS present.");
    }

    private AssetAdministrationShellEnvironment loadAASX(ByteArrayOutputStream byteStream) throws DeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer();
        AssetAdministrationShellEnvironment env = deserializer.read(new ByteArrayInputStream(byteStream.toByteArray()));
        return env;
    }

    private Aspect loadAspect(TestAspect testAspect) {
        final VersionedModel model = TestResources.getModel( testAspect, KnownVersion.BAMM_1_0_0 ).get();
        Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked(model);
        return aspect;
    }
}
