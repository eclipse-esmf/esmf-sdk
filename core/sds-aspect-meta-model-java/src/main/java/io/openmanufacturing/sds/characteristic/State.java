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

package io.openmanufacturing.sds.characteristic;

import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Value;

@SuppressWarnings( { "squid:S1150", "This is not java.util.Enumeration." } )
/**
 * A list of possible states. Provides a default value.
 *
 * @since BAMM 1.0.0
 */
public interface State extends Enumeration {

   /**
    * @return the default value defined for this State. The type of the values is determined by the {@link Type}
    *       returned by {@link State#getDataType()}.
    */
   Value getDefaultValue();
}
