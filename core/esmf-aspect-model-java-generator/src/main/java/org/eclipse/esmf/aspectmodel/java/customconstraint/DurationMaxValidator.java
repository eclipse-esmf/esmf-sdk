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
import javax.xml.datatype.Duration;

import org.eclipse.esmf.metamodel.impl.BoundDefinition;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates assigned values of type {@link Duration}, which must be below or equal to this limit depending on the
 * provided {@link BoundDefinition}.
 */
public class DurationMaxValidator implements ConstraintValidator<DurationMax, Duration> {

   private static final Logger LOG = LoggerFactory.getLogger( DurationMaxValidator.class );

   private Duration durationMax;
   private BoundDefinition boundDefinition;

   @Override
   public void initialize( final DurationMax durationMax ) {
      try {
         final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
         this.durationMax = datatypeFactory.newDuration( durationMax.value() );
         boundDefinition = durationMax.boundDefinition();
      } catch ( final DatatypeConfigurationException exception ) {
         LOG.error( "Could not instantiate DatatypeFactory", exception );
      }
   }

   @Override
   public boolean isValid( final Duration durationValue, final ConstraintValidatorContext context ) {
      if ( durationValue == null ) {
         return true;
      }
      return boundDefinition.isValid( durationValue, durationMax );
   }
}
