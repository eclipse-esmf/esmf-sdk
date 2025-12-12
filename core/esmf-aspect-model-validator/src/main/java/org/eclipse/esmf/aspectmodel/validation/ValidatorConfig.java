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

package org.eclipse.esmf.aspectmodel.validation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.validation.services.CustomValidator;

public class ValidatorConfig {

   private final boolean disableValidation;
   private final Set<CustomValidator> customValidators;

   private ValidatorConfig( Builder builder ) {
      this.disableValidation = builder.disableValidation;
      this.customValidators = builder.customValidators;
   }

   public ValidatorConfig() {
      this.disableValidation = false;
      this.customValidators = Collections.emptySet();
   }

   public boolean isEnable() {
      return !disableValidation;
   }

   public Set<CustomValidator> getCustomValidators() {
      return customValidators;
   }

   public static class Builder {
      private boolean disableValidation = false;
      private final Set<CustomValidator> customValidators = new HashSet<>();

      public Builder disableValidation( boolean disableValidation ) {
         this.disableValidation = disableValidation;
         return this;
      }

      public Builder addCustomValidator( CustomValidator custom ) {
         if ( custom != null ) {
            this.customValidators.add( custom );
         }
         return this;
      }

      public ValidatorConfig build() {
         return new ValidatorConfig( this );
      }
   }
}
