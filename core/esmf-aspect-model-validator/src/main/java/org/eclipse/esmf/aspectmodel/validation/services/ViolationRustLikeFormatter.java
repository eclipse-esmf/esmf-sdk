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

package org.eclipse.esmf.aspectmodel.validation.services;

import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.resolver.parser.RdfTextFormatter;
import org.eclipse.esmf.aspectmodel.shacl.RustLikeFormatter;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClassTypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClosedViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.DisjointViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.EqualsViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidValueViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.JsConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.LanguageFromListViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.LessThanOrEqualsViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.LessThanViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxCountViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxExclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxInclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxLengthViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinCountViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinExclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinInclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinLengthViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MissingTypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.NodeKindViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.NotViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.PatternViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.UniqueLanguageViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ValueFromListViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

public class ViolationRustLikeFormatter extends ViolationFormatter {

   private final RustLikeFormatter formatter;

   private final Model rawModel;

   public ViolationRustLikeFormatter() {
      rawModel = null;
      formatter = new RustLikeFormatter();
   }

   public ViolationRustLikeFormatter( final Model rawModel, final RdfTextFormatter formatter ) {
      this.rawModel = rawModel;
      this.formatter = new RustLikeFormatter( formatter );
   }

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
      return formatter.constructDetailedMessage( violation.actualClass(), violation.message(), rawModel );
   }

   @Override
   public String visitDatatypeViolation( final DatatypeViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message(), rawModel );
   }

   @Override
   public String visitInvalidValueViolation( final InvalidValueViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message(), rawModel );
   }

   @Override
   public String visitLanguageFromListViolation( final LanguageFromListViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message(), rawModel );
   }

   @Override
   public String visitMaxCountViolation( final MaxCountViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.allowed() == 0 ? violation.context().element() : violation.context().property().get(),
            violation.message(), rawModel );
   }

   @Override
   public String visitMaxExclusiveViolation( final MaxExclusiveViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message(), rawModel );
   }

   @Override
   public String visitMaxInclusiveViolation( final MaxInclusiveViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message(), rawModel );
   }

   @Override
   public String visitMaxLengthViolation( final MaxLengthViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message(), rawModel );
   }

   @Override
   public String visitMinCountViolation( final MinCountViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.allowed() == 1 ? violation.context().element() : violation.context().property().get(),
            violation.message(), rawModel );
   }

   @Override
   public String visitMinExclusiveViolation( final MinExclusiveViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message(), rawModel );
   }

   @Override
   public String visitMinInclusiveViolation( final MinInclusiveViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message(), rawModel );
   }

   @Override
   public String visitMinLengthViolation( final MinLengthViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message(), rawModel );
   }

   @Override
   public String visitMissingTypeViolation( final MissingTypeViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().element(), violation.message(), rawModel );
   }

   @Override
   public String visitNodeKindViolation( final NodeKindViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message(), rawModel );
   }

   @Override
   public String visitPatternViolation( final PatternViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message(), rawModel );
   }

   @Override
   public String visitSparqlConstraintViolation( final SparqlConstraintViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.bindings().get( "highlight" ) != null ? violation.bindings().get( "highlight" ) : violation.bindings().get( "this" ),
            violation.message(), rawModel );
   }

   @Override
   public String visitUniqueLanguageViolation( final UniqueLanguageViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message(), rawModel );
   }

   @Override
   public String visitEqualsViolation( final EqualsViolation violation ) {
      return formatter.constructDetailedMessage( violation.actualValue(), violation.message(), rawModel );
   }

   @Override
   public String visitDisjointViolation( final DisjointViolation violation ) {
      return formatter.constructDetailedMessage( violation.context().property().get(), violation.message(), rawModel );
   }

   @Override
   public String visitLessThanViolation( final LessThanViolation violation ) {
      return formatter.constructDetailedMessage( violation.actualValue(), violation.message(), rawModel );
   }

   @Override
   public String visitLessThanOrEqualsViolation( final LessThanOrEqualsViolation violation ) {
      return formatter.constructDetailedMessage( violation.actualValue(), violation.message(), rawModel );
   }

   @Override
   public String visitValueFromListViolation( final ValueFromListViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message(), rawModel );
   }

   @Override
   public String visitClosedViolation( final ClosedViolation violation ) {
      return formatter.constructDetailedMessage( violation.actual(), violation.message(), rawModel );
   }

   @Override
   public String visitNotViolation( final NotViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message(), rawModel );
   }

   @Override
   public String visitJsViolation( final JsConstraintViolation violation ) {
      return formatter.constructDetailedMessage(
            violation.context().property().isPresent() ? violation.context().property().get() : violation.context().element(),
            violation.message(), rawModel );
   }
}
