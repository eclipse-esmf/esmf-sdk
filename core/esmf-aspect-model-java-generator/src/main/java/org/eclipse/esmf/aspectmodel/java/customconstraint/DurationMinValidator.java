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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.esmf.metamodel.impl.BoundDefinition;

/**
 * Validates assigned values of type {@link Duration}, which must be above or equal to this limit
 * depending on the provided {@link BoundDefinition}.
 */
public class DurationMinValidator implements ConstraintValidator<DurationMin, Duration> {

   private static final Logger LOG = LoggerFactory.getLogger( DurationMinValidator.class );

   private Duration durationMin;
   private BoundDefinition boundDefinition;

   @Override
   public void initialize( final DurationMin durationMin ) {
      try {
         final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
         this.durationMin = datatypeFactory.newDuration( durationMin.value() );
         boundDefinition = durationMin.boundDefinition();
      } catch ( final DatatypeConfigurationException exception ) {
         LOG.error( "Could not instantiate DatatypeFactory", exception );
      }
   }

   @Override
   public boolean isValid( final Duration durationValue, final ConstraintValidatorContext context ) {
      if ( durationValue == null ) {
         return true;
      }
      return boundDefinition.isValid( durationValue, durationMin );
   }
}
