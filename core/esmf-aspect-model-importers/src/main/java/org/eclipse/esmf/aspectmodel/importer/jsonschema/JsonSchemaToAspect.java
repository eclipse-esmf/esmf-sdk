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

package org.eclipse.esmf.aspectmodel.importer.jsonschema;

import static org.eclipse.esmf.metamodel.builder.SammBuilder.aspect;

import org.eclipse.esmf.aspectmodel.generator.AspectArtifact;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.builder.SammBuilder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Generator that translates a JSON Schema into an {@link Aspect}
 */
@SuppressWarnings( { "NewClassNamingConvention", "checkstyle:ClassTypeParameterName", "checkstyle:MethodTypeParameterName" } )
public class JsonSchemaToAspect extends JsonSchemaImporter<Aspect, AspectArtifact> {
   public JsonSchemaToAspect( final JsonNode focus, final JsonSchemaImporterConfig config ) {
      super( focus, config );
   }

   @SuppressWarnings( "unchecked" )
   @Override
   //@formatter:off
   protected <SELF extends SammBuilder.StructureElementBuilder<SELF, Aspect>>
         SammBuilder.StructureElementBuilder<SELF, Aspect> elementBuilder() {
      return (SammBuilder.StructureElementBuilder<SELF, Aspect>) aspect( config.aspectModelUrn() );
   }
   //@formatter:on

   @Override
   protected AspectArtifact buildArtifact() {
      return new AspectArtifact( config.aspectModelUrn(), buildElement() );
   }
}
