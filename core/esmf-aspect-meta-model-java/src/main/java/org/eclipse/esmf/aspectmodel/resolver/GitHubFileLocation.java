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

package org.eclipse.esmf.aspectmodel.resolver;

import java.io.Serial;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import io.vavr.control.Try;

public record GitHubFileLocation(
      GithubRepository repositoryLocation,
      String directory,
      String namespaceMainPart,
      String version,
      String filename
) {
   public static Optional<GitHubFileLocation> parse( final String url ) {
      return Try.of( () -> new GitHubUrlParser( url ).get() ).toJavaOptional();
   }

   private static class ParsingException extends RuntimeException {
      @Serial
      private static final long serialVersionUID = -4855068216382713797L;
   }

   private static class GitHubUrlParser implements Supplier<GitHubFileLocation> {
      private int index = 0;
      private final String source;

      private GitHubUrlParser( final String source ) {
         this.source = source;
      }

      private String readSection() {
         final int oldIndex = index;
         while ( index < source.length() && source.charAt( index ) != '/' ) {
            index++;
         }
         if ( source.charAt( index ) != '/' ) {
            throw new ParsingException();
         }
         final String result = source.substring( oldIndex, index );
         index++; // eat the slash
         return result;
      }

      private void eatToken( final String token ) {
         if ( source.startsWith( token, index ) ) {
            index += token.length();
            return;
         }
         throw new ParsingException();
      }

      @Override
      public GitHubFileLocation get() {
         eatToken( "https://" );
         final String host = readSection();
         final String owner = readSection();
         final String repository = readSection();
         final GithubRepository.Ref ref;
         if ( source.substring( index ).startsWith( "blob" ) ) {
            eatToken( "blob/" );
            final String blob = readSection();
            ref = blob.matches( "[vV]?\\d+\\.\\d+.*" )
                  ? new GithubRepository.Tag( blob )
                  : new GithubRepository.Branch( blob );
         } else {
            eatToken( "raw/refs/" );
            if ( source.substring( index ).startsWith( "heads" ) ) {
               eatToken( "heads/" );
               final String branchName = readSection();
               ref = new GithubRepository.Branch( branchName );
            } else {
               eatToken( "tags/" );
               final String tag = readSection();
               ref = new GithubRepository.Tag( tag );
            }
         }
         final String rest = source.substring( index );
         final String[] parts = rest.split( "/" );
         final String fileName = parts[parts.length - 1];
         final String version = parts[parts.length - 2];
         final String namespaceMainPart = parts[parts.length - 3];
         final String directory = String.join( "/", Arrays.copyOfRange( parts, 0, parts.length - 3 ) );
         final GithubRepository repoLocation = new GithubRepository( host, owner, repository, ref );
         return new GitHubFileLocation( repoLocation, directory, namespaceMainPart, version, fileName );
      }
   }
}
