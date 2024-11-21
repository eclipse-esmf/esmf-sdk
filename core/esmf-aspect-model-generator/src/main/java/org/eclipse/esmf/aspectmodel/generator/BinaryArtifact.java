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

/**
 * Abstract base class for {@link Artifact}s that have content represented as byte[]
 */
public abstract class BinaryArtifact implements Artifact<String, byte[]> {
   private final String id;
   private final byte[] content;

   public BinaryArtifact( final String id, final byte[] content ) {
      this.id = id;
      this.content = content;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public byte[] getContent() {
      return content;
   }

   @Override
   public byte[] serialize() {
      return content;
   }
}
