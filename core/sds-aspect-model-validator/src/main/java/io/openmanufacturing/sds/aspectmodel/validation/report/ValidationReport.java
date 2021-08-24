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

package io.openmanufacturing.sds.aspectmodel.validation.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Describes the results of an Aspect Model validation.
 */
@SuppressWarnings( { "squid:S1610", "This should not be converted into an interface, because it is a sealed class." } )
public abstract class ValidationReport {
   private ValidationReport() {
   }

   /**
    * Represents a succeeded validation
    */
   public static class ValidReport extends ValidationReport {
      @Override
      public boolean conforms() {
         return true;
      }

      public String toString() {
         return "Validation report: Input model is valid";
      }

      @Override
      public Collection<ValidationError> getValidationErrors() {
         return Collections.emptyList();
      }
   }

   /**
    * Represents a failed validation
    */
   public static class InvalidReport extends ValidationReport {
      private final Collection<ValidationError> validationErrors = new ArrayList<>();

      InvalidReport( final ValidationReportBuilder builder ) {
         validationErrors.addAll( builder.getValidationErrors() );
      }

      @Override
      public boolean conforms() {
         return false;
      }

      @Override
      public Collection<ValidationError> getValidationErrors() {
         return validationErrors;
      }

      @Override
      public String toString() {
         return "Validation report: Validation failed: \n"
               + validationErrors.stream().map( ValidationError::toString ).collect( Collectors.joining( "\n" ) );
      }
   }

   /**
    * Determines, if the validation was successful.
    *
    * @return true, if the validation was successful, false if there are validation results
    */
   public abstract boolean conforms();

   /**
    * Returns an {@link Collection} containing the validation errors, if {@link #conforms()} returns true, otherwise an
    * empty {@link Collection} is returned.
    *
    * @return an {@link Collection} containing the collection of validation errors
    */
   public abstract Collection<ValidationError> getValidationErrors();
}
