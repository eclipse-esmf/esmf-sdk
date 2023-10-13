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

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.Quantifiable;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.datatypes.LangString;

import com.google.common.collect.Sets;

/**
 * Aspect Model Visitor that retrieves all used Locales in an Aspect Model
 *
 * @deprecated Use org.eclipse.esmf.aspectmodel.generator.LanguageCollector instead
 */
@Deprecated( forRemoval = true )
public class LanguageCollectorModelVisitor implements AspectVisitor<Set<Locale>, Set<Locale>> {
   @Override
   public Set<Locale> visitBase( final ModelElement modelElement, final Set<Locale> context ) {
      if ( modelElement instanceof final NamedElement described ) {
         return visitIsDescribed( described, context );
      }
      return context;
   }

   @Override
   public Set<Locale> visitCharacteristic( final Characteristic characteristic, final Set<Locale> context ) {
      return Stream.of(
                  Stream.of( visitIsDescribed( characteristic, context ) ),
                  characteristic.getDataType().stream().map( type -> type.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( context, Sets::union );
   }

   public Set<Locale> visitIsDescribed( final NamedElement element, final Set<Locale> context ) {
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
                  aspect.getOperations().stream().map( operation -> operation.accept( this, Collections.emptySet() ) ),
                  aspect.getEvents().stream().map( event -> event.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( nestedContext, Sets::union );
   }

   @Override
   public Set<Locale> visitProperty( final Property property, final Set<Locale> context ) {
      return Stream.of(
                  Stream.of( visitIsDescribed( property, context ) ),
                  property.getCharacteristic().stream().map( characteristic -> characteristic.accept( this, Collections.emptySet() ) ),
                  property.getExtends().stream().map( superProperty -> superProperty.accept( this, Collections.emptySet() ) ) )
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
                  entity.getProperties().stream().map( property -> property.accept( this, Collections.emptySet() ) ),
                  entity.getExtends().stream().map( superEntity -> superEntity.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( context, Sets::union );
   }

   @Override
   public Set<Locale> visitCollection( final Collection collection, final Set<Locale> context ) {
      return Stream.of(
                  Stream.of( visitIsDescribed( collection, context ) ),
                  collection.getElementCharacteristic().stream().map( characteristic -> characteristic.accept( this,
                        Collections.emptySet() ) ),
                  collection.getDataType().stream().map( type -> type.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( context, Sets::union );
   }

   @Override
   public Set<Locale> visitQuantifiable( final Quantifiable quantifiable, final Set<Locale> context ) {
      return Stream.of(
                  Stream.of( visitIsDescribed( quantifiable, context ) ),
                  quantifiable.getUnit().stream().map( unit -> unit.accept( this, Collections.emptySet() ) ),
                  quantifiable.getDataType().stream().map( type -> type.accept( this, Collections.emptySet() ) ) )
            .reduce( Stream.empty(), Stream::concat ).reduce( context, Sets::union );
   }
}
