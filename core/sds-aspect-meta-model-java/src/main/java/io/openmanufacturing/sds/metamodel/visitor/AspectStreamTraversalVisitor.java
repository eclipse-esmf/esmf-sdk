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
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Quantifiable;
import io.openmanufacturing.sds.metamodel.QuantityKind;
import io.openmanufacturing.sds.metamodel.StructureElement;
import io.openmanufacturing.sds.metamodel.StructuredValue;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Unit;

/**
 * Aspect Meta Model visitor that recursively traverses all elements of the model
 */
public class AspectStreamTraversalVisitor implements AspectVisitor<Stream<Base>, Void> {

   private final List<Base> hasVisited = new LinkedList<>();

   @Override
   public Stream<Base> visitBase( final Base base, final Void context ) {
      return Stream.of( base );
   }

   @Override
   public Stream<Base> visitStructureElement( final StructureElement structureElement, final Void context ) {
      return Stream.concat( Stream.of( structureElement ),
            structureElement.getProperties().stream().flatMap( property -> property.accept( this, null ) ) );
   }

   @Override
   public Stream<Base> visitAspect( final Aspect aspect, final Void context ) {
      return Stream.concat(
            visitStructureElement( aspect, null ),
            aspect.getOperations().stream().flatMap( operation -> operation.accept( this, null ) ) );
   }

   @SuppressWarnings( "squid:S2250" )
   //Amount of elements in list is in regard to amount of properties in aspect model. Even in bigger aspects this should not lead to performance issues
   @Override
   public Stream<Base> visitProperty( final Property property, final Void context ) {
      if ( hasVisited.contains( property ) ) {
         return Stream.<Base> of( property );
      }
      hasVisited.add( property );
      return Stream.concat( Stream.<Base> of( property ), property.getCharacteristic().accept( this, null ) );
   }

   @Override
   public Stream<Base> visitOperation( final Operation operation, final Void context ) {
      return Stream.of( Stream.<Base> of( operation ),
                  operation.getInput().stream().flatMap( property -> property.accept( this, null ) ),
                  operation.getOutput().stream().flatMap( property -> property.accept( this, null ) ) )
            .reduce( Stream.empty(), Stream::concat );
   }

   @Override
   public Stream<Base> visitEither( final Either either, final Void context ) {
      return Stream.of(
            either.getLeft().accept( this, null ),
            either.getRight().accept( this, null ),
            visitCharacteristic( either, null )
      ).flatMap( Function.identity() );
   }

   @Override
   public Stream<Base> visitUnit( final Unit unit, final Void context ) {
      return Stream.concat( Stream.of( unit ),
            unit.getQuantityKinds().stream().flatMap( quantityKind -> quantityKind.accept( this, null ) ) );
   }

   @Override
   public Stream<Base> visitQuantityKind( final QuantityKind quantityKind, final Void context ) {
      return Stream.of( quantityKind );
   }

   @Override
   public Stream<Base> visitQuantifiable( final Quantifiable quantifiable, final Void context ) {
      return Stream.concat(
            quantifiable.getUnit().stream().flatMap( unit -> unit.accept( this, null ) ),
            visitCharacteristic( quantifiable, null )
      );
   }

   @Override
   public Stream<Base> visitTrait( final Trait trait, final Void context ) {
      return Stream.concat(
            visitCharacteristic( trait.getBaseCharacteristic(), null ),
            trait.getConstraints().stream().flatMap( constraint -> constraint.accept( this, null ) ) );
   }

   @Override
   public Stream<Base> visitCharacteristic( final Characteristic characteristic, final Void context ) {
      return Stream.concat( Stream.of( characteristic ),
            characteristic.getDataType().stream().flatMap( type -> type.accept( this, null ) ) );
   }

   @Override
   public Stream<Base> visitStructuredValue( final StructuredValue structuredValue, final Void context ) {
      return Stream.concat( Stream.of( structuredValue ),
            structuredValue.getElements().stream().filter( Property.class::isInstance )
                  .map( Property.class::cast )
                  .flatMap( property -> property.accept( this, null ) ) );
   }

   @Override
   public Stream<Base> visitEntity( final Entity entity, final Void context ) {
      if ( hasVisited.contains( entity ) ) {
         return Stream.<Base> of( entity );
      }
      hasVisited.add( entity );
      return visitComplexType( entity, context );
   }

   @Override
   public Stream<Base> visitAbstractEntity( final AbstractEntity abstractEntity, final Void context ) {
      if ( hasVisited.contains( abstractEntity ) ) {
         return Stream.<Base> of( abstractEntity );
      }
      hasVisited.add( abstractEntity );
      return Stream.concat(
            abstractEntity.getExtendingElements().stream().flatMap( complexType -> complexType.accept( this, null ) ),
            visitComplexType( abstractEntity, context )
      );
   }

   @Override
   public Stream<Base> visitComplexType( final ComplexType complexType, final Void context ) {
      final Stream<Base> complexTypeWithPropertiesStream = Stream.concat( Stream.of( complexType ),
            complexType.getProperties().stream().flatMap( property -> property.accept( this, null ) ) );

      if ( complexType.getExtends().isPresent() ) {
         return Stream.concat( complexTypeWithPropertiesStream, complexType.getExtends().get().accept( this, null ) );
      }
      return complexTypeWithPropertiesStream;
   }
}
