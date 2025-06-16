/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;

import io.vavr.control.Either;
import io.vavr.control.Try;

/**
 * The AspectModelUrnInputHandler knows how to load Aspect Models if the given input is an Aspect Model URN
 */
public class AspectModelUrnInputHandler extends AbstractInputHandler {
   private final AspectModelUrn urn;

   public AspectModelUrnInputHandler( final String input, final ResolverConfigurationMixin resolverConfig, final boolean details,
         final boolean validate ) {
      super( input, resolverConfig, details, validate );
      urn = urnFromInput( input );
   }

   private AspectModelUrn urnFromInput( final String input ) {
      return AspectModelUrn.from( input ).getOrElseThrow( cause -> {
         throw new CommandException( "Aspect Model URN " + input + " is invalid", cause );
      } );
   }

   @Override
   protected List<ResolutionStrategy> resolutionStrategies() {
      final boolean noName = urn.getName().isEmpty();
      final boolean noResolverConfig = resolverConfig == null;
      final boolean noGitHubResolver = noResolverConfig || resolverConfig.gitHubResolverOptions == null
            || resolverConfig.gitHubResolverOptions.isEmpty();
      final boolean noLocalModelRoots = noResolverConfig || resolverConfig.modelsRoots == null || resolverConfig.modelsRoots.isEmpty();
      final boolean noCustomResolver = noResolverConfig || resolverConfig.commandLine == null || resolverConfig.commandLine.isEmpty();
      if ( !noName && noGitHubResolver && noLocalModelRoots && noCustomResolver ) {
         throw new CommandException(
               "When resolving a URN, at least one models root directory, GitHub repository or custom resolver must be set" );
      }
      return configuredStrategies();
   }

   @Override
   protected String expectedAspectName() {
      return urn.getName();
   }

   public static boolean appliesToInput( final String input ) {
      return input.startsWith( AspectModelUrn.PROTOCOL_AND_NAMESPACE_PREFIX );
   }

   @Override
   public AspectModel loadAspectModel() {
      final boolean urnDenotesNamespace = urn.getName().isEmpty();
      final Function<AspectModelLoader, AspectModel> loaderFunction;
      if ( validate ) {
         loaderFunction = ( (Function<AspectModelLoader, Either<List<Violation>, AspectModel>>) loader ->
               ( urnDenotesNamespace
                     ? loader.withValidation( validator ).loadNamespace( urn )
                     : loader.withValidation( validator ).load( urn ) ) )
               .andThen( this::getAspectModelOrPrintValidationReport );
      } else {
         loaderFunction = urnDenotesNamespace
               ? aspectModelLoader -> aspectModelLoader.loadNamespace( urn )
               : aspectModelLoader -> aspectModelLoader.load( urn );
      }
      return applyAspectModelLoader( loaderFunction );
   }

   @Override
   public URI inputUri() {
      return URI.create( urn.toString() );
   }

   public AspectModelUrn urn() {
      return urn;
   }

   @Override
   public AspectModelFile loadAspectModelFile() {
      final AspectModel aspectModel = loadAspectModel();
      return Try.of( () -> aspectModel.getElementByUrn( urn ) )
            .map( ModelElement::getSourceFile )
            .getOrElseThrow( () -> new CommandException( "Could not load: " + input ) );
   }
}
