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
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.eclipse.esmf.aspectmodel.RdfUtil;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Takes a {@link ChangeReport} as an input and renders it as a string
 */
public class ChangeReportFormatter implements ChangeReport.Visitor<String, ChangeReportFormatter.Context>,
      BiFunction<ChangeReport, AspectChangeManagerConfig, String> {
   public static final ChangeReportFormatter INSTANCE = new ChangeReportFormatter();

   private ChangeReportFormatter() {
   }

   public record Context( int indentationLevel, AspectChangeManagerConfig config ) {
      public Context indent() {
         return new Context( indentationLevel() + 1, config() );
      }
   }

   @Override
   public String apply( final ChangeReport changeReport, final AspectChangeManagerConfig aspectChangeManagerConfig ) {
      return changeReport.accept( this, new Context( 0, aspectChangeManagerConfig ) );
   }

   @Override
   public String visitNoChanges( final ChangeReport.NoChanges noChanges, final Context context ) {
      return "";
   }

   @Override
   public String visitSimpleEntry( final ChangeReport.SimpleEntry simpleEntry, final Context context ) {
      return simpleEntry.text();
   }

   @Override
   public String visitEntryWithDetails( final ChangeReport.EntryWithDetails entryWithDetails, final Context context ) {
      final String indent = "  ".repeat( context.indentationLevel() );
      final StringBuilder builder = new StringBuilder();

      builder.append( indent );
      builder.append( "- " );
      builder.append( entryWithDetails.summary() );
      builder.append( "\n" );

      for ( final Map.Entry<String, Model> entry : entryWithDetails.details().entrySet() ) {
         if ( context.config().detailedChangeReport() ) {
            builder.append( indent );
            builder.append( "  - " );
            builder.append( entry.getKey() );
            builder.append( ": " );
            builder.append( "\n" );
            show( entry.getValue() ).lines()
                  .forEach( line -> {
                     builder.append( indent );
                     builder.append( "    " );
                     builder.append( line );
                     builder.append( "\n" );
                  } );
         } else {
            final int numberOfStatements = entry.getValue().listStatements().toList().size();
            final int numberOfPrefixes = entry.getValue().getNsPrefixMap().size();
            if ( numberOfStatements > 0 ) {
               builder.append( indent );
               builder.append( "  - " );
               builder.append( entry.getKey() );
               builder.append( ": " );
               builder.append( numberOfStatements );
               builder.append( " RDF statements and " );
               builder.append( numberOfPrefixes );
               builder.append( " prefixes" );
               builder.append( "\n" );
            }
         }
      }

      return builder.toString();
   }

   @Override
   public String visitMultipleEntries( final ChangeReport.MultipleEntries multipleEntries, final Context context ) {
      final String indent = "  ".repeat( context.indentationLevel() );
      final StringBuilder builder = new StringBuilder();
      if ( multipleEntries.summary() != null ) {
         builder.append( indent );
         builder.append( "- " );
         builder.append( multipleEntries.summary() );
         builder.append( "\n" );
      }
      final List<ChangeReport> entries = multipleEntries.entries();
      for ( int i = 0; i < entries.size(); i++ ) {
         final ChangeReport entry = entries.get( i );
         final Context nestedContext = multipleEntries.summary() == null ? context : context.indent();
         builder.append( entry.accept( this, nestedContext ) );
         if ( i < entries.size() - 1 ) {
            builder.append( "\n" );
         }
      }
      return builder.toString();
   }

   private String show( final Model model ) {
      final Model copy = ModelFactory.createDefaultModel();
      copy.add( model );
      if ( !copy.listStatements().toList().isEmpty() ) {
         RdfUtil.cleanPrefixes( copy );
      }
      final StringWriter stringWriter = new StringWriter();
      stringWriter.append( "--------------------\n" );
      copy.write( stringWriter, "TURTLE" );
      stringWriter.append( "--------------------\n" );
      return stringWriter.toString();
   }
}
