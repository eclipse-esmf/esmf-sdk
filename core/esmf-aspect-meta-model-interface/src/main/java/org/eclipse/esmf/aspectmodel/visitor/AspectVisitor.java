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
package org.eclipse.esmf.aspectmodel.visitor;

import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.HasProperties;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.Code;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Duration;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.List;
import org.eclipse.esmf.metamodel.characteristic.Measurement;
import org.eclipse.esmf.metamodel.characteristic.Quantifiable;
import org.eclipse.esmf.metamodel.characteristic.Set;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.characteristic.SortedSet;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.characteristic.StructuredValue;
import org.eclipse.esmf.metamodel.characteristic.TimeSeries;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.EncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.LocaleConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;

/**
 * Visitor interface for the traversal of Aspect Meta Model instances
 *
 * @param <T> The result type of the traversal operation
 * @param <C> The context of the computation
 */
public interface AspectVisitor<T, C> {
   T visitBase( ModelElement modelElement, C context );

   default T visitStructureElement( final StructureElement structureElement, final C context ) {
      return visitBase( structureElement, context );
   }

   default T visitAspect( final Aspect aspect, final C context ) {
      return visitBase( aspect, context );
   }

   default T visitHasProperties( final Aspect aspect, final C context ) {
      return visitHasProperties( (HasProperties) aspect, context );
   }

   default T visitProperty( final Property property, final C context ) {
      return visitBase( property, context );
   }

   default T visitOperation( final Operation operation, final C context ) {
      return visitBase( operation, context );
   }

   default T visitEvent( final Event event, final C context ) {
      return visitBase( event, context );
   }

   default T visitCharacteristic( final Characteristic characteristic, final C context ) {
      return visitBase( characteristic, context );
   }

   default T visitEntity( final Entity entity, final C context ) {
      return visitComplexType( entity, context );
   }

   default T visitAbstractEntity( final AbstractEntity abstractEntity, final C context ) {
      return visitComplexType( abstractEntity, context );
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

   default T visitCharacteristic( final Code code, final C context ) {
      return visitCharacteristic( (Characteristic) code, context );
   }

   default T visitTrait( final Trait trait, final C context ) {
      return visitCharacteristic( trait, context );
   }

   default T visitCharacteristic( final Trait trait, final C context ) {
      return visitCharacteristic( (Characteristic) trait, context );
   }

   default T visitCollection( final Collection collection, final C context ) {
      return visitCharacteristic( collection, context );
   }

   default T visitCharacteristic( final Collection collection, final C context ) {
      return visitCharacteristic( (Characteristic) collection, context );
   }

   default T visitDuration( final Duration duration, final C context ) {
      return visitQuantifiable( duration, context );
   }

   default T visitCharacteristic( final Duration duration, final C context ) {
      return visitCharacteristic( (Characteristic) duration, context );
   }

   default T visitEither( final Either either, final C context ) {
      return visitCharacteristic( either, context );
   }

   default T visitCharacteristic( final Either either, final C context ) {
      return visitCharacteristic( (Characteristic) either, context );
   }

   default T visitEnumeration( final Enumeration enumeration, final C context ) {
      return visitCharacteristic( enumeration, context );
   }

   default T visitCharacteristic( final Enumeration enumeration, final C context ) {
      return visitCharacteristic( (Characteristic) enumeration, context );
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

   default T visitCharacteristic( final List list, final C context ) {
      return visitCharacteristic( (Characteristic) list, context );
   }

   default T visitLocaleConstraint( final LocaleConstraint localeConstraint, final C context ) {
      return visitConstraint( localeConstraint, context );
   }

   default T visitMeasurement( final Measurement measurement, final C context ) {
      return visitQuantifiable( measurement, context );
   }

   default T visitCharacteristic( final Measurement measurement, final C context ) {
      return visitCharacteristic( (Characteristic) measurement, context );
   }

   default T visitQuantifiable( final Quantifiable quantifiable, final C context ) {
      return visitCharacteristic( quantifiable, context );
   }

   default T visitCharacteristic( final Quantifiable quantifiable, final C context ) {
      return visitCharacteristic( (Characteristic) quantifiable, context );
   }

   default T visitRangeConstraint( final RangeConstraint rangeConstraint, final C context ) {
      return visitConstraint( rangeConstraint, context );
   }

   default T visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint, final C context ) {
      return visitConstraint( regularExpressionConstraint, context );
   }

   default T visitSet( final Set set, final C context ) {
      return visitCollection( set, context );
   }

   default T visitCharacteristic( final Set set, final C context ) {
      return visitCharacteristic( (Characteristic) set, context );
   }

   default T visitSingleEntity( final SingleEntity singleEntity, final C context ) {
      return visitCharacteristic( singleEntity, context );
   }

   default T visitCharacteristic( final SingleEntity singleEntity, final C context ) {
      return visitCharacteristic( (Characteristic) singleEntity, context );
   }

   default T visitSortedSet( final SortedSet sortedSet, final C context ) {
      return visitCollection( sortedSet, context );
   }

   default T visitCharacteristic( final SortedSet sortedSet, final C context ) {
      return visitCharacteristic( (Characteristic) sortedSet, context );
   }

   default T visitState( final State state, final C context ) {
      return visitEnumeration( state, context );
   }

   default T visitCharacteristic( final State state, final C context ) {
      return visitCharacteristic( (Characteristic) state, context );
   }

   default T visitStructuredValue( final StructuredValue structuredValue, final C context ) {
      return visitCharacteristic( structuredValue, context );
   }

   default T visitCharacteristic( final StructuredValue structuredValue, final C context ) {
      return visitCharacteristic( (Characteristic) structuredValue, context );
   }

   default T visitTimeSeries( final TimeSeries timeSeries, final C context ) {
      return visitSortedSet( timeSeries, context );
   }

   default T visitCharacteristic( final TimeSeries timeSeries, final C context ) {
      return visitCharacteristic( (Characteristic) timeSeries, context );
   }

   default T visitHasProperties( final HasProperties element, final C context ) {
      return visitStructureElement( (StructureElement) element, context );
   }

   default T visitType( final Type type, final C context ) {
      return visitBase( type, context );
   }

   default T visitScalar( final Scalar scalar, final C context ) {
      return visitType( scalar, context );
   }

   default T visitType( final Scalar scalar, final C context ) {
      return visitType( (Type) scalar, context );
   }

   default T visitComplexType( final ComplexType complexType, final C context ) {
      return visitType( complexType, context );
   }

   default T visitHasProperties( final ComplexType complexType, final C context ) {
      return visitHasProperties( (HasProperties) complexType, context );
   }

   default T visitType( final ComplexType complexType, final C context ) {
      return visitType( (Type) complexType, context );
   }

   default T visitValue( final Value value, final C context ) {
      return visitBase( value, context );
   }

   default T visitScalarValue( final ScalarValue value, final C context ) {
      return visitValue( value, context );
   }

   default T visitCollectionValue( final CollectionValue value, final C context ) {
      return visitValue( value, context );
   }

   default T visitEntityInstance( final EntityInstance instance, final C context ) {
      return visitValue( instance, context );
   }
}
