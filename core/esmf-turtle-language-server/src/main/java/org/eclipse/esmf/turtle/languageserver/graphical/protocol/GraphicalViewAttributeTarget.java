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
package org.eclipse.esmf.turtle.languageserver.graphical.protocol;

/** A response-local attribute-row marker and its semantic source selector. */
public record GraphicalViewAttributeTarget(
      String id,
      String kind,
      String ownerUrn,
      String predicateUrn,
      String selection,
      String language
) implements GraphicalViewRenderTarget {
   public static final String ATTRIBUTE_ROW = "attributeRow";
}
