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

package io.openmanufacturing.sds.aspectmodel.java;

import java.util.Collection;

/**
 * Interface which defines the type of {@link Collection} provided by a Collection Aspect.
 *
 * @param <C> the type of collection provided by the Collection Aspect
 * @param <T> the data type contained with in the {@link Collection} provided by the Collection Aspect
 */
@SuppressWarnings( "squid:S2326" ) // Type parameter is used in runtime byte code generation
public interface CollectionAspect<C extends Collection<T>, T> {
}
