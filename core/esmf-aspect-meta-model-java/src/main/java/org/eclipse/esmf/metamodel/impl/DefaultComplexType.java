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
import java.util.Optional;
import java.util.WeakHashMap;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultComplexType extends ModelElementImpl implements ComplexType {

   /**
    * Used to keep track of all {@link ComplexType} instances regardles of whether they are directly or indirectly
    * referenced in the {@link Aspect}.
    */
   protected static WeakHashMap<AspectModelUrn, ComplexType> instances = new WeakHashMap<>();

   private final List<Property> properties;
   private final Optional<ComplexType> _extends;

   protected static DefaultComplexType createDefaultComplexType( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties, final Optional<ComplexType> _extends ) {
      final DefaultComplexType defaultComplexType = new DefaultComplexType( metaModelBaseAttributes, properties, _extends );
      instances.put( metaModelBaseAttributes.getUrn().get(), defaultComplexType );
      return defaultComplexType;
   }

   protected DefaultComplexType( final MetaModelBaseAttributes metaModelBaseAttributes, final List<? extends Property> properties,
         final Optional<ComplexType> _extends ) {
      super( metaModelBaseAttributes );
      this.properties = new ArrayList<>( properties );
      this._extends = _extends;
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

   public static Map<AspectModelUrn, ComplexType> getInstances() {
      return Collections.unmodifiableMap( instances );
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitComplexType( this, context );
   }
}
