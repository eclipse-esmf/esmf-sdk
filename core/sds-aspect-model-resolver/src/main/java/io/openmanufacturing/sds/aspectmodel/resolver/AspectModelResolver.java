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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.urn.UrnSyntaxException;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorFactory;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorServiceLoader;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Value;
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
   public static Set<AspectModelUrn> getAllUrnsInModel( final Model model ) {
      return Streams.stream( model.listStatements().mapWith( statement -> {
         final Stream<String> subjectUri = statement.getSubject().isURIResource() ?
               Stream.of( statement.getSubject().getURI() ) : Stream.empty();
         final Stream<String> propertyUri = Stream.of( statement.getPredicate().getURI() );
         final Stream<String> objectUri = statement.getObject().isURIResource() ?
               Stream.of( statement.getObject().asResource().getURI() ) : Stream.empty();

         return Stream.of( subjectUri, propertyUri, objectUri )
                      .flatMap( Function.identity() )
                      .map( AspectModelResolver::getAspectModelUrn )
                      .flatMap( Value::toJavaStream );
      } ) ).flatMap( Function.identity() ).collect( Collectors.toSet() );
   }

   /**
    * Parses an Aspect (meta) model URN into an {@link AspectModelUrn}
    *
    * @param uri The Aspect (meta) model URN
    * @return The {@link AspectModelUrn} if parsing succeeds, an {@link UrnSyntaxException} otherwise
    */
   private static Try<AspectModelUrn> getAspectModelUrn( final String uri ) {
      try {
         return Try.success( AspectModelUrn.fromUrn( uri ) );
      } catch ( final UrnSyntaxException exception ) {
         return Try.failure( exception );
      }
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

      final Tuple2<Try<Model>, Set<AspectModelUrn>> resolutionResult =
            resolve( ModelFactory.createDefaultModel(), input, resolver, Set.of() );
      final Try<Model> mergedModel = resolutionResult._1();

      if ( mergedModel.isFailure() ) {
         if ( mergedModel.getCause() instanceof FileNotFoundException ) {
            return Try.failure( new ModelResolutionException( mergedModel.getCause() ) );
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
                                 .orElse( migratorService.getMigratorFactory()
                                                         .map( MigratorFactory::createAspectMetaModelResourceResolver )
                                                         .map( Try::success )
                                                         .orElseThrow()
                                                         .flatMap( metaResolver -> metaResolver
                                                               .mergeMetaModelIntoRawModel( model,
                                                                     oldest ) ) )
                                 .flatMap(
                                       migratorService::updateMetaModelVersion ) ) );
   }

   /**
    * Checks if a given model contains the definition of a model element. This is determined by checking whether the
    * statement "modelElement rdf:type *" or "modelElement bamm:refines *" exists.
    *
    * @param model the model
    * @param urn the URN of the model element
    * @return true if the model contains the definition of the model element
    */
   public static boolean containsDefinition( final Model model, final AspectModelUrn urn ) {
      return Streams.stream( model.listStatements() )
                    .anyMatch( statement -> isBammElementDefinition( urn, statement ) );
   }

   private static boolean isBammElementDefinition( final AspectModelUrn urn, final Statement statement ) {
      final Resource resource = ResourceFactory.createResource( urn.getUrn().toString() );
      final String bammRefinesRegex = ".*\\bmeta-model:\\b.*\\b#refines";
      return resource.equals( statement.getSubject() ) &&
            (RDF.type.equals( statement.getPredicate() ) ||
                  statement.getPredicate().toString().matches( bammRefinesRegex ));
   }

   private Tuple2<Try<Model>, Set<AspectModelUrn>> resolve( final Model targetModel,
         final AspectModelUrn urn,
         final ResolutionStrategy resolver, final Set<AspectModelUrn> resolvedUrns ) {

      if ( resolvedUrns.contains( urn ) ) {
         return Tuple.of( Try.success( targetModel ), resolvedUrns );
      }

      final Set<AspectModelUrn> updatedResolvedUrns = Sets.union( resolvedUrns, Set.of( urn ) );
      if ( containsDefinition( targetModel, urn ) ) {
         return Tuple.of( Try.success( targetModel ), updatedResolvedUrns );
      }

      final Try<Model> loadedModel = resolver.apply( urn );
      if ( loadedModel.isFailure() ) {
         return Tuple.of( loadedModel, updatedResolvedUrns );
      }

      addModelWithoutOverwritingEmptyPrefix( targetModel, loadedModel.get() );
      return getAllUrnsInModel( loadedModel.get() )
            .stream()
            .filter( modelUrn -> !modelUrn.isBammUrn() )
            .reduce( Tuple.of( Try.success( targetModel ), updatedResolvedUrns ),
                  (( tuple, aspectModelUrn ) ->
                        resolve( targetModel, aspectModelUrn, resolver, updatedResolvedUrns )),
                  ( tuple, tuple2 ) -> {
                     final Try<Model> mergedModels = tuple._1().flatMap( model ->
                           tuple2._1().map( model2 -> addModelWithoutOverwritingEmptyPrefix( model, model2 ) ) );
                     final Set<AspectModelUrn> mergedSets = Sets.union( tuple._2(), tuple2._2() );
                     return Tuple.of( mergedModels, mergedSets );
                  } );
   }

   private Model addModelWithoutOverwritingEmptyPrefix( final Model target, final Model modelToAdd ) {
      if ( !target.getNsPrefixMap().containsKey( "" ) && modelToAdd.getNsPrefixMap().containsKey( "" ) ) {
         target.setNsPrefix( "", modelToAdd.getNsPrefixURI( "" ) );
      }

      modelToAdd.getNsPrefixMap().entrySet().stream().filter( entry -> !"".equals( entry.getKey() ) )
                .forEach( entry -> target.setNsPrefix( entry.getKey(), entry.getValue() ) );

      migratorService.getMigratorFactory().map( MigratorFactory::createAspectMetaModelResourceResolver ).stream()
                     .flatMap(
                           aspectMetaModelResourceResolver -> aspectMetaModelResourceResolver
                                 .listAspectStatements( modelToAdd, target ) ).forEach( target::add );

      migratorService.getSdsMigratorFactory().createAspectMetaModelResourceResolver()
                     .listAspectStatements( modelToAdd, target ).forEach( target::add );
      return target;
   }
}
