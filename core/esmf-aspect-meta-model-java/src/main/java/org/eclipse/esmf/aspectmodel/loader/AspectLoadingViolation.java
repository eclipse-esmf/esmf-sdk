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

package org.eclipse.esmf.aspectmodel.loader;

import java.net.URI;
import java.util.function.Supplier;

import org.apache.jena.rdf.model.RDFNode;

import org.eclipse.esmf.aspectmodel.DocumentLocationViolation;
import org.eclipse.esmf.aspectmodel.ElementFocussedViolation;
import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.ProjectInfo;
import org.eclipse.esmf.aspectmodel.ViolationCode;
import org.eclipse.esmf.aspectmodel.resolver.parser.SmartToken;
import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Represents the failure to load an Aspect Model or Aspect Model file.
 *
 * @param message the specific message
 * @param sourceDocument the document source location
 * @param documentContent the content of the source document
 * @param highlight the model element this violation is focussed on
 */
@RecordBuilder
public record AspectLoadingViolation(
      String message,
      URI sourceDocument,
      Supplier<String> documentContent,
      RDFNode highlight
) implements ElementFocussedViolation {
   public static final String ERROR_CODE = "ERR_ASPECT_LOADING";

   @Override
   public Code code() {
      return new ViolationCode( ERROR_CODE, ProjectInfo.esmfErrorCodeUrl( ERROR_CODE ) );
   }

   @Override
   public Location location() {
      return TokenRegistry.getToken( highlight().asNode() )
            .map( SmartToken::location ).orElse( DocumentLocationViolation.WHOLE_DOCUMENT );
   }
}
