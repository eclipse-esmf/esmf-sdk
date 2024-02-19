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

import picocli.CommandLine;

/**
 * Configuration of the external command that can be executed to custom resolve a model. The command will be executed
 * as given, with the model URN to resolve added as the last parameter automatically by the program logic.
 */
public class ExternalResolverMixin {
   @CommandLine.Option( names = {
         "--custom-resolver" }, description = "External command to execute to produce the custom model resolution." )
   protected String commandLine = "";
}
