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

import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;

/**
 * Refactoring operation: Renames a model element, i.e., its local name but not its namespace
 */
public class RenameElement extends RenameUrn {
   public RenameElement( final ModelElement modelElement, final String newName ) {
      this( modelElement.urn(), newName );
      if ( modelElement.isAnonymous() ) {
         throw new ModelChangeException( "Can not rename anonymous model element" );
      }
   }

   public RenameElement( final AspectModelUrn urn, final String newName ) {
      super( urn, AspectModelUrn.fromUrn( urn.getUrnPrefix() + newName ) );
   }

   @Override
   protected String changeDescription() {
      return "Rename " + from() + " to " + to().getName();
   }
}
