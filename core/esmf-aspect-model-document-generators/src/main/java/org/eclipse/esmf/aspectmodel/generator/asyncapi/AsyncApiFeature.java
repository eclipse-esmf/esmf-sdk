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
public enum AsyncApiFeature {
   /**
    * Adds a {@code tenant-id} channel parameter. If no explicit channel address is configured, the
    * generated address is additionally prefixed with {@code /{tenant-id}}. If an explicit channel
    * address is configured and contains the literal {@code {tenant-id}} placeholder, only the
    * parameter is added.
    */
   ADD_TENANT_ID_IN_PARAMETERS,

   /**
    * Changes the {@code namespace}, {@code version} and {@code aspect-name} channel parameters (and
    * {@code tenant-id}, if present) from plain string values to AsyncAPI Parameter Objects.
    */
   EXPAND_CHANNEL_ADDRESS_PARAMETERS
}
