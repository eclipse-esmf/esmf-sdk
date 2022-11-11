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

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.QuantityKind;
import io.openmanufacturing.sds.characteristic.Trait;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.datatypes.LangString;

/**
 * Aspect Model Visitor that retrieves all used Locales in an Aspect Model
 */
public class LanguageCollectorModelVisitor implements AspectVisitor<Set<Locale>, Set<Locale>> {
   @Override
   public Set<Locale> visitBase( final Base base, final Set<Locale> context ) {
      return context;
   }

   public Set<Locale> visitIsDescribed( final IsDescribed element, final Set<Locale> context ) {
      return Stream.concat(
            element.getPreferredNames().stream().map( LangString::getLanguageTag ),
            element.getDescriptions().stream().map( LangString::getLanguageTag ) ).collect( Collectors.toSet() );
   }

   @Override
   public Set<Locale> visitAspect( final Aspect aspect, final Set<Locale> context ) {
      final Set<Locale> nestedContext = context == null ? Collections.emptySet() : context;
      return Stream.of(
                  Stream.of( visitIsDescribed( aspect, nestedContext ) ),
                  aspect.getProperties().stream().map( property -> property.accept( this, Collections.emptySet() ) ),
                  aspect.getOperations().stream().map( operation -> operation.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( nestedContext, Sets::union );
   }

   @Override
   public Set<Locale> visitProperty( final Property property, final Set<Locale> context ) {
      return Stream.of(
                  Stream.of( visitIsDescribed( property, context ) ),
                  property.getCharacteristic().stream().map( characteristic -> characteristic.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( context, Sets::union );
   }

   @Override
   public Set<Locale> visitOperation( final Operation operation, final Set<Locale> context ) {
      return Stream.of(
                  Stream.of( visitIsDescribed( operation, context ) ),
                  operation.getInput().stream().map( property -> property.accept( this, Collections.emptySet() ) ),
                  operation.getOutput().stream().map( property -> property.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( context, Sets::union );
   }

   @Override
   public Set<Locale> visitTrait( final Trait trait, final Set<Locale> context ) {
      return Stream.of(
                  Stream.of( visitIsDescribed( trait, context ) ),
                  Stream.of( trait.getBaseCharacteristic().accept( this, Collections.emptySet() ) ),
                  trait.getConstraints().stream().map( constraint ->
                        visitIsDescribed( constraint, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( context, Sets::union );
   }

   @Override
   public Set<Locale> visitEntity( final Entity entity, final Set<Locale> context ) {
      return Stream.of(
                  Stream.of( visitIsDescribed( entity, context ) ),
                  entity.getProperties().stream().map( property -> property.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( context, Sets::union );
   }

   @Override
   public Set<Locale> visitUnit( final Unit unit, final Set<Locale> context ) {
      return Collections.emptySet();
   }

   @Override
   public Set<Locale> visitQuantityKind( final QuantityKind quantityKind, final Set<Locale> context ) {
      return Collections.emptySet();
   }
}
