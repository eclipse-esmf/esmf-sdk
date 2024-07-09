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

import java.util.Optional;

import org.apache.jena.datatypes.RDFDatatype;

public interface SammType<T> extends RDFDatatype {
   /**
    * Parses a lexical representation of a value of the type
    *
    * @param lexicalForm the lexical representation
    * @return if the lexical representation is valid for the type, Optional.of(x) where x is an object of the corresponding Java type (@see
    * {@link #getJavaClass()}), otherwise Optional.empty.
    */
   Optional<T> parseTyped( String lexicalForm );

   String unparseTyped( T value );

   @Override
   Class<T> getJavaClass();
}
