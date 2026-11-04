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

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Generates PostgreSQL with a denormalized schema from an Aspect Model.
 */
public class AspectModelPostgreSqlDenormalizedSqlVisitor
      extends AspectModelPostgreSqlVisitor<AspectModelPostgreSqlDenormalizedSqlVisitor.Context> {
   private static final String LEVEL_DELIMITER = "__";
   private static final int MAX_RECURSION_DEPTH = 20;
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
      super( config );
   }

   @Override
   public String visitBase( final ModelElement modelElement, final Context context ) {
      return "";
   }

   @Override
   public String visitStructureElement( final StructureElement structureElement, final Context context ) {
      final StringBuilder result = new StringBuilder();
      structureElement.getProperties().stream()
            .filter( property -> !property.isNotInPayload() )
            .forEach( property -> appendLine( result, property.accept( this, context ) ) );
      if ( structureElement instanceof Aspect ) {
         config.customColumns().forEach( columnDefinition -> {
            appendLine( result, columnDefinition.toString() );
            if ( config.includeColumnComments() ) {
               columnDefinition.comment().ifPresent( comment -> columnComments.add(
                     new ColumnComment( columnDefinition.name(), comment ) ) );
            }
         } );
         config.tableConstraints().forEach( constraint -> appendLine( result, constraint ) );
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
