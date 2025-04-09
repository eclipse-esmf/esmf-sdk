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

package org.eclipse.esmf.aspectmodel.java.metamodel;

import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.metamodel.Characteristic;

public record StaticCodeGenerationContext(
      JavaCodeGenerationConfig codeGenerationConfig,
      String modelUrnPrefix,
      String characteristicBaseUrn,
      Characteristic currentCharacteristic
) {
   public StaticCodeGenerationContext withCurrentCharacteristic( final Characteristic characteristic ) {
      return new StaticCodeGenerationContext( codeGenerationConfig(), modelUrnPrefix(), characteristicBaseUrn(), characteristic );
   }
}
