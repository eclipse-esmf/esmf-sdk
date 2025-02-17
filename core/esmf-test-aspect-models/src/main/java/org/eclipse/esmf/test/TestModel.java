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

package org.eclipse.esmf.test;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFWriter;

@SuppressWarnings( "squid:S1214" ) // Can not be avoided because enums can't inherit from an abstract class
public interface TestModel {
   String TEST_NAMESPACE = "urn:samm:org.eclipse.esmf.test:1.0.0#";

   String getName();

   default AspectModelUrn getUrn() {
      return AspectModelUrn.fromUrn( TEST_NAMESPACE + getName() );
   }

   static String modelToString( final Model model ) {
      //      org.apache.jena.riot.RDFWriter x;
      final String string = RDFWriter.create().format( RDFFormat.TURTLE ).lang( RDFLanguages.TURTLE ).source( model ).asString();

      //      final StringWriter stringWriter = new StringWriter();
      //      model.write( stringWriter, "TURTLE" );
      //      return stringWriter.toString();
      return string;
   }
}
