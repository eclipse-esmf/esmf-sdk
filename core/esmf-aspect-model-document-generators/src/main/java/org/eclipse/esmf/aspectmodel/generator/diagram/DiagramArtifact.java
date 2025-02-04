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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.generator.BinaryArtifact;
import org.eclipse.esmf.aspectmodel.generator.LocalizedArtifact;

/**
 * Generation artifact that represents a diagram
 */
public class DiagramArtifact extends BinaryArtifact implements LocalizedArtifact {
   private final Locale language;

   public DiagramArtifact( final String id, final byte[] content, final Locale language ) {
      super( id, content );
      this.language = language;
   }

   @Override
   public Locale language() {
      return language;
   }
}
