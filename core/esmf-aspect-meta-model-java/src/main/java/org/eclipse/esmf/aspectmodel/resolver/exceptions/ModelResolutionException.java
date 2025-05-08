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
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

public class ModelResolutionException extends RuntimeException {
   public record LoadingFailure(
         AspectModelUrn element,
         String location,
         String description,
         Optional<Throwable> cause
   ) {
      public LoadingFailure( final AspectModelUrn element, final String location, final String description ) {
         this( element, location, description, Optional.empty() );
      }

      public LoadingFailure( final AspectModelUrn element, final String location, final String description, final Throwable cause ) {
         this( element, location, description, Optional.of( cause ) );
      }
   }

   private final List<LoadingFailure> checkedLocations;

   public ModelResolutionException( final LoadingFailure checkedLocation ) {
      this( List.of( checkedLocation ) );
   }

   public ModelResolutionException( final List<LoadingFailure> checkedLocations ) {
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

   public List<LoadingFailure> getCheckedLocations() {
      return checkedLocations;
   }
}
