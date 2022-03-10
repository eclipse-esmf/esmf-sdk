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

package io.openmanufacturing.sds.aspectmodel.java;

import java.util.regex.Pattern;

import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.CollectionValue;
import io.openmanufacturing.sds.metamodel.EntityInstance;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

/**
 * Creates a valid Java enumeration key for a {@link io.openmanufacturing.sds.metamodel.Value}
 */
public class ValueToEnumKeyVisitor implements AspectVisitor<String, Void> {
   @Override
   public String visitBase( final Base base, final Void context ) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String visitScalarValue( final ScalarValue value, final Void context ) {
      final String stringValue = value.getValue().toString();

      final Pattern startsWithNumber = Pattern.compile( "^[0-9]" );
      if ( startsWithNumber.matcher( stringValue ).lookingAt() ) {
         return "NUMBER_" + AspectModelJavaUtil.toConstant( stringValue.replaceAll( "[^\\p{Alnum}]", "_" ) );
      }

      return AspectModelJavaUtil.toConstant( stringValue
                  .replaceAll( "([^\\p{Alnum}_])", "_" )
                  .replaceAll( "^[^\\p{Alpha}_]", "_" ) )
            .replaceAll( "__*", "_" );
   }

   @Override
   public String visitEntityInstance( final EntityInstance instance, final Void context ) {
      return AspectModelJavaUtil.toConstant( instance.getName() );
   }

   @Override
   public String visitCollectionValue( final CollectionValue value, final Void context ) {
      throw new UnsupportedOperationException( "Tried to create an enumeration key for a collection value" );
   }
}
