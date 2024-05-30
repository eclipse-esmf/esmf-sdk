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

import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.aspectmodel.resolver.services.ModelFile;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.ModelNamespace;

public class DefaultAspectModel implements AspectModel {
   private final List<ModelFile> files;
   private final List<ModelElement> elements;


   public DefaultAspectModel( final List<ModelFile> files, final List<ModelElement> elements ) {
      this.files = files;
      this.elements = elements;
   }

   @Override
   public List<ModelNamespace> namespaces() {
      return List.of();
   }

   @Override
   public List<ModelFile> files() {
      return files;
   }

   @Override
   public List<ModelElement> elements() {
      return elements;
   }
}
