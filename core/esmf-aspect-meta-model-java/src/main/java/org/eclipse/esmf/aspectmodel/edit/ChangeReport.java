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

import java.util.List;
import java.util.Map;

public sealed interface ChangeReport {
   record SimpleEntry( String text ) implements ChangeReport {
   }

   record EntryWithDetails( String summary, Map<String, Object> details ) implements ChangeReport {
   }

   record MultipleEntries( String summary, List<ChangeReport> entries ) implements ChangeReport {
      public MultipleEntries( final List<ChangeReport> entries ) {
         this( null, entries );
      }
   }
}
