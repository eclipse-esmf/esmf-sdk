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

package io.openmanufacturing.sds.staticmetamodel;

import java.util.List;

/**
 * Defines a Meta Model element which contains {@link StaticProperty}(ies).
 */
public interface PropertyContainer {

   /**
    * @return a {@link List} of {@link StaticProperty}(ies) contained in this Meta Model element
    */
   @SuppressWarnings( "squid:S1452" )
   List<StaticProperty<?>> getProperties();

   /**
    * @return a {@link List} of {@link StaticProperty}(ies) contained in this Meta Model element and the {@link StaticProperty}(ies) of the extended elements.
    */
   @SuppressWarnings( "squid:S1452" )
   List<StaticProperty<?>> getAllProperties();
}
