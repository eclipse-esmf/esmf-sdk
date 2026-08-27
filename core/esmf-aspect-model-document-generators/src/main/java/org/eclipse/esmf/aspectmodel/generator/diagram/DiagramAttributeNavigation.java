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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.eclipse.esmf.metamodel.ModelElement;

/** Allocates opt-in opaque markers and semantic locators for physical attribute rows. */
final class DiagramAttributeNavigation {
   private final boolean enabled;
   private final Set<String> allocatedMarkerIds = new LinkedHashSet<>();

   private DiagramAttributeNavigation( final boolean enabled ) {
      this.enabled = enabled;
   }

   static DiagramAttributeNavigation disabled() {
      return new DiagramAttributeNavigation( false );
   }

   static DiagramAttributeNavigation enabled() {
      return new DiagramAttributeNavigation( true );
   }

   List<Optional<Row>> rowsFor( final ModelElement owner, final String predicateUrn, final Selection selection,
         final Locale language, final int physicalRows ) {
      if ( !enabled || owner.isAnonymous() || predicateUrn == null || predicateUrn.isBlank() ) {
         return java.util.Collections.nCopies( physicalRows, Optional.empty() );
      }
      final String normalizedLanguage = language == null ? null : language.toLanguageTag().toLowerCase( Locale.ROOT );
      final Locator locator = new Locator( owner.urn().toString(), predicateUrn, selection, normalizedLanguage );
      return java.util.stream.IntStream.range( 0, physicalRows )
            .mapToObj( ignored -> Optional.of( new Row( newMarkerId(), locator ) ) )
            .toList();
   }

   private String newMarkerId() {
      String markerId;
      do {
         markerId = "gv-attribute-" + UUID.randomUUID().toString().replace( "-", "" );
      } while ( !allocatedMarkerIds.add( markerId ) );
      return markerId;
   }

   enum Selection {
      SINGLE_OCCURRENCE( "singleOccurrence" ),
      PREDICATE_START( "predicateStart" );

      private final String wireValue;

      Selection( final String wireValue ) {
         this.wireValue = wireValue;
      }

      String wireValue() {
         return wireValue;
      }
   }

   record Locator(
         String ownerUrn,
         String predicateUrn,
         Selection selection,
         String language
   ) {}

   record Row(
         String markerId,
         Locator locator
   ) {}
}
