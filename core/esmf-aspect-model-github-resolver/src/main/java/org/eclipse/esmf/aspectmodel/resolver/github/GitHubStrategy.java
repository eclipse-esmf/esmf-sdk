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

package org.eclipse.esmf.aspectmodel.resolver.github;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * A resolution strategy to retrieve files from a remote GitHub repository
 */
public class GitHubStrategy extends GitHubModelSource implements ResolutionStrategy {
   /**
    * Constructor.
    *
    * @param repositoryName the repository name in the form 'org/reponame'
    * @param branchName the branch name
    * @param directory the directory in the repository in the form 'some/directory', empty for root
    * @param config sets proxy configuration
    */
   public GitHubStrategy( final String repositoryName, final String branchName, final String directory, final Config config ) {
      super( repositoryName, branchName, directory, config );
   }

   /**
    * Constructor. Proxy settings are automatically detected.
    *
    * @param repositoryName the repository name in the form 'org/reponame'
    * @param branchName the branch name
    * @param directory the directory in the repository in the form 'some/directory', empty for root
    */
   public GitHubStrategy( final String repositoryName, final String branchName, final String directory ) {
      super( repositoryName, branchName, directory );
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport )
         throws ModelResolutionException {
      return loadContentsForNamespace( aspectModelUrn )
            .filter( file -> resolutionStrategySupport.containsDefinition( file, aspectModelUrn ) )
            .findFirst()
            .orElseThrow( () -> new ModelResolutionException( "No model file containing " + aspectModelUrn
                  + " could be found in GitHub repository: " + orgName + "/" + repositoryName
                  + " in branch " + branchName ) );
   }
}
