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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.generator.ts.QualifiedName;
import org.eclipse.esmf.aspectmodel.generator.ts.TsCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.ts.TsGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.StructureElement;

/**
 * Generates Ts classes representing the static meta model for an Aspect.
 */
public class StaticMetaModelTsGenerator extends TsGenerator {
   public StaticMetaModelTsGenerator( final Aspect aspect, final TsCodeGenerationConfig config ) {
      super( aspect, config );
   }

   @Override
   public Stream<Artifact<QualifiedName, String>> generate() {
      final StaticMetaModelTsArtifactGenerator<StructureElement> template = new StaticMetaModelTsArtifactGenerator<>();
      return applyArtifactGenerator( StructureElement.class, template, config )
            .collect( Collectors.toSet() )
            .stream();
   }
}
