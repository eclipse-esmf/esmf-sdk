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

package org.eclipse.esmf.metamodel;

import java.util.Map;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

/**
 * Defines the data type of a {@link Characteristic} as being a scalar value.
 */
public interface Scalar extends Type {
   @Override
   default boolean isScalar() {
      return true;
   }

   private boolean transitivelyCastable( final String from, final String to ) {
      if ( from.equals( to ) ) {
         return true;
      }
      final Map<String, String> castable = ImmutableMap.<String, String> builder()
            .put( XSD.xbyte.getURI(), XSD.xshort.getURI() )
            .put( XSD.xshort.getURI(), XSD.xint.getURI() )
            .put( XSD.xint.getURI(), XSD.xlong.getURI() )
            .put( XSD.xlong.getURI(), XSD.integer.getURI() )
            .put( XSD.integer.getURI(), XSD.decimal.getURI() )
            .put( XSD.unsignedByte.getURI(), XSD.unsignedShort.getURI() )
            .put( XSD.unsignedShort.getURI(), XSD.unsignedInt.getURI() )
            .put( XSD.unsignedInt.getURI(), XSD.unsignedLong.getURI() )
            .put( XSD.unsignedLong.getURI(), XSD.nonNegativeInteger.getURI() )
            .put( XSD.positiveInteger.getURI(), XSD.nonNegativeInteger.getURI() )
            .put( XSD.nonNegativeInteger.getURI(), XSD.integer.getURI() )
            .put( XSD.negativeInteger.getURI(), XSD.nonPositiveInteger.getURI() )
            .put( XSD.nonPositiveInteger.getURI(), XSD.integer.getURI() )
            .put( XSD.dateTimeStamp.getURI(), XSD.dateTime.getURI() )
            .put( XSD.yearMonthDuration.getURI(), XSD.duration.getURI() )
            .put( XSD.dayTimeDuration.getURI(), XSD.duration.getURI() )
            .build();

      final String entry = castable.get( from );
      if ( entry == null ) {
         return false;
      } else if ( entry.equals( to ) ) {
         return true;
      }
      return transitivelyCastable( entry, to );
   }

   @Override
   default boolean isTypeOrSubtypeOf( final Type other ) {
      return transitivelyCastable( getUrn(), other.getUrn() );
   }

   default boolean hasStringLikeValueSpace() {
      return ImmutableList.<String> builder()
            .add( XSD.xstring.getURI() )
            .add( XSD.date.getURI() )
            .add( XSD.time.getURI() )
            .add( XSD.dateTime.getURI() )
            .add( XSD.dateTimeStamp.getURI() )
            .add( XSD.gYear.getURI() )
            .add( XSD.gMonth.getURI() )
            .add( XSD.gDay.getURI() )
            .add( XSD.gYearMonth.getURI() )
            .add( XSD.gMonthDay.getURI() )
            .add( XSD.duration.getURI() )
            .add( XSD.yearMonthDuration.getURI() )
            .add( XSD.dayTimeDuration.getURI() )
            .add( XSD.hexBinary.getURI() )
            .add( XSD.base64Binary.getURI() )
            .add( XSD.anyURI.getURI() )
            .add( SammNs.SAMM.curie().getURI() )
            .add( RDF.langString.getURI() )
            .build()
            .contains( getUrn() );
   }
}
