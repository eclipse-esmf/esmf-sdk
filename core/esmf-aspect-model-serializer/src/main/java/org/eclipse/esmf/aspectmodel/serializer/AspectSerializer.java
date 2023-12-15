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

import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Model;

/**
 * Convenience function to serialize an Aspect into a String
 */
public class AspectSerializer implements Function<Aspect, String> {
   public static final AspectSerializer INSTANCE = new AspectSerializer();

   @Override
   public String apply( final Aspect aspect ) {
      final Namespace aspectNamespace = () -> aspect.getAspectModelUrn().get().getUrnPrefix();
      final RdfModelCreatorVisitor rdfModelCreatorVisitor = new RdfModelCreatorVisitor( KnownVersion.getLatest(), aspectNamespace );
      final StringWriter stringWriter = new StringWriter();
      try ( final PrintWriter printWriter = new PrintWriter( stringWriter ) ) {
         final Model model = aspect.accept( rdfModelCreatorVisitor, null ).getModel();
         final VersionedModel versionedModel = new SammAspectMetaModelResourceResolver()
               .mergeMetaModelIntoRawModel( model, aspect.getMetaModelVersion() ).get();
         final PrettyPrinter prettyPrinter = new PrettyPrinter(
               versionedModel, aspect.getAspectModelUrn().get(), printWriter );
         prettyPrinter.print();
      }
      return stringWriter.toString();
   }
}
