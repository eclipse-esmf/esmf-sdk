/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

/**
 * Represents the result of the operation of writing changes in the {@link AspectChangeManager} to the file system.
 */
sealed public interface WriteResult {
   interface Visitor<T> {
      T visitSuccess( Success success );

      T visitWriteFailure( WriteFailure failure );

      T visitPreconditionsNotMet( PreconditionsNotMet preconditionsNotMet );
   }

   <T> T accept( Visitor<T> visitor );

   record PreconditionsNotMet(
         List<String> errorMessages,
         boolean canBeFixedByOverwriting
   ) implements WriteResult {
      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visitPreconditionsNotMet( this );
      }
   }

   record WriteFailure(
         List<String> errorMessages
   ) implements WriteResult {
      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visitWriteFailure( this );
      }
   }

   final class Success implements WriteResult {
      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visitSuccess( this );
      }
   }
}
