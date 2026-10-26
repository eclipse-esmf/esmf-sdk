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

package org.eclipse.esmf.aspectmodel;

import java.net.URI;
import java.util.function.Supplier;

/**
 * A violation that refers to a specific document via its location. This interface extends the
 * base Violation interface and adds a method to retrieve the source location of the document.
 */
public interface DocumentViolation extends Violation {
   /**
    * Identifier for the location of the document which this violation refers to
    *
    * @return the document source location
    */
   URI sourceDocument();

   /**
    * Supplier for the content of the document which this violation refers to.
    *
    * @return the content of the source document
    */
   Supplier<String> documentContent();
}
