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

package org.eclipse.esmf.aspectmodel.edit.change;

import java.net.URI;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.Change;

public abstract class AbstractChange implements Change {
   protected String show( final AspectModelFile aspectModelFile ) {
      return show( aspectModelFile.sourceLocation() );
   }

   protected String show( final URI sourceLocation ) {
      return sourceLocation.toString();
   }

   protected String show( final Optional<URI> sourceLocation ) {
      return sourceLocation.map( this::show ).orElse( "(unknown file)" );
   }
}
