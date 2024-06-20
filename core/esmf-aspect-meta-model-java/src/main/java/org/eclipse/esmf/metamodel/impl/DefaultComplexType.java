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
import java.util.Objects;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;

public class DefaultComplexType extends ModelElementImpl implements ComplexType {
   protected final List<Property> properties;
   @SuppressWarnings( "checkstyle:MemberName" )
   protected final Optional<ComplexType> extends_;
   protected final List<AspectModelUrn> extendingElements;
   protected final ModelElementFactory loadedElements;

   protected DefaultComplexType(
         final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         @SuppressWarnings( "checkstyle:ParameterName" ) final Optional<ComplexType> extends_,
         final List<AspectModelUrn> extendingElements,
         final ModelElementFactory loadedElements ) {
      super( metaModelBaseAttributes );
      this.properties = new ArrayList<>( properties );
      this.extends_ = extends_;
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
      return extends_;
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

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final DefaultComplexType that = (DefaultComplexType) o;
      return Objects.equals( properties, that.properties ) && Objects.equals( extends_, that.extends_ )
            && Objects.equals( extendingElements, that.extendingElements );
   }

   @Override
   public int hashCode() {
      return Objects.hash( properties, extends_, extendingElements );
   }
}
