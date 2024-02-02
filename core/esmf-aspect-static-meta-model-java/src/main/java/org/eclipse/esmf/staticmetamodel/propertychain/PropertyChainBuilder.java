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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.staticmetamodel.ContainerProperty;
import org.eclipse.esmf.staticmetamodel.StaticProperty;

/**
 * A typesafe builder for property chains.
 */
class PropertyChainBuilder {
   List<StaticProperty<?, Object>> properties;

   private PropertyChainBuilder( final List<StaticProperty<?, Object>> properties ) {
      this.properties = new ArrayList<>( properties );
   }

   List<StaticProperty<?, Object>> append( final StaticProperty<?, ?> property ) {
      this.properties.add( (StaticProperty<?, Object>) property );

      return List.copyOf( this.properties );
   }

   /**
    * A builder for simple property chains without any container types in between.
    *
    * @param <F> the type containing the property chain
    * @param <T> the type of the currently last property of the chain
    */
   public static class SingleBuilder<F, T> extends PropertyChainBuilder {
      public SingleBuilder( final List<StaticProperty<?, Object>> properties ) {
         super( properties );
      }

      public <V> SingleBuilder<F, V> via( final StaticProperty<T, V> property ) {
         return new SingleBuilder<>( append( property ) );
      }

      public <C extends Collection<V>, V> CollectionBuilder<F, C, V> viaCollection(final ContainerProperty<V> property ) {
         return new CollectionBuilder<>( append( (StaticProperty<?, ?>) property ) );
      }

      public <L> PropertyChain<F, L> to( final StaticProperty<T, L> lastProperty ) {
         return new PropertyChain<>( this.properties, lastProperty );
      }
   }

   /**
    * A builder for property chains with collection types in between.
    *
    * @param <F> the type containing the property chain
    * @param <T> the type of the currently last property of the chain (the collection type)
    * @param <P> the contained type
    */
   public static class CollectionBuilder<F, T extends Collection<P>, P> extends PropertyChainBuilder {
      public CollectionBuilder( final List<StaticProperty<?, Object>> properties ) {
         super( properties );
      }

      public <V, P extends StaticProperty<?, ?>> CollectionBuilder<F, List<V>, V> via( final P property ) {
         return new CollectionBuilder<>( append( property ) );
      }

      public <L, P extends StaticProperty<?, L>> ContainerPropertyChain<F, List<L>, L> to( final P lastProperty ) {
         return new ContainerPropertyChain<>( append( lastProperty ) );
      }
   }

   /**
    * A builder for property chains with {@link Optional}s in between.
    *
    * @param <F> the type containing the property chain
    * @param <T> the type of the currently last property of the chain (the {@code Optional} type)
    * @param <P> the contained type
    */
   public static class OptionalBuilder<F, T extends Optional<P>, P> extends PropertyChainBuilder {

      public OptionalBuilder( final List<StaticProperty<?, Object>> properties ) {
         super( properties );
      }

      public <V> OptionalBuilder<F, Optional<V>, V> via( final StaticProperty<T, V> property ) {
         return new OptionalBuilder<>( append( property ) );
      }

      public <L> ContainerPropertyChain<F, Optional<L>, L> to( final StaticProperty<P, L> lastProperty ) {
         return new ContainerPropertyChain<>( append( lastProperty ) );
      }
   }
}
