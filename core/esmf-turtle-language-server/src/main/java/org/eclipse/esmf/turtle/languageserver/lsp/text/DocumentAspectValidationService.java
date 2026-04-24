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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import java.util.List;

import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationError;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationErrorType;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationResult;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentAspectValidationService {
   private static final Logger LOG = LoggerFactory.getLogger( DocumentAspectValidationService.class );
   private final AspectValidationCoordinator aspectValidationCoordinator;

   public DocumentAspectValidationService( final AspectValidationCoordinator aspectValidationCoordinator ) {
      this.aspectValidationCoordinator = aspectValidationCoordinator;
   }

   public AspectValidationResult validateDocument( final String uri, final Document document ) {
      if ( document == null ) {
         return failedValidation( AspectValidationErrorType.LOAD, "Document is not available in memory: " + uri );
      }
      return aspectValidationCoordinator.validateSync( document );
   }

   private AspectValidationResult failedValidation( final AspectValidationErrorType type, final String message ) {
      return new AspectValidationResult( false, message, List.of(), new AspectValidationError( type, message ) );
   }
}
