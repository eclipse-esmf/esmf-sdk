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

package org.eclipse.esmf.aspectmodel.generator.asyncapi;

/**
 * Optional, individually toggleable behaviors of {@link AspectModelAsyncApiGenerator}. Each feature is
 * opt-in; when none are enabled, generator output is unchanged from the default behavior.
 */
public enum AsyncApiGenerationFeature {
   /**
    * Adds a {@code tenant-id} channel parameter. If no explicit channel address is configured, the
    * generated address is additionally prefixed with {@code /{tenant-id}}. If an explicit channel
    * address is configured and contains the literal {@code {tenant-id}} placeholder, only the
    * parameter is added.
    */
   ADD_TENANT_ID_IN_CHANNEL_PARAMETERS( "AddTenantIdInChannelParameters" ),

   /**
    * Changes the {@code namespace}, {@code version} and {@code aspect-name} channel parameters (and
    * {@code tenant-id}, if present) from plain string values to AsyncAPI Parameter Objects.
    */
   EXPAND_CHANNEL_PARAMETERS( "ExpandChannelParameters" );

   private final String value;

   AsyncApiGenerationFeature( final String value ) {
      this.value = value;
   }

   /**
    * Resolves a feature by its PascalCased value, e.g. {@code AddTenantIdInChannelParameters}.
    *
    * @param value the PascalCased value
    * @return the matching feature
    * @throws IllegalArgumentException if no feature matches the given value
    */
   public static AsyncApiGenerationFeature fromValue( final String value ) {
      for ( final AsyncApiGenerationFeature feature : values() ) {
         if ( feature.value.equalsIgnoreCase( value ) ) {
            return feature;
         }
      }
      throw new IllegalArgumentException( "Unknown feature flag: " + value );
   }

   @Override
   public String toString() {
      return value;
   }
}
