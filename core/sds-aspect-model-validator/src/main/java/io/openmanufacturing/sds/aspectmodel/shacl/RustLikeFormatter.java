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

package io.openmanufacturing.sds.aspectmodel.shacl;

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
import io.openmanufacturing.sds.aspectmodel.validation.services.ViolationFormatter;

public class RustLikeFormatter extends ViolationFormatter {
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
      return MessageFormatter.constructDetailedMessage( violation.actualClass(), violation.message() );
   }

   @Override
   public String visitDatatypeViolation( final DatatypeViolation violation ) {
      return MessageFormatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message() );
   }

   @Override
   public String visitInvalidValueViolation( final InvalidValueViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitLanguageFromListViolation( final LanguageFromListViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitMaxCountViolation( final MaxCountViolation violation ) {
      return MessageFormatter.constructDetailedMessage(
            violation.allowed() == 0 ? violation.context().element() : violation.context().property().get(),
            violation.message() );
   }

   @Override
   public String visitMaxExclusiveViolation( final MaxExclusiveViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitMaxInclusiveViolation( final MaxInclusiveViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitMaxLengthViolation( final MaxLengthViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitMinCountViolation( final MinCountViolation violation ) {
      return MessageFormatter.constructDetailedMessage(
            violation.allowed() == 1 ? violation.context().element() : violation.context().property().get(),
            violation.message() );
   }

   @Override
   public String visitMinExclusiveViolation( final MinExclusiveViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitMinInclusiveViolation( final MinInclusiveViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitMinLengthViolation( final MinLengthViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitMissingTypeViolation( final MissingTypeViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.context().element(), violation.message() );
   }

   @Override
   public String visitNodeKindViolation( final NodeKindViolation violation ) {
      return MessageFormatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message() );
   }

   @Override
   public String visitPatternViolation( final PatternViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitSparqlConstraintViolation( final SparqlConstraintViolation violation ) {
      return MessageFormatter.constructDetailedMessage(
            violation.bindings().get( "highlight" ) != null ? violation.bindings().get( "highlight" ) : violation.bindings().get( "this" ),
            violation.message() );
   }

   @Override
   public String visitUniqueLanguageViolation( final UniqueLanguageViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitEqualsViolation( final EqualsViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actualValue(), violation.message() );
   }

   @Override
   public String visitDisjointViolation( final DisjointViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.context().property().get(), violation.message() );
   }

   @Override
   public String visitLessThanViolation( final LessThanViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actualValue(), violation.message() );
   }

   @Override
   public String visitLessThanOrEqualsViolation( final LessThanOrEqualsViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actualValue(), violation.message() );
   }

   @Override
   public String visitValueFromListViolation( final ValueFromListViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitClosedViolation( final ClosedViolation violation ) {
      return MessageFormatter.constructDetailedMessage( violation.actual(), violation.message() );
   }

   @Override
   public String visitNotViolation( final NotViolation violation ) {
      return MessageFormatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message() );
   }

   @Override
   public String visitJsViolation( final JsConstraintViolation violation ) {
      return MessageFormatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message() );
   }
}
