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

package io.openmanufacturing.sds.metamodel;

/**
 * Represents a domain specific concept.
 *
 * @since BAMM 1.0.0
 */
@SuppressWarnings( "squid:S3655" ) // Entity should always have a URN
public interface Entity extends Type, StructureElement, CanRefine {
   @Override
   default String getUrn() {
      return getAspectModelUrn().get().toString();
   }

   @Override
   default boolean isScalar() {
      return false;
   }
}
