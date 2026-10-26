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

import org.apache.jena.rdf.model.RDFNode;

import org.eclipse.esmf.aspectmodel.ProjectInfo;
import org.eclipse.esmf.aspectmodel.ViolationCode;
import org.eclipse.esmf.aspectmodel.loader.TokenBasedElementFocussedViolation;

/**
 * The Aspect Model file uses a SAMM meta model term that is not defined in the meta model version
 * the
 * file declares, or it mixes meta model namespaces of different versions.
 *
 * <p>
 * This violation is not raised by a SHACL shape. It is determined while the file is loaded, before
 * it is migrated to the latest meta model version, because migration removes the information the
 * check
 * relies on. The message names the file it applies to.
 *
 * @param message the description of the inconsistency, including how to resolve it
 */
public record MetaModelVersionViolation(
      String message,
      RDFNode highlight
) implements TokenBasedElementFocussedViolation {
   public static final String ERROR_CODE = "ERR_METAMODEL_VERSION";

   @Override
   public Code code() {
      return new ViolationCode( ERROR_CODE, ProjectInfo.esmfErrorCodeUrl( ERROR_CODE ) );
   }
}
