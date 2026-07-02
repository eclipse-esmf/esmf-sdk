/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.lsp;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.eclipse.esmf.aspectmodel.resolver.EitherStrategy;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.fs.FlatModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.navigation.MetaModelStrategy;

public class LspUtil {

   private LspUtil() {
   }

   public static ResolutionStrategy buildResolutionStrategyForDocument( final ParsedDocument parsedDocument ) {
      final Path openFilePath;
      try {
         openFilePath = Path.of( new URI( parsedDocument.getUri() ) );
      } catch ( URISyntaxException e ) {
         throw new RuntimeException( e );
      }
      final FileSystemStrategy structuredStrategy = new FileSystemStrategy(
            new StructuredModelsRoot( openFilePath.getParent().getParent().getParent() ) );
      final FileSystemStrategy flatStrategy = new FileSystemStrategy( new FlatModelsRoot( openFilePath.getParent() ) );
      final MetaModelStrategy metaModelStrategy = new MetaModelStrategy();
      return new EitherStrategy( structuredStrategy, flatStrategy, metaModelStrategy );
   }
}
