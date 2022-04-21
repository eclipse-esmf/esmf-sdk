/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.visitor.AspectStreamTraversalVisitor;

public class AspectModelHelper {
   private final VersionedModel modelVersion;

   public AspectModelHelper( VersionedModel modelVersion ) {
      this.modelVersion = modelVersion;
   }

   public VersionedModel getModelVersion() {
      return this.modelVersion;
   }

   public static List<Property> sortPropertiesByPreferredName( final List<Property> properties, final Locale locale ) {
      if ( properties != null ) {
         properties.sort( Comparator.comparing( property -> property.getPreferredName( locale ) ) );
      }
      return properties;
   }

   public static List<ComplexType> sortEntitiesByPreferredName( final List<ComplexType> entities, final Locale locale ) {
      if ( entities != null ) {
         entities.sort( Comparator.comparing( entity -> entity.getPreferredName( locale ) ) );
      }
      return entities;
   }

   public static List<Operation> sortOperationsByPreferredName( final List<Operation> operations, final Locale locale ) {
      if ( operations != null ) {
         operations.sort( Comparator.comparing( operation -> operation.getPreferredName( locale ) ) );
      }
      return operations;
   }

   public static List<ComplexType> getEntities( final Aspect aspectModel ) {
      return new AspectStreamTraversalVisitor()
            .visitAspect( aspectModel, null )
            .filter( base -> ComplexType.class.isAssignableFrom( base.getClass() ) )
            .distinct()
            .map( ComplexType.class::cast )
            .collect( Collectors.toList() );
   }

   public static Set<Constraint> getConstraints( final Property property ) {
      final Set<Constraint> constraints = new HashSet<>();
      if ( property.getCharacteristic() instanceof Trait ) {
         constraints.addAll( ((Trait) property.getCharacteristic()).getConstraints() );
      }
      return constraints;
   }

   public static ComplexType resolveEntity( final SingleEntity singleEntity, final List<ComplexType> entities ) {
      return entities.stream()
            .filter( entity -> singleEntity.getDataType()
                  .map( Type::getUrn )
                  .map( u -> u.equals( entity.getUrn() ) ).orElse( false ) )
            .findFirst()
            .orElseThrow( () -> new DocumentGenerationException( "Could not find entity " + singleEntity
                  + " in list of entities: " + entities ) );
   }

   public static String getNameFromURN( final String urn ) {
      String[] parts = urn.split( "#" );
      return parts.length == 2 ? parts[1] : urn;
   }

   public static Class<?> getClassForObject( final Object o ) {
      Class<?>[] interfaces = o.getClass().getInterfaces();
      if ( interfaces.length <= 0 ) {
         return Object.class;
      }
      return interfaces[0];
   }

   public static boolean isProperty( final Object object ) {
      return object instanceof Property;
   }

   public static int increment( final int number ) {
      return number + 1;
   }
}
