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

package org.eclipse.esmf.aspectmodel.generator.sql;

import org.eclipse.esmf.aspectmodel.generator.StringArtifact;

/**
 * Represents a generated SQL script.
 */
public class SqlArtifact extends StringArtifact {
   public SqlArtifact( final String id, final String content ) {
      super( id, content );
   }
}
