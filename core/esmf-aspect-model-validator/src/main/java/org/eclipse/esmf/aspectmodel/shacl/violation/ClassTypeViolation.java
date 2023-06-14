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

import org.apache.jena.rdf.model.Resource;

public record ClassTypeViolation( EvaluationContext context, Resource allowedClass, Resource actualClass ) implements Violation {
   public static final String ERROR_CODE = "ERR_CLASS_TYPE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String message() {
      return String.format( "Property %s on %s has type %s, but only %s is allowed.",
            propertyName(), elementName(), shortUri( actualClass().getURI() ), shortUri( allowedClass().getURI() ) );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitClassTypeViolation( this );
   }
}
