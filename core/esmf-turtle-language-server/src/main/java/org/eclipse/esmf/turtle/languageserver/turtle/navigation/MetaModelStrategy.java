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

package org.eclipse.esmf.turtle.languageserver.turtle.navigation;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.urn.ElementType;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ResolutionStrategy} that resolves Aspect Model URNs referring to elements defined in the
 * SAMM meta model by serving bundled classpath resources.
 *
 * <p>
 * All known SAMM versions (1.0.0, 2.0.0, 2.1.0, 2.2.0, ...) are supported because the SDK JAR
 * ships every version's TTL files on the classpath under
 * {@code samm/{elementType}/{version}/{file}.ttl}.
 * The elementType is derived from the URN (e.g. {@code meta-model}, {@code characteristic},
 * {@code entity}, {@code unit}). The set of filenames per elementType is taken from the
 * {@link MetaModelFile} enum (which always lists the same filenames regardless of version).
 *
 * <p>
 * On first access, each resolved file is extracted to a temporary directory so the LSP client
 * receives a navigable {@code file://} URI. Subsequent requests for the same file reuse the cached
 * temp path.
 */
public class MetaModelStrategy implements ResolutionStrategy {
   private static final Logger LOG = LoggerFactory.getLogger( MetaModelStrategy.class );

   private static final Map<String, List<String>> ELEMENT_TYPE_FILENAMES = buildElementTypeFilenames();

   private final Path tempDir;
   private final Map<String, Path> extractedFiles = new ConcurrentHashMap<>();

   public MetaModelStrategy() {
      try {
         tempDir = Files.createTempDirectory( "samm-meta-model-" );
      } catch ( final IOException e ) {
         throw new IllegalStateException( "Could not create temp directory for SAMM meta model files", e );
      }
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn urn, final ResolutionStrategySupport support ) {
      if ( ElementType.NONE.equals( urn.getElementType() ) ) {
         throw new ModelResolutionException( "URN " + urn + " is not a SAMM meta model URN" );
      }

      final String elementType = urn.getElementType().getValue();
      final String version = urn.getVersion();
      final String urnString = urn.toString();

      final List<String> filenames = ELEMENT_TYPE_FILENAMES.getOrDefault( elementType, List.of() );
      if ( filenames.isEmpty() ) {
         throw new ModelResolutionException( "No meta model files known for SAMM elementType '" + elementType + "'" );
      }

      for ( final String filename : filenames ) {
         final String classpathPath = "samm/" + elementType + "/" + version + "/" + filename;
         final Optional<URL> url = classpathUrl( classpathPath );
         if ( url.isEmpty() ) {
            LOG.debug( "[meta-model-strategy] classpath resource not found: {}", classpathPath );
            continue;
         }
         final AspectModelFile candidate = AspectModelFileLoader.load( url.get() );
         if ( hasSubject( candidate.sourceModel(), urnString ) ) {
            final Path tempFile = extractToTempDir( elementType, version, filename,
                  candidate.sourceRepresentation() );
            return RawAspectModelFileBuilder.builder()
                  .sourceRepresentation( candidate.sourceRepresentation() )
                  .sourceModel( candidate.sourceModel() )
                  .sourceLocation( Optional.of( tempFile.toUri() ) )
                  .build();
         }
      }

      throw new ModelResolutionException(
            "No SAMM meta model file (version " + version + ") contains a definition for " + urn );
   }

   private Optional<URL> classpathUrl( final String path ) {
      return Optional.ofNullable( MetaModelStrategy.class.getClassLoader().getResource( path ) );
   }

   private boolean hasSubject( final Model model, final String urnString ) {
      return model.contains( model.createResource( urnString ), null, (RDFNode) null );
   }

   private Path extractToTempDir( final String elementType, final String version, final String filename,
         final String content ) {
      final String cacheKey = elementType + "/" + version + "/" + filename;
      return extractedFiles.computeIfAbsent( cacheKey, key -> {
         final String uniqueName = elementType + "-" + version + "-" + filename;
         final Path target = tempDir.resolve( uniqueName );
         try {
            Files.writeString( target, content );
            LOG.debug( "[meta-model-strategy] extracted {} to {}", cacheKey, target );
         } catch ( final IOException e ) {
            throw new ModelResolutionException( "Could not extract meta model file " + cacheKey, e );
         }
         return target;
      } );
   }

   /**
    * Builds a map from SAMM classpath section name to the list of TTL filenames in that section.
    * The filenames are taken from the {@link MetaModelFile} enum (which is version-agnostic in
    * terms of filenames — the same files exist for each supported version).
    */
   private static Map<String, List<String>> buildElementTypeFilenames() {
      final Map<String, List<String>> result = new HashMap<>();
      for ( final MetaModelFile entry : MetaModelFile.values() ) {
         final String namespace = entry.getRdfNamespace().getUri();
         final String elementType = AspectModelUrn.fromUrn( namespace ).getElementType().getValue();
         entry.filename().ifPresent( filename -> result.computeIfAbsent( elementType, s -> new ArrayList<>() ).add( filename ) );
      }
      return result;
   }

   @Override
   public Stream<URI> listContents() {
      return Stream.empty();
   }

   @Override
   public Stream<URI> listContentsForNamespace( final AspectModelUrn namespace ) {
      return Stream.empty();
   }

   @Override
   public Stream<AspectModelFile> loadContents() {
      return Stream.empty();
   }

   @Override
   public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn namespace ) {
      return Stream.empty();
   }
}
