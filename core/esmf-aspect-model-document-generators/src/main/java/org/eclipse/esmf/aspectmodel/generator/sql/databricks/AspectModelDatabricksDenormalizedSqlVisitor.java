/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.sql.databricks;

import static org.eclipse.esmf.aspectmodel.generator.sql.databricks.AspectModelDatabricksDenormalizedSqlVisitorContextBuilder.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AbstractGenerator;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.Either;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

/**
 * Generates Databricks SQL with a denormalized schema from an Aspect Model.
 */
public class AspectModelDatabricksDenormalizedSqlVisitor
      implements AspectVisitor<String, AspectModelDatabricksDenormalizedSqlVisitor.Context> {
   private static final String LEVEL_DELIMITER = "__";
   private static final int MAX_RECURSION_DEPTH = 20;
   private final DatabricksSqlGenerationConfig config;
   private final Map<String, DatabricksType> databricksTypeMap;

   @RecordBuilder
   public record Context(
         String prefix,
         Property currentProperty,
         boolean forceOptional,
         NamedElement forceDescriptionFromElement,
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

      public AspectModelDatabricksDenormalizedSqlVisitorContextBuilder copy() {
         return builder()
               .prefix( prefix() )
               .currentProperty( currentProperty() )
               .forceOptional( forceOptional() )
               .forceDescriptionFromElement( forceDescriptionFromElement() )
               .recursionDepth( recursionDepth() );
      }
   }

   public AspectModelDatabricksDenormalizedSqlVisitor( final DatabricksSqlGenerationConfig config ) {
      this.config = config;
      databricksTypeMap = ImmutableMap.<String, DatabricksType> builder()
            .put( XSD.xstring.getURI(), DatabricksType.STRING )
            .put( XSD.xboolean.getURI(), DatabricksType.BOOLEAN )
            .put( XSD.decimal.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.integer.getURI(), new DatabricksType.DatabricksDecimal() )
            .put( XSD.xdouble.getURI(), DatabricksType.DOUBLE )
            .put( XSD.xfloat.getURI(), DatabricksType.FLOAT )
            .put( XSD.date.getURI(), DatabricksType.STRING )
            .put( XSD.time.getURI(), DatabricksType.STRING )
            .put( XSD.dateTime.getURI(), DatabricksType.STRING )
            .put( XSD.dateTimeStamp.getURI(), DatabricksType.TIMESTAMP )
            .put( XSD.gYear.getURI(), DatabricksType.STRING )
            .put( XSD.gMonth.getURI(), DatabricksType.STRING )
            .put( XSD.gDay.getURI(), DatabricksType.STRING )
            .put( XSD.gYearMonth.getURI(), DatabricksType.STRING )
            .put( XSD.gMonthDay.getURI(), DatabricksType.STRING )
            .put( XSD.duration.getURI(), DatabricksType.STRING )
            .put( XSD.yearMonthDuration.getURI(), DatabricksType.STRING )
            .put( XSD.dayTimeDuration.getURI(), DatabricksType.STRING )
            .put( XSD.xbyte.getURI(), DatabricksType.TINYINT )
            .put( XSD.xshort.getURI(), DatabricksType.SMALLINT )
            .put( XSD.xint.getURI(), DatabricksType.INT )
            .put( XSD.xlong.getURI(), DatabricksType.BIGINT )
            .put( XSD.unsignedByte.getURI(), DatabricksType.SMALLINT )
            .put( XSD.unsignedShort.getURI(), DatabricksType.INT )
            .put( XSD.unsignedInt.getURI(), DatabricksType.BIGINT )
            .put( XSD.unsignedLong.getURI(), new DatabricksType.DatabricksDecimal() )
            .put( XSD.positiveInteger.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.nonNegativeInteger.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.negativeInteger.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.nonPositiveInteger.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.hexBinary.getURI(), DatabricksType.BINARY )
            .put( XSD.base64Binary.getURI(), DatabricksType.BINARY )
            .put( XSD.anyURI.getURI(), DatabricksType.STRING )
            .put( new SAMM( KnownVersion.getLatest() ).resource( "curie" ).getURI(), DatabricksType.STRING )
            .put( RDF.langString.getURI(), DatabricksType.STRING )
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
      for ( final Property property : structureElement.getProperties() ) {
         if ( property.isNotInPayload() ) {
            continue;
         }
         final String propertyResult = property.accept( this, context );
         if ( !propertyResult.isBlank() ) {
            if ( !result.isEmpty() ) {
               result.append( ",\n" );
            }
            if ( !propertyResult.startsWith( "  " ) ) {
               result.append( "  " );
            }
            result.append( propertyResult );
         }
      }
      return result.toString();
   }

   private String escapeComment( final String comment ) {
      return comment.replace( "'", "\\'" );
   }

   @Override
   public String visitAspect( final Aspect aspect, final Context context ) {
      final String columnDeclarations = visitStructureElement( aspect, context );
      final String comment = config.includeTableComment()
            ? Optional.ofNullable( aspect.getDescription( config.commentLanguage() ) ).map( description ->
            "COMMENT '" + escapeComment( description ) + "'\n" ).orElse( "" )
            : "";
      return "%s %s (\n%s%s)\n%sTBLPROPERTIES ('%s'='%s');\n".formatted(
            config.createTableCommandPrefix(),
            tableName( aspect ),
            columnDeclarations,
            columnDeclarations.isEmpty() ? "" : "\n",
            comment,
            AbstractGenerator.SAMM_EXTENSION,
            aspect.getAspectModelUrn().orElseThrow()
      );
   }

   @Override
   public String visitProperty( final Property property, final Context context ) {
      context.recursionDepth().put( property, context.recursionDepth().getOrDefault( property, 0 ) + 1 );
      if ( property.getCharacteristic().isEmpty() ) {
         return "";
      }

      return property.getCharacteristic().get().accept( this, context.copy()
            .prefix( (context.prefix().isEmpty() ? "" : context.prefix() + LEVEL_DELIMITER) + columnName( property ) )
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
      return leftResult + "\n" + (rightResult.startsWith( "  " ) ? "" : "  ") + rightResult;
   }

   @Override
   public String visitCharacteristic( final Characteristic characteristic, final Context context ) {
      final Property property = context.currentProperty();
      final Type type = characteristic.getDataType().orElseThrow();
      if ( type.isComplexType() ) {
         // Break endless recursion
         if ( context.recursionDepth().getOrDefault( property, 0 ) >= MAX_RECURSION_DEPTH ) {
            return "";
         }
         // If the property is optional but points to an Entity with mandatory properties, the columns for those
         // properties still need to be optional (i.e. nullable), so we force optionality here.
         final Context contextForComplexType = property.isOptional()
               ? context.copy().forceOptional( true ).build()
               : context;
         return type.accept( this, contextForComplexType );
      }

      final Optional<String> comment = config.includeColumnComments()
            ? Optional.ofNullable( Optional.ofNullable( context.forceDescriptionFromElement() ).orElse( property )
            .getDescription( config.commentLanguage() ) )
            : Optional.empty();
      return column( context.prefix(), type.accept( this, context ), property.isOptional() || context.forceOptional(), comment );
   }

   private String column( final String columnName, final String columnType, final boolean isNullable, final Optional<String> comment ) {
      return "%s %s%s".formatted( columnName, columnType, isNullable ? "" : " NOT NULL" )
            + comment.map( args -> " COMMENT '%s'".formatted( escapeComment( args ) ) ).orElse( "" );
   }

   @Override
   public String visitTrait( final Trait trait, final Context context ) {
      return trait.getBaseCharacteristic().accept( this, context );
   }

   @Override
   public String visitCollection( final Collection collection, final Context context ) {
      final Property property = context.currentProperty();
      final Type type = collection.getDataType().orElseThrow();
      final Optional<String> comment = config.includeColumnComments()
            ? Optional.ofNullable( Optional.ofNullable( context.forceDescriptionFromElement() ).orElse( property )
            .getDescription( config.commentLanguage() ) )
            : Optional.empty();
      final String typeDef = type.isComplexType()
            ? entityToStruct( type.as( ComplexType.class ) ).toString()
            : type.accept( this, context );
      return column( context.prefix(), "ARRAY<" + typeDef + ">", property.isOptional() || context.forceOptional(),
            comment );
   }

   private DatabricksType.DatabricksStruct entityToStruct( final ComplexType entity ) {
      return new DatabricksType.DatabricksStruct( entity.getAllProperties().stream().flatMap( property -> {
               if ( property.getDataType().isEmpty() || property.isNotInPayload() ) {
                  return Stream.empty();
               }
               final Type type = property.getDataType().get();
               final DatabricksType databricksType;
               if ( type instanceof final Scalar scalar ) {
                  databricksType = databricksTypeMap.get( scalar.getUrn() );
               } else if ( type instanceof final Entity entityType ) {
                  databricksType = entityToStruct( entityType );
               } else {
                  return Stream.empty();
               }
               return Stream.of( new DatabricksType.DatabricksStructEntry( columnName( property ), databricksType,
                     !property.isOptional(), Optional.ofNullable( property.getDescription( config.commentLanguage() ) ) ) );
            } )
            .toList() );
   }

   @Override
   public String visitScalar( final Scalar scalar, final Context context ) {
      return databricksTypeMap.get( scalar.getUrn() ).toString();
   }

   @Override
   public String visitEntity( final Entity entity, final Context context ) {
      return visitStructureElement( entity, context );
   }
}
