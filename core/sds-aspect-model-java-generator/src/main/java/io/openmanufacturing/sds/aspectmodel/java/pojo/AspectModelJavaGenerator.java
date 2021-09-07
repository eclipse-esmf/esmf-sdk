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

import java.util.function.Function;
import java.util.stream.Stream;

import io.openmanufacturing.sds.aspectmodel.generator.Artifact;
import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.aspectmodel.java.JavaGenerator;
import io.openmanufacturing.sds.aspectmodel.java.QualifiedName;
import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;

/**
 * Generates Java Domain classes for an Aspect model and all its contained elements.
 */
public class AspectModelJavaGenerator extends JavaGenerator {
   public AspectModelJavaGenerator( final Aspect aspect, final boolean enableJacksonAnnotations ) {
      this( aspect, aspect.getAspectModelUrn().map( AspectModelUrn::getNamespace )
                          .orElseThrow( () -> new CodeGenerationException(
                                "An Aspect may not be defined as an anonymous node" ) ), enableJacksonAnnotations );
   }

   public AspectModelJavaGenerator( final Aspect aspect, final String javaPackagename,
         final boolean enableJacksonAnnotations ) {
      super( aspect, new JavaCodeGenerationConfig( enableJacksonAnnotations, javaPackagename ) );
   }

   public AspectModelJavaGenerator( final VersionedModel versionedModel, final boolean enableJacksonAnnotations ) {
      this( AspectModelLoader.fromVersionedModelUnchecked( versionedModel ), enableJacksonAnnotations );
   }

   public AspectModelJavaGenerator( final VersionedModel versionedModel, final String javaPackageName,
         final boolean enableJacksonAnnotations ) {
      this( AspectModelLoader.fromVersionedModelUnchecked( versionedModel ), javaPackageName,
            enableJacksonAnnotations );
   }

   @Override
   protected Stream<Artifact<QualifiedName, String>> generateArtifacts() {
      return Stream.of( applyTemplate( Aspect.class, new StructureElementJavaArtifactGenerator<>(), config ),
            applyTemplate( ComplexType.class, new StructureElementJavaArtifactGenerator<>(), config ),
            applyTemplate( Enumeration.class, new EnumerationJavaArtifactGenerator<>(), config ) )
                   .flatMap( Function.identity() );
   }
}
