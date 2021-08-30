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

package io.openmanufacturing.sds;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

/**
 * This class checks whether the value is given.
 * If the value is a new argument, an error will be output.
 */
public class IsValueSet implements IParameterValidator {
   public void validate( String name, String value ) {
      if ( value.startsWith( "--" ) || value.startsWith( "-" ) ) {
         throw new ParameterException( "Missing value for parameter: " + name );
      }
   }
}
