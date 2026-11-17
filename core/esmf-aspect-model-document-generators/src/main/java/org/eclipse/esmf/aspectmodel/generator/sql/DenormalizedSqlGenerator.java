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

package org.eclipse.esmf.aspectmodel.generator.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksType;
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

import com.google.common.collect.ImmutableList;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Dialect-agnostic SQL generator that creates denormalized schemas.
 *
 * @param <U> the SQL dialect-specific configuration type
 * @param <D> the type of the column datatypes
 * @param <C> the column definition type
 * @param <T> the table definition type
 */
public class DenormalizedSqlGenerator<
      U extends SqlGenerationConfig.DialectSpecificConfig<D, C>,
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      T extends SqlTableDefinition<D, C>>
      extends SqlArtifactGeneratorBase<U, D, C, T> {
   private static final String LEVEL_DELIMITER = "__";
   private static final int MAX_RECURSION_DEPTH = 20;
   private final SqlDialect<U, D, C, T> sqlDialect;

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

      public DenormalizedSqlGeneratorContextBuilder copy() {
         return DenormalizedSqlGeneratorContextBuilder.builder()
               .prefix( prefix() )
               .currentProperty( currentProperty() )
               .forceOptional( forceOptional() )
               .forceDescriptionFromElement( forceDescriptionFromElement() )
               .recursionDepth( recursionDepth() );
      }
   }

   public DenormalizedSqlGenerator( final U config, final SqlDialect<U, D, C, T> sqlDialect ) {
      super( config );
      this.sqlDialect = sqlDialect;
   }

   protected Stream<C> column( final Property property, final Context context ) {
      context.recursionDepth().put( property, context.recursionDepth().getOrDefault( property, 0 ) + 1 );
      return property.getCharacteristic().stream().flatMap( characteristic -> {
         final Context newContext = context.copy()
               .prefix( ( context.prefix().isEmpty() ? "" : context.prefix() + LEVEL_DELIMITER ) + sqlDialect.columnName( property ) )
               .currentProperty( property )
               .build();
         return characteristic.accept( new CharacteristicVisitor(), newContext );
      } );
   }

   private class CharacteristicVisitor implements AspectVisitor<Stream<C>, Context> {
      @Override
      public Stream<C> visitBase( final ModelElement modelElement, final Context context ) {
         return Stream.empty();
      }

      @Override
      public Stream<C> visitCharacteristic( final Characteristic characteristic, final Context context ) {
         final Property property = context.currentProperty();
         final Type type = characteristic.getDataType().orElseThrow();
         if ( type.isComplexType() ) {
            // Break endless recursion
            if ( context.recursionDepth().getOrDefault( property, 0 ) >= MAX_RECURSION_DEPTH ) {
               return Stream.empty();
            }
            // If the property is optional but points to an Entity with mandatory properties, the columns for
            // those properties still need to be optional (i.e. nullable), so we force optionality here.
            final Context contextForComplexType = property.isOptional()
                  ? context.copy().forceOptional( true ).build()
                  : context;
            return type.accept( this, contextForComplexType );
         }

         final Optional<String> comment = config.includeColumnComments()
               ? Optional.ofNullable( Optional.ofNullable( context.forceDescriptionFromElement() ).orElse( property )
                     .getDescription( config.commentLanguage() ) )
               : Optional.empty();
         final D sqlType = sqlDialect.sqlType( type.getUrn() );
         final C column = sqlDialect.columnDefinition( context.prefix(), sqlType,
               property.isOptional() || context.forceOptional(), comment );
         return Stream.of( column );
      }

      @Override
      public Stream<C> visitEither( final Either either, final Context context ) {
         final Stream<C> leftResult = either.getLeft().accept( this, context.copy()
               .prefix( context.prefix() + LEVEL_DELIMITER + "left" )
               .forceOptional( true )
               .forceDescriptionFromElement( either.getLeft() )
               .build() );
         final Stream<C> rightResult = either.getRight().accept( this, context.copy()
               .prefix( context.prefix() + LEVEL_DELIMITER + "right" )
               .forceOptional( true )
               .forceDescriptionFromElement( either.getRight() )
               .build() );
         return Stream.concat( leftResult, rightResult );
      }

      @Override
      public Stream<C> visitTrait( final Trait trait, final Context context ) {
         return trait.getBaseCharacteristic().accept( this, context );
      }

      @Override
      public Stream<C> visitCollection( final Collection collection, final Context context ) {
         final Property property = context.currentProperty();
         final Type type = collection.getDataType().orElseThrow();
         final Optional<String> comment = config.includeColumnComments()
               ? Optional.ofNullable( Optional.ofNullable( context.forceDescriptionFromElement() ).orElse( property )
                     .getDescription( config.commentLanguage() ) )
               : Optional.empty();

         if ( type.isComplexType() ) {
            // Flattening collections of complex types
            final ComplexType complexType = type.as( ComplexType.class );
            collection.as( Collection.class ).getCollectionType();
            return processComplexType( complexType, context, context.prefix(), collection.is( DefaultList.class ) );
         } else {
            // Handle scalar types normally
            final D d = sqlDialect.sqlType( type.getUrn() );
            final D arrayType = sqlDialect.sqlArrayType( d );
            return Stream.of( sqlDialect.columnDefinition( context.prefix(), arrayType,
                  property.isOptional() || context.forceOptional(), comment ) );
         }
      }

      @Override
      public Stream<C> visitEntity( final Entity entity, final Context context ) {
         // final ImmutableList.Builder<C> columnsBuilder = ImmutableList.builder();
         return entity.getProperties().stream()
               .filter( property -> !property.isNotInPayload() )
               .flatMap( property -> column( property, context ) );
         // return visitStructureElement( entity, context );
      }

      private Stream<C> processComplexType( final ComplexType entity, final Context context, final String parentPrefix,
            final boolean isDefaultList ) {
         final List<C> columns = new ArrayList<>();
         entity.getAllProperties().forEach( property -> {
            if ( property.getDataType().isEmpty() || property.isNotInPayload() ) {
               return; // Skip properties with no data type or not in payload
            }

            final Type type = property.getDataType().get();
            String columnPrefix = sqlDialect.columnName( property );

            if ( parentPrefix.contains( LEVEL_DELIMITER ) ) {
               columnPrefix = parentPrefix + LEVEL_DELIMITER + sqlDialect.columnName( property );
               final D columnType = sqlDialect.sqlType( XSD.xlong.getURI() );
               final C column = sqlDialect.columnDefinition( columnPrefix + "_id", columnType, false,
                     Optional.empty() );
               columns.add( column );
            }

            if ( isDefaultList && !parentPrefix.contains( LEVEL_DELIMITER ) ) {
               columnPrefix = parentPrefix + LEVEL_DELIMITER + sqlDialect.columnName( property );
            }

            if ( type instanceof final Scalar scalar ) {
               final D columnType = sqlDialect.sqlType( scalar.getUrn() );
               final Optional<String> comment = config.includeColumnComments()
                     ? Optional.ofNullable( Optional.ofNullable( context.forceDescriptionFromElement() ).orElse( property )
                           .getDescription( config.commentLanguage() ) )
                     : Optional.empty();
               columns.add( sqlDialect.columnDefinition( columnPrefix, columnType, property.isOptional(), comment ) );
            } else if ( type instanceof ComplexType ) {
               processComplexType( type.as( ComplexType.class ), context, columnPrefix, type.is( DefaultList.class ) )
                     .forEach( columns::add );
            }
         } );

         return columns.stream();
      }
   }

   protected SqlTableDefinition<D, C> table( final StructureElement structureElement, final Context context ) {
      final ImmutableList.Builder<C> columnsBuilder = ImmutableList.builder();
      structureElement.getProperties().stream()
            .filter( property -> !property.isNotInPayload() )
            .flatMap( property -> column( property, context ) )
            .forEach( columnsBuilder::add );
      final List<C> columns = columnsBuilder.addAll( config.customColumns() ).build();
      final Optional<String> comment = config.includeTableComment()
            ? Optional.ofNullable( structureElement.getDescription( config.commentLanguage() ) )
            : Optional.empty();
      @SuppressWarnings( "unchecked" )
      final Map<String, SqlValue<? extends DatabricksType>> tableProperties =
            Map.of( AspectModelJsonSchemaGenerator.SAMM_EXTENSION, sqlDialect.sqlStringValue( structureElement.urn().toString() ) );
      return sqlDialect.tableDefinition( sqlDialect.tableName( structureElement ), columns, comment, config.tableConstraints(),
            tableProperties );
   }

   @Override
   public SqlArtifact apply( final Aspect aspect, final U generationConfig ) {
      final Context context = DenormalizedSqlGeneratorContextBuilder.builder().build();
      return new <U, D, C, T>SqlArtifact( aspect.getName() + ".sql", List.of( table( aspect, context ) ), generationConfig );
   }
}
