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

package org.eclipse.esmf.aspectmodel.generator.docu;

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.generator.LocalizedArtifact;
import org.eclipse.esmf.aspectmodel.generator.StringArtifact;

/**
 * Generation artifact that represents documentation for an Aspect Model
 */
public class DocumentationArtifact extends StringArtifact implements LocalizedArtifact {
   private final Locale language;

   public DocumentationArtifact( final String id, final String content, final Locale language ) {
      super( id, content );
      this.language = language;
   }

   @Override
   public Locale language() {
      return language;
   }
}
