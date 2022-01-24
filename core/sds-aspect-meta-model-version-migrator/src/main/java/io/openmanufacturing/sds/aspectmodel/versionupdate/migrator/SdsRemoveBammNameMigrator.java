/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.versionupdate.migrator;

import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;

public class SdsRemoveBammNameMigrator extends AbstractSdsMigrator {

   private final BAMM bamm;

   public SdsRemoveBammNameMigrator( final KnownVersion sourceKnownVersion, final KnownVersion targetKnownVersion ) {
      super( sourceKnownVersion, targetKnownVersion, 60 );
      this.bamm = new BAMM( targetKnownVersion );
   }

   @Override
   public Model migrate( final Model sourceModel ) {
      final Model targetModel = ModelFactory.createDefaultModel();
      final Map<String, String> sourcePrefixMap = sourceModel.getNsPrefixMap();
      targetModel.setNsPrefixes( sourcePrefixMap );

      final String bammNameUrn = bamm.getNamespace() + "name";
      sourceModel.listStatements().toList().stream()
            .filter( statement -> !statement.getPredicate().getURI().equals( bammNameUrn ) )
            .forEach( targetModel::add );

      return targetModel;
   }
}
