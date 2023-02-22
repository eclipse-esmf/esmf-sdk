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

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

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
      final BAMM bamm = new BAMM( metaModelVersion );
      final BAMMC bammc = new BAMMC( metaModelVersion );
      final BAMME bamme = new BAMME( metaModelVersion, bamm );
      final UNIT unit = new UNIT( metaModelVersion, bamm );

      final Map<String, String> result = new LinkedHashMap<>();
      result.put( "bamm", bamm.getUri() + "#" );
      result.put( "bamm-c", bammc.getUri() + "#" );
      result.put( "bamm-e", bamme.getUri() + "#" );
      result.put( "unit", unit.getUri() + "#" );
      result.put( "rdf", RDF.getURI() );
      result.put( "rdfs", RDFS.getURI() );
      result.put( "xsd", XSD.getURI() );

      return result;
   }
}
