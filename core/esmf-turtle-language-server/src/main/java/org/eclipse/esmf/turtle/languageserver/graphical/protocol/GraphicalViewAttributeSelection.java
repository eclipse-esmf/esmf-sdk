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
package org.eclipse.esmf.turtle.languageserver.graphical.protocol;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

/** Closed semantic selection policy for current Turtle attribute statements. */
public enum GraphicalViewAttributeSelection {
   SINGLE_OCCURRENCE( "singleOccurrence" ),
   PREDICATE_START( "predicateStart" );

   private final String wireValue;

   GraphicalViewAttributeSelection( final String wireValue ) {
      this.wireValue = wireValue;
   }

   @JsonValue
   public String wireValue() {
      return wireValue;
   }

   public static Optional<GraphicalViewAttributeSelection> fromWireValue( final String value ) {
      return Arrays.stream( values() ).filter( selection -> selection.wireValue.equals( value ) ).findFirst();
   }
}
