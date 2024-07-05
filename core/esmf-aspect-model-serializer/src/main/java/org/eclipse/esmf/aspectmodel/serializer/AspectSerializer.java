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

package org.eclipse.esmf.aspectmodel.serializer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;

import org.apache.jena.rdf.model.Model;

/**
 * Convenience function to serialize an Aspect into a String
 */
public class AspectSerializer implements Function<Aspect, String> {
   public static final AspectSerializer INSTANCE = new AspectSerializer();

   @Override
   public String apply( final Aspect aspect ) {
      final RdfNamespace aspectNamespace = new RdfNamespace() {
         @Override
         public String getShortForm() {
            return "";
         }

         @Override
         public String getUri() {
            return aspect.urn().getUrnPrefix();
         }
      };
      final RdfModelCreatorVisitor rdfModelCreatorVisitor = new RdfModelCreatorVisitor( aspectNamespace );
      final StringWriter stringWriter = new StringWriter();
      try ( final PrintWriter printWriter = new PrintWriter( stringWriter ) ) {
         final Model model = aspect.accept( rdfModelCreatorVisitor, null ).model();
         final PrettyPrinter prettyPrinter = new PrettyPrinter( model, aspect.urn(), printWriter );
         prettyPrinter.print();
      }
      return stringWriter.toString();
   }
}
