/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.metamodel.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.metamodel.datatypes.LangString;
import io.vavr.Value;

/**
 * Wrapper class for the attributes all Aspect Meta Model elements have.
 */
public class MetaModelBaseAttributes {

   private static final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
   private final KnownVersion metaModelVersion;
   private final Optional<AspectModelUrn> urn;
   private final String name;
   private final Set<LangString> preferredNames;
   private final Set<LangString> descriptions;
   private final List<String> see;
   private final boolean hasSyntheticName;

   public MetaModelBaseAttributes( final KnownVersion metaModelVersion,
         final AspectModelUrn urn,
         final String name,
         final Set<LangString> preferredNames,
         final Set<LangString> descriptions,
         final List<String> see ) {
      this( metaModelVersion, urn, name, preferredNames, descriptions, see, false );
   }

   public MetaModelBaseAttributes( final KnownVersion metaModelVersion,
         final AspectModelUrn urn,
         final String name,
         final Set<LangString> preferredNames,
         final Set<LangString> descriptions,
         final List<String> see,
         final boolean hasSyntheticName ) {
      this.metaModelVersion = metaModelVersion;
      this.urn = Optional.ofNullable( urn );
      this.name = name;
      this.preferredNames = preferredNames;
      this.descriptions = descriptions;
      this.see = see;
      this.hasSyntheticName = hasSyntheticName;
   }

   public KnownVersion getMetaModelVersion() {
      return metaModelVersion;
   }

   public Optional<AspectModelUrn> getUrn() {
      return urn;
   }

   public String getName() {
      return name;
   }

   public Set<LangString> getPreferredNames() {
      return preferredNames;
   }

   public Set<LangString> getDescriptions() {
      return descriptions;
   }

   public List<String> getSee() {
      return see;
   }

   public boolean hasSyntheticName() {
      return hasSyntheticName;
   }

   /**
    * Creates a builder for {@link MetaModelBaseAttributes} for the given meta model element name.
    *
    * @param name the meta model element name
    * @return the builder instance
    */
   public static Builder builderFor( final String name ) {
      return new Builder( name );
   }

   /**
    * Creates an instance of {@link MetaModelBaseAttributes} from a meta model version, an URN and a name.
    *
    * @param metaModelVersion the used meta model version
    * @param urn the meta model element URN
    * @param name the meta model element name
    * @return the newly created instance
    */
   public static MetaModelBaseAttributes from( final KnownVersion metaModelVersion, final AspectModelUrn urn, final String name ) {
      return builderFor( name ).withMetaModelVersion( metaModelVersion ).withUrn( urn ).build();
   }

   /**
    * Creates an instance of {@link MetaModelBaseAttributes} from a meta model version, an URN, a name and
    * a preferredName for {@link Locale#ENGLISH}.
    *
    * @param metaModelVersion the used meta model version
    * @param urn the meta model element URN
    * @param name the meta model element name
    * @return the newly created instance
    */
   public static MetaModelBaseAttributes from( final KnownVersion metaModelVersion, final AspectModelUrn urn, final String name, final String preferredName ) {
      return builderFor( name ).withMetaModelVersion( metaModelVersion ).withUrn( urn )
            .withPreferredName( Locale.ENGLISH, preferredName ).build();
   }

   /**
    * Creates an instance of {@link MetaModelBaseAttributes} for a specific Meta Model element.
    *
    * @param metaModelVersion the used meta model version
    * @param metaModelElement the Aspect Meta Model element to be processed.
    * @param model the RDF {@link Model} representing the entire Aspect Meta Model.
    * @param bamm the Aspect Meta Model vocabulary
    * @return the newly created instance
    */
   public static MetaModelBaseAttributes fromMetaModelElement( final KnownVersion metaModelVersion,
         final Resource metaModelElement, final Model model, final BAMM bamm ) {
      final Optional<AspectModelUrn> urn = metaModelElement.isAnon() ?
            Optional.empty() :
            Optional.of( AspectModelUrn.fromUrn( metaModelElement.getURI() ) );
      final Set<LangString> preferredNames = getLanguages( metaModelElement, bamm.preferredName(), model );
      final Set<LangString> descriptions = getLanguages( metaModelElement, bamm.description(), model );
      final List<String> seeValues = getSeeValues( metaModelElement, model, bamm );
      final Optional<String> actualName = urn.map( AspectModelUrn::getName );
      final String name = actualName.orElseGet( () -> getSyntheticName( metaModelElement, model ) );
      final boolean isSyntheticName = actualName.isEmpty();
      return new MetaModelBaseAttributes( metaModelVersion, urn.orElse( null ), name, preferredNames, descriptions, seeValues, isSyntheticName );
   }

   /**
    * Generates a synthetic name for a model element, when the model element is defined as an anonymous node and
    * has no bamm:name set. The synthetic name is a random string that prefixed with the type of the model element
    * if it can be determined easily. For example, for the following model element:
    * <code>
    * [ a bamm-c:LengthConstraint; bamm-c:minValue 5 ]
    * </code>
    * a synthetic name of "LengthConstraint12345ABC" would be generated (with the last part being a random string).
    *
    * @param modelElement a model element
    * @param model the containing model
    * @return a synthetic name for the model element, possibly prefixed with the model element's type name
    */
   private static String getSyntheticName( final Resource modelElement, final Model model ) {
      final String randomPart = UUID.nameUUIDFromBytes( modelElement.getId().toString().getBytes() ).toString()
            .substring( 0, 7 );
      return ImmutableList.copyOf( model.listStatements( modelElement, RDF.type, (RDFNode) null ) ).stream()
            .findFirst()
            .filter( statement -> statement.getObject().isResource() )
            .map( statement -> statement.getObject().asResource() )
            .map( type -> metaModelResourceResolver.getAspectModelUrn( type.getURI() ) )
            .flatMap( Value::toJavaOptional )
            .map( AspectModelUrn::getName )
            .orElse( "" ) + randomPart;
   }

   /**
    * @param subject the RDF {@link Resource} representing the Aspect Model element to be processed
    * @param property the RDF {@link org.apache.jena.rdf.model.Property} for which the values will be retrieved
    * @param model the RDF {@link Model} representing the entire Aspect Meta Model
    * @return a {@link List} containing all values for the given Property in the given Aspect Model element
    */
   private static Set<LangString> getLanguages( final Resource subject,
         final org.apache.jena.rdf.model.Property property, final Model model ) {
      return Streams.stream( model.listStatements( subject, property, (RDFNode) null ) )
            .filter( languageStatement -> !"und"
                  .equals( Locale.forLanguageTag( languageStatement.getLanguage() )
                        .toLanguageTag() ) )
            .map( statement -> new LangString( statement.getString(), Locale.forLanguageTag( statement.getLanguage() ) ) )
            .collect( Collectors.toSet() );
   }

   /**
    * @param resource the RDF {@link Resource} representing the Aspect Model element to be processed
    * @param model the RDF {@link Model} representing the entire Aspect Meta Model
    * @param bamm the Aspect Meta Model vocabulary
    * @return a {@link List} containing all {@link BAMM#see()} values for a Aspect Model element
    */
   private static List<String> getSeeValues( final Resource resource, final Model model, final BAMM bamm ) {
      return ImmutableList
            .copyOf( model.listStatements( resource, bamm.see(), (RDFNode) null ) )
            .stream()
            .map( statement -> statement.getObject().toString() )
            .sorted()
            .collect( Collectors.toList() );
   }

   public static class Builder {
      private AspectModelUrn urn;
      private final String name;
      private final Set<LangString> preferredNames = new HashSet<>();
      private final Set<LangString> descriptions = new HashSet<>();
      private final List<String> see = new ArrayList<>();
      private KnownVersion metaModelVersion;
      private boolean hasSyntheticName;

      public Builder( final String name ) {
         super();
         this.name = name;
      }

      public Builder withUrn( final AspectModelUrn urn ) {
         this.urn = urn;
         return this;
      }

      public Builder withPreferredName( final Locale locale, final String preferredName ) {
         preferredNames.add( new LangString( preferredName, locale ) );
         return this;
      }

      public Builder withDescription( final Locale locale, final String description ) {
         descriptions.add( new LangString( description, locale ) );
         return this;
      }

      public Builder withSee( final String see ) {
         this.see.add( see );
         return this;
      }

      public Builder withMetaModelVersion( final KnownVersion metaModelVersion ) {
         this.metaModelVersion = metaModelVersion;
         return this;
      }

      public Builder hasSyntheticName( final boolean hasSyntheticName ) {
         this.hasSyntheticName = hasSyntheticName;
         return this;
      }

      public MetaModelBaseAttributes build() {
         return new MetaModelBaseAttributes( metaModelVersion, urn, name, preferredNames, descriptions, see, hasSyntheticName );
      }
   }
}
