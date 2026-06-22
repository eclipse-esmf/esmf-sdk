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

package org.eclipse.esmf.aspectmodel.aas;

import java.util.regex.Pattern;

final class AasIdShort {
   private static final Pattern VALID_ID_SHORT = Pattern.compile( "[a-zA-Z][a-zA-Z0-9_-]*[a-zA-Z0-9_]+" );
   private static final String DEFAULT_ID_SHORT = "idShort";

   private AasIdShort() {}

   static String from( final String value ) {
      if ( value != null && VALID_ID_SHORT.matcher( value ).matches() ) {
         return value;
      }

      final String sanitized = value == null ? ""
            : value.chars()
                  .dropWhile( character -> !Character.isLetter( character ) )
                  .filter( character -> Character.isLetterOrDigit( character ) || character == '_' || character == '-' )
                  .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                  .toString()
                  .replaceAll( "-+$", "" );
      if ( sanitized.isEmpty() ) {
         return DEFAULT_ID_SHORT;
      }
      if ( sanitized.length() == 1 ) {
         return sanitized + "_";
      }
      return sanitized;
   }
}
