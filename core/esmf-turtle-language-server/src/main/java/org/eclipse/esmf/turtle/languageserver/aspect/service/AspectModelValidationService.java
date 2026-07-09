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

package org.eclipse.esmf.turtle.languageserver.aspect.service;

import java.net.URI;
import java.util.List;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.turtle.languageserver.lsp.LspUtil;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectModelValidationService {
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelValidationService.class );

   private final AspectModelLoader loader;
   private final AspectModelValidator validator;

   public AspectModelValidationService() {
      this( new AspectModelLoader(), new AspectModelValidator() );
   }

   AspectModelValidationService( final AspectModelLoader loader, final AspectModelValidator validator ) {
      this.loader = loader;
      this.validator = validator;
   }

   public List<Violation> validate( final RawAspectModelFile file ) {
      final List<Violation> violations = validate( file, loader );
      LOG.debug( "[validate] validation finished with {} violation(s)", violations.size() );
      return violations;
   }

   public List<Violation> validate( final RawAspectModelFile file, final ParsedDocument parsedDocument ) {
      final AspectModelLoader documentLoader = loaderFor( parsedDocument );
      final List<Violation> violations = validate( file, documentLoader );
      LOG.debug( "[validate] validation finished for {} with {} violation(s)", parsedDocument.getUri(), violations.size() );
      return violations;
   }

   private AspectModelLoader loaderFor( final ParsedDocument parsedDocument ) {
      final URI documentUri = URI.create( parsedDocument.getUri() );
      if ( documentUri.getScheme() == null ) {
         return new AspectModelLoader();
      }
      return new AspectModelLoader( LspUtil.buildResolutionStrategyForDocument( parsedDocument ) );
   }

   private List<Violation> validate( final RawAspectModelFile file, final AspectModelLoader modelLoader ) {
      return validator.validateModel( () -> modelLoader.loadRawAspectModelFile( file ) );
   }
}
