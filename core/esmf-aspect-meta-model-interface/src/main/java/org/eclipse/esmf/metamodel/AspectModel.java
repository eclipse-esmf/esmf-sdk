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
   /**
    * The merged RDF graph of the all AspectModelFiles of this AspectModel.
    *
    * @return the merged RDF graph
    */
   Model mergedModel();

   /**
    * The namespaces that make up this AspectModel.
    *
    * @return the list of namespaces
    */
   List<Namespace> namespaces();

   /**
    * The list of files this AspectModel consists of.
    *
    * @return the list of files
    */
   default List<AspectModelFile> files() {
      return elements().stream()
            .flatMap( element -> Optional.ofNullable( element.getSourceFile() ).stream() )
            .collect( Collectors.toSet() )
            .stream().toList();
   }

   /**
    * Retrieves a given Model Element by URN.
    *
    * @param urn the model element URN
    * @return the model element
    * @throws NoSuchElementException if no element exists with this URN
    */
   default ModelElement getElementByUrn( final AspectModelUrn urn ) {
      return elements().stream()
            .filter( element -> urn.equals( element.urn() ) )
            .findFirst()
            .orElseThrow( NoSuchElementException::new );
   }
}
