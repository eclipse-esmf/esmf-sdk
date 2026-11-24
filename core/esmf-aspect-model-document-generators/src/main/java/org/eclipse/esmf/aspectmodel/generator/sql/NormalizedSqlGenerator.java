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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.aspectmodel.generator.GeneratorConstants;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Trait;

import com.google.common.collect.ImmutableList;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Generator for normalized SQL tables.
 *
 * @param <U> the SQL dialect-specific configuration type
 * @param <D> the type of the column datatypes
 * @param <C> the column definition type
 * @param <T> the table definition type
 * @param <X> the constraint type
 */
public class NormalizedSqlGenerator<
      U extends SqlGenerationConfig.DialectSpecificConfig<D, C, X>,
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      T extends SqlTableDefinition<D, C, X>,
      X extends SqlConstraint>
      extends SqlArtifactGeneratorBase<U, D, C, T, X>
      implements AspectVisitor<Stream<C>, NormalizedSqlGenerator.Context<D, C, X>> {

   @RecordBuilder
   public record Context<D extends SqlType, C extends SqlColumnDefinition<D, C>, X extends SqlConstraint>(
         String columnName,
         StructureElement currentStructureElement,
         Property currentProperty,
         boolean forceOptional,
         ModelElement forceDescriptionFromElement,
         List<SqlTableDefinition<D, C, X>> tables,
         Set<StructureElement> visitedStructureElements
   ) {
      public Context {
         if ( tables == null ) {
            tables = new ArrayList<>();
         }
         if ( visitedStructureElements == null ) {
            visitedStructureElements = new HashSet<>();
         }
      }

      public NormalizedSqlGeneratorContextBuilder<D, C, X> copy() {
         return NormalizedSqlGeneratorContextBuilder.<D, C, X>builder()
               .columnName( columnName() )
               .currentStructureElement( currentStructureElement() )
               .currentProperty( currentProperty() )
               .forceOptional( forceOptional() )
               .forceDescriptionFromElement( forceDescriptionFromElement() )
               .tables( tables() )
               .visitedStructureElements( visitedStructureElements() );
      }
   }

   public NormalizedSqlGenerator( final U config, final SqlDialect<U, D, C, T, X> sqlDialect ) {
      super( config, sqlDialect );
   }

   @Override
   public Stream<C> visitBase( final ModelElement modelElement, final Context<D, C, X> context ) {
      return Stream.empty();
   }

   @Override
   public Stream<C> visitProperty( final Property property, final Context<D, C, X> context ) {
      return property.getCharacteristic().stream().flatMap( characteristic -> {
         final NormalizedSqlGenerator.Context<D, C, X> newContext = context.copy()
               .columnName( sqlDialect.columnName( property ) )
               .currentProperty( property )
               .forceOptional( false )
               .build();
         return characteristic.accept( this, newContext );
      } );
   }

   @Override
   public Stream<C> visitAspect( final Aspect aspect, final Context<D, C, X> context ) {
      return visitStructureElement( aspect, context );
   }

   @Override
   public Stream<C> visitCharacteristic( final Characteristic characteristic, final Context<D, C, X> context ) {
      if ( characteristic.getDataType().isEmpty() ) {
         return Stream.empty();
      }
      final Property property = context.currentProperty();
      final Type type = characteristic.getDataType().orElseThrow();
      if ( type instanceof final ComplexType complexType ) {
         // Result of this call is not needed, but it will add the additional table for the complex type to
         // the context
         complexType.accept( this, context );
         addTable( referenceTableForComplexType( complexType, false, context ), context );
         return Stream.empty();
      }

      final Optional<String> comment = config.includeColumnComments()
            ? Optional.ofNullable( Optional.ofNullable( context.forceDescriptionFromElement() ).orElse( property )
                  .getDescription( config.commentLanguage() ) )
            : Optional.empty();
      final D sqlType = sqlDialect.sqlType( type.getUrn() );
      final C column =
            sqlDialect.columnDefinition( context.columnName(), sqlType, property.isOptional() || context.forceOptional(), comment );
      return Stream.of( column );
   }

   @Override
   public Stream<C> visitEither( final Either either, final Context<D, C, X> context ) {
      final Stream<C> leftResult = either.getLeft().accept( this, context.copy()
            .columnName( context.columnName() + LEVEL_DELIMITER + "left" )
            .forceOptional( true )
            .forceDescriptionFromElement( either.getLeft() )
            .build() );
      final Stream<C> rightResult = either.getRight().accept( this, context.copy()
            .columnName( context.columnName() + LEVEL_DELIMITER + "right" )
            .forceOptional( true )
            .forceDescriptionFromElement( either.getRight() )
            .build() );
      return Stream.concat( leftResult, rightResult );
   }

   @Override
   public Stream<C> visitTrait( final Trait trait, final Context<D, C, X> context ) {
      return trait.getBaseCharacteristic().accept( this, context );
   }

   private void addTable( final SqlTableDefinition<D, C, X> tableDefinition, final Context<D, C, X> context ) {
      if ( context.tables().stream().noneMatch( t -> t.name().equals( tableDefinition.name() ) ) ) {
         context.tables().add( tableDefinition );
      }
   }

   private SqlTableDefinition<D, C, X> referenceTableForComplexType( final ComplexType complexType, final boolean addIndexColumn,
         final Context<D, C, X> context ) {
      // The additional underscore is intentional and necessary:
      // If the source table structure element happens to have a property called "references" which a
      // collection Characteristic, this would also lead to a table name "typename__references".
      // Therefore, an additional underscore is added.
      final String tableName = sqlDialect.tableName( complexType ) + LEVEL_DELIMITER + "_references";
      final ImmutableList.Builder<C> builder = ImmutableList.<C>builder()
            .add( sqlDialect.columnDefinition( "_id_source", sqlDialect.sqlIdColumnType(), false, Optional.empty() ) )
            .add( sqlDialect.columnDefinition( "_id_target", sqlDialect.sqlIdColumnType(), false, Optional.empty() ) )
            .add( sqlDialect.columnDefinition( "property_name", sqlDialect.sqlType( XSD.xstring.getURI() ), false,
                  Optional.empty() ) );
      if ( addIndexColumn ) {
         builder.add(
               sqlDialect.columnDefinition( "index", sqlDialect.sqlType( XSD.integer.getURI() ), false, Optional.empty() ) );
      }
      final List<C> columns = List.of(
            sqlDialect.columnDefinition( "_id_source", sqlDialect.sqlIdColumnType(), false, Optional.empty() ),
            sqlDialect.columnDefinition( "_id_target", sqlDialect.sqlIdColumnType(), false, Optional.empty() ),
            sqlDialect.columnDefinition( "property_name", sqlDialect.sqlType( XSD.xstring.getURI() ), false, Optional.empty() )
      );
      final List<X> constraints = ImmutableList.<X>builder()
            .add( sqlDialect.sqlForeignKeyConstraint( "_id_source", sqlDialect.tableName( context.currentStructureElement() ), "_id" ) )
            .add( sqlDialect.sqlForeignKeyConstraint( "_id_target", sqlDialect.tableName( complexType ), "_id" ) )
            .build();
      return sqlDialect.tableDefinition( tableName, columns, Optional.empty(), constraints, Map.of() );
   }

   @Override
   public Stream<C> visitCollection( final Collection collection, final Context<D, C, X> context ) {
      final Type type = collection.getDataType().orElseThrow();
      if ( type instanceof final ComplexType complexType ) {
         // Result of this call is not needed, but it will add the additional table for the complex type to
         // the context
         complexType.accept( this, context );
         addTable( referenceTableForComplexType( complexType, true, context ), context );
         return Stream.empty();
      }

      // Build values table
      final String tableName = sqlDialect.tableName( context.currentStructureElement() ) + LEVEL_DELIMITER + context.columnName();
      final List<C> columns = List.of(
            sqlDialect.columnDefinition( "_id_source", sqlDialect.sqlIdColumnType(), false, Optional.empty() ),
            sqlDialect.columnDefinition( "value", sqlDialect.sqlType( type.getUrn() ), false, Optional.empty() )
      );
      final ImmutableList.Builder<X> constraintBuilder = ImmutableList.builder();
      constraintBuilder.add(
            sqlDialect.sqlForeignKeyConstraint( "_id_source", sqlDialect.tableName( context.currentStructureElement() ), "_id" ) );
      if ( collection.getCollectionType() == CollectionValue.CollectionType.SET
            || collection.getCollectionType() == CollectionValue.CollectionType.SORTEDSET ) {
         constraintBuilder.add( sqlDialect.sqlUniqueConstraint( "u_value", "value" ) );
      }
      final SqlTableDefinition<D, C, X> valuesTable =
            sqlDialect.tableDefinition( tableName, columns, Optional.empty(), constraintBuilder.build(), Map.of() );
      addTable( valuesTable, context );
      return Stream.empty();
   }

   @Override
   public Stream<C> visitComplexType( final ComplexType complexType, final Context<D, C, X> context ) {
      return processStructureElement( complexType, complexType.getAllProperties(), context );
   }

   @Override
   public Stream<C> visitStructureElement( final StructureElement structureElement, final Context<D, C, X> context ) {
      return processStructureElement( structureElement, structureElement.getProperties(), context );
   }

   private Stream<C> processStructureElement( final StructureElement structureElement, final List<Property> properties,
         final Context<D, C, X> context ) {
      if ( context.visitedStructureElements().contains( structureElement ) ) {
         return Stream.empty();
      }
      context.visitedStructureElements().add( structureElement );
      final String tableName = sqlDialect.tableName( structureElement );
      final ImmutableList.Builder<C> columnsBuilder = ImmutableList.builder();
      columnsBuilder.add( sqlDialect.columnDefinition( "_id", sqlDialect.sqlIdColumnType(), false, Optional.empty() ) );
      final Context<D, C, X> newContext = context.copy()
            .currentStructureElement( structureElement )
            .build();
      properties.stream()
            .filter( property -> !property.isNotInPayload() )
            .flatMap( property -> property.accept( this, newContext ) )
            .forEach( columnsBuilder::add );
      // noinspection DuplicatedCode
      config.customColumnsPerTable().getOrDefault( tableName, List.of() ).forEach( columnsBuilder::add );
      final List<C> columns = columnsBuilder.build();
      final Optional<String> comment = config.includeTableComment()
            ? Optional.ofNullable( structureElement.getDescription( config.commentLanguage() ) )
            : Optional.empty();
      final Map<String, SqlValue<? extends D>> tableProperties =
            Map.of( GeneratorConstants.SAMM_EXTENSION, sqlDialect.sqlStringValue( structureElement.urn().toString() ) );
      final List<X> tableConstraints = ImmutableList.<X>builder()
            .addAll( config.tableConstraintsPerTable().getOrDefault( tableName, List.of() ) )
            .add( sqlDialect.sqlPrimaryKeyConstraint( "_id" ) )
            .build();
      final SqlTableDefinition<D, C, X> table =
            sqlDialect.tableDefinition( tableName, columns, comment, tableConstraints, tableProperties );
      addTable( table, context );
      return columns.stream();
   }

   @Override
   public SqlArtifact apply( final Aspect aspect, final U generationConfig ) {
      final Context<D, C, X> context = NormalizedSqlGeneratorContextBuilder.<D, C, X>builder().build();
      aspect.accept( this, context );
      final List<SqlTableDefinition<D, C, X>> tables = context.tables().stream().toList();
      return new <U, D, C, T, X>SqlArtifact( aspect.getName() + ".sql", tables, generationConfig );
   }
}
