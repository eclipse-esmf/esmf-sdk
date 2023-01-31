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

package examples;

// tag::imports[]
import java.nio.file.Paths;
import java.util.Map;
import io.openmanufacturing.sds.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.docu.AspectModelDocumentationGenerator.HtmlGenerationOption;
import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.FileSystemStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.ResolutionStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.AspectContext;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
// end::imports[]
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class GenerateHtml extends AbstractGenerator {
   @Test
   public void generate() throws IOException {
      final ResolutionStrategy strategy = new FileSystemStrategy( Paths.get( "aspect-models" ) );
      // tag::generate[]
      final AspectModelUrn targetAspect
            = AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing.examples.movement:1.0.0#Movement" );
      // VersionedModel as returned by the AspectModelResolver
      final VersionedModel model = // ...
            // end::generate[]
            new AspectModelResolver().resolveAspectModel( strategy, targetAspect ).get();
      // tag::generate[]
      final Aspect aspect = AspectModelLoader.getAspects( model ).toJavaStream() // <1>
            .flatMap( aspects -> aspects.stream() )
            .filter( theAspect -> theAspect.getAspectModelUrn().map( urn -> urn.equals( targetAspect ) ).orElse( false ) )
            .findFirst().orElseThrow();
      final AspectModelDocumentationGenerator generator = // <2>
            new AspectModelDocumentationGenerator( new AspectContext( model, aspect ) );

      final Map<HtmlGenerationOption, String> options = Map.of(); // <3>
      generator.generate( this::outputStreamForName, options );
      // end::generate[]
   }
}
