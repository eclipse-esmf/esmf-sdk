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
package org.eclipse.esmf.aspectmodel.validation.report;

import java.util.ArrayList;
import java.util.Collection;

@Deprecated( forRemoval = true )
public class ValidationReportBuilder {

   private final Collection<ValidationError> validationErrors = new ArrayList<>();

   public ValidationReport.ValidReport buildValidReport() {
      return new ValidationReport.ValidReport();
   }

   public ValidationReport.InvalidReport buildInvalidReport() {
      return new ValidationReport.InvalidReport( this );
   }

   public <T extends ValidationError> ValidationReportBuilder withValidationErrors(
         final Collection<T> validationErrors ) {
      this.validationErrors.addAll( validationErrors );
      return this;
   }

   Collection<ValidationError> getValidationErrors() {
      return validationErrors;
   }
}
