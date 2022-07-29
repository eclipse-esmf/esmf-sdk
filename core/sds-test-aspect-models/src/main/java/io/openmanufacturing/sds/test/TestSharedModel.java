package io.openmanufacturing.sds.test;

import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;

public interface TestSharedModel {
   String TEST_NAMESPACE = "urn:bamm:io.openmanufacturing.test.shared:1.0.0#";
   String RESOURCE_PATH = "io.openmanufacturing.test.shared/1.0.0";
   String getName();

   default AspectModelUrn getUrn() {
      return AspectModelUrn.fromUrn( TEST_NAMESPACE + getName() );
   }

   static String modelToString( final Model model ) {
      final StringWriter stringWriter = new StringWriter();
      model.write( stringWriter, "TURTLE" );
      return stringWriter.toString();
   }
}
