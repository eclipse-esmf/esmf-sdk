/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.sql;

import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.StructureElement;

import com.google.common.base.CaseFormat;

/**
 * Base class for AspectVisitors that create SQL statements
 *
 * @param <C> the context class
 */
public abstract class AspectModelSqlVisitor<C> implements AspectVisitor<String, C> {
   protected void appendLine( final StringBuilder result, final String line ) {
      if ( !line.isBlank() ) {
         // Turning entire result into String for checking its contents can be ineffective,
         // so we pick up the minimum substring here though it's verbose a bit.
         if ( !result.isEmpty() && !result.substring( result.length() < 4 ? 0 : result.length() - 4 ).stripTrailing().endsWith( "," ) ) {
            result.append( ",\n" );
         }
         if ( !line.startsWith( "  " ) && ( result.length() < 2 || !result.substring( result.length() - 2 ).equals( "  " ) ) ) {
            result.append( "  " );
         }
         result.append( line );
      }
   }

   protected String tableName( final StructureElement element ) {
      return CaseFormat.UPPER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, element.getName() );
   }

   protected String columnName( final Property property ) {
      return CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, property.getPayloadName() );
   }
}
