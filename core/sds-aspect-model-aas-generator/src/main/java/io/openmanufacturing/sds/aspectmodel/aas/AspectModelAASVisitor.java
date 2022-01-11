/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.aspectmodel.aas;

import io.adminshell.aas.v3.model.*;
import io.adminshell.aas.v3.model.impl.*;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.HasProperties;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;
import io.vavr.collection.Stream;
import org.apache.jena.sparql.algebra.Op;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AspectModelAASVisitor implements AspectVisitor<Submodel, Submodel> {

   private static final DefaultSubmodel.Builder submodelBuilder = new DefaultSubmodel.Builder();
   private final Submodel rootNode = submodelBuilder.build();

   @Override
   public Submodel visitBase( final Base base, final Submodel context ) {
      return submodelBuilder.build();
   }

   @Override
   public Submodel visitAspect( final Aspect aspect, final Submodel context ) {
      // bamm:Aspect/name -> aas:Submodel/idShort
      rootNode.setIdShort(aspect.getName());

      // urn of bamm aspect model -> aas:Submodel/semanticId (optional)
      // TODO to be clarified if this mapping is ok
      Identifier identifier = new DefaultIdentifier.Builder().
              identifier( aspect.getAspectModelUrn().get().toString() ).
              idType(IdentifierType.CUSTOM).build();

      rootNode.setIdentification(identifier);

      // bamm:Aspect/description -> aas:Submodel/description
      rootNode.setDescriptions(map(aspect.getDescriptions()));

      // bamm:Aspect/properties -> aas:Submodel/submodelElements
      visitHasProperties( aspect, rootNode );

      // bamm:Aspect/operations ->aas:Submodel/submodelElement
      visitOperations(aspect.getOperations());

      return rootNode;
   }

   private void visitOperations(List<io.openmanufacturing.sds.metamodel.Operation> elements){
      final List<SubmodelElement> subModelElements =
              Stream.ofAll( elements )
                      .map(this::map)
                      .collect(Collectors.toList());

      rootNode.setSubmodelElements(subModelElements);
   }

   @Override
   public Submodel visitHasProperties( final HasProperties element, final Submodel context ) {
      final List<SubmodelElement> subModelElements =
            Stream.ofAll( element.getProperties() )
                    .map(this::map)
                    .collect(Collectors.toList());

      context.setSubmodelElements(subModelElements);
      return context;
   }

   private SubmodelElement map(Property property) {

      // TODO add decision logic on what kind of property is to be used
      return new DefaultProperty.Builder().
              displayNames(map(property.getPreferredNames())).
              value(property.getExampleValue().get().toString()).
              descriptions(map(property.getDescriptions())).
              build();
   }

   private Operation map(io.openmanufacturing.sds.metamodel.Operation operation) {

      // TODO add decision logic on what kind of property is to be used
      return new DefaultOperation.Builder().
              displayNames(map(operation.getPreferredNames())).
              descriptions(map(operation.getDescriptions())).
              idShort(operation.getName())
              .inputVariables(operation.getInput().stream().map(this::mapOperation).collect(Collectors.toList()))
              .outputVariables(operation.getOutput().stream().map(this::mapOperation).collect(Collectors.toList()))
              .build();
   }

   OperationVariable mapOperation(Property property) {
      return new DefaultOperationVariable.Builder().
              value(map(property)).build();
   }

   private List<LangString> map(Map<Locale, String> localizedStrings) {
     return localizedStrings.entrySet().stream().map((entry) ->
             map(entry.getKey(), entry.getValue())).
             collect(Collectors.toList());
   }

   private LangString map(Locale locale, String value) {
      LangString langString = new LangString();
      langString.setLanguage(locale.getCountry());
      langString.setValue(value);
      return langString;
   }
}
