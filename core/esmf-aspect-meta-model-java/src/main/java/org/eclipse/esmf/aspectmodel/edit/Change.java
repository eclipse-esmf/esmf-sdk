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

package org.eclipse.esmf.aspectmodel.edit;

import org.eclipse.esmf.metamodel.AspectModel;

public interface Change {
   void applyChange( AspectModel aspectModel );

   Change reverse();

   <T> T accept( Visitor<T> visitor );

   interface Visitor<T> {
      T visitChangeGroup( ModelRefactoring.ChangeGroup changeGroup );

      T visitRenameElement( ModelRefactoring.RenameElement renameElement );

      T visitRenameUrn( ModelRefactoring.RenameUrn renameUrn );
   }
}
