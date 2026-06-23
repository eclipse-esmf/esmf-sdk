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

package org.eclipse.esmf.aspectmodel.jackson;

import javax.xml.datatype.XMLGregorianCalendar;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Jackson serializer for {@see XMLGregorianCalendar}
 */
public class XmlGregorianCalendarSerializer extends ValueSerializer<XMLGregorianCalendar> {
   public static final XmlGregorianCalendarSerializer INSTANCE = new XmlGregorianCalendarSerializer();

   @Override
   public void serialize( final XMLGregorianCalendar value, final JsonGenerator generator, final SerializationContext context )
         throws JacksonException {
      generator.writeString( value.toXMLFormat() );
   }
}
