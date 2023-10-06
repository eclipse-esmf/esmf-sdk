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
package org.eclipse.esmf.metamodel.entity;

import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

import java.util.*;

public class OperationEntity extends ModelEntityImpl implements Operation {
   private List<Property> input;
   private Optional<Property> output;

   public OperationEntity( MetaModelBaseAttributes metaModelBaseAttributes,
                           List<PropertyEntity> input,
                           Optional<Property> output ) {
      super( metaModelBaseAttributes );
      this.input = new ArrayList<>( input );
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
      return new StringJoiner( ", ", OperationEntity.class.getSimpleName() + "[", "]" )
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
      final OperationEntity that = (OperationEntity) o;
      return Objects.equals( input, that.input ) &&
            Objects.equals( output, that.output );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), input, output );
   }

   public void setInput(List<Property> input) {
      this.input = input;
   }

   public void setOutput(Optional<Property> output) {
      this.output = output;
   }
}
