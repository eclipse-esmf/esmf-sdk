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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Units;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSuggestionContextDetector.CursorContext;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSuggestionContextDetector.TargetPosition;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public final class SammAttributeValueCompletionService {
   private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

   private static final List<String> ISO_639_1_LANGUAGES = List.of(
         "aa", "ab", "ae", "af", "ak", "am", "an", "ar", "as", "av", "ay", "az",
         "ba", "be", "bg", "bi", "bm", "bn", "bo", "br", "bs",
         "ca", "ce", "ch", "co", "cr", "cs", "cu", "cv", "cy",
         "da", "de", "dv", "dz",
         "ee", "el", "en", "eo", "es", "et", "eu",
         "fa", "ff", "fi", "fj", "fo", "fr", "fy",
         "ga", "gd", "gl", "gn", "gu", "gv",
         "ha", "he", "hi", "ho", "hr", "ht", "hu", "hy", "hz",
         "ia", "id", "ie", "ig", "ii", "ik", "io", "is", "it", "iu",
         "ja", "jv",
         "ka", "kg", "ki", "kj", "kk", "kl", "km", "kn", "ko", "kr", "ks", "ku", "kv", "kw", "ky",
         "la", "lb", "lg", "li", "ln", "lo", "lt", "lu", "lv",
         "mg", "mh", "mi", "mk", "ml", "mn", "mr", "ms", "mt", "my",
         "na", "nb", "nd", "ne", "ng", "nl", "nn", "no", "nr", "nv", "ny",
         "oc", "oj", "om", "or", "os",
         "pa", "pi", "pl", "ps", "pt",
         "qu",
         "rm", "rn", "ro", "ru", "rw",
         "sa", "sc", "sd", "se", "sg", "si", "sk", "sl", "sm", "sn", "so", "sq", "sr", "ss", "st", "su", "sv", "sw",
         "ta", "te", "tg", "th", "ti", "tk", "tl", "tn", "to", "tr", "ts", "tt", "tw", "ty",
         "ug", "uk", "ur", "uz",
         "ve", "vi", "vo",
         "wa", "wo",
         "xh",
         "yi", "yo",
         "za", "zh", "zu" );

   private static final Set<String> PRIORITY_LANGUAGES = Set.of( "en", "de", "fr", "es", "it", "zh", "ja" );

   private static final List<String> COMMON_BCP_47_LOCALES = List.of(
         "en-US", "en-GB", "en-CA", "en-AU", "en-NZ", "en-IE", "en-IN",
         "de-DE", "de-AT", "de-CH",
         "fr-FR", "fr-CA", "fr-BE", "fr-CH",
         "es-ES", "es-MX", "es-AR", "es-CO", "es-CL",
         "it-IT", "it-CH",
         "pt-BR", "pt-PT",
         "nl-NL", "nl-BE",
         "pl-PL", "sv-SE", "da-DK", "fi-FI", "nb-NO",
         "cs-CZ", "hu-HU", "ro-RO", "el-GR", "tr-TR", "ru-RU",
         "ja-JP", "zh-CN", "zh-TW", "zh-HK", "ko-KR",
         "ar-SA", "ar-AE", "ar-EG", "hi-IN" );

   private static final List<String> ENCODINGS = List.of(
         "UTF-8", "US-ASCII", "ISO-8859-1", "UTF-16", "UTF-16BE", "UTF-16LE" );

   private static final List<String> UNIT_NAMES = Units.getUnits().stream()
         .map( Unit::getName )
         .sorted()
         .toList();

   private static final List<String> QUANTITY_KIND_NAMES = Units.getUnits().stream()
         .flatMap( u -> u.getQuantityKinds().stream() )
         .map( QuantityKind::getName )
         .distinct()
         .sorted()
         .toList();

   private record MimeTypeEntry( String mimeType, String description, boolean priority ) {}

   private static final List<MimeTypeEntry> MIME_TYPES = List.of(
         // Application formats
         new MimeTypeEntry( "application/json", "JavaScript Object Notation (JSON)", true ),
         new MimeTypeEntry( "application/ld+json", "JSON-LD Linked Data", true ),
         new MimeTypeEntry( "application/xml", "Extensible Markup Language (XML)", true ),
         new MimeTypeEntry( "application/pdf", "Portable Document Format (PDF)", true ),
         new MimeTypeEntry( "application/octet-stream", "Arbitrary binary data", true ),
         new MimeTypeEntry( "application/zip", "ZIP archive", true ),
         new MimeTypeEntry( "application/gzip", "GZIP compressed archive", false ),
         new MimeTypeEntry( "application/x-tar", "Tape Archive (TAR)", false ),
         new MimeTypeEntry( "application/x-7z-compressed", "7-Zip compressed archive", false ),
         // Text formats
         new MimeTypeEntry( "text/turtle", "Turtle RDF document", true ),
         new MimeTypeEntry( "text/plain", "Plain text", true ),
         new MimeTypeEntry( "text/csv", "Comma-Separated Values (CSV)", true ),
         new MimeTypeEntry( "text/html", "HyperText Markup Language (HTML)", false ),
         new MimeTypeEntry( "text/xml", "XML text", false ),
         new MimeTypeEntry( "text/markdown", "Markdown text", false ),
         // Image formats
         new MimeTypeEntry( "image/png", "Portable Network Graphics (PNG)", true ),
         new MimeTypeEntry( "image/jpeg", "JPEG image", true ),
         new MimeTypeEntry( "image/svg+xml", "Scalable Vector Graphics (SVG)", true ),
         new MimeTypeEntry( "image/gif", "Graphics Interchange Format (GIF)", false ),
         new MimeTypeEntry( "image/webp", "WebP image", false ),
         new MimeTypeEntry( "image/tiff", "Tagged Image File Format (TIFF)", false ),
         // Audio & Video
         new MimeTypeEntry( "audio/mpeg", "MP3 audio", false ),
         new MimeTypeEntry( "audio/wav", "WAV audio", false ),
         new MimeTypeEntry( "audio/ogg", "Ogg audio", false ),
         new MimeTypeEntry( "video/mp4", "MP4 video", false ),
         new MimeTypeEntry( "video/webm", "WebM video", false ),
         // 3D & CAD formats
         new MimeTypeEntry( "model/gltf-binary", "glTF binary 3D model", false ),
         new MimeTypeEntry( "model/gltf+json", "glTF JSON 3D model", false ),
         new MimeTypeEntry( "model/stl", "STL 3D model", false ),
         new MimeTypeEntry( "model/step", "STEP CAD model", false ),
         new MimeTypeEntry( "model/obj", "Wavefront OBJ 3D model", false )
   );

   private static final List<String> DURATION_TIME_UNITS = Units.getUnits().stream()
         .filter( u -> u.getQuantityKinds().stream().anyMatch( qk -> "time".equalsIgnoreCase( qk.getName() ) ) )
         .map( Unit::getName )
         .sorted()
         .toList();

   private static final List<String> COMMON_DURATION_UNITS = List.of(
         "secondUnitOfTime", "minuteUnitOfTime", "hour", "day", "week", "month", "year",
         "millisecond", "microsecond", "nanosecond" );

   private static final List<String> PREDEFINED_CHARACTERISTICS = List.of(
         "Text", "Boolean", "Timestamp", "MultiLanguageText", "Language", "Locale",
         "MimeType", "ResourcePath", "UnitReference" );

   private static final List<String> PREDEFINED_ENTITIES = List.of(
         "FileResource", "Point3d", "Quantity", "TimeSeriesEntity" );

   private static final List<String> COMMON_PRIMITIVE_TYPES = List.of(
         "string", "integer", "boolean", "decimal", "double", "float",
         "dateTime", "date", "time", "duration" );

   private static final List<String> ALL_XSD_LOCAL_NAMES = SammXsdType.ALL_TYPES.stream()
         .map( SammType::getURI )
         .filter( uri -> uri.startsWith( "http://www.w3.org/2001/XMLSchema#" ) )
         .map( uri -> uri.substring( "http://www.w3.org/2001/XMLSchema#".length() ) )
         .sorted()
         .toList();

   private static final List<String> TOP_LEVEL_CORE_CLASSES = List.of(
         "Aspect", "Property", "Characteristic", "Entity", "AbstractEntity",
         "AbstractProperty", "Operation", "Event" );

   private static final List<String> CHARACTERISTIC_CLASSES = List.of(
         "Trait", "Enumeration", "State", "Code", "Measurement", "Quantifiable", "Duration",
         "SingleEntity", "Collection", "List", "Set", "SortedSet", "TimeSeries",
         "StructuredValue", "Either" );

   private static final List<String> CONSTRAINT_CLASSES = List.of(
         "RangeConstraint", "LengthConstraint", "RegularExpressionConstraint",
         "FixedPointConstraint", "EncodingConstraint", "LanguageConstraint", "LocaleConstraint" );

   private final SammSuggestionContextDetector detector = new SammSuggestionContextDetector();

   public List<CompletionItem> complete( final ParsedDocument document, final Position position ) {
      return detector.detect( document, position )
            .filter( context -> context.targetPosition() == TargetPosition.OBJECT_VALUE )
            .filter( context -> context.activePredicate().isPresent() )
            .map( this::generateCompletions )
            .orElseGet( List::of );
   }

   private List<CompletionItem> generateCompletions( final CursorContext context ) {
      final String predicate = context.activePredicate().orElse( "" );
      if ( isPredicate( predicate, "lowerBoundDefinition" ) ) {
         return lowerBoundCompletions( context );
      }
      if ( isPredicate( predicate, "upperBoundDefinition" ) ) {
         return upperBoundCompletions( context );
      }
      if ( isPredicate( predicate, "value" ) && isEncodingConstraintSubject( context ) ) {
         return encodingCompletions( context );
      }
      if ( isPredicate( predicate, "optional" ) || isPredicate( predicate, "notInPayload" ) ) {
         return booleanCompletions( context );
      }
      if ( isPredicate( predicate, "languageCode" ) ) {
         return languageCodeCompletions( context );
      }
      if ( isPredicate( predicate, "localeCode" ) ) {
         return localeCodeCompletions( context );
      }
      if ( isPredicate( predicate, "mimeType" ) ) {
         return mimeTypeCompletions( context );
      }
      if ( isPredicate( predicate, "referenceUnit" ) || isPredicate( predicate, "unit" ) ) {
         return unitCompletions( context );
      }
      if ( isPredicate( predicate, "quantityKind" ) ) {
         return quantityKindCompletions( context );
      }
      if ( isPredicate( predicate, "characteristic" )
            || isPredicate( predicate, "baseCharacteristic" )
            || isPredicate( predicate, "elementCharacteristic" )
            || isPredicate( predicate, "left" )
            || isPredicate( predicate, "right" ) ) {
         return characteristicCompletions( context );
      }
      if ( isPredicate( predicate, "dataType" ) ) {
         return dataTypeCompletions( context );
      }
      if ( isPredicate( predicate, "type" ) || predicate.equals( RDF_TYPE ) ) {
         return rdfTypeCompletions( context );
      }
      if ( isPredicate( predicate, "extends" ) ) {
         return extendsCompletions( context );
      }
      return List.of();
   }

   private static boolean isPredicate( final String predicateUri, final String localName ) {
      return predicateUri.equals( localName )
            || predicateUri.endsWith( ":" + localName )
            || predicateUri.endsWith( "#" + localName )
            || predicateUri.endsWith( "/" + localName );
   }

   private static boolean isEncodingConstraintSubject( final CursorContext context ) {
      return context.hasSubjectType( "EncodingConstraint" );
   }

   private List<CompletionItem> lowerBoundCompletions( final CursorContext context ) {
      final String prefix = context.characteristicPrefix().orElse( "samm-c:" );
      return List.of(
            createCompletionItem( prefix + "AT_LEAST", prefix + "AT_LEAST", context,
                  CompletionItemKind.EnumMember, "Inclusive lower bound (default)", "01", prefix + "AT_LEAST AT_LEAST" ),
            createCompletionItem( prefix + "GREATER_THAN", prefix + "GREATER_THAN", context,
                  CompletionItemKind.EnumMember, "Exclusive lower bound", "02", prefix + "GREATER_THAN GREATER_THAN" )
      );
   }

   private List<CompletionItem> upperBoundCompletions( final CursorContext context ) {
      final String prefix = context.characteristicPrefix().orElse( "samm-c:" );
      return List.of(
            createCompletionItem( prefix + "AT_MOST", prefix + "AT_MOST", context,
                  CompletionItemKind.EnumMember, "Inclusive upper bound (default)", "01", prefix + "AT_MOST AT_MOST" ),
            createCompletionItem( prefix + "LESS_THAN", prefix + "LESS_THAN", context,
                  CompletionItemKind.EnumMember, "Exclusive upper bound", "02", prefix + "LESS_THAN LESS_THAN" )
      );
   }

   private List<CompletionItem> encodingCompletions( final CursorContext context ) {
      final String prefix = context.sammPrefix();
      final List<CompletionItem> result = new ArrayList<>( ENCODINGS.size() );
      for ( int i = 0; i < ENCODINGS.size(); i++ ) {
         final String enc = ENCODINGS.get( i );
         result.add( createCompletionItem( prefix + enc, prefix + enc, context,
               CompletionItemKind.EnumMember, "Encoding charset", String.format( "%02d", i + 1 ), prefix + enc + " " + enc ) );
      }
      return result;
   }

   private List<CompletionItem> booleanCompletions( final CursorContext context ) {
      return List.of(
            createCompletionItem( "true", "true", context, CompletionItemKind.Keyword, "xsd:boolean", "01", "true" ),
            createCompletionItem( "false", "false", context, CompletionItemKind.Keyword, "xsd:boolean", "02", "false" )
      );
   }

   private List<CompletionItem> languageCodeCompletions( final CursorContext context ) {
      final List<CompletionItem> result = new ArrayList<>( ISO_639_1_LANGUAGES.size() );
      for ( final String code : ISO_639_1_LANGUAGES ) {
         final String quoted = "\"" + code + "\"";
         final String display = Locale.forLanguageTag( code ).getDisplayLanguage( Locale.ENGLISH );
         final String detail = display.isEmpty() ? "ISO 639-1 language code" : display + " (ISO 639-1)";
         final String sort = ( PRIORITY_LANGUAGES.contains( code ) ? "01_" : "02_" ) + code;
         final String filter = quoted + " " + code + " " + display;
         result.add( createCompletionItem( quoted, quoted, context, CompletionItemKind.Value, detail, sort, filter ) );
      }
      return result;
   }

   private List<CompletionItem> localeCodeCompletions( final CursorContext context ) {
      final List<CompletionItem> result = new ArrayList<>( COMMON_BCP_47_LOCALES.size() );
      for ( int i = 0; i < COMMON_BCP_47_LOCALES.size(); i++ ) {
         final String code = COMMON_BCP_47_LOCALES.get( i );
         final String quoted = "\"" + code + "\"";
         final String display = Locale.forLanguageTag( code ).getDisplayName( Locale.ENGLISH );
         final String detail = display.isEmpty() ? "BCP 47 locale code" : display + " (BCP 47)";
         final String sort = String.format( "%02d_%s", i + 1, code );
         final String filter = quoted + " " + code + " " + display;
         result.add( createCompletionItem( quoted, quoted, context, CompletionItemKind.Value, detail, sort, filter ) );
      }
      return result;
   }

   private List<CompletionItem> mimeTypeCompletions( final CursorContext context ) {
      final List<CompletionItem> result = new ArrayList<>( MIME_TYPES.size() );
      for ( int i = 0; i < MIME_TYPES.size(); i++ ) {
         final MimeTypeEntry entry = MIME_TYPES.get( i );
         final String quoted = "\"" + entry.mimeType() + "\"";
         final String sort = ( entry.priority() ? "01_" : "02_" ) + String.format( "%02d_%s", i + 1, entry.mimeType() );
         final String filter = quoted + " " + entry.mimeType() + " " + entry.description();
         result.add( createCompletionItem( quoted, quoted, context, CompletionItemKind.Value,
               entry.description(), sort, filter ) );
      }
      return result;
   }

   private List<CompletionItem> unitCompletions( final CursorContext context ) {
      final String prefix = context.unitPrefix().orElse( "unit:" );
      final boolean isDuration = context.hasSubjectType( "Duration" );
      final List<String> units = isDuration ? DURATION_TIME_UNITS : UNIT_NAMES;
      final List<CompletionItem> result = new ArrayList<>( units.size() );
      for ( int i = 0; i < units.size(); i++ ) {
         final String unitName = units.get( i );
         final String prefixed = prefix + unitName;
         final String detail = isDuration ? durationUnitDetail( unitName ) : "Unit";
         final String sort = isDuration ? durationUnitSort( unitName, i ) : null;
         final String filter = isDuration
               ? prefixed + " " + unitName + " " + durationUnitFilter( unitName )
               : prefixed + " " + unitName;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Reference,
               detail, sort, filter ) );
      }
      return result;
   }

   private static String durationUnitDetail( final String unitName ) {
      return Units.fromName( unitName )
            .map( u -> {
               final String name = u.getPreferredName( Locale.ENGLISH );
               final String symbol = u.getSymbol().map( s -> " (" + s + ")" ).orElse( "" );
               return name + symbol + " [Time Unit]";
            } )
            .orElse( "Time Unit" );
   }

   private static String durationUnitFilter( final String unitName ) {
      return Units.fromName( unitName )
            .map( u -> {
               final String name = u.getPreferredName( Locale.ENGLISH );
               final String symbol = u.getSymbol().orElse( "" );
               return name + " " + symbol;
            } )
            .orElse( "" );
   }

   private static String durationUnitSort( final String unitName, final int index ) {
      final int commonIdx = COMMON_DURATION_UNITS.indexOf( unitName );
      if ( commonIdx >= 0 ) {
         return String.format( "01_%02d_%s", commonIdx + 1, unitName );
      }
      return String.format( "02_%02d_%s", index + 1, unitName );
   }

   private List<CompletionItem> quantityKindCompletions( final CursorContext context ) {
      final String prefix = context.unitPrefix().orElse( "unit:" );
      final List<CompletionItem> result = new ArrayList<>( QUANTITY_KIND_NAMES.size() );
      for ( final String qkName : QUANTITY_KIND_NAMES ) {
         final String prefixed = prefix + qkName;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Reference,
               "QuantityKind", null, prefixed + " " + qkName ) );
      }
      return result;
   }

   private List<CompletionItem> characteristicCompletions( final CursorContext context ) {
      final String prefix = context.characteristicPrefix().orElse( "samm-c:" );
      final List<CompletionItem> result = new ArrayList<>();
      for ( int i = 0; i < PREDEFINED_CHARACTERISTICS.size(); i++ ) {
         final String name = PREDEFINED_CHARACTERISTICS.get( i );
         final String prefixed = prefix + name;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Reference,
               "Predefined Characteristic", String.format( "01_%02d_%s", i + 1, name ), prefixed + " " + name ) );
      }
      final List<String> localChars = context.localCharacteristics();
      for ( int i = 0; i < localChars.size(); i++ ) {
         final String name = localChars.get( i );
         final String filter = name + " " + ( name.contains( ":" ) ? name.substring( name.indexOf( ':' ) + 1 ) : name );
         result.add( createCompletionItem( name, name, context, CompletionItemKind.Reference,
               "Local Characteristic", String.format( "02_%02d_%s", i + 1, name ), filter ) );
      }
      return result;
   }

   private List<CompletionItem> dataTypeCompletions( final CursorContext context ) {
      if ( context.hasSubjectType( "SingleEntity" ) ) {
         return singleEntityDataTypeCompletions( context );
      }
      if ( context.hasSubjectType( "TimeSeries" ) ) {
         return timeSeriesDataTypeCompletions( context );
      }
      return generalDataTypeCompletions( context );
   }

   private List<CompletionItem> singleEntityDataTypeCompletions( final CursorContext context ) {
      final String prefix = context.entityPrefix().orElse( "samm-e:" );
      final List<CompletionItem> result = new ArrayList<>();
      for ( int i = 0; i < PREDEFINED_ENTITIES.size(); i++ ) {
         final String name = PREDEFINED_ENTITIES.get( i );
         final String prefixed = prefix + name;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Class,
               "SAMM Predefined Entity", String.format( "01_%02d_%s", i + 1, name ), prefixed + " " + name ) );
      }
      final List<String> localEntities = context.localEntities();
      for ( int i = 0; i < localEntities.size(); i++ ) {
         final String name = localEntities.get( i );
         final String filter = name + " " + ( name.contains( ":" ) ? name.substring( name.indexOf( ':' ) + 1 ) : name );
         result.add( createCompletionItem( name, name, context, CompletionItemKind.Class,
               "Local Entity", String.format( "02_%02d_%s", i + 1, name ), filter ) );
      }
      return result;
   }

   private List<CompletionItem> timeSeriesDataTypeCompletions( final CursorContext context ) {
      final String prefix = context.entityPrefix().orElse( "samm-e:" );
      final List<CompletionItem> result = new ArrayList<>();
      final String tsEntity = prefix + "TimeSeriesEntity";
      result.add( createCompletionItem( tsEntity, tsEntity, context, CompletionItemKind.Class,
            "SAMM Predefined Entity", "01_TimeSeriesEntity", tsEntity + " TimeSeriesEntity" ) );

      final String fullTsEntityUri = context.namespaces().getOrDefault( prefix,
            "urn:samm:org.eclipse.esmf.samm:entity:" + context.version().toVersionString() + "#" ) + "TimeSeriesEntity";
      final List<String> extending = context.localEntitiesExtending( fullTsEntityUri );
      for ( int i = 0; i < extending.size(); i++ ) {
         final String name = extending.get( i );
         final String filter = name + " " + ( name.contains( ":" ) ? name.substring( name.indexOf( ':' ) + 1 ) : name );
         result.add( createCompletionItem( name, name, context, CompletionItemKind.Class,
               "Local Entity (extends TimeSeriesEntity)", String.format( "02_%02d_%s", i + 1, name ), filter ) );
      }
      return result;
   }

   private List<CompletionItem> generalDataTypeCompletions( final CursorContext context ) {
      final String xsdPrefix = context.xsdPrefix().orElse( "xsd:" );
      final String sammPrefix = context.sammPrefix();
      final String rdfPrefix = resolveNamespacePrefix( context, "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:" );
      final List<CompletionItem> result = new ArrayList<>();

      for ( int i = 0; i < COMMON_PRIMITIVE_TYPES.size(); i++ ) {
         final String name = COMMON_PRIMITIVE_TYPES.get( i );
         final String prefixed = xsdPrefix + name;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Class,
               "XSD Datatype", String.format( "01_%02d_%s", i + 1, name ), prefixed + " " + name ) );
      }
      final String langString = rdfPrefix + "langString";
      result.add( createCompletionItem( langString, langString, context, CompletionItemKind.Class,
            "RDF Datatype", "01_98_langString", langString + " langString" ) );
      final String curie = sammPrefix + "curie";
      result.add( createCompletionItem( curie, curie, context, CompletionItemKind.Class,
            "SAMM Datatype", "01_99_curie", curie + " curie" ) );

      final Set<String> commonSet = Set.copyOf( COMMON_PRIMITIVE_TYPES );
      for ( final String name : ALL_XSD_LOCAL_NAMES ) {
         if ( commonSet.contains( name ) ) {
            continue;
         }
         final String prefixed = xsdPrefix + name;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Class,
               "XSD Datatype", "02_" + name, prefixed + " " + name ) );
      }

      final List<String> localEntities = context.localEntities();
      for ( int i = 0; i < localEntities.size(); i++ ) {
         final String name = localEntities.get( i );
         final String filter = name + " " + ( name.contains( ":" ) ? name.substring( name.indexOf( ':' ) + 1 ) : name );
         result.add( createCompletionItem( name, name, context, CompletionItemKind.Class,
               "Local Entity", String.format( "03_%02d_%s", i + 1, name ), filter ) );
      }
      return result;
   }

   private List<CompletionItem> extendsCompletions( final CursorContext context ) {
      final String prefix = context.entityPrefix().orElse( "samm-e:" );
      final List<CompletionItem> result = new ArrayList<>();
      for ( int i = 0; i < PREDEFINED_ENTITIES.size(); i++ ) {
         final String name = PREDEFINED_ENTITIES.get( i );
         final String prefixed = prefix + name;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Class,
               "SAMM Base Entity", String.format( "01_%02d_%s", i + 1, name ), prefixed + " " + name ) );
      }
      final List<String> localAbstract = context.localAbstractEntities();
      for ( int i = 0; i < localAbstract.size(); i++ ) {
         final String name = localAbstract.get( i );
         final String filter = name + " " + ( name.contains( ":" ) ? name.substring( name.indexOf( ':' ) + 1 ) : name );
         result.add( createCompletionItem( name, name, context, CompletionItemKind.Class,
               "Local Abstract Entity", String.format( "02_%02d_%s", i + 1, name ), filter ) );
      }
      final List<String> localEntities = context.localEntities();
      for ( int i = 0; i < localEntities.size(); i++ ) {
         final String name = localEntities.get( i );
         if ( localAbstract.contains( name ) ) {
            continue;
         }
         final String filter = name + " " + ( name.contains( ":" ) ? name.substring( name.indexOf( ':' ) + 1 ) : name );
         result.add( createCompletionItem( name, name, context, CompletionItemKind.Class,
               "Local Entity", String.format( "03_%02d_%s", i + 1, name ), filter ) );
      }
      return result;
   }

   private List<CompletionItem> rdfTypeCompletions( final CursorContext context ) {
      final String sammPrefix = context.sammPrefix();
      final String charPrefix = context.characteristicPrefix().orElse( "samm-c:" );
      final Optional<String> enclosing = context.enclosingPredicate();

      if ( enclosing.isPresent() ) {
         final String enc = enclosing.get();
         if ( isPredicate( enc, "characteristic" ) || isPredicate( enc, "baseCharacteristic" )
               || isPredicate( enc, "elementCharacteristic" ) || isPredicate( enc, "left" )
               || isPredicate( enc, "right" ) ) {
            return characteristicClassesCompletions( context, charPrefix, "01" );
         }
         if ( isPredicate( enc, "constraint" ) ) {
            return constraintClassesCompletions( context, charPrefix, "01" );
         }
         if ( isPredicate( enc, "properties" ) ) {
            final String prop = sammPrefix + "Property";
            return List.of( createCompletionItem( prop, prop, context, CompletionItemKind.Class,
                  "SAMM Meta-Model", "01_Property", prop + " Property" ) );
         }
         if ( isPredicate( enc, "operations" ) ) {
            final String op = sammPrefix + "Operation";
            return List.of( createCompletionItem( op, op, context, CompletionItemKind.Class,
                  "SAMM Meta-Model", "01_Operation", op + " Operation" ) );
         }
         if ( isPredicate( enc, "events" ) ) {
            final String ev = sammPrefix + "Event";
            return List.of( createCompletionItem( ev, ev, context, CompletionItemKind.Class,
                  "SAMM Meta-Model", "01_Event", ev + " Event" ) );
         }
      }

      final List<CompletionItem> result = new ArrayList<>();
      for ( int i = 0; i < TOP_LEVEL_CORE_CLASSES.size(); i++ ) {
         final String name = TOP_LEVEL_CORE_CLASSES.get( i );
         final String prefixed = sammPrefix + name;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Class,
               "SAMM Core Class", String.format( "01_%02d_%s", i + 1, name ), prefixed + " " + name ) );
      }
      result.addAll( characteristicClassesCompletions( context, charPrefix, "02" ) );
      result.addAll( constraintClassesCompletions( context, charPrefix, "03" ) );

      final String unitClass = sammPrefix + "Unit";
      result.add( createCompletionItem( unitClass, unitClass, context, CompletionItemKind.Class,
            "SAMM Unit Class", "04_01_Unit", unitClass + " Unit" ) );
      final String qkClass = sammPrefix + "QuantityKind";
      result.add( createCompletionItem( qkClass, qkClass, context, CompletionItemKind.Class,
            "SAMM QuantityKind Class", "04_02_QuantityKind", qkClass + " QuantityKind" ) );

      return result;
   }

   private List<CompletionItem> characteristicClassesCompletions( final CursorContext context, final String prefix, final String sortPrefix ) {
      final List<CompletionItem> result = new ArrayList<>( CHARACTERISTIC_CLASSES.size() );
      for ( int i = 0; i < CHARACTERISTIC_CLASSES.size(); i++ ) {
         final String name = CHARACTERISTIC_CLASSES.get( i );
         final String prefixed = prefix + name;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Class,
               "SAMM Characteristic Class", String.format( "%s_%02d_%s", sortPrefix, i + 1, name ), prefixed + " " + name ) );
      }
      return result;
   }

   private List<CompletionItem> constraintClassesCompletions( final CursorContext context, final String prefix, final String sortPrefix ) {
      final List<CompletionItem> result = new ArrayList<>( CONSTRAINT_CLASSES.size() );
      for ( int i = 0; i < CONSTRAINT_CLASSES.size(); i++ ) {
         final String name = CONSTRAINT_CLASSES.get( i );
         final String prefixed = prefix + name;
         result.add( createCompletionItem( prefixed, prefixed, context, CompletionItemKind.Class,
               "SAMM Constraint Class", String.format( "%s_%02d_%s", sortPrefix, i + 1, name ), prefixed + " " + name ) );
      }
      return result;
   }

   private static String resolveNamespacePrefix( final CursorContext context, final String namespaceUri, final String defaultPrefix ) {
      return context.namespaces().entrySet().stream()
            .filter( entry -> entry.getValue().equals( namespaceUri ) )
            .map( Map.Entry::getKey )
            .findFirst()
            .orElse( defaultPrefix );
   }

   private static CompletionItem createCompletionItem(
         final String label,
         final String insertText,
         final CursorContext context,
         final CompletionItemKind kind,
         final String detail,
         final String sortText,
         final String filterText ) {
      final CompletionItem item = new CompletionItem( label );
      item.setKind( kind );
      item.setDetail( detail );
      if ( sortText != null ) {
         item.setSortText( sortText );
      }
      item.setFilterText( filterText != null ? filterText : label );
      item.setInsertText( insertText );
      item.setInsertTextFormat( InsertTextFormat.PlainText );
      item.setTextEdit( Either.forLeft( new TextEdit( context.range(), insertText ) ) );
      return item;
   }
}
