/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.jackson;

import java.io.IOException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

/**
 * Jackson deserializer for {@see XMLGregorianCalendar}
 */
public class XMLGregorianCalendarDeserializer extends StdScalarDeserializer<XMLGregorianCalendar> {
   private static final long serialVersionUID = 8911315963918963886L;
   private static final Logger LOG = LoggerFactory.getLogger( XMLGregorianCalendarDeserializer.class );
   public static final XMLGregorianCalendarDeserializer INSTANCE = new XMLGregorianCalendarDeserializer();

   private final transient DatatypeFactory datatypeFactory;

   @SuppressWarnings( "squid:S2139" ) // Must throw runtime exception to fail fast
   private XMLGregorianCalendarDeserializer() {
      super( XMLGregorianCalendar.class );
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch ( final DatatypeConfigurationException exception ) {
         LOG.error( "Could not instantiate DatatypeFactory", exception );
         throw new RuntimeJsonMappingException( exception.getMessage() );
      }
   }

   @Override
   public XMLGregorianCalendar deserialize( final JsonParser parser, final DeserializationContext context )
         throws IOException {
      if ( parser.getCurrentTokenId() == JsonTokenId.ID_STRING ) {
         return datatypeFactory.newXMLGregorianCalendar( parser.getText().trim() );
      }

      return (XMLGregorianCalendar) context.handleUnexpectedToken( XMLGregorianCalendar.class, parser );
   }
}
