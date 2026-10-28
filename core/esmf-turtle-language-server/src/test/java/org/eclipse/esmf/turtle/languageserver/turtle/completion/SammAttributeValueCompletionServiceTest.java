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

import java.util.List;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleCompletionService;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SammAttributeValueCompletionServiceTest {
   private static final String FULL_HEADER = """
      @prefix : <urn:samm:example.test:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
      @prefix samm-e: <urn:samm:org.eclipse.esmf.samm:entity:2.2.0#> .
      @prefix unit: <urn:samm:org.eclipse.esmf.samm:unit:2.2.0#> .
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
      """;

   private final TreeSitterTurtleParserService parser = new TreeSitterTurtleParserService();
   private final SammAttributeValueCompletionService attributeService = new SammAttributeValueCompletionService();
   private final TurtleCompletionService turtleCompletionService = new TurtleCompletionService();

   @AfterEach
   void close() {
      parser.close();
   }

   @Test
   void suggestsLowerBoundDefinitionsAtEmptyObject() {
      final Request req = request( FULL_HEADER + """
         :Range a samm-c:RangeConstraint ;
             samm-c:lowerBoundDefinition |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .containsExactly( "samm-c:AT_LEAST", "samm-c:GREATER_THAN" );
      assertThat( items ).extracting( CompletionItem::getKind )
            .containsOnly( CompletionItemKind.EnumMember );
      assertThat( items.get( 0 ).getTextEdit().getLeft().getNewText() ).isEqualTo( "samm-c:AT_LEAST" );
      assertThat( items.get( 0 ).getTextEdit().getLeft().getRange() )
            .isEqualTo( new Range( req.position, req.position ) );
   }

   @Test
   void suggestsLowerBoundDefinitionsWhenTypingPrefixOrIdentifier() {
      final Request req = request( FULL_HEADER + """
         :Range a samm-c:RangeConstraint ;
             samm-c:lowerBoundDefinition samm-c:AT|
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .containsExactly( "samm-c:AT_LEAST", "samm-c:GREATER_THAN" );

      final Range editRange = items.get( 0 ).getTextEdit().getLeft().getRange();
      final String replaced = substring( req.source, editRange );
      assertThat( replaced ).isEqualTo( "samm-c:AT" );
   }

   @Test
   void respectsCustomCharacteristicPrefixForBoundDefinitions() {
      final String header = """
         @prefix : <urn:samm:example.test:1.0.0#> .
         @prefix meta: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         """;
      final Request req = request( header + """
         :Range a c:RangeConstraint ;
             c:upperBoundDefinition |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .containsExactly( "c:AT_MOST", "c:LESS_THAN" );
   }

   @Test
   void suggestsUpperBoundDefinitions() {
      final Request req = request( FULL_HEADER + """
         :Range a samm-c:RangeConstraint ;
             samm-c:upperBoundDefinition |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .containsExactly( "samm-c:AT_MOST", "samm-c:LESS_THAN" );
   }

   @Test
   void suggestsEncodingsForEncodingConstraint() {
      final Request req = request( FULL_HEADER + """
         :Enc a samm-c:EncodingConstraint ;
             samm:value |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .containsExactly(
                  "samm:UTF-8", "samm:US-ASCII", "samm:ISO-8859-1",
                  "samm:UTF-16", "samm:UTF-16BE", "samm:UTF-16LE" );
      assertThat( items ).extracting( CompletionItem::getKind )
            .containsOnly( CompletionItemKind.EnumMember );
   }

   @Test
   void suggestsEncodingsInsideAnonymousBlankNode() {
      final Request req = request( FULL_HEADER + """
         :Trait a samm-c:Trait ;
             samm-c:baseCharacteristic :C ;
             samm-c:constraint [
                 a samm-c:EncodingConstraint ;
                 samm:value |
             ] .
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "samm:UTF-8", "samm:US-ASCII" );
   }

   @Test
   void doesNotSuggestEncodingsForOtherSubjectsWithValue() {
      final Request req = request( FULL_HEADER + """
         :OtherEntity a samm:Entity ;
             samm:value |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isEmpty();
   }

   @Test
   void suggestsBooleansForOptionalAndNotInPayload() {
      final Request reqOptional = request( FULL_HEADER + """
         :Aspect a samm:Aspect ;
             samm:properties ( [
                 samm:property :p ;
                 samm:optional |
             ] ) .
         """ );

      final List<CompletionItem> optItems = attributeService.complete( reqOptional.document, reqOptional.position );
      assertThat( optItems ).extracting( CompletionItem::getLabel )
            .containsExactly( "true", "false" );
      assertThat( optItems ).extracting( CompletionItem::getKind )
            .containsOnly( CompletionItemKind.Keyword );

      final Request reqNotInPayload = request( FULL_HEADER + """
         :Aspect a samm:Aspect ;
             samm:properties ( [
                 samm:property :p ;
                 samm:notInPayload |
             ] ) .
         """ );

      final List<CompletionItem> nipItems = attributeService.complete( reqNotInPayload.document, reqNotInPayload.position );
      assertThat( nipItems ).extracting( CompletionItem::getLabel )
            .containsExactly( "true", "false" );
   }

   @Test
   void suggestsLanguageCodes() {
      final Request req = request( FULL_HEADER + """
         :Lang a samm-c:LanguageConstraint ;
             samm-c:languageCode |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "\"en\"", "\"de\"", "\"fr\"", "\"es\"", "\"ja\"", "\"zh\"" );
      assertThat( items ).extracting( CompletionItem::getKind )
            .containsOnly( CompletionItemKind.Value );

      // Check priority sort ordering: "en" sortText should start with "01_"
      final CompletionItem enItem = items.stream().filter( i -> "\"en\"".equals( i.getLabel() ) ).findFirst().orElseThrow();
      assertThat( enItem.getSortText() ).startsWith( "01_" );
   }

   @Test
   void suggestsLanguageCodesWhenTypingQuotedText() {
      final Request req = request( FULL_HEADER + """
         :Lang a samm-c:LanguageConstraint ;
             samm-c:languageCode "e|
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      final CompletionItem enItem = items.stream().filter( i -> "\"en\"".equals( i.getLabel() ) ).findFirst().orElseThrow();
      final Range editRange = enItem.getTextEdit().getLeft().getRange();
      final String replaced = substring( req.source, editRange );
      assertThat( replaced ).isEqualTo( "\"e" );
   }

   @Test
   void suggestsLocaleCodes() {
      final Request req = request( FULL_HEADER + """
         :Loc a samm-c:LocaleConstraint ;
             samm-c:localeCode |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "\"en-US\"", "\"en-GB\"", "\"de-DE\"", "\"fr-FR\"" );
      assertThat( items ).extracting( CompletionItem::getKind )
            .containsOnly( CompletionItemKind.Value );
   }

   @Test
   void suggestsUnitsForReferenceUnitAndUnit() {
      final Request reqRefUnit = request( FULL_HEADER + """
         :Length a samm:QuantityKind ;
             samm:referenceUnit |
         """ );

      final List<CompletionItem> refItems = attributeService.complete( reqRefUnit.document, reqRefUnit.position );
      assertThat( refItems ).isNotEmpty();
      assertThat( refItems ).extracting( CompletionItem::getLabel )
            .contains( "unit:metre", "unit:secondUnitOfTime", "unit:kilometre" );
      assertThat( refItems ).extracting( CompletionItem::getKind )
            .containsOnly( CompletionItemKind.Reference );

      final Request reqUnit = request( FULL_HEADER + """
         :MyTrait a samm-c:Trait ;
             samm-c:baseCharacteristic :C ;
             samm-c:unit |
         """ );

      final List<CompletionItem> unitItems = attributeService.complete( reqUnit.document, reqUnit.position );
      assertThat( unitItems ).isNotEmpty();
      assertThat( unitItems ).extracting( CompletionItem::getLabel )
            .contains( "unit:metre", "unit:secondUnitOfTime", "unit:kilometre" );
   }

   @Test
   void suggestsQuantityKinds() {
      final Request req = request( FULL_HEADER + """
         :Km a samm:Unit ;
             samm:quantityKind |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "unit:length", "unit:mass", "unit:time" );
      assertThat( items ).extracting( CompletionItem::getKind )
            .containsOnly( CompletionItemKind.Reference );
   }

   @Test
   void returnsEmptyOnPredicatePosition() {
      final Request req = request( FULL_HEADER + """
         :Range a samm-c:RangeConstraint ;
             samm-c:|
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isEmpty();
   }

   @Test
   void suggestsPredefinedAndLocalCharacteristicsOnProperty() {
      final Request req = request( FULL_HEADER + """
         :MyTrait a samm-c:Trait ;
             samm-c:baseCharacteristic samm-c:Text .

         :MyProperty a samm:Property ;
             samm:characteristic |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "samm-c:Text", "samm-c:Boolean", "samm-c:Timestamp", ":MyTrait" );

      final CompletionItem textItem = items.stream().filter( i -> "samm-c:Text".equals( i.getLabel() ) ).findFirst().orElseThrow();
      assertThat( textItem.getDetail() ).isEqualTo( "Predefined Characteristic" );
      assertThat( textItem.getSortText() ).startsWith( "01_" );

      final CompletionItem localItem = items.stream().filter( i -> ":MyTrait".equals( i.getLabel() ) ).findFirst().orElseThrow();
      assertThat( localItem.getDetail() ).isEqualTo( "Local Characteristic" );
      assertThat( localItem.getSortText() ).startsWith( "02_" );
   }

   @Test
   void suggestsCharacteristicsOnBaseAndElementCharacteristic() {
      final Request reqBase = request( FULL_HEADER + """
         :Trait a samm-c:Trait ;
             samm-c:baseCharacteristic |
         """ );

      final List<CompletionItem> baseItems = attributeService.complete( reqBase.document, reqBase.position );
      assertThat( baseItems ).extracting( CompletionItem::getLabel )
            .contains( "samm-c:Text", "samm-c:MultiLanguageText" );

      final Request reqElem = request( FULL_HEADER + """
         :List a samm-c:List ;
             samm-c:elementCharacteristic |
         """ );

      final List<CompletionItem> elemItems = attributeService.complete( reqElem.document, reqElem.position );
      assertThat( elemItems ).extracting( CompletionItem::getLabel )
            .contains( "samm-c:Text", "samm-c:Boolean" );
   }

   @Test
   void suggestsGeneralDataTypesOnCharacteristic() {
      final Request req = request( FULL_HEADER + """
         :LocalEntity a samm:Entity .

         :MyChar a samm:Characteristic ;
             samm:dataType |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "xsd:string", "xsd:integer", "xsd:boolean", "rdf:langString", "samm:curie", ":LocalEntity" );

      final CompletionItem stringItem = items.stream().filter( i -> "xsd:string".equals( i.getLabel() ) ).findFirst().orElseThrow();
      assertThat( stringItem.getSortText() ).startsWith( "01_" );

      final CompletionItem localEntityItem = items.stream().filter( i -> ":LocalEntity".equals( i.getLabel() ) ).findFirst().orElseThrow();
      assertThat( localEntityItem.getSortText() ).startsWith( "03_" );
   }

   @Test
   void suggestsOnlyEntitiesOnSingleEntityDataType() {
      final Request req = request( FULL_HEADER + """
         :LocalEntity a samm:Entity .

         :Single a samm-c:SingleEntity ;
             samm:dataType |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "samm-e:FileResource", "samm-e:Point3d", "samm-e:Quantity", "samm-e:TimeSeriesEntity", ":LocalEntity" );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .doesNotContain( "xsd:string", "xsd:integer", "xsd:boolean" );
   }

   @Test
   void suggestsTimeSeriesEntityOnTimeSeriesDataType() {
      final Request req = request( FULL_HEADER + """
         :TsExt a samm:Entity ;
             samm:extends samm-e:TimeSeriesEntity .

         :Ts a samm-c:TimeSeries ;
             samm:dataType |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "samm-e:TimeSeriesEntity", ":TsExt" );
      assertThat( items ).extracting( CompletionItem::getLabel )
            .doesNotContain( "xsd:string" );
   }

   @Test
   void suggestsTopLevelClassesForRdfType() {
      final Request req = request( FULL_HEADER + """
         :MyAspect a |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "samm:Aspect", "samm:Property", "samm:Entity", "samm-c:Trait", "samm-c:RangeConstraint" );

      final CompletionItem aspectItem = items.stream().filter( i -> "samm:Aspect".equals( i.getLabel() ) ).findFirst().orElseThrow();
      assertThat( aspectItem.getSortText() ).startsWith( "01_" );

      final CompletionItem traitItem = items.stream().filter( i -> "samm-c:Trait".equals( i.getLabel() ) ).findFirst().orElseThrow();
      assertThat( traitItem.getSortText() ).startsWith( "02_" );
   }

   @Test
   void suggestsScopedClassesForRdfTypeInsideBlankNodes() {
      final Request reqChar = request( FULL_HEADER + """
         :Property a samm:Property ;
             samm:characteristic [
                 a |
             ] .
         """ );

      final List<CompletionItem> charItems = attributeService.complete( reqChar.document, reqChar.position );
      assertThat( charItems ).extracting( CompletionItem::getLabel )
            .contains( "samm-c:Trait", "samm-c:Enumeration", "samm-c:Measurement", "samm-c:SingleEntity" );
      assertThat( charItems ).extracting( CompletionItem::getLabel )
            .doesNotContain( "samm:Aspect", "samm:Operation", "samm-c:RangeConstraint" );

      final Request reqConstraint = request( FULL_HEADER + """
         :Property a samm:Property ;
             samm-c:constraint [
                 a |
             ] .
         """ );

      final List<CompletionItem> constraintItems = attributeService.complete( reqConstraint.document, reqConstraint.position );
      assertThat( constraintItems ).extracting( CompletionItem::getLabel )
            .contains( "samm-c:RangeConstraint", "samm-c:LengthConstraint", "samm-c:EncodingConstraint" );
      assertThat( constraintItems ).extracting( CompletionItem::getLabel )
            .doesNotContain( "samm:Aspect", "samm-c:Trait" );

      final Request reqProp = request( FULL_HEADER + """
         :Aspect a samm:Aspect ;
             samm:properties ( [
                 a |
             ] ) .
         """ );

      final List<CompletionItem> propItems = attributeService.complete( reqProp.document, reqProp.position );
      assertThat( propItems ).extracting( CompletionItem::getLabel )
            .containsExactly( "samm:Property" );
   }

   @Test
   void suggestsEntitiesForExtends() {
      final Request req = request( FULL_HEADER + """
         :AbstractParent a samm:AbstractEntity .

         :Child a samm:Entity ;
             samm:extends |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "samm-e:TimeSeriesEntity", "samm-e:Point3d", "samm-e:Quantity", "samm-e:FileResource", ":AbstractParent" );
   }

   @Test
   void turtleCompletionServiceIntegratesAttributeValues() {
      final Request req = request( FULL_HEADER + """
         :Range a samm-c:RangeConstraint ;
             samm-c:lowerBoundDefinition |
         """ );

      final CompletionParams params = new CompletionParams( new TextDocumentIdentifier( "test.ttl" ), req.position );
      final List<CompletionItem> items = turtleCompletionService.complete( req.document, params, true );

      assertThat( items ).extracting( CompletionItem::getLabel )
            .containsExactly( "samm-c:AT_LEAST", "samm-c:GREATER_THAN" );
   }

   @Test
   void suggestsMimeTypesOnMimeTypePredicate() {
      final Request req = request( FULL_HEADER + """
         :DocFile a samm-e:FileResource ;
             samm-e:mimeType |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "\"application/json\"", "\"application/pdf\"", "\"text/turtle\"", "\"image/png\"" );
      assertThat( items ).extracting( CompletionItem::getKind )
            .containsOnly( CompletionItemKind.Value );

      // Check priority sort ordering: "application/json" sortText should start with "01_"
      final CompletionItem jsonItem = items.stream()
            .filter( i -> "\"application/json\"".equals( i.getLabel() ) )
            .findFirst()
            .orElseThrow();
      assertThat( jsonItem.getSortText() ).startsWith( "01_" );
   }

   @Test
   void suggestsMimeTypesWithPartialTyped() {
      final Request req = request( FULL_HEADER + """
         :DocFile a samm-e:FileResource ;
             samm-e:mimeType "app|
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      final CompletionItem jsonItem = items.stream()
            .filter( i -> "\"application/json\"".equals( i.getLabel() ) )
            .findFirst()
            .orElseThrow();
      final Range editRange = jsonItem.getTextEdit().getLeft().getRange();
      final String replaced = substring( req.source, editRange );
      assertThat( replaced ).isEqualTo( "\"app" );
   }

   @Test
   void suggestsFilteredTimeUnitsOnDuration() {
      final Request req = request( FULL_HEADER + """
         :Interval a samm-c:Duration ;
             samm-c:unit |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "unit:secondUnitOfTime", "unit:minuteUnitOfTime", "unit:hour", "unit:day", "unit:year" )
            .doesNotContain( "unit:kilometre", "unit:degreeCelsius", "unit:volt", "unit:ampere" );

      // Common units like secondUnitOfTime should have priority sort text starting with "01_"
      final CompletionItem secondItem = items.stream()
            .filter( i -> "unit:secondUnitOfTime".equals( i.getLabel() ) )
            .findFirst()
            .orElseThrow();
      assertThat( secondItem.getSortText() ).startsWith( "01_" );
      assertThat( secondItem.getDetail() ).contains( "Time Unit" );
   }

   @Test
   void suggestsFilteredTimeUnitsOnAnonymousDuration() {
      final Request req = request( FULL_HEADER + """
         :timeout a samm:Property ;
             samm:characteristic [
                 a samm-c:Duration ;
                 samm-c:unit |
             ] .
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "unit:secondUnitOfTime", "unit:minuteUnitOfTime", "unit:hour", "unit:day" )
            .doesNotContain( "unit:kilometre", "unit:degreeCelsius", "unit:volt" );
   }

   @Test
   void suggestsAllUnitsWhenNotDuration() {
      final Request req = request( FULL_HEADER + """
         :Distance a samm-c:Measurement ;
             samm-c:unit |
         """ );

      final List<CompletionItem> items = attributeService.complete( req.document, req.position );
      assertThat( items ).isNotEmpty();
      assertThat( items ).extracting( CompletionItem::getLabel )
            .contains( "unit:kilometre", "unit:secondUnitOfTime", "unit:volt" );
   }

   private Request request( final String markedSource ) {
      final int cursor = markedSource.indexOf( '|' );
      assertThat( cursor ).isGreaterThanOrEqualTo( 0 );
      final String source = markedSource.substring( 0, cursor ) + markedSource.substring( cursor + 1 );
      final Position position = SammSuggestionContextDetector.positionAt( source, cursor );
      return new Request( parser.apply( new Document( "file:///test.ttl", source ) ), position, source );
   }

   private String substring( final String source, final Range range ) {
      final int start = offset( source, range.getStart() );
      final int end = offset( source, range.getEnd() );
      return source.substring( start, end );
   }

   private int offset( final String source, final Position position ) {
      int line = 0;
      int col = 0;
      for ( int i = 0; i < source.length(); i++ ) {
         if ( line == position.getLine() && col == position.getCharacter() ) {
            return i;
         }
         if ( source.charAt( i ) == '\n' ) {
            line++;
            col = 0;
         } else {
            col++;
         }
      }
      return source.length();
   }

   private record Request(
         ParsedDocument document, Position position, String source
   ) {}
}
