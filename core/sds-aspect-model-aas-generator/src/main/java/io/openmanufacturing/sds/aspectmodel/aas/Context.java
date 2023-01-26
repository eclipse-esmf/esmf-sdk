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

import java.util.List;
import java.util.Optional;

import org.eclipse.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.aas4j.v3.model.ConceptDescription;
import org.eclipse.aas4j.v3.model.Environment;
import org.eclipse.aas4j.v3.model.Submodel;
import org.eclipse.aas4j.v3.model.SubmodelElement;

import io.openmanufacturing.sds.metamodel.Property;

public class Context {

   Environment environment;
   Submodel submodel;
   Property property;
   SubmodelElement propertyResult;

   public Context( Environment environment, Submodel ofInterest ) {
      this.environment = environment;
      this.submodel = ofInterest;
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
   }

   public SubmodelElement getPropertyResult() {
      return propertyResult;
   }

   public void setPropertyResult( final SubmodelElement propertyResult ) {
      this.propertyResult = propertyResult;
   }

   public void setEnvironment( Environment environment ) {
      this.environment = environment;
   }
}
