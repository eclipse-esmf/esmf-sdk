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

package org.eclipse.esmf.aspectmodel;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.ModelElementGroup;
import org.eclipse.esmf.metamodel.Namespace;

import org.apache.jena.rdf.model.Model;

/**
 * An AspectModelFile is the abstraction of one "code unit". When its source location is in a file system, the
 * AspectModelFile corresponds to one file, but this does not have to be the case: An AspectModelFile could also
 * exist in memory, or purely virtually (e.g., as an abstraction of a sub-graph in a triple store).
 */
public interface AspectModelFile extends ModelElementGroup {
   /**
    * The RDF model with the contents of this AspectModelFile
    *
    * @return the model
    */
   Model sourceModel();

   /**
    * The list of Strings that are contained as a comment block at the start of the file. This is often used
    * for copyright and license information.
    *
    * @return the header comment
    */
   default List<String> headerComment() {
      return List.of();
   }

   /**
    * Returns the SPDX license identifier for this file, if one exists
    *
    * @return the SPDX identifier
    */
   default Optional<String> spdxLicenseIdentifier() {
      return headerComment().stream()
            .filter( line -> line.startsWith( "SPDX-License-Identifier:" ) )
            .map( line -> line.split( ":" )[1].trim() )
            .findFirst();
   }

   /**
    * The URI that denominates the source location, if present. It can be a file:// or https:// URL, but it
    * could for example also be an Aspect Model URN, if it refers to a file that is part of the SAMM specification.
    * Generally, this should be the physical location, not a logical identifier, in other words, where was this
    * AspectModelFile loaded from.
    *
    * @return the source location
    */
   Optional<URI> sourceLocation();

   /**
    * Returns the {@link Namespace} this AspectModelFile is a part of
    *
    * @return the namespace
    */
   default Namespace namespace() {
      throw new UnsupportedOperationException( "Uninitialized Aspect Model" );
   }

   /**
    * Lists the model elements that are contained in this AspectModelFile
    *
    * @return the model elements
    */
   @Override
   default List<ModelElement> elements() {
      throw new UnsupportedOperationException( "Uninitialized Aspect Model" );
   }
}