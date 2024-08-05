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

package org.eclipse.esmf.aspectmodel.edit;

import java.io.StringWriter;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class ChangeReportFormatter implements Function<ChangeReport, String> {
   public static final ChangeReportFormatter INSTANCE = new ChangeReportFormatter();

   private ChangeReportFormatter() {
   }

   private void append( final StringBuilder builder, final ChangeReport report, final int indentation ) {
      final String indent = " ".repeat( indentation );
      if ( report instanceof final ChangeReport.SimpleEntry simpleEntry ) {
         builder.append( simpleEntry.text() );
         builder.append( "\n" );
      } else if ( report instanceof final ChangeReport.MultipleEntries multipleEntries ) {
         for ( final ChangeReport entry : multipleEntries.entries() ) {
            append( builder, entry, indentation + 2 );
            builder.append( "\n" );
         }
      } else if ( report instanceof final ChangeReport.EntryWithDetails entryWithDetails ) {
         builder.append( indent );
         builder.append( entryWithDetails.summary() );
         builder.append( "\n" );
         for ( final Map.Entry<String, Object> entry : entryWithDetails.details().entrySet() ) {
            builder.append( indent );
            builder.append( indent );
            builder.append( entry.getKey() );
            builder.append( ": " );
            if ( entry.getValue() instanceof final Model model ) {
               builder.append( "\n" );
               show( model ).lines()
                     .forEach( line -> {
                        builder.append( indent );
                        builder.append( indent );
                        builder.append( indent );
                        builder.append( line );
                        builder.append( "\n" );
                     } );
            } else {
               builder.append( entry.getValue().toString() );
               builder.append( "\n" );
            }
         }
      }
   }

   protected String show( final Model model ) {
      final Model copy = ModelFactory.createDefaultModel();
      copy.add( model );
      RdfUtil.getAllUrnsInModel( model ).forEach( urn -> {
         switch ( urn.getElementType() ) {
            case META_MODEL -> copy.setNsPrefix( SammNs.SAMM.getShortForm(), SammNs.SAMM.getNamespace() );
            case CHARACTERISTIC -> copy.setNsPrefix( SammNs.SAMMC.getShortForm(), SammNs.SAMMC.getNamespace() );
            case ENTITY -> copy.setNsPrefix( SammNs.SAMME.getShortForm(), SammNs.SAMME.getNamespace() );
            case UNIT -> copy.setNsPrefix( SammNs.UNIT.getShortForm(), SammNs.UNIT.getNamespace() );
         }
      } );
      Streams.stream( model.listObjects() )
            .filter( RDFNode::isLiteral )
            .map( RDFNode::asLiteral )
            .map( Literal::getDatatypeURI )
            .filter( type -> type.startsWith( XSD.NS ) )
            .filter( type -> !type.equals( XSD.xstring.getURI() ) )
            .findAny()
            .ifPresent( __ -> copy.setNsPrefix( "xsd", XSD.NS ) );
      Streams.stream( model.listStatements( null, RDF.type, (RDFNode) null ) )
            .map( Statement::getSubject )
            .map( Resource::getURI )
            .map( AspectModelUrn::fromUrn )
            .forEach( urn -> copy.setNsPrefix( "", urn.getUrnPrefix() ) );
      final StringWriter stringWriter = new StringWriter();
      stringWriter.append( "--------------------\n" );
      copy.write( stringWriter, "TURTLE" );
      stringWriter.append( "--------------------\n" );
      return stringWriter.toString();
   }

   @Override
   public String apply( final ChangeReport changeReport ) {
      final StringBuilder builder = new StringBuilder();
      append( builder, changeReport, 0 );
      return builder.toString();
   }
}
