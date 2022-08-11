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

package io.openmanufacturing.sds.aspectmodel.resolver;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import com.google.common.collect.Streams;

import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.urn.ElementType;
import io.openmanufacturing.sds.aspectmodel.urn.UrnSyntaxException;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorFactory;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorServiceLoader;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Provides facilities for loading an Aspect model and resolving referenced meta model elements and
 * model elements from other Aspect models
 */
public class AspectModelResolver {

   private final MigratorService migratorService = MigratorServiceLoader.getInstance().getMigratorService();

   /**
    * Returns all valid model URNs for Aspects and model elements in a model
    *
    * @param model The RDF model
    * @return The set of URNs
    */
   public static Set<String> getAllUrnsInModel( final Model model ) {
      return Streams.stream( model.listStatements().mapWith( statement -> {
         final Stream<String> subjectUri = statement.getSubject().isURIResource() ?
               Stream.of( statement.getSubject().getURI() ) : Stream.empty();
         final Stream<String> propertyUri = Stream.of( statement.getPredicate().getURI() );
         final Stream<String> objectUri = statement.getObject().isURIResource() ?
               Stream.of( statement.getObject().asResource().getURI() ) : Stream.empty();

         return Stream.of( subjectUri, propertyUri, objectUri )
               .flatMap( Function.identity() );
      } ) ).flatMap( Function.identity() ).collect( Collectors.toSet() );
   }

   /**
    * Method to resolve a given {@link AspectModelUrn} using a suitable {@link ResolutionStrategy}.
    * This creates the closure (merged model) of all referenced models and the corresponding meta model.
    *
    * @param resolver The strategy to resolve input URNs to RDF models
    * @param input The input to be resolved by the strategy
    * @return The resolved model on success
    */
   public Try<VersionedModel> resolveAspectModel( final ResolutionStrategy resolver, final AspectModelUrn input ) {
      final Try<Model> mergedModel = resolve( input.toString(), resolver );

      if ( mergedModel.isFailure() ) {
         if ( mergedModel.getCause() instanceof FileNotFoundException ) {
            return Try.failure( new ModelResolutionException( "While trying to resolve " + input + ": " + mergedModel.getCause() ) );
         }
         return Try.failure( mergedModel.getCause() );
      }

      final AspectMetaModelResourceResolver resourceResolver =
            migratorService.getSdsMigratorFactory().createAspectMetaModelResourceResolver();

      final Set<VersionNumber> usedMetaModelVersions =
            mergedModel.map( resourceResolver::getUsedMetaModelVersions )
                  .getOrElse( Collections.emptySet() );

      if ( usedMetaModelVersions.isEmpty() ) {
         return Try.failure( new ModelResolutionException( "Could not determine used meta model version" ) );
      }

      if ( usedMetaModelVersions.size() == 1 && migratorService.getMigratorFactory().isEmpty() ) {
         return mergedModel.flatMap( model ->
               migratorService.getSdsMigratorFactory().createAspectMetaModelResourceResolver()
                     .mergeMetaModelIntoRawModel( model, usedMetaModelVersions.iterator().next() ) );
      }

      final Try<VersionNumber> oldestVersion =
            Option.ofOptional( usedMetaModelVersions.stream().sorted().findFirst() ).toTry();

      return mergedModel.flatMap( model ->
            oldestVersion.flatMap( oldest ->
                  migratorService.getSdsMigratorFactory()
                        .createAspectMetaModelResourceResolver()
                        .mergeMetaModelIntoRawModel( model, oldest )
                        .orElse( () -> migratorService.getMigratorFactory()
                              .map( MigratorFactory::createAspectMetaModelResourceResolver )
                              .map( Try::success )
                              .orElseThrow()
                              .flatMap( metaResolver -> metaResolver.mergeMetaModelIntoRawModel( model, oldest ) ) )
                        .flatMap( migratorService::updateMetaModelVersion ) ) );
   }

   /**
    * Checks if a given model contains the definition of a model element.
    *
    * @param model the model
    * @param urn the URN of the model element
    * @return true if the model contains the definition of the model element
    */
   public static boolean containsDefinition( final Model model, final AspectModelUrn urn ) {
      return model.contains( model.createResource( urn.toString() ), RDF.type, (RDFNode) null );
   }

   /**
    * The main model resolution method that takes an Aspect Model element URN and a resolution strategy as input.
    * The strategy is applied to the URN to load a model, and then repeated for all URNs in the loaded model that
    * have not yet been loaded.
    * @param urn the Aspect Model element URN
    * @param resolutionStrategy the resolution strategy that knowns how to turn a URN into a Model
    * @return the fully resolved model, or a failure if one of the transitively referenced elements can't be found
    */
   private Try<Model> resolve( final String urn, final ResolutionStrategy resolutionStrategy ) {
      final Model result = ModelFactory.createDefaultModel();
      final Stack<String> unresolvedUrns = new Stack<>();
      final Set<Model> mergedModels = new HashSet<>();
      unresolvedUrns.push( urn );

      while ( !unresolvedUrns.isEmpty() ) {
         final String urnToResolve = unresolvedUrns.pop();
         final Try<Model> resolvedModel = getModelForUrn( urnToResolve, resolutionStrategy );
         if ( resolvedModel.isFailure() ) {
            return resolvedModel;
         }
         final Model model = resolvedModel.get();

         // Merge the resolved model into the target if it was not already merged before.
         // It could have been merged before when the model contains another model definition that was already resolved
         if ( !mergedModels.contains( model ) ) {
            mergeModels( result, model );
            mergedModels.add( model );
         }
         for ( final String element : getAllUrnsInModel( model ) ) {
            if ( !result.contains( model.createResource( element ), RDF.type, (RDFNode) null )
                  // Backwards compatibility with BAMM 1.0.0
                  && !result.contains( model.createResource( element ), model.createProperty( "urn:bamm:io.openmanufacturing:meta-model:1.0.0#refines" ),
                  (RDFNode) null )
                  && !unresolvedUrns.contains( element ) ) {
               unresolvedUrns.push( element );
            }
         }
      }

      return Try.success( result );
   }

   private final Model EMPTY_MODEL = ModelFactory.createDefaultModel();

   /**
    * Applies a {@link ResolutionStrategy} to a URI to be resolved, but only if the URI is actually a valid {@link AspectModelUrn}.
    * For meta model elements or other URIs, an empty model is returned. This method returns only a failure, when the used resolution
    * strategy fails.
    * @param urn the URN to resolve
    * @param resolutionStrategy the resolution strategy to apply
    * @return the model containing the defintion of the given model element
    */
   private Try<Model> getModelForUrn( final String urn, final ResolutionStrategy resolutionStrategy ) {
      if ( urn.startsWith( RDF.getURI() ) || urn.startsWith( XSD.getURI() ) ) {
         return Try.success( EMPTY_MODEL );
      }

      try {
         final AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn( urn );
         if ( aspectModelUrn.getElementType() != ElementType.NONE ) {
            return Try.success( EMPTY_MODEL );
         }
         return resolutionStrategy.apply( aspectModelUrn ).flatMap( model -> {
            if ( !model.contains( model.createResource( urn ), RDF.type, (RDFNode) null ) ) {
               return Try.failure( new ModelResolutionException( "Resolution strategy returned a model which does contain element definition for " + urn ) );
            }
            return Try.success( model );
         } );
      } catch ( final UrnSyntaxException e ) {
         // If it's no valid Aspect Model URN but some other URI (e.g., a bamm:see value), there is nothing
         // to resolve, so we return just an empty model
         return Try.success( EMPTY_MODEL );
      }
   }

   /**
    * Defensively merge a model into an existing target model. This means:
    * <ul>
    *    <li>Prefixes are only added when they are not already present, i.e., a model won't overwrite the empty prefix of the target model</li>
    *    <li>Statements that have a blank node (including RDF lists) as their object won't be added</li>
    * </ul>
    * @param target the model to merge into
    * @param other the model to be merged
    */
   private void mergeModels( final Model target, final Model other ) {
      for ( final Map.Entry<String, String> prefixEntry : other.getNsPrefixMap().entrySet() ) {
         if ( !target.getNsPrefixMap().containsKey( prefixEntry.getKey() ) ) {
            target.setNsPrefix( prefixEntry.getKey(), prefixEntry.getValue() );
         }
      }

      for ( final StmtIterator it = other.listStatements(); it.hasNext(); ) {
         final Statement statement = it.next();

         if ( target.contains( statement.getSubject(), statement.getPredicate(), (RDFNode) null ) ) {
            // If the value is a language string, add the additional assertion
            if ( statement.getObject().isLiteral() && !statement.getLiteral().getLanguage().isEmpty() ) {
               target.add( statement );
            }
            // If the value is a named resource, also add the additional assertion. This for example
            // is the case with multiple bamm:see assertions
            if ( statement.getObject().isResource() && statement.getResource().isURIResource() ) {
               target.add( statement );
            }
         } else {
            target.add( statement );
         }
      }
   }
}
