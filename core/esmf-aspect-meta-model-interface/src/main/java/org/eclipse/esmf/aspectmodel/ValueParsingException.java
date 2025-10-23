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

package org.eclipse.esmf.aspectmodel;

import java.net.URI;

import org.apache.jena.rdf.model.Resource;

/**
 * Failure to parse a RDF literal
 */
public class ValueParsingException extends RuntimeException {
   private final Resource type;
   private final Object value;
   private long line;
   private long column;
   private URI sourceLocation;
   private String sourceDocument;

   public ValueParsingException( final Resource type, final Object value ) {
      this.type = type;
      this.value = value;
   }

   public ValueParsingException( final Resource type, final Object value, final Throwable cause ) {
      super( cause );
      this.type = type;
      this.value = value;
   }

   public Resource getType() {
      return type;
   }

   public Object getValue() {
      return value;
   }

   public ValueParsingException setLine( final long line ) {
      this.line = line;
      return this;
   }

   public ValueParsingException setColumn( final long column ) {
      this.column = column;
      return this;
   }

   public long getLine() {
      return line;
   }

   public long getColumn() {
      return column;
   }

   public String getSourceDocument() {
      return sourceDocument;
   }

   public URI getSourceLocation() {
      return sourceLocation;
   }

   public void setSourceDocument( final String sourceDocument ) {
      this.sourceDocument = sourceDocument;
   }

   public void setSourceLocation( final URI sourceLocation ) {
      this.sourceLocation = sourceLocation;
   }
}
