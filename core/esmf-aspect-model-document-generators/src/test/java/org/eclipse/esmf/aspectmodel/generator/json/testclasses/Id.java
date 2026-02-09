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
package org.eclipse.esmf.aspectmodel.generator.json.testclasses;

import java.net.URI;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * Generated class for Unique Identifier.
 *
 */

public class Id {

   @NotNull
   private URI productId;

   @JsonCreator
   public Id( @JsonProperty( value = "productId" ) final URI productId ) {
      super(

      );
      this.productId = productId;
   }

   /**
    * Returns Unique Identifier
    *
    * @return {@link #productId}
    */
   public URI getProductId() {
      return productId;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }

      final Id that = (Id) o;
      return Objects.equals( productId, that.productId );
   }

   @Override
   public int hashCode() {
      return Objects.hash( productId );
   }
}
