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
public class ChangeReportFormatter implements BiFunction<ChangeReport, AspectChangeManagerConfig, String> {
   public static final ChangeReportFormatter INSTANCE = new ChangeReportFormatter();

   private ChangeReportFormatter() {
   }

   private void handleSimpleEntry( final StringBuilder builder, final ChangeReport.SimpleEntry simpleEntry, final String indent ) {
      builder.append( indent );
      builder.append( "- " );
      builder.append( simpleEntry.text() );
      builder.append( "\n" );
   }

   private void handleMultipleEntries( final StringBuilder builder, final ChangeReport.MultipleEntries multipleEntries, final String indent,
         final int indentationLevel, final AspectChangeManagerConfig config ) {
      if ( multipleEntries.summary() != null ) {
         builder.append( indent );
         builder.append( "- " );
         builder.append( multipleEntries.summary() );
         builder.append( "\n" );
      }
      final List<ChangeReport> entries = multipleEntries.entries();
      for ( int i = 0; i < entries.size(); i++ ) {
         final ChangeReport entry = entries.get( i );
         final int entryIndentation = multipleEntries.summary() == null
               ? indentationLevel
               : indentationLevel + 1;
         append( builder, entry, config, entryIndentation );
         if ( i < entries.size() - 1 ) {
            builder.append( "\n" );
         }
      }
   }

   private void handleEntryWithDetails( final StringBuilder builder, final ChangeReport.EntryWithDetails entryWithDetails,
         final String indent, final AspectChangeManagerConfig config ) {
      builder.append( indent );
      builder.append( "- " );
      builder.append( entryWithDetails.summary() );
      builder.append( "\n" );
      for ( final Map.Entry<String, Object> entry : entryWithDetails.details().entrySet() ) {
         if ( config.detailedChangeReport() && entry.getValue() instanceof final Model model ) {
            builder.append( indent );
            builder.append( "  - " );
            builder.append( entry.getKey() );
            builder.append( ": " );
            builder.append( "\n" );
            show( model ).lines()
                  .forEach( line -> {
                     builder.append( indent );
                     builder.append( "    " );
                     builder.append( line );
                     builder.append( "\n" );
                  } );
         } else if ( !config.detailedChangeReport() && entry.getValue() instanceof final Model model ) {
            final int numberOfStatements = model.listStatements().toList().size();
            if ( numberOfStatements > 0 ) {
               builder.append( indent );
               builder.append( "  - " );
               builder.append( entry.getKey() );
               builder.append( ": " );
               builder.append( numberOfStatements );
               builder.append( " RDF statements" );
               builder.append( "\n" );
            }
         } else {
            builder.append( indent );
            builder.append( "  - " );
            builder.append( entry.getKey() );
            builder.append( ": " );
            builder.append( entry.getValue().toString() );
            builder.append( "\n" );
         }
      }
   }

   private void append( final StringBuilder builder, final ChangeReport report, final AspectChangeManagerConfig config,
         final int indentationLevel ) {
      final String indent = "  ".repeat( indentationLevel );
      if ( report instanceof final ChangeReport.SimpleEntry simpleEntry ) {
         handleSimpleEntry( builder, simpleEntry, indent );
      } else if ( report instanceof final ChangeReport.MultipleEntries multipleEntries ) {
         handleMultipleEntries( builder, multipleEntries, indent, indentationLevel, config );
      } else if ( report instanceof final ChangeReport.EntryWithDetails entryWithDetails ) {
         handleEntryWithDetails( builder, entryWithDetails, indent, config );
      }
   }

   private String show( final Model model ) {
      final Model copy = ModelFactory.createDefaultModel();
      copy.add( model );
      RdfUtil.cleanPrefixes( copy );
      final StringWriter stringWriter = new StringWriter();
      stringWriter.append( "--------------------\n" );
      copy.write( stringWriter, "TURTLE" );
      stringWriter.append( "--------------------\n" );
      return stringWriter.toString();
   }

   @Override
   public String apply( final ChangeReport changeReport, final AspectChangeManagerConfig config ) {
      final StringBuilder builder = new StringBuilder();
      append( builder, changeReport, config, 0 );
      return builder.toString();
   }
}
