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

package org.eclipse.esmf.aspectmodel.generator.ts.metamodel;

import org.eclipse.esmf.aspectmodel.generator.ts.TsCodeGenerationConfig;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.StructureElement;

public record StaticCodeGenerationContext(
      TsCodeGenerationConfig codeGenerationConfig,
      String modelUrnPrefix,
      String characteristicBaseUrn,
      Characteristic currentCharacteristic,
      StructureElement currentElement,
      Property currentProperty
) {
   public StaticCodeGenerationContext withCurrentCharacteristic( final Characteristic characteristic ) {
      return new StaticCodeGenerationContext( codeGenerationConfig(), modelUrnPrefix(), characteristicBaseUrn(), characteristic,
            currentElement(), currentProperty() );
   }

   public StaticCodeGenerationContext withCurrentElement( final StructureElement element ) {
      return new StaticCodeGenerationContext( codeGenerationConfig(), modelUrnPrefix(), characteristicBaseUrn(), currentCharacteristic(),
            element, currentProperty() );
   }

   public StaticCodeGenerationContext withCurrentProperty( final Property property ) {
      return new StaticCodeGenerationContext( codeGenerationConfig(), modelUrnPrefix(), characteristicBaseUrn(), currentCharacteristic(),
            currentElement(), property );
   }
}
