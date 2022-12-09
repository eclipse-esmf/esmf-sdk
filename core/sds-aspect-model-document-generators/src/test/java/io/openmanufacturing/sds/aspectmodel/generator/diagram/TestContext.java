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

package io.openmanufacturing.sds.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.vocabulary.RDF;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.test.TestModel;
import io.openmanufacturing.sds.test.TestResources;

public class TestContext {
   private final AspectModelDiagramGenerator service;
   private final KnownVersion version;
   private final BoxModel boxModel;
   private final GetElementNameFunctionFactory getElementNameFunctionFactory;

   public TestContext( final TestModel model, final KnownVersion version ) {
      this.version = version;
      final VersionedModel versionedModel = TestResources.getModel( model, version ).get();
      service = new AspectModelDiagramGenerator( versionedModel );
      boxModel = new BoxModel( version );
      service.model.setNsPrefix( "", boxModel.getNamespace() );
      getElementNameFunctionFactory = new GetElementNameFunctionFactory( versionedModel.getModel() );
   }

   /**
    * Returns a statement selector in a simple statement syntax. Valid examples:
    * :Foo a :Bar
    * :Foo a *
    * * rdfs:subClassOf *
    *
    * @param statement The statement, which consists of subject, predicate and object, each of which can be an
    *       asterisk as wildcard
    * @return A statement selector for the pattern
    */
   public Selector selector( final String statement ) {
      final String[] parts = statement.split( " ", 3 );
      final Resource subject = parts[0].equals( "*" ) ?
            null : ResourceFactory.createResource( resolve( parts[0] ) );
      final Property predicate;
      switch ( parts[1] ) {
      case "*":
         predicate = null;
         break;
      case "a":
         predicate = RDF.type;
         break;
      default:
         predicate = ResourceFactory.createProperty( resolve( parts[1] ) );
      }

      if ( parts[2].contains( ":" ) ) {
         final Resource object = ResourceFactory.createResource( resolve( parts[2] ) );
         return new SimpleSelector( subject, predicate, object );
      }

      final Literal object = parts[2].equals( "*" ) ?
            null : ResourceFactory.createStringLiteral( parts[2] );
      return new SimpleSelector( subject, predicate, object );
   }

   /**
    * Resolve the namespace of a statement pattern against the prefixes of the Aspect Model, e.g.
    * "rdf:type" will return "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
    * ":Box" will return "urn:bamm:io.openmanufacturing:meta-model:1.0.0/boxmodel#Box"
    *
    * @param statementPattern This short statement pattern
    * @return The expanded statement pattern
    */
   public String resolve( final String statementPattern ) {
      final String[] statementParts = statementPattern.split( ":" );
      final Map<String, String> prefixMap = service.model.getNsPrefixMap();
      return prefixMap.get( statementParts[0] ) + statementParts[1];
   }

   public void executeAttributeIsPresentTest( final String sparqlQueryFileName,
         final String boxSelectorStatement, final String entriesSelectorStatement,
         final int totalNumberOfExpectedEntries,
         final int indexOfExpectedEntry, final String expectedTitle, final String expectedValue ) {

      final Model queryResult = executeQuery( sparqlQueryFileName );
      assertThat( queryResult.listStatements( selector( boxSelectorStatement ) ).toList() ).hasSizeGreaterThanOrEqualTo( 1 );

      final List<RDFNode> entries = queryResult
            .listStatements( selector( entriesSelectorStatement ) )
            .nextStatement().getObject().as( RDFList.class ).asJavaList();

      assertEntry( entries, totalNumberOfExpectedEntries, indexOfExpectedEntry, expectedTitle, expectedValue );
   }

   public void executeAttributeIsNotPresentTest( final String sparqlQueryFileName,
         final String boxSelectorStatement, final String entriesSelectorStatement,
         final int totalNumberOfExpectedEntries, final int indexOfExpectedEntry ) {

      final Model queryResult = executeQuery( sparqlQueryFileName );
      assertThat( queryResult.listStatements( selector( boxSelectorStatement ) ).toList() ).hasSize( 1 );

      final List<RDFNode> entries = queryResult
            .listStatements( selector( entriesSelectorStatement ) )
            .nextStatement().getObject().as( RDFList.class ).asJavaList();
      assertThat( entries ).hasSize( totalNumberOfExpectedEntries );

      final Statement entryText = entries.get( indexOfExpectedEntry ).asResource().getProperty( boxModel.text() );
      assertThat( entryText ).isNull();
   }

   public Model executeQuery( final String sparqlQueryFileName ) {
      final Query query = QueryFactory.create( getInputStreamAsString( sparqlQueryFileName ) );
      final Model queryResult = ModelFactory.createDefaultModel();
      try ( final QueryExecution qexec = QueryExecutionFactory.create( query, model() ) ) {
         FunctionRegistry.get( qexec.getContext() ).put( AspectModelDiagramGenerator.GET_ELEMENT_NAME_FUNC, getElementNameFunctionFactory );
         qexec.execConstruct( queryResult );
      }
      return queryResult;
   }

   public void assertEntry( final List<RDFNode> entries, final int totalNumberOfExpectedEntries,
         final int indexOfExpectedEntry, final String expectedTitle, final String expectedValue ) {
      assertThat( entries ).hasSize( totalNumberOfExpectedEntries );

      assertThat( entries.get( indexOfExpectedEntry ).asResource().getPropertyResourceValue( RDF.type ) )
            .isEqualTo( boxModel.entry() );
      assertThat(
            entries.get( indexOfExpectedEntry ).asResource().getProperty( boxModel.title() ).getObject().asLiteral()
                  .getString() ).isEqualTo( expectedTitle );

      final String entryText = entries.get( indexOfExpectedEntry ).asResource().getProperty( boxModel.text() )
            .getObject().asLiteral().getString();
      assertThat( entryText ).isEqualTo( expectedValue );
   }

   public InputStream getInputStream( final String resource ) {
      return service.getInputStream( resource );
   }

   public String getInputStreamAsString( final String resource ) {
      return service.getInputStreamAsString( resource );
   }

   public Model model() {
      return service.model;
   }

   public BoxModel boxModel() {
      return boxModel;
   }

   public KnownVersion getVersion() {
      return version;
   }

   public String executeQuery( final Model model, final Query query ) {
      return service.executeQuery( model, query );
   }

   public AspectModelDiagramGenerator service() {
      return service;
   }
}
