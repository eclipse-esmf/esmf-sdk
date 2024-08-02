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

import java.util.Map;
import java.util.function.Function;

import org.apache.jena.rdf.model.Model;

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
               builder.append( RdfUtil.modelToString( model ) );
            } else {
               builder.append( entry.getValue().toString() );
            }
            builder.append( "\n" );
         }
      }
   }

   @Override
   public String apply( final ChangeReport changeReport ) {
      final StringBuilder builder = new StringBuilder();
      append( builder, changeReport, 0 );
      return builder.toString();
   }
}
