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

package org.eclipse.esmf.aspectmodel.java;

import java.util.List;

import org.eclipse.esmf.metamodel.characteristic.StructuredValue;
import org.eclipse.esmf.metamodel.Property;

/**
 * Encapsulates a {@link Property} and, if it uses a {@link StructuredValue} characteristic, the corresponding
 * deconstruction rule and the referenced Properties
 */
public class DeconstructionSet {
   private final Property originalProperty;
   private final String deconstructionRule;
   private final List<Property> elementProperties;

   public DeconstructionSet( final Property originalProperty, final String deconstructionRule,
         final List<Property> elementProperties ) {
      this.originalProperty = originalProperty;
      this.deconstructionRule = deconstructionRule;
      this.elementProperties = elementProperties;
   }

   public Property getOriginalProperty() {
      return originalProperty;
   }

   public String getDeconstructionRule() {
      return deconstructionRule;
   }

   public List<Property> getElementProperties() {
      return elementProperties;
   }
}
