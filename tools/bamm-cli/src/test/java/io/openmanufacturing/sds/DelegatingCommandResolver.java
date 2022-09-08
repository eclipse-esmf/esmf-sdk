/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.FileSystemStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.vavr.control.Try;

// Utility class to help test custom model resolution mechanism. It simply wraps the FileSystemStrategy, delegates the resolution to it,
// and serializes the result to stdout, as expected of the external custom resolver.
public class DelegatingCommandResolver {

   public static void main( final String[] args ) throws URISyntaxException {
      final Path target = Paths.get( DelegatingCommandResolver.class.getResource( "/" ).toURI() ).getParent();
      final Path modelsRoot = Paths.get( target.toString(), "classes", "valid", args[0] );
      final AspectModelUrn urn = AspectModelUrn.fromUrn( args[1] );
      final Try<VersionedModel> resolution = new AspectModelResolver().resolveAspectModel( new FileSystemStrategy( modelsRoot ), urn );
      resolution.get().getModel().write( System.out );
   }
}
