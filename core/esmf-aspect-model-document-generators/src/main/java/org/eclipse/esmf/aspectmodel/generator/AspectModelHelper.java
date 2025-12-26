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

package org.eclipse.esmf.aspectmodel.generator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.visitor.AspectStreamTraversalVisitor;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.characteristic.Trait;

public class AspectModelHelper {
   public List<Property> sortPropertiesByPreferredName( final List<Property> properties, final Locale locale ) {
      if ( properties != null ) {
         properties.sort( Comparator.comparing( property -> property.getPreferredName( locale ) ) );
      }
      return properties;
   }

   public List<ComplexType> sortEntitiesByPreferredName( final List<ComplexType> entities, final Locale locale ) {
      if ( entities != null ) {
         entities.sort( Comparator.comparing( entity -> entity.getPreferredName( locale ) ) );
      }
      return entities;
   }

   public List<Operation> sortOperationsByPreferredName( final List<Operation> operations, final Locale locale ) {
      if ( operations != null ) {
         operations.sort( Comparator.comparing( operation -> operation.getPreferredName( locale ) ) );
      }
      return operations;
   }

   public List<ComplexType> getEntities( final Aspect aspectModel ) {
      return new AspectStreamTraversalVisitor()
            .visitAspect( aspectModel, null )
            .filter( base -> ComplexType.class.isAssignableFrom( base.getClass() ) )
            .distinct()
            .map( ComplexType.class::cast )
            .collect( Collectors.toList() );
   }

   public Set<Constraint> getConstraints( final Property property ) {
      final Set<Constraint> constraints = new HashSet<>();
      property.getCharacteristic().filter( characteristic -> characteristic.is( Trait.class ) )
            .map( characteristic -> characteristic.as( Trait.class ) )
            .ifPresent( trait -> constraints.addAll( trait.getConstraints() ) );
      return constraints;
   }

   public ComplexType resolveEntity( final SingleEntity singleEntity, final List<ComplexType> entities ) {
      return entities.stream()
            .filter( entity -> singleEntity.getDataType()
                  .map( Type::getUrn )
                  .map( u -> u.equals( entity.getUrn() ) ).orElse( false ) )
            .findFirst()
            .orElseThrow( () -> new DocumentGenerationException( "Could not find entity " + singleEntity
                  + " in list of entities: " + entities ) );
   }

   public String getNameFromUrn( final String urn ) {
      final String[] parts = urn.split( "#" );
      return parts.length == 2 ? parts[1] : urn;
   }

   public Class<?> getClassForObject( final Object o ) {
      final Class<?>[] interfaces = o.getClass().getInterfaces();
      if ( interfaces.length <= 0 ) {
         return Object.class;
      }
      return interfaces[0];
   }

   public boolean isProperty( final Object object ) {
      return object instanceof Property;
   }

   public int increment( final int number ) {
      return number + 1;
   }

   private String namespaceAnchorPart( final ModelElement modelElement ) {
      return Optional.ofNullable( modelElement )
            .map( ModelElement::urn )
            .map( urn -> urn.getNamespaceMainPart().replace( ".", "-" ) ).orElse( "" );
   }

   public String buildAnchor( final ModelElement modelElement, final ModelElement parentElement, final String suffix ) {
      final String parentNamespaceAnchorPart = namespaceAnchorPart( parentElement );
      final String parentPart = suffix.equals( "property" ) ? parentNamespaceAnchorPart + "-"
            + Optional.ofNullable( parentElement ).map( ModelElement::getName ).orElse( "" ) + "-" : "";

      if ( modelElement.is( Property.class ) ) {
         final Property property = modelElement.as( Property.class );
         if ( property.getExtends().isPresent() ) {
            // The Property actually extends another (possibly Abstract) Property, so it won't have an Aspect Model URN on its own.
            // Use the parent element's namespace for the anchor.
            return parentPart + parentNamespaceAnchorPart + "-" + property.getName() + "-" + suffix;
         }
      }

      return parentPart + namespaceAnchorPart( modelElement ) + "-" + modelElement.getName() + "-" + suffix;
   }

   public List<Event> sortEventsByPreferredName( final List<Event> events, final Locale locale ) {
      if ( events != null ) {
         events.sort( Comparator.comparing(
               event -> eventSortKey( event, locale ),
               String.CASE_INSENSITIVE_ORDER
         ) );
      }
      return events;
   }

   private static String eventSortKey( final Event e, final Locale l ) {
      return Optional.ofNullable( e )
            .map( x -> Optional.ofNullable( x.getPreferredName( l ) ).filter( s -> !s.isBlank() ).orElse( x.getName() ) )
            .map( String::trim )
            .filter( s -> !s.isBlank() )
            .orElse( "" );
   }
}
