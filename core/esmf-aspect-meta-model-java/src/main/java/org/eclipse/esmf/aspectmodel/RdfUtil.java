/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel;

import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.urn.ElementType;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.Streams;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class RdfUtil {
   public static Model getModelElementDefinition( final Resource element ) {
      final Model result = ModelFactory.createDefaultModel();
      element.getModel().listStatements( element, null, (RDFNode) null ).toList().forEach( statement -> {
         final Resource subject = statement.getSubject();
         final Resource newSubject = subject.isAnon()
               ? result.createResource( subject.getId() )
               : result.createResource( subject.getURI() );
         final Property newPredicate = result.createProperty( statement.getPredicate().getURI() );
         final RDFNode newObject;
         if ( statement.getObject().isURIResource() ) {
            newObject = result.createResource( statement.getObject().asResource().getURI() );
         } else if ( statement.getObject().isLiteral() ) {
            newObject = statement.getObject();
         } else if ( statement.getObject().isAnon() ) {
            newObject = result.createResource( statement.getObject().asResource().getId() );
            result.add( getModelElementDefinition( statement.getObject().asResource() ) );
         } else {
            newObject = statement.getObject();
         }
         result.add( newSubject, newPredicate, newObject );
      } );
      return result;
   }

   public static Set<AspectModelUrn> getAllUrnsInModel( final Model model ) {
      return Streams.stream( model.listStatements().mapWith( statement -> {
         final Stream<String> subjectUri = statement.getSubject().isURIResource()
               ? Stream.of( statement.getSubject().getURI() )
               : Stream.empty();
         final Stream<String> propertyUri = Stream.of( statement.getPredicate().getURI() );
         final Stream<String> objectUri = statement.getObject().isURIResource()
               ? Stream.of( statement.getObject().asResource().getURI() )
               : Stream.empty();

         return Stream.of( subjectUri, propertyUri, objectUri )
               .flatMap( Function.identity() )
               .flatMap( urn -> AspectModelUrn.from( urn ).toJavaOptional().stream() );
      } ) ).flatMap( Function.identity() ).collect( toSet() );
   }

   public static void cleanPrefixes( final Model model ) {
      final Map<String, String> originalPrefixMap = new HashMap<>( model.getNsPrefixMap() );
      model.clearNsPrefixMap();
      // SAMM prefixes
      getAllUrnsInModel( model ).forEach( urn -> {
         switch ( urn.getElementType() ) {
            case META_MODEL -> model.setNsPrefix( SammNs.SAMM.getShortForm(), SammNs.SAMM.getNamespace() );
            case CHARACTERISTIC -> model.setNsPrefix( SammNs.SAMMC.getShortForm(), SammNs.SAMMC.getNamespace() );
            case ENTITY -> model.setNsPrefix( SammNs.SAMME.getShortForm(), SammNs.SAMME.getNamespace() );
            case UNIT -> model.setNsPrefix( SammNs.UNIT.getShortForm(), SammNs.UNIT.getNamespace() );
            default -> {
               // nothing to do
            }
         }
      } );
      // XSD
      Stream.concat(
                  Streams.stream( model.listObjects() )
                        .filter( RDFNode::isLiteral )
                        .map( RDFNode::asLiteral )
                        .map( Literal::getDatatypeURI )
                        .filter( type -> type.startsWith( XSD.NS ) )
                        .filter( type -> !type.equals( XSD.xstring.getURI() ) ),
                  Streams.stream( model.listObjects() )
                        .filter( RDFNode::isURIResource )
                        .map( RDFNode::asResource )
                        .map( Resource::getURI )
                        .filter( type -> type.startsWith( XSD.NS ) ) )
            .findAny()
            .ifPresent( resource -> model.setNsPrefix( "xsd", XSD.NS ) );
      // Empty (namespace) prefix
      Streams.stream( model.listStatements( null, RDF.type, (RDFNode) null ) )
            .map( Statement::getSubject )
            .filter( Resource::isURIResource )
            .map( Resource::getURI )
            .map( uri -> uri.endsWith( "#" ) ? uri.replace( "#", "" ) : uri )
            .map( AspectModelUrn::fromUrn )
            .findAny()
            .ifPresent( urn -> model.setNsPrefix( "", urn.getUrnPrefix() ) );
      // Add back custom prefixes not already covered:
      // - if the prefix or URI is not set already
      // - it's not XSD (no need to add it here if it's not added above)
      // - if it's a SAMM URN, it's a regular namespace (not a meta model namespace)
      originalPrefixMap.forEach( ( prefix, uri ) -> {
         if ( !model.getNsPrefixMap().containsKey( prefix )
               && !model.getNsPrefixMap().containsValue( uri )
               && !uri.equals( XSD.NS )
               && ( !uri.startsWith( "urn:samm:" ) || AspectModelUrn.fromUrn( uri + "x" ).getElementType() == ElementType.NONE )
         ) {
            model.setNsPrefix( prefix, uri );
         }
      } );
   }

   /**
    * Convenience method to load an RDF/Turtle model from its String representation
    *
    * @param ttlRepresentation the RDF/Turtle representation of the model
    * @return the parsed model
    */
   public static Model createModel( final String ttlRepresentation ) {
      return TurtleLoader.loadTurtle( ttlRepresentation ).get();
   }

   /**
    * Returns the List of named resources that transitively point to a given node. Transtively here means that the given node
    * is reachable from the named resource via any number of anonymous nodes inbetween.
    *
    * @param node the target node
    * @return the list of resources that point to the node
    */
   public static List<Resource> getNamedElementsReferringTo( final RDFNode node ) {
      final ParameterizedSparqlString sparqlString = new ParameterizedSparqlString();
      sparqlString.setCommandText( "select ?s where { ?s (<>|!<>)* ?node . }" );
      sparqlString.setParam( "node", node );
      try ( final QueryExecution queryExecution = QueryExecutionFactory.create( sparqlString.asQuery(), node.getModel() ) ) {
         return Streams.stream( queryExecution.execSelect() ).flatMap( querySolution -> {
            final RDFNode subject = querySolution.get( "s" );
            return subject.isURIResource() && !subject.equals( node )
                  ? Stream.of( subject.asResource() )
                  : Stream.empty();
         } ).toList();
      }
   }

   /**
    * Merges an RDF-model into another on a per-element basis instead of a per-statement basis. This means only those model element
    * definitions from the model to merge are merged into the target model that are not already present in the target model.
    * This prevents duplicate assertions of statements where the object is a blank node.
    *
    * @param target the model to merge into
    * @param modelToMerge the source model of model element definitions to merge
    */
   public static void mergeModel( final Model target, final Model modelToMerge ) {
      final Set<String> targetSubjects = Streams.stream( target.listSubjects() )
            .filter( Resource::isURIResource )
            .map( Resource::getURI )
            .collect( toSet() );
      for ( final ResIterator it = modelToMerge.listSubjects(); it.hasNext(); ) {
         final Resource resource = it.next();
         if ( resource.isAnon() ) {
            continue;
         }
         if ( !targetSubjects.contains( resource.getURI() ) ) {
            final Model modelElementDefinition = getModelElementDefinition( resource );
            target.add( modelElementDefinition );
         }
      }
      for ( final Map.Entry<String, String> prefixEntry : modelToMerge.getNsPrefixMap().entrySet() ) {
         target.setNsPrefix( prefixEntry.getKey(), prefixEntry.getValue() );
      }
   }
}
