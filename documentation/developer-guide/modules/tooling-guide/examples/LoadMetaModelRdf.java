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

import static org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

import org.apache.jena.vocabulary.RDF;
import org.eclipse.esmf.aspectmodel.resolver.services.MetaModelUrls;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;
import org.junit.jupiter.api.Test;

public class LoadMetaModelRdf {
   @Test
   public void loadMetaModelRdf() {
      // tag::loadMetaModelRdf[]
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final Optional<URL> characteristicDefinitionsUrl =
            MetaModelUrls.url( "characteristic", metaModelVersion, "characteristic-instances.ttl" );

      characteristicDefinitionsUrl.ifPresent( url -> {
         try ( final InputStream inputStream = url.openStream() ) {
            loadTurtle( inputStream ).forEach( model -> {
               // Do something with the org.apache.jena.org.rdf.model.Model
               final SAMM samm = new SAMM( metaModelVersion );
               final int numberOfCharacteristicInstances =
                     model.listStatements( null, RDF.type, samm.Characteristic() ).toList().size();
               final String result = String.format( "Meta Model Version " + metaModelVersion.toVersionString()
                     + " defines " + numberOfCharacteristicInstances + " Characteristic instances" );
            } );
         } catch ( IOException e ) {
            throw new RuntimeException( e );
         }
      } );
      // end::loadMetaModelRdf[]
   }
}
