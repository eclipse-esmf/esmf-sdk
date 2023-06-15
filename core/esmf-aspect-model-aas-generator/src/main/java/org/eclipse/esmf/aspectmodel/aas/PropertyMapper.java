/*
 * Copyright (c) 2021-2023 Robert Bosch Manufacturing Solutions GmbH
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

import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXSD;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;

import com.google.common.collect.ImmutableMap;

/**
 * Base interface for any class that can map a property to a {@link SubmodelElement}.
 *
 * @param <T> the concrete type of {@link SubmodelElement} the implementing mapper produces
 */
public interface PropertyMapper<T extends SubmodelElement> {
   static String UNKNOWN_TYPE = "Unknown";

   static String UNKNOWN_EXAMPLE = UNKNOWN_TYPE;

   /**
    * Maps Aspect types to DataTypeDefXSD Schema types, with no explicit mapping defaulting to
    * string
    */
   static final Map<Resource, DataTypeDefXSD> AAS_XSD_TYPE_MAP =
         ImmutableMap.<Resource, DataTypeDefXSD> builder()
               .put( XSD.anyURI, DataTypeDefXSD.ANY_URI )
               .put( XSD.yearMonthDuration, DataTypeDefXSD.DURATION )
               .put( XSD.xboolean, DataTypeDefXSD.BOOLEAN )
               .put( XSD.xbyte, DataTypeDefXSD.BYTE )
               .put( XSD.date, DataTypeDefXSD.DATE )
               .put( XSD.dateTime, DataTypeDefXSD.DATE_TIME )
               .put( XSD.dateTimeStamp, DataTypeDefXSD.DATE_TIME )
               .put( XSD.dayTimeDuration, DataTypeDefXSD.DURATION )
               .put( XSD.decimal, DataTypeDefXSD.DECIMAL )
               .put( XSD.xdouble, DataTypeDefXSD.DOUBLE )
               .put( XSD.duration, DataTypeDefXSD.DURATION )
               .put( XSD.xfloat, DataTypeDefXSD.FLOAT )
               .put( XSD.gMonth, DataTypeDefXSD.GMONTH )
               .put( XSD.gMonthDay, DataTypeDefXSD.GMONTH_DAY )
               .put( XSD.gYear, DataTypeDefXSD.GYEAR )
               .put( XSD.gYearMonth, DataTypeDefXSD.GYEAR_MONTH )
               .put( XSD.hexBinary, DataTypeDefXSD.HEX_BINARY )
               .put( XSD.xint, DataTypeDefXSD.INT )
               .put( XSD.integer, DataTypeDefXSD.INTEGER )
               .put( XSD.xlong, DataTypeDefXSD.LONG )
               .put( XSD.negativeInteger, DataTypeDefXSD.NEGATIVE_INTEGER )
               .put( XSD.nonNegativeInteger, DataTypeDefXSD.NON_NEGATIVE_INTEGER )
               .put( XSD.positiveInteger, DataTypeDefXSD.POSITIVE_INTEGER )
               .put( XSD.xshort, DataTypeDefXSD.SHORT )
               .put( XSD.normalizedString, DataTypeDefXSD.STRING )
               .put( XSD.time, DataTypeDefXSD.TIME )
               .put( XSD.unsignedByte, DataTypeDefXSD.UNSIGNED_BYTE )
               .put( XSD.unsignedInt, DataTypeDefXSD.UNSIGNED_INT )
               .put( XSD.unsignedLong, DataTypeDefXSD.UNSIGNED_LONG )
               .put( XSD.unsignedShort, DataTypeDefXSD.UNSIGNED_SHORT )
               .build();

   /**
    * Performs the mapping of the given property to a AAS {@link SubmodelElement}.
    *
    * @param type the type of the given property
    * @param property the property to map
    * @param context the current visitor context
    * @return the newly created {@link SubmodelElement}
    */
   T mapToAasProperty( Type type, Property property, Context context );

   /**
    * Whether this {@code PropertyMapper} can handle the given property.
    *
    * Defaults to {@code true}, implementors should override.
    *
    * @param property the property to test
    * @return {@code true} if this property mapper can handle the given property, {@code false} else
    */
   default boolean canHandle( final Property property ) {
      return true;
   }

   /**
    * Maps the given URN to a {@link DataTypeDefXSD} schema type.
    *
    * @param urn the URN to map
    * @return the {@code DataTypeDefXSD} for the given URN
    */
   default DataTypeDefXSD mapAASXSDataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return AAS_XSD_TYPE_MAP.getOrDefault( resource, DataTypeDefXSD.STRING );
   }

   /**
    * Builds a concept description reference for the given property.
    *
    * @param property the property to build the reference for
    * @return the newly created reference
    */
   default Reference buildReferenceToConceptDescription( final Property property ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.CONCEPT_DESCRIPTION )
                  .value( determineIdentifierFor( property ) )
                  .build();
      return new DefaultReference.Builder().type( ReferenceTypes.EXTERNAL_REFERENCE ).keys( key ).build();
   }

   /**
    * Determines the identifier for the given {@link NamedElement}.
    *
    * @param element the element to get the identifier for
    * @return the identifier
    */
   default String determineIdentifierFor( final NamedElement element ) {
      return element.getAspectModelUrn()
            .map( AspectModelUrn::toString )
            .orElseGet( element::getName );
   }
}
