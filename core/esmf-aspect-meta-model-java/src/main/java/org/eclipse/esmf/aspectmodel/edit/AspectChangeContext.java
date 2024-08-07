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

import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.esmf.aspectmodel.AspectModelBuilder;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.impl.DefaultAspectModel;

public class AspectChangeContext {
   private final Deque<Change> undoStack = new ArrayDeque<>();
   private final Deque<Change> redoStack = new ArrayDeque<>();
   private final DefaultAspectModel aspectModel;
   private final AspectChangeContextConfig config;

   public AspectChangeContext( final AspectChangeContextConfig config, final AspectModel aspectModel ) {
      this.config = config;
      if ( aspectModel instanceof final DefaultAspectModel defaultAspectModel ) {
         this.aspectModel = defaultAspectModel;
      } else {
         throw new ModelChangeException( "AspectModel must be an instance of DefaultAspectModel" );
      }
   }

   public AspectChangeContext( final AspectModel aspectModel ) {
      this( AspectChangeContextConfigBuilder.builder().build(), aspectModel );
   }

   public synchronized ChangeReport applyChange( final Change change ) {
      final ChangeReport result = change.fire( new ChangeContext( aspectModel.files(), config ) );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
      return result;
   }

   public synchronized void undoChange() {
      if ( undoStack.isEmpty() ) {
         return;
      }
      final Change change = undoStack.pollLast();
      change.fire( new ChangeContext( aspectModel.files(), config ) );
      updateAspectModelAfterChange();
      redoStack.offerLast( change.reverse() );
   }

   public synchronized void redoChange() {
      if ( redoStack.isEmpty() ) {
         return;
      }
      final Change change = redoStack.pollLast();
      change.fire( new ChangeContext( aspectModel.files(), config ) );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
   }

   private void updateAspectModelAfterChange() {
      final AspectModel updatedModel = AspectModelBuilder.buildAspectModelFromFiles( aspectModel.files() );
      aspectModel.setMergedModel( updatedModel.mergedModel() );
      aspectModel.setElements( updatedModel.elements() );
      aspectModel.setFiles( updatedModel.files() );
   }
}
