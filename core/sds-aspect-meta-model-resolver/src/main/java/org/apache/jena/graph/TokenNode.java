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

package org.apache.jena.graph;

import java.io.ObjectStreamException;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.shared.PrefixMapping;

import io.openmanufacturing.sds.aspectmodel.resolver.parser.SmartToken;

public class TokenNode extends Node {
   private final SmartToken token;
   private final Node wrappedNode;

   public TokenNode( final Node wrappedNode, final Token token ) {
      super( wrappedNode.label );
      this.wrappedNode = wrappedNode;
      this.token = new SmartToken( token );
   }

   @Override
   public Object visitWith( final NodeVisitor visitor ) {
      return wrappedNode.visitWith( visitor );
   }

   @Override
   public boolean isConcrete() {
      return wrappedNode.isConcrete();
   }

   @Override
   public boolean equals( final Object o ) {
      return wrappedNode.equals( o instanceof TokenNode ? ((TokenNode) o).wrappedNode : o );
   }

   public SmartToken getToken() {
      return token;
   }

   @Override
   public boolean isLiteral() {
      return wrappedNode.isLiteral();
   }

   @Override
   public boolean isBlank() {
      return wrappedNode.isBlank();
   }

   @Override
   public boolean isURI() {
      return wrappedNode.isURI();
   }

   @Override
   public boolean isVariable() {
      return wrappedNode.isVariable();
   }

   @Override
   public boolean isNodeTriple() {
      return wrappedNode.isNodeTriple();
   }

   @Override
   public boolean isNodeGraph() {
      return wrappedNode.isNodeGraph();
   }

   @Override
   public boolean isExt() {
      return wrappedNode.isExt();
   }

   @Override
   public BlankNodeId getBlankNodeId() {
      return wrappedNode.getBlankNodeId();
   }

   @Override
   public String getBlankNodeLabel() {
      return wrappedNode.getBlankNodeLabel();
   }

   @Override
   public LiteralLabel getLiteral() {
      return wrappedNode.getLiteral();
   }

   @Override
   public Object getLiteralValue() {
      return wrappedNode.getLiteralValue();
   }

   @Override
   public String getLiteralLexicalForm() {
      return wrappedNode.getLiteralLexicalForm();
   }

   @Override
   public String getLiteralLanguage() {
      return wrappedNode.getLiteralLanguage();
   }

   @Override
   public String getLiteralDatatypeURI() {
      return wrappedNode.getLiteralDatatypeURI();
   }

   @Override
   public RDFDatatype getLiteralDatatype() {
      return wrappedNode.getLiteralDatatype();
   }

   @Override
   public boolean getLiteralIsXML() {
      return wrappedNode.getLiteralIsXML();
   }

   @Override
   public Object getIndexingValue() {
      return wrappedNode.getIndexingValue();
   }

   @Override
   public String getURI() {
      return wrappedNode.getURI();
   }

   @Override
   public String getNameSpace() {
      return wrappedNode.getNameSpace();
   }

   @Override
   public String getLocalName() {
      return wrappedNode.getLocalName();
   }

   @Override
   public String getName() {
      return wrappedNode.getName();
   }

   @Override
   public Triple getTriple() {
      return wrappedNode.getTriple();
   }

   @Override
   public Graph getGraph() {
      return wrappedNode.getGraph();
   }

   @Override
   public boolean hasURI( final String uri ) {
      return wrappedNode.hasURI( uri );
   }

   @Override
   public boolean sameValueAs( final Object o ) {
      return wrappedNode.sameValueAs( o );
   }

   @Override
   public int hashCode() {
      return wrappedNode.hashCode();
   }

   @Override
   public boolean matches( final Node other ) {
      return wrappedNode.matches( other );
   }

   @Override
   protected Object writeReplace() throws ObjectStreamException {
      return wrappedNode.writeReplace();
   }

   @Override
   public String toString() {
      return wrappedNode.toString();
   }

   @Override
   public String toString( final boolean quoting ) {
      return wrappedNode.toString( quoting );
   }

   @Override
   public String toString( final PrefixMapping pm ) {
      return wrappedNode.toString( pm );
   }

   @Override
   public String toString( final PrefixMapping pm, final boolean quoting ) {
      return wrappedNode.toString( pm, quoting );
   }
}
