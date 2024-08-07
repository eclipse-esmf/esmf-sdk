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

package org.eclipse.esmf.metamodel.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;

import org.apache.jena.rdf.model.Model;

public class DefaultAspectModel implements AspectModel {
   private Model mergedModel;
   private List<ModelElement> elements;
   private List<AspectModelFile> files;

   public DefaultAspectModel( final List<AspectModelFile> files, final Model mergedModel, final List<ModelElement> elements ) {
      this.files = files;
      this.mergedModel = mergedModel;
      this.elements = elements;
   }

   @Override
   public List<Namespace> namespaces() {
      return files().stream()
            .map( AspectModelFile::namespace )
            .collect( Collectors.toSet() )
            .stream().toList();
   }

   @Override
   public List<AspectModelFile> files() {
      return files;
   }

   @Override
   public List<ModelElement> elements() {
      return elements;
   }

   @Override
   public Model mergedModel() {
      return mergedModel;
   }

   public void setFiles( final List<AspectModelFile> files ) {
      this.files = files;
   }

   public void setMergedModel( final Model mergedModel ) {
      this.mergedModel = mergedModel;
   }

   public void setElements( final List<ModelElement> elements ) {
      this.elements = elements;
   }
}
