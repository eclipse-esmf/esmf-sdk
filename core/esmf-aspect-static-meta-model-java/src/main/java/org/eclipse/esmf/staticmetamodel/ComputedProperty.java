/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.staticmetamodel;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;

/**
 * Allows ad-hoc definitions of static properties that wrap another property and compute their value using a given function, taking the
 * original property value as an input. The computed property value also can be of a different type.
 * <p>
 * A typical example would be a unit conversion, e.g. transforming a property value that is given in {@code kg} into a value that is given
 * in {@code gram}, by using the function {@code v -> v * 1000}.
 * <p>
 * Using computation functions that use further input it is also possible to create dynamic computed properties. It is however not
 * recommended to build functions where the <i>result type</i> is dynamic.
 *
 * @param <C> the type containing the property
 * @param <R> the type of the computation result
 */
public class ComputedProperty<C, R> extends StaticProperty<C, R> {
   private PropertyAccessor<C, ?> accessor;

   private Function<Object, R> computation;

   private <T> ComputedProperty( final DefaultProperty wrappedProperty, final PropertyAccessor<C, T> accessor,
         final Function<T, R> computation ) {
      super( MetaModelBaseAttributes.fromModelElement( wrappedProperty ),
            wrappedProperty.getCharacteristic().get(),
            wrappedProperty.getExampleValue(), wrappedProperty.isOptional(), wrappedProperty.isNotInPayload(),
            Optional.ofNullable( wrappedProperty.getPayloadName() ),
            wrappedProperty.isAbstract(), wrappedProperty.getExtends()
      );
      this.accessor = accessor;
      this.computation = (Function<Object, R>) computation;
   }

   private <T> ComputedProperty( final StaticProperty<C, T> wrappedProperty, final Function<T, R> computation ) {
      this( wrappedProperty, wrappedProperty, computation );
   }

   /**
    * Creates a {@code ComputedProperty} from the given {@link StaticProperty}, applying the given {@link Function}.
    *
    * @param wrappedProperty the static property to wrap
    * @param computation the computation function to apply to the property value
    * @param <C> the type containing the wrapped property
    * @param <T> the type of the wrapped property
    * @param <R> the type of the computed property, i.e. of the computation result
    * @return the computed property
    */
   public static <C, T, R> ComputedProperty<C, R> of( final StaticProperty<C, T> wrappedProperty, final Function<T, R> computation ) {
      return new ComputedProperty<>( wrappedProperty, computation );
   }

   /**
    * Creates a {@code ComputedProperty} from the given {@link DefaultProperty} and an accompanying {@link PropertyAccessor}, applying the
    * given {@link Function}.
    *
    * @param wrappedProperty the property to wrap
    * @param accessor the property accessor to use on the wrapped property, as a {@code DefaultProperty} does not allow property access
    * @param computation the computation function to apply to the property value
    * @param <C> the type containing the wrapped property
    * @param <T> the type of the wrapped property
    * @param <R> the type of the computed property, i.e. of the computation result
    * @return the computed property
    */
   public static <C, T, R> ComputedProperty<C, R> of( final DefaultProperty wrappedProperty, final PropertyAccessor<C, T> accessor,
         final Function<T, R> computation ) {
      return new ComputedProperty<>( wrappedProperty, accessor, computation );
   }

   @Override
   public Class<R> getPropertyType() {
      if ( accessor instanceof StaticProperty<C, ?> staticProperty ) {
         return (Class<R>) staticProperty.getPropertyType();
      }
      return (Class<R>) Object.class;
   }

   @Override
   public Class<C> getContainingType() {
      if ( accessor instanceof StaticProperty<C, ?> staticProperty ) {
         return staticProperty.getContainingType();
      }
      return super.getContainingType();
   }

   @Override
   public R getValue( C object ) {
      return computation.apply( accessor.getValue( object ) );
   }
}
