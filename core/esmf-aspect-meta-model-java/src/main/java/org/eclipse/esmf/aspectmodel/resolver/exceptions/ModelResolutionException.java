/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionViolation;

public class ModelResolutionException extends RuntimeException {
   private final List<ModelResolutionViolation> checkedLocations;

   public ModelResolutionException( final ModelResolutionViolation checkedLocation ) {
      this( List.of( checkedLocation ) );
   }

   public ModelResolutionException( final List<ModelResolutionViolation> checkedLocations ) {
      this.checkedLocations = checkedLocations;
   }

   public ModelResolutionException( final String message ) {
      super( message );
      checkedLocations = List.of();
   }

   public ModelResolutionException( final String message, final Throwable cause ) {
      super( message, cause );
      checkedLocations = List.of();
   }

   public List<ModelResolutionViolation> getCheckedLocations() {
      return checkedLocations;
   }

   @Override
   public String getMessage() {
      if ( getCheckedLocations().isEmpty() ) {
         if ( super.getMessage() == null ) {
            return "Model resolution exception";
         }
         return super.getMessage();
      }
      return getCheckedLocations().stream().map( ModelResolutionViolation::message ).collect( Collectors.joining( "; " ) );
   }
}
