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
public interface IRdfTextFormatter {

   void reset();

   String getResult();

   IRdfTextFormatter formatIri( String iri );

   IRdfTextFormatter formatDirective( String directive );

   IRdfTextFormatter formatPrefix( String prefix );

   IRdfTextFormatter formatName( String name );

   IRdfTextFormatter formatString( String string );

   IRdfTextFormatter formatLangTag( String langTag );

   IRdfTextFormatter formatDefault( String text );

   IRdfTextFormatter formatPrimitive( String primitive );

   IRdfTextFormatter formatError( String text );
}
