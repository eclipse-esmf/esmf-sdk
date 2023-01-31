/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package examples;

// tag::imports[]
import static io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader.loadTurtle;
import static io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader.openUrl;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import org.apache.jena.vocabulary.RDF;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.MetaModelUrls;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
// end::imports[]
import org.junit.jupiter.api.Test;

public class LoadMetaModelRdf {
   @Test
   public void loadMetaModelRdf() {
      // tag::loadMetaModelRdf[]
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final Optional<URL> characteristicDefinitionsUrl =
            MetaModelUrls.url( "characteristic", metaModelVersion, "characteristic-instances.ttl" );

      characteristicDefinitionsUrl.ifPresent( url -> {
         final InputStream inputStream = openUrl( url );
         loadTurtle( inputStream ).forEach( model -> {
            // Do something with the org.apache.jena.org.rdf.model.Model
            final BAMM bamm = new BAMM( metaModelVersion );
            final int numberOfCharacteristicInstances =
                  model.listStatements( null, RDF.type, bamm.Characteristic() ).toList().size();
            final String result = String.format( "Meta Model Version " + metaModelVersion.toVersionString()
                  + " defines " + numberOfCharacteristicInstances + " Characteristic instances" );
         } );
      } );
      // end::loadMetaModelRdf[]
   }
}
