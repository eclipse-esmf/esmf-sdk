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

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.esmf.aspectmodel.UnsupportedVersionException;
import org.eclipse.esmf.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.visitor.LanguageCollectorModelVisitor;
import org.eclipse.esmf.samm.KnownVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

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
    * @return The ste of language tabs used in the Aspect Model
    */
   public static Set<Locale> collectUsedLanguages( final Aspect aspect ) {
      return aspect.accept( new LanguageCollectorModelVisitor(), null );
   }

   /**
    * Returns the set of language tags used in the Aspect Model
    *
    * @param model The Aspect Model
    * @return The set of language tags used in the Aspect Model
    */
   public static Set<Locale> collectUsedLanguages( final Model model ) {
      final SdsAspectMetaModelResourceResolver resolver = new SdsAspectMetaModelResourceResolver();
      return resolver.getMetaModelVersion( model ).map( metaModelVersion -> {
         final SAMM SAMM = new SAMM( KnownVersion.fromVersionString( metaModelVersion.toString() )
               .orElseThrow( () -> new UnsupportedVersionException( metaModelVersion ) ) );

         final String nameSpace = model.listStatements( null, RDF.type, SAMM.Aspect() ).nextStatement().getSubject()
               .getNameSpace();
         final Set<Locale> locales = Stream.concat(
                     ImmutableList.copyOf( model.listStatements( null, SAMM.preferredName(), (RDFNode) null ) ).stream(),
                     ImmutableList.copyOf( model.listStatements( null, SAMM.description(), (RDFNode) null ) ).stream() )
               .filter( statement -> !statement.getSubject().isAnon() )
               .filter( statement -> statement.getSubject().getNameSpace()
                     .contains( nameSpace ) )
               .map( Statement::getLanguage )
               .filter( language -> !language.isEmpty() )
               .map( Locale::forLanguageTag )
               .collect( Collectors.toSet() );
         if ( locales.isEmpty() ) {
            locales.add( Locale.ENGLISH );
         }
         return locales;
      } ).recover( throwable -> {
         LOG.warn( "Could not retrieve language tags", throwable );
         return Collections.emptySet();
      } ).get();
   }
}
