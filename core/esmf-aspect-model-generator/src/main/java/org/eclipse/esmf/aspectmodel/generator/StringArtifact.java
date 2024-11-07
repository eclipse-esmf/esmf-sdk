/*
 * Copyright (c) 2024 Bosch Software Innovations GmbH. All rights reserved.
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
