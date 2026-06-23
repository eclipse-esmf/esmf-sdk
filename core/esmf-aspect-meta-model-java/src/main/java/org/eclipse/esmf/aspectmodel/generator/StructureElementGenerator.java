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

package org.eclipse.esmf.aspectmodel.generator;

import org.eclipse.esmf.metamodel.StructureElement;

public abstract class StructureElementGenerator<S extends StructureElement, I, T, C extends GenerationConfig, A extends Artifact<I, T>>
      extends ModelElementGenerator<S, I, T, C, A> {
   public StructureElementGenerator( final S focus, final C config ) {
      super( focus, config );
   }

   protected S structureElement() {
      return getFocus();
   }
}
