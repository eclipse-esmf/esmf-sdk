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
package io.openmanufacturing.sds.aspectmodel.java.metamodel;

import java.util.stream.Stream;

import io.openmanufacturing.sds.aspectmodel.generator.Artifact;
import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.JavaGenerator;
import io.openmanufacturing.sds.aspectmodel.java.QualifiedName;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.StructureElement;

/**
 * Generates Java classes representing the static meta model for an Aspect.
 */
public class StaticMetaModelJavaGenerator extends JavaGenerator {
   public StaticMetaModelJavaGenerator( final Aspect aspect, final JavaCodeGenerationConfig config ) {
      super( aspect, config );
   }
   @Override
   protected Stream<Artifact<QualifiedName, String>> generateArtifacts() {
      final StaticMetaModelJavaArtifactGenerator<StructureElement> template = new StaticMetaModelJavaArtifactGenerator<>();
      return applyTemplate( StructureElement.class, template, config );
   }
}
