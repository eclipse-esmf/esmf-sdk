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
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.DocumentLocationViolation;
import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.Violation;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Default implementation for DocumentDocumentLocationViolation
 *
 * @param message see {@link DocumentLocationViolation#message()}
 * @param code see {@link DocumentLocationViolation#code()}
 * @param sourceDocument see {@link DocumentLocationViolation#sourceDocument()}
 * @param documentContent see {@link DocumentLocationViolation#documentContent()}
 * @param location see {@link DocumentLocationViolation#location()}
 * @param severity see {@link DocumentLocationViolation#severity()}
 */
@RecordBuilder
public record DefaultDocumentLocationViolation(
      String message,
      Violation.Code code,
      URI sourceDocument,
      Supplier<String> documentContent,
      Location location,
      Violation.Severity severity
) implements DocumentLocationViolation {
   public DefaultDocumentLocationViolation {
      if ( severity == null ) {
         severity = Severity.ERROR;
      }
   }
}
