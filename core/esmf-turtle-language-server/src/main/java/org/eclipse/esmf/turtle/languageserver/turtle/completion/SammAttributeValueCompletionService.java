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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Units;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SAMME;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.TargetPosition;

import org.apache.jena.vocabulary.RDF;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public final class SammAttributeValueCompletionService {
   private static final List<String> ISO_639_1_LANGUAGES = Arrays.stream( Locale.getISOLanguages() ).sorted().toList();

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

   private record MimeTypeEntry(
         String mimeType, String description, boolean priority
   ) {}

   private static final List<MimeTypeEntry> MIME_TYPES = List.of(
         // Application formats - structured data
         new MimeTypeEntry( "application/json", "JSON format", true ),
         new MimeTypeEntry( "application/xml", "XML format", true ),
         new MimeTypeEntry( "application/ld+json", "JSON-LD format", true ),
         new MimeTypeEntry( "application/yaml", "YAML format", true ),
         new MimeTypeEntry( "application/pdf", "Adobe PDF document", true ),
         new MimeTypeEntry( "application/octet-stream", "Binary data stream", true ),
         new MimeTypeEntry( "application/x-protobuf", "Protocol Buffers binary", false ),
         new MimeTypeEntry( "application/avro", "Apache Avro binary", false ),
         new MimeTypeEntry( "application/cbor", "Concise Binary Object Representation", false ),
         // Application formats - office documents
         new MimeTypeEntry( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Microsoft Excel (XLSX)", true ),
         new MimeTypeEntry( "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Microsoft Word (DOCX)", false ),
         new MimeTypeEntry( "application/vnd.ms-excel", "Microsoft Excel (XLS legacy)", false ),
         new MimeTypeEntry( "application/vnd.ms-powerpoint", "Microsoft PowerPoint (PPT legacy)", false ),
         new MimeTypeEntry( "application/vnd.openxmlformats-officedocument.presentationml.presentation",
               "Microsoft PowerPoint (PPTX)", false ),
         // Application formats - archives
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
            .map( this::complete )
            .orElseGet( List::of );
   }

   public List<CompletionItem> complete( final SammCompletionContext context ) {
      if ( context.targetPosition() != TargetPosition.OBJECT_VALUE || context.activePredicate().isEmpty() ) {
         return List.of();
      }
      return generateCompletions( context );
   }

   private List<CompletionItem> generateCompletions( final SammCompletionContext context ) {
      return switch ( localName( context.activePredicate().orElse( "" ) ) ) {
         case "lowerBoundDefinition" -> lowerBoundCompletions( context );
         case "upperBoundDefinition" -> upperBoundCompletions( context );
         case "value" -> context.hasSubjectType( "EncodingConstraint" ) ? encodingCompletions( context ) : List.of();
         case "optional", "notInPayload" -> booleanCompletions( context );
         case "languageCode" -> languageCodeCompletions( context );
         case "localeCode" -> localeCodeCompletions( context );
         case "mimeType" -> mimeTypeCompletions( context );
         case "referenceUnit", "unit" -> unitCompletions( context );
         case "quantityKind" -> quantityKindCompletions( context );
         case "characteristic", "baseCharacteristic", "elementCharacteristic", "left", "right" ->
            characteristicCompletions( context );
         case "dataType" -> dataTypeCompletions( context );
         case "type", "a" -> rdfTypeCompletions( context );
         case "extends" -> extendsCompletions( context );
         default -> List.of();
      };
   }

   private static String localName( final String uriOrCurie ) {
      return SammCompletionContext.localName( uriOrCurie );
   }

   private List<CompletionItem> lowerBoundCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().characteristicPrefix().orElse( "samm-c:" );
      return List.of(
            createCompletionItem( prefix + "AT_LEAST", context,
                  CompletionItemKind.EnumMember, "Inclusive lower bound (default)", "01", prefix + "AT_LEAST AT_LEAST" ),
            createCompletionItem( prefix + "GREATER_THAN", context,
                  CompletionItemKind.EnumMember, "Exclusive lower bound", "02", prefix + "GREATER_THAN GREATER_THAN" )
      );
   }

   private List<CompletionItem> upperBoundCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().characteristicPrefix().orElse( "samm-c:" );
      return List.of(
            createCompletionItem( prefix + "AT_MOST", context,
                  CompletionItemKind.EnumMember, "Inclusive upper bound (default)", "01", prefix + "AT_MOST AT_MOST" ),
            createCompletionItem( prefix + "LESS_THAN", context,
                  CompletionItemKind.EnumMember, "Exclusive upper bound", "02", prefix + "LESS_THAN LESS_THAN" )
      );
   }

   private List<CompletionItem> encodingCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().sammPrefix();
      final List<CompletionItem> result = new ArrayList<>( ENCODINGS.size() );
      for ( int i = 0; i < ENCODINGS.size(); i++ ) {
         final String enc = ENCODINGS.get( i );
         result.add( createCompletionItem( prefix + enc, context,
               CompletionItemKind.EnumMember, "Encoding charset", String.format( "%02d", i + 1 ), prefix + enc + " " + enc ) );
      }
      return result;
   }

   private List<CompletionItem> booleanCompletions( final SammCompletionContext context ) {
      return List.of(
            createCompletionItem( "true", context, CompletionItemKind.Keyword, "xsd:boolean", "01", "true" ),
            createCompletionItem( "false", context, CompletionItemKind.Keyword, "xsd:boolean", "02", "false" )
      );
   }

   private List<CompletionItem> languageCodeCompletions( final SammCompletionContext context ) {
      final List<CompletionItem> result = new ArrayList<>( ISO_639_1_LANGUAGES.size() );
      for ( final String code : ISO_639_1_LANGUAGES ) {
         final String quoted = "\"" + code + "\"";
         final String display = Locale.forLanguageTag( code ).getDisplayLanguage( Locale.ENGLISH );
         final String detail = display.isEmpty() ? "ISO 639-1 language code" : display + " (ISO 639-1)";
         final String sort = ( PRIORITY_LANGUAGES.contains( code ) ? "01_" : "02_" ) + code;
         final String filter = quoted + " " + code + " " + display;
         result.add( createCompletionItem( quoted, context, CompletionItemKind.Value, detail, sort, filter ) );
      }
      return result;
   }

   private List<CompletionItem> localeCodeCompletions( final SammCompletionContext context ) {
      final List<CompletionItem> result = new ArrayList<>( COMMON_BCP_47_LOCALES.size() );
      for ( int i = 0; i < COMMON_BCP_47_LOCALES.size(); i++ ) {
         final String code = COMMON_BCP_47_LOCALES.get( i );
         final String quoted = "\"" + code + "\"";
         final String display = Locale.forLanguageTag( code ).getDisplayName( Locale.ENGLISH );
         final String detail = display.isEmpty() ? "BCP 47 locale code" : display + " (BCP 47)";
         final String sort = String.format( "%02d_%s", i + 1, code );
         final String filter = quoted + " " + code + " " + display;
         result.add( createCompletionItem( quoted, context, CompletionItemKind.Value, detail, sort, filter ) );
      }
      return result;
   }

   private List<CompletionItem> mimeTypeCompletions( final SammCompletionContext context ) {
      final List<CompletionItem> result = new ArrayList<>( MIME_TYPES.size() );
      for ( int i = 0; i < MIME_TYPES.size(); i++ ) {
         final MimeTypeEntry entry = MIME_TYPES.get( i );
         final String quoted = "\"" + entry.mimeType() + "\"";
         final String sort = ( entry.priority() ? "01_" : "02_" ) + String.format( "%02d_%s", i + 1, entry.mimeType() );
         final String filter = quoted + " " + entry.mimeType() + " " + entry.description();
         result.add( createCompletionItem( quoted, context, CompletionItemKind.Value,
               entry.description(), sort, filter ) );
      }
      return result;
   }

   private List<CompletionItem> unitCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().unitPrefix().orElse( "unit:" );
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
         result.add( createCompletionItem( prefixed, context, CompletionItemKind.Reference,
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

   private List<CompletionItem> quantityKindCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().unitPrefix().orElse( "unit:" );
      final List<CompletionItem> result = new ArrayList<>( QUANTITY_KIND_NAMES.size() );
      for ( final String qkName : QUANTITY_KIND_NAMES ) {
         final String prefixed = prefix + qkName;
         result.add( createCompletionItem( prefixed, context, CompletionItemKind.Reference,
               "QuantityKind", null, prefixed + " " + qkName ) );
      }
      return result;
   }

   private List<CompletionItem> characteristicCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().characteristicPrefix().orElse( "samm-c:" );
      if ( context.hasSubjectType( "SingleEntity" ) ) {
         return characteristicEntityCompletions( context, prefix );
      }
      if ( context.hasSubjectType( "Duration" ) ) {
         return characteristicDurationCompletions( context, prefix );
      }
      final List<CompletionItem> result = prefixedResourceCompletions( context, PREDEFINED_CHARACTERISTICS, prefix,
            CompletionItemKind.Reference, "Predefined Characteristic", indexedSort( "01" ) );
      result.addAll( localResourceCompletions( context, context.localCharacteristics(),
            CompletionItemKind.Reference, "Local Characteristic", "02" ) );
      return result;
   }

   private List<CompletionItem> characteristicEntityCompletions( final SammCompletionContext context, final String prefix ) {
      final List<CompletionItem> result = new ArrayList<>();
      final String entity = prefix + "SingleEntity";
      result.add( createCompletionItem( entity, context, CompletionItemKind.Reference,
            "Predefined Characteristic", "01_SingleEntity", entity + " SingleEntity" ) );
      result.addAll( localResourceCompletions( context, context.localCharacteristics(),
            CompletionItemKind.Reference, "Local Characteristic", "02" ) );
      return result;
   }

   private List<CompletionItem> characteristicDurationCompletions( final SammCompletionContext context, final String prefix ) {
      final List<CompletionItem> result = new ArrayList<>();
      final String dur = prefix + "Duration";
      result.add( createCompletionItem( dur, context, CompletionItemKind.Reference,
            "Predefined Characteristic", "01_Duration", dur + " Duration" ) );
      result.addAll( localResourceCompletions( context, context.localCharacteristics(),
            CompletionItemKind.Reference, "Local Characteristic", "02" ) );
      return result;
   }

   private List<CompletionItem> dataTypeCompletions( final SammCompletionContext context ) {
      if ( context.hasSubjectType( "SingleEntity" ) ) {
         return singleEntityDataTypeCompletions( context );
      }
      if ( context.hasSubjectType( "TimeSeries" ) ) {
         return timeSeriesDataTypeCompletions( context );
      }
      return generalDataTypeCompletions( context );
   }

   private List<CompletionItem> singleEntityDataTypeCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().entityPrefix().orElse( "samm-e:" );
      final List<CompletionItem> result = prefixedResourceCompletions( context, PREDEFINED_ENTITIES, prefix,
            CompletionItemKind.Class, "SAMM Predefined Entity", indexedSort( "01" ) );
      result.addAll( localResourceCompletions( context, context.localEntities(), CompletionItemKind.Class, "Local Entity", "02" ) );
      return result;
   }

   private List<CompletionItem> timeSeriesDataTypeCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().entityPrefix().orElse( "samm-e:" );
      final List<CompletionItem> result = new ArrayList<>();
      final String tsEntity = prefix + "TimeSeriesEntity";
      result.add( createCompletionItem( tsEntity, context, CompletionItemKind.Class,
            "SAMM Predefined Entity", "01_TimeSeriesEntity", tsEntity + " TimeSeriesEntity" ) );

      final String entityNs = new SAMME( context.namespaces().version(), new SAMM( context.namespaces().version() ) ).getNamespace();
      final String fullTsEntityUri = context.namespaces().declarations().getOrDefault( prefix, entityNs ) + "TimeSeriesEntity";
      result.addAll( localResourceCompletions( context, context.localEntitiesExtending( fullTsEntityUri ), CompletionItemKind.Class,
            "Local Entity (extends TimeSeriesEntity)", "02" ) );
      return result;
   }

   private List<CompletionItem> generalDataTypeCompletions( final SammCompletionContext context ) {
      final String xsdPrefix = context.namespaces().xsdPrefix().orElse( "xsd:" );
      final String sammPrefix = context.namespaces().sammPrefix();
      final String rdfPrefix = resolveNamespacePrefix( context, RDF.getURI(), "rdf:" );
      final List<CompletionItem> result = prefixedResourceCompletions( context, COMMON_PRIMITIVE_TYPES, xsdPrefix,
            CompletionItemKind.Class, "XSD Datatype", indexedSort( "01" ) );
      final String langString = rdfPrefix + "langString";
      result.add( createCompletionItem( langString, context, CompletionItemKind.Class,
            "RDF Datatype", "01_98_langString", langString + " langString" ) );
      final String curie = sammPrefix + "curie";
      result.add( createCompletionItem( curie, context, CompletionItemKind.Class,
            "SAMM Datatype", "01_99_curie", curie + " curie" ) );

      final Set<String> commonSet = Set.copyOf( COMMON_PRIMITIVE_TYPES );
      final List<String> otherXsdTypes = ALL_XSD_LOCAL_NAMES.stream().filter( name -> !commonSet.contains( name ) ).toList();
      result.addAll( prefixedResourceCompletions( context, otherXsdTypes, xsdPrefix, CompletionItemKind.Class,
            "XSD Datatype", ( index, name ) -> "02_" + name ) );
      result.addAll( localResourceCompletions( context, context.localEntities(), CompletionItemKind.Class, "Local Entity", "03" ) );
      return result;
   }

   private List<CompletionItem> extendsCompletions( final SammCompletionContext context ) {
      final String prefix = context.namespaces().entityPrefix().orElse( "samm-e:" );
      final List<CompletionItem> result = prefixedResourceCompletions( context, PREDEFINED_ENTITIES, prefix,
            CompletionItemKind.Class, "SAMM Base Entity", indexedSort( "01" ) );
      final List<String> localAbstract = context.localAbstractEntities();
      result.addAll( localResourceCompletions( context, localAbstract, CompletionItemKind.Class, "Local Abstract Entity", "02" ) );
      result.addAll( localResourceCompletions( context, context.localEntities(), CompletionItemKind.Class, "Local Entity", "03",
            name -> !localAbstract.contains( name ) ) );
      return result;
   }

   private List<CompletionItem> rdfTypeCompletions( final SammCompletionContext context ) {
      final String sammPrefix = context.namespaces().sammPrefix();
      final String charPrefix = context.namespaces().characteristicPrefix().orElse( "samm-c:" );
      final Optional<String> enclosing = context.enclosingPredicate();

      if ( enclosing.isPresent() ) {
         final List<CompletionItem> scoped = switch ( localName( enclosing.get() ) ) {
            case "characteristic", "baseCharacteristic", "elementCharacteristic", "left", "right" ->
               characteristicClassesCompletions( context, charPrefix, "01" );
            case "constraint" -> constraintClassesCompletions( context, charPrefix, "01" );
            case "properties" -> List.of( singleClassCompletion( context, sammPrefix, "Property" ) );
            case "operations" -> List.of( singleClassCompletion( context, sammPrefix, "Operation" ) );
            case "events" -> List.of( singleClassCompletion( context, sammPrefix, "Event" ) );
            default -> List.of();
         };
         if ( !scoped.isEmpty() ) {
            return scoped;
         }
      }

      final List<CompletionItem> result = prefixedResourceCompletions( context, TOP_LEVEL_CORE_CLASSES, sammPrefix,
            CompletionItemKind.Class, "SAMM Core Class", indexedSort( "01" ) );
      result.addAll( characteristicClassesCompletions( context, charPrefix, "02" ) );
      result.addAll( constraintClassesCompletions( context, charPrefix, "03" ) );

      final String unitClass = sammPrefix + "Unit";
      result.add( createCompletionItem( unitClass, context, CompletionItemKind.Class,
            "SAMM Unit Class", "04_01_Unit", unitClass + " Unit" ) );
      final String qkClass = sammPrefix + "QuantityKind";
      result.add( createCompletionItem( qkClass, context, CompletionItemKind.Class,
            "SAMM QuantityKind Class", "04_02_QuantityKind", qkClass + " QuantityKind" ) );

      return result;
   }

   private static CompletionItem singleClassCompletion(
         final SammCompletionContext context, final String prefix, final String className ) {
      final String prefixed = prefix + className;

      return createCompletionItem( prefixed, context, CompletionItemKind.Class,
            "SAMM Meta-Model", "01_" + className, prefixed + " " + className );
   }

   private List<CompletionItem> characteristicClassesCompletions( final SammCompletionContext context, final String prefix,
         final String sortPrefix ) {
      return prefixedResourceCompletions( context, CHARACTERISTIC_CLASSES, prefix, CompletionItemKind.Class,
            "SAMM Characteristic Class", indexedSort( sortPrefix ) );
   }

   private List<CompletionItem> constraintClassesCompletions( final SammCompletionContext context, final String prefix,
         final String sortPrefix ) {
      return prefixedResourceCompletions( context, CONSTRAINT_CLASSES, prefix, CompletionItemKind.Class,
            "SAMM Constraint Class", indexedSort( sortPrefix ) );
   }

   private static List<CompletionItem> prefixedResourceCompletions( final SammCompletionContext context, final List<String> names,
         final String prefix, final CompletionItemKind kind, final String detail, final BiFunction<Integer, String, String> sortText ) {
      final List<CompletionItem> result = new ArrayList<>( names.size() );
      for ( int i = 0; i < names.size(); i++ ) {
         final String name = names.get( i );
         final String prefixed = prefix + name;
         result.add( createCompletionItem( prefixed, context, kind, detail, sortText.apply( i, name ), prefixed + " " + name ) );
      }
      return result;
   }

   private static List<CompletionItem> localResourceCompletions( final SammCompletionContext context, final List<String> names,
         final CompletionItemKind kind, final String detail, final String sortPrefix ) {
      return localResourceCompletions( context, names, kind, detail, sortPrefix, name -> true );
   }

   private static List<CompletionItem> localResourceCompletions( final SammCompletionContext context, final List<String> names,
         final CompletionItemKind kind, final String detail, final String sortPrefix, final Predicate<String> include ) {
      final List<CompletionItem> result = new ArrayList<>( names.size() );
      for ( int i = 0; i < names.size(); i++ ) {
         final String name = names.get( i );
         if ( include.test( name ) ) {
            final String localName = name.contains( ":" ) ? name.substring( name.indexOf( ':' ) + 1 ) : name;
            result.add( createCompletionItem( name, context, kind, detail,
                  String.format( "%s_%02d_%s", sortPrefix, i + 1, name ), name + " " + localName ) );
         }
      }
      return result;
   }

   private static BiFunction<Integer, String, String> indexedSort( final String sortPrefix ) {
      return ( index, name ) -> String.format( "%s_%02d_%s", sortPrefix, index + 1, name );
   }

   private static String resolveNamespacePrefix( final SammCompletionContext context, final String namespaceUri,
         final String defaultPrefix ) {
      return context.namespaces().declarations().entrySet().stream()
            .filter( entry -> entry.getValue().equals( namespaceUri ) )
            .map( Map.Entry::getKey )
            .findFirst()
            .orElse( defaultPrefix );
   }

   private static CompletionItem createCompletionItem(
         final String label,
         final SammCompletionContext context,
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
      item.setInsertText( label );
      item.setInsertTextFormat( InsertTextFormat.PlainText );
      item.setTextEdit( Either.forLeft( new TextEdit( context.edit().range(), label ) ) );

      return item;
   }
}
