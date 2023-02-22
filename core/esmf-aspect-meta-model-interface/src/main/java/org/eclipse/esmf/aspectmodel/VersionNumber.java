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

package org.eclipse.esmf.aspectmodel;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Represents, parses, and compares version numbers. Supports scheme: MAJOR.MINOR.MICRO.
 * All parts must be digits. The separator must be '.'
 */
public class VersionNumber implements Comparable<VersionNumber> {
   private static final String VERSION_TEMPLATE = "%d.%d.%d";
   private final int major;
   private final int minor;
   private final int micro;

   public VersionNumber( final int major, final int minor, final int micro ) {
      Preconditions.checkArgument( major >= 0, "Major cannot be negative" );
      Preconditions.checkArgument( minor >= 0, "Minor cannot be negative" );
      Preconditions.checkArgument( micro >= 0, "Micro cannot be negative" );
      this.major = major;
      this.minor = minor;
      this.micro = micro;
   }

   public int getMajor() {
      return major;
   }

   public int getMinor() {
      return minor;
   }

   public int getMicro() {
      return micro;
   }

   public VersionNumber nextMinor() {
      return new VersionNumber( major, minor + 1, micro );
   }

   public VersionNumber nextMajor() {
      return new VersionNumber( major + 1, minor, micro );
   }

   public VersionNumber nextMicro() {
      return new VersionNumber( major, minor, micro + 1 );
   }

   public boolean greaterThan( final VersionNumber targetVersion ) {
      return compareTo( targetVersion ) > 0;
   }

   @Override
   public int compareTo( final VersionNumber other ) {
      if ( major != other.major ) {
         return major - other.major;
      }
      if ( minor != other.minor ) {
         return minor - other.minor;
      }
      if ( micro != other.micro ) {
         return micro - other.micro;
      }
      return 0;
   }

   @Override
   public boolean equals( final Object other ) {
      return other instanceof VersionNumber && compareTo( (VersionNumber) other ) == 0;
   }

   @Override
   public int hashCode() {
      int result = major;
      result = 31 * result + minor;
      result = 31 * result + micro;
      return result;
   }

   @Override
   public String toString() {
      return String.format( VERSION_TEMPLATE, major, minor, micro );
   }

   public static VersionNumber parse( final String versionString ) {
      if ( StringUtils.isEmpty( versionString ) ) {
         throw new UnsupportedVersionException();
      }
      final VersionNumberParser parser = new VersionNumberParser( versionString );

      int major = 0;
      int minor = 0;
      int micro = 0;

      major = parser.parseNextDigit();
      minor = parser.parseNextDigit();
      micro = parser.parseNextDigit();

      if ( parser.isEnd() ) {
         return new VersionNumber( major, minor, micro );
      }

      throw new UnsupportedVersionException( versionString );
   }

   private static class VersionNumberParser {
      private final String version;
      private final int length;
      private int position;

      private VersionNumberParser( final String version ) {
         this.version = version;
         length = version.length();
      }

      private char parsePosition() {
         return version.charAt( position );
      }

      private boolean isDigit() {
         return position < length && Character.isDigit( parsePosition() );
      }

      boolean isSeparator() {
         return position < length && parsePosition() == '.';
      }

      int parseNextDigit() {
         if ( isEnd() ) {
            return 0;
         }

         if ( isSeparator() ) {
            skipSeparator();
         }

         final int start = position;
         while ( !isEnd() ) {
            if ( isSeparator() && start != position ) {
               return Integer.parseInt( version.substring( start, position ) );
            }
            if ( !isDigit() ) {
               throw new UnsupportedVersionException( version );
            }
            position++;
         }
         return Integer.parseInt( version.substring( start, position ) );
      }

      public boolean isEnd() {
         return position == length;
      }

      public void skipSeparator() {
         position++;
      }
   }
}

