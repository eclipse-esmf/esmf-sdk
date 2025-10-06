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
package org.eclipse.esmf.aspectmodel.java.pojo;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.JavaGenerator;
import org.eclipse.esmf.aspectmodel.java.QualifiedName;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;

/**
 * Generates Java Domain classes for an Aspect model and all its contained elements.
 */
public class AspectModelJavaGenerator extends JavaGenerator {
   public AspectModelJavaGenerator( final Aspect aspect, final JavaCodeGenerationConfig config ) {
      super( aspect, config );
   }

   @Override
   public Stream<Artifact<QualifiedName, String>> generate() {
      final Set<ComplexType> structureElements = elements( ComplexType.class )
            .filter( element -> element.getExtends().isPresent() )
            .collect( Collectors.toSet() );
      return Stream.of(
                  applyArtifactGenerator( Aspect.class, new StructureElementJavaArtifactGenerator<>(), config ),
                  applyArtifactGenerator( ComplexType.class, new StructureElementJavaArtifactGenerator<>( structureElements ), config ),
                  applyArtifactGenerator( Event.class, new StructureElementJavaArtifactGenerator<>(), config ),
                  applyArtifactGenerator( Enumeration.class, new EnumerationJavaArtifactGenerator<>(), config ) )
            .flatMap( Function.identity() )
            .collect( Collectors.toSet() )
            .stream();
   }
}
