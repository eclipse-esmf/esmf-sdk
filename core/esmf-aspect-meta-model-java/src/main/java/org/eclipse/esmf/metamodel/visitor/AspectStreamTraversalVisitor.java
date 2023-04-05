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
package org.eclipse.esmf.metamodel.visitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.characteristic.Either;
import org.eclipse.esmf.characteristic.Quantifiable;
import org.eclipse.esmf.characteristic.StructuredValue;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Unit;

/**
 * Aspect Meta Model visitor that recursively traverses all elements of the model
 */
public class AspectStreamTraversalVisitor implements AspectVisitor<Stream<ModelElement>, Void> {

   private final Set<ModelElement> hasVisited = new HashSet<>();

   @Override
   public Stream<ModelElement> visitBase( final ModelElement modelElement, final Void context ) {
      return Stream.of( modelElement );
   }

   @Override
   public Stream<ModelElement> visitStructureElement( final StructureElement structureElement, final Void context ) {
      return Stream.concat( Stream.of( structureElement ),
            visit( structureElement.getProperties() ) );
   }

   @Override
   public Stream<ModelElement> visitAspect( final Aspect aspect, final Void context ) {
      return Stream.concat(
            visitStructureElement( aspect, null ),
            visit( aspect.getOperations() ) );
   }

   @SuppressWarnings( "squid:S2250" )
   //Amount of elements in list is in regard to amount of properties in aspect model. Even in bigger aspects this should not lead to performance issues
   @Override
   public Stream<ModelElement> visitProperty( final Property property, final Void context ) {
      if ( hasVisited.contains( property ) ) {
         return Stream.of( property );
      }
      hasVisited.add( property );
      final Stream<ModelElement> characteristicResult = visit( property.getCharacteristic() );
      final Stream<ModelElement> extendsResult = visit( property.getExtends() );
      return Stream.of( Stream.<ModelElement> of( property ), characteristicResult, extendsResult
      ).flatMap( Function.identity() );
   }

   @Override
   public Stream<ModelElement> visitOperation( final Operation operation, final Void context ) {
      return Stream.of( Stream.<ModelElement> of( operation ),
                  visit( operation.getInput() ),
                  visit( operation.getOutput() ) )
            .reduce( Stream.empty(), Stream::concat );
   }

   @Override
   public Stream<ModelElement> visitEither( final Either either, final Void context ) {
      return Stream.of(
            either.getLeft().accept( this, null ),
            either.getRight().accept( this, null ),
            visitCharacteristic( either, null )
      ).flatMap( Function.identity() );
   }

   @Override
   public Stream<ModelElement> visitUnit( final Unit unit, final Void context ) {
      return Stream.concat( Stream.of( unit ),
            visit( unit.getQuantityKinds() ) );
   }

   @Override
   public Stream<ModelElement> visitQuantityKind( final QuantityKind quantityKind, final Void context ) {
      return Stream.of( quantityKind );
   }

   @Override
   public Stream<ModelElement> visitQuantifiable( final Quantifiable quantifiable, final Void context ) {
      return Stream.concat(
            visit( quantifiable.getUnit() ),
            visitCharacteristic( quantifiable, null )
      );
   }

   @Override
   public Stream<ModelElement> visitTrait( final Trait trait, final Void context ) {
      return Stream.concat(
            visitCharacteristic( trait.getBaseCharacteristic(), null ),
            visit( trait.getConstraints() ) );
   }

   @Override
   public Stream<ModelElement> visitCharacteristic( final Characteristic characteristic, final Void context ) {
      return Stream.concat( Stream.of( characteristic ),
            visit( characteristic.getDataType() ) );
   }

   @Override
   public Stream<ModelElement> visitStructuredValue( final StructuredValue structuredValue, final Void context ) {
      return Stream.concat( Stream.of( structuredValue ),
            structuredValue.getElements().stream().filter( Property.class::isInstance )
                  .map( Property.class::cast )
                  .flatMap( property -> property.accept( this, null ) ) );
   }

   @Override
   public Stream<ModelElement> visitEntity( final Entity entity, final Void context ) {
      return visitComplexType( entity, context );
   }

   @Override
   public Stream<ModelElement> visitAbstractEntity( final AbstractEntity abstractEntity, final Void context ) {
      return visitComplexType( abstractEntity, context );
   }

   @Override
   public Stream<ModelElement> visitComplexType( final ComplexType entity, final Void context ) {
      if ( hasVisited.contains( entity ) ) {
         return Stream.of( entity );
      }
      hasVisited.add( entity );

      final Stream<ModelElement> complexTypeWithPropertiesStream = Stream.concat(
            Stream.of( entity ),
            visit( entity.getProperties() ) );

      return Stream.concat(
            complexTypeWithPropertiesStream,
            Stream.concat(
                  visit( entity.getExtends() ),
                  visit( entity.getExtendingElements() ) ) );
   }

   private <T extends ModelElement> Stream<ModelElement> visit( Collection<T> collection ) {
      return collection.stream().flatMap( item -> item.accept( this, null ) );
   }

   @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
   private <T extends ModelElement> Stream<ModelElement> visit( Optional<T> optional ) {
      return optional.stream().flatMap( item -> item.accept( this, null ) );
   }
}
