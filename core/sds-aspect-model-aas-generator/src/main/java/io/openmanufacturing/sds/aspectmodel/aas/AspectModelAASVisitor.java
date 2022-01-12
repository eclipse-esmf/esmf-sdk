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
import io.adminshell.aas.v3.model.Operation;
import io.adminshell.aas.v3.model.impl.*;
import io.openmanufacturing.sds.metamodel.*;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;
import io.vavr.collection.Stream;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AspectModelAASVisitor implements AspectVisitor<AssetAdministrationShellEnvironment, Base> {

   public static final String UNKNOWN_TYPE = "Unknown";
   private static final String UNKNOWN_URN = UNKNOWN_TYPE;


   private final Submodel submodel;
   private final AssetAdministrationShellEnvironment environment;

   public AspectModelAASVisitor() {
      this(new DefaultAssetAdministrationShellEnvironment.Builder().build());
   }

   public AspectModelAASVisitor (final AssetAdministrationShellEnvironment environment) {
      this.environment = environment;
      this.submodel = new DefaultSubmodel.Builder().build();
   }

   @Override
   public AssetAdministrationShellEnvironment visitBase( final Base base, final Base context ) {
      return environment;
   }

   @Override
   public AssetAdministrationShellEnvironment visitAspect( final Aspect aspect, final Base context ) {
      // bamm:Aspect/name -> aas:Submodel/idShort
      submodel.setIdShort(aspect.getName());

      // urn of bamm aspect model -> aas:Submodel/semanticId (optional)
      // TODO to be clarified if this mapping is ok
      submodel.setIdentification( extractIdentifier(aspect) );

      // bamm:Aspect/description -> aas:Submodel/description
      submodel.setDescriptions(map(aspect.getDescriptions()));

      // bamm:Aspect/properties -> aas:Submodel/submodelElements
      visitProperties( aspect.getProperties() );

      // bamm:Aspect/operations ->aas:Submodel/submodelElement
      visitOperations( aspect.getOperations() );

      //aspect.getOperations().stream().map( operation -> operation.accept( this, aspect ) ).forEach( this::merge );

      return environment;
   }

   private void visitOperations(final List<io.openmanufacturing.sds.metamodel.Operation> elements){
      final List<SubmodelElement> subModelElements =
              Stream.ofAll( elements )
                      .map( this::map )
                      .collect( Collectors.toList() );

      submodel.setSubmodelElements(subModelElements);
   }

   private void visitProperties(final List<io.openmanufacturing.sds.metamodel.Property>  elements) {
      final List<SubmodelElement> subModelElements =
            Stream.ofAll( elements )
                    .map(this::map)
                    .collect(Collectors.toList());

      final List<ConceptDescription> conceptDescriptions =
              Stream.ofAll(elements )
                      .map(this::createConceptDescription)
                      .collect(Collectors.toList());

      // Hint: As the AAS Meta Model Implementation exposes the internal data structure where the elements
      // of a collection are stored just setting it would overwrite previous entries. Hence this approach.
      submodel.getSubmodelElements().addAll(subModelElements);
      environment.getConceptDescriptions().addAll(conceptDescriptions);
   }

   private SubmodelElement map(Property property) {
      // TODO add decision logic on what kind of property is to be used
      return new DefaultProperty.Builder()
              .idShort(property.getName())
              .valueType(property.getCharacteristic().getDataType().isPresent() ?
                      mapType(property.getCharacteristic().getDataType().get()) : UNKNOWN_TYPE)
              .displayNames(map(property.getPreferredNames()))
              .value(property.getExampleValue().get().toString())
              .descriptions(map(property.getDescriptions()))
              .build();
   }

   private ConceptDescription createConceptDescription(final Property property){
      ConceptDescription conceptDescription = new DefaultConceptDescription.Builder()
              .displayNames(map(property.getPreferredNames())) // prefered name not found in AAS
              .embeddedDataSpecification(extractEmbeddedDataSpecification(property))
              .identification(extractIdentifier(property))
              .build();
      return conceptDescription;
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification(Property property) {
      EmbeddedDataSpecification embeddedDataSpecification = new DefaultEmbeddedDataSpecification.Builder()
              .dataSpecificationContent(extractDataSpecificationContent(property))
              .build();
      return embeddedDataSpecification;
   }

   private DataSpecificationContent extractDataSpecificationContent(Property property) {
      DataSpecificationContent dataSpecificationContent = new DefaultDataSpecificationIEC61360.Builder()
              // TODO add stuff which needs to be converted
              .build();
      return dataSpecificationContent;
   }

   private Identifier extractIdentifier(IsDescribed element){
      Identifier identifier = new DefaultIdentifier.Builder()
              .identifier( element.getAspectModelUrn().isPresent() ?
                      element.getAspectModelUrn().get().toString() : UNKNOWN_URN )
              .idType(IdentifierType.IRI).build();
      return identifier;
   }


   private String mapType(Type type){
      return type.getUrn();
   }

   private Operation map(io.openmanufacturing.sds.metamodel.Operation operation) {

      // TODO add decision logic on what kind of property is to be used
      return new DefaultOperation.Builder()
              .displayNames(map(operation.getPreferredNames()))
              .descriptions(map(operation.getDescriptions()))
              .idShort(operation.getName())
              .inputVariables(operation.getInput().stream().map(this::mapOperation).collect(Collectors.toList()))
              .outputVariables(operation.getOutput().stream().map(this::mapOperation).collect(Collectors.toList()))
              .build();
   }

   private OperationVariable mapOperation(Property property) {
      return new DefaultOperationVariable.Builder()
      .value(map(property)).build();
   }

   private List<LangString> map(Map<Locale, String> localizedStrings) {
     return localizedStrings.entrySet().stream().map((entry) ->
             map(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());
   }

   private LangString map(Locale locale, String value) {
      LangString langString = new LangString();
      langString.setLanguage(locale.getCountry());
      langString.setValue(value);
      return langString;
   }

   private AssetAdministrationShellEnvironment merge(AssetAdministrationShellEnvironment env1, AssetAdministrationShellEnvironment env2) {
      env1.getAssets().addAll(env2.getAssets());
      env1.getConceptDescriptions().addAll(env2.getConceptDescriptions());
      env1.getSubmodels().addAll(env2.getSubmodels());
      env1.getAssetAdministrationShells().addAll(env2.getAssetAdministrationShells());

      return env1;
   }

   private AssetAdministrationShellEnvironment merge(AssetAdministrationShellEnvironment env2) {
      return merge(environment, env2);
   }
}
