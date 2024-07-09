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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

/**
 * Abstracts an RDF namespace
 */
public interface RdfNamespace {
   String getShortForm();

   String getUri();

   default String getNamespace() {
      final String uri = getUri();
      return uri.endsWith( "#" ) ? uri : uri + "#";
   }

   default String urn( final String element ) {
      return getNamespace() + element;
   }

   default Property property( final String name ) {
      return ResourceFactory.createProperty( urn( name ) );
   }

   default Resource resource( final String name ) {
      return ResourceFactory.createResource( urn( name ) );
   }

   static Map<String, String> createPrefixMap( final KnownVersion metaModelVersion ) {
      final Map<String, String> result = new LinkedHashMap<>();
      final SAMM samm = new SAMM( metaModelVersion );
      result.put( "samm", samm.getNamespace() );
      result.put( "samm-c", new SAMMC( metaModelVersion ).getNamespace() );
      result.put( "samm-e", new SAMME( metaModelVersion, samm ).getNamespace() );
      result.put( "unit", new UNIT( metaModelVersion, samm ).getNamespace() );
      result.put( "rdf", RDF.getURI() );
      result.put( "rdfs", RDFS.getURI() );
      result.put( "xsd", XSD.getURI() );
      return result;
   }

   static Map<String, String> createPrefixMap() {
      final Map<String, String> result = new LinkedHashMap<>();
      result.put( "samm", SammNs.SAMM.getNamespace() );
      result.put( "samm-c", SammNs.SAMMC.getNamespace() );
      result.put( "samm-e", SammNs.SAMME.getNamespace() );
      result.put( "unit", SammNs.UNIT.getNamespace() );
      result.put( "rdf", RDF.getURI() );
      result.put( "rdfs", RDFS.getURI() );
      result.put( "xsd", XSD.getURI() );
      return result;
   }
}
