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
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;

import org.apache.jena.rdf.model.Model;

public class DefaultAspectModel implements AspectModel {
   private final Model mergedModel;
   private final List<ModelElement> elements;

   public DefaultAspectModel( final Model mergedModel, final List<ModelElement> elements ) {
      this.mergedModel = mergedModel;
      this.elements = elements;
   }

   @Override
   public List<Namespace> namespaces() {
      return elements().stream()
            .filter( element -> !Namespace.ANONYMOUS.equals( element.urn().getUrnPrefix() ) )
            .collect( Collectors.groupingBy( element -> element.urn().getUrnPrefix() ) )
            .entrySet()
            .stream()
            .<Namespace> map( entry -> new DefaultNamespace( entry.getKey(), entry.getValue(), Optional.empty() ) )
            .toList();
   }

   @Override
   public List<ModelElement> elements() {
      return elements;
   }

   @Override
   public Model mergedModel() {
      return mergedModel;
   }
}
