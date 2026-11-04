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
package org.eclipse.esmf.aspectmodel.generator.ts;

import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.metamodel.Aspect;

/**
 * Base class for all generators that want to create Typescript source code.
 */
public abstract class TsGenerator extends
      AspectGenerator<QualifiedName, String, TsCodeGenerationConfig, Artifact<QualifiedName, String>> {
   protected TsGenerator( final Aspect aspect, final TsCodeGenerationConfig config ) {
      super( aspect, config );
   }
}
