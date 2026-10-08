/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.protocol;

/** Parameters for {@code turtle/graphicalView/resolveAttributeTarget}. */
public record GraphicalViewResolveAttributeTargetParams(
      String sourceUri,
      String ownerUrn,
      String predicateUrn,
      String selection,
      String language
) {}
