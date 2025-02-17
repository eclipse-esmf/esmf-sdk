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

import java.util.function.Predicate;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;

import org.assertj.core.api.AbstractIterableAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.data.Index;
import org.assertj.core.internal.Iterables;
import org.assertj.core.internal.Lists;
import org.assertj.core.presentation.PredicateDescription;

/**
 * Assert for {@link Namespace}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class NamespaceAssert<SELF extends NamespaceAssert<SELF, ACTUAL>, ACTUAL extends Namespace>
      extends AbstractHasDescriptionAssert<SELF, ACTUAL> {
   protected Lists lists = Lists.instance();
   protected Iterables iterables = Iterables.instance();

   public NamespaceAssert( final ACTUAL actual ) {
      super( actual, NamespaceAssert.class );
   }

   public SELF contains( final ModelElement modelElement ) {
      assertThat( actual.elements() ).contains( modelElement );
      return myself;
   }

   public ListAssert<ModelElement> elements() {
      return assertThat( actual.elements() );
   }

   /**
    * @see AbstractListAssert#contains(Object, Index)
    */
   public SELF contains( final ModelElement value, final Index index ) {
      lists.assertContains( info, actual.elements(), value, index );
      return myself;
   }

   /**
    * @see AbstractListAssert#doesNotContain(Object, Index)
    */
   public SELF doesNotContain( final ModelElement value, final Index index ) {
      lists.assertDoesNotContain( info, actual.elements(), value, index );
      return myself;
   }

   /**
    * @see AbstractIterableAssert#allMatch(Predicate)
    */
   public SELF allElementsMatch( final Predicate<? super ModelElement> predicate ) {
      iterables.assertAllMatch( info, actual.elements(), predicate, PredicateDescription.GIVEN );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends AspectModelFileAssert<S, A>, A extends AspectModelFile> AspectModelFileAssert<S, A> sourceFile() {
      if ( actual.source().isEmpty() ) {
         failWithMessage( "Expected Namespace <%s> to have a source file, but it didn't", actual.urn().toString() );
      }
      return new AspectModelFileAssert<>( (A) actual.source().orElseThrow() );
   }

   @SuppressWarnings( "unchecked" )
   public <S extends AspectModelUrnAssert<S, A>, A extends AspectModelUrn> AspectModelUrnAssert<S, A> urn() {
      return new AspectModelUrnAssert<>( (A) actual.urn() );
   }
}
