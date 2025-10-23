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

package org.eclipse.esmf.metamodel.vocabulary;

import java.util.stream.Stream;

import org.eclipse.esmf.samm.KnownVersion;

/**
 * RDF vocabularies of the SAMM meta model namespaces
 */
@SuppressWarnings( { "checkstyle:AbbreviationAsWordInName", "checkstyle:MethodName" } ) // RDF vocabularies are exempt from naming rules
public class SammNs {
   public static final SAMM SAMM = new SAMM( KnownVersion.getLatest() );
   public static final SAMMC SAMMC = new SAMMC( KnownVersion.getLatest() );
   public static final SAMME SAMME = new SAMME( KnownVersion.getLatest(), SAMM );
   public static final UNIT UNIT = new UNIT( KnownVersion.getLatest(), SAMM );

   private static RdfNamespace RDF;
   private static RdfNamespace RDFS;
   private static RdfNamespace XSD;

   private SammNs() {
   }

   /**
    * All SAMM-specific metamodel RDF namespaces
    *
    * @return the namespaces
    */
   public static Stream<RdfNamespace> sammNamespaces() {
      return Stream.of( SAMM, SAMMC, SAMME, UNIT );
   }

   public static synchronized RdfNamespace RDF() {
      if ( RDF == null ) {
         RDF = new SimpleRdfNamespace( "rdf", org.apache.jena.vocabulary.RDF.getURI() );
      }
      return RDF;
   }

   public static synchronized RdfNamespace RDFS() {
      if ( RDFS == null ) {
         RDFS = new SimpleRdfNamespace( "rdfs", org.apache.jena.vocabulary.RDFS.getURI() );
      }
      return RDFS;
   }

   public static synchronized RdfNamespace XSD() {
      if ( XSD == null ) {
         XSD = new SimpleRdfNamespace( "xsd", org.apache.jena.vocabulary.XSD.getURI() );
      }
      return XSD;
   }

   /**
    * All "well-known" RDF namespaces
    *
    * @return the namespaces
    */
   public static Stream<RdfNamespace> wellKnownNamespaces() {
      return Stream.concat( sammNamespaces(), Stream.of( RDF(), RDFS(), XSD() ) );
   }
}
