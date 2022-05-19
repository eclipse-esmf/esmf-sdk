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

import org.apache.commons.lang3.StringUtils;
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
    * @param modelElement the Aspect model element to be processed.
    * @param model the RDF {@link Model} representing the entire Aspect Meta Model.
    * @param bamm the Aspect Meta Model vocabulary
    * @return the newly created instance
    */
   public static MetaModelBaseAttributes fromModelElement( final KnownVersion metaModelVersion,
         final Resource modelElement, final Model model, final BAMM bamm ) {
      final AttributeValueRetriever valueRetriever = new AttributeValueRetriever( bamm );

      final Optional<AspectModelUrn> urn = getUrn( modelElement, bamm );
      final Set<LangString> preferredNames = getLanguages( modelElement, bamm.preferredName(), valueRetriever );
      final Set<LangString> descriptions = getLanguages( modelElement, bamm.description(), valueRetriever );
      final List<String> seeValues = getSeeValues( modelElement, bamm, valueRetriever );
      final String name = getName( modelElement, bamm )
            .orElseGet( () -> getSyntheticName( modelElement, model, bamm ) );
      final boolean isSyntheticName = urn.isEmpty();
      return new MetaModelBaseAttributes( metaModelVersion, urn.orElse( null ), name, preferredNames, descriptions, seeValues, isSyntheticName );
   }

   private static Optional<AspectModelUrn> getUrn( final Resource modelElement, final BAMM bamm ) {
      if ( modelElement.isAnon() ) {
         final Statement propertyStatement = modelElement.getProperty( bamm.property() );
         if ( propertyStatement != null ) {
            return getUrn( propertyStatement.getObject().asResource(), bamm );
         }
         return Optional.empty();
      }
      return Optional.of( AspectModelUrn.fromUrn( modelElement.getURI() ) );
   }

   /**
    * Returns a model element's name: If it's a named resource, the name is part of its URN; otherwise
    * (e.g., [ bamm:extends :foo ; ... ]) go up the inheritance tree recursively.
    *
    * @param modelElement the model element to retrieve the name for
    * @param bamm the meta model vocabulary
    * @return the element's local name
    */
   private static Optional<String> getName( final Resource modelElement, final BAMM bamm ) {
      if ( !modelElement.isAnon() ) {
         return Optional.of( AspectModelUrn.fromUrn( modelElement.getURI() ).getName() );
      }

      final Statement propertyStatement = modelElement.getProperty( bamm.property() );
      if ( propertyStatement != null ) {
         return getName( propertyStatement.getObject().asResource(), bamm );
      }

      final Optional<Statement> extendsStatement = Streams.stream(
            modelElement.getModel().listStatements( modelElement, bamm._extends(), (RDFNode) null ) ).findAny();
      return extendsStatement.flatMap( statement -> getName( statement.getObject().asResource(), bamm ) );
   }

   private static String getSyntheticName( final Resource modelElement, final Model model, final BAMM bamm ) {
      Resource parentModelElement = model.listStatements( null, null, modelElement ).next().getSubject();
      while ( parentModelElement.isAnon() ) {
         parentModelElement = model.listStatements( null, null, parentModelElement ).next().getSubject();
      }

      final String parentModelElementUri = parentModelElement.getURI();
      final String parentModelElementName = metaModelResourceResolver.getAspectModelUrn( parentModelElementUri )
            .toJavaOptional()
            .map( AspectModelUrn::getName )
            .map( StringUtils::capitalize )
            .orElse( "" );

      final Resource modelElementType = getModelElementType( modelElement, bamm );
      final String modelElementTypeUri = modelElementType.getURI();
      final String modelElementTypeName = metaModelResourceResolver.getAspectModelUrn( modelElementTypeUri )
            .toJavaOptional()
            .map( AspectModelUrn::getName )
            .orElse( "" );

      return parentModelElementName + modelElementTypeName;
   }

   private static Resource getModelElementType( final Resource modelElement, final BAMM bamm ) {
      final Statement typeStatement = modelElement.getProperty( RDF.type );
      if ( typeStatement != null ) {
         return typeStatement.getObject().asResource();
      }

      // If the model element is a Property reference, the actual type will be found when we follow bamm:property
      final Statement propertyStatement = modelElement.getProperty( bamm.property() );
      if ( propertyStatement != null ) {
         return getModelElementType( propertyStatement.getObject().asResource(), bamm );
      }

      // This model element has no type, but maybe it extends another element
      final Statement extendsStatement = modelElement.getProperty( bamm._extends() );
      if ( extendsStatement == null ) {
         throw new AspectLoadingException( "Model element has no type and does not extend another type: " + modelElement );
      }

      final Resource superElement = extendsStatement.getObject().asResource();
      return getModelElementType( superElement, bamm );
   }

   /**
    * @param modelElement the RDF {@link Resource} representing the Aspect Model element to be processed
    * @param attribute the RDF {@link org.apache.jena.rdf.model.Property} for which the values will be retrieved
    * @param valueRetriever the {@link AttributeValueRetriever} used to retrieve the attribute values
    * @return a {@link List} containing all values for the given Property in the given Aspect Model element
    */
   private static Set<LangString> getLanguages( final Resource modelElement,
         final org.apache.jena.rdf.model.Property attribute, final AttributeValueRetriever valueRetriever ) {
      return valueRetriever.attributeValues( modelElement, attribute ).stream()
            .filter( languageStatement -> !"und".equals( Locale.forLanguageTag( languageStatement.getLanguage() ).toLanguageTag() ) )
            .map( statement -> new LangString( statement.getString(), Locale.forLanguageTag( statement.getLanguage() ) ) )
            .collect( Collectors.toSet() );
   }

   /**
    * @param resource the RDF {@link Resource} representing the Aspect Model element to be processed
    * @param bamm the Aspect Meta Model vocabulary
    * @param valueRetriever the {@link AttributeValueRetriever} used to retrieve the attribute values
    * @return a {@link List} containing all {@link BAMM#see()} values for a Aspect Model element
    */
   private static List<String> getSeeValues( final Resource resource, final BAMM bamm, final AttributeValueRetriever valueRetriever ) {
      return valueRetriever.attributeValues( resource, bamm.see() ).stream()
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
