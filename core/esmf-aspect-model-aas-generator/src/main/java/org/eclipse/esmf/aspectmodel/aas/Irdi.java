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

package org.eclipse.esmf.aspectmodel.aas;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Encodes an International Registration Data Identifier (IRDI) as defined by ISO 29002-5.
 * The IRDI has the following format:
 * <pre>
 *           ┌─ registration authority identifier (RAI)
 *           │          ┌─ data identifier (DI)
 *           │          │  ┌─ version identifier (VI)
 *           │          │  └───────────┐
 *           │          └────────┐     │
 * ╭─────────┴─────────────╮ ╭───┴───╮ │
 * nnnn-xxxx-xxxxxx-a-xxxxxx#xx-xxxxxx#n
 *          ^^^^^^^^^^^^^^^^               ─{ optional
 * ╰┬─╯ ╰┬─╯ ╰─┬──╯ │ ╰─┬──╯ │  ╰─┬──╯
 *  │    │     │    │   │    │    └─ item code (IC)
 *  │    │     │    │   │    └─ code space id. (CSI)
 *  │    │     │    │   └─ additional information (AI)
 *  │    │     │    └─ OPI source (OPIS)
 *  │    │     └─ organization part identifier (OPI)
 *  │    └─ organization identifier (OI)
 *  └─ international code designator (ICD)
 * </pre>
 * where {@code n} are digits, {@code a} is alphanumeric, {@code x} is a safe character and {@code # -} are separator characters.
 * RAI, DI and VI are specified in ISO/IEC 11179-6; OPIS, OPI, OI and ICD are specified in ISO/IEC 6523.
 * The term "safe character" refers to an upper case letter, a digit, a colon, a period, or an underscore.
 * An IRDI uses only digits (0-9), upper case letters (A-Z) and the symbols {@code - # . : _}.
 */
public class Irdi {
   private static final int IRDI_MAX_LENGTH = 290;
   // Min/max lengths of attributes taken from ISO/TS 29002-5:2009(E) section 8.
   private static final Pattern IRDI_PATTERN = Pattern.compile( "N{1,4}-X{1,35}(-X{1,35}-A-X{1,70})?#X{1,2}-X{1,131}#N{1,10}"
         .replace( "N", "\\d" )
         .replace( "X", "[A-Z0-9:._]" )
         .replace( "A", "\\p{Alnum}" )
   );

   private final String lexicalRepresentation;

   private Irdi( final String lexicalRepresentation ) {
      this.lexicalRepresentation = lexicalRepresentation;
   }

   /**
    * Factory method: Returns an instance of the IRDI if it is valid, otherwise empty.
    *
    * @param string the lexical representation
    * @return the IRDI instance
    */
   public static Optional<Irdi> from( final String string ) {
      if ( IRDI_PATTERN.matcher( string ).matches() && string.length() <= IRDI_MAX_LENGTH ) {
         return Optional.of( new Irdi( string ) );
      }
      return Optional.empty();
   }

   @Override
   public String toString() {
      return lexicalRepresentation;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final Irdi irdi = (Irdi) o;
      return Objects.equals( lexicalRepresentation, irdi.lexicalRepresentation );
   }

   @Override
   public int hashCode() {
      return Objects.hash( lexicalRepresentation );
   }
}
