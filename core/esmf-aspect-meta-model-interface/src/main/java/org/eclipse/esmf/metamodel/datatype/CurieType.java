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

package org.eclipse.esmf.metamodel.datatype;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.impl.LiteralLabel;

/**
 * Represents the samm:curie datatype itself. For the class that represents a Curie value, see {@link Curie}.
 */
public class CurieType implements SammType<Curie> {
   public static final CurieType INSTANCE = new CurieType();
   public static final String CURIE_REGEX = "[a-zA-Z]*:[a-zA-Z]+";
   private final Function<String, Curie> parser = Curie::new;
   private final Function<Curie, String> unparser = Curie::value;
   private final Predicate<String> lexicalValidator = value -> value.matches( CURIE_REGEX );

   private CurieType() {
   }

   @Override
   public Optional<Curie> parseTyped( final String lexicalForm ) {
      try {
         return Optional.of( parser.apply( lexicalForm ) );
      } catch ( final RuntimeException exception ) {
         if ( SammXsdType.isCheckingEnabled() ) {
            throw exception;
         }
      }
      return Optional.empty();
   }

   @Override
   public String unparseTyped( final Curie value ) {
      return unparser.apply( value );
   }

   @Override
   public String getURI() {
      return SammNs.SAMM.curie().getURI();
   }

   @Override
   public String unparse( final Object value ) {
      return unparseTyped( (Curie) value );
   }

   @Override
   public Object parse( final String lexicalForm ) throws DatatypeFormatException {
      try {
         return parser.apply( lexicalForm );
      } catch ( final Exception exception ) {
         if ( SammXsdType.isCheckingEnabled() ) {
            throw exception;
         }
      }
      return lexicalForm;
   }

   @Override
   public boolean isValid( final String lexicalForm ) {
      return lexicalValidator.test( lexicalForm );
   }

   @Override
   public boolean isValidValue( final Object valueForm ) {
      return isValid( unparse( valueForm ) );
   }

   @Override
   public boolean isValidLiteral( final LiteralLabel lit ) {
      return lexicalValidator.test( lit.getValue().toString() );
   }

   @Override
   public boolean isEqual( final LiteralLabel value1, final LiteralLabel value2 ) {
      return Objects.equals( value1, value2 );
   }

   @Override
   public int getHashCode( final LiteralLabel lit ) {
      return System.identityHashCode( lit );
   }

   @Override
   public Class<Curie> getJavaClass() {
      return Curie.class;
   }

   @Override
   public Object cannonicalise( final Object value ) {
      return value;
   }

   @Override
   public Object extendedTypeDefinition() {
      return Curie.class;
   }

   @Override
   public RDFDatatype normalizeSubType( final Object value, final RDFDatatype dt ) {
      return dt;
   }
}
