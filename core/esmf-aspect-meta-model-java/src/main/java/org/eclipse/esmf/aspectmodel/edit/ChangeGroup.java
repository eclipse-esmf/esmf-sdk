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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangeGroup implements Change {
   private final String summary;
   private final List<Change> changes;

   public ChangeGroup( final String summary, final Change... changes ) {
      this( summary, Arrays.asList( changes ) );
   }

   public ChangeGroup( final Change... changes ) {
      this( null, Arrays.asList( changes ) );
   }

   public ChangeGroup( final String summary, final List<Change> changes ) {
      this.summary = summary;
      this.changes = changes;
   }

   public ChangeGroup( final List<Change> changes ) {
      this( null, changes );
   }

   @Override
   public ChangeReport fire( final ChangeContext changeContext ) {
      return new ChangeReport.MultipleEntries( summary, changes.stream().map( change -> change.fire( changeContext ) ).toList() );
   }

   @Override
   public Change reverse() {
      final List<Change> reversedChanges = new ArrayList<>( changes.size() );
      for ( int i = changes.size() - 1; i >= 0; i-- ) {
         reversedChanges.add( changes.get( i ).reverse() );
      }
      return new ChangeGroup( reversedChanges );
   }
}
