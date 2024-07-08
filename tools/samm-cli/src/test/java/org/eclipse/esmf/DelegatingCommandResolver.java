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

package org.eclipse.esmf;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;

/**
 * Utility class to help test custom model resolution mechanism. It simply wraps the FileSystemStrategy, delegates the resolution to it,
 * and serializes the result to stdout, as expected of the external custom resolver.
 */
public class DelegatingCommandResolver {
   public static void main( final String[] args ) throws URISyntaxException {
      final Path target = Paths.get( DelegatingCommandResolver.class.getResource( "/" ).toURI() ).getParent();
      final Path modelsRoot = Paths.get( target.toString(), "classes", "valid" );
      final AspectModelUrn urn = AspectModelUrn.fromUrn( args[0] );
      final AspectModel aspectModel = new AspectModelLoader( new FileSystemStrategy( modelsRoot ) ).load( urn );
      aspectModel.mergedModel().write( System.out );
   }
}
