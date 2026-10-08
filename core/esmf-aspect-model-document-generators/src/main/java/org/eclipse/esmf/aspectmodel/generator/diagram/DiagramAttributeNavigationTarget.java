/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.aspectmodel.generator.diagram;

/** Semantic sidecar entry for one physical graphical-view attribute row. */
public record DiagramAttributeNavigationTarget(
      String id,
      String ownerUrn,
      String predicateUrn,
      String selection,
      String language
) {}
