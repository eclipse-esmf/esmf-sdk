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

package org.eclipse.esmf.aspectmodel.resolver.services;

import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as the bridge between the scalar types used in RDF (XSD/RDF/SAMM-specific (i.e. samm:curie)) and
 * their representations in Java. It implements {@link RDFDatatype} in order to register the types directly
 * in the Jena RDF parser. The actual registration is performed by calling {@link #setupTypeMapping()}.
 */
public class DataType {
   private static final Logger LOG = LoggerFactory.getLogger( DataType.class );
   private static boolean setupPerformed = false;

   private DataType() {
   }

   /**
    * Idempotent method to register the SAMM type mapping in the Jena RDF parser.
    */
   public static synchronized void setupTypeMapping() {
      if ( !setupPerformed ) {
         try {
            ExtendedXsdDataType.datatypeFactory = DatatypeFactory.newInstance();
         } catch ( final DatatypeConfigurationException exception ) {
            LOG.error( "Could not instantiate DatatypeFactory", exception );
         }

         final TypeMapper typeMapper = TypeMapper.getInstance();
         DataType.getAllSupportedTypes().forEach( typeMapper::registerDatatype );
         setupPerformed = true;
      }
   }

   /**
    * Returns all XSD types supported in Aspect models
    *
    * @return the list of supported XSD types
    */
   public static List<RDFDatatype> getSupportedXsdTypes() {
      return ExtendedXsdDataType.SUPPORTED_XSD_TYPES;
   }

   /**
    * Returns the list of all supported DataTypes, which is equivalent to the union of {@link #getSupportedXsdTypes()}
    * and DataTypes for samm:curie of all known meta model versions ({@see KnownVersion}).
    *
    * @return the list of all supported types
    */
   public static List<RDFDatatype> getAllSupportedTypes() {
      return getSupportedXsdTypes();
   }

   /**
    * Returns the list of all supported DataTypes of a given meta model version, which is equivalent to the union
    * of {@link #getSupportedXsdTypes()} and the DataType for samm:curie corresponding to the meta model version.
    *
    * @param metaModelVersion the given meta model version
    * @return the list of all supported types in the meta model version
    */
   public static List<RDFDatatype> getAllSupportedTypesForMetaModelVersion( final KnownVersion metaModelVersion ) {
      return ImmutableList
            .copyOf( Iterables.concat( getAllSupportedTypes(), List.of( SammDataType.curie( metaModelVersion ) ) ) );
   }

   /**
    * Returns the Java class corresponding to a XSD type in a given meta model version.
    *
    * @param type the resource of the data type
    * @param metaModelVersion the given meta model version
    * @return the java class
    */
   public static Class<?> getJavaTypeForMetaModelType( final Resource type, final KnownVersion metaModelVersion ) {
      return DataType.getAllSupportedTypesForMetaModelVersion( metaModelVersion )
            .stream()
            .filter( xsdType -> xsdType.getURI().equals( type.getURI() ) )
            .map( RDFDatatype::getJavaClass )
            .findAny()
            .orElseThrow(
                  () -> new IllegalStateException( "Invalid data type " + type + " found in model." ) );
   }
}
