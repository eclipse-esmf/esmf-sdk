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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionViolation;
import org.eclipse.esmf.aspectmodel.resolver.UnresolvedReferenceViolation;

public class ModelResolutionException extends RuntimeException {
   private final List<Violation> violations;

   public ModelResolutionException( final ModelResolutionViolation checkedLocation ) {
      this( List.of( checkedLocation ) );
   }

   public ModelResolutionException( final List<? extends Violation> violations ) {
      this.violations = List.copyOf( violations );
   }

   public ModelResolutionException( final String message ) {
      super( message );
      violations = List.of();
   }

   public ModelResolutionException( final String message, final Throwable cause ) {
      super( message, cause );
      violations = List.of();
   }

   public List<ModelResolutionViolation> getCheckedLocations() {
      final Set<ModelResolutionViolation> checkedLocations = new LinkedHashSet<>();
      for ( final Violation violation : violations ) {
         if ( violation instanceof final ModelResolutionViolation attempt ) {
            checkedLocations.add( attempt );
         } else if ( violation instanceof final UnresolvedReferenceViolation reference ) {
            checkedLocations.addAll( reference.checkedLocations() );
         }
      }
      return List.copyOf( checkedLocations );
   }

   public List<Violation> getViolations() {
      return violations;
   }

   @Override
   public String getMessage() {
      final List<ModelResolutionViolation> checkedLocations = getCheckedLocations();
      if ( checkedLocations.isEmpty() ) {
         if ( super.getMessage() == null ) {
            return "Model resolution exception";
         }
         return super.getMessage();
      }
      return checkedLocations.stream().map( ModelResolutionViolation::message ).collect( Collectors.joining( "; " ) );
   }
}
