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

package org.eclipse.esmf.metamodel;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * Represents model elements that have human-readable names and descriptions
 */
@Deprecated( forRemoval = true )
public interface NamedElement extends ModelElement, HasDescription {
   @Override
   default String getName() {
      return ModelElement.super.getName();
   }

   /**
    * @return the URN which identifies an Aspect Model element.
    * @deprecated Use {@link ModelElement#urn()} instead
    */
   @Override
   @Deprecated( forRemoval = true )
   default Optional<AspectModelUrn> getAspectModelUrn() {
      return Optional.of( urn() );
   }

   /**
    * Determines whether this model element has a generated name
    *
    * @return true if the name is synthetic (generated at load time), false if it is given in the model
    * @deprecated Use {@link ModelElement#isAnonymous()} instead
    */
   @Deprecated( forRemoval = true )
   default boolean hasSyntheticName() {
      return isAnonymous();
   }
}
