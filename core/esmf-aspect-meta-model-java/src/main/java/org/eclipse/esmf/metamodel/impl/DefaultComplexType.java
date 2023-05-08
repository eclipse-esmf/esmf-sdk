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

package org.eclipse.esmf.metamodel.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.ExtendedAspectContext;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultComplexType extends ModelElementImpl implements ComplexType {

   /**
    * Used to keep track of all {@link ComplexType} instances regardles of whether they are directly or indirectly
    * referenced in the {@link Aspect}.
    */
   private static final WeakHashMap<AspectModelUrn, ComplexType> instances = new WeakHashMap<>();

   private final List<Property> properties;
   private final Optional<ComplexType> _extends;
   private final List<AspectModelUrn> extendingElements;

   protected DefaultComplexType(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         final Optional<ComplexType> _extends,
         final List<AspectModelUrn> extendingElements ) {
      super( metaModelBaseAttributes );
      //noinspection OptionalGetWithoutIsPresent
      instances.put( metaModelBaseAttributes.getUrn().get(), this );
      this.properties = new ArrayList<>( properties );
      this._extends = _extends;
      this.extendingElements = extendingElements;
   }

   /**
    * A list of properties defined in the scope of the Complex Type.
    *
    * @return the properties.
    */
   @Override
   public List<Property> getProperties() {
      return List.copyOf( properties );
   }

   @Override
   public Optional<ComplexType> getExtends() {
      return _extends;
   }

   /**
    * @return all {@link ComplexType} instances from the {@link DefaultComplexType#instances} Map which extend this
    *       Abstract Entity.
    */
   @Deprecated( forRemoval = true )
   @Override
   public List<ComplexType> getExtendingElements() {
      return extendingElements.stream().map( instances::get ).filter( Objects::nonNull ).collect( Collectors.toList() );
   }

   @Override
   public List<ComplexType> getExtendingElements( final ExtendedAspectContext context ) {
      return extendingElements.stream()
            .map( urn -> context.loadedElements().get( urn ) )
            .filter( Objects::nonNull )
            .map( modelElement -> (ComplexType) modelElement )
            .collect( Collectors.toList() );
   }

   public static Map<AspectModelUrn, ComplexType> getInstances() {
      return Collections.unmodifiableMap( instances );
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitComplexType( this, context );
   }
}
