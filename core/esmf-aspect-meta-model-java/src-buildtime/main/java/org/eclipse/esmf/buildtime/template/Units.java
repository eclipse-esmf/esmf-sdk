/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.buildtime.template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.impl.DefaultUnit;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

/**
 * Enumeration of Units as defined in <a href="https://tfig.unece.org/contents/recommendation-20.htm">Recommendation 20</a> by
 * the <a href="https://www.unece.org/info/ece-homepage.html">UNECE</a> (United Nations Economic Commission for Europe).
 */
@Generated( "${generator}" )
public class Units {
   private static final Map<String, Unit> UNITS_BY_NAME = new HashMap<>();

   private Units() {
   }

   // ${initMethods}

   /**
    * Returns the file that defines this unit
    */
   public AspectModelFile getSourceFile() {
      return MetaModelFile.UNITS;
   }

   /**
    * Returns the unit with a given name
    */
   public static synchronized Optional<Unit> fromName( final String name ) {
      if ( UNITS_BY_NAME.isEmpty() ) {
         // ${initMethodCalls}
      }
      return Optional.ofNullable( UNITS_BY_NAME.get( name ) );
   }

   /**
    * Finds the unit with a given code
    */
   public static Optional<Unit> fromCode( final String code ) {
      if ( UNITS_BY_NAME.isEmpty() ) {
         fromName( "" );
      }
      return UNITS_BY_NAME.values()
            .stream()
            .filter( unit -> unit.getCode().map( code2 -> code2.equals( code ) ).orElse( false ) )
            .findAny();
   }

   /**
    * Finds the units with a given symbol. In most, but not all cases, this set will contain exactly one unit.
    */
   public static Set<Unit> fromSymbol( final String symbol ) {
      if ( UNITS_BY_NAME.isEmpty() ) {
         fromName( "" );
      }
      return UNITS_BY_NAME.values()
            .stream()
            .flatMap( unit -> unit.getSymbol()
                  .filter( symbol2 -> symbol2.equals( symbol ) )
                  .stream()
                  .map( symbol2 -> unit ) )
            .collect( Collectors.toSet() );
   }

   /**
    * Returns all units of a given quantity kind
    */
   public static Set<Unit> unitsWithQuantityKind( final QuantityKind quantityKind ) {
      if ( UNITS_BY_NAME.isEmpty() ) {
         fromName( "" );
      }
      return UNITS_BY_NAME.values()
            .stream()
            .filter( unit -> unit.getQuantityKinds().contains( quantityKind ) )
            .collect( Collectors.toSet() );
   }
}
