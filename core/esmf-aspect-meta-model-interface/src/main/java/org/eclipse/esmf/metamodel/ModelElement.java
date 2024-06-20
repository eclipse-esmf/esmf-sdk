/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

import static java.lang.System.identityHashCode;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.ModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

/**
 * The Base interface provides all facilities that all Aspect Model elements have.
 */
public interface ModelElement extends HasDescription {
   default AspectModelUrn urn() {
      return AspectModelUrn.fromUrn( "urn:samm:anonymous.elements:0.0.0#" + "x%08X".formatted( identityHashCode( this ) ) );
   }

   @Deprecated( forRemoval = true )
   default Optional<AspectModelUrn> getAspectModelUrn() {
      return Optional.of( urn() );
   }

   @Deprecated( forRemoval = true )
   default KnownVersion getMetaModelVersion() {
      return KnownVersion.getLatest();
   }

   default String getName() {
      return urn().getName();
   }

   default Optional<ModelFile> getSourceFile() {
      return Optional.empty();
   }

   /**
    * Determines whether the model element is identified by a proper Aspect Model URN.
    *
    * @return true of the element is considered anonymous, otherwise it has a global identifying URN
    */
   default boolean isAnonymous() {
      return true;
   }

   <T, C> T accept( AspectVisitor<T, C> visitor, C context );

   default <T extends ModelElement> boolean is( final Class<T> clazz ) {
      return clazz.isAssignableFrom( getClass() );
   }

   default <T extends ModelElement> T as( final Class<T> clazz ) {
      return clazz.cast( this );
   }
}
