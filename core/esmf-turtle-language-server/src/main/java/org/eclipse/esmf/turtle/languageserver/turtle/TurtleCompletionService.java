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

package org.eclipse.esmf.turtle.languageserver.turtle;

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;
import static org.eclipse.esmf.metamodel.vocabulary.SammNs.UNIT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Resource;
import org.eclipse.esmf.aspectmodel.shacl.SHACL;
import org.eclipse.esmf.metamodel.Units;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCursor;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammAttributeValueCompletionService;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammShapeUtils;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSnippetCompletionService;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSuggestionContextDetector;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class TurtleCompletionService extends TurtleService {
   static final Map<String, List<CompletionItem>> SAMM_PREFIXES_COMPLETION_MAP = initSammPrefixesCompletionMap();
   private final SammSuggestionContextDetector contextDetector = new SammSuggestionContextDetector();
   private final SammSnippetCompletionService snippetCompletionService = new SammSnippetCompletionService();
   private final SammAttributeValueCompletionService attributeValueCompletionService = new SammAttributeValueCompletionService();

   /**
    * Dropdown completion for the prefixed name at the cursor. Returns the full list of candidates for
    * the client to filter, so client features like fuzzy match are supported.
    * <ul>
    * <li>{@code :} prefix: the local names of the document subjects that fit the expected type of the
    * predicate</li>
    * <li>otherwise: all names of the typed SAMM, unit or SHACL namespace</li>
    * </ul>
    */
   public List<CompletionItem> complete( final ParsedDocument parsedDocument, final CompletionParams completionParams ) {
      return complete( parsedDocument, completionParams, false );
   }

   public List<CompletionItem> complete( final ParsedDocument parsedDocument, final CompletionParams completionParams,
         final boolean snippetSupport ) {
      final TurtleCursor analysis = new TurtleCompletionTree( parsedDocument ).cursorAt( completionParams.getPosition() );
      final Optional<SammCompletionContext> cursorContext = contextDetector.detect( analysis );
      if ( cursorContext.isPresent() ) {
         final List<CompletionItem> attributeCompletions = attributeValueCompletionService.complete( cursorContext.get() );
         if ( !attributeCompletions.isEmpty() ) {
            return attributeCompletions;
         }
      }

      final List<CompletionItem> result = new ArrayList<>( vocabularyCompletions( analysis, completionParams.getPosition() ) );
      if ( snippetSupport && cursorContext.isPresent() ) {
         result.addAll( snippetCompletionService.complete( cursorContext.get() ) );
      }
      return result;
   }

   private List<CompletionItem> vocabularyCompletions( final TurtleCursor analysis, final Position position ) {
      final Optional<String> typedPrefix = analysis.prefix();
      if ( typedPrefix.isEmpty() ) {
         return List.of();
      }
      final String prefix = typedPrefix.get();
      final Range range = new Range( analysis.typedStart(), position );

      if ( ":".equals( prefix ) ) {
         final Optional<Set<String>> expectedSubjects = SammShapeUtils.expectedSubjects( analysis.subjectTypes(), analysis.predicateIri() );
         final Map<String, Set<String>> subjectTypes = analysis.subjectTypes();
         return analysis.otherLocalNamesOf( prefix ).stream()
               .filter( name -> expectedSubjects.map( subjects -> subjects.contains( analysis.iri( prefix + name ) ) ).orElse( true ) )
               // The subject of the statement being edited must not be suggested as its own object
               .filter( name -> !analysis.isStatementSubject( analysis.iri( prefix + name ) ) )
               .map( name -> localNameItem( name, prefix, analysis, subjectTypes, range ) )
               .toList();
      }

      final String namespace = TurtleCursor.withLatestSammNamespace( analysis.iri( prefix ) );
      return SAMM_PREFIXES_COMPLETION_MAP.getOrDefault( namespace, List.of() ).stream()
            .map( template -> expandedItem( template, prefix, range ) )
            .toList();
   }

   /**
    * A local name of the document, with the type of its subject as detail and a text edit that
    * replaces the typed
    * prefix and name
    */
   private static CompletionItem localNameItem( final String name, final String prefix, final TurtleCursor analysis,
         final Map<String, Set<String>> subjectTypes, final Range range ) {
      final Set<String> types = subjectTypes.getOrDefault( analysis.iri( prefix + name ), Set.of() );
      final String sammNamespace = SammNs.SAMM.getNamespace();
      final CompletionItem item = new CompletionItem( name );
      item.setKind( CompletionItemKind.Reference );
      item.setDetail( types.stream().filter( type -> type.startsWith( sammNamespace ) )
            .map( type -> "Local " + type.substring( sammNamespace.length() ).replaceAll( "(?<=[a-z])(?=[A-Z])", " " ) )
            .findFirst().orElse( "Local name" ) );
      item.setSortText( "01_" + name );
      item.setFilterText( prefix + name );
      item.setInsertTextFormat( InsertTextFormat.PlainText );
      item.setTextEdit( Either.forLeft( new TextEdit( range, prefix + name ) ) );
      return item;
   }

   private static HashMap<String, List<CompletionItem>> initSammPrefixesCompletionMap() {
      final HashMap<String, List<CompletionItem>> result = new HashMap<>(
            SammNs.sammNamespaces().map( n -> Map.entry( n.getNamespace(), namespaceItems( n, "SAMM" ) ) ).collect( asMap() ) );

      result.put( UNIT.getNamespace(), Units.getUnits().stream()
            .map( u -> templateItem( u.getName(), CompletionItemKind.Reference, SAMM.UNIT ) ).toList() );

      final SHACL shacl = new SHACL();
      result.put( shacl.getNamespace(), namespaceItems( shacl, "SHACL" ) );

      return result;
   }

   private static List<CompletionItem> namespaceItems( final RdfNamespace rdfNamespace, final String namespace ) {
      return Stream.of(
            templateItems( rdfNamespace.allResources(), CompletionItemKind.Class, namespace + " Class" ),
            templateItems( rdfNamespace.allProperties(), CompletionItemKind.Property, namespace + " Attribute" ) )
            .flatMap( Collection::stream ).toList();
   }

   private static List<CompletionItem> templateItems( final Collection<? extends Resource> resources,
         final CompletionItemKind kind, final String detail ) {
      return resources.stream().map( r -> templateItem( r.getLocalName(), kind, detail ) ).toList();
   }

   private static CompletionItem templateItem( final String label, final CompletionItemKind kind, final String detail ) {
      final CompletionItem item = new CompletionItem( label );
      item.setKind( kind );
      item.setDetail( detail );
      return item;
   }

   /**
    * A copy of a template item for the typed prefix, with the prefixed name as label, insert text and
    * filter text
    * as the attribute value completion does, and a text edit over the range that replaces the typed
    * prefix and name
    */
   private static CompletionItem expandedItem( final CompletionItem template, final String prefix, final Range range ) {
      final String name = template.getLabel();
      final String prefixed = prefix + name;
      final CompletionItem item = templateItem( prefixed, template.getKind(), template.getDetail() );
      item.setFilterText( prefixed + " " + name );
      item.setInsertText( prefixed );
      item.setInsertTextFormat( InsertTextFormat.PlainText );
      item.setTextEdit( Either.forLeft( new TextEdit( range, prefixed ) ) );
      return item;
   }
}
