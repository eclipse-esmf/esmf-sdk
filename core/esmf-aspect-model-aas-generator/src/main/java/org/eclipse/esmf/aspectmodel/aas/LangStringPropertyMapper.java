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

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.jena.vocabulary.RDF;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultMultiLanguageProperty;

import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * Special mapper to create {@link MultiLanguageProperty}s from Aspect Model properties that carry
 * multiple localized strings.
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
      return new DefaultMultiLanguageProperty.Builder()
            .idShort( context.getPropertyIdShort( this ) )
            .description( LangStringMapper.TEXT.map( property.getDescriptions() ) )
            .displayName( LangStringMapper.NAME.map( property.getPreferredNames() ) )
            .semanticId( buildPropertyReferenceToGlobalReference( property ) )
            .value( extractLangStrings( context ) )
            .build();
   }

   private List<LangStringTextType> extractLangStrings( final Context context ) {
      return context.getRawPropertyValue().stream()
            .flatMap( node -> node.isArray() ? StreamSupport.stream( node.spliterator(), false ) : Stream.of( node ) )
            .filter( JsonNode::isObject )
            .map( ObjectNode.class::cast )
            .flatMap( node -> node.properties().stream()
                  .map( entry -> LangStringMapper.TEXT.createLangString( entry.getValue().asString(), entry.getKey() ) ) )
            .toList();
   }
}
