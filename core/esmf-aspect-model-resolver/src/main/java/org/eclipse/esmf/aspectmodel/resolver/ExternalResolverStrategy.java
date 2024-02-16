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

package org.eclipse.esmf.aspectmodel.resolver;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;

/**
 * A ResolutionStrategy that executes an external command, which will be executed using a {@link CommandExecutor}.
 */
public class ExternalResolverStrategy implements ResolutionStrategy {
   private final String command;

   public ExternalResolverStrategy( final String command ) {
      this.command = command;
   }

   @Override
   public Try<Model> apply( final AspectModelUrn aspectModelUrn ) {
      final String commandWithParameters = command + " " + aspectModelUrn.toString();
      final String result = CommandExecutor.executeCommand( commandWithParameters );
      return TurtleLoader.loadTurtle( new ByteArrayInputStream( result.getBytes( StandardCharsets.UTF_8 ) ) );
   }
}
