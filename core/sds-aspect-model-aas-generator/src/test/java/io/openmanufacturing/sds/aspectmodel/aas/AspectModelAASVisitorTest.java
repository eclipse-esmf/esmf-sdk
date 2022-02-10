package io.openmanufacturing.sds.aspectmodel.aas;

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


class AspectModelAASVisitorTest {

    private final SdsAspectMetaModelResourceResolver aspectMetaModelResourceResolver = new SdsAspectMetaModelResourceResolver();

    @org.junit.jupiter.api.Test
    void visitAspect() {
        VersionNumber bammVersion = new VersionNumber(1,0,0);

        final Model model = TestResources.getModel( TestAspect.ASPECT_WITH_ENGLISH_DESCRIPTION, KnownVersion.BAMM_1_0_0)
                .get().getModel();

        VersionedModel versionedModel = aspectMetaModelResourceResolver.mergeMetaModelIntoRawModel(model,bammVersion).get();
        Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked(versionedModel);

        AspectModelAASVisitor visitor = new AspectModelAASVisitor();
        AssetAdministrationShellEnvironment environment = visitor.visitAspect(aspect, null);
    }
}
