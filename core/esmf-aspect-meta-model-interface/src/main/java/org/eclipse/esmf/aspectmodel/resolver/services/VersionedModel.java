/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver.services;

import java.util.List;

import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Model;

/**
 * Encapsulates an Aspect Model (as RDF model) and the Meta Model version it uses
 */
// TODO: Remove use of this class throughout API, replace by ModelFile/AspectModel
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

   /**
    * The source files of the model
    */
   private final List<ModelFile> sources;

   public VersionedModel( final Model model, final VersionNumber version, final Model rawModel, final List<ModelFile> sources ) {
      this.model = model;
      this.version = version;
      this.rawModel = rawModel;
      this.sources = sources;
   }

   public VersionedModel( final Model model, final VersionNumber version, final Model rawModel ) {
      this( model, version, rawModel, List.of() );
   }

   public VersionedModel( final Model model, final KnownVersion version, final Model rawModel ) {
      this( model, VersionNumber.parse( version.toVersionString() ), rawModel );
   }

   public Model getModel() {
      return model;
   }

   public VersionNumber getMetaModelVersion() {
      return version;
   }

   public Model getRawModel() {
      return rawModel;
   }

   public List<ModelFile> getSources() {
      return sources;
   }

   public VersionedModel withSources( final List<ModelFile> sources ) {
      return new VersionedModel( model, version, rawModel, sources );
   }
}
