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
import java.util.Collection;
import java.util.Map;
import org.eclipse.esmf.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import org.eclipse.esmf.aspectmodel.generator.docu.AspectModelDocumentationGenerator.HtmlGenerationOption;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectContext;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import java.io.IOException;
// end::imports[]
import org.junit.jupiter.api.Test;

public class GenerateHtml extends AbstractGenerator {
   @Test
   public void generate() throws IOException {
      final ResolutionStrategy strategy = new FileSystemStrategy( Paths.get( "aspect-models" ) );
      // tag::generate[]
      final AspectModelUrn targetAspect
            = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.examples.movement:1.0.0#Movement" );
      // VersionedModel as returned by the AspectModelResolver
      final VersionedModel model = // ...
            // end::generate[]
            new AspectModelResolver().resolveAspectModel( strategy, targetAspect ).get();
      // tag::generate[]
      final Aspect aspect = AspectModelLoader.getAspects( model ).toJavaStream() // <1>
            .flatMap( Collection::stream )
            .filter( theAspect -> theAspect.getAspectModelUrn().map( urn -> urn.equals( targetAspect ) ).orElse( false ) )
            .findFirst().orElseThrow();
      final AspectModelDocumentationGenerator generator = // <2>
            new AspectModelDocumentationGenerator( new AspectContext( model, aspect ) );

      final Map<HtmlGenerationOption, String> options = Map.of(); // <3>
      generator.generate( this::outputStreamForName, options );
      // end::generate[]
   }
}
