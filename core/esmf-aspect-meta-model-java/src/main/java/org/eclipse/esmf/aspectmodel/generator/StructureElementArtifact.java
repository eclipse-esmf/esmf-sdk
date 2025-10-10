/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator;

import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.StructureElement;

/**
 * Result of a generation process that generates a StructureElement
 *
 * @param <T> the type of element, e.g., {@link Aspect} or {@link Entity}
 */
public class StructureElementArtifact<T extends StructureElement> implements Artifact<AspectModelUrn, T> {
   private final AspectModelUrn urn;
   private final T modelElement;

   protected StructureElementArtifact( final AspectModelUrn urn, final T modelElement ) {
      this.urn = urn;
      this.modelElement = modelElement;
   }

   @Override
   public AspectModelUrn getId() {
      return urn;
   }

   @Override
   public T getContent() {
      return modelElement;
   }

   @Override
   public byte[] serialize() {
      return AspectSerializer.INSTANCE.modelElementToString( modelElement ).getBytes( StandardCharsets.UTF_8 );
   }
}
