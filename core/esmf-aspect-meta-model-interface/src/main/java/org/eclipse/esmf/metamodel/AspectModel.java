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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.Model;

/**
 * The core interface that represents an Aspect Model. It contains the underlying RDF model, the list of model elements
 * (as instances of {@link ModelElement} and provides information about the {@link Namespace}s and the source files
 * ({@link AspectModelFile}) that make up the model.
 */
public interface AspectModel extends ModelElementGroup {
   Model mergedModel();

   List<Namespace> namespaces();

   default List<AspectModelFile> files() {
      return elements().stream()
            .flatMap( element -> Optional.ofNullable( element.getSourceFile() ).stream() )
            .collect( Collectors.toSet() )
            .stream().toList();
   }

   default ModelElement elementByUrn( final AspectModelUrn urn ) {
      return elements().stream()
            .filter( element -> urn.equals( element.urn() ) )
            .findFirst()
            .orElseThrow( NoSuchElementException::new );
   }
}
