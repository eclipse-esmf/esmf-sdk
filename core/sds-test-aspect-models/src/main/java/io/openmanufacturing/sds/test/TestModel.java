/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.test;

import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;

@SuppressWarnings( "squid:S1214" ) // Can not be avoided because enums can't inherit from an abstract class
public interface TestModel {
   String TEST_NAMESPACE = "urn:bamm:io.openmanufacturing.test:1.0.0#";

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
