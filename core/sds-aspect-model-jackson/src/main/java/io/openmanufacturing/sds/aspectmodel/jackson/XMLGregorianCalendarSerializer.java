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

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Jackson serializer for {@see XMLGregorianCalendar}
 */
class XMLGregorianCalendarSerializer extends StdSerializer<XMLGregorianCalendar> {
   private static final long serialVersionUID = -7121579663349330794L;
   public static final XMLGregorianCalendarSerializer INSTANCE = new XMLGregorianCalendarSerializer();

   private XMLGregorianCalendarSerializer() {
      super( XMLGregorianCalendar.class );
   }

   @Override
   public void serialize( final XMLGregorianCalendar value, final JsonGenerator generator,
         final SerializerProvider serializers ) throws IOException {
      generator.writeString( value.toXMLFormat() );
   }
}
