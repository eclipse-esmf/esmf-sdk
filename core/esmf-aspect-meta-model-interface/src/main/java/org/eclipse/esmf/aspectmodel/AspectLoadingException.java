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

package org.eclipse.esmf.aspectmodel;

import java.net.URI;
import java.util.function.Supplier;

import org.apache.jena.rdf.model.RDFNode;

public class AspectLoadingException extends RuntimeException {
   private static final long serialVersionUID = 7687644022103150329L;
   private final RDFNode highlightElement;
   private final URI sourceDocument;
   private final Supplier<String> documentContent;

   public AspectLoadingException( final String message ) {
      super( message );
      highlightElement = null;
      sourceDocument = null;
      documentContent = null;
   }

   public AspectLoadingException( final String message, final Throwable cause ) {
      super( message, cause );
      highlightElement = null;
      sourceDocument = null;
      documentContent = null;
   }

   public AspectLoadingException( final String message, final URI sourceDocument, final Supplier<String> documentContent,
         final RDFNode highlightElement ) {
      super( message );
      this.sourceDocument = sourceDocument;
      this.documentContent = documentContent;
      this.highlightElement = highlightElement;
   }

   public RDFNode highlightElement() {
      return highlightElement;
   }

   public URI getSourceDocument() {
      return sourceDocument;
   }

   public Supplier<String> getDocumentContent() {
      return documentContent;
   }
}
