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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;

public record GithubRepository(
      String host,
      String owner,
      String repository,
      Ref branchOrTag
) {
   public GithubRepository( final String owner, final String repository, final Ref branchOrTag ) {
      this( "github.com", owner, repository, branchOrTag );
   }

   public sealed interface Ref {
      String name();

      String refType();

      String refTypeName();
   }

   public record Branch( String name ) implements Ref {
      @Override
      public String refType() {
         return "heads";
      }

      @Override
      public String refTypeName() {
         return "branch";
      }
   }

   public record Tag( String name ) implements Ref {
      @Override
      public String refType() {
         return "tags";
      }

      @Override
      public String refTypeName() {
         return "tag";
      }
   }

   public URL zipLocation() {
      // See https://docs.github.com/en/rest/repos/contents?apiVersion=2022-11-28#download-a-repository-archive-zip
      // General URL structure: https://api.github.com/repos/OWNER/REPO/zipball/REF
      final String theHost = host().equals( "github.com" ) ? "api.github.com" : host();
      final String url = "https://%s/repos/%s/%s/zipball/%s".formatted( theHost, owner(), repository(), branchOrTag().name() );
      try {
         return new URL( url );
      } catch ( final MalformedURLException exception ) {
         throw new ModelResolutionException( "Constructed GitHub URL is invalid: " + url );
      }
   }

   @Override
   public String toString() {
      return "GitHub repository %s/%s (%s %s)".formatted( owner(), repository(), branchOrTag().refTypeName(),
            branchOrTag().name() );
   }
}
