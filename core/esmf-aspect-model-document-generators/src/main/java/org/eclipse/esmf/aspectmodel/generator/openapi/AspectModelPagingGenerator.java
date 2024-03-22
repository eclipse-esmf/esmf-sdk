/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.openapi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.TimeSeries;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.HasProperties;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.vavr.collection.Stream;

class AspectModelPagingGenerator {

   private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelPagingGenerator.class );

   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final String UNSPECIFIC_PAGING_TYPE = "There is more than one option for paging. %s will be used.";
   private static final String NO_PAGING_POSSIBLE = "There is no possible paging type defined in the aspect.";
   private static final String WRONG_TYPE_CHOSEN = "The specified paging type %s is not possible for the given aspect.";

   /**
    * Sets the paging properties for an aspect to an given ObjectNode.
    *
    * @param aspect               The related aspect for the paging properties.
    * @param selectedPagingOption The selected paging option.
    * @param objectNode           The ObjectNode where the properties shall be inserted.
    * @throws IOException In case the root property file can't be loaded.
    */
   public void setPagingProperties( final Aspect aspect, final Optional<PagingOption> selectedPagingOption, final ObjectNode objectNode )
         throws IOException {
      final Set<PagingOption> possiblePagingOptions = getPagingTypesForAspect( aspect );
      validatePaging( selectedPagingOption, possiblePagingOptions );

      if ( selectedPagingOption.isPresent() && possiblePagingOptions.contains( selectedPagingOption.get() ) ) {
         setPagingTypeToPath( selectedPagingOption.get(), objectNode );
         return;
      }
      final PagingOption pagingOption = pickOneOfManyPagingOptions( possiblePagingOptions );
      if ( possiblePagingOptions.size() > 1 && selectedPagingOption.isEmpty() ) {
         final String message = String.format( UNSPECIFIC_PAGING_TYPE, pagingOption );
         LOG.info( message );
      }
      setPagingTypeToPath( pickOneOfManyPagingOptions( possiblePagingOptions ), objectNode );
   }

   /**
    * Sets the paging schema for an aspect to an given ObjectNode.
    *
    * @param aspect               The related aspect for the paging schema.
    * @param selectedPagingOption The selected paging option.
    * @param schemaNode           The ObjectNode where the schema shall be inserted.
    * @throws IOException In case the root schema file can't be loaded.
    */
   public void setSchemaInformationForPaging( final Aspect aspect, final ObjectNode schemaNode,
         final Optional<PagingOption> selectedPagingOption ) throws IOException {
      final Set<PagingOption> possiblePagingOptions = getPagingTypesForAspect( aspect );
      validatePaging( selectedPagingOption, possiblePagingOptions );

      if ( selectedPagingOption.isPresent() && possiblePagingOptions.contains( selectedPagingOption.get() ) ) {
         setSchemaInformation( aspect, selectedPagingOption.get(), schemaNode );
         return;
      }
      final PagingOption pagingOption = pickOneOfManyPagingOptions( possiblePagingOptions );
      if ( possiblePagingOptions.size() > 1 && selectedPagingOption.isEmpty() ) {
         final String message = String.format( UNSPECIFIC_PAGING_TYPE, pagingOption );
         LOG.info( message );
      }
      setSchemaInformation( aspect, pagingOption, schemaNode );
   }

   /**
    * Returns If paging is possible for the given aspect.
    *
    * @param aspect The aspect which shall be proofed.
    * @return True if it is possible, false otherwise.
    */
   public boolean isPagingPossible( final Aspect aspect ) {
      return !getPagingTypesForAspect( aspect ).isEmpty();
   }

   private PagingOption pickOneOfManyPagingOptions( final Set<PagingOption> pagingOptions ) {
      if ( pagingOptions.contains( PagingOption.TIME_BASED_PAGING ) ) {
         return PagingOption.TIME_BASED_PAGING;
      }
      if ( pagingOptions.contains( PagingOption.OFFSET_BASED_PAGING ) ) {
         return PagingOption.OFFSET_BASED_PAGING;
      }
      return PagingOption.CURSOR_BASED_PAGING;
   }

   private void setPagingTypeToPath( final PagingOption pagingOption, final ObjectNode propertiesNode )
         throws IOException {
      final ObjectNode rootNode = getPathRootNode( pagingOption );
      final JsonNode node = rootNode.get( "request" );
      node.forEach( subNode -> propertiesNode.set( UUID.randomUUID().toString(), subNode ) );
   }

   private void setSchemaInformation( final Aspect aspect, final PagingOption pagingOption, final ObjectNode schemaNode )
         throws IOException {
      final ObjectNode node = (ObjectNode) getPathRootNode( pagingOption ).get( "response" );
      schemaNode.set( AspectModelOpenApiGenerator.FIELD_PAGING_SCHEMA, node );

      final ObjectNode itemNode = (ObjectNode) node.get( "properties" ).get( "items" );
      itemNode.set( "items", FACTORY.objectNode().put( "$ref", "#/components/schemas/" + aspect.getName() ) );
   }

   private void validatePaging( final Optional<PagingOption> definedPagingOption, final Set<PagingOption> possiblePagingOptions ) {
      if ( definedPagingOption.isPresent() && !possiblePagingOptions.contains( definedPagingOption.get() ) ) {
         final String errorMessage = String.format( WRONG_TYPE_CHOSEN, definedPagingOption.get() );
         LOG.error( errorMessage );
         throw new IllegalArgumentException( errorMessage );
      }
      if ( possiblePagingOptions.isEmpty() ) {
         LOG.error( NO_PAGING_POSSIBLE );
         throw new IllegalArgumentException( NO_PAGING_POSSIBLE );
      }
   }

   private ObjectNode getPathRootNode( final PagingOption pagingOption ) throws IOException {
      String fileName = switch ( pagingOption ) {
         case TIME_BASED_PAGING -> "TimeBasedPaging.json";
         case CURSOR_BASED_PAGING -> "CursorBasedPaging.json";
         case OFFSET_BASED_PAGING -> "OffsetBasedPaging.json";
         default -> throw new IllegalArgumentException(
               String.format( "There is no file defined for the chosen paging option %s", pagingOption ) );
      };
      try ( final InputStream inputStream = getClass().getResourceAsStream( "/openapi/" + fileName ) ) {
         return (ObjectNode) OBJECT_MAPPER.readTree( IOUtils.toString( inputStream, StandardCharsets.UTF_8 ) );
      }
   }

   private Set<PagingOption> getPagingTypesForAspect( final Aspect aspect ) {
      return new AspectModelPagingVisitor().visitAspect( aspect, null );
   }

   private static class AspectModelPagingVisitor implements AspectVisitor<Set<PagingOption>, Object> {
      private final Map<Characteristic, Integer> characteristicCounter = new HashMap<>();

      @Override
      public Set<PagingOption> visitTimeSeries( final TimeSeries timeSeries, final Object context ) {
         if ( !characteristicCounter.containsKey( timeSeries ) ) {
            characteristicCounter.put( timeSeries, 0 );
         }
         return Set.of( PagingOption.TIME_BASED_PAGING, PagingOption.CURSOR_BASED_PAGING, PagingOption.OFFSET_BASED_PAGING );
      }

      @Override
      public Set<PagingOption> visitBase( final ModelElement modelElement, final Object context ) {
         return Set.of();
      }

      @Override
      public Set<PagingOption> visitAspect( final Aspect aspect, final Object context ) {
         return visitHasProperties( aspect, context );
      }

      @Override
      public Set<PagingOption> visitCollection( final Collection collection, final Object context ) {
         return Set.of( PagingOption.CURSOR_BASED_PAGING, PagingOption.OFFSET_BASED_PAGING );
      }

      @Override
      public Set<PagingOption> visitHasProperties( final HasProperties element, final Object context ) {
         return Stream.ofAll( element.getProperties() )
               .filter( property -> !property.isNotInPayload() )
               .flatMap( property -> visitProperty( property, context ) )
               .collect( Collectors.toSet() );
      }

      @Override
      public Set<PagingOption> visitProperty( final Property property, final Object context ) {
         return property.getCharacteristic().map( characteristic -> characteristic.accept( this, context ) )
               .orElse( Collections.emptySet() );
      }
   }
}
