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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.openmanufacturing.sds.metamodel.*;
import org.apache.commons.lang3.StringUtils;

public class AspectModelUtil {

   private static final List<Characteristic> characteristicList = new LinkedList<>();

   private AspectModelUtil() {
   }

   public static List<Property> sortPropertiesByPreferredName( final List<Property> properties, final Locale local ) {
      if ( properties != null ) {
         properties.sort( Comparator.comparing( property -> property.getPreferredName( local ) ) );
      }
      return properties;
   }

   public static List<Entity> sortEntitiesByPreferredName( final List<Entity> entities, final Locale local ) {
      if ( entities != null ) {
         entities.sort( Comparator.comparing( entity -> entity.getPreferredName( local ) ) );
      }
      return entities;
   }

   public static List<Operation> sortOperationsByPreferredName( final List<Operation> operations, final Locale local ) {
      if ( operations != null ) {
         operations.sort( Comparator.comparing( operation -> operation.getPreferredName( local ) ) );
      }
      return operations;
   }

   public static List<Entity> getEntities( final Aspect aspectModel ) {
      final List<Property> properties = getProperties( aspectModel );
      return properties.stream()
                       .filter( property -> property.getDataType().isPresent()
                             && property.getDataType().get() instanceof Entity )
                       .map( property -> (Entity) property.getDataType().get() ).distinct()
                       .collect( Collectors.toList() );
   }

   public static List<Property> getProperties( final Aspect aspectModel ) {
      final List<Property> properties = new ArrayList<>( aspectModel.getProperties() );
      aspectModel.getOperations().forEach( operation -> properties.addAll( getProperties( operation ) ) );
      final Set<Property> characteristicProperties = new HashSet<>();
      properties
            .forEach( property -> characteristicProperties.addAll( getProperties( property.getCharacteristic() ) ) );
      properties.addAll( characteristicProperties );
      return properties;
   }

   public static Set<Property> getProperties( final Operation operation ) {
      return Stream.concat( operation.getInput().stream(), operation.getOutput().stream() )
                   .collect( Collectors.toSet() );
   }

   public static Set<Constraint> getConstraints( final Property property ) {
      final Set<Constraint> constraints = new HashSet<>();
      if ( property.getCharacteristic() instanceof Trait) {
         constraints.addAll( ((Trait) property.getCharacteristic()).getConstraints() );
      }
      return constraints;
   }

   @SuppressWarnings( "squid:S2250" )
   //Amount of elements in list is regarding the amount of properties in aspect model. Even in bigger aspects this should not lead to performance issues
   public static Set<Property> getProperties( final Characteristic characteristic ) {
      if ( characteristicList.contains( characteristic ) ) {
         return Set.of();
      }
      characteristicList.add( characteristic );
      final Optional<Type> dataType = characteristic.getDataType();
      if ( dataType.isPresent() && !(dataType.get() instanceof Entity) ) {
         return Collections.emptySet();
      }
      final Set<Property> properties = dataType.stream()
                                               .map( Entity.class::cast )
                                               .map( HasProperties::getProperties )
                                               .flatMap( Collection::stream )
                                               .collect( Collectors.toSet() );

      final Set<Property> propertiesCharacteristics = new HashSet<>();
      properties.forEach(
            property -> propertiesCharacteristics.addAll( getProperties( property.getCharacteristic() ) ) );
      properties.addAll( propertiesCharacteristics );
      characteristicList.remove( characteristic );
      return properties;
   }

   public static Entity resolveEntity( final SingleEntity singleEntity, final List<Entity> entities ) {
      return entities.stream()
                     .filter( entity -> singleEntity.getDataType()
                                                    .map( Type::getUrn )
                                                    .map( u -> u.equals( entity.getUrn() ) ).orElse( false ) )
                     .findFirst()
                     .orElseThrow( () -> new DocumentGenerationException( "Could not find entity " + singleEntity
                           + " in list of entities: " + entities ) );
   }

   @SuppressWarnings( "squid:S3655" )
   //There won't be many issues in calling without isPresent as everything will be underneath the velocity template and there is no alternative in case it
   // isn't present.
   public static Entity getComplexTypeOfCharacteristic( final Characteristic characteristic ) {
      return (Entity) characteristic.getDataType().get();
   }

   public static Class<?> getClassForObject( final Object o ) {
      return o.getClass().getInterfaces()[0];
   }

   public static boolean isMap( final Object object ) {
      return object instanceof Map;
   }

   public static int increment( final int number ) {
      return number + 1;
   }

   public static String capitalize( final String text ) {
      return StringUtils.capitalize( text );
   }
}
