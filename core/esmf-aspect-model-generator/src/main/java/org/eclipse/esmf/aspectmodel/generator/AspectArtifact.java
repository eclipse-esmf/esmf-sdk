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

package org.eclipse.esmf.aspectmodel.generator;

import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;

/**
 * Result of a generation process that generates an Aspect
 */
public class AspectArtifact implements Artifact<AspectModelUrn, Aspect> {
   private final AspectModelUrn urn;
   private final Aspect aspect;

   public AspectArtifact( final AspectModelUrn urn, final Aspect aspect ) {
      this.urn = urn;
      this.aspect = aspect;
   }

   @Override
   public AspectModelUrn getId() {
      return urn;
   }

   @Override
   public Aspect getContent() {
      return aspect;
   }

   @Override
   public byte[] serialize() {
      return AspectSerializer.INSTANCE.aspectToString( aspect ).getBytes( StandardCharsets.UTF_8 );
   }
}
