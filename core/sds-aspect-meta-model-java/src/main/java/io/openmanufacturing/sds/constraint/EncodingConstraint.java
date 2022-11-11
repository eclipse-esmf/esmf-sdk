/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.constraint;

import java.nio.charset.Charset;

import io.openmanufacturing.sds.metamodel.Constraint;

/**
 * Restricts a string value to a certain encoding.
 * Possible encodings are US-ASCII, ISO-8859-1, UTF-8, UTF-16, UTF-16BE and UTF-16LE.
 *
 * @since BAMM 1.0.0
 */
public interface EncodingConstraint extends Constraint {

   /**
    * @return the {@link Charset} which defines the encoding for the value.
    */
   Charset getValue();
}
