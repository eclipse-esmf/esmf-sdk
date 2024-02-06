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

package org.eclipse.esmf.staticmetamodel.propertychain;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.staticmetamodel.PropertyAccessor;
import org.eclipse.esmf.staticmetamodel.PropertyTypeInformation;
import org.eclipse.esmf.staticmetamodel.StaticContainerProperty;
import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.propertychain.spi.PropertyChainElementAccessor;

/**
 * Defines a chain of properties to be able to access properties of nested entities as if they were properties of one of the containing
 * entities or aspects.
 * <p>
 * To build instances of property chains it is recommended to use the type safe builders.
 *
 * @param <C> the type containing the property (this is the containing type of the first chain element)
 * @param <P> the type of the property (this is the property type of the last chain element)
 * @see ContainerPropertyChain
 * @see PropertyChainBuilder
 */
public class PropertyChain<C, P> extends DefaultProperty implements PropertyTypeInformation<C, P>, PropertyAccessor<C, P> {
   private final List<StaticProperty<Object, Object>> properties;

   private final PropertyAccessor<C, P> accessor;

   private final List<PropertyChainElementAccessor<Object>> chainElementAccessors;

   PropertyChain( final List<? extends StaticProperty<? extends Object, ? extends Object>> properties,
         final StaticProperty<? extends Object, P> lastProperty ) {
      super( MetaModelBaseAttributes.fromModelElement( lastProperty ),
            lastProperty.getCharacteristic(),
            lastProperty.getExampleValue(), lastProperty.isOptional(), lastProperty.isNotInPayload(),
            Optional.ofNullable( lastProperty.getPayloadName() ),
            lastProperty.isAbstract(), lastProperty.getExtends() );
      this.accessor = getAccessor();
      this.properties = Stream.concat( properties.stream(), Stream.of( lastProperty ) )
            .map( p -> (StaticProperty<Object, Object>) p )
            .toList();
      this.chainElementAccessors = PropertyChainElementAccessors.getAllPropertyAccessors();
   }

   /**
    * Creates a property chain using the given list of properties.
    * <p>
    * <b>Important:</b> This constructor does not ensure that the chain is valid and thus should only be used in situations where this is
    * explicitly known.
    * Prefer to use a builder using {@link #from(StaticProperty)}, {@link #fromOptional(StaticContainerProperty)} (StaticContainerProperty)}
    * or {@link #fromCollection(StaticContainerProperty)}.
    *
    * @param properties
    */
   public PropertyChain( final List<? extends StaticProperty<? extends Object, ? extends Object>> properties ) {
      this( properties.subList( 0, properties.size() - 1 ), (StaticProperty<? extends Object, P>) properties.get( properties.size() - 1 ) );
   }

   /**
    * Creates a typesafe builder for a property chain, starting from the given property.
    *
    * @param firstProperty the first property of the chain
    * @param <F> the type containing the property
    * @param <T> the type of the property
    * @return the property chain builder
    */
   public static <F, T> PropertyChainBuilder.SingleBuilder<F, T> from( final StaticProperty<F, T> firstProperty ) {
      return new PropertyChainBuilder.SingleBuilder<>( List.of( (StaticProperty<?, Object>) firstProperty ) );
   }

   /**
    * Creates a typesafe builder for a property chain, starting from the given container property.
    *
    * @param firstProperty the first property of the chain
    * @param <F> the type containing the property
    * @param <T> the type of the property
    * @param <C> the type of the container containing the property value(s)
    * @return the property chain builder
    */
   public static <F, T, C extends Optional<T>> PropertyChainBuilder.OptionalBuilder<F, C, T> fromOptional(
         final StaticContainerProperty<F, T, C> firstProperty ) {
      return new PropertyChainBuilder.OptionalBuilder<>( List.of( (StaticProperty) firstProperty ) );
   }

   /**
    * Creates a typesafe builder for a property chain, starting from the given collection container property.
    *
    * @param firstProperty the first property of the chain
    * @param <F> the type containing the property
    * @param <T> the type of the property
    * @param <C> the type of the collection containing the property value(s)
    * @return the property chain builder
    */
   public static <F, T, C extends Collection<T>> PropertyChainBuilder.CollectionBuilder<F, C, T> fromCollection(
         final StaticContainerProperty<F, T, C> firstProperty ) {
      return new PropertyChainBuilder.CollectionBuilder<>( List.of( (StaticProperty) firstProperty ) );
   }

   private PropertyAccessor<C, P> getAccessor() {
      return ( C object ) -> {
         Object currentValue = object;
         final Iterator<StaticProperty<Object, Object>> propertyIterator = PropertyChain.this.properties.iterator();
         while ( currentValue != null && propertyIterator.hasNext() ) {
            currentValue = getNextValue( currentValue, propertyIterator.next() );
         }

         return (P) currentValue;
      };
   }

   private Object getNextValue( final Object currentValue, final StaticProperty<Object, Object> property ) {
      if ( currentValue == null ) {
         return null;
      }

      return findPropertyChainElementAccessor( currentValue ).getValue( currentValue, property );
   }

   private PropertyChainElementAccessor<Object> findPropertyChainElementAccessor( final Object currentValue ) {
      return this.chainElementAccessors.stream()
            .filter( chainElementAccessor -> chainElementAccessor.getHandledElementClass().isAssignableFrom( currentValue.getClass() ) )
            .findAny()
            .orElseGet( PropertyChainElementAccessors::getDefaultAccessor );
   }

   @Override
   public Class<P> getPropertyType() {
      return getLastProperty().getPropertyType();
   }

   @Override
   public Class<C> getContainingType() {
      return getFirstProperty().getContainingType();
   }

   @Override
   public P getValue( final C object ) {
      return accessor.getValue( object );
   }

   /**
    * @param <T> the type of the first property
    * @return the first property of the chain
    */
   public <T> StaticProperty<C, T> getFirstProperty() {
      return (StaticProperty<C, T>) properties.get( 0 );
   }

   /**
    * @param <T> the type containing the last property
    * @return the last property of the chain
    */
   public <T> StaticProperty<T, P> getLastProperty() {
      return (StaticProperty<T, P>) properties.get( properties.size() - 1 );
   }

   /**
    * @return all properties of the chain
    */
   public List<StaticProperty<Object, Object>> getProperties() {
      return properties;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      if ( !super.equals( o ) ) {
         return false;
      }
      final PropertyChain<?, ?> that = (PropertyChain<?, ?>) o;
      return Objects.equals( properties, that.properties );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), properties );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", getClass().getSimpleName() + "[", "]" )
            .add( getContainingType().getSimpleName() )
            .add( properties.stream().map( NamedElement::getName ).collect( Collectors.joining( "." ) ) )
            .toString();
   }
}
