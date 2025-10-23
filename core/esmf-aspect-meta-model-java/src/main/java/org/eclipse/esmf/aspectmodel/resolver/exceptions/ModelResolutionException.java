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
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

public class ModelResolutionException extends RuntimeException {
   public record LoadingFailure(
         Optional<AspectModelUrn> element,
         String location,
         String description,
         Optional<Throwable> cause
   ) {
      public LoadingFailure( final String location, final String description ) {
         this( Optional.empty(), location, description, Optional.empty() );
      }

      public LoadingFailure( final String location, final String description, final Throwable cause ) {
         this( Optional.empty(), location, description, Optional.of( cause ) );
      }

      public LoadingFailure( final AspectModelUrn element, final String location, final String description ) {
         this( Optional.of( element ), location, description, Optional.empty() );
      }

      public LoadingFailure( final AspectModelUrn element, final String location, final String description, final Throwable cause ) {
         this( Optional.of( element ), location, description, Optional.of( cause ) );
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

   @Override
   public String getMessage() {
      if ( getCheckedLocations().isEmpty() ) {
         if ( super.getMessage() == null ) {
            return "Model resolution exception";
         }
         return super.getMessage();
      }
      return getCheckedLocations().stream().map( failure ->
                  "%s (%s)".formatted( failure.description(), failure.location() ) )
            .collect( Collectors.joining( "; " ) );
   }
}
