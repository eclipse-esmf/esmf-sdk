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

import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ChangeReport;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Namespace;

public class MoveElementToOtherNamespace implements Change {
   private final AspectModelUrn elementUrn;
   private final Namespace targetNamespace;

   public MoveElementToOtherNamespace( final AspectModelUrn elementUrn, final Namespace targetNamespace ) {
      this.elementUrn = elementUrn;
      this.targetNamespace = targetNamespace;
   }

   @Override
   public void fire( final ChangeContext changeContext ) {

   }

   @Override
   public Change reverse() {
      return null;
   }

   @Override
   public ChangeReport report( final ChangeContext changeContext ) {
      return null;
   }
}
