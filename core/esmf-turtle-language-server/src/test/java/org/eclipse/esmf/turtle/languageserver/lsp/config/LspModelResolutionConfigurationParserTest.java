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

package org.eclipse.esmf.turtle.languageserver.lsp.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.esmf.aspectmodel.resolver.GithubRepository;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfig;

import org.junit.jupiter.api.Test;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

class LspModelResolutionConfigurationParserTest {
   private JsonElement json( final String content ) {
      return JsonParser.parseString( content );
   }

   @Test
   void testParseNullReturnsEmpty() {
      assertThat( LspModelResolutionConfigurationParser.parse( null ) ).isEmpty();
   }

   @Test
   void testParseMissingGithubNodeReturnsEmpty() {
      assertThat( LspModelResolutionConfigurationParser.parse( json( "{ \"unrelated\": true }" ) ) ).isEmpty();
   }

   @Test
   void testParseExamplePayload() {
      final List<GithubModelSourceConfig> result = LspModelResolutionConfigurationParser.parse( json( """
         {
           "semantic-models" : {
             "modelResolution" : {
               "githubRepositories" : [ {
                 "repository" : "eclipse-esmf/esmf-sdk",
                 "path" : "/",
                 "branch" : "main",
                 "token" : "test"
               } ]
             }
           }
         }
         """ ) );

      assertThat( result ).hasSize( 1 );
      final GithubModelSourceConfig config = result.get( 0 );
      assertThat( config.repository().host() ).isEqualTo( "github.com" );
      assertThat( config.repository().owner() ).isEqualTo( "eclipse-esmf" );
      assertThat( config.repository().repository() ).isEqualTo( "esmf-sdk" );
      assertThat( config.repository().branchOrTag() ).isInstanceOf( GithubRepository.Branch.class );
      assertThat( config.repository().branchOrTag().name() ).isEqualTo( "main" );
      assertThat( config.directory() ).isEmpty();
      assertThat( config.token() ).isEqualTo( "test" );
   }

   @Test
   void testParseDefaultsToMainBranchWhenNeitherBranchNorTagGiven() {
      final List<GithubModelSourceConfig> result = LspModelResolutionConfigurationParser.parse( json( """
         {
           "semantic-models" : {
             "modelResolution" : {
               "githubRepositories" : [ {
                 "repository" : "eclipse-esmf/esmf-sdk"
               } ]
             }
           }
         }
         """ ) );

      assertThat( result ).hasSize( 1 );
      assertThat( result.get( 0 ).repository().branchOrTag() ).isInstanceOf( GithubRepository.Branch.class );
      assertThat( result.get( 0 ).repository().branchOrTag().name() ).isEqualTo( "main" );
      assertThat( result.get( 0 ).directory() ).isEmpty();
      assertThat( result.get( 0 ).token() ).isNull();
   }

   @Test
   void testParseTagIsInterpretedAsTag() {
      final List<GithubModelSourceConfig> result = LspModelResolutionConfigurationParser.parse( json( """
         {
           "semantic-models" : {
             "modelResolution" : {
               "githubRepositories" : [ {
                 "repository" : "eclipse-esmf/esmf-sdk",
                 "tag" : "v2.9.0"
               } ]
             }
           }
         }
         """ ) );

      assertThat( result.get( 0 ).repository().branchOrTag() ).isInstanceOf( GithubRepository.Tag.class );
      assertThat( result.get( 0 ).repository().branchOrTag().name() ).isEqualTo( "v2.9.0" );
   }

   @Test
   void testParseTagTakesPrecedenceOverBranch() {
      final List<GithubModelSourceConfig> result = LspModelResolutionConfigurationParser.parse( json( """
         {
           "semantic-models" : {
             "modelResolution" : {
               "githubRepositories" : [ {
                 "repository" : "eclipse-esmf/esmf-sdk",
                 "branch" : "develop",
                 "tag" : "v2.9.0"
               } ]
             }
           }
         }
         """ ) );

      assertThat( result.get( 0 ).repository().branchOrTag() ).isInstanceOf( GithubRepository.Tag.class );
      assertThat( result.get( 0 ).repository().branchOrTag().name() ).isEqualTo( "v2.9.0" );
   }

   @Test
   void testParseSubDirectoryIsPassedThrough() {
      final List<GithubModelSourceConfig> result = LspModelResolutionConfigurationParser.parse( json( """
         {
           "semantic-models" : {
             "modelResolution" : {
               "githubRepositories" : [ {
                 "repository" : "eclipse-esmf/esmf-sdk",
                 "path" : "/some/directory"
               } ]
             }
           }
         }
         """ ) );

      assertThat( result.get( 0 ).directory() ).isEqualTo( "/some/directory" );
   }

   @Test
   void testParseMultipleRepositoriesWithIndividualTokens() {
      final List<GithubModelSourceConfig> result = LspModelResolutionConfigurationParser.parse( json( """
         {
           "semantic-models" : {
             "modelResolution" : {
               "githubRepositories" : [
                 { "repository" : "eclipse-esmf/esmf-sdk", "branch" : "main", "token" : "token-1" },
                 { "repository" : "eclipse-tractusx/sldt-semantic-models", "branch" : "main", "token" : "token-2" }
               ]
             }
           }
         }
         """ ) );

      assertThat( result ).hasSize( 2 );
      assertThat( result.get( 0 ).repository().repository() ).isEqualTo( "esmf-sdk" );
      assertThat( result.get( 0 ).token() ).isEqualTo( "token-1" );
      assertThat( result.get( 1 ).repository().repository() ).isEqualTo( "sldt-semantic-models" );
      assertThat( result.get( 1 ).token() ).isEqualTo( "token-2" );
   }

   @Test
   void testInvalidEntriesAreSkipped() {
      final List<GithubModelSourceConfig> result = LspModelResolutionConfigurationParser.parse( json( """
         {
           "semantic-models" : {
             "modelResolution" : {
               "githubRepositories" : [
                 { "repository" : "not-a-valid-entry" },
                 { "path" : "/some/directory" },
                 { "repository" : "eclipse-esmf/esmf-sdk", "branch" : "main" }
               ]
             }
           }
         }
         """ ) );

      assertThat( result ).hasSize( 1 );
      assertThat( result.get( 0 ).repository().owner() ).isEqualTo( "eclipse-esmf" );
   }
}
