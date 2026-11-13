/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

package org.eclipse.esmf.aspectmodel.generator.protobuf;

import org.eclipse.esmf.aspectmodel.generator.StringArtifact;

/**
 * Represents a generated Protocol Buffers message schema.
 */
public class ProtobufArtifact extends StringArtifact {
   /**
    * Creates a schema artifact.
    *
    * @param id the file name of the schema
    * @param content the proto3 source
    */
   public ProtobufArtifact( final String id, final String content ) {
      super( id, content );
   }
}
