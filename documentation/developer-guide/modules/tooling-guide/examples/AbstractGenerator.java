/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package examples;

import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;

public abstract class AbstractGenerator {
   // tag::outputStream[]
   OutputStream outputStreamForName( final String aspectFileName ) {
      // Create an OutputStream for the file name, e.g. a FileOutputStream
      // end::outputStream[]
      return NullOutputStream.INSTANCE;
      // tag::outputStream[]
   }
   // end::outputStream[]
}
