package org.eclipse.esmf.aspectmodel.aspect;

import org.apache.jena.rdf.model.Model;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.serializer.PrettyPrinter;
import org.eclipse.esmf.aspectmodel.serializer.RdfModelCreatorVisitor;
import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;
import org.eclipse.esmf.metamodel.Aspect;

import java.io.*;
import java.util.function.Function;

public class AASModelAspectGenerator {

    /**
     * Generates an AASX archive file for a given Aspect and writes it to a given OutputStream provided by <code>nameMapper<code/>
     *
     * @param environment the Aspect for which an AASX archive shall be generated
     * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
     * @throws IOException in case the generation can not properly be executed
     */
    public void generateTtlFile(final Environment environment, final Function<String, OutputStream> nameMapper )
            throws IOException {
        try ( final OutputStream output = nameMapper.apply( "env_name" ) ) {
            output.write( generateAspectOutput( environment ).toByteArray() );
        }
    }

    protected ByteArrayOutputStream generateAspectOutput(final Environment environment ) throws IOException {
        final AASModelAspectTranslator visitor = new AASModelAspectTranslator();
        final Aspect aspect = visitor.visitAas( environment ).get(0);

        final Namespace aspectNamespace = () -> aspect.getAspectModelUrn().get().getUrnPrefix();
        final RdfModelCreatorVisitor rdfCreator = new RdfModelCreatorVisitor(
                aspect.getMetaModelVersion(), aspectNamespace );
        final Model model = rdfCreator.visitAspect( aspect, null ).getModel();

        // At this point, you can manipulate the RDF model, if required

        // Second step: Serialize RDF model into nicely formatted Turtle
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter( System.out );
        final VersionedModel versionedModel = new SammAspectMetaModelResourceResolver()
                .mergeMetaModelIntoRawModel( model, aspect.getMetaModelVersion() ).get();
        final PrettyPrinter prettyPrinter = new PrettyPrinter(
                versionedModel, aspect.getAspectModelUrn().get(), printWriter );
        prettyPrinter.print();
        printWriter.close();

        try ( final ByteArrayOutputStream out = new ByteArrayOutputStream() ) {
            out.write(model.toString().getBytes());
            return out;
        }
    }
}
