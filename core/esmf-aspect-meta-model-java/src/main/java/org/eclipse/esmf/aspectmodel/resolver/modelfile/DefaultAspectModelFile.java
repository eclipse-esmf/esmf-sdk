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
import java.util.Objects;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.metamodel.ModelElement;

import org.apache.jena.rdf.model.Model;

public final class DefaultAspectModelFile implements AspectModelFile {
   private final Model sourceModel;
   private final List<String> headerComment;
   private final Optional<URI> sourceLocation;
   private List<ModelElement> elements;

   public DefaultAspectModelFile( final Model sourceModel, final List<String> headerComment, final Optional<URI> sourceLocation ) {
      this.sourceModel = sourceModel;
      this.headerComment = headerComment;
      this.sourceLocation = sourceLocation;
   }

   public DefaultAspectModelFile( final Model sourceModel, final List<String> headerComment, final Optional<URI> sourceLocation,
         final List<ModelElement> elements ) {
      this( sourceModel, headerComment, sourceLocation );
      this.elements = elements;
   }

   @Override
   public Model sourceModel() {
      return sourceModel;
   }

   @Override
   public List<String> headerComment() {
      return headerComment;
   }

   @Override
   public Optional<URI> sourceLocation() {
      return sourceLocation;
   }

   @Override
   public List<ModelElement> elements() {
      return elements;
   }

   public void setElements( final List<ModelElement> elements ) {
      this.elements = elements;
   }

   @Override
   public boolean equals( final Object obj ) {
      if ( obj == this ) {
         return true;
      }
      if ( obj == null || obj.getClass() != getClass() ) {
         return false;
      }
      final DefaultAspectModelFile that = (DefaultAspectModelFile) obj;
      return Objects.equals( sourceModel, that.sourceModel )
            && Objects.equals( headerComment, that.headerComment )
            && Objects.equals( sourceLocation, that.sourceLocation )
            && Objects.equals( elements, that.elements );
   }

   @Override
   public int hashCode() {
      return Objects.hash( sourceModel, headerComment, sourceLocation, elements );
   }

   @Override
   public String toString() {
      return "DefaultAspectModelFile["
            + "sourceModel=" + sourceModel + ", "
            + "headerComment=" + headerComment + ", "
            + "sourceLocation=" + sourceLocation + ", "
            + "elements=" + elements + ']';
   }
}
