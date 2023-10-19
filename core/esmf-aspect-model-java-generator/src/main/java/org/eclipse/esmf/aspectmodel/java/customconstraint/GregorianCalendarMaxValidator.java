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

package org.eclipse.esmf.aspectmodel.java.customconstraint;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.metamodel.impl.BoundDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates assigned values of type {@link XMLGregorianCalendar}, which must be below or equal to this limit depending
 * on the provided {@link BoundDefinition}.
 */
public class GregorianCalendarMaxValidator implements ConstraintValidator<GregorianCalendarMax, XMLGregorianCalendar> {

   private static final Logger LOG = LoggerFactory.getLogger( GregorianCalendarMaxValidator.class );

   private XMLGregorianCalendar xmlGregorianCalendar;
   private BoundDefinition boundDefinition;

   @Override
   public void initialize( final GregorianCalendarMax gregorianCalendarMax ) {
      try {
         final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
         xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar( gregorianCalendarMax.value() );
         boundDefinition = gregorianCalendarMax.boundDefinition();
      } catch ( final DatatypeConfigurationException exception ) {
         LOG.error( "Could not instantiate DatatypeFactory", exception );
      }
   }

   @Override
   public boolean isValid( final XMLGregorianCalendar xmlGregorianCalendarValue,
         final ConstraintValidatorContext context ) {
      if ( xmlGregorianCalendarValue == null ) {
         return true;
      }
      return boundDefinition.isValid( xmlGregorianCalendarValue, xmlGregorianCalendar );
   }
}
