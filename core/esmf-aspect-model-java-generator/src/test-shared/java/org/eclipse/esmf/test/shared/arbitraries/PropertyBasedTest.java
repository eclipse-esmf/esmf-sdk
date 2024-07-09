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

package org.eclipse.esmf.test.shared.arbitraries;

import static org.junit.jupiter.api.Assertions.fail;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

public abstract class PropertyBasedTest implements SammArbitraries {
   private final DatatypeFactory datatypeFactory;

   public PropertyBasedTest() {
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch ( final DatatypeConfigurationException exception ) {
         fail( "Could not instantiate DatatypeFactory", exception );
         throw new RuntimeException( exception );
      }
   }

   @Override
   public DatatypeFactory getDatatypeFactory() {
      return datatypeFactory;
   }
}
