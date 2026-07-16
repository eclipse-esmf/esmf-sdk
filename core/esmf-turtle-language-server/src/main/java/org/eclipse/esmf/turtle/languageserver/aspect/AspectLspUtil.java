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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.EitherStrategy;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.fs.FlatModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

public class AspectLspUtil {
   private static final List<String> SAMM_PREFIXES = SammNs.sammNamespaces().map( RdfNamespace::getShortForm ).toList();

   public static boolean isAspectModel( final TurtleSyntaxTree syntaxTree ) {
      return syntaxTree.rootNode().children().stream()
            .filter( n -> ParserTokenType.DIRECTIVE.equals( n.type() ) ).flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.PREFIX_ID.equals( n.type() ) ).flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.NAMESPACE.equals( n.type() ) ).flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.PN_PREFIX.equals( n.type() ) )
            .anyMatch( n -> SAMM_PREFIXES.contains( n.content() ) );
   }

   public static ResolutionStrategy buildResolutionStrategyForDocument( final ParsedDocument parsedDocument ) {
      final Path openFilePath;
      try {
         openFilePath = Path.of( new URI( parsedDocument.getUri() ) );
      } catch ( final URISyntaxException | IllegalArgumentException exception ) {
         throw new ModelResolutionException( "Failed to parse URI: " + parsedDocument.getUri(), exception );
      }
      final FileSystemStrategy structuredStrategy = new FileSystemStrategy(
            new StructuredModelsRoot( openFilePath.getParent().getParent().getParent() ) );
      final FileSystemStrategy flatStrategy = new FileSystemStrategy( new FlatModelsRoot( openFilePath.getParent() ) );
      final MetaModelStrategy metaModelStrategy = new MetaModelStrategy();
      return new EitherStrategy( structuredStrategy, flatStrategy, metaModelStrategy );
   }

   public static RawAspectModelFile documentToAspectModelFile( final ParsedDocument parsedDocument ) {
      return AspectModelFileLoader.load( parsedDocument.turtleSyntaxTree(), URI.create( parsedDocument.getUri() ) );
   }
}
