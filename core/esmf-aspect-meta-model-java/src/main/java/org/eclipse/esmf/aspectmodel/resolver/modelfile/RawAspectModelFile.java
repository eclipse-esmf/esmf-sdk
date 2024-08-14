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

package org.eclipse.esmf.aspectmodel.resolver.modelfile;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

@SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
@RecordBuilder
public record RawAspectModelFile(
      Model sourceModel,
      List<String> headerComment,
      Optional<URI> sourceLocation )
      implements AspectModelFile {
   public RawAspectModelFile {
      if ( sourceModel == null ) {
         sourceModel = ModelFactory.createDefaultModel();
      }
      if ( headerComment == null ) {
         headerComment = List.of();
      }
      if ( sourceLocation == null ) {
         sourceLocation = Optional.empty();
      }
   }

   @Override
   public String toString() {
      return sourceLocation().map( URI::toString ).orElse( "(unknown file)" );
   }
}
