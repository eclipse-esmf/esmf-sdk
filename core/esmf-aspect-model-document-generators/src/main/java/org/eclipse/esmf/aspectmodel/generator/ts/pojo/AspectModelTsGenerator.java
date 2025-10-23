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
package org.eclipse.esmf.aspectmodel.generator.ts.pojo;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.generator.ts.QualifiedName;
import org.eclipse.esmf.aspectmodel.generator.ts.TsCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.ts.TsGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Event;

/**
 * Generates Ts Domain classes for an Aspect model and all its contained elements.
 */
public class AspectModelTsGenerator extends TsGenerator {
   public AspectModelTsGenerator( final Aspect aspect, final TsCodeGenerationConfig config ) {
      super( aspect, config );
   }

   @Override
   public Stream<Artifact<QualifiedName, String>> generate() {
      final Set<ComplexType> structureElements = elements( ComplexType.class )
            .filter( element -> element.getExtends().isPresent() )
            .collect( Collectors.toSet() );
      return Stream.of(
                  applyArtifactGenerator( Aspect.class, new StructureElementTsArtifactGenerator<>(), config ),
                  applyArtifactGenerator( ComplexType.class, new StructureElementTsArtifactGenerator<>( structureElements ), config ),
                  applyArtifactGenerator( Event.class, new StructureElementTsArtifactGenerator<>(), config )
                  //                  ,                  applyArtifactGenerator( Enumeration.class, new EnumerationTsArtifactGenerator<>
                  //                  (), config )
            )

            .flatMap( Function.identity() )
            .collect( Collectors.toSet() )
            .stream();
   }
}
