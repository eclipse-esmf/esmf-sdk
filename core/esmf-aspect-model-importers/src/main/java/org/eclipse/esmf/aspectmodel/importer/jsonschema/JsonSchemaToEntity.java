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

import static org.eclipse.esmf.metamodel.builder.SammBuilder.entity;

import org.eclipse.esmf.aspectmodel.generator.EntityArtifact;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.builder.SammBuilder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Generator that translates a JSON Schema into an {@link Entity}
 */
@SuppressWarnings( { "NewClassNamingConvention", "checkstyle:ClassTypeParameterName", "checkstyle:MethodTypeParameterName" } )
public class JsonSchemaToEntity extends JsonSchemaImporter<Entity, EntityArtifact> {
   public JsonSchemaToEntity( final JsonNode focus, final AspectGenerationConfig config ) {
      super( focus, config );
   }

   @SuppressWarnings( "unchecked" )
   @Override
   protected <SELF extends SammBuilder.StructureElementBuilder<SELF, Entity>>
   SammBuilder.StructureElementBuilder<SELF, Entity> elementBuilder() {
      return (SammBuilder.StructureElementBuilder<SELF, Entity>) entity( config.aspectModelUrn() );
   }

   @Override
   protected EntityArtifact buildArtifact() {
      return new EntityArtifact( config.aspectModelUrn(), buildElement() );
   }
}
