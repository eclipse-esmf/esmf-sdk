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

package org.eclipse.esmf.aspectmodel.resolver;

import java.net.URI;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.DocumentLocationViolation;
import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.ProjectInfo;
import org.eclipse.esmf.aspectmodel.ViolationCode;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * An unresolved reference at its source location, retaining the resolution attempts for detailed
 * reporting.
 *
 * @param element the element that could not be resolved
 * @param sourceDocument the document containing the reference
 * @param documentContent the source document content
 * @param location the location of the reference within the document
 * @param checkedLocations the attempted resolution locations and their failures
 */
public record UnresolvedReferenceViolation(
      AspectModelUrn element,
      URI sourceDocument,
      Supplier<String> documentContent,
      Location location,
      List<ModelResolutionViolation> checkedLocations
) implements DocumentLocationViolation {

   @Override
   public String message() {
      return "Reference '%s' could not be resolved.".formatted( element.getName() );
   }

   @Override
   public Code code() {
      return new ViolationCode( ModelResolutionViolation.ERROR_CODE, ProjectInfo.esmfErrorCodeUrl( ModelResolutionViolation.ERROR_CODE ) );
   }
}
