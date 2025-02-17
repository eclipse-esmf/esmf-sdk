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
import static org.assertj.core.api.Fail.fail;

import java.util.NoSuchElementException;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;

import org.apache.jena.rdf.model.Model;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ListAssert;

/**
 * Assert for {@link AspectModel}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class AspectModelAssert<SELF extends AspectModelAssert<SELF, ACTUAL>, ACTUAL extends AspectModel>
      extends AbstractAssert<SELF, ACTUAL> {
   public AspectModelAssert( final ACTUAL actual ) {
      super( actual, AspectModelAssert.class );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends AspectAssert<S, A>, A extends Aspect> AspectAssert<S, A> hasSingleAspectThat() {
      if ( actual.aspects().size() != 1 ) {
         failWithMessage( "Expected AspectModel to contain exactly one Aspect, but it contained %d", actual.files().size() );
      }
      return new AspectAssert<>( (A) actual.aspect() );
   }

   public SELF hasAtLeastOneAspect() {
      aspects().isNotEmpty();
      return myself;
   }

   public SELF hasNoAspects() {
      aspects().isEmpty();
      return myself;
   }

   public ListAssert<Aspect> aspects() {
      return assertThat( actual.aspects() );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends AspectModelFileAssert<S, A>, A extends AspectModelFile> AspectModelFileAssert<S, A> hasSingleAspectModelFileThat() {
      if ( actual.files().size() != 1 ) {
         failWithMessage( "Expected AspectModel to contain exactly one AspectModelFile, but it contained %d", actual.files().size() );
      }
      return new AspectModelFileAssert<>( (A) actual.files().get( 0 ) );
   }

   public SELF hasAtLeastOneFile() {
      files().isNotEmpty();
      return myself;
   }

   public SELF hasNoFiles() {
      files().isEmpty();
      return myself;
   }

   public ListAssert<AspectModelFile> files() {
      return assertThat( actual.files() );
   }

   public SELF hasAtLeastOneElement() {
      elements().isNotEmpty();
      return myself;
   }

   public SELF hasNoElements() {
      elements().isEmpty();
      return myself;
   }

   public ListAssert<ModelElement> elements() {
      return assertThat( actual.elements() );
   }

   public <S extends ModelElementAssert<S, A>, A extends ModelElement> ModelElementAssert<S, A> modelElementByUrn( final String urn ) {
      return AspectModelUrn.from( urn ).map( this::<S, A>modelElementByUrn ).getOrElseThrow( exception -> {
         fail( exception );
         return new RuntimeException();
      } );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ModelElementAssert<S, A>, A extends ModelElement> ModelElementAssert<S, A> modelElementByUrn(
         final AspectModelUrn urn ) {
      try {
         final ModelElement element = actual.getElementByUrn( urn );
         return new ModelElementAssert<>( (A) element );
      } catch ( final NoSuchElementException exception ) {
         failWithMessage( "Expected AspectModel to contain model element %s, but it didn't", urn );
      }
      return null;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends NamespaceAssert<S, A>, A extends Namespace> NamespaceAssert<S, A> hasSingleNamespaceThat() {
      if ( actual.namespaces().size() != 1 ) {
         failWithMessage( "Expected AspectModel to contain exactly one Namespace, but it contained %d", actual.namespaces().size() );
      }
      return new NamespaceAssert<>( (A) actual.namespaces().get( 0 ) );
   }

   public SELF hasAtLeastOneNamespace() {
      namespaces().isNotEmpty();
      return myself;
   }

   public SELF hasNoNamespaces() {
      namespaces().isEmpty();
      return myself;
   }

   public ListAssert<Namespace> namespaces() {
      return assertThat( actual.namespaces() );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends ModelAssert<S, A>, A extends Model> ModelAssert<S, A> mergedModel() {
      return new ModelAssert<>( (A) actual.mergedModel() );
   }
}
