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
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultComplexType extends ModelElementImpl implements ComplexType {
   private final List<Property> properties;
   private final Optional<ComplexType> _extends;
   private final List<AspectModelUrn> extendingElements;
   private final ModelElementFactory loadedElements;

   protected DefaultComplexType(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         final Optional<ComplexType> _extends,
         final List<AspectModelUrn> extendingElements,
         final ModelElementFactory loadedElements ) {
      super( metaModelBaseAttributes );
      this.properties = new ArrayList<>( properties );
      this._extends = _extends;
      this.extendingElements = extendingElements;
      this.loadedElements = loadedElements;
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
    * @return all {@link ComplexType} instances which extend this Abstract Entity.
    */
   @Override
   public List<ComplexType> getExtendingElements() {
      if ( loadedElements == null ) {
         throw new RuntimeException( "No inheritance information is available." );
      }
      return loadedElements.getExtendingElements( extendingElements );
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitComplexType( this, context );
   }
}
