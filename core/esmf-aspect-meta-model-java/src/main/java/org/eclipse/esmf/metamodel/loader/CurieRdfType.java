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

package org.eclipse.esmf.metamodel.loader;

import java.util.Optional;

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.impl.LiteralLabel;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.aspectmodel.resolver.services.TypedRdfDatatype;
import org.eclipse.esmf.aspectmodel.vocabulary.BAMM;
import org.eclipse.esmf.metamodel.datatypes.Curie;

public class CurieRdfType implements TypedRdfDatatype<Curie> {
   @Override
   public Optional<Curie> parseTyped( final String lexicalForm ) {
      if ( isValid( lexicalForm ) ) {
         return Optional.of( new Curie( lexicalForm ) );
      }
      return Optional.empty();
   }

   @Override
   public String unparseTyped( final Curie value ) {
      return value.getValue();
   }

   @Override
   public String getURI() {
      return new BAMM( KnownVersion.getLatest() ).curie().getURI();
   }

   @Override
   public String unparse( final Object value ) {
      if ( value instanceof Curie curie ) {
         return unparseTyped( curie );
      }
      throw new AspectLoadingException( "Value is no valid curie: " + value );
   }

   @Override
   public Object parse( final String lexicalForm ) throws DatatypeFormatException {
      return parseTyped( lexicalForm ).orElseThrow( () -> new DatatypeFormatException() );
   }

   @Override
   public boolean isValid( final String lexicalForm ) {
      return lexicalForm.matches( "[^:]*:.*" );
   }

   @Override
   public boolean isValidValue( final Object valueForm ) {
      return isValid( valueForm.toString() );
   }

   @Override
   public boolean isValidLiteral( final LiteralLabel lit ) {
      return isValid( lit.getLexicalForm() );
   }

   @Override
   public boolean isEqual( final LiteralLabel value1, final LiteralLabel value2 ) {
      return value1.getLexicalForm().equals( value2.getLexicalForm() );
   }

   @Override
   public int getHashCode( final LiteralLabel lit ) {
      return lit.getDefaultHashcode();
   }

   @Override
   public Class<Curie> getJavaClass() {
      return (Class<Curie>) Curie.class;
   }

   @Override
   public Object cannonicalise( final Object value ) {
      return value;
   }

   @Override
   public Object extendedTypeDefinition() {
      return null;
   }

   @Override
   public RDFDatatype normalizeSubType( final Object value, final RDFDatatype datatype ) {
      return datatype;
   }
}
