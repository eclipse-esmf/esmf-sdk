/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.jsonld;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.generator.AbstractGenerator;
import org.eclipse.esmf.metamodel.Aspect;

public class AspectModelToJsonLdGenerator extends AbstractGenerator {

   private static final String JSON_LD_FORMAT = "JSON-LD";
   private final Aspect aspect;

   public AspectModelToJsonLdGenerator( final Aspect aspect ) {
      this.aspect = aspect;
   }

   /**
    * Generates a JSON-LD representation of the aspect's source model and writes it to an output stream.
    *
    * <p>This method takes a function (nameMapper) that maps the name of the aspect to an output stream,
    * then writes the aspect's source model in JSON-LD format to that stream. The function is expected
    * to accept the aspect's name as a parameter and return an appropriate OutputStream where the JSON-LD
    * will be written. The OutputStream is closed automatically when the operation is complete.</p>
    *
    * @param nameMapper a function that maps the aspect name to an OutputStream for writing the JSON-LD.
    * @throws IOException if an I/O error occurs while writing to the output stream.
    */
   public void generateJsonLd( final Function<String, OutputStream> nameMapper ) throws IOException {
      try ( final OutputStream output = nameMapper.apply( aspect.getName() ) ) {
         aspect.getSourceFile().sourceModel().write( output, JSON_LD_FORMAT );
      }
   }

   public String generateJsonLd() {
      StringWriter stringWriter = new StringWriter();
      aspect.getSourceFile().sourceModel().write( stringWriter, JSON_LD_FORMAT );
      return stringWriter.toString();
   }
}
