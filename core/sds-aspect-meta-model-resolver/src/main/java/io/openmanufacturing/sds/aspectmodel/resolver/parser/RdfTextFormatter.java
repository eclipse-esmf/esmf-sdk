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

package io.openmanufacturing.sds.aspectmodel.resolver.parser;

/**
 * Interface to allow interested parties to do structured formatting of RDF documents (for example syntax highlighting)
 */
public interface RdfTextFormatter {

   void reset();

   String getResult();

   RdfTextFormatter formatIri( String iri );

   RdfTextFormatter formatDirective( String directive );

   RdfTextFormatter formatPrefix( String prefix );

   RdfTextFormatter formatName( String name );

   RdfTextFormatter formatString( String string );

   RdfTextFormatter formatLangTag( String langTag );

   RdfTextFormatter formatDefault( String text );

   RdfTextFormatter formatPrimitive( String primitive );

   RdfTextFormatter formatError( String text );
}
