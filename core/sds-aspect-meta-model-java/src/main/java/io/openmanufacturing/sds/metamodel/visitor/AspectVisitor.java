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
package io.openmanufacturing.sds.metamodel.visitor;

import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Code;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.Duration;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.EncodingConstraint;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.FixedPointConstraint;
import io.openmanufacturing.sds.metamodel.HasProperties;
import io.openmanufacturing.sds.metamodel.LanguageConstraint;
import io.openmanufacturing.sds.metamodel.LengthConstraint;
import io.openmanufacturing.sds.metamodel.List;
import io.openmanufacturing.sds.metamodel.LocaleConstraint;
import io.openmanufacturing.sds.metamodel.Measurement;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Quantifiable;
import io.openmanufacturing.sds.metamodel.QuantityKind;
import io.openmanufacturing.sds.metamodel.RangeConstraint;
import io.openmanufacturing.sds.metamodel.RegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.Set;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.State;
import io.openmanufacturing.sds.metamodel.StructuredValue;
import io.openmanufacturing.sds.metamodel.TimeSeries;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Unit;

/**
 * Visitor interface for the traversal of Aspect Meta Model instances
 *
 * @param <T> The result type of the traversal operation
 * @param <C> The context of the computation
 */
public interface AspectVisitor<T, C> {
   T visitBase( Base base, C context );

   default T visitAspect( final Aspect aspect, final C context ) {
      return visitBase( aspect, context );
   }

   default T visitProperty( final Property property, final C context ) {
      return visitBase( property, context );
   }

   default T visitOperation( final Operation operation, final C context ) {
      return visitBase( operation, context );
   }

   default T visitCharacteristic( final Characteristic characteristic, final C context ) {
      return visitBase( characteristic, context );
   }

   default T visitEntity( final Entity entity, final C context ) {
      return visitBase( entity, context );
   }

   default T visitUnit( final Unit unit, final C context ) {
      return visitBase( unit, context );
   }

   default T visitQuantityKind( final QuantityKind quantityKind, final C context ) {
      return visitBase( quantityKind, context );
   }

   default T visitConstraint( final Constraint constraint, final C context ) {
      return visitBase( constraint, context );
   }

   default T visitCode( final Code code, final C context ) {
      return visitCharacteristic( code, context );
   }

   default T visitTrait( final Trait trait, final C context ) {
      return visitCharacteristic( trait, context );
   }

   default T visitCollection( final Collection collection, final C context ) {
      return visitCharacteristic( collection, context );
   }

   default T visitDuration( final Duration duration, final C context ) {
      return visitQuantifiable( duration, context );
   }

   default T visitEither( final Either either, final C context ) {
      return visitCharacteristic( either, context );
   }

   default T visitEnumeration( final Enumeration enumeration, final C context ) {
      return visitCharacteristic( enumeration, context );
   }

   default T visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final C context ) {
      return visitConstraint( fixedPointConstraint, context );
   }

   default T visitEncodingConstraint( final EncodingConstraint encodingConstraint, final C context ) {
      return visitConstraint( encodingConstraint, context );
   }

   default T visitLanguageConstraint( final LanguageConstraint languageConstraint, final C context ) {
      return visitConstraint( languageConstraint, context );
   }

   default T visitLengthConstraint( final LengthConstraint lengthConstraint, final C context ) {
      return visitConstraint( lengthConstraint, context );
   }

   default T visitList( final List list, final C context ) {
      return visitCollection( list, context );
   }

   default T visitLocaleConstraint( final LocaleConstraint localeConstraint, final C context ) {
      return visitConstraint( localeConstraint, context );
   }

   default T visitMeasurement( final Measurement measurement, final C context ) {
      return visitQuantifiable( measurement, context );
   }

   default T visitQuantifiable( final Quantifiable quantifiable, final C context ) {
      return visitCharacteristic( quantifiable, context );
   }

   default T visitRangeConstraint( final RangeConstraint rangeConstraint, final C context ) {
      return visitConstraint( rangeConstraint, context );
   }

   default T visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final C context ) {
      return visitConstraint( regularExpressionConstraint, context );
   }

   default T visitSet( final Set set, final C context ) {
      return visitCollection( set, context );
   }

   default T visitSingleEntity( final SingleEntity singleEntity, final C context ) {
      return visitCharacteristic( singleEntity, context );
   }

   default T visitSortedSet( final SortedSet sortedSet, final C context ) {
      return visitCollection( sortedSet, context );
   }

   default T visitState( final State state, final C context ) {
      return visitEnumeration( state, context );
   }

   default T visitStructuredValue( final StructuredValue structuredValue, final C context ) {
      return visitCharacteristic( structuredValue, context );
   }

   default T visitTimeSeries( final TimeSeries timeSeries, final C context ) {
      return visitSortedSet( timeSeries, context );
   }

   default T visitHasProperties( final HasProperties element, final C context ) {
      if ( element instanceof Aspect ) {
         return visitAspect( (Aspect) element, context );
      }
      return visitEntity( (Entity) element, context );
   }

   default T visitType( final Type type, final C context ) {
      if ( type instanceof Scalar ) {
         return visitScalar( (Scalar) type, context );
      }
      return visitEntity( (Entity) type, context );
   }

   default T visitScalar( final Scalar scalar, final C context ) {
      return visitBase( scalar, context );
   }
}
