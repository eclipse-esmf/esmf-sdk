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

package org.eclipse.esmf.aspectmodel.generator;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.visitor.AspectStreamTraversalVisitor;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.datatype.LangString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the logic to determine the language tags used in an Aspect Model.
 */
public class LanguageCollector {
   private static final Logger LOG = LoggerFactory.getLogger( LanguageCollector.class );

   private LanguageCollector() {
   }

   /**
    * Returns the set of language tags used in the Aspect model
    *
    * @param aspect The Aspect Model
    * @return The set of language tags used in the Aspect Model
    */
   public static Set<Locale> collectUsedLanguages( final Aspect aspect ) {
      final Stream<Locale> fromModel = new AspectStreamTraversalVisitor().visitAspect( aspect, null )
            .flatMap( element -> Stream.concat(
                  element.getPreferredNames().stream().map( LangString::getLanguageTag ),
                  element.getDescriptions().stream().map( LangString::getLanguageTag ) ) );
      return Stream.concat( fromModel, Stream.of( Locale.ENGLISH ) ).collect( toSet() );
   }

   public static Set<Locale> collectUsedLanguages( final Collection<Aspect> aspects ) {
      return aspects.stream().map( LanguageCollector::collectUsedLanguages ).flatMap( Set::stream ).collect( toSet() );
   }
}
