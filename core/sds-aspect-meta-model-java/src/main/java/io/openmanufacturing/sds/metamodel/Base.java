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

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

/**
 * The Base interface provides all facilities that all Aspect Model elements have.
 */
public interface Base {
   /**
    * @return the version of the Aspect Meta Model on which the Aspect Model is based.
    */
   KnownVersion getMetaModelVersion();

   <T, C> T accept( AspectVisitor<T, C> visitor, C context );
}
