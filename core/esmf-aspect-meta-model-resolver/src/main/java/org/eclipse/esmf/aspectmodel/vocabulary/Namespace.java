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

package org.eclipse.esmf.aspectmodel.vocabulary;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.samm.KnownVersion;

/**
 * Abstracts an RDF namespace
 */
public interface Namespace {
   String getUri();

   default String getNamespace() {
      final String uri = getUri();
      return uri.endsWith( "#" ) ? uri : uri + "#";
   }

   default Property property( final String name ) {
      return ResourceFactory.createProperty( getNamespace() + name );
   }

   default Resource resource( final String name ) {
      return ResourceFactory.createResource( getNamespace() + name );
   }

   static Map<String, String> createPrefixMap( final KnownVersion metaModelVersion ) {
      final SAMM SAMM = new SAMM( metaModelVersion );
      final SAMMC SAMMC = new SAMMC( metaModelVersion );
      final SAMME SAMME = new SAMME( metaModelVersion, SAMM );
      final UNIT unit = new UNIT( metaModelVersion, SAMM );

      final Map<String, String> result = new LinkedHashMap<>();
      result.put( "bamm", SAMM.getUri() + "#" );
      result.put( "bamm-c", SAMMC.getUri() + "#" );
      result.put( "bamm-e", SAMME.getUri() + "#" );
      result.put( "unit", unit.getUri() + "#" );
      result.put( "rdf", RDF.getURI() );
      result.put( "rdfs", RDFS.getURI() );
      result.put( "xsd", XSD.getURI() );

      return result;
   }
}
