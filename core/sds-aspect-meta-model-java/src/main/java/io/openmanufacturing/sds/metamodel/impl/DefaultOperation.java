/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.metamodel.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultOperation extends ModelElementImpl implements Operation {
   private final List<Property> input;
   private final Optional<Property> output;

   public DefaultOperation( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<Property> input,
         final Optional<Property> output ) {
      super( metaModelBaseAttributes );
      this.input = input;
      this.output = output;
   }

   /**
    * A list of input parameters for the Operation. If the operation does not take any input parameters, the input may
    * be omitted.
    *
    * @return the input.
    */
   @Override
   public List<Property> getInput() {
      return input;
   }

   /**
    * The return value of the Operation. If the Operation does not return anything, the output may be omitted.
    *
    * @return the output.
    */
   @Override
   public Optional<Property> getOutput() {
      return output;
   }

   /**
    * Accepts an Aspect visitor
    *
    * @param visitor The visitor to accept
    * @param <T> The result type of the traversal operation
    * @param <C> The context of the visitor traversal
    */
   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitOperation( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultOperation.class.getSimpleName() + "[", "]" )
            .add( "input=" + input )
            .add( "output=" + output )
            .toString();
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      if ( !super.equals( o ) ) {
         return false;
      }
      final DefaultOperation that = (DefaultOperation) o;
      return Objects.equals( input, that.input ) &&
            Objects.equals( output, that.output );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), input, output );
   }
}
