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

public class AspectChangeContext implements ChangeContext {
   private final Deque<Change> undoStack = new ArrayDeque<>();
   private final Deque<Change> redoStack = new ArrayDeque<>();
   private final DefaultAspectModel aspectModel;
   private final AspectChangeContextConfig config;

   public AspectChangeContext( final AspectChangeContextConfig config, final AspectModel aspectModel ) {
      this.config = config;
      if ( !( aspectModel instanceof DefaultAspectModel ) ) {
         throw new RuntimeException();
      }
      this.aspectModel = (DefaultAspectModel) aspectModel;
   }

   public AspectChangeContext( final AspectModel aspectModel ) {
      this( AspectChangeContextConfigBuilder.builder().build(), aspectModel );
   }

   public synchronized void applyChange( final Change change ) {
      change.fire( this );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
   }

   public synchronized void undoChange() {
      if ( undoStack.isEmpty() ) {
         return;
      }
      final Change change = undoStack.pollLast();
      change.fire( this );
      updateAspectModelAfterChange();
      redoStack.offerLast( change.reverse() );
   }

   public synchronized void redoChange() {
      if ( redoStack.isEmpty() ) {
         return;
      }
      final Change change = redoStack.pollLast();
      change.fire( this );
      updateAspectModelAfterChange();
      undoStack.offerLast( change.reverse() );
   }

   private void updateAspectModelAfterChange() {
      final AspectModel updatedModel = AspectModelBuilder.buildAspectModelFromFiles( aspectModel.files() );
      aspectModel.setMergedModel( updatedModel.mergedModel() );
      aspectModel.setElements( updatedModel.elements() );
      aspectModel.setFiles( updatedModel.files() );
   }

   @Override
   public AspectModel aspectModel() {
      return aspectModel;
   }

   @Override
   public AspectChangeContextConfig config() {
      return config;
   }
}
