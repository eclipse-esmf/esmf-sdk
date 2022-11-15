/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.shacl;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Resource;

/**
 * Encodes the different ways a NodeShape can refer to the set of elements it applies to
 */
public sealed interface Target permits Target.Class, Target.Node, Target.ObjectsOf, Target.Sparql, Target.SubjectsOf {
   /**
    * Implements <code>sh:targetClass</code>. This means that this shape applies to elements of the given class.
    * @param class_ the referred class
    */
   record Class(Resource class_) implements Target {
   }

   record Node(Resource node) implements Target {
   }

   record ObjectsOf(org.apache.jena.rdf.model.Property property) implements Target {
   }

   /**
    * Implements <code>sh:targetSubjectsOf</code>. This means that the shape applies to all elements that use a certain property.
    * @param property the given property
    */
   record SubjectsOf(org.apache.jena.rdf.model.Property property) implements Target {
   }

   /**
    * Implements <code>sh:target [ a sh:SPARQLTarget ... ]</code>. This means that the shape applies to all elements returned
    * by a given SPARQL query
    * @param query the given SPARQL query
    */
   record Sparql(Query query) implements Target {
   }
}
