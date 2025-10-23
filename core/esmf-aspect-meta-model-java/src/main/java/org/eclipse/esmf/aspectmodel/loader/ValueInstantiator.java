/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.loader;

import java.util.Locale;
import java.util.Optional;

import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;

import org.apache.jena.vocabulary.RDF;

/**
 * Creates new instances of {@link ScalarValue} from the value representation in RDF
 */
public class ValueInstantiator {

   public static final String UNDEFINED_LANGUAGE_TAG = "und";

   /**
    * Creates a new scalar value from a lexical value representation.
    *
    * @param lexicalRepresentation the lexical value representation
    * @param languageTag if the datatype is rdf:langString, this must be set to the corresponding language tag, otherwise it can be null
    * @param datatypeUri the URI of the datatype that describes the value
    * @return the ScalarValue instance or empty if the lexical representation can not be parsed according to the datatype
    */
   public Optional<ScalarValue> buildScalarValue( final String lexicalRepresentation, final String languageTag, final String datatypeUri ) {
      // rdf:langString needs special handling here:
      // 1. A custom parser for rdf:langString values can not be registered with Jena, because it would only receive from Jena during
      // parsing the lexical representation of the value without the language tag (this is handled specially in Jena).
      // 2. This means that a Literal we receive here which has a type URI of rdf:langString will be of type org.apache.jena.datatypes
      // .xsd.impl.RDFLangString but _not_ org.eclipse.esmf.metamodel.datatypes.LangString as we would like to.
      // 3. So we construct an instance of LangString here from the RDFLangString.
      if ( datatypeUri.equals( RDF.langString.getURI() ) ) {
         return buildLanguageString( lexicalRepresentation, languageTag );
      }

      return SammXsdType.ALL_TYPES.stream()
            .filter( type -> type.getURI().equals( datatypeUri ) )
            .map( type -> type.parse( lexicalRepresentation ) )
            .<ScalarValue> map(
                  value -> new DefaultScalarValue( MetaModelBaseAttributes.builder().build(), value, new DefaultScalar( datatypeUri ) ) )
            .findAny();
   }

   private Optional<ScalarValue> buildLanguageString( final String lexicalRepresentation, final String languageTag ) {
      final Locale locale = Locale.forLanguageTag( languageTag );
      if ( UNDEFINED_LANGUAGE_TAG.equals( locale.toLanguageTag() ) ) {
         return Optional.empty();
      }
      final LangString langString = new LangString( lexicalRepresentation, locale );
      final Scalar type = new DefaultScalar( RDF.langString.getURI() );
      return Optional.of( new DefaultScalarValue( MetaModelBaseAttributes.builder().build(), langString, type ) );
   }
}
