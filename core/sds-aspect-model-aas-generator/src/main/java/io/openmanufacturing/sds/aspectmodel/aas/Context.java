/*
 * Copyright (c) 2021, 2022 Robert Bosch Manufacturing Solutions GmbH
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.ModelingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import com.fasterxml.jackson.databind.JsonNode;

import io.openmanufacturing.sds.metamodel.Property;

/**
 * Contains and tracks the context while the {@link AspectModelAASVisitor} traverses the metamodel.
 */
public class Context {

   Environment environment;
   Submodel submodel;
   Property property;
   SubmodelElement propertyResult;
   List<Property> propertyPath = new ArrayList<>();
   JsonNode aspectData;
   Map<Property, Integer> indices = new HashMap<>();

   public Context( final Environment environment, final Submodel ofInterest ) {
      this.environment = environment;
      submodel = ofInterest;
   }

   /**
    * When iterating over the aspect data of a collection-valued property, this method has to be used to track the iteration for each property in the property
    * graph.
    * Iteration is always forward, i.e. the index will always increase by 1.
    *
    * @param collectionProperty the property being iterated
    * @return {@code this} context
    */
   public Context iterate( final Property collectionProperty ) {
      indices.put( collectionProperty, 1 + indices.getOrDefault( collectionProperty, -1 ) );

      return this;
   }

   /**
    * Finishes the iteration for a collection-valued property, i.e. removes index tracking.
    *
    * @param collectionProperty the property to end the iteration for
    * @return {@code this} context
    */
   public Context finishIteration( final Property collectionProperty ) {
      indices.remove( collectionProperty );

      return this;
   }

   public boolean hasEnvironmentConceptDescription( final String id ) {
      return getEnvironment().getConceptDescriptions().stream()
                             .anyMatch( x -> x.getId().equals( id ) );
   }

   public ConceptDescription getConceptDescription( final String id ) {
      final Optional<ConceptDescription> optional =
            getEnvironment().getConceptDescriptions().stream()
                            .filter( x -> x.getId().equals( id ) )
                            .findFirst();
      if ( optional.isEmpty() ) {
         throw new IllegalArgumentException(
               String.format( "No ConceptDescription with name %s available.", id ) );
      }
      return optional.get();
   }

   public Environment getEnvironment() {
      return environment;
   }

   public void setEnvironment( final Environment environment ) {
      this.environment = environment;
   }

   public Submodel getSubmodel() {
      return submodel;
   }

   public void appendToSubModelElements( final List<SubmodelElement> elements ) {
      // Hint: As the AAS Meta Model Implementation exposes the internal data structure where the
      // elements of a collection are stored, just setting it would overwrite previous entries.
      // Hence, this approach.
      getSubmodel().getSubmodelElements().addAll( elements );
   }

   public Property getProperty() {
      return property;
   }

   public void setProperty( final Property property ) {
      this.property = property;
      propertyPath.add( property );
   }

   public SubmodelElement getPropertyResult() {
      propertyPath.remove( propertyPath.size() - 1 );
      return propertyResult;
   }

   public void setPropertyResult( final SubmodelElement propertyResult ) {
      this.propertyResult = propertyResult;
   }

   public JsonNode getAspectData() {
      return aspectData;
   }

   public void setAspectData( final JsonNode aspectData ) {
      this.aspectData = aspectData;
   }

   public ModelingKind getModelingKind() {
      if ( aspectData == null ) {
         return ModelingKind.TEMPLATE;
      }

      return ModelingKind.INSTANCE;
   }

   /**
    * Retrieves the string value at the current property path or the given default value, if none exists.
    *
    * @param defaultValue the default value to use when no value could be found at the current property path
    * @return the property value at the current property path
    */
   public String getPropertyValue( final String defaultValue ) {
      return getRawPropertyValue().map( JsonNode::asText ).orElse( defaultValue );
   }

   /**
    * Retrieves the raw JSON property value at the current property path.
    *
    * @return a present {@link Optional} with the {@link JsonNode} if it could be found, {@link Optional#empty()} else.
    */
   public Optional<JsonNode> getRawPropertyValue() {
      if ( aspectData == null ) {
         return Optional.empty();
      }

      AtomicReference<String> pathExpression = new AtomicReference<>(
            "/" + String.join( "/", propertyPath.stream().map( Property::getPayloadName ).toList() ) );
      indices.entrySet()
             .forEach( index -> {
                pathExpression.getAndUpdate(
                      path -> path.replace( index.getKey().getPayloadName(), index.getKey().getPayloadName() + "/" + index.getValue() ) );
             } );

      return Optional.of( aspectData.at( pathExpression.get() ) );
   }
}
