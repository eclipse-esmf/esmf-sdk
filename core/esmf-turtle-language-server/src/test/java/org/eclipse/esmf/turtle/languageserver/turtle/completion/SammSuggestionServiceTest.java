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

package org.eclipse.esmf.turtle.languageserver.turtle.completion;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleCompletionService;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSnippetCatalog.SnippetDefinition;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class SammSuggestionServiceTest {
   private static final String HEADER = """
      @prefix : <urn:samm:example.snippets:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
      """;
   private static final String FULL_HEADER = HEADER + """
      @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.2.0#> .
      @prefix unit: <urn:samm:org.eclipse.esmf.samm:unit:2.2.0#> .
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
      """;
   private final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
   private final SammSnippetCompletionService snippets = new SammSnippetCompletionService();

   @AfterEach
   void close() {
      parser.close();
   }

   @Test
   void topLevelPropertyHasFourEditableFieldsAndFinalTabStop() {
      final Request request = request( HEADER + "\n|" );
      final CompletionItem item = snippetNamed( request, "SAMM Property" );
      assertThat( item.getKind() ).isEqualTo( CompletionItemKind.Snippet );
      assertThat( item.getInsertTextFormat() ).isEqualTo( InsertTextFormat.Snippet );
      assertThat( item.getTextEdit().getLeft().getNewText() ).isEqualTo( """
         :${1:propertyName} a samm:Property ;
            samm:preferredName "${2}"@en ;
            samm:description "${3}"@en ;
            samm:characteristic :${4:CharacteristicName} .$0""" );
      assertThat( item.getTextEdit().getLeft().getRange().getStart() ).isEqualTo( request.position );
      assertThat( item.getTextEdit().getLeft().getRange().getEnd() ).isEqualTo( request.position );
      assertValid( apply( request, item ) );
   }

   @Test
   void topLevelSnippetIsOfferedWhenTypedTextIsDirectlyFollowedByAnotherStatement() {
      final Request request = request( HEADER + "\nprop|\n:Existing a samm:Aspect ;\n   samm:properties ( ) .\n" );
      assertThat( snippets.complete( request.document, request.position ) )
            .extracting( CompletionItem::getLabel )
            .contains( "SAMM Property" );
      final TextEdit edit = snippetNamed( request, "SAMM Property" ).getTextEdit().getLeft();
      assertThat( edit.getRange().getStart() ).isEqualTo( new Position( 4, 0 ) );
      assertThat( edit.getRange().getEnd() ).isEqualTo( request.position );
   }

   @Test
   void shorthandDoesNotProduceSnippet() {
      final Request request = request( HEADER + "\ntemperature.prop|" );
      assertThat( snippets.complete( request.document, request.position ) ).isEmpty();
   }

   @ParameterizedTest
   @ValueSource( strings = { "2.0.0", "2.1.0", "2.2.0" } )
   void honorsDeclaredVersionAndNamedModelAlias( final String version ) {
      final String header =
            HEADER.replace( "@prefix :", "@prefix model:" ).replace( "@prefix samm:", "@prefix meta:" ).replace( "2.2.0", version );
      final CompletionItem item = snippetNamed( request( header + "\n|" ), "SAMM Property" );
      assertThat( item.getDetail() ).isEqualTo( "SAMM " + version );
      assertThat( item.getTextEdit().getLeft().getNewText() ).startsWith( "model:${1:propertyName} a meta:Property" )
            .contains( "meta:characteristic model:${4:CharacteristicName}" );
   }

   @Test
   void descriptionHasEditableTextAndPreservesFinalPeriod() {
      final Request request = request( HEADER + ":p a samm:Property ;\n    |.\n" );
      final CompletionItem item = snippetNamed( request, "SAMM description" );
      assertThat( item.getTextEdit().getLeft().getNewText() ).isEqualTo( "samm:description \"${1}\"@en" );
      assertValid( apply( request, item ) );
   }

   @Test
   void descriptionInsertsSeparatorBeforeExistingPredicate() {
      final Request request = request( HEADER + ":p a samm:Property ;\n    samm:des|\n    samm:characteristic :C ." );
      final CompletionItem item = snippetNamed( request, "SAMM description" );
      assertThat( item.getTextEdit().getLeft().getNewText() ).isEqualTo( "samm:description \"${1}\"@en ;" );
      assertValid( apply( request, item ) );
   }

   @Test
   void descriptionPreservesExistingSemicolon() {
      final Request request = request( HEADER + ":p a samm:Property ;\n    samm:des| ; samm:characteristic :C ." );
      assertValid( apply( request, snippetNamed( request, "SAMM description" ) ) );
   }

   @Test
   void ordinaryCompletionCanReplaceWholePartialPredicateAtMidToken() {
      final Request request = request( HEADER + ":p a samm:Property ;\n    samm:de|s ." );
      assertValid( apply( request, snippetNamed( request, "SAMM description" ) ) );
   }

   @Test
   void preservesCrLfIndentationAndUtf16EditPositions() {
      final Request request = request( HEADER.replace( "\n", "\r\n" ) + "# 😀\r\n\t|" );
      final CompletionItem item = snippetNamed( request, "SAMM Property" );
      assertThat( item.getTextEdit().getLeft().getRange().getStart() ).isEqualTo( new Position( 4, 1 ) );
      assertThat( item.getTextEdit().getLeft().getNewText() ).contains( "\r\n\t   samm:characteristic" );
      final Request unicode = request( HEADER + ":p a samm:Property ; samm:preferredName \"😀\"@en ; samm:des| ." );
      final CompletionItem description = snippetNamed( unicode, "SAMM description" );
      assertThat( description.getTextEdit().getLeft().getRange().getEnd() ).isEqualTo( unicode.position );
      assertValid( apply( unicode, description ) );
   }

   @Test
   void descriptionDoesNotRequireModelNamespaceDeclaration() {
      final Request request = request( "@prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .\n"
            + "<urn:example:p> a samm:Property ; samm:des|" );
      assertThat( snippets.complete( request.document, request.position ) )
            .anyMatch( item -> item.getLabel().equals( "SAMM description" ) );
   }

   @Test
   void templateResultsAreMergedWithVocabularyAndGatedBySnippetSupport() {
      final Request request = request( HEADER + ":p a samm:Property ; samm:des|" );
      final CompletionParams params = new CompletionParams( new TextDocumentIdentifier( "file:///test.ttl" ), request.position );
      final TurtleCompletionService service = new TurtleCompletionService();
      final List<CompletionItem> original = service.complete( request.document, params );
      assertThat( original ).isNotEmpty().noneMatch( item -> item.getKind() == CompletionItemKind.Snippet );
      assertThat( service.complete( request.document, params, true ) ).hasSizeGreaterThan( original.size() )
            .anyMatch( item -> item.getKind() == CompletionItemKind.Snippet );
   }

   @ParameterizedTest
   @ValueSource( strings = { "\"label\"@en", "\"label\"^^<http://www.w3.org/2001/XMLSchema#string>",
         "\"label\"^^xsd:string", "3.14", "true", "[ samm:preferredName \"nested\"@en ]", "( :one :two )",
         ":reference\\;part", ":reference\\#part", ":reference\\(part\\)" } )
   void recognizesPredicateSlotsFollowingDifferentObjectForms( final String object ) {
      final Request request = request( HEADER + "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n"
            + ":p a samm:Property ; samm:see " + object + " ;\n    samm:des| ." );
      assertValid( apply( request, snippetNamed( request, "SAMM description" ) ) );
   }

   @Test
   void suppressesInvalidPositionsWithoutThrowing() {
      final ParsedDocument document = parser.apply( new Document( "file:///test.ttl", HEADER ) );
      for ( final Position position : List.of( new Position( -1, 0 ), new Position( 0, -1 ), new Position( 0, 1000 ),
            new Position( 100, 0 ) ) ) {
         assertThat( snippets.complete( document, position ) ).isEmpty();
      }
   }

   @Test
   void descriptionOnAnotherSubjectDoesNotSuppressSuggestion() {
      final Request request = request( HEADER + ":other samm:description \"other\"@en .\n:p a samm:Property ; samm:des| ." );
      assertValid( apply( request, snippetNamed( request, "SAMM description" ) ) );
   }

   @Test
   void recognizesEscapedSubjectBeforeIncompletePredicate() {
      final Request request = request( HEADER + ":p\\;name a samm:Property ; samm:des| ." );
      assertValid( apply( request, snippetNamed( request, "SAMM description" ) ) );
   }

   @Test
   void recognizesStandaloneSubjectRecoveredWithoutSubjectWrapper() {
      final Request request = request( HEADER + ":p a samm:Property .\n:p |" );
      assertThat( snippetNamed( request, "SAMM description" ).getTextEdit().getLeft().getRange().getStart() )
            .isEqualTo( request.position );
   }

   @Test
   void recognizesPropertyTypeAcrossStatementsAndMultipleTypeObjects() {
      final Request request = request( HEADER + ":p a :Other, samm:Property .\n:p samm:des| ." );
      assertValid( apply( request, snippetNamed( request, "SAMM description" ) ) );
   }

   @Test
   void usesActualTypedAliasWhenMultipleAliasesResolveToSamm() {
      final Request request = request( HEADER + "@prefix meta: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .\n"
            + ":p a samm:Property ; meta:des|" );
      assertThat( snippetNamed( request, "SAMM description" ).getTextEdit().getLeft().getNewText() ).startsWith( "meta:description" );
   }

   @Test
   void supportsPrefixHeaderSnippetOnEmptyFileAndPrefixKeyword() {
      final Request emptyFile = request( "|" );
      final CompletionItem item = snippetNamed( emptyFile, "SAMM Prefix Header" );
      final String expanded = apply( emptyFile, item ) + ":p a samm:Property ; samm:characteristic :C .";
      assertValid( expanded );

      final Request typedAtPrefix = request( "@pre|" );
      final CompletionItem fromKeyword = snippetNamed( typedAtPrefix, "SAMM Prefix Header" );
      assertValid( apply( typedAtPrefix, fromKeyword ) + ":p a samm:Property ; samm:characteristic :C ." );
   }

   @Test
   void currentEditDoesNotSuppressFullyTypedPredicate() {
      final Request request = request( HEADER + ":p a samm:Property ; samm:description| ." );
      assertValid( apply( request, snippetNamed( request, "SAMM description" ) ) );
   }

   @Test
   void supportsBlankNodeSubjectsAndMultipleSeePredicates() {
      final Request anonBnode = request( HEADER + "[ a samm:Property ; samm:des| ] ." );
      assertValid( apply( anonBnode, snippetNamed( anonBnode, "SAMM description" ) ) );

      final Request labeledBnode = request( HEADER + "_:b0 a samm:Property ; samm:des| ." );
      assertValid( apply( labeledBnode, snippetNamed( labeledBnode, "SAMM description" ) ) );

      final Request aspectSubject = request( HEADER + ":A a samm:Aspect ; samm:des| ." );
      assertValid( apply( aspectSubject, snippetNamed( aspectSubject, "SAMM description" ) ) );
      assertThat( snippets.complete( aspectSubject.document, aspectSubject.position ) )
            .noneMatch( item -> item.getLabel().equals( "SAMM exampleValue" ) );

      final Request multipleSee = request(
            HEADER + ":p a samm:Property ; samm:see <https://example.com/1> ; samm:se| ." );
      assertValid( apply( multipleSee, snippetNamed( multipleSee, "SAMM see" ) ) );
   }

   @Test
   void supportsAnonymousBracketTriggerAcrossAllFourObjectContexts() {
      // Context 1: samm:properties list on Aspect (with auto-closed [])
      final Request aspectProps = request( FULL_HEADER + ":A a samm:Aspect ;\n    samm:properties ( [|] ) ." );
      final List<CompletionItem> aspectItems = snippets.complete( aspectProps.document, aspectProps.position );
      assertThat( aspectItems ).extracting( CompletionItem::getLabel )
            .containsExactlyInAnyOrder( "SAMM Optional Property", "SAMM Property with Payload Name" );
      for ( final CompletionItem item : aspectItems ) {
         assertValid( apply( aspectProps, item ) );
      }

      // Context 1 & 4: samm:properties list on Entity with samm:extends
      final Request entityProps = request(
            FULL_HEADER + ":E a samm:Entity ;\n    samm:extends :Base ;\n    samm:properties ( [|] ) ." );
      final List<CompletionItem> entityItems = snippets.complete( entityProps.document, entityProps.position );
      assertThat( entityItems ).extracting( CompletionItem::getLabel )
            .containsExactlyInAnyOrder(
                  "SAMM Optional Property",
                  "SAMM Property with Payload Name",
                  "SAMM Property Not In Payload",
                  "SAMM Extended AbstractProperty" );
      for ( final CompletionItem item : entityItems ) {
         assertValid( apply( entityProps, item ) );
      }

      // Context 2: samm-c:constraint [|] on Trait
      final Request constraintCtx = request(
            FULL_HEADER + ":T a samm-c:Trait ;\n    samm-c:baseCharacteristic :C ;\n    samm-c:constraint [|] ." );
      final List<CompletionItem> constraintItems = snippets.complete( constraintCtx.document, constraintCtx.position );
      assertThat( constraintItems ).hasSize( 7 );
      for ( final CompletionItem item : constraintItems ) {
         assertValid( apply( constraintCtx, item ) );
      }

      // Inner filtering after [: [Len|]
      final Request filteredConstraint = request(
            FULL_HEADER + ":T a samm-c:Trait ;\n    samm-c:baseCharacteristic :C ;\n    samm-c:constraint [Len|] ." );
      assertThat( snippets.complete( filteredConstraint.document, filteredConstraint.position ) )
            .extracting( CompletionItem::getLabel )
            .containsExactly( "SAMM Anonymous LengthConstraint" );

      // Context 3: samm:characteristic [|] on Property
      final Request charCtx = request( FULL_HEADER + ":p a samm:Property ;\n    samm:characteristic [|] ." );
      final List<CompletionItem> charItems = snippets.complete( charCtx.document, charCtx.position );
      assertThat( charItems ).hasSize( 16 );
      for ( final CompletionItem item : charItems ) {
         assertValid( apply( charCtx, item ) );
      }
   }

   static Stream<SnippetDefinition> allTopLevelSnippets() {
      return new SammSnippetCatalog().allSnippets().stream()
            .filter( snippet -> snippet.targetPosition() == SammCompletionContext.TargetPosition.TOP_LEVEL_SUBJECT );
   }

   @ParameterizedTest
   @MethodSource( "allTopLevelSnippets" )
   void everyTopLevelSnippetExpandsToValidTurtleWithCustomAliases( final SnippetDefinition definition ) {
      final String customHeader = """
         @prefix model: <urn:samm:example.snippets:1.0.0#> .
         @prefix meta: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix char: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix u: <urn:samm:org.eclipse.esmf.samm:unit:2.2.0#> .
         @prefix xs: <http://www.w3.org/2001/XMLSchema#> .
         """;
      final Request request = request( customHeader + "\n|" );
      final CompletionItem item = snippetNamed( request, definition.label() );
      final String renderedBody = item.getTextEdit().getLeft().getNewText();
      assertThat( renderedBody )
            .doesNotContain( " :" )
            .doesNotContain( "( :" )
            .doesNotContain( "xsd:" )
            .doesNotContain( "unit:" )
            .doesNotContain( "samm:" )
            .doesNotContain( "samm-c:" );
      assertValid( apply( request, item ) );
   }

   /**
    * An empty tab stop between whitespace, such as in {@code values ( ${5} )}: the user types a whole
    * Turtle value here.
    */
   private static final Pattern VALUE_TAB_STOP = Pattern.compile( "(?<=\\s)\\$\\{\\d+}(?=\\s|$)" );
   /** A rendered choice group, for example {@code ${4|xsd:string,xsd:integer|}}. */
   private static final Pattern CHOICE_GROUP = Pattern.compile( "\\$\\{\\d+\\|([^}]*)\\|}" );
   /**
    * The start of a choice group in an unrendered snippet body, whose choices may contain
    * {{placeholders}}.
    */
   private static final Pattern CHOICE_GROUP_START = Pattern.compile( "\\$\\{\\d+\\|" );

   static Stream<SnippetDefinition> snippetsWithChoices() {
      return new SammSnippetCatalog().allSnippets().stream()
            .filter( snippet -> CHOICE_GROUP_START.matcher( String.join( "\n", snippet.body() ) ).find() );
   }

   @ParameterizedTest
   @MethodSource( "snippetsWithChoices" )
   void everyChoiceOfEverySnippetExpandsToValidTurtleWithoutRdfPrefix( final SnippetDefinition definition ) {
      // FULL_HEADER declares no rdf: prefix
      final Request request = definition.targetPosition() == SammCompletionContext.TargetPosition.TOP_LEVEL_SUBJECT
            ? request( FULL_HEADER + "\n|" )
            : request( FULL_HEADER + ":p a samm:Property ;\n    samm:characteristic [|] ." );
      final CompletionItem item = snippetNamed( request, definition.label() );
      final String text = item.getTextEdit().getLeft().getNewText();
      assertThat( text ).doesNotContain( "rdf:" );

      final Matcher groups = CHOICE_GROUP.matcher( text );
      int groupIndex = 0;
      int expandedChoices = 0;
      while ( groups.find() ) {
         for ( final String choice : groups.group( 1 ).split( "," ) ) {
            assertValid( applyText( request, item, expandChoice( text, groupIndex, choice ) ) );
            expandedChoices++;
         }
         groupIndex++;
      }
      assertThat( expandedChoices ).isGreaterThan( 0 );
   }

   @Test
   void langStringChoiceUsesFullIriWhenRdfPrefixIsNotDeclared() {
      final Request request = request( FULL_HEADER + "\n|" );
      final CompletionItem item = snippetNamed( request, "SAMM Characteristic" );
      final String text = item.getTextEdit().getLeft().getNewText();

      assertThat( text ).contains( "<http://www.w3.org/1999/02/22-rdf-syntax-ns#langString>" ).doesNotContain( "rdf:langString" );
   }

   @Test
   void langStringChoiceUsesDeclaredRdfPrefix() {
      final String header = FULL_HEADER + "@prefix r: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
      final Request request = request( header + "\n|" );
      for ( final String label : List.of( "SAMM Characteristic", "SAMM Enumeration", "SAMM State" ) ) {
         final String text = snippetNamed( request, label ).getTextEdit().getLeft().getNewText();

         assertThat( text ).contains( ",r:langString," ).doesNotContain( "rdf:langString" ).doesNotContain( "<http://www.w3.org" );
      }
   }

   static Stream<String> enumerationAndStateSnippets() {
      return Stream.of( "SAMM Enumeration", "SAMM State", "SAMM Anonymous Enumeration", "SAMM Anonymous State" );
   }

   @ParameterizedTest
   @MethodSource( "enumerationAndStateSnippets" )
   void enumerationAndStateSnippetsInsertNoTypedValues( final String label ) {
      final String text = snippetNamed( requestFor( label ), label ).getTextEdit().getLeft().getNewText();

      // The values (and the default value of a State) depend on the data type. So they are left empty for
      // the user.
      assertThat( text ).doesNotContain( "value1", "state1", "state2" );
      assertThat( VALUE_TAB_STOP.matcher( text ).results().count() ).isEqualTo( label.contains( "State" ) ? 2 : 1 );
   }

   static Stream<String> dataTypeSnippets() {
      return Stream.of( "Characteristic", "Enumeration", "State", "Measurement", "Quantifiable", "Duration" )
            .flatMap( name -> Stream.of( "SAMM " + name, "SAMM Anonymous " + name ) );
   }

   @ParameterizedTest
   @MethodSource( "dataTypeSnippets" )
   void dataTypeChoicesAreGeneratedFromTheSammDataTypes( final String label ) {
      final String text = snippetNamed( requestFor( label ), label ).getTextEdit().getLeft().getNewText();
      final Matcher dataTypeChoices = CHOICE_GROUP.matcher( text );
      assertThat( dataTypeChoices.find() ).isTrue();

      final List<String> offered = Stream.of( dataTypeChoices.group( 1 ).split( "," ) ).toList();
      final List<SammType<?>> expected = label.endsWith( "Measurement" ) || label.endsWith( "Quantifiable" )
            ? SammSnippetCatalog.NUMERIC_DATA_TYPES
            : label.endsWith( "Duration" ) ? SammSnippetCatalog.DURATION_DATA_TYPES : SammXsdType.ALL_TYPES;
      assertThat( offered ).containsExactlyElementsOf( expected.stream().map( type -> prefixed( type.getURI() ) ).toList() );
   }

   @Test
   void numericAndDurationDataTypeListsMatchTheSammSpecification() {
      final List<String> numeric = List.of( "decimal", "integer", "double", "float", "byte", "short", "int", "long", "unsignedByte",
            "unsignedShort", "unsignedInt", "unsignedLong", "positiveInteger", "nonNegativeInteger", "negativeInteger",
            "nonPositiveInteger" );
      assertThat( SammSnippetCatalog.NUMERIC_DATA_TYPES ).extracting( type -> prefixed( type.getURI() ) )
            .containsExactlyElementsOf( numeric.stream().map( name -> "xsd:" + name ).toList() );
      assertThat( SammSnippetCatalog.DURATION_DATA_TYPES ).extracting( type -> prefixed( type.getURI() ) )
            .containsExactlyElementsOf( Stream.concat( Stream.of( "duration", "yearMonthDuration", "dayTimeDuration" ), numeric.stream() )
                  .map( name -> "xsd:" + name ).toList() );
      assertThat( SammXsdType.ALL_TYPES ).hasSize( 35 );
   }

   /** The data type URI written with the prefixes of {@link #FULL_HEADER}, as the snippets do. */
   private static String prefixed( final String uri ) {
      if ( uri.startsWith( XSD.getURI() ) ) {
         return "xsd:" + uri.substring( XSD.getURI().length() );
      }
      if ( uri.equals( RDF.langString.getURI() ) ) {
         return "<" + uri + ">";
      }
      return "samm:" + uri.substring( uri.indexOf( '#' ) + 1 );
   }

   @ParameterizedTest
   @MethodSource( "dataTypeSnippets" )
   void dataTypeSnippetsAreValidForEveryDataTypeChoice( final String label ) {
      final Request request = requestFor( label );
      final CompletionItem item = snippetNamed( request, label );
      final String text = item.getTextEdit().getLeft().getNewText();
      final Matcher dataTypeChoices = CHOICE_GROUP.matcher( text );
      assertThat( dataTypeChoices.find() ).isTrue();

      final AspectModelValidator validator = new AspectModelValidator();
      for ( final String choice : dataTypeChoices.group( 1 ).split( "," ) ) {
         // Like a user, fill the empty values and the empty preferred name and description
         final String filled = expandChoice( text, 0, choice, sampleLiteral( choice ) ).replace( "\"\"@en", "\"text\"@en" );
         final String source = applyText( request, item, filled );
         final List<String> violations = validator
               .validateModel( () -> new AspectModelLoader().load( source, URI.create( "file:///test.ttl" ) ) )
               .violations().stream().map( Violation::message ).toList();
         assertThat( violations ).as( "%s with data type %s%n%s", label, choice, source ).isEmpty();
      }
   }

   @Test
   void suppressesSnippetsWhenRequiredNamespacesAreMissingOrVersionMismatched() {
      // HEADER lacks xsd: and unit:, so Measurement and Characteristic are suppressed
      final Request withoutXsdOrUnit = request( HEADER + "\n|" );
      assertThat( snippets.complete( withoutXsdOrUnit.document, withoutXsdOrUnit.position ) )
            .extracting( CompletionItem::getLabel )
            .doesNotContain( "SAMM Characteristic", "SAMM Measurement", "SAMM Enumeration" );

      // Mismatched samm-c version (2.1.0 vs samm 2.2.0) suppresses samm-c snippets
      final String mismatchedCharVersion = FULL_HEADER.replace(
            "characteristic:2.2.0#", "characteristic:2.1.0#" );
      final Request mismatchedRequest = request( mismatchedCharVersion + "\n|" );
      assertThat( snippets.complete( mismatchedRequest.document, mismatchedRequest.position ) )
            .extracting( CompletionItem::getLabel )
            .contains( "SAMM Aspect", "SAMM Property", "SAMM Characteristic" )
            .doesNotContain( "SAMM SingleEntity", "SAMM Measurement", "SAMM Enumeration" );
   }

   static Stream<String> nonSnippetContexts() {
      return Stream.of(
            HEADER + "# temperature.prop|",
            HEADER + "# comment 😀 |\n",
            HEADER + ":p a samm:Property ; samm:preferredName \"temperature.prop|\"@en .",
            HEADER + ":p a samm:Property ; samm:preferredName \"unterminated ; samm:des|",
            HEADER + ":p a samm:Property ; samm:preferredName '''multi\nline ; samm:des|''' .",
            HEADER + ":p a samm:Property ; samm:see <urn:example:samm:des|> .",
            HEADER + ":p a samm:Property ; samm:see <urn:example:unterminated|",
            HEADER + ":p a samm:Property ; samm:characteristic samm:des|",
            HEADER + ":p a :CustomNonSammType ; samm:des|",
            HEADER + ":p a samm:Property ; samm:characteristic [ samm:des| ] .",
            HEADER + ":p a samm:Property ; samm:see ( samm:des| ) .",
            HEADER + ":p a samm:Property ; samm:characteristic [\n temperature.prop|",
            HEADER + ":p a samm:Property ; samm:characteristic |",
            HEADER + "@prefix model: <urn:example:|> .",
            HEADER + "@prefix model: |",
            HEADER + "@base |",
            HEADER + "temperature.prop| otherText",
            HEADER + "temperature.prop|erty",
            HEADER + "temperature.|",
            HEADER + "temperature.\n|",
            HEADER + ":temperature.prop|",
            HEADER + "temperature.other|",
            HEADER + ":p a samm:Property ; other:des|",
            HEADER + ":p a samm:Property ; samm:characteristic [ ) ; samm:des|",
            HEADER + ":p a samm:Property ; samm:characteristic invalid ; samm:des|",
            HEADER + ":p a samm:Property ; samm:characteristic other:Unknown ; samm:des|",
            HEADER + ":A a samm:Aspect ; samm:properties ( ( [|] ) ) .",
            HEADER + ":p a samm:Property ; samm:des| :C .",
            HEADER.replace( "2.2.0", "99.0.0" ) + "|",
            HEADER.replace( "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#", "urn:example:not-samm#" ) + "|",
            "@prefix : <urn:samm:example.snippets:1.0.0#> .\n|",
            "@prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .\n|",
            HEADER + "@prefix other: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .\n|",
            HEADER + "@prefix samm: <urn:example:redefined#> .\n|" );
   }

   @ParameterizedTest
   @MethodSource( "nonSnippetContexts" )
   void returnsNoSnippetsOutsideEligibleContexts( final String source ) {
      final Request request = request( source );
      assertThat( snippets.complete( request.document, request.position ) ).isEmpty();
   }

   static Stream<String> duplicateDescriptionContexts() {
      return Stream.of(
            HEADER + ":p a samm:Property ; samm:description \"existing\"@en ; samm:des|",
            HEADER + ":p a samm:Property ; samm:des| ; samm:description \"later\"@en .",
            HEADER + ":p a samm:Property ; samm:des| .\n:p samm:description \"other statement\"@en .",
            HEADER + ":p a samm:Property ; samm:des| .\n@prefix later: <urn:samm:example.snippets:1.0.0#> .\n"
                  + "later:p samm:description \"later alias\"@en .",
            HEADER + "@prefix model: <urn:samm:example.snippets:1.0.0#> .\n"
                  + ":p a samm:Property ; samm:des| .\nmodel:p samm:description \"alias\"@en .",
            HEADER + ":p a samm:Property ; samm:des| .\n:p <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#description>"
                  + " \"full IRI\"@en ." );
   }

   @ParameterizedTest
   @MethodSource( "duplicateDescriptionContexts" )
   void suppressesDescriptionAlreadyDeclaredForTheSameSubject( final String source ) {
      final Request request = request( source );
      assertThat( snippets.complete( request.document, request.position ) ).isEmpty();
   }

   @Test
   void existingDescriptionKeepsOtherEligiblePredicates() {
      final Request request = request( HEADER + ":p a samm:Property ; samm:description \"existing\"@en ; | ." );
      final List<CompletionItem> items = snippets.complete( request.document, request.position );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .containsExactly( "SAMM preferredName", "SAMM see", "SAMM exampleValue" );
      for ( final CompletionItem item : items ) {
         assertValid( apply( request, item ) );
      }
   }

   @Test
   void supportsSparqlPrefixDeclarationsAndFullIriPropertyTypes() {
      final Request request = request( """
         PREFIX meta: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#>
         <urn:example:p> a <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#Property> ;
             meta:des|""" );
      assertThat( snippetNamed( request, "SAMM description" ).getTextEdit().getLeft().getNewText() ).startsWith( "meta:description" );
   }

   @Test
   void ordersSnippetsFromMostMatchedToLessMatchedCaseInsensitively() {
      // 1. Top-level typed "prop": "SAMM Property" (prefix match) before "SAMM AbstractProperty"
      // (CamelCase match),
      // other non-matching snippets excluded, and sortText assigned in order
      final Request topLevelProp = request( HEADER + "\nprop|" );
      final List<CompletionItem> propItems = snippets.complete( topLevelProp.document, topLevelProp.position );
      assertThat( propItems ).extracting( CompletionItem::getLabel )
            .containsExactly( "SAMM Property", "SAMM AbstractProperty" );
      assertThat( propItems.get( 0 ).getSortText() ).isEqualTo( "0000" );
      assertThat( propItems.get( 1 ).getSortText() ).isEqualTo( "0001" );

      // Case-insensitivity: "PROP" yields identical result
      final Request topLevelUpperProp = request( HEADER + "\nPROP|" );
      assertThat( snippets.complete( topLevelUpperProp.document, topLevelUpperProp.position ) )
            .extracting( CompletionItem::getLabel )
            .containsExactly( "SAMM Property", "SAMM AbstractProperty" );

      // 2. Top-level typed "entity": "SAMM Entity" (exact) > "SAMM SingleEntity" (shorter) > "SAMM
      // AbstractEntity" (longer)
      final Request topLevelEntity = request( FULL_HEADER + "\nentity|" );
      final List<CompletionItem> entityItems = snippets.complete( topLevelEntity.document, topLevelEntity.position );
      assertThat( entityItems ).extracting( CompletionItem::getLabel )
            .containsExactly( "SAMM Entity", "SAMM SingleEntity", "SAMM AbstractEntity" );
      assertThat( entityItems ).extracting( CompletionItem::getSortText )
            .containsExactly( "0000", "0001", "0002" );

      // 3. Predicate with prefix typed case-insensitively: "samm:DES" matches "SAMM description"
      final Request predicateDes = request( HEADER + ":p a samm:Property ;\n    samm:DES|" );
      final List<CompletionItem> desItems = snippets.complete( predicateDes.document, predicateDes.position );
      assertThat( desItems ).extracting( CompletionItem::getLabel )
            .contains( "SAMM description" );

      // 4. Predicate typed "samm:p" on Aspect: prefix match "preferredName" (Tier 1) comes before
      // substring match "description" (Tier 3)
      final Request predicateP = request( FULL_HEADER + ":A a samm:Aspect ;\n    samm:p|" );
      final List<CompletionItem> pItems = snippets.complete( predicateP.document, predicateP.position );
      assertThat( pItems ).extracting( CompletionItem::getLabel )
            .containsExactly( "SAMM preferredName", "SAMM description" );
      assertThat( pItems.get( 0 ).getSortText() ).isEqualTo( "0000" );
      assertThat( pItems.get( 1 ).getSortText() ).isEqualTo( "0001" );

      // 5. Predicate matching via CamelCase word start: "samm:name" matches preferredName
      final Request predicateName = request( FULL_HEADER + ":A a samm:Aspect ;\n    samm:name|" );
      final List<CompletionItem> nameItems = snippets.complete( predicateName.document, predicateName.position );
      assertThat( nameItems ).extracting( CompletionItem::getLabel )
            .contains( "SAMM preferredName" );

      // 6. Anonymous object bracket trigger case-insensitive: "[len" matches LengthConstraint
      final Request anonLen = request(
            FULL_HEADER + ":T a samm-c:Trait ;\n    samm-c:baseCharacteristic :C ;\n    samm-c:constraint [len|] ." );
      final List<CompletionItem> anonItems = snippets.complete( anonLen.document, anonLen.position );
      assertThat( anonItems ).extracting( CompletionItem::getLabel )
            .containsExactly( "SAMM Anonymous LengthConstraint" );
   }

   @Test
   void seeSnippetUsesVendorNeutralExampleUri() {
      final Request request = request( HEADER + ":p a samm:Property ;\n    samm:se| .\n" );
      final CompletionItem item = snippetNamed( request, "SAMM see" );
      assertThat( item.getTextEdit().getLeft().getNewText() ).contains( "https://example.com/term/" )
            .doesNotContain( "bosch.com" );
      assertValid( apply( request, item ) );
   }

   @Test
   void filterTextIncludesKeywordsAndTypedPrefixForEditorFiltering() {
      final Request request = request( HEADER + ":p a samm:Property ;\n    samm:des|\n" );
      final CompletionItem item = snippetNamed( request, "SAMM description" );
      assertThat( item.getFilterText() ).contains( "samm:des" )
            .contains( "samm:description" )
            .contains( "description" );
   }

   @Test
   void positionAtHandlesBothLfAndCrlfLineEndings() {
      final String sourceLf = "abc\ndef\nxyz";
      assertThat( SammSuggestionContextDetector.positionAt( sourceLf, 0 ) ).isEqualTo( new Position( 0, 0 ) );
      assertThat( SammSuggestionContextDetector.positionAt( sourceLf, 4 ) ).isEqualTo( new Position( 1, 0 ) );
      assertThat( SammSuggestionContextDetector.positionAt( sourceLf, 6 ) ).isEqualTo( new Position( 1, 2 ) );

      final String sourceCrlf = "abc\r\ndef\r\nxyz";
      assertThat( SammSuggestionContextDetector.positionAt( sourceCrlf, 0 ) ).isEqualTo( new Position( 0, 0 ) );
      assertThat( SammSuggestionContextDetector.positionAt( sourceCrlf, 5 ) ).isEqualTo( new Position( 1, 0 ) );
      assertThat( SammSuggestionContextDetector.positionAt( sourceCrlf, 7 ) ).isEqualTo( new Position( 1, 2 ) );
   }

   @Test
   void anonymousSnippetRenderingOmitsServerBaseIndentation() {
      final Request request = request( FULL_HEADER + ":T a samm-c:Trait ;\n   samm-c:constraint [Len|] ." );
      final CompletionItem item = snippetNamed( request, "SAMM Anonymous LengthConstraint" );
      assertThat( item.getTextEdit().getLeft().getNewText() ).isEqualTo( """
         [
            a samm-c:LengthConstraint ;
            samm-c:minValue "${1:2}"^^xsd:nonNegativeInteger ;
            samm-c:maxValue "${2:2}"^^xsd:nonNegativeInteger
         ]$0""" );
      assertValid( apply( request, item ) );
   }

   private Request requestFor( final String label ) {
      return label.contains( "Anonymous" )
            ? request( FULL_HEADER + ":p a samm:Property ;\n    samm:characteristic [|] ." )
            : request( FULL_HEADER + "\n|" );
   }

   /**
    * @return a Turtle literal of the given data type, as a user would type it as a value of an
    *         Enumeration or State
    */
   private static String sampleLiteral( final String dataTypeChoice ) {
      final String name = dataTypeChoice.replaceAll( ".*[#:]", "" ).replace( ">", "" );
      return switch ( name ) {
         case "string" -> "\"a\"";
         case "langString" -> "\"a\"@en";
         case "curie" -> "\"samm:Aspect\"^^samm:curie";
         case "boolean" -> "true";
         case "integer", "int", "long", "short", "byte", "unsignedLong", "unsignedInt", "unsignedShort", "unsignedByte",
               "positiveInteger", "nonNegativeInteger" ->
            "\"1\"^^xsd:" + name;
         case "negativeInteger", "nonPositiveInteger" -> "\"-1\"^^xsd:" + name;
         case "decimal", "float", "double" -> "\"1.5\"^^xsd:" + name;
         case "dateTime" -> "\"2020-01-01T00:00:00\"^^xsd:dateTime";
         case "dateTimeStamp" -> "\"2020-01-01T00:00:00Z\"^^xsd:dateTimeStamp";
         case "date" -> "\"2020-01-01\"^^xsd:date";
         case "time" -> "\"12:00:00\"^^xsd:time";
         case "gYear" -> "\"2020\"^^xsd:gYear";
         case "gMonth" -> "\"--01\"^^xsd:gMonth";
         case "gYearMonth" -> "\"2020-01\"^^xsd:gYearMonth";
         case "gDay" -> "\"---01\"^^xsd:gDay";
         case "gMonthDay" -> "\"--01-01\"^^xsd:gMonthDay";
         case "duration" -> "\"P1D\"^^xsd:duration";
         case "yearMonthDuration" -> "\"P1Y\"^^xsd:yearMonthDuration";
         case "dayTimeDuration" -> "\"P1D\"^^xsd:dayTimeDuration";
         case "hexBinary" -> "\"0FB7\"^^xsd:hexBinary";
         case "base64Binary" -> "\"AQID\"^^xsd:base64Binary";
         case "anyURI" -> "\"http://example.com\"^^xsd:anyURI";
         default -> throw new AssertionError( "No sample literal for data type " + dataTypeChoice );
      };
   }

   private String applyText( final Request request, final CompletionItem item, final String plainText ) {
      final TextEdit edit = item.getTextEdit().getLeft();
      final int start =
            request.document.sourceDocument().index( edit.getRange().getStart().getLine(), edit.getRange().getStart().getCharacter() );
      final int end =
            request.document.sourceDocument().index( edit.getRange().getEnd().getLine(), edit.getRange().getEnd().getCharacter() );
      return request.source.substring( 0, start ) + plainText + request.source.substring( end );
   }

   /**
    * Replaces choice group number {@code selectedGroup} with {@code choice}, other groups with their
    * first choice.
    */
   private String expandChoice( final String snippet, final int selectedGroup, final String choice ) {
      return expandChoice( snippet, selectedGroup, choice, "\"x\"" );
   }

   private String expandChoice( final String snippet, final int selectedGroup, final String choice, final String valueLiteral ) {
      final Matcher matcher = CHOICE_GROUP.matcher( snippet );
      final StringBuilder result = new StringBuilder();
      int group = 0;
      while ( matcher.find() ) {
         final String replacement = group == selectedGroup ? choice : matcher.group( 1 ).split( "," )[0];
         matcher.appendReplacement( result, Matcher.quoteReplacement( replacement ) );
         group++;
      }
      matcher.appendTail( result );
      return plainText( result.toString(), valueLiteral );
   }

   /**
    * Turns snippet syntax into plain text. An empty tab stop that stands for a whole Turtle value,
    * such as in
    * {@code values ( ${5} )}, is filled with {@code valueLiteral}, like a user who types a value.
    * Other empty tab stops stay empty.
    */
   private static String plainText( final String snippet, final String valueLiteral ) {
      return VALUE_TAB_STOP.matcher( snippet.replaceAll( "\\$\\{\\d+:([^}]+)}", "$1" ) )
            .replaceAll( Matcher.quoteReplacement( valueLiteral ) )
            .replaceAll( "\\$\\{\\d+}", "" )
            .replace( "$0", "" );
   }

   private CompletionItem snippetNamed( final Request request, final String label ) {
      final List<CompletionItem> result = snippets.complete( request.document, request.position );
      return result.stream()
            .filter( item -> item.getLabel().equals( label ) )
            .findFirst()
            .orElseThrow( () -> new AssertionError( "Snippet not found: " + label + " in " + result ) );
   }

   private Request request( final String markedSource ) {
      final int cursor = markedSource.indexOf( '|' );
      assertThat( cursor ).isGreaterThanOrEqualTo( 0 );
      final String source = markedSource.substring( 0, cursor ) + markedSource.substring( cursor + 1 );
      final Position position = SammSuggestionContextDetector.positionAt( source, cursor );
      return new Request( parser.apply( new Document( "file:///test.ttl", source ) ), position, source );
   }

   private String apply( final Request request, final CompletionItem item ) {
      final TextEdit edit = item.getTextEdit().getLeft();
      final int start =
            request.document.sourceDocument().index( edit.getRange().getStart().getLine(), edit.getRange().getStart().getCharacter() );
      final int end =
            request.document.sourceDocument().index( edit.getRange().getEnd().getLine(), edit.getRange().getEnd().getCharacter() );
      final String plainText = plainText( edit.getNewText().replaceAll( "\\$\\{\\d+\\|([^,|]+)[^}]*\\|}", "$1" ), "\"x\"" );
      return request.source.substring( 0, start ) + plainText + request.source.substring( end );
   }

   private void assertValid( final String source ) {
      final Model model = TurtleLoader.loadTurtle( source, URI.create( "file:///test.ttl" ) ).get();
      try {
         assertThat( model.isEmpty() ).isFalse();
      } finally {
         model.close();
      }
   }

   private record Request(
         ParsedDocument document, Position position, String source
   ) {}
}
