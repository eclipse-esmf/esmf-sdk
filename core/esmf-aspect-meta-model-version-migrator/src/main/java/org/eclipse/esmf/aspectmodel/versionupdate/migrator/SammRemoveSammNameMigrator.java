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

package org.eclipse.esmf.aspectmodel.versionupdate.migrator;

import java.util.Map;

import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class SammRemoveSammNameMigrator extends AbstractSammMigrator {

   private final SAMM sourceSamm;

   public SammRemoveSammNameMigrator( final KnownVersion sourceKnownVersion, final KnownVersion targetKnownVersion ) {
      super( sourceKnownVersion, targetKnownVersion, 60 );
      this.sourceSamm = new SAMM( sourceKnownVersion );
   }

   @Override
   public Model migrate( final Model sourceModel ) {
      final Model targetModel = ModelFactory.createDefaultModel();
      final Map<String, String> sourcePrefixMap = sourceModel.getNsPrefixMap();
      targetModel.setNsPrefixes( sourcePrefixMap );

      final String sammNameUrn = sourceSamm.getNamespace() + "name";
      sourceModel.listStatements().toList().stream()
            .filter( statement -> !statement.getPredicate().getURI().equals( sammNameUrn ) )
            .forEach( targetModel::add );

      return targetModel;
   }
}
