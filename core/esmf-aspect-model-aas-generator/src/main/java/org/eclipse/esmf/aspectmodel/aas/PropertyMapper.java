/*
 * Copyright (c) 2021-2023 Robert Bosch Manufacturing Solutions GmbH
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
package org.eclipse.esmf.aspectmodel.aas;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;

import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

/**
 * Base interface for any class that can map a property to a {@link SubmodelElement}.
 *
 * @param <T> the concrete type of {@link SubmodelElement} the implementing mapper produces
 */
public interface PropertyMapper<T extends SubmodelElement> extends Comparable<PropertyMapper<T>> {
   static String UNKNOWN_TYPE = "Unknown";

   static String UNKNOWN_EXAMPLE = "";

   /**
    * Performs the mapping of the given property to a AAS {@link SubmodelElement}.
    *
    * @param type the type of the given property
    * @param property the property to map
    * @param context the current visitor context
    * @return the newly created {@link SubmodelElement}
    */
   T mapToAasProperty( Type type, Property property, Context context );

   /**
    * Whether this {@code PropertyMapper} can handle the given property. Defaults to {@code true}, implementors should override.
    *
    * @param property the property to test
    * @return {@code true} if this property mapper can handle the given property, {@code false} else
    */
   default boolean canHandle( final Property property ) {
      return true;
   }

   /**
    * Returns the ordering value for this property mapper.
    *
    * <p>The order is used to determine the correct mapper if multiple matches can occur. By default mappers have
    * {@link Integer#MAX_VALUE} applied as their order value, meaning they will be sorted to the very end.
    *
    * <p>One example for the need of a proper ordering is, if a general mapper for a specific property type is used, but an even more
    * specific mapper should be used for one exact property, that also has this type.
    *
    * @return the order value
    */
   default int getOrder() {
      return Integer.MAX_VALUE;
   }

   @Override
   default int compareTo( PropertyMapper<T> otherPropertyMapper ) {
      return Integer.compare( getOrder(), otherPropertyMapper.getOrder() );
   }

   /**
    * Builds a concept description reference for the given property.
    *
    * @param property the property to build the reference for
    * @return the newly created reference
    */
   default Reference buildReferenceToGlobalReference( final Property property ) {
      final Key key = new DefaultKey.Builder()
            .type( KeyTypes.GLOBAL_REFERENCE )
            .value( determineIdentifierFor( property ) )
            .build();
      return new DefaultReference.Builder().type( ReferenceTypes.EXTERNAL_REFERENCE ).keys( key ).build();
   }

   /**
    * Determines the identifier for the given {@link NamedElement}.
    *
    * @param element the element to get the identifier for
    * @return the identifier
    */
   default String determineIdentifierFor( final NamedElement element ) {
      return element.getAspectModelUrn()
            .map( AspectModelUrn::toString )
            .orElseGet( element::getName );
   }
}
