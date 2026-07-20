/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf;

/**
 * A diagnostic that refers to a specific location within a document. This interface extends the
 * base Diagnostic interface and adds methods to retrieve the source location of the document and
 * the specific location within that document that the diagnostic refers to.
 *
 * @param <C> the type of diagnostics code
 */
public interface DocumentDiagnostic<C extends Diagnostic.Code> extends Diagnostic<C> {
   /**
    * Identifier for the location of the document which this diagnostic refers to
    *
    * @return the document source location
    */
   String sourceLocation();

   /**
    * The location within the document this diagnostic refers to
    *
    * @return the location within the document
    */
   Location location();

   @Override
   default boolean hasLocation() {
      return true;
   }
}
