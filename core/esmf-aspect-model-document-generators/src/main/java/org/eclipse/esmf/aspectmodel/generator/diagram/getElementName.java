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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.vocabulary.RDF;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * A custom SPARQL function which provides the name of an Aspect model element which may be defined as a named node or an anonymous node. In case the element
 * is defined as a named node, the name from the URN of the element is returned. In case the element is defined as an anonymous node, a synthetic name is
 * returned.
 */
public class getElementName extends FunctionBase1 {

   private Model context;

   /**
    * @param context the {@link Model} holding the Aspect model which is being processed by the SPARQL queries using the custom {@link getElementName}
    *                function.
    */
   public getElementName( final Model context ) {
      super();
      this.context = context;
   }

   @Override
   public NodeValue exec( NodeValue nodeValue ) {
      final Node node = nodeValue.asNode();
      if ( node.isBlank() ) {
         final Resource nodeResource = ((ModelCom) context).getNodeAs( node, Resource.class );
         final List<Statement> nodeProperties = context.listStatements( nodeResource, null, (RDFNode) null ).toList();
         final String nodePropertyValues = nodeProperties.stream().map( Statement::getObject )
               .filter( rdfNode -> !rdfNode.isAnon() )
               .map( RDFNode::toString )
               .collect( Collectors.joining( ":" ) );
         final String randomPart = UUID.nameUUIDFromBytes( nodePropertyValues.getBytes() ).toString()
               .substring( 0, 7 );
         final Node typePropertyValue = context.listStatements( nodeResource, RDF.type, (RDFNode) null ).nextStatement().getObject().asNode();
         final AspectModelUrn typePropertyUrn = AspectModelUrn.fromUrn( typePropertyValue.getURI() );
         final String syntheticElementName = typePropertyUrn.getName() + randomPart;
         return NodeValue.makeString( syntheticElementName );
      }
      final AspectModelUrn nodeUrn = AspectModelUrn.fromUrn( node.getURI() );
      return NodeValue.makeString( nodeUrn.getName() );
   }
}
