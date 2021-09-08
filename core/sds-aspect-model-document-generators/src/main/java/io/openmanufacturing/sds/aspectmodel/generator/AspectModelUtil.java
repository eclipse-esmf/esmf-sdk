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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.openmanufacturing.sds.metamodel.*;
import io.openmanufacturing.sds.metamodel.visitor.AspectStreamTraversalVisitor;

import org.apache.commons.lang3.StringUtils;

public class AspectModelUtil {

   private AspectModelUtil() {
   }

   public static List<Property> sortPropertiesByPreferredName( final List<Property> properties, final Locale local ) {
      if ( properties != null ) {
         properties.sort( Comparator.comparing( property -> property.getPreferredName( local ) ) );
      }
      return properties;
   }

   public static List<Operation> sortOperationsByPreferredName( final List<Operation> operations, final Locale local ) {
      if ( operations != null ) {
         operations.sort( Comparator.comparing( operation -> operation.getPreferredName( local ) ) );
      }
      return operations;
   }

   public static Set<Constraint> getConstraints( final Property property ) {
      final Set<Constraint> constraints = new HashSet<>();
      if ( property.getCharacteristic() instanceof Trait) {
         constraints.addAll( ((Trait) property.getCharacteristic()).getConstraints() );
      }
      return constraints;
   }

   public static List<ComplexType> resolveEntitiesSortedByPreferredName( final Collection collection,
         final Locale local ) {
      return new AspectStreamTraversalVisitor()
         .visitCollection( collection, null )
         .filter( base -> ComplexType.class.isAssignableFrom( base.getClass() ) )
         .distinct()
         .map( ComplexType.class::cast )
         .sorted( Comparator.comparing( complexType -> complexType.getPreferredName( local ) ) )
         .collect( Collectors.toList() );
   }

   public static List<ComplexType> resolveEntitiesSortedByPreferredName( final SingleEntity singleEntity,
         final Locale local ) {
      return new AspectStreamTraversalVisitor()
         .visitSingleEntity( singleEntity, null )
         .filter( base -> ComplexType.class.isAssignableFrom( base.getClass() ) )
         .distinct()
         .map( ComplexType.class::cast )
         .sorted( Comparator.comparing( complexType -> complexType.getPreferredName( local ) ) )
         .collect( Collectors.toList() );
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
