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

package io.openmanufacturing.sds.aspectmodel.serializer;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFLanguages;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;

public class PrettyPrinterTest extends MetaModelVersions {

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   public void testPrettyPrinter( final TestAspect testAspect ) {
      final KnownVersion metaModelVersion = KnownVersion.getLatest();
      final Model originalModel = TestResources
            .getModelWithoutResolution( testAspect, metaModelVersion ).getRawModel();

      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      final PrintWriter writer = new PrintWriter( buffer );
      new PrettyPrinter( new VersionedModel( ModelFactory.createDefaultModel(),
            VersionNumber.parse( metaModelVersion.toVersionString() ), originalModel ),
            testAspect.getUrn(), writer )
            .print();
      writer.flush();

      final InputStream bufferInput = new ByteArrayInputStream( buffer.toString().getBytes() );
      final Model prettyPrintedModel = ModelFactory.createDefaultModel();
      try {
         prettyPrintedModel.read( bufferInput, "", RDFLanguages.TURTLE.getName() );
      } catch ( final Exception e ) {
         System.out.println( e.getMessage() );
         System.out.println();
         System.out.println( buffer );
         fail( "Syntax error" );
      }

      assertThat( hash( originalModel ).equals( hash( prettyPrintedModel ) ) ).isTrue();
   }

   private String hash( final Model model ) {
      return model.listStatements().toList().stream().map( this::hash ).sorted().collect( Collectors.joining() );
   }

   private String hash( final Statement statement ) {
      return hash( statement.getSubject() ) + hash( statement.getPredicate() ) + hash( statement.getObject() );
   }

   private String hash( final RDFNode object ) {
      if ( object.isResource() ) {
         return hash( object.asResource() );
      }
      return object.asLiteral().getValue().toString();
   }

   private String hash( final Resource resource ) {
      if ( resource.isURIResource() ) {
         return resource.getURI();
      }

      return hashAnonymousResource( resource );
   }

   private String hashAnonymousResource( final Resource resource ) {
      return resource.listProperties().toList().stream()
            .map( statement -> hash( statement.getPredicate() ) + hash( statement.getObject() ) )
            .sorted()
            .collect( Collectors.joining() );
   }
}
