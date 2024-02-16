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

import java.io.StringWriter;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.Model;

public interface TestSharedModel {
   String TEST_NAMESPACE = "urn:samm:org.eclipse.esmf.test.shared:1.0.0#";

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
