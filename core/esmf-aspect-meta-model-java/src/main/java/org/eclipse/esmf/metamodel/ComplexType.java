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

package org.eclipse.esmf.metamodel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines the data type of a {@link Characteristic} as being a complex value.
 */
public interface ComplexType extends Type, StructureElement {

   /**
    * @return a {@link java.util.List} of {@link ComplexType}s which extend this Entity
    */
   default List<ComplexType> getExtendingElements() {
      return Collections.emptyList();
   }

   default boolean isAbstractEntity() {
      return false;
   }

   /**
    * @return all {@link Property}s defined in the context of this Complex Type as well as all extended Complex Types
    */
   default List<Property> getAllProperties() {
      if ( getExtends().isPresent() ) {
         return Stream.of( getProperties(), getExtends().get().getAllProperties() ).flatMap( Collection::stream )
               .collect( Collectors.toList() );
      }
      return List.copyOf( getProperties() );
   }

   @Override
   default String getUrn() {
      return getAspectModelUrn().get().toString();
   }

   /**
    * @return the {@link ComplexType} that is extended by this Complex Type, if present
    */
   default Optional<ComplexType> getExtends() {
      return Optional.empty();
   }

   @Override
   default boolean isComplexType() {
      return true;
   }
}
