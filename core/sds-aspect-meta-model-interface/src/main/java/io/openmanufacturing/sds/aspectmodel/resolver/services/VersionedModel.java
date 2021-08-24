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

package io.openmanufacturing.sds.aspectmodel.resolver.services;

import org.apache.jena.rdf.model.Model;

import io.openmanufacturing.sds.aspectmodel.VersionNumber;

/**
 * Encapsulates an Aspect Model (as RDF model) and the Meta Model version it uses
 */
public class VersionedModel {
   /**
    * The model including its corresponding meta model
    */
   private final Model model;

   /**
    * The model without its corresponding meta model
    */
   private final Model rawModel;

   /**
    * The meta model version the model refers to
    */
   private final VersionNumber version;

   public VersionedModel( final Model model, final VersionNumber version, final Model rawModel ) {
      this.model = model;
      this.version = version;
      this.rawModel = rawModel;
   }

   public Model getModel() {
      return model;
   }

   public VersionNumber getVersion() {
      return version;
   }

   public Model getRawModel() {
      return rawModel;
   }
}
