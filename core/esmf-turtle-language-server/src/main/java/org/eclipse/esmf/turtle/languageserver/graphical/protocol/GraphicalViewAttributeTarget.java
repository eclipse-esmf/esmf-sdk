/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
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
