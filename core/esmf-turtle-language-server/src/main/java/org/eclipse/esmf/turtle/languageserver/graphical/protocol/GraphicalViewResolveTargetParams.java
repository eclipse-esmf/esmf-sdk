/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.protocol;

/** Parameters for {@code turtle/graphicalView/resolveTarget}. */
public record GraphicalViewResolveTargetParams(
      String sourceUri, String elementUrn
) {}
