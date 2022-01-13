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

public class AspectModelAASVisitor implements AspectVisitor<AssetAdministrationShellEnvironment, Context> {

   public static final String UNKNOWN_TYPE = "Unknown";
   private static final String UNKNOWN_URN = UNKNOWN_TYPE;


   @Override
   public AssetAdministrationShellEnvironment visitBase( final Base base, Context context ) {
      return new DefaultAssetAdministrationShellEnvironment.Builder().build();
   }

   @Override
   public AssetAdministrationShellEnvironment visitAspect( final Aspect aspect, Context context ) {

      if(context == null){
         Submodel submodel = new DefaultSubmodel.Builder().build();
         AssetAdministrationShellEnvironment environment = new DefaultAssetAdministrationShellEnvironment.Builder()
                 .submodels(submodel).build();
         context = new Context(environment, submodel);
      }

      Submodel submodel = (Submodel) context.getOfInterest();

       submodel.setIdShort(aspect.getName());
       submodel.setIdentification(extractIdentifier(aspect));
       submodel.setDescriptions(map(aspect.getDescriptions()));
       submodel.setKind(ModelingKind.TEMPLATE);

      visitProperties(aspect.getProperties(), context);
      visitOperations(aspect.getOperations(), context);

      return context.getEnvironment();
   }

   public AssetAdministrationShellEnvironment visitOperation(final io.openmanufacturing.sds.metamodel.Operation operation, Context context ) {
      return visitBase( operation, context );
   }


   private void visitOperations(final List<io.openmanufacturing.sds.metamodel.Operation> elements, Context context){
      final List<SubmodelElement> subModelElements =
              Stream.ofAll( elements )
                      .map( this::map )
                      .collect( Collectors.toList() );

      ((Submodel) context.getOfInterest()).setSubmodelElements(subModelElements);
   }

   private void visitProperties(final List<Property> elements, Context context) {
      final List<SubmodelElement> subModelElements =
            Stream.ofAll( elements )
                    .map(i -> {createConceptDescription(i, context); return map(i); })
                    .collect(Collectors.toList());

      // Hint: As the AAS Meta Model Implementation exposes the internal data structure where the elements
      // of a collection are stored, just setting it would overwrite previous entries. Hence this approach.
      ((Submodel)context.getOfInterest()).getSubmodelElements().addAll(subModelElements);
   }

   private SubmodelElement map(Property property) {
      io.adminshell.aas.v3.model.Property aasProperty = new DefaultProperty.Builder()
              .idShort(property.getName())
              .valueType(property.getCharacteristic().getDataType().isPresent() ?
                      mapType(property.getCharacteristic().getDataType().get()) : UNKNOWN_TYPE)
              .displayNames(map(property.getPreferredNames()))
              .value(property.getExampleValue().get().toString())
              .descriptions(map(property.getDescriptions()))
              .semanticId(buildReference(property.getCharacteristic())) // this is the link to the conceptDescription containing the details for the Characteristic
              .build();

      return aasProperty;
   }

   private Reference buildReference(Characteristic characteristic) {
      Key key = new DefaultKey.Builder()
              .idType(KeyType.IRI)
              .type(KeyElements.CONCEPT_DESCRIPTION)
              .value(extractIdentifier(characteristic).getIdentifier())
              .build();
      return new DefaultReference.Builder().key(key).build();
   }

   private void createConceptDescription(final Property property, Context context){
     Characteristic characteristic = property.getCharacteristic();
     characteristic.accept(this, context);
   }

   @Override
   public AssetAdministrationShellEnvironment visitCharacteristic(Characteristic characteristic, Context context) {
     ConceptDescription conceptDescription = new DefaultConceptDescription.Builder()
     .idShort(characteristic.getName())
     .displayNames(map(characteristic.getPreferredNames())) // preferred name not found in AAS
     .embeddedDataSpecification(extractEmbeddedDataSpecification(characteristic))
     .identification(extractIdentifier(characteristic))
     .build();
      context.getEnvironment().getConceptDescriptions().add(conceptDescription);
      return context.environment;
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

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitCollection(Collection collection, Context context) {
      return visitCharacteristic(collection, context);
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
   public AssetAdministrationShellEnvironment visitList(io.openmanufacturing.sds.metamodel.List list, Context context) {
      return visitCharacteristic(list, context);
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
   public AssetAdministrationShellEnvironment visitSet(Set set, Context context) {
      return visitCharacteristic(set, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitSingleEntity(SingleEntity singleEntity, Context context) {
      return visitCharacteristic(singleEntity, context);
   }

   // TODO specific implementation required and general Characteristics implementation replaced
   @Override
   public AssetAdministrationShellEnvironment visitSortedSet(SortedSet sortedSet, Context context) {
      return visitCharacteristic(sortedSet, context);
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

}
