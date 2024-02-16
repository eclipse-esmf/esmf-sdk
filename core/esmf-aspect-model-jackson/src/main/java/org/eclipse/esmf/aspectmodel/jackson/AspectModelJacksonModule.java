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

import org.eclipse.esmf.metamodel.datatypes.LangString;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A Jackson module to register serializers and deserializers specific to Aspect models
 */
public class AspectModelJacksonModule extends SimpleModule {
   private static final long serialVersionUID = -2621996753536054906L;

   public AspectModelJacksonModule() {
      addSerializer( XMLGregorianCalendar.class, XmlGregorianCalendarSerializer.INSTANCE );
      addDeserializer( XMLGregorianCalendar.class, XmlGregorianCalendarDeserializer.INSTANCE );
      addSerializer( byte[].class, HexBinarySerializer.INSTANCE );
      addDeserializer( byte[].class, HexBinaryDeserializer.INSTANCE );
      addSerializer( byte[].class, Base64BinarySerializer.INSTANCE );
      addDeserializer( byte[].class, Base64BinaryDeserializer.INSTANCE );
      addDeserializer( LangString.class, LangStringDeserializer.INSTANCE );
      addSerializer( LangString.class, LangStringSerializer.INSTANCE );
   }
}
