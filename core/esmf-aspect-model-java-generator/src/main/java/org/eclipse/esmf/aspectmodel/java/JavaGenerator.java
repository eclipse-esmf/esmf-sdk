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

import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.metamodel.Aspect;

/**
 * Base class for all generators that want to create Java source code.
 */
public abstract class JavaGenerator extends
      AspectGenerator<QualifiedName, String, JavaCodeGenerationConfig, Artifact<QualifiedName, String>> {
   public JavaGenerator( final Aspect aspect, final JavaCodeGenerationConfig config ) {
      super( aspect, config );
   }
}
