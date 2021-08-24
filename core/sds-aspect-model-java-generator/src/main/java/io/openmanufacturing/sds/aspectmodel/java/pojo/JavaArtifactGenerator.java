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

package io.openmanufacturing.sds.aspectmodel.java.pojo;

import io.openmanufacturing.sds.aspectmodel.generator.Artifact;
import io.openmanufacturing.sds.aspectmodel.generator.ArtifactGenerator;
import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.QualifiedName;
import io.openmanufacturing.sds.metamodel.Base;

/**
 * A {@link ArtifactGenerator} specific to Java code: The type identifying artifacts is {@link QualifiedName} and
 * the content type is {@link String}.
 *
 * @param <E> the model element type to generate code for
 */
public interface JavaArtifactGenerator<E extends Base>
      extends ArtifactGenerator<QualifiedName, String, E, JavaCodeGenerationConfig, Artifact<QualifiedName, String>> {
}
