/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.esmf.aspectmodel.AspectLoadingException;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.serializer.SerializationException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.versionupdate.MetaModelVersionMigrator;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Namespace;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a namespace package as described in
 * <a href="https://github.com/eclipse-esmf/esmf-semantic-aspect-meta-model/blob/main/documentation/decisions/0009-namespace-packages.md">
 * ADR-0009</a>. A namespace package can be created from its raw representation in a zip file, via
 * {@link #NamespacePackage(byte[], URI)} (i.e., a "loading" operation), or from an existing {@link AspectModel} (i.e., a "writing"
 * operation). A namespace package is also a {@link ResolutionStrategy} (model elements are resolved from its contents).
 */
public class NamespacePackage implements ResolutionStrategy, Artifact<URI, byte[]> {
   private static final Logger LOG = LoggerFactory.getLogger( NamespacePackage.class );
   private static final String ASPECT_MODELS_FOLDER = "aspect-models";

   // Fields that are always set
   private final String modelsRoot;
   private final List<AspectModelFile> files;

   // Fields that are only set when reading from ZIP
   private final URI location;
   private final byte[] content;

   // Fields that are only set when wrapping an existing AspectModel
   private final AspectModel aspectModel;

   /**
    * Create a NamespacePackage from an existing ZIP file, i.e., a "loading" operation
    *
    * @param source the input stream providing the binary content of the zip file
    * @param location the logical location of the input file (e.g., a local file URI or URL)
    */
   public NamespacePackage( final InputStream source, final URI location ) {
      this( readInputStream( source ), location );
   }

   private static byte[] readInputStream( final InputStream source ) {
      try {
         return IOUtils.toByteArray( source );
      } catch ( final IOException exception ) {
         throw new AspectLoadingException( exception );
      }
   }

   /**
    * Create a NamespacePackage from an existing ZIP file, i.e., a "loading" operation
    *
    * @param content the binary content of the input file
    * @param location the logical location of the input file (e.g., a local file URI or URL)
    */
   public NamespacePackage( final byte[] content, final URI location ) {
      this.location = location;
      this.content = content;
      modelsRoot = findModelsRootInZip();
      files = loadZipContent();
      aspectModel = null;
   }

   /**
    * Create a NamespacePackage from an existing Aspect Model that should be serialized, i.e., a "save as" operation
    *
    * @param aspectModel the input aspect model
    */
   public NamespacePackage( final AspectModel aspectModel ) {
      location = null;
      content = null;
      modelsRoot = "";
      files = aspectModel.files();
      this.aspectModel = aspectModel;
   }

   /**
    * Determines the location of the models root inside the package zip, as described in
    * <a
    * href="https://github.com/eclipse-esmf/esmf-semantic-aspect-meta-model/blob/main/documentation/decisions/0009-namespace-packages
    * .md#namespace-package-specification">ADR-0009</a>.
    *
    * @return The location of the models root, e.g., "", "aspect-models" or "something/aspect-models"
    */
   private String findModelsRootInZip() {
      final List<String> directoryNames = new ArrayList<>();
      try ( final ZipInputStream zis = new ZipInputStream( new ByteArrayInputStream( content ) ) ) {
         for ( ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry() ) {
            if ( entry.isDirectory() ) {
               directoryNames.add( entry.getName().replaceAll( "/?$", "" ) );
            }
         }
      } catch ( final IOException exception ) {
         LOG.error( "Error reading the archive input stream", exception );
         throw new AspectLoadingException( "Error reading the namespace package input stream", exception );
      }

      // If a directory aspect-models exists in / of the ZIP file, it is the models root.
      if ( directoryNames.contains( ASPECT_MODELS_FOLDER ) ) {
         return ASPECT_MODELS_FOLDER;
      }
      // Otherwise, the directories in / of the ZIP are traversed non-recursively in lexicographically sorted order (by en-US locale)
      // and the first subdirectory of any of them that is called aspect-models is the models root.
      final Collator collator = Collator.getInstance( Locale.forLanguageTag( "en-US" ) );
      collator.setStrength( Collator.PRIMARY );
      directoryNames.sort( collator );
      for ( final String directoryName : directoryNames ) {
         final String[] parts = directoryName.split( "/" );
         if ( parts.length == 2 && directoryName.endsWith( "/" + ASPECT_MODELS_FOLDER ) ) {
            return directoryName;
         }
      }
      // Otherwise, if no such directory exists, / of the ZIP file is the models root.
      return "";
   }

   /**
    * Constructs the "jar:" URL as described in {@link java.net.JarURLConnection}, as a URI.
    *
    * @param filePath the relative path inside the file
    * @return the full jar URL
    */
   private URI constructLocationForFile( final String filePath ) {
      return URI.create( "jar:%s!/%s".formatted( location, filePath ) );
   }

   private List<AspectModelFile> loadZipContent() {
      final ImmutableList.Builder<AspectModelFile> builder = ImmutableList.builder();
      try ( final ZipInputStream inputStream = new ZipInputStream( new ByteArrayInputStream( content ) ) ) {
         for ( ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry() ) {
            if ( entry.getName().startsWith( modelsRoot ) && entry.getName().endsWith( ".ttl" ) ) {
               final RawAspectModelFile rawFile = AspectModelFileLoader.load( inputStream,
                     Optional.of( constructLocationForFile( entry.getName() ) ) );
               builder.add( rawFile );
            }
         }
      } catch ( final IOException exception ) {
         LOG.error( "Error reading the archive input stream", exception );
         throw new ModelResolutionException( "Error reading the archive input stream", exception );
      }
      return builder.build();
   }

   @Override
   public Stream<URI> listContents() {
      if ( content == null ) {
         return aspectModel.files().stream().flatMap( f -> f.sourceLocation().stream() );
      }

      final List<URI> contents = new ArrayList<>();
      try ( final ZipInputStream inputStream = new ZipInputStream( new ByteArrayInputStream( content ) ) ) {
         for ( ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry() ) {
            if ( entry.getName().endsWith( ".ttl" ) ) {
               contents.add( constructLocationForFile( entry.getName() ) );
            }
         }
      } catch ( final IOException exception ) {
         LOG.error( "Error reading the archive input stream", exception );
         throw new ModelResolutionException( "Error reading the archive input stream", exception );
      }
      return contents.stream();
   }

   @Override
   public Stream<URI> listContentsForNamespace( final AspectModelUrn namespace ) {
      final String pathToFilter = modelsRoot.isEmpty()
            ? namespace.getNamespaceMainPart() + "/" + namespace.getVersion()
            : modelsRoot + "/" + namespace.getNamespaceMainPart() + "/" + namespace.getVersion();
      return listContents().filter( uri ->
            uri.toString().contains( pathToFilter ) );
   }

   /**
    * Similar to {@link #loadContents()} except files are not automatically migrated to the latest SAMM version.
    *
    * @return The stream of files
    */
   public Stream<AspectModelFile> loadLiteralFiles() {
      return files.stream();
   }

   @Override
   public Stream<AspectModelFile> loadContents() {
      return files.stream()
            .map( MetaModelVersionMigrator.INSTANCE );
   }

   @Override
   public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn namespace ) {
      final String pathToFilter = modelsRoot.isEmpty()
            ? namespace.getNamespaceMainPart() + "/" + namespace.getVersion()
            : modelsRoot + "/" + namespace.getNamespaceMainPart() + "/" + namespace.getVersion();
      return loadContents()
            .filter( file -> file.sourceLocation().map(
                  uri -> uri.toString().contains( pathToFilter )
            ).orElse( false ) );
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport )
         throws ModelResolutionException {
      return loadContentsForNamespace( aspectModelUrn )
            .filter( file -> resolutionStrategySupport.containsDefinition( file, aspectModelUrn ) )
            .findFirst()
            .orElseThrow( () -> new ModelResolutionException(
                  new ModelResolutionException.LoadingFailure( aspectModelUrn, location.toString(),
                        "Namespace package " + location + " does not contain definition for " + aspectModelUrn ) ) );
   }

   @Override
   public URI getId() {
      return location;
   }

   @Override
   public byte[] getContent() {
      return serialize();
   }

   private void addDirectory( final ZipOutputStream zipOutputStream, final String directory ) throws IOException {
      final ZipEntry directoryEntry = new ZipEntry( directory );
      zipOutputStream.putNextEntry( directoryEntry );
      zipOutputStream.closeEntry();
   }

   private void addFile( final ZipOutputStream zipOutputStream, final String path, final byte[] content ) throws IOException {
      final ZipEntry fileEntry = new ZipEntry( path );
      zipOutputStream.putNextEntry( fileEntry );
      zipOutputStream.write( content, 0, content.length );
      zipOutputStream.closeEntry();
   }

   private String outputFileName( final AspectModelFile aspectModelFile ) {
      return aspectModelFile.filename().orElseGet( () -> "definitions" + aspectModelFile.hashCode() + ".ttl" );
   }

   @Override
   public byte[] serialize() {
      if ( content != null ) {
         return content;
      }

      final Set<String> directoryEntries = new HashSet<>();
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      try ( final ZipOutputStream out = new ZipOutputStream( buffer ) ) {
         for ( final Map.Entry<Namespace, List<AspectModelFile>> namespaceEntry : files.stream()
               .collect( Collectors.groupingBy( AspectModelFile::namespace ) ).entrySet() ) {
            final Namespace namespace = namespaceEntry.getKey();
            final String namespaceMainPartDirectoryEntry = "%s/".formatted( namespace.namespaceMainPart() );
            if ( !directoryEntries.contains( namespaceMainPartDirectoryEntry ) ) {
               addDirectory( out, namespaceMainPartDirectoryEntry );
               directoryEntries.add( namespaceMainPartDirectoryEntry );
            }
            final String namespaceVersionDirectoryEntry = "%s/%s/".formatted( namespace.namespaceMainPart(), namespace.version() );
            if ( !directoryEntries.contains( namespaceVersionDirectoryEntry ) ) {
               addDirectory( out, namespaceVersionDirectoryEntry );
               directoryEntries.add( namespaceVersionDirectoryEntry );
            }
            for ( final AspectModelFile file : namespaceEntry.getValue() ) {
               final String filePath = "%s/%s/%s".formatted( namespace.namespaceMainPart(), namespace.version(), outputFileName( file ) );
               final byte[] fileContent = AspectSerializer.INSTANCE.aspectModelFileToString( file ).getBytes( StandardCharsets.UTF_8 );
               addFile( out, filePath, fileContent );
            }
         }
      } catch ( final IOException exception ) {
         throw new SerializationException( exception );
      }

      return buffer.toByteArray();
   }

   /**
    * Returns the location of the namespace package, if it was created from an external location. If it was created from an in-memory
    * {@link AspectModel}, the returned location is null.
    *
    * @return the location if set, or null
    */
   public URI getLocation() {
      return location;
   }

   public String getModelsRoot() {
      return modelsRoot;
   }
}
