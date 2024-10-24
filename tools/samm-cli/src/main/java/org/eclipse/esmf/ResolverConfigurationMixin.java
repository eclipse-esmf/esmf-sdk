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
         description = "External command to execute to produce the custom model resolution." )
   public String commandLine = "";

   @CommandLine.Option(
         names = { "--models-root", "-mr" },
         description = "Set the models root directory",
         arity = "0..*" )
   public List<String> modelsRoots = List.of();

   @CommandLine.ArgGroup( exclusive = false )
   public GitHubResolutionOptions gitHubResolutionOptions;

   public static class GitHubResolutionOptions {
      @CommandLine.Option(
            names = { "--github", "-gh" },
            required = true,
            description = "Enable loading Aspect Models from GitHub and set the GitHub repository name. Example: eclipse-esmf/esmf-sdk." )
      public String gitHubName;

      @CommandLine.Option(
            names = { "--github-directory", "-ghd" },
            description = "Set the GitHub directory (default: ${DEFAULT-VALUE}" )
      public String gitHubDirectory = "/";

      @CommandLine.Option(
            names = { "--github-branch", "-ghb" },
            description = "Set the GitHub branch (default: ${DEFAULT-VALUE}" )
      public String gitHubBranch = "main";

      @CommandLine.Option(
            names = { "--github-tag", "-ght" },
            description = "Set the GitHub tag (default: ${DEFAULT-VALUE}" )
      public String gitHubTag = null;
   }
}
