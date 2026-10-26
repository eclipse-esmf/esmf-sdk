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

package org.eclipse.esmf.turtle.languageserver.aspect;

import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestModel;
import org.eclipse.esmf.test.TestResources;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

public class TestUtil {
   public static ParsedDocument emptyParsedDocument() {
      return parsedDocument( "test.ttl", "" );
   }

   public static ParsedDocument parsedDocument( final String fileName, final String content ) {
      final TreeSitterTurtleParserService parserService = new TreeSitterTurtleParserService();
      final Document document = new Document( new File( fileName ).toURI().toString(), content );
      parserService.onOpen( document );
      return parserService.apply( document );
   }

   public static ParsedDocument parsedDocument( final TestModel testModel ) {
      final String modelContent;
      try {
         final InputStream inputStream = testModel instanceof final InvalidTestAspect invalidTestAspect
               ? TestResources.inputStream( invalidTestAspect ).inputStream()
               : TestResources.inputStream( testModel ).inputStream();
         modelContent = IOUtils.toString( inputStream, StandardCharsets.UTF_8 );
         final Document document = new Document( new File( testModel.getName() + ".ttl" ).toURI().toString(), modelContent );
         return new TreeSitterTurtleParserService().apply( document );
      } catch ( final IOException exception ) {
         fail( exception );
         throw new RuntimeException( exception );
      }
   }
}
