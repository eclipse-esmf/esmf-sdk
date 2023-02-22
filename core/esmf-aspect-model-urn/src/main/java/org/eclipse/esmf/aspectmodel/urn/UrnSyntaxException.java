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

package org.eclipse.esmf.aspectmodel.urn;

/**
 * Custom exception to be used when a URN identifying an Aspect Model is invalid.
 */
public class UrnSyntaxException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public static final String URN_IS_NULL_MESSAGE = "The URN may not be null.";
   public static final String URN_IS_MISSING_SECTIONS_MESSAGE = "The URN must consist of at least 5 sections adhering to the following schema: urn:bamm:<organisation>:<optional>:<version>:<model-name>.";
   public static final String URN_IS_TOO_LONG = "The length of the URN is limited to {0} characters";
   public static final String URN_INVALID_PROTOCOL_MESSAGE = "The protocol must be equal to {0}.";
   public static final String URN_INVALID_NAMESPACE_IDENTIFIER_MESSAGE = "The namespace identifier must be equal to {0}.";
   public static final String URN_INVALID_NAMESPACE_MESSAGE = "The namespace must match {0}.";
   public static final String URN_INVALID_ELEMENT_NAME_MESSAGE = "The {0} name must match {1}: {2}";
   public static final String URN_IS_NO_URI = "The URN is no valid URI";
   public static final String URN_INVALID_ELEMENT_TYPE = "The element-type must be one of aspect-model, meta-model, characteristic, entity or {0}.";
   public static final String URN_INVALID_VERSION = "Invalid version in URN: {0}.";

   public UrnSyntaxException( final String message ) {
      super( message );
   }
}
