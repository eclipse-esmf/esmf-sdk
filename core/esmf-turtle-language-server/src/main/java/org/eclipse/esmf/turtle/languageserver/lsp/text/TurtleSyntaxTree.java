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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;
import org.treesitter.TSNode;
import org.treesitter.TSTree;

/**
 * <pre>
 * - 'document'
 *   - 'directive' (@prefix ex: <http://example.org/> .)
 *     - 'prefix_id' (@prefix ex: <http://example.org/> .)
 *       - '@prefix'
 *       - 'namespace' (ex:)
 *         - 'pn_prefix' (ex)
 *         - ':'
 *       - 'iri_reference' (<http://example.org/>)
 *         - '<'
 *         - '>'
 *       - '.'
 *   - 'triple'
 *     - 'subject' (ex:subject)
 *       - 'prefixed_name' (ex:subject)
 *         - 'namespace' (ex:)
 *           - 'pn_prefix' (ex)
 *           - ':'
 *         - 'pn_local' (subject)
 *     - 'property_list'
 *       - 'property' (ex:predicate1 ex:object1)
 *         - 'predicate' (ex:predicate1)
 *           - 'prefixed_name' (ex:predicate1)
 *             - 'namespace' (ex:)
 *               - 'pn_prefix' (ex)
 *               - ':'
 *             - 'pn_local' (predicate1)
 *         - 'object_list' (ex:object1)
 *           - 'prefixed_name' (ex:object1)
 *             - 'namespace' (ex:)
 *               - 'pn_prefix' (ex)
 *               - ':'
 *             - 'pn_local' (object1)
 *       - ';'
 *       - 'property' (ex:predicate2 123)
 *         - 'predicate' (ex:predicate2)
 *           - 'prefixed_name' (ex:predicate2)
 *             - 'namespace' (ex:)
 *               - 'pn_prefix' (ex)
 *               - ':'
 *             - 'pn_local' (predicate2)
 *         - 'object_list' (123)
 *           - 'integer' (123)
 *       - ';'
 *       - 'property' (ex:predicate3 true)
 *         - 'predicate' (ex:predicate3)
 *           - 'prefixed_name' (ex:predicate3)
 *             - 'namespace' (ex:)
 *               - 'pn_prefix' (ex)
 *               - ':'
 *             - 'pn_local' (predicate3)
 *         - 'object_list' (true)
 *           - 'boolean_literal' (true)
 *             - 'true'
 *       - ';'
 *       - 'property' (ex:predicate4 "some string")
 *         - 'predicate' (ex:predicate4)
 *           - 'prefixed_name' (ex:predicate4)
 *             - 'namespace' (ex:)
 *               - 'pn_prefix' (ex)
 *               - ':'
 *             - 'pn_local' (predicate4)
 *         - 'object_list' ("some string")
 *           - 'rdf_literal' ("some string")
 *             - 'string' ("some string")
 *               - '"'
 *               - '"'
 *       - ';'
 *       - 'property' (ex:predicate5 "some langString"@en)
 *         - 'predicate' (ex:predicate5)
 *           - 'prefixed_name' (ex:predicate5)
 *             - 'namespace' (ex:)
 *               - 'pn_prefix' (ex)
 *               - ':'
 *             - 'pn_local' (predicate5)
 *         - 'object_list' ("some langString"@en)
 *           - 'rdf_literal' ("some langString"@en)
 *             - 'string' ("some langString")
 *               - '"'
 *               - '"'
 *             - 'lang_tag' (@en)
 *       - ';'
 *       - 'property' (ex:predicate6 "123"^^xsd:decimal)
 *         - 'predicate' (ex:predicate6)
 *           - 'prefixed_name' (ex:predicate6)
 *             - 'namespace' (ex:)
 *               - 'pn_prefix' (ex)
 *               - ':'
 *             - 'pn_local' (predicate6)
 *         - 'object_list' ("123"^^xsd:decimal)
 *           - 'rdf_literal' ("123"^^xsd:decimal)
 *             - 'string' ("123")
 *               - '"'
 *               - '"'
 *             - '^^'
 *             - 'prefixed_name' (xsd:decimal)
 *               - 'namespace' (xsd:)
 *                 - 'pn_prefix' (xsd)
 *                 - ':'
 *               - 'pn_local' (decimal)
 *       - ';'
 *       - 'property' (ex:predicate7 <http://example.org/fulluri>)
 *         - 'predicate' (ex:predicate7)
 *           - 'prefixed_name' (ex:predicate7)
 *             - 'namespace' (ex:)
 *               - 'pn_prefix' (ex)
 *               - ':'
 *             - 'pn_local' (predicate7)
 *         - 'object_list' (<http://example.org/fulluri>)
 *           - 'iri_reference' (<http://example.org/fulluri>)
 *             - '<'
 *             - '>'
 *     - '.'
 *   - 'triple' (<http://example.com/anotherSubject> a rdf:type .)
 *     - 'subject' (<http://example.com/anotherSubject>)
 *       - 'iri_reference' (<http://example.com/anotherSubject>)
 *         - '<'
 *         - '>'
 *     - 'property_list' (a rdf:type)
 *       - 'property' (a rdf:type)
 *         - 'predicate' (a)
 *           - 'a'
 *         - 'object_list' (rdf:type)
 *           - 'prefixed_name' (rdf:type)
 *             - 'namespace' (rdf:)
 *               - 'pn_prefix' (rdf)
 *               - ':'
 *             - 'pn_local' (type)
 *     - '.'
 * </pre>
 */
public class TurtleSyntaxTree {
   private final TSTree parserTree;
   private final Document document;

   /**
    * See rules in grammar.js
    */
   private enum ParserType {
      document,
      graph,
      comment,
      triple,
      directive,
      prefix_id,
      base,
      sparql_base,
      sparql_prefix,
      property_list,
      property,
      object_list,
      predicate,
      subject,
      blank_node_property_list,
      collection,
      string,
      iri_reference,
      integer,
      decimal,
      _double( "double" ),
      rdf_literal,
      boolean_literal,
      prefixed_name,
      namespace,
      blank_node_label,
      lang_tag,
      anon,
      pn_prefix,
      pn_local;

      private final String type;

      ParserType() {
         type = name().toLowerCase();
      }

      ParserType( final String type ) {
         this.type = type;
      }

      public static @Nullable ParserType fromString( final String type ) {
         if ( type.equals( "double" ) ) {
            return _double;
         }
         try {
            return valueOf( type );
         } catch ( final IllegalArgumentException exception ) {
            return null;
         }
      }

      public String getType() {
         return type;
      }
   }

   public TurtleSyntaxTree( final TSTree parserTree, final Document document ) {
      this.parserTree = parserTree;
      this.document = document;
   }

   public Map<String, String> prefixes() {
      return null;
   }

   private static Stream<TSNode> children( final TSNode node ) {
      final Stream.Builder<TSNode> builder = Stream.builder();
      for ( int i = 0; i < node.getChildCount(); i++ ) {
         builder.add( node.getChild( 0 ) );
      }
      return builder.build();
   }

   private static TurtleDocument parseRootNode( final TSNode rootNode ) {
      return new DefaultTurtleDocument( children( rootNode ).map( TurtleSyntaxTree::parse ).toList() );
   }

   private static Node parse( final TSNode node ) {
      return switch ( ParserType.fromString( node.getType() ) ) {
         case directive -> parse( node.getChild( 0 ) );
         case prefix_id -> null;
         case namespace -> null;
         case pn_prefix -> null;
         case document -> null;
         case graph -> null;
         case comment -> null;
         case triple -> null;
         case subject -> null;
         case prefixed_name -> null;
         case pn_local -> null;
         case base -> null;
         case sparql_base -> null;
         case sparql_prefix -> null;
         case property_list -> null;
         case property -> null;
         case predicate -> null;
         case object_list -> null;
         case integer -> null;
         case boolean_literal -> null;
         case rdf_literal -> null;
         case blank_node_property_list -> null;
         case collection -> null;
         case string -> null;
         case lang_tag -> null;
         case iri_reference -> null;
         case decimal -> null;
         case _double -> null;
         case blank_node_label -> null;
         case anon -> null;
         case null -> throw new UnsupportedOperationException();
      };
   }

   interface TurtleDocument {
      List<Node> nodes();
   }

   public record DefaultTurtleDocument(
         List<Node> nodes
   ) implements TurtleDocument {}

   interface Node {
   }

   interface Directive extends Node {
   }

   interface Comment extends Node {
      String comment();
   }

   interface PrefixDeclaration extends Directive {
      String prefix();

      String namespace();
   }

   public record DefaultPrefixDeclaration(
         String prefix, String namespace
   ) implements PrefixDeclaration {}

   interface RelativeOrAbsoluteIri extends Object {
   }

   interface Iri extends RelativeOrAbsoluteIri {
      String iri();
   }

   interface PrefixedName extends RelativeOrAbsoluteIri {
      String prefix();

      String localName();
   }

   interface Triple extends Node {
      RelativeOrAbsoluteIri subject();

      List<PredicateDeclaration> properties();
   }

   interface PredicateDeclaration extends Node {
      RelativeOrAbsoluteIri predicate();

      List<ObjectDeclaration> objects();
   }

   interface ObjectDeclaration extends Directive {
      List<Object> objects();
   }

   interface Object {
   }

   interface Literal extends Object {
      String lexicalValue();
   }

   interface NumericLiteral extends Literal {
   }

   interface BooleanLiteral extends Literal {
      enum Value {
         TRUE, FALSE
      }

      Value value();
   }

   interface StringLiteral extends Literal {
   }

   interface LangStringLiteral extends Literal {
      String languageTag();
   }

   interface TypedLiteral extends Literal {
      RelativeOrAbsoluteIri datatypeIri();
   }
}

