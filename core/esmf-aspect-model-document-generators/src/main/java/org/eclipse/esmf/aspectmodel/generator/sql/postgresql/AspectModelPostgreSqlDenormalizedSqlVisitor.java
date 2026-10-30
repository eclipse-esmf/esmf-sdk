/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

package org.eclipse.esmf.aspectmodel.generator.sql.postgresql;

import static org.eclipse.esmf.aspectmodel.generator.sql.postgresql.AspectModelPostgreSqlDenormalizedSqlVisitorContextBuilder.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultList;
import org.eclipse.esmf.metamodel.datatype.RdfDatatypeUris;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.apache.jena.vocabulary.XSD;

/**
 * Generates PostgreSQL with a denormalized schema from an Aspect Model.
 */
public class AspectModelPostgreSqlDenormalizedSqlVisitor
      implements AspectVisitor<String, AspectModelPostgreSqlDenormalizedSqlVisitor.Context> {
   private static final String LEVEL_DELIMITER = "__";
   private static final int MAX_RECURSION_DEPTH = 20;
   private final PostgreSqlGenerationConfig config;
   private final Map<String, PostgreSqlType> postgreSqlTypeMap;
   private final List<ColumnComment> columnComments = new ArrayList<>();
   private String currentTableName;

   private record ColumnComment(
         String columnName, String comment
   ) {}

   @RecordBuilder
   public record Context(
         String prefix,
         Property currentProperty,
         boolean forceOptional,
         ModelElement forceDescriptionFromElement,
         Map<Property, Integer> recursionDepth
   ) {
      public Context {
         if ( prefix == null ) {
            prefix = "";
         }
         if ( recursionDepth == null ) {
            recursionDepth = new HashMap<>();
         }
      }

      public AspectModelPostgreSqlDenormalizedSqlVisitorContextBuilder copy() {
         return builder()
               .prefix( prefix() )
               .currentProperty( currentProperty() )
               .forceOptional( forceOptional() )
               .forceDescriptionFromElement( forceDescriptionFromElement() )
               .recursionDepth( recursionDepth() );
      }
   }

   public AspectModelPostgreSqlDenormalizedSqlVisitor( final PostgreSqlGenerationConfig config ) {
      this.config = config;
      postgreSqlTypeMap = ImmutableMap.<String, PostgreSqlType>builder()
            .put( XSD.xstring.getURI(), PostgreSqlType.TEXT )
            .put( XSD.xboolean.getURI(), PostgreSqlType.BOOLEAN )
            .put( XSD.decimal.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ),
                  Optional.of( config.decimalScale() ) ) )
            .put( XSD.integer.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.xdouble.getURI(), PostgreSqlType.DOUBLE_PRECISION )
            .put( XSD.xfloat.getURI(), PostgreSqlType.REAL )
            .put( XSD.date.getURI(), PostgreSqlType.DATE )
            .put( XSD.time.getURI(), PostgreSqlType.TIME )
            .put( XSD.dateTime.getURI(), PostgreSqlType.TIMESTAMP )
            .put( XSD.dateTimeStamp.getURI(), PostgreSqlType.TIMESTAMP_WITH_TIME_ZONE )
            .put( XSD.gYear.getURI(), PostgreSqlType.TEXT )
            .put( XSD.gMonth.getURI(), PostgreSqlType.TEXT )
            .put( XSD.gDay.getURI(), PostgreSqlType.TEXT )
            .put( XSD.gYearMonth.getURI(), PostgreSqlType.TEXT )
            .put( XSD.gMonthDay.getURI(), PostgreSqlType.TEXT )
            .put( XSD.duration.getURI(), PostgreSqlType.TEXT )
            .put( XSD.yearMonthDuration.getURI(), PostgreSqlType.TEXT )
            .put( XSD.dayTimeDuration.getURI(), PostgreSqlType.TEXT )
            .put( XSD.xbyte.getURI(), PostgreSqlType.SMALLINT )
            .put( XSD.xshort.getURI(), PostgreSqlType.SMALLINT )
            .put( XSD.xint.getURI(), PostgreSqlType.INTEGER )
            .put( XSD.xlong.getURI(), PostgreSqlType.BIGINT )
            .put( XSD.unsignedByte.getURI(), PostgreSqlType.SMALLINT )
            .put( XSD.unsignedShort.getURI(), PostgreSqlType.INTEGER )
            .put( XSD.unsignedInt.getURI(), PostgreSqlType.BIGINT )
            .put( XSD.unsignedLong.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( 20 ) ) )
            .put( XSD.positiveInteger.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.nonNegativeInteger.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.negativeInteger.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.nonPositiveInteger.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.hexBinary.getURI(), PostgreSqlType.BYTEA )
            .put( XSD.base64Binary.getURI(), PostgreSqlType.BYTEA )
            .put( XSD.anyURI.getURI(), PostgreSqlType.TEXT )
            .put( new SAMM( KnownVersion.getLatest() ).resource( "curie" ).getURI(), PostgreSqlType.TEXT )
            .put( RdfDatatypeUris.LANG_STRING, PostgreSqlType.TEXT )
            .build();
   }

   @Override
   public String visitBase( final ModelElement modelElement, final Context context ) {
      return "";
   }

   private String tableName( final Aspect aspect ) {
      return CaseFormat.UPPER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, aspect.getName() );
   }

   private String columnName( final Property property ) {
      return CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, property.getPayloadName() );
   }

   @Override
   public String visitStructureElement( final StructureElement structureElement, final Context context ) {
      final StringBuilder result = new StringBuilder();
      final Consumer<String> appendLine = line -> {
         if ( !line.isBlank() ) {
            if ( !result.isEmpty() && !result.substring( result.length() < 4 ? 0 : result.length() - 4 ).stripTrailing().endsWith( "," ) ) {
               result.append( ",\n" );
            }
            if ( !line.startsWith( "  " ) && ( result.length() < 2 || !result.substring( result.length() - 2 ).equals( "  " ) ) ) {
               result.append( "  " );
            }
            result.append( line );
         }
      };
      for ( final Property property : structureElement.getProperties() ) {
         if ( !property.isNotInPayload() ) {
            appendLine.accept( property.accept( this, context ) );
         }
      }
      if ( structureElement instanceof Aspect ) {
         for ( final PostgreSqlColumnDefinition columnDefinition : config.customColumns() ) {
            appendLine.accept( columnDefinition.toString() );
            if ( config.includeColumnComments() ) {
               columnDefinition.comment().ifPresent( comment -> columnComments.add(
                     new ColumnComment( columnDefinition.name(), comment ) ) );
            }
         }
      }
      return result.toString();
   }

   @Override
   public String visitAspect( final Aspect aspect, final Context context ) {
      currentTableName = tableName( aspect );
      columnComments.clear();
      String columnDeclarations = visitStructureElement( aspect, context );
      if ( columnDeclarations.endsWith( ",\n  " ) ) {
         columnDeclarations = columnDeclarations.substring( 0, columnDeclarations.length() - 4 );
      }
      final StringBuilder result = new StringBuilder( "%s %s (\n%s%s);\n".formatted(
            config.createTableCommandPrefix(), currentTableName, columnDeclarations, columnDeclarations.isEmpty() ? "" : "\n" ) );
      if ( config.includeTableComment() ) {
         Optional.ofNullable( aspect.getDescription( config.commentLanguage() ) )
               .ifPresent( description -> result.append( "COMMENT ON TABLE %s IS '%s';\n".formatted(
                     currentTableName, escapeComment( description ) ) ) );
      }
      columnComments.forEach( comment -> result.append( "COMMENT ON COLUMN %s.%s IS '%s';\n".formatted(
            currentTableName, comment.columnName(), escapeComment( comment.comment() ) ) ) );
      result.append( "-- x-samm-aspect-model-urn: " ).append( aspect.urn() ).append( '\n' );
      return result.toString();
   }

   private String escapeComment( final String comment ) {
      return comment.replace( "\r\n", "\n" ).replace( "'", "''" );
   }

   @Override
   public String visitProperty( final Property property, final Context context ) {
      context.recursionDepth().put( property, context.recursionDepth().getOrDefault( property, 0 ) + 1 );
      if ( property.getCharacteristic().isEmpty() ) {
         return "";
      }
      return property.getCharacteristic().get().accept( this, context.copy()
            .prefix( ( context.prefix().isEmpty() ? "" : context.prefix() + LEVEL_DELIMITER ) + columnName( property ) )
            .currentProperty( property )
            .build() );
   }

   @Override
   public String visitEither( final Either either, final Context context ) {
      final String leftResult = either.getLeft().accept( this, context.copy()
            .prefix( context.prefix() + LEVEL_DELIMITER + "left" )
            .forceOptional( true )
            .forceDescriptionFromElement( either.getLeft() )
            .build() );
      final String rightResult = either.getRight().accept( this, context.copy()
            .prefix( context.prefix() + LEVEL_DELIMITER + "right" )
            .forceOptional( true )
            .forceDescriptionFromElement( either.getRight() )
            .build() );
      return leftResult + ( leftResult.isBlank() ? "" : ",\n" ) + ( rightResult.startsWith( "  " ) ? "" : "  " ) + rightResult;
   }

   @Override
   public String visitCharacteristic( final Characteristic characteristic, final Context context ) {
      final Property property = context.currentProperty();
      final Type type = characteristic.getDataType().orElseThrow();
      if ( type.isComplexType() ) {
         if ( context.recursionDepth().getOrDefault( property, 0 ) >= MAX_RECURSION_DEPTH ) {
            return "";
         }
         final Context contextForComplexType = property.isOptional()
               ? context.copy().forceOptional( true ).build()
               : context;
         return type.accept( this, contextForComplexType );
      }

      final Optional<String> comment = description( property, context );
      return column( context.prefix(), type.accept( this, context ), property.isOptional() || context.forceOptional(), comment );
   }

   private Optional<String> description( final Property property, final Context context ) {
      return config.includeColumnComments()
            ? Optional.ofNullable( Optional.ofNullable( context.forceDescriptionFromElement() ).orElse( property )
                  .getDescription( config.commentLanguage() ) )
            : Optional.empty();
   }

   private String column( final String name, final String type, final boolean nullable, final Optional<String> comment ) {
      comment.ifPresent( value -> columnComments.add( new ColumnComment( name, value ) ) );
      return "%s %s%s".formatted( name, type, nullable ? "" : " NOT NULL" );
   }

   @Override
   public String visitTrait( final Trait trait, final Context context ) {
      return trait.getBaseCharacteristic().accept( this, context );
   }

   @Override
   public String visitCollection( final Collection collection, final Context context ) {
      final Property property = context.currentProperty();
      final Type type = collection.getDataType().orElseThrow();
      final Optional<String> comment = description( property, context );
      if ( type.isComplexType() ) {
         return processComplexType( type.as( ComplexType.class ), context, context.prefix(), collection.is( DefaultList.class ) );
      }
      return column( context.prefix(), type.accept( this, context ) + "[]",
            property.isOptional() || context.forceOptional(), comment );
   }

   private String processComplexType( final ComplexType entity, final Context context, final String parentPrefix,
         final boolean isDefaultList ) {
      final StringBuilder columns = new StringBuilder();
      final String lineDelimiter = ",\n  ";
      entity.getAllProperties().forEach( property -> {
         if ( property.getDataType().isEmpty() || property.isNotInPayload() ) {
            return;
         }
         final Type type = property.getDataType().get();
         String columnPrefix = columnName( property );
         if ( parentPrefix.contains( LEVEL_DELIMITER ) ) {
            columnPrefix = parentPrefix + LEVEL_DELIMITER + columnName( property );
            columns.append( column( columnPrefix + "_id", PostgreSqlType.BIGINT.toString(), false, Optional.empty() ) )
                  .append( lineDelimiter );
         }
         if ( isDefaultList && !parentPrefix.contains( LEVEL_DELIMITER ) ) {
            columnPrefix = parentPrefix + LEVEL_DELIMITER + columnName( property );
         }
         if ( type instanceof Scalar ) {
            columns.append( column( columnPrefix, type.accept( this, context ), property.isOptional(), description( property, context ) ) )
                  .append( lineDelimiter );
         } else if ( type instanceof ComplexType ) {
            columns.append( processComplexType( type.as( ComplexType.class ), context, columnPrefix, type.is( DefaultList.class ) ) );
         }
      } );
      return columns.toString();
   }

   @Override
   public String visitScalar( final Scalar scalar, final Context context ) {
      return postgreSqlTypeMap.get( scalar.getUrn() ).toString();
   }

   @Override
   public String visitEntity( final Entity entity, final Context context ) {
      return visitStructureElement( entity, context );
   }
}
