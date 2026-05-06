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

package org.eclipse.esmf.treesitterturtle;

/**
 * The types of parser tokens the RDF/Turtle parser produces, see also grammar.js.
 */
public class ParserTokenType {
   private ParserTokenType() {}

   public static final String SYMBOL_COLON = ":";
   public static final String SYMBOL_DOT = ".";
   public static final String SYMBOL_SEMICOLON = ";";
   public static final String SYMBOL_QUOTE = "\"";
   public static final String SYMBOL_SINGLE_QUOTE = "'";
   public static final String SYMBOL_TRIPLE_QUOTE = "\"\"\"";
   public static final String SYMBOL_TRIPLE_SINGLE_QUOTE = "'''";
   public static final String SYMBOL_DOUBLE_CARET = "^^";
   public static final String SYMBOL_OPENING_BRACKET = "(";
   public static final String SYMBOL_CLOSING_BRACKET = ")";
   public static final String SYMBOL_OPENING_SQUARE_BRACKET = "[";
   public static final String SYMBOL_CLOSING_SQUARE_BRACKET = "]";
   public static final String SYMBOL_OPENING_POINTY_BRACKET = "<";
   public static final String SYMBOL_CLOSING_POINTY_BRACKET = ">";

   public static final String A = "a"; // the syntactic shortcut for rdf:type
   public static final String AT_PREFIX = "@prefix";
   public static final String AT_BASE = "@base";
   public static final String DOCUMENT = "document";
   public static final String GRAPH = "graph";
   public static final String COMMENT = "comment";
   public static final String TRIPLE = "triple";
   public static final String DIRECTIVE = "directive";
   public static final String PREFIX_ID = "prefix_id";
   public static final String BASE = "base";
   public static final String SPARQL_BASE = "sparql_base";
   public static final String SPARQL_PREFIX = "sparql_prefix";
   public static final String PROPERTY_LIST = "property_list";
   public static final String PROPERTY = "property";
   public static final String OBJECT_LIST = "object_list";
   public static final String PREDICATE = "predicate";
   public static final String SUBJECT = "subject";
   public static final String BLANK_NODE_PROPERTY_LIST = "blank_node_property_list";
   public static final String COLLECTION = "collection";
   public static final String STRING = "string";
   public static final String IRI_REFERENCE = "iri_reference";
   public static final String INTEGER = "integer";
   public static final String DECIMAL = "decimal";
   public static final String DOUBLE = "double";
   public static final String RDF_LITERAL = "rdf_literal";
   public static final String BOOLEAN_LITERAL = "boolean_literal";
   public static final String PREFIXED_NAME = "prefixed_name";
   public static final String NAMESPACE = "namespace";
   public static final String BLANK_NODE_LABEL = "blank_node_label";
   public static final String LANG_TAG = "lang_tag";
   public static final String ANON = "anon";
   public static final String PN_PREFIX = "pn_prefix";
   public static final String PN_LOCAL = "pn_local";
}
