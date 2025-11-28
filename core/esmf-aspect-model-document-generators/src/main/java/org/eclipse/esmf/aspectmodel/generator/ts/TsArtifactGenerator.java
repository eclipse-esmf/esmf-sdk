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
import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.metamodel.ModelElement;

/**
 * A {@link ArtifactGenerator} specific to Typescript code: The type identifying artifacts is {@link QualifiedName} and
 * the content type is {@link String}.
 *
 * @param <E> the model element type to generate code for
 */
public interface TsArtifactGenerator<E extends ModelElement>
      extends ArtifactGenerator<QualifiedName, String, E, TsCodeGenerationConfig, Artifact<QualifiedName, String>> {
}
