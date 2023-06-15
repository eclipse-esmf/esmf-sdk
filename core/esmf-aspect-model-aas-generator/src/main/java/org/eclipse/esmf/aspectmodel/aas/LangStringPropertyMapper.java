/*
 * Copyright (c) 2021-2023 Robert Bosch Manufacturing Solutions GmbH
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
package org.eclipse.esmf.aspectmodel.aas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.vocabulary.RDF;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultMultiLanguageProperty;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Special mapper to create {@link MultiLanguageProperty}s from Aspect Model properties that carry multiple localized strings.
 */
public class LangStringPropertyMapper implements PropertyMapper<MultiLanguageProperty> {

   @Override
   public boolean canHandle( final Property property ) {
      return property.getDataType()
            .map( Type::getUrn )
            .filter( RDF.langString.getURI()::equals )
            .isPresent();
   }

   @Override
   public MultiLanguageProperty mapToAasProperty( final Type type, final Property property, final Context context ) {
      return new DefaultMultiLanguageProperty.Builder().idShort( context.getPropertyShortId() )
            .description( LangStringMapper.TEXT.map( property.getDescriptions() ) )
            .displayName( LangStringMapper.NAME.map( property.getPreferredNames() ) )
            .semanticID( buildReferenceToConceptDescription( property ) )
            .value( extractLangStrings( property, context ) )
            .build();
   }

   private List<LangStringTextType> extractLangStrings( final Property property, final Context context ) {
      return context.getRawPropertyValue()
            .filter( JsonNode::isObject )
            .map( node -> {
               final Map<String, String> entries = new HashMap<>();
               node.fields().forEachRemaining( field -> entries.put( field.getKey(), field.getValue().asText() ) );
               return entries;
            } )
            .map( rawEntries -> rawEntries.entrySet()
                  .stream()
                  .map( entry -> LangStringMapper.TEXT.createLangString( entry.getValue(), entry.getKey() ) )
                  .toList() )
            .orElseGet( () -> List.of() );
   }
}
