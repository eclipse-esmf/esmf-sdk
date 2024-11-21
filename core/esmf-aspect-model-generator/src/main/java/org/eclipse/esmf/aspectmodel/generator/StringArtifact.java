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

package org.eclipse.esmf.aspectmodel.generator;

public abstract class StringArtifact implements Artifact<String, String> {
   protected final String id;
   protected final String content;

   public StringArtifact( final String id, final String content ) {
      this.id = id;
      this.content = content;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public String getContent() {
      return content;
   }
}
