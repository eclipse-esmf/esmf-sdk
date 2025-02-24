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

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;

import io.vavr.control.Try;

/**
 * The AspectModelUrnInputHandler knows how to load Aspect Models if the given input is an Aspect Model URN
 */
public class AspectModelUrnInputHandler extends AbstractInputHandler {
   private final AspectModelUrn urn;

   public AspectModelUrnInputHandler( final String input, final ResolverConfigurationMixin resolverConfig, final boolean details ) {
      super( input, resolverConfig, details );
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
      return input.startsWith( "urn:samm:" );
   }

   @Override
   public AspectModel loadAspectModel() {
      return applyAspectModelLoader( aspectModelLoader -> aspectModelLoader.load( urn ) );
   }

   @Override
   public URI inputUri() {
      return URI.create( urn.toString() );
   }

   @Override
   public AspectModelFile loadAspectModelFile() {
      final AspectModel aspectModel = loadAspectModel();
      return Try.of( () -> aspectModel.getElementByUrn( urn ) )
            .map( ModelElement::getSourceFile )
            .getOrElseThrow( () -> new CommandException( "Could not load: " + input ) );
   }
}
