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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.ModelElement;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.characteristic.Either;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.characteristic.Quantifiable;
import io.openmanufacturing.sds.metamodel.QuantityKind;
import io.openmanufacturing.sds.metamodel.StructureElement;
import io.openmanufacturing.sds.characteristic.StructuredValue;
import io.openmanufacturing.sds.characteristic.Trait;
import io.openmanufacturing.sds.metamodel.Unit;

/**
 * Aspect Meta Model visitor that recursively traverses all elements of the model
 */
public class AspectStreamTraversalVisitor implements AspectVisitor<Stream<ModelElement>, Void> {

   private final List<ModelElement> hasVisited = new LinkedList<>();

   @Override
   public Stream<ModelElement> visitBase( final ModelElement modelElement, final Void context ) {
      return Stream.of( modelElement );
   }

   @Override
   public Stream<ModelElement> visitStructureElement( final StructureElement structureElement, final Void context ) {
      return Stream.concat( Stream.of( structureElement ),
            structureElement.getProperties().stream().flatMap( property -> property.accept( this, null ) ) );
   }

   @Override
   public Stream<ModelElement> visitAspect( final Aspect aspect, final Void context ) {
      return Stream.concat(
            visitStructureElement( aspect, null ),
            aspect.getOperations().stream().flatMap( operation -> operation.accept( this, null ) ) );
   }

   @SuppressWarnings( "squid:S2250" )
   //Amount of elements in list is in regard to amount of properties in aspect model. Even in bigger aspects this should not lead to performance issues
   @Override
   public Stream<ModelElement> visitProperty( final Property property, final Void context ) {
      if ( hasVisited.contains( property ) ) {
         return Stream.<ModelElement> of( property );
      }
      hasVisited.add( property );
      final Stream<ModelElement> characteristicResult = property.getCharacteristic().stream().flatMap( characteristic ->
            characteristic.accept( this, null ) );
      final Stream<ModelElement> extendsResult = property.getExtends().stream().flatMap( superProperty -> superProperty.accept( this, null ) );
      return Stream.of( Stream.<ModelElement> of( property ), characteristicResult, extendsResult
      ).flatMap( Function.identity() );
   }

   @Override
   public Stream<ModelElement> visitOperation( final Operation operation, final Void context ) {
      return Stream.of( Stream.<ModelElement> of( operation ),
                  operation.getInput().stream().flatMap( property -> property.accept( this, null ) ),
                  operation.getOutput().stream().flatMap( property -> property.accept( this, null ) ) )
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
            unit.getQuantityKinds().stream().flatMap( quantityKind -> quantityKind.accept( this, null ) ) );
   }

   @Override
   public Stream<ModelElement> visitQuantityKind( final QuantityKind quantityKind, final Void context ) {
      return Stream.of( quantityKind );
   }

   @Override
   public Stream<ModelElement> visitQuantifiable( final Quantifiable quantifiable, final Void context ) {
      return Stream.concat(
            quantifiable.getUnit().stream().flatMap( unit -> unit.accept( this, null ) ),
            visitCharacteristic( quantifiable, null )
      );
   }

   @Override
   public Stream<ModelElement> visitTrait( final Trait trait, final Void context ) {
      return Stream.concat(
            visitCharacteristic( trait.getBaseCharacteristic(), null ),
            trait.getConstraints().stream().flatMap( constraint -> constraint.accept( this, null ) ) );
   }

   @Override
   public Stream<ModelElement> visitCharacteristic( final Characteristic characteristic, final Void context ) {
      return Stream.concat( Stream.of( characteristic ),
            characteristic.getDataType().stream().flatMap( type -> type.accept( this, null ) ) );
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
      if ( hasVisited.contains( entity ) ) {
         return Stream.<ModelElement> of( entity );
      }
      hasVisited.add( entity );
      return visitComplexType( entity, context );
   }

   @Override
   public Stream<ModelElement> visitAbstractEntity( final AbstractEntity abstractEntity, final Void context ) {
      if ( hasVisited.contains( abstractEntity ) ) {
         return Stream.<ModelElement> of( abstractEntity );
      }
      hasVisited.add( abstractEntity );
      return Stream.concat(
            abstractEntity.getExtendingElements().stream()
                  .flatMap( complexType -> complexType.accept( this, null ) ),
            visitComplexType( abstractEntity, context )
      );
   }

   @Override
   public Stream<ModelElement> visitComplexType( final ComplexType complexType, final Void context ) {
      final Stream<ModelElement> complexTypeWithPropertiesStream = Stream.concat( Stream.of( complexType ),
            complexType.getProperties().stream().flatMap( property -> property.accept( this, null ) ) );

      if ( complexType.getExtends().isPresent() ) {
         return Stream.concat( complexTypeWithPropertiesStream, complexType.getExtends().get().accept( this, null ) );
      }
      return complexTypeWithPropertiesStream;
   }
}
