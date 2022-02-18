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
import io.adminshell.aas.v3.model.Submodel;
import io.adminshell.aas.v3.model.Operation;
import io.adminshell.aas.v3.model.impl.*;
import io.openmanufacturing.sds.metamodel.*;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Set;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;
import io.vavr.collection.Stream;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


/*

Todos for AASXGenerator

TODO lear out result passing in AASVisitor. Using context object to carry AASEnvironment through chain seems promising; problem not side effect free implementation

TODO Implement mappings of specific characteristics classes from meta model via overwritten visitor methods

TODO Implement tests; example could be openAPI or RDF Generatror tests

TODO Use AAS XSD Schema to validate well formedness -> https://www.rgagnon.com/javadetails/java-0669.html
 */
public class AspectModelAASVisitor implements AspectVisitor<AssetAdministrationShellEnvironment, Context> {

   private interface SubModelElementCollectionBuilder{
      SubmodelElementCollection build (Property property);
   }

   public static final String UNKNOWN_TYPE = "Unknown";
   private static final String UNKNOWN_URN = UNKNOWN_TYPE;
   private static final String UNKNOWN_EXAMPLE = UNKNOWN_TYPE;


   @Override
   public AssetAdministrationShellEnvironment visitBase( final Base base, Context context ) {
      return new DefaultAssetAdministrationShellEnvironment.Builder().build();
   }

   @Override
   public AssetAdministrationShellEnvironment visitAspect( final Aspect aspect, Context context ) {

      if(context == null){
         Submodel submodel = new DefaultSubmodel.Builder().build();
         AssetAdministrationShellEnvironment environment =
                 new DefaultAssetAdministrationShellEnvironment.Builder()
                 .submodels(submodel).build();
         context = new Context(environment, submodel);
      }

      Submodel submodel = context.getSubmodel();

       submodel.setIdShort(aspect.getName());
       submodel.setIdentification(extractIdentifier(aspect));
       submodel.setDescriptions(map(aspect.getDescriptions()));
       submodel.setKind(ModelingKind.TEMPLATE);

      AssetAdministrationShell administrationShell =
              new DefaultAssetAdministrationShell.Builder()
                      .submodel(buildReferenceToSubmodel(submodel))
                      .build();


      // Hint: As the AAS Meta Model Implementation exposes the internal data structure where the elements
      // of a collection are stored, just setting it would overwrite previous entries. Hence this approach.
      ((Submodel)context.getSubmodel()).getSubmodelElements().addAll(visitProperties(aspect.getProperties(), context));
      ((Submodel)context.getSubmodel()).getSubmodelElements().addAll(visitOperations(aspect.getOperations(), context));

      return context.getEnvironment();
   }

   private List<SubmodelElement> visitOperations(final List<io.openmanufacturing.sds.metamodel.Operation> elements, Context context){
      final List<SubmodelElement> subModelElements =
              Stream.ofAll( elements )
                      .map( i -> map(i, context) )
                      .collect( Collectors.toList() );

      return subModelElements;
   }

   private List<SubmodelElement> visitProperties(final List<Property> elements, Context context) {
      final List<SubmodelElement> subModelElements =
            Stream.ofAll( elements )
                    .map(i -> map(i, context) )
                    .collect(Collectors.toList());

      return subModelElements;
   }

   private SubmodelElement map(Property property, Context context) {

      // Characteristic defines how the property is mapped to SubmodelElement
      Characteristic characteristic = property.getCharacteristic();

      context.setProperty(property);
      characteristic.accept(this, context);
      SubmodelElement element = context.getPropertyResult();

      return element;
   }

   private SubmodelElement decideOnMapping(Property property, Context context) {
      if(property.getCharacteristic().getDataType().isEmpty()) {
         return new DefaultProperty.Builder().build();
      }

      Type type = property.getCharacteristic().getDataType().get();
      if (type instanceof Entity) {
         return mapToAasSubModelElementCollection((Entity)type, context);
      } else {
         return mapToAasProperty(property, context);
      }
   }

   private SubmodelElementCollection mapToAasSubModelElementCollection(Entity entity, Context context) {
      List<SubmodelElement> submodelElements = visitProperties(entity.getAllProperties(), context);
      SubmodelElementCollection aasSubModelElementCollection = new DefaultSubmodelElementCollection.Builder()
              .idShort(entity.getName())
              .displayNames(map(entity.getPreferredNames()))
              .descriptions(map(entity.getDescriptions()))
              .values(submodelElements)
              .build();

      return aasSubModelElementCollection;
   }

   private io.adminshell.aas.v3.model.Property mapToAasProperty(Property property, Context context) {
      io.adminshell.aas.v3.model.Property aasProperty = new DefaultProperty.Builder()
              .idShort(property.getName())
              .valueType(property.getCharacteristic().getDataType().isPresent() ?
                      mapType(property.getCharacteristic().getDataType().get()) : UNKNOWN_TYPE) // TODO decided whether give unknown or not call valueType at all
              .displayNames(map(property.getPreferredNames()))
              .value(property.getExampleValue().isPresent() ? property.getExampleValue().get().toString() : UNKNOWN_EXAMPLE)
              .descriptions(map(property.getDescriptions()))
              .semanticId(buildReferenceToConceptDescription(property.getCharacteristic())) // this is the link to the conceptDescription containing the details for the Characteristic
              .build();

      return aasProperty;
   }

   private Reference buildReferenceToConceptDescription(Characteristic characteristic) {
      Key key = new DefaultKey.Builder()
              .idType(KeyType.CUSTOM)
              .type(KeyElements.CONCEPT_DESCRIPTION)
              .value(extractIdentifier(characteristic).getIdentifier())
              .build();
      return new DefaultReference.Builder().key(key).build();
   }

   private Reference buildReferenceToSubmodel(Submodel submodel) {
      Key key = new DefaultKey.Builder()
              .idType(KeyType.CUSTOM)
              .type(KeyElements.SUBMODEL)
              .value(submodel.getIdentification().getIdentifier())
              .build();
      return new DefaultReference.Builder().key(key).build();
   }



   @Override
   public AssetAdministrationShellEnvironment visitCharacteristic(Characteristic characteristic, Context context) {
      io.openmanufacturing.sds.metamodel.Property property = context.getProperty();


     SubmodelElement element = decideOnMapping(property, context);
     context.setPropertyResult(element);

      createConceptDescription(characteristic, context);
      return context.environment;
   }

   private void createConceptDescription(Characteristic characteristic, Context context) {
      // check if the concept description is already created. If not create a new one.
      if(context.getEnvironment().getConceptDescriptions().contains(characteristic.getName())) {
         return;
      }else {
         ConceptDescription conceptDescription = new DefaultConceptDescription.Builder()
                 .idShort(characteristic.getName())
                 .displayNames(map(characteristic.getPreferredNames())) // preferred name not found in AAS
                 .embeddedDataSpecification(extractEmbeddedDataSpecification(characteristic))
                 .identification(extractIdentifier(characteristic))
                 .build();
         context.getEnvironment().getConceptDescriptions().add(conceptDescription);
      }
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitCode(Code code, Context context) {
      return visitCharacteristic(code, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitTrait(Trait trait, Context context) {
      return AspectVisitor.super.visitTrait(trait, context);
   }

   @Override
   public AssetAdministrationShellEnvironment visitCollection(Collection collection, Context context) {

      SubModelElementCollectionBuilder builder = (property) -> {
         SubmodelElementCollection aasSubModelElementCollection = new DefaultSubmodelElementCollection.Builder()
                 .idShort(property.getName())
                 .displayNames(map(property.getPreferredNames()))
                 .descriptions(map(property.getDescriptions()))
                 .values(Arrays.asList(decideOnMapping(property, context)))
                 .build();
         return aasSubModelElementCollection;
      };
      createSubModelElement(collection, builder, context);

      return context.getEnvironment();
   }

   private void createSubModelElement(Collection collection, SubModelElementCollectionBuilder op, Context context) {
      Property property = context.getProperty();
      SubmodelElementCollection aasSubModelElementCollection = op.build(property);

      context.setPropertyResult(aasSubModelElementCollection);
      createConceptDescription(collection, context);
   }

   @Override
   public AssetAdministrationShellEnvironment visitList(io.openmanufacturing.sds.metamodel.List list, Context context) {
      SubModelElementCollectionBuilder builder = (property) -> {
         SubmodelElementCollection aasSubModelElementCollection = new DefaultSubmodelElementCollection.Builder()
                 .idShort(property.getName())
                 .displayNames(map(property.getPreferredNames()))
                 .descriptions(map(property.getDescriptions()))
                 .values(Arrays.asList(decideOnMapping(property, context)))
                 .ordered(true)
                 .build();
         return aasSubModelElementCollection;
      };
      createSubModelElement(list, builder, context);

      return context.getEnvironment();
   }

   @Override
   public AssetAdministrationShellEnvironment visitSet(Set set, Context context) {
      SubModelElementCollectionBuilder builder = (property) -> {
         SubmodelElementCollection aasSubModelElementCollection = new DefaultSubmodelElementCollection.Builder()
                 .idShort(property.getName())
                 .displayNames(map(property.getPreferredNames()))
                 .descriptions(map(property.getDescriptions()))
                 .values(Arrays.asList(decideOnMapping(property, context)))
                 .ordered(false)
                 .allowDuplicates(false)
                 .build();
         return aasSubModelElementCollection;
      };
      createSubModelElement(set, builder, context);

      return context.getEnvironment();
   }

   @Override
   public AssetAdministrationShellEnvironment visitSortedSet(SortedSet sortedSet, Context context) {
      SubModelElementCollectionBuilder builder = (property) -> {
         SubmodelElementCollection aasSubModelElementCollection = new DefaultSubmodelElementCollection.Builder()
                 .idShort(property.getName())
                 .displayNames(map(property.getPreferredNames()))
                 .descriptions(map(property.getDescriptions()))
                 .values(Arrays.asList(decideOnMapping(property, context)))
                 .ordered(true)
                 .allowDuplicates(false)
                 .build();
         return aasSubModelElementCollection;
      };
      createSubModelElement(sortedSet, builder, context);

      return context.getEnvironment();
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitDuration(Duration duration, Context context) {
      return visitCharacteristic(duration, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitEither(Either either, Context context) {
      return visitCharacteristic(either, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitEnumeration(Enumeration enumeration, Context context) {
      return visitCharacteristic(enumeration, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitMeasurement(Measurement measurement, Context context) {
      return visitCharacteristic(measurement, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitQuantifiable(Quantifiable quantifiable, Context context) {
      return visitCharacteristic(quantifiable, context);
   }


   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitSingleEntity(SingleEntity singleEntity, Context context) {
      return visitCharacteristic(singleEntity, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitState(State state, Context context) {
      return visitCharacteristic(state, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitStructuredValue(StructuredValue structuredValue, Context context) {
      return visitCharacteristic(structuredValue, context);
   }

   private EmbeddedDataSpecification extractEmbeddedDataSpecification(Characteristic characteristic) {
      EmbeddedDataSpecification embeddedDataSpecification = new DefaultEmbeddedDataSpecification.Builder()
              .dataSpecificationContent(extractDataSpecificationContent(characteristic))
              .build();
      return embeddedDataSpecification;
   }

   private DataSpecificationContent extractDataSpecificationContent(Characteristic characteristic) {
      DataSpecificationContent dataSpecificationContent = new DefaultDataSpecificationIEC61360.Builder()
              // TODO add stuff which needs to be converted
              //TODO clarify how to map types .dataType(characteristic.getDataType().isPresent() ? mapType(characteristic.getDataType().get()) : UNKNOWN_TYPE)
              .definitions(map(characteristic.getDescriptions()))
              .preferredNames(map(characteristic.getPreferredNames()))
              .shortName(new LangString(characteristic.getName(), "EN")) // TODO find generic solution
              .build();
      return dataSpecificationContent;
   }

   private Identifier extractIdentifier(IsDescribed element){
      Identifier identifier = new DefaultIdentifier.Builder()
              .identifier( element.getAspectModelUrn().isPresent() ?
                      element.getAspectModelUrn().get().toString() : UNKNOWN_URN )
              .idType(IdentifierType.CUSTOM).build();
      return identifier;
   }

   private String mapType(Type type){
      return type.getUrn();
   }

   private Operation map(io.openmanufacturing.sds.metamodel.Operation operation, Context context) {

      // TODO add decision logic on what kind of property is to be used
      return new DefaultOperation.Builder()
              .displayNames(map(operation.getPreferredNames()))
              .descriptions(map(operation.getDescriptions()))
              .idShort(operation.getName())
              .inputVariables(operation.getInput().stream().map(i -> mapOperationVariable(i, context)).collect(Collectors.toList()))
              .outputVariables(operation.getOutput().stream().map(i -> mapOperationVariable(i, context)).collect(Collectors.toList()))
              .build();
   }

   private OperationVariable mapOperationVariable(Property property, Context context) {
      return new DefaultOperationVariable.Builder()
      .value(map(property, context)).build();
   }

   private List<LangString> map(Map<Locale, String> localizedStrings) {
     return localizedStrings.entrySet().stream().map((entry) ->
             map(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());
   }

   private LangString map(Locale locale, String value) {
      LangString langString = new LangString();
      langString.setLanguage(locale.getLanguage());
      langString.setValue(value);
      return langString;
   }

}
