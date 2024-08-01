/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.scanner.AspectModelScanner;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubScanner implements AspectModelScanner {

   private static final Logger LOG = LoggerFactory.getLogger( GitHubScanner.class );

   private static final String GITHUB_BASE = "https://github.com";

   private static final String GITHUB_ZIP_URL = GITHUB_BASE + "/%s/archive/refs/heads/%s.zip";

   private static final String DOWNLOADED_ZIP_NAME = "%s.zip";
   protected final String repositoryName;

   protected final String branchName;

   public GitHubScanner( final String repositoryName, final String branchName ) {
      this.repositoryName = repositoryName;
      this.branchName = branchName;
   }

   /**
    * Finds and returns a list of valid {@link AspectModelFile} objects
    * that match the specified aspect model file name.
    *
    * <ol>
    *    <li>Connects to GitHub anonymously and retrieves the specified repository.</li>
    *    <li>Checks if the specified aspect model file exists in the repository.</li>
    *    <li>If the file exists, it retrieves the file content and loads the aspect model using {@link AspectModelLoader}.</li>
    *    <li>Downloads the entire repository (or specified branch) as a ZIP file.</li>
    *    <li>Processes the downloaded package to extract aspect model files.</li>
    *    <li>Deletes the downloaded package after processing.</li>
    *    <li>Finds and returns the aspect model files that contain definitions matching the URNs in the search aspect model.</li>
    * </ol>
    *
    * @param aspectModelFileUrl The url of the aspect model file to search for in the repository.
    * @return A list of {@link AspectModelFile} objects that match the specified aspect model file name
    * by {@link AspectModelUrn}.
    * @throws RuntimeException if an I/O error occurs during the process.
    */
   @Override
   public List<AspectModelFile> find( final String aspectModelFileUrl ) {
      List<AspectModelFile> resultAspectModelFiles = new ArrayList<>();
      try {
         final GitHub github = GitHub.connectAnonymously();
         final GHRepository repository = github.getRepository( repositoryName );

         if ( checkFileExists( repository, aspectModelFileUrl ) ) {

            final GHContent contentOfSearchFile = repository.getFileContent( aspectModelFileUrl, branchName );

            final AspectModelLoader aspectModelLoader = new AspectModelLoader();

            final AspectModel searchAspectModel = aspectModelLoader.load( new URL( contentOfSearchFile.getDownloadUrl() ).openStream() );

            final String githubUrl = String.format( GITHUB_ZIP_URL, repositoryName, branchName );
            final String downloadedPackageName = String.format( DOWNLOADED_ZIP_NAME, branchName );
            final File downloadedPackage = downloadFile( new URL( githubUrl ), downloadedPackageName );

            final List<AspectModelFile> filesInPackage = processPackage( new FileInputStream( downloadedPackage ) );

            final boolean packageIsDeleted = downloadedPackage.delete();
            if ( packageIsDeleted ) {
               LOG.info( String.format( "Package %s was deleted!", downloadedPackage.getName() ) );
            }

            final List<AspectModelUrn> searchAspectUrns = searchAspectModel.elements().stream().map( ModelElement::urn ).toList();

            for ( final AspectModelFile aspectModelFile : filesInPackage ) {
               final Set<AspectModelUrn> urnsAspectModelFile = AspectModelLoader.getAllUrnsInModel( aspectModelFile.sourceModel() );

               for ( final AspectModelUrn aspectModelUrn : searchAspectUrns ) {
                  if ( urnsAspectModelFile.contains( aspectModelUrn ) ) {
                     resultAspectModelFiles.add( aspectModelFile );
                  }
               }
            }
         }
      } catch ( IOException e ) {
         throw new RuntimeException( e );
      }

      return resultAspectModelFiles;
   }

   private boolean checkFileExists( final GHRepository repository, final String aspectModelFileUrl ) {
      try {
         final GHContent content = repository.getFileContent( aspectModelFileUrl, branchName );
         return content != null;
      } catch ( IOException e ) {
         throw new RuntimeException(
               new IOException( String.format( "File %s can't found in %s repository.", aspectModelFileUrl, repository.getUrl() ) ) );
      }
   }

   private File downloadFile( final URL repositoryUrl, final String outputFileName ) throws IOException {
      LOG.info( String.format( "Downloading %s repository to local...", repositoryUrl.getPath() ) );

      ReadableByteChannel rbc;
      File outputFile = new File( outputFileName );
      try ( final BufferedInputStream bis = new BufferedInputStream( repositoryUrl.openStream() );
            final FileOutputStream fos = new FileOutputStream( outputFileName ) ) {
         rbc = Channels.newChannel( bis );
         fos.getChannel().transferFrom( rbc, 0, Long.MAX_VALUE );
      } catch ( FileNotFoundException e ) {
         throw new FileNotFoundException( String.format( "Can't download repository, file %s not found! %s", repositoryName, e ) );
      } catch ( IOException e ) {
         throw new IOException( String.format( "Can't write zip file %s", outputFileName ) );
      }

      LOG.info( String.format( "Downloaded %s repository to local.", repositoryUrl.getPath() ) );

      return outputFile;
   }

   /**
    * This method provides valid files from package
    *
    * @param inputStream of repository package
    * @return list of valid {@link AspectModelFile} from package
    */
   private List<AspectModelFile> processPackage( final InputStream inputStream ) {
      List<AspectModelFile> aspectModelFiles = new ArrayList<>();

      try ( ZipInputStream zis = new ZipInputStream( inputStream ) ) {
         ZipEntry entry;

         while ( (entry = zis.getNextEntry()) != null ) {
            if ( entry.getName().endsWith( ".ttl" ) ) {
               final String content = new BufferedReader( new InputStreamReader( zis, StandardCharsets.UTF_8 ) ).lines()
                     .collect( Collectors.joining( "\n" ) );
               final Try<Model> tryModel = TurtleLoader.loadTurtle( content );
               if ( !tryModel.isFailure() ) {
                  final AspectModelFile aspectModelFile = new RawAspectModelFile( tryModel.get(), new ArrayList<>(), Optional.empty() );
                  aspectModelFiles.add( aspectModelFile );
               }
            }
         }

         zis.closeEntry();
      } catch ( IOException e ) {
         LOG.error( "Error reading the Package input stream", e );
         throw new RuntimeException( new IOException( "Error reading the Package input stream", e ) );
      }

      return aspectModelFiles;
   }
}
