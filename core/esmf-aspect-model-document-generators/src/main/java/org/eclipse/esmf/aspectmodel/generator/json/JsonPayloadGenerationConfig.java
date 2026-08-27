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

package org.eclipse.esmf.aspectmodel.generator.json;

import java.util.Random;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.JsonGenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * A {@link GenerationConfig} for JSON sample payload generation.
 *
 * @param randomStrategy the Random instance to use for random value generation
 * @param addTypeAttributeForEntityInheritance if set to true, adds "@type" attribute in payloads
 *        for inherited entities
 * @param failOnInvalidRegularExpressions if a sample value for a regex can not be generated, fail
 *        instead of creating a fallback
 * @param timestamp custom timestamp in XML Schema dateTime format (e.g. 2025-06-15T10:30:00.000Z)
 *        to use when no example value is defined. Must be a full dateTime; partial timestamps are
 *        not supported.
 */
@RecordBuilder
public record JsonPayloadGenerationConfig(
      Random randomStrategy,
      boolean addTypeAttributeForEntityInheritance,
      boolean failOnInvalidRegularExpressions,
      String timestamp
) implements JsonGenerationConfig {
   public JsonPayloadGenerationConfig {
      if ( randomStrategy == null ) {
         randomStrategy = new Random();
      }
      if ( timestamp != null && !timestamp.isBlank() ) {
         final XMLGregorianCalendar cal;
         try {
            cal = DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar( timestamp );
         } catch ( final IllegalArgumentException e ) {
            throw new IllegalArgumentException(
                  "Invalid timestamp format: '" + timestamp
                        + "'. Expected XML Schema dateTime format (e.g. 2025-06-15T10:30:00.000Z).",
                  e );
         }
         if ( cal.getYear() == DatatypeConstants.FIELD_UNDEFINED
               || cal.getMonth() == DatatypeConstants.FIELD_UNDEFINED
               || cal.getDay() == DatatypeConstants.FIELD_UNDEFINED
               || cal.getHour() == DatatypeConstants.FIELD_UNDEFINED
               || cal.getMinute() == DatatypeConstants.FIELD_UNDEFINED
               || cal.getSecond() == DatatypeConstants.FIELD_UNDEFINED ) {
            throw new IllegalArgumentException(
                  "Timestamp must be a full XML Schema dateTime (e.g. 2025-06-15T10:30:00.000Z). "
                        + "Partial timestamps are not supported: '" + timestamp + "'." );
         }
      }
   }
}
