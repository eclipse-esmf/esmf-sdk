/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewAttributeTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewTarget;

import org.junit.jupiter.api.Test;

class GraphicalViewSvgSidecarValidatorTest {
   private final GraphicalViewSvgSidecarValidator validator = new GraphicalViewSvgSidecarValidator();

   @Test
   void acceptsConsistentHeaderAndAttributeMarkers() {
      final String headerId = "gv-header-1234567890abcdef";
      final String attributeId = "gv-attribute-1234567890abcdef";
      assertThat( validator.isValid( "<svg id=\"%s\" id=\"%s\"/>".formatted( headerId, attributeId ), List.of(
            new GraphicalViewTarget( headerId, "elementHeader", "urn:samm:example.validation:1.0.0#Target" ),
            new GraphicalViewAttributeTarget( attributeId, "attributeRow", "urn:samm:example.validation:1.0.0#Target",
                  "urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#description", "singleOccurrence", "en" ) ) ) )
                        .isTrue();
   }

   @Test
   void rejectsDuplicateMissingMalformedAndKindInconsistentMarkers() {
      final String id = "gv-header-1234567890abcdef";
      final GraphicalViewTarget valid = new GraphicalViewTarget( id, "elementHeader", "urn:samm:example.validation:1.0.0#Target" );
      assertThat( validator.isValid( "<svg id=\"" + id + "\" id=\"" + id + "\"/>", List.of( valid ) ) ).isFalse();
      assertThat( validator.isValid( "<svg/>", List.of( valid ) ) ).isFalse();
      assertThat( validator.isValid( "<svg id=\"gv-header-BAD\"/>", List.of() ) ).isFalse();
      assertThat( validator.isValid( "<svg id=\"" + id + "\"/>", List.of(
            new GraphicalViewTarget( id, "attributeRow", "urn:samm:example.validation:1.0.0#Target" ) ) ) ).isFalse();
   }
}
