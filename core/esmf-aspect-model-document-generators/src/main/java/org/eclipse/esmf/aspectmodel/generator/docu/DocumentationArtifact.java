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

import org.eclipse.esmf.aspectmodel.generator.Artifact;

public class DocumentationArtifact implements Artifact<String, String> {
   private final String id;
   private final String content;

   public DocumentationArtifact( final String id, final String content ) {
      this.id = id;
      this.content = content;
   }

   @Override
   public String getId() {
      return "";
   }

   @Override
   public String getContent() {
      return "";
   }
}
