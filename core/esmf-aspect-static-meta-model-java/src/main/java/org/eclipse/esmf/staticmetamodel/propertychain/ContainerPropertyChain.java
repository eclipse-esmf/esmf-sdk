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
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.staticmetamodel.ContainerProperty;
import org.eclipse.esmf.staticmetamodel.StaticProperty;

/**
 * A {@link PropertyChain} that holds chain elements of type {@link ContainerProperty}, e.g. {@link Optional} or {@link Collection}.
 *
 * The first container type within the chain is the one propagated to the end of the chain, i.e. if somewhere an {@code Optional} appears,
 * the final property type will also be an optional. The same holds true for {@code Collection}s in between. However, independent from the
 * collection types that are found within the chain, the final collection type will always be a {@link List}.
 *
 * @param <C> the type containing the property (this is the containing type of the first chain element)
 * @param <P> the type of the property (this is the property type of the last chain element)
 * @param <T> the contained type
 * @see ContainerPropertyChain
 * @see PropertyChainBuilder
 */
public class ContainerPropertyChain<C, P, T> extends PropertyChain<C, P> implements ContainerProperty<T> {

   public ContainerPropertyChain( final List<? extends StaticProperty<? extends Object, ? extends Object>> properties ) {
      super( properties );
   }

   @Override
   public Class<T> getContainedType() {
      return (getLastProperty() instanceof ContainerProperty container) ?
            container.getContainedType() :
            (Class<T>) getLastProperty().getPropertyType();
   }

   @Override
   public Class<P> getPropertyType() {
      return (Class<P>) getProperties().stream().filter( p -> p instanceof ContainerProperty<?> )
            .findFirst()
            .map( StaticProperty::getPropertyType )
            .orElseThrow( () -> new IllegalStateException( "Invalid container property chain: no container property found in chain!" ) );
   }
}
