/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.test.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.error.OptionalShouldBePresent.shouldBePresent;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;

import org.apache.jena.datatypes.RDFDatatype;

/**
 * Base class for Characteristic asserts.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public abstract class AbstractCharacteristicAssert<SELF extends AbstractCharacteristicAssert<SELF, ACTUAL>,
      ACTUAL extends Characteristic> extends ModelElementAssert<SELF, ACTUAL> {
   protected AbstractCharacteristicAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
      super( actual, selfType, modelElementType );
   }

   public SELF hasDataType( final Type type ) {
      assertThat( actual.getDataType() ).isNotEmpty().contains( type );
      return myself;
   }

   public SELF hasDataType( final RDFDatatype type ) {
      return hasDataType( new DefaultScalar( type.getURI() ) );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends TypeAssert<S, A>, A extends Type> TypeAssert<S, A> dataType() {
      if ( actual.getDataType().isEmpty() ) {
         throwAssertionError( shouldBePresent( actual ) );
      }
      return new TypeAssert<>( (A) actual.getDataType().orElseThrow() );
   }
}
