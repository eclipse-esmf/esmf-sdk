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

import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.JavaGenerator;
import org.eclipse.esmf.aspectmodel.java.QualifiedName;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.StructureElement;

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
