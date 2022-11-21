/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator.json;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.RDF;

import com.google.common.collect.ImmutableMap;

import io.openmanufacturing.sds.metamodel.ModelElement;
import io.openmanufacturing.sds.metamodel.CollectionValue;
import io.openmanufacturing.sds.metamodel.EntityInstance;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.datatypes.LangString;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

/**
 * Simple {@link AspectVisitor} that turns a {@link Value} into the corresponding simple Java objects,
 * i.e., {@link ScalarValue}s are turned into their value, {@link CollectionValue}s are turned into {@link List}s of their simple objects,
 * and {@link EntityInstance}s are turned into {@link Map}s of property (payload) name to simple object.
 */
public class ValueToPayloadStructure implements AspectVisitor<Object, Void> {
   @Override
   public Object visitBase( final ModelElement modelElement, final Void context ) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object visitScalarValue( final ScalarValue scalarValue, final Void context ) {
      if ( scalarValue.getType().getUrn().equals( RDF.langString.getURI() ) ) {
         final LangString langString = (LangString) scalarValue.getValue();
         return ImmutableMap.of( langString.getLanguageTag().toLanguageTag(), langString.getValue() );
      }
      return scalarValue.getValue();
   }

   @Override
   public Object visitCollectionValue( final CollectionValue value, final Void context ) {
      return value.getValues().stream().map( v -> v.accept( this, context ) ).collect( Collectors.toList() );
   }

   @Override
   public Object visitEntityInstance( final EntityInstance instance, final Void context ) {
      final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();
      for ( final Property property : instance.getEntityType().getAllProperties() ) {
         if ( property.isNotInPayload() ) {
            continue;
         }
         final Value propertyValue = instance.getAssertions().get( property );
         // The value can be missing of the Property is optional in the Entity
         if ( propertyValue == null ) {
            continue;
         }
         mapBuilder.put( property.getPayloadName(), propertyValue.accept( this, context ) );
      }
      return mapBuilder.build();
   }
}
