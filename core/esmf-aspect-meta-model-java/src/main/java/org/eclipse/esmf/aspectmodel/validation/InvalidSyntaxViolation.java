/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.validation;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.DocumentLocationViolation;
import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.ProjectInfo;
import org.eclipse.esmf.aspectmodel.ViolationCode;
import org.eclipse.esmf.aspectmodel.ViolationCodeBuilder;
import org.eclipse.esmf.treesitterturtle.TurtleViolationCode;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Meta violation: Syntax error in source file
 *
 * @param message the error message
 * @param sourceDocument the source location of the violation
 * @param documentContent a supplier for the content of the source document
 * @param location the location in the source file
 */
@RecordBuilder
public record InvalidSyntaxViolation(
      String message,
      URI sourceDocument,
      Supplier<String> documentContent,
      Location location
) implements DocumentLocationViolation {
   public static final String ERROR_CODE = TurtleViolationCode.ERR_SYNTAX.code();

   @Override
   public Code code() {
      return ViolationCodeBuilder.from( (ViolationCode) TurtleViolationCode.ERR_SYNTAX )
            .withHref( Optional.of( ProjectInfo.esmfErrorCodeUrl( ERROR_CODE ) ) );
   }
}
