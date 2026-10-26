/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

public final class RdfDatatypeUris {
   // Do not use RDF.langString.getURI() for string-only URI access: touching
   // Jena's RDF vocabulary can trigger order-sensitive Jena runtime initialization.
   public static final String LANG_STRING = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";

   private RdfDatatypeUris() {}
}
