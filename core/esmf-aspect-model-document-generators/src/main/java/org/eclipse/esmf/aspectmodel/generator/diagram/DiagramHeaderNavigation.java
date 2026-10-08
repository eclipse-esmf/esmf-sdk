/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Trait;

final class DiagramHeaderNavigation {
   private final boolean enabled;
   private final Set<String> allocatedMarkerIds = new LinkedHashSet<>();

   private DiagramHeaderNavigation( final boolean enabled ) {
      this.enabled = enabled;
   }

   static DiagramHeaderNavigation disabled() {
      return new DiagramHeaderNavigation( false );
   }

   static DiagramHeaderNavigation enabled() {
      return new DiagramHeaderNavigation( true );
   }

   Optional<Header> headerFor( final ModelElement element ) {
      if ( !enabled ) {
         return Optional.empty();
      }
      return targetUrn( element ).map( targetUrn -> {
         final String markerId = newMarkerId();
         return new Header( markerId, targetUrn );
      } );
   }

   private Optional<String> targetUrn( final ModelElement element ) {
      if ( !element.isAnonymous() ) {
         return Optional.of( element.urn().toString() );
      }

      final LinkedHashSet<String> candidates = new LinkedHashSet<>();
      if ( element instanceof final Property property ) {
         property.getExtends().ifPresent( candidate -> addNamedCandidate( candidates, candidate ) );
         property.getCharacteristic().ifPresent( candidate -> addNamedCandidate( candidates, candidate ) );
      }
      if ( element instanceof final Characteristic characteristic ) {
         characteristic.getDataType()
               .filter( Type::isComplexType )
               .map( type -> type.as( ComplexType.class ) )
               .ifPresent( candidate -> addNamedCandidate( candidates, candidate ) );
      }
      if ( element instanceof final Collection collection ) {
         collection.getElementCharacteristic().ifPresent( candidate -> addNamedCandidate( candidates, candidate ) );
      }
      if ( element instanceof final Trait trait ) {
         addNamedCandidate( candidates, trait.getBaseCharacteristic() );
         trait.getConstraints().forEach( candidate -> addNamedCandidate( candidates, candidate ) );
      }
      if ( element instanceof final Either either ) {
         addNamedCandidate( candidates, either.getLeft() );
         addNamedCandidate( candidates, either.getRight() );
      }
      if ( element instanceof final EntityInstance entityInstance ) {
         addNamedCandidate( candidates, entityInstance.getEntityType() );
      }

      return candidates.size() == 1 ? Optional.of( candidates.getFirst() ) : Optional.empty();
   }

   private void addNamedCandidate( final java.util.Collection<String> candidates, final ModelElement candidate ) {
      if ( !candidate.isAnonymous() ) {
         candidates.add( candidate.urn().toString() );
      }
   }

   private String newMarkerId() {
      String markerId;
      do {
         markerId = "gv-header-" + UUID.randomUUID().toString().replace( "-", "" );
      } while ( !allocatedMarkerIds.add( markerId ) );
      return markerId;
   }

   record Header(
         String markerId,
         String targetUrn
   ) {}
}
