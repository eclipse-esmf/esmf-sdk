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

package org.eclipse.esmf.metamodel;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.metamodel.vocabulary.SAMMC;

/**
 * Defines the possible values for the {@link SAMMC#lowerBoundDefinition()} and {@link SAMMC#upperBoundDefinition()}
 * attributes of the {@link SAMMC#RangeConstraint()} for {@link Comparable} and {@link Duration}.
 */
public enum BoundDefinition {

   OPEN {
      @Override
      public <T extends Comparable<T>> boolean isValid( final T value, final T limit ) {
         return true;
      }

      @Override
      public <T extends Duration> boolean isValid( final T value, final T limit ) {
         return true;
      }

      @Override
      public <T extends XMLGregorianCalendar> boolean isValid( final T value, final T limit ) {
         return true;
      }
   },
   AT_LEAST {
      @Override
      public <T extends Comparable<T>> boolean isValid( final T value, final T limit ) {
         return value.compareTo( limit ) >= 0;
      }

      @Override
      public <T extends Duration> boolean isValid( final T value, final T limit ) {
         return value.compare( limit ) >= 0;
      }

      @Override
      public <T extends XMLGregorianCalendar> boolean isValid( final T value, final T limit ) {
         return value.compare( limit ) >= 0;
      }
   },
   GREATER_THAN {
      @Override
      public <T extends Comparable<T>> boolean isValid( final T value, final T limit ) {
         return value.compareTo( limit ) > 0;
      }

      @Override
      public <T extends Duration> boolean isValid( final T value, final T limit ) {
         return value.compare( limit ) > 0;
      }

      @Override
      public <T extends XMLGregorianCalendar> boolean isValid( final T value, final T limit ) {
         return value.compare( limit ) > 0;
      }
   },
   LESS_THAN {
      @Override
      public <T extends Comparable<T>> boolean isValid( final T value, final T limit ) {
         return value.compareTo( limit ) < 0;
      }

      @Override
      public <T extends Duration> boolean isValid( final T value, final T limit ) {
         return value.compare( limit ) < 0;
      }

      @Override
      public <T extends XMLGregorianCalendar> boolean isValid( final T value, final T limit ) {
         return value.compare( limit ) < 0;
      }
   },
   AT_MOST {
      @Override
      public <T extends Comparable<T>> boolean isValid( final T value, final T limit ) {
         return value.compareTo( limit ) <= 0;
      }

      @Override
      public <T extends Duration> boolean isValid( final T value, final T limit ) {
         return value.compare( limit ) <= 0;
      }

      @Override
      public <T extends XMLGregorianCalendar> boolean isValid( final T value, final T limit ) {
         return value.compare( limit ) <= 0;
      }
   };

   @Override
   public String toString() {
      return name().replaceAll( "_", " " ).toLowerCase();
   }

   public abstract <T extends Comparable<T>> boolean isValid( T value, T limit );

   public abstract <T extends Duration> boolean isValid( T value, T limit );

   public abstract <T extends XMLGregorianCalendar> boolean isValid( T value, T limit );
}
