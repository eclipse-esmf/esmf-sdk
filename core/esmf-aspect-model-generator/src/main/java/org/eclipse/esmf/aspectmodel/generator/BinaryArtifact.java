/*
 * Copyright (c) 2024 Bosch Software Innovations GmbH. All rights reserved.
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
