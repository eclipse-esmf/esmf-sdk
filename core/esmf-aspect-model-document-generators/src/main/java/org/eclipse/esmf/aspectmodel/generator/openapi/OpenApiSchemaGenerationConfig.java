/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.openapi;

import static org.eclipse.esmf.aspectmodel.generator.openapi.AspectModelOpenApiGenerator.ObjectNodeExtension.getter;

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.JsonGenerationConfig;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * A {@link GenerationConfig} for OpenAPI schema. Note that for providing additional properties, you can use either the JSON representation
 * (jsonProperties) or the YAML representation (yamlProperties).
 *
 * @param locale the locale for choosing the preferred language for description and preferred name.
 * @param generateCommentForSeeAttributes generate $comment OpenAPI element for samm:see attributes in the model
 * @param useSemanticVersion if set to true, the complete semantic version of the Aspect Model will be used as the version of the API.
 * @param baseUrl the base URL for the Aspect API
 * @param resourcePath the resource path for the Aspect API endpoints. If no resource path is given, the resource path will be derived
 * from the Aspect name
 * @param properties the needed properties for the resource path, defined in JSON.
 * @param pagingOption if defined, the chosen paging type will be in the JSON.
 * @param includeQueryApi if set to true, a path section for the Query API Endpoint of the Aspect API will be included in the
 * specification
 */
@RecordBuilder
public record OpenApiSchemaGenerationConfig(
      Locale locale,
      boolean generateCommentForSeeAttributes,
      boolean useSemanticVersion,
      String baseUrl,
      String readApiPath,
      String queryApiPath,
      String resourcePath,
      ObjectNode properties,
      PagingOption pagingOption,
      boolean includeQueryApi,
      boolean includeCrud,
      boolean includePost,
      boolean includePut,
      boolean includePatch,
      ObjectNode template
) implements JsonGenerationConfig {
   private static final String QUERIES_TEMPLATE_PATH = "__DEFAULT_QUERIES_TEMPLATE__";

   public OpenApiSchemaGenerationConfig {
      if ( locale == null ) {
         locale = Locale.ENGLISH;
      }
   }

   public ObjectNode queriesTemplate() {
      if ( template == null ) {
         return null;
      }

      final ObjectNode objectNode = getter( "paths" ).apply( template );
      return getter( QUERIES_TEMPLATE_PATH ).apply( objectNode );
   }

   public ObjectNode documentTemplate() {
      if ( template == null ) {
         return null;
      }
      final ObjectNode objectNode = template.deepCopy();
      getter( "paths" ).apply( objectNode ).remove( QUERIES_TEMPLATE_PATH );
      return objectNode;
   }
}
