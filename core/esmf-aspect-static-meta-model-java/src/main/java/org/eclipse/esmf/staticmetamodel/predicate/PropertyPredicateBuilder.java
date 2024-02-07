package org.eclipse.esmf.staticmetamodel.predicate;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.eclipse.esmf.staticmetamodel.PropertyAccessor;

/**
 * Implementations of different predicate builders depending on the property type.
 *
 * @param <P> the meta property type
 * @param <T> the property type
 */
public class PropertyPredicateBuilder<P extends PropertyAccessor<?, T>, T> {
   P property;

   PropertyPredicateBuilder( final P property ) {
      this.property = property;
   }

   public static class SingleBuilder<C, T> extends PropertyPredicateBuilder<PropertyAccessor<C, T>, T> {
      public SingleBuilder( final PropertyAccessor<C, T> property ) {
         super( property );
      }

      /**
       * Builds a predicate checking whether the property value is equal to the given value.
       *
       * @param referenceValue the reference value
       * @return the predicate
       */
      public Predicate<C> isEqualTo( T referenceValue ) {
         return object -> referenceValue.equals( property.getValue( object ) );
      }

      /**
       * Builds a predicate checking whether the property value is {@code null}.
       *
       * @return the predicate
       */
      public Predicate<C> isNull() {
         return object -> null == property.getValue( object );
      }
   }

   public static class CharSequenceBuilder<C, T extends CharSequence> extends PropertyPredicateBuilder<PropertyAccessor<C, T>, T> {
      public CharSequenceBuilder( final PropertyAccessor<C, T> property ) {
         super( property );
      }

      /**
       * Builds a predicate checking whether the property value matches the given regex.
       *
       * @param regex the regex to check against
       * @return the predicate
       */
      public Predicate<C> matches( String regex ) {
         return object -> Pattern.matches( regex, property.getValue( object ) );
      }

      /**
       * Builds a predicate checking whether the property value contains the given substring.
       *
       * @param substring the substring to check for
       * @return the predicate
       */
      public Predicate<C> contains( String substring ) {
         return object -> property.getValue( object ).toString().contains( substring );
      }
   }

   public static class ComparableBuilder<C, T extends Comparable<T>> extends PropertyPredicateBuilder<PropertyAccessor<C, T>, T> {
      public ComparableBuilder( final PropertyAccessor<C, T> property ) {
         super( property );
      }

      /**
       * Builds a predicate checking whether the property value is strictly less than the given value.
       *
       * @param referenceValue the reference value
       * @return the predicate
       */
      public Predicate<C> lessThan( final T referenceValue ) {
         return object -> referenceValue.compareTo( property.getValue( object ) ) > 0;
      }

      /**
       * Builds a predicate checking whether the property value is less than or equal to the given value.
       *
       * @param referenceValue the reference value
       * @return the predicate
       */
      public Predicate<C> atMost( final T referenceValue ) {
         return object -> referenceValue.compareTo( property.getValue( object ) ) >= 0;
      }

      /**
       * Builds a predicate checking whether the property value is strictly greater than the given value.
       *
       * @param referenceValue the reference value
       * @return the predicate
       */
      public Predicate<C> greaterThan( final T referenceValue ) {
         return object -> referenceValue.compareTo( property.getValue( object ) ) < 0;
      }

      /**
       * Builds a predicate checking whether the property value is greater than or equal to the given value.
       *
       * @param referenceValue the reference value
       * @return the predicate
       */
      public Predicate<C> atLeast( final T referenceValue ) {
         return object -> referenceValue.compareTo( property.getValue( object ) ) <= 0;
      }

      /**
       * Builds a predicate checking whether the property value is within the closed range {@code [lower, upper]}.
       *
       * @param lower the lower end of the range
       * @param upper the upper end of the range
       * @return the predicate
       */
      public Predicate<C> withinClosed( final T lower, final T upper ) {
         return object -> {
            final var propertyValue = property.getValue( object );

            return upper.compareTo( propertyValue ) >= 0 && lower.compareTo( propertyValue ) <= 0;
         };
      }

      /**
       * Builds a predicate checking whether the property value is within the closed-open range {@code [lower, upper)}.
       *
       * @param lower the lower end of the range
       * @param upper the upper end of the range
       * @return the predicate
       */
      public Predicate<C> withinClosedOpen( final T lower, final T upper ) {
         return object -> {
            final var propertyValue = property.getValue( object );

            return upper.compareTo( propertyValue ) > 0 && lower.compareTo( propertyValue ) <= 0;
         };
      }

      /**
       * Builds a predicate checking whether the property value is within the open range {@code (lower, upper)}.
       *
       * @param lower the lower end of the range
       * @param upper the upper end of the range
       * @return the predicate
       */
      public Predicate<C> withinOpen( final T lower, final T upper ) {
         return object -> {
            final var propertyValue = property.getValue( object );

            return upper.compareTo( propertyValue ) > 0 && lower.compareTo( propertyValue ) < 0;
         };
      }

      /**
       * Builds a predicate checking whether the property value is within the open-closed range {@code (lower, upper]}.
       *
       * @param lower the lower end of the range
       * @param upper the upper end of the range
       * @return the predicate
       */
      public Predicate<C> withinOpenClosed( final T lower, final T upper ) {
         return object -> {
            final var propertyValue = property.getValue( object );

            return upper.compareTo( propertyValue ) >= 0 && lower.compareTo( propertyValue ) < 0;
         };
      }
   }

   public static class CollectionBuilder<E, T extends Collection<P>, P>
         extends PropertyPredicateBuilder<PropertyAccessor<E, T>, T> {
      public CollectionBuilder( final PropertyAccessor<E, T> property ) {
         super( property );
      }

      /**
       * Builds a predicate checking whether the collection property value contains the given element.
       *
       * @param element the element
       * @return the predicate
       */
      public Predicate<E> contains( final P element ) {
         return object -> property.getValue( object ).contains( element );
      }

      /**
       * Builds a predicate checking whether the collection property value contains any of the given elements.
       *
       * @param elements the elements
       * @return the predicate
       */
      public Predicate<E> containsAnyOf( final T elements ) {
         return object -> !Collections.disjoint( property.getValue( object ), elements );
      }

      /**
       * Builds a predicate checking whether the collection property value contains all of the given elements.
       *
       * @param elements the elements
       * @return the predicate
       */
      public Predicate<E> containsAllOf( final T elements ) {
         return object -> property.getValue( object ).containsAll( elements );
      }
   }
}
