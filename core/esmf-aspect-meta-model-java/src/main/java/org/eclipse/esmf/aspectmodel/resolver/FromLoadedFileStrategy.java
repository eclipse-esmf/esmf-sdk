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

package org.eclipse.esmf.aspectmodel.resolver;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * Simple resolution strategy that resolves a URN by returning a previously loaded AspectModelFile.
 */
public class FromLoadedFileStrategy implements ResolutionStrategy {
   private final AspectModelFile aspectModelFile;

   public FromLoadedFileStrategy( final AspectModelFile aspectModelFile ) {
      this.aspectModelFile = aspectModelFile;
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionSupport resolutionSupport )
         throws ModelResolutionException {

      if ( resolutionSupport.containsDefinition( aspectModelFile, aspectModelUrn ) ) {
         return aspectModelFile;
      }
      throw new ModelResolutionException( "File " + aspectModelFile + " should contain defintion, but does not: " + aspectModelUrn );
   }
}
