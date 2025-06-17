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

package org.eclipse.esmf.aspectmodel.shacl.violation;

import java.util.List;

import org.eclipse.esmf.aspectmodel.shacl.constraint.UniqueLangConstraint;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link UniqueLangConstraint}
 *
 * @param context the evaluation context
 * @param allowed the list of allowed languages
 * @param actual the encountered language
 */
public record LanguageFromListViolation( EvaluationContext context, List<String> allowed, String actual ) implements Violation {
   public static final String ERROR_CODE = "ERR_LANGUAGE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format( "Property %s on %s has language tag %s, which is not in the list of allowed languages: %s.",
            context.propertyName(), context.elementName(), actual, allowed );
   }

   @Override
   public RDFNode highlight() {
      return context().property().get();
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitLanguageFromListViolation( this );
   }
}
