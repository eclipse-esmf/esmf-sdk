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
      result.put( samm.getShortForm(), samm.getNamespace() );
      final SAMMC sammc = new SAMMC( metaModelVersion );
      result.put( sammc.getShortForm(), sammc.getNamespace() );
      final SAMME samme = new SAMME( metaModelVersion, samm );
      result.put( samme.getShortForm(), samme.getNamespace() );
      final UNIT unit = new UNIT( metaModelVersion, samm );
      result.put( unit.getShortForm(), unit.getNamespace() );
      result.put( "rdf", RDF.getURI() );
      result.put( "rdfs", RDFS.getURI() );
      result.put( "xsd", XSD.getURI() );
      return result;
   }

   static Map<String, String> createPrefixMap() {
      final Map<String, String> result = new LinkedHashMap<>();
      result.put( SammNs.SAMM.getShortForm(), SammNs.SAMM.getNamespace() );
      result.put( SammNs.SAMMC.getShortForm(), SammNs.SAMMC.getNamespace() );
      result.put( SammNs.SAMME.getShortForm(), SammNs.SAMME.getNamespace() );
      result.put( SammNs.UNIT.getShortForm(), SammNs.UNIT.getNamespace() );
      result.put( "rdf", RDF.getURI() );
      result.put( "rdfs", RDFS.getURI() );
      result.put( "xsd", XSD.getURI() );
      return result;
   }
}
