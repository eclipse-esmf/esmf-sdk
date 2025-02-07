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

package org.eclipse.esmf.test.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;

import org.apache.jena.rdf.model.Model;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractUriAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.OptionalAssert;

/**
 * Assert for {@link AspectModelFile}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class AspectModelFileAssert<SELF extends AspectModelFileAssert<SELF, ACTUAL>, ACTUAL extends AspectModelFile>
      extends AbstractAssert<SELF, ACTUAL> {
   protected AspectModelFileAssert( final ACTUAL aspectModelFile ) {
      super( aspectModelFile, AspectModelFileAssert.class );
   }

   public SELF hasLocation( final URI location ) {
      assertThat( actual.sourceLocation() ).isNotEmpty().contains( location );
      return myself;
   }

   public SELF hasLocation( final String location ) {
      return hasLocation( URI.create( location ) );
   }

   public SELF hasLocationMatching( final Predicate<URI> locationPredicate ) {
      assertThat( actual.sourceLocation() ).isNotEmpty();
      assertThat( locationPredicate.test( actual.sourceLocation().orElseThrow() ) ).isTrue();
      return myself;
   }

   public AbstractUriAssert<?> location() {
      assertThat( actual.sourceLocation() ).isNotEmpty();
      return assertThat( actual.sourceLocation().orElseThrow() );
   }

   public SELF hasHeaderComment( final List<String> headerComment ) {
      assertThat( actual.headerComment() ).isEqualTo( headerComment );
      return myself;
   }

   public ListAssert<String> headerComment() {
      return assertThat( actual.headerComment() );
   }

   public SELF hasHeaderCommentThatContains( final String comment ) {
      assertThat( actual.headerComment() ).anyMatch( line -> line.contains( comment ) );
      return myself;
   }

   public SELF hasSpdxLicenseIdentifier( final String identifier ) {
      assertThat( actual.spdxLicenseIdentifier() ).isPresent().contains( identifier );
      return myself;
   }

   public OptionalAssert<String> spdxIdentifier() {
      return assertThat( actual.spdxLicenseIdentifier() );
   }

   public SELF hasNamespace( final Namespace namespace ) {
      assertThat( actual.namespace() ).isEqualTo( namespace );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends NamespaceAssert<S, A>, A extends Namespace> NamespaceAssert<S, A> namespace() {
      return new NamespaceAssert<>( (A) actual.namespace() );
   }

   public ListAssert<ModelElement> elements() {
      return assertThat( actual.elements() );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ModelAssert<S, A>, A extends Model> ModelAssert<S, A> sourceModel() {
      return new ModelAssert<>( (A) actual.sourceModel() );
   }
}
