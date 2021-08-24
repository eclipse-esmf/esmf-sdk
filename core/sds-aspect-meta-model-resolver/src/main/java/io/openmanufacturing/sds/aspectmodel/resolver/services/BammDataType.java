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

package io.openmanufacturing.sds.aspectmodel.resolver.services;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.jena.datatypes.BaseDatatype;

import io.openmanufacturing.sds.metamodel.datatypes.Curie;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class BammDataType<T> extends BaseDatatype implements TypedRdfDatatype<T> {
   private final Class<T> javaClass;
   private final Function<String, T> parser;
   private final Function<T, String> unparser;
   private final Predicate<String> lexicalValidator;

   public static final String CURIE_REGEX = "[a-zA-Z]*:[a-zA-Z]+";

   private BammDataType( final String uri, final Class<T> javaClass, final Function<String, T> parser,
         final Function<T, String> unparser, final Predicate<String> lexicalValidator ) {
      super( uri );
      this.javaClass = javaClass;
      this.parser = parser;
      this.unparser = unparser;
      this.lexicalValidator = lexicalValidator;
   }

   /**
    * Returns the DataType for bamm:curie of a given meta model version.
    *
    * @param metaModelVersion the respective meta model version
    * @return the corresponding DataType
    */
   public static TypedRdfDatatype<Curie> curie( final KnownVersion metaModelVersion ) {
      final String curieUrn = String
            .format( "urn:bamm:io.openmanufacturing:meta-model:%s#curie", metaModelVersion.toVersionString() );
      return new BammDataType<>( curieUrn, Curie.class, Curie::new, Curie::getValue,
            value -> value.matches( CURIE_REGEX ) );
   }

   @Override
   public Object parse( final String lexicalForm ) {
      try {
         return parser.apply( lexicalForm );
      } catch ( final Exception exception ) {
         if ( ExtendedXsdDataType.isCheckingEnabled() ) {
            throw exception;
         }
      }
      return lexicalForm;
   }

   @Override
   public Optional<T> parseTyped( final String lexicalForm ) {
      try {
         return Optional.of( parser.apply( lexicalForm ) );
      } catch ( final RuntimeException exception ) {
         if ( ExtendedXsdDataType.isCheckingEnabled() ) {
            throw exception;
         }
      }
      return Optional.empty();
   }

   @Override
   @SuppressWarnings( "unchecked" )
   public String unparse( final Object value ) {
      return unparseTyped( (T) value );
   }

   @Override
   public String unparseTyped( final T value ) {
      return unparser.apply( value );
   }

   @Override
   public boolean isValid( final String lexicalForm ) {
      return lexicalValidator.test( lexicalForm );
   }

   @Override
   public Class<T> getJavaClass() {
      return javaClass;
   }
}
