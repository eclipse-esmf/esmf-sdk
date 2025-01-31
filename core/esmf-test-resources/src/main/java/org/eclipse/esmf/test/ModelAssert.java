/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ListAssert;

/**
 * Assert for {@link Model}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class ModelAssert<SELF extends ModelAssert<SELF, ACTUAL>, ACTUAL extends Model> extends AbstractAssert<SELF, ACTUAL> {
   protected ModelAssert( final ACTUAL actual ) {
      super( actual, ModelAssert.class );
   }

   public ListAssert<Statement> statements() {
      return assertThat( Streams.stream( actual.listStatements() ) );
   }

   public ListAssert<Statement> statements( final Resource subject, final Property predicate, final RDFNode object ) {
      return assertThat( Streams.stream( actual.listStatements( subject, predicate, object ) ) );
   }

   public ListAssert<Resource> subjects() {
      return assertThat( Streams.stream( actual.listSubjects() ) );
   }
}
