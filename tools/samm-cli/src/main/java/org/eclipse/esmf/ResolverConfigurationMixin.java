/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

import java.util.List;

import picocli.CommandLine;

/**
 * Configuration of resolver strategies
 */
public class ResolverConfigurationMixin {
   @CommandLine.Option(
         names = { "--custom-resolver" },
         description = "External command to execute to produce the custom model resolution.",
         arity = "0..*"
   )
   public List<String> commandLine = List.of();

   @CommandLine.Option(
         names = { "--models-root", "-mr" },
         description = "Set the models root directory",
         arity = "0..*"
   )
   public List<String> modelsRoots = List.of();

   @CommandLine.ArgGroup( exclusive = false, multiplicity = "0..*" )
   public List<GitHubResolverOptions> gitHubResolverOptions;

   public static class GitHubResolverOptions {
      @CommandLine.Option(
            names = { "--github", "-gh" },
            description = "Enable loading Aspect Models from GitHub and set the GitHub repository name. Example: eclipse-esmf/esmf-sdk."
      )
      public String gitHubName;

      @CommandLine.Option(
            names = { "--github-directory", "-ghd" },
            description = "Set the GitHub directory (default: /)"
      )
      public String gitHubDirectory = "/";

      @CommandLine.Option(
            names = { "--github-branch", "-ghb" },
            description = "Set the GitHub branch (default: main)"
      )
      public String gitHubBranch = "main";

      @CommandLine.Option(
            names = { "--github-tag", "-ght" },
            description = "Set the GitHub tag"
      )
      public String gitHubTag = null;

      @CommandLine.Option(
            names = { "--token", "-gt" },
            description = "Set the GitHub token for the specific repository"
      )
      public String token = null;
   }

   // This option is intentionally outside the GitHubResolverOptions argument group so it can be used when resolving Aspect Models
   // from a URL:  samm aspect https://github.com/... validate --github-token ...
   @CommandLine.Option(
         names = { "--github-token", "-ghtoken" },
         description = "Set the GitHub token"
   )
   public String gitHubToken = null;
}
