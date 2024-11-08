/*
 * Copyright (c) 2024 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.eclipse.esmf.aspectmodel.generator.zip;

import org.eclipse.esmf.aspectmodel.generator.BinaryArtifact;

public class NamespacePackageArtifact extends BinaryArtifact {
   public NamespacePackageArtifact( final String id, final byte[] content ) {
      super( id, content );
   }
}
