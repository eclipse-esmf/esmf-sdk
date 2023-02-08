/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.validation.services;

import io.openmanufacturing.sds.aspectmodel.shacl.RustLikeFormatter;
import io.openmanufacturing.sds.aspectmodel.shacl.fix.Fix;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ClassTypeViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ClosedViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.DatatypeViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.DisjointViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EqualsViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.InvalidValueViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.JsConstraintViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.LanguageFromListViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.LessThanOrEqualsViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.LessThanViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxCountViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxExclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxInclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxLengthViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinCountViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinExclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinInclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinLengthViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MissingTypeViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.NodeKindViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.NotViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.PatternViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.SparqlConstraintViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.UniqueLanguageViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ValueFromListViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

public class ViolationRustLikeFormatter extends ViolationFormatter {

   private final RustLikeFormatter formatter = new RustLikeFormatter();

   @Override
   public String visit( final Violation violation ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( violation.accept( this ) );
      for ( final Fix possibleFix : violation.fixes() ) {
         builder.append( String.format( "%n  > Possible fix: %s", possibleFix.description() ) );
      }
      return builder.toString();
   }

   @Override
   public String visitClassTypeViolation( final ClassTypeViolation violation ) {
      return formatter.constructDetailedMessage( violation.actualClass(), violation.message() );
   }

   @Override
   public String visitDatatypeViolation( final DatatypeViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message() );
   }

   @Override
   public String visitInvalidValueViolation( final InvalidValueViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitLanguageFromListViolation( final LanguageFromListViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitMaxCountViolation( final MaxCountViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.allowed() == 0 ? violation.context().element() : violation.context().property().get(),
            violation.message() );
   }

   @Override
   public String visitMaxExclusiveViolation( final MaxExclusiveViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitMaxInclusiveViolation( final MaxInclusiveViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitMaxLengthViolation( final MaxLengthViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitMinCountViolation( final MinCountViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.allowed() == 1 ? violation.context().element() : violation.context().property().get(),
            violation.message() );
   }

   @Override
   public String visitMinExclusiveViolation( final MinExclusiveViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitMinInclusiveViolation( final MinInclusiveViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitMinLengthViolation( final MinLengthViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitMissingTypeViolation( final MissingTypeViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().element(), violation.message() );
   }

   @Override
   public String visitNodeKindViolation( final NodeKindViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message() );
   }

   @Override
   public String visitPatternViolation( final PatternViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitSparqlConstraintViolation( final SparqlConstraintViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.bindings().get( "highlight" ) != null ? violation.bindings().get( "highlight" ) : violation.bindings().get( "this" ),
            violation.message() );
   }

   @Override
   public String visitUniqueLanguageViolation( final UniqueLanguageViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitEqualsViolation( final EqualsViolation violation ) {
      return formatter.constructDetailedMessage( violation.actualValue(), violation.message() );
   }

   @Override
   public String visitDisjointViolation( final DisjointViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitLessThanViolation( final LessThanViolation violation ) {
      return formatter.constructDetailedMessage( violation.actualValue(), violation.message() );
   }

   @Override
   public String visitLessThanOrEqualsViolation( final LessThanOrEqualsViolation violation ) {
      return formatter.constructDetailedMessage( violation.actualValue(), violation.message() );
   }

   @Override
   public String visitValueFromListViolation( final ValueFromListViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitClosedViolation( final ClosedViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitNotViolation( final NotViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message() );
   }

   @Override
   public String visitJsViolation( final JsConstraintViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message() );
   }
}
