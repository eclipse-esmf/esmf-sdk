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

package io.openmanufacturing.sds.aspectmodel.shacl.violation;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.aspectmodel.shacl.fix.Fix;

public interface Violation {
   String errorCode();

   EvaluationContext context();

   String message();

   <T> T accept( Visitor<T> visitor );

   enum AppliesTo {
      WHOLE_ELEMENT, ONLY_PROPERTY
   }

   interface Visitor<T> {
      T visit( final Violation violation );

      default T visitClassTypeViolation( final ClassTypeViolation violation ) {
         return visit( violation );
      }

      default T visitDatatypeViolation( final DatatypeViolation violation ) {
         return visit( violation );
      }

      default T visitInvalidValueViolation( final InvalidValueViolation violation ) {
         return visit( violation );
      }

      default T visitLanguageFromListViolation( final LanguageFromListViolation violation ) {
         return visit( violation );
      }

      default T visitMaxCountViolation( final MaxCountViolation violation ) {
         return visit( violation );
      }

      default T visitMaxExclusiveViolation( final MaxExclusiveViolation violation ) {
         return visit( violation );
      }

      default T visitMaxInclusiveViolation( final MaxInclusiveViolation violation ) {
         return visit( violation );
      }

      default T visitMaxLengthViolation( final MaxLengthViolation violation ) {
         return visit( violation );
      }

      default T visitMinCountViolation( final MinCountViolation violation ) {
         return visit( violation );
      }

      default T visitMinExclusiveViolation( final MinExclusiveViolation violation ) {
         return visit( violation );
      }

      default T visitMinInclusiveViolation( final MinInclusiveViolation violation ) {
         return visit( violation );
      }

      default T visitMinLengthViolation( final MinLengthViolation violation ) {
         return visit( violation );
      }

      default T visitMissingTypeViolation( final MissingTypeViolation violation ) {
         return visit( violation );
      }

      default T visitNodeKindViolation( final NodeKindViolation violation ) {
         return visit( violation );
      }

      default T visitPatternViolation( final PatternViolation violation ) {
         return visit( violation );
      }

      default T visitSparqlConstraintViolation( final SparqlConstraintViolation violation ) {
         return visit( violation );
      }

      default T visitUniqueLanguageViolation( final UniqueLanguageViolation violation ) {
         return visit( violation );
      }

      default T visitEqualsViolation( final EqualsViolation violation ) {
         return visit( violation );
      }

      default T visitDisjointViolation( final DisjointViolation violation ) {
         return visit( violation );
      }

      default T visitLessThanViolation( final LessThanViolation violation ) {
         return visit( violation );
      }

      default T visitLessThanOrEqualsViolation( final LessThanOrEqualsViolation violation ) {
         return visit( violation );
      }

      default T visitValueFromListViolation( final ValueFromListViolation violation ) {
         return visit( violation );
      }

      default T visitClosedViolation( final ClosedViolation violation ) {
         return visit( violation );
      }
   }

   default AppliesTo appliesTo() {
      return AppliesTo.WHOLE_ELEMENT;
   }

   default String shortUri( final String uri ) {
      return context().element().getModel().shortForm( uri );
   }

   default String elementName() {
      return Optional.ofNullable( context().element().getURI() )
            .map( this::shortUri )
            .orElse( "anonymous element" );
   }

   default String propertyName() {
      return context().property().map( Resource::getURI ).map( this::shortUri ).orElse( elementName() );
   }

   default List<Fix> fixes() {
      return List.of();
   }
}

