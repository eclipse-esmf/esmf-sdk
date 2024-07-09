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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;

import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;
// end::imports[]
import org.junit.jupiter.api.Test;

public class LoadMetaModelRdf {
   @Test
   public void loadMetaModelRdf() {
      // tag::loadMetaModelRdf[]
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final Model characteristicDefinitions = MetaModelFile.CHARACTERISTIC_DEFINITIONS.sourceModel();

      // Do something with the org.apache.jena.org.rdf.model.Model
      final int numberOfCharacteristicInstances =
            characteristicDefinitions.listStatements( null, RDF.type, SammNs.SAMM.Characteristic() ).toList().size();
      final String result = String.format( "Meta Model Version " + metaModelVersion.toVersionString()
            + " defines " + numberOfCharacteristicInstances + " Characteristic instances" );
      // end::loadMetaModelRdf[]
   }
}
