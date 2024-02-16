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

package org.eclipse.esmf.substitution;

import java.util.Objects;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.util.ReflectionHelper;

/**
 * This is used as a stand-in/replacement for its superclass {@link ReflectionHelper.ImplementationInfo}, which unfortunately can't be
 * instantiated directly due to its protected constructor. This class is used in the substitution class for {@link ReflectionHelper}, see
 * {@link Target_org_eclipse_digitaltwin_aas4j_v3_dataformat_core_util_ReflectionHelper} for more information.
 */
public class ImplementationInfo<T> extends ReflectionHelper.ImplementationInfo<T> {
   public ImplementationInfo( final Class<?> interfaceType, final Class<?> implementationType ) {
      super( (Class<T>) interfaceType, (Class<? extends T>) implementationType );
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final ImplementationInfo impl = (ImplementationInfo) o;
      return Objects.equals( getInterfaceType(), impl.getInterfaceType() ) && Objects.equals( getImplementationType(),
            impl.getImplementationType() );
   }

   @Override
   public int hashCode() {
      return Objects.hash( getInterfaceType(), getImplementationType() );
   }
}
