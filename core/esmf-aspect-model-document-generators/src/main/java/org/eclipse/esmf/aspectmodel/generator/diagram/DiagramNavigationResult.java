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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.util.List;
import java.util.Map;

/**
 * SVG diagram output with opt-in graphical-view header navigation targets.
 *
 * @param svg the SVG document
 * @param navigationTargets opaque header marker ID to full semantic URN
 */
public record DiagramNavigationResult(
      String svg,
      Map<String, String> navigationTargets,
      List<DiagramAttributeNavigationTarget> attributeNavigationTargets
) {
   public DiagramNavigationResult( final String svg, final Map<String, String> navigationTargets ) {
      this( svg, navigationTargets, List.of() );
   }

   public DiagramNavigationResult {
      navigationTargets = Map.copyOf( navigationTargets );
      attributeNavigationTargets = List.copyOf( attributeNavigationTargets );
   }
}
