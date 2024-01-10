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

package org.eclipse.esmf.aspectmodel.aas;

import java.util.stream.Stream;

/**
 * The list of supported AAS file formats
 */
public enum AasFileFormat {
   AASX,
   XML,
   JSON;

   @Override
   public String toString() {
      return switch ( this ) {
         case AASX -> "aasx";
         case XML -> "xml";
         case JSON -> "json";
      };
   }

   /**
    * Returns a formatted list of the valid formats
    * @return the list of formats
    */
   public static String allValues() {
      return String.join( ", ", Stream.of( values() ).map( AasFileFormat::toString ).toList() );
   }
}
