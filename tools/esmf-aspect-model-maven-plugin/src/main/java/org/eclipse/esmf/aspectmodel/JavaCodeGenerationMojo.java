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
package org.eclipse.esmf.aspectmodel;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base class for all Mojos that generate Java code.
 */
public abstract class JavaCodeGenerationMojo extends CodeGenerationMojo {
   @Parameter( defaultValue = "false" )
   protected boolean enableSetters;

   @Parameter( defaultValue = "standard" )
   protected String setterStyle;
}
