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

package org.eclipse.esmf.metamodel.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.Streams;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * Wrapper class for the attributes all Aspect Meta Model elements have.
 */
public class MetaModelBaseAttributes {
   private static final SammAspectMetaModelResourceResolver META_MODEL_RESOURCE_RESOLVER = new SammAspectMetaModelResourceResolver();
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
    * Creates a builder for the given meta model element name.
    *
    * @param name the meta model element name
    * @return the builder instance
    */
   public static Builder builderFor( final String name ) {
      return new Builder( name );
   }

   /**
    * Creates an instance from a meta model version, an URN and a name.
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
    * Creates an instance from a meta model version, an URN, a name and
    * a preferredName for {@link Locale#ENGLISH}.
    *
    * @param metaModelVersion the used meta model version
    * @param urn the meta model element URN
    * @param name the meta model element name
    * @return the newly created instance
    */
   public static MetaModelBaseAttributes from( final KnownVersion metaModelVersion, final AspectModelUrn urn, final String name,
         final String preferredName ) {
      return builderFor( name ).withMetaModelVersion( metaModelVersion ).withUrn( urn )
            .withPreferredName( Locale.ENGLISH, preferredName ).build();
   }

   /**
    * Creates an instance for a specific Meta Model element.
    *
    * @param metaModelVersion the used meta model version
    * @param modelElement the Aspect model element to be processed.
    * @param model the RDF {@link Model} representing the entire Aspect Meta Model.
    * @param samm the Aspect Meta Model vocabulary
    * @return the newly created instance
    */
   public static MetaModelBaseAttributes fromModelElement( final KnownVersion metaModelVersion,
         final Resource modelElement, final Model model, final SAMM samm ) {
      final AttributeValueRetriever valueRetriever = new AttributeValueRetriever( samm );

      final Optional<AspectModelUrn> urn = getUrn( modelElement, samm );
      final Set<LangString> preferredNames = getLanguages( modelElement, samm.preferredName(), valueRetriever );
      final Set<LangString> descriptions = getLanguages( modelElement, samm.description(), valueRetriever );
      final List<String> seeValues = getSeeValues( modelElement, samm, valueRetriever );
      final String name = getName( modelElement, samm )
            .orElseGet( () -> getSyntheticName( modelElement, model, samm ) );
      final boolean isSyntheticName = urn.isEmpty();
      return new MetaModelBaseAttributes( metaModelVersion, urn.orElse( null ), name, preferredNames, descriptions, seeValues,
            isSyntheticName );
   }

   /**
    * Creates an instance of {@link MetaModelBaseAttributes} by copying them from a given {@link NamedElement}.
    *
    * @param modelElement the named model element to copy the base attributes from
    * @return the newly created instance
    */
   public static MetaModelBaseAttributes fromModelElement( final NamedElement modelElement ) {
      return new MetaModelBaseAttributes( modelElement.getMetaModelVersion(), modelElement.getAspectModelUrn().get(),
            modelElement.getName(), modelElement.getPreferredNames(), modelElement.getDescriptions(), modelElement.getSee() );
   }

   private static Optional<AspectModelUrn> getUrn( final Resource modelElement, final SAMM samm ) {
      if ( modelElement.isAnon() ) {
         final Statement propertyStatement = modelElement.getProperty( samm.property() );
         if ( propertyStatement != null ) {
            return getUrn( propertyStatement.getObject().asResource(), samm );
         }
         return Optional.empty();
      }
      return Optional.of( AspectModelUrn.fromUrn( modelElement.getURI() ) );
   }

   /**
    * Returns a model element's name: If it's a named resource, the name is part of its URN; otherwise
    * (e.g., [ samm:extends :foo ; ... ]) go up the inheritance tree recursively.
    *
    * @param modelElement the model element to retrieve the name for
    * @param samm the meta model vocabulary
    * @return the element's local name
    */
   private static Optional<String> getName( final Resource modelElement, final SAMM samm ) {
      if ( !modelElement.isAnon() ) {
         return Optional.of( AspectModelUrn.fromUrn( modelElement.getURI() ).getName() );
      }

      final Statement propertyStatement = modelElement.getProperty( samm.property() );
      if ( propertyStatement != null ) {
         return getName( propertyStatement.getObject().asResource(), samm );
      }

      final Optional<Statement> extendsStatement = Streams.stream(
            modelElement.getModel().listStatements( modelElement, samm._extends(), (RDFNode) null ) ).findAny();
      return extendsStatement.flatMap( statement -> getName( statement.getObject().asResource(), samm ) );
   }

   private static String getSyntheticName( final Resource modelElement, final Model model, final SAMM samm ) {
      final Resource namedParent = getNamedParent( modelElement, model );
      if ( namedParent == null ) {
         throw new AspectLoadingException( "At least one anonymous node in the model does not have a parent with a regular name." );
      }
      final String parentModelElementUri = namedParent.getURI();
      final String parentModelElementName = AspectModelUrn.from( parentModelElementUri )
            .toJavaOptional()
            .map( AspectModelUrn::getName )
            .map( StringUtils::capitalize )
            .orElse( "" );

      final Resource modelElementType = getModelElementType( modelElement, samm );
      final String modelElementTypeUri = modelElementType.getURI();
      final String modelElementTypeName = AspectModelUrn.from( modelElementTypeUri )
            .toJavaOptional()
            .map( AspectModelUrn::getName )
            .orElse( "" );

      return parentModelElementName + modelElementTypeName;
   }

   // We have to be careful when searching for the parent nodes with a regular name - the "listStatements" API returns the matching nodes
   // in no particular order; with some very specific models this could lead to non-deterministic behavior.
   // In the following very simplified example we are looking for ":NumberList" as the parent of "_:blankNode", but could get the
   // anonymous node [] instead.
   // [
   //  aux:contains _:blankNode ;
   // ] .
   // :NumberList a samm-c:List ;
   //    samm-c:elementCharacteristic _:blankNode .
   // _:blankNode a samm-c:Trait ;
   private static Resource getNamedParent( final Resource modelElement, final Model model ) {
      final StmtIterator elements = model.listStatements( null, null, modelElement );
      while ( elements.hasNext() ) {
         final Resource parentModelElement = elements.next().getSubject();
         if ( parentModelElement.isAnon() ) {
            final Resource grandParent = getNamedParent( parentModelElement, model );
            if ( null != grandParent ) {
               return grandParent;
            }
         } else {
            return parentModelElement;
         }
      }
      return null; // element has no named parent
   }

   private static Resource getModelElementType( final Resource modelElement, final SAMM samm ) {
      final Statement typeStatement = modelElement.getProperty( RDF.type );
      if ( typeStatement != null ) {
         return typeStatement.getObject().asResource();
      }

      // If the model element is a Property reference, the actual type will be found when we follow samm:property
      final Statement propertyStatement = modelElement.getProperty( samm.property() );
      if ( propertyStatement != null ) {
         return getModelElementType( propertyStatement.getObject().asResource(), samm );
      }

      // This model element has no type, but maybe it extends another element
      final Statement extendsStatement = modelElement.getProperty( samm._extends() );
      if ( extendsStatement == null ) {
         throw new AspectLoadingException( "Model element has no type and does not extend another type: " + modelElement );
      }

      final Resource superElement = extendsStatement.getObject().asResource();
      return getModelElementType( superElement, samm );
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
    * @param samm the Aspect Meta Model vocabulary
    * @param valueRetriever the {@link AttributeValueRetriever} used to retrieve the attribute values
    * @return a {@link List} containing all {@link SAMM#see()} values for a Aspect Model element
    */
   private static List<String> getSeeValues( final Resource resource, final SAMM samm, final AttributeValueRetriever valueRetriever ) {
      return valueRetriever.attributeValues( resource, samm.see() ).stream()
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
