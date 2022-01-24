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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMMC;

public class SdsAnonymousEnumerationMigrator extends AbstractSdsMigrator {

   private final BAMM bamm;
   private final BAMMC bammc;

   public SdsAnonymousEnumerationMigrator( final KnownVersion sourceKnownVersion, final KnownVersion targetKnownVersion ) {
      super( sourceKnownVersion, targetKnownVersion, 55 );
      this.bamm = new BAMM( targetKnownVersion );
      this.bammc = new BAMMC( targetKnownVersion );
   }

   @Override
   public Model migrate( final Model sourceModel ) {
      final List<Resource> anonymousEnumerations = sourceModel.listStatements( null, RDF.type, bammc.Enumeration() ).toList().stream()
            .map( Statement::getSubject )
            .filter( RDFNode::isAnon ).collect( Collectors.toList() );

      final String localNameSpace = sourceModel.listStatements( null, RDF.type, bamm.Aspect() ).nextStatement().getSubject().getNameSpace();
      anonymousEnumerations.forEach( anonymousEnumeration -> {
         final Property bammName = ResourceFactory.createProperty( bamm.getNamespace() + "name" );
         final String enumerationName = anonymousEnumeration.getProperty( bammName ).getObject().asLiteral().getString();
         final String namedEnumerationUrn = String.format( "%s%s", localNameSpace, enumerationName );
         final Resource namedEnumeration = ModelFactory.createDefaultModel().createResource( namedEnumerationUrn );
         anonymousEnumeration.listProperties().toList().forEach( statement -> namedEnumeration.addProperty( statement.getPredicate(), statement.getObject() ) );
         sourceModel.listStatements( null, null, anonymousEnumeration ).toList().forEach( statement -> statement.changeObject( namedEnumeration ) );
         sourceModel.add( namedEnumeration.getModel() );
         sourceModel.remove( anonymousEnumeration,  RDF.type, bammc.Enumeration() );
      } );

      return sourceModel;
   }
}
