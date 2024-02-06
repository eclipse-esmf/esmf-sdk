package org.eclipse.esmf.staticmetamodel.predicate;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.esmf.staticmetamodel.PropertyAccessor;
import org.eclipse.esmf.staticmetamodel.propertychain.PropertyChain;

/**
 * Provides type safe {@link Predicate}s on properties allowing convenient and expressive filtering.
 *
 * Property predicates can also be used with {@link PropertyChain}s in order to filter on parent elements using nested properties.
 *
 * @see PropertyPredicateBuilder
 */
public final class PropertyPredicates {
   /**
    * Entry point for simple predicates on single valued properties.
    *
    * @param property the property to apply the predicate to
    * @return the builder
    * @param <C> the type containing the property
    * @param <T> the property type
    */
   public static <C, T> PropertyPredicateBuilder.SingleBuilder<C, T> on( final PropertyAccessor<C, T> property ) {
      return new PropertyPredicateBuilder.SingleBuilder<>( property );
   }

   /**
    * Entry point for matching predicates on {@link CharSequence} properties.
    *
    * @param property the property to apply the predicate to
    * @return the builder
    * @param <C> the type containing the property
    * @param <T> the property type
    */
   public static <C, T extends CharSequence> PropertyPredicateBuilder.CharSequenceBuilder<C, T> matchOn( final PropertyAccessor<C, T> property ) {
      return new PropertyPredicateBuilder.CharSequenceBuilder<>( property );
   }

   /**
    * Entry point for predicates on {@link Comparable} properties.
    *
    * @param property the property to apply the predicate to
    * @return the builder
    * @param <C> the type containing the property
    * @param <T> the property type
    */
   public static <C, T extends Comparable<T>> PropertyPredicateBuilder.ComparableBuilder<C, T> compareOn(
         final PropertyAccessor<C, T> property ) {
      return new PropertyPredicateBuilder.ComparableBuilder<>( property );
   }

   /**
    * Entry point for simple predicates on single valued {@link Optional} properties.
    *
    * @param property the property to apply the predicate to
    * @return the builder
    * @param <C> the type containing the property
    * @param <T> the property type
    */
   public static <C, T extends Optional<P>, P> PropertyPredicateBuilder.SingleBuilder<C, P> onOptional(
         final PropertyAccessor<C, T> property ) {
      return new PropertyPredicateBuilder.SingleBuilder<>( object -> property.getValue( object ).orElse( null ) );
   }

   /**
    * Entry point for predicates on collection valued properties.
    *
    * @param property the property to apply the predicate to
    * @return the builder
    * @param <C> the type containing the property
    * @param <T> the property type
    * @param <P> the type contained within the collection
    */
   public static <C, T extends Collection<P>, P> PropertyPredicateBuilder.CollectionBuilder<C, T, P> onCollection(
         final PropertyAccessor<C, T> property ) {
      return new PropertyPredicateBuilder.CollectionBuilder<>( property );
   }
}

