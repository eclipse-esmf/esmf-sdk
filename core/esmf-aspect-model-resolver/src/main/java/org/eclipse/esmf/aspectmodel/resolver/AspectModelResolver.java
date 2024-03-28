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

package org.eclipse.esmf.aspectmodel.resolver;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.fs.FlatModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.urn.ElementType;
import org.eclipse.esmf.aspectmodel.urn.UrnSyntaxException;
import org.eclipse.esmf.aspectmodel.versionupdate.MigratorFactory;
import org.eclipse.esmf.aspectmodel.versionupdate.MigratorService;
import org.eclipse.esmf.aspectmodel.versionupdate.MigratorServiceLoader;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.BammUriRewriter;
import org.eclipse.esmf.samm.KnownVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Streams;

import io.vavr.Value;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Provides facilities for loading an Aspect model and resolving referenced meta model elements and
 * model elements from other Aspect models
 */
public class AspectModelResolver {
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelResolver.class );
   private static final List<Pair<Predicate<URI>, Function<URI, Try<ResolutionStrategy>>>> STRATEGY_BUILDERS = new ArrayList<>();

   private final MigratorService migratorService = MigratorServiceLoader.getInstance().getMigratorService();
   private final BammUriRewriter bamm100UriRewriter = new BammUriRewriter( BammUriRewriter.BammVersion.BAMM_1_0_0 );
   private final BammUriRewriter bamm200UriRewriter = new BammUriRewriter( BammUriRewriter.BammVersion.BAMM_2_0_0 );

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
               .flatMap( Function.identity() )
               .map( AspectModelResolver::resolveSammUrn )
               .flatMap( Value::toJavaStream );
      } ) ).flatMap( Function.identity() ).collect( Collectors.toSet() );
   }

   /**
    * Tries to resolve the given SAMM URN {@link AspectModelUrn}
    *
    * @param urn The Aspect (meta) model URN
    * @return The {@link String} if it is resolvable, an {@link UrnSyntaxException} otherwise
    */
   private static Try<String> resolveSammUrn( final String urn ) {
      try {
         AspectModelUrn.fromUrn( urn );
         return Try.success( urn );
      } catch ( final UrnSyntaxException exception ) {
         return Try.failure( exception );
      }
   }

   static {
      Predicate<URI> all = __ -> true;

      // Construct the resolution strategy: Models should be searched in the structured folder (if it exists) and then in the
      // same directory. If the structured folder can not be resolved, directly search in the same directory.
      STRATEGY_BUILDERS.add( Pair.of( all, uri -> Try.success( new FileSystemStrategy( new FlatModelsRoot( uri ) ) ) ) );
      STRATEGY_BUILDERS.add( Pair.of( all, uri -> Try.of( () -> new FileSystemStrategy( new StructuredModelsRoot( uri ) ) ) ) );
   }

   /**
    * Method to resolve a given {@link AspectModelUrn} using a suitable {@link ResolutionStrategy}.
    * This creates the closure (merged model) of all referenced models and the corresponding meta model.
    *
    * @param resolutionStrategy the strategy to resolve input URNs to RDF models
    * @param input              the input to be resolved by the strategy
    * @return the resolved model on success
    */
   public Try<VersionedModel> resolveAspectModel( final ResolutionStrategy resolutionStrategy, final AspectModelUrn input ) {
      return resolveAspectModel( resolutionStrategy, List.of( input ) );
   }

   public Try<VersionedModel> resolveAspectModel( final ResolutionStrategy resolutionStrategy, final URI input ) {
      return resolutionStrategy.applyUri( input ).flatMap( model -> resolveAspectModel( resolutionStrategy, model ) );
   }

   /**
    * Method to load an Aspect Model from an input stream, and resolve it using a suitable {@link ResolutionStrategy}.
    *
    * @param resolutionStrategy the strategy to resolve input URNs to RDF models
    * @param inputStream        the inputs stream to read the RDF/Turtle representation from
    * @return the resolved model on success
    */
   public Try<VersionedModel> resolveAspectModel( final ResolutionStrategy resolutionStrategy, final InputStream inputStream ) {
      return TurtleLoader.loadTurtle( inputStream ).flatMap( model -> resolveAspectModel( resolutionStrategy, model ) );
   }

   /**
    * Method to load an Aspect Model from a string, and resolve it using a suitable {@link ResolutionStrategy}.
    *
    * @param resolutionStrategy the strategy to resolve input URNs to RDF models
    * @param modelContent       a string containing the RDF/Turtle representation
    * @return the resolved model on success
    */
   public Try<VersionedModel> resolveAspectModel( final ResolutionStrategy resolutionStrategy, final String modelContent ) {
      return resolveAspectModel( resolutionStrategy, new ByteArrayInputStream( modelContent.getBytes( StandardCharsets.UTF_8 ) ) );
   }

   /**
    * Method to resolve a given aspect model.
    *
    * @param resolutionStrategy the strategy to resolve input URNs to RDF models
    * @param model              the initial aspect model
    * @return the resolved model on success
    */
   public Try<VersionedModel> resolveAspectModel( final ResolutionStrategy resolutionStrategy, final Model model ) {
      return resolveAspectModel( model, resolutionStrategy, urnsToResolve( model, model ) );
   }

   /**
    * Method to resolve multiple {@link AspectModelUrn}s using a suitable {@link ResolutionStrategy}.
    * This creates the closure (merged model) of all referenced models and the corresponding meta model.
    *
    * @param resolutionStrategy the strategy to resolve input URNs to RDF models
    * @param input              the input to be resolved by the strategy
    * @return the resolved model on success
    */
   public Try<VersionedModel> resolveAspectModel( final ResolutionStrategy resolutionStrategy, final List<AspectModelUrn> input ) {
      return resolveAspectModel( ModelFactory.createDefaultModel(), resolutionStrategy, input );
   }

   /**
    * Method to resolve multiple {@link AspectModelUrn}s using a suitable {@link ResolutionStrategy} against an inital model.
    * This creates the closure (merged model) of all referenced models and the corresponding meta model.
    *
    * @param initialModel       the initial model
    * @param resolutionStrategy the strategy to resolve input URNs to RDF models
    * @param input              the input to resolved by the strategy
    * @return the resolved model on success
    */
   public Try<VersionedModel> resolveAspectModel( final Model initialModel, final ResolutionStrategy resolutionStrategy,
         final List<AspectModelUrn> input ) {
      final Try<Model> mergedModel = resolve( initialModel, input, resolutionStrategy )
            .map( bamm100UriRewriter::migrate )
            .map( bamm200UriRewriter::migrate );

      if ( mergedModel.isFailure() ) {
         if ( mergedModel.getCause() instanceof final FileNotFoundException fileNotFoundException ) {
            final String failedUrns = input.stream()
                  .filter( urn -> !urn.getElementType().equals( ElementType.META_MODEL ) )
                  .filter( urn -> !urn.getElementType().equals( ElementType.CHARACTERISTIC ) )
                  .filter( urn -> !urn.getElementType().equals( ElementType.ENTITY ) )
                  .filter( urn -> !urn.getElementType().equals( ElementType.UNIT ) )
                  .map( AspectModelUrn::toString )
                  .collect( Collectors.joining( ", " ) );
            LOG.debug( "Could not resolve {}", failedUrns, fileNotFoundException );
            return Try.failure( new ModelResolutionException( "Could not resolve " + failedUrns, fileNotFoundException ) );
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

      if ( usedMetaModelVersions.size() == 1
            && usedMetaModelVersions.iterator().next().toString().equals( KnownVersion.getLatest().toVersionString() )
            && migratorService.getMigratorFactory().isEmpty()
      ) {
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
    * @param urn   the URN of the model element
    * @return true if the model contains the definition of the model element
    */
   public static boolean containsDefinition( final Model model, final AspectModelUrn urn ) {
      if ( model.getNsPrefixMap().values().stream().anyMatch( prefixUri -> prefixUri.startsWith( "urn:bamm:" ) ) ) {
         final boolean result = model.contains( model.createResource( urn.toString().replace( "urn:samm:", "urn:bamm:" ) ), RDF.type,
               (RDFNode) null );
         LOG.debug( "Checking if model contains {}: {}", urn, result );
         return result;
      }
      final boolean result = model.contains( model.createResource( urn.toString() ), RDF.type, (RDFNode) null );
      LOG.debug( "Checking if model contains {}: {}", urn, result );
      return result;
   }

   /**
    * The main model resolution method that takes Aspect Model element URNs and a resolution strategy as input.
    * The strategy is applied to the URNs to load a model, and then repeated for all URNs in the loaded model that
    * have not yet been loaded.
    *
    * @param result             the (possibly pre-filled) model for which elements need to be resolved
    * @param urns               the Aspect Model element URNs
    * @param resolutionStrategy the resolution strategy that knowns how to turn a URN into a Model
    * @return the fully resolved model, or a failure if one of the transitively referenced elements can't be found
    */
   private Try<Model> resolve( final Model result, final List<AspectModelUrn> urns, final ResolutionStrategy resolutionStrategy ) {
      final Stack<String> unresolvedUrns = new Stack<>();
      final Set<Model> mergedModels = new HashSet<>();
      for ( final AspectModelUrn urn : urns ) {
         unresolvedUrns.push( urn.toString() );
      }

      while ( !unresolvedUrns.isEmpty() ) {
         final String urnToResolve = unresolvedUrns.pop();
         final Try<Model> resolvedModel = getModelForUrn( urnToResolve, resolutionStrategy );
         if ( resolvedModel.isFailure() ) {
            LOG.debug( "Tried to resolve {} using {}, but it failed", urnToResolve, resolutionStrategy );
            return resolvedModel;
         }
         final Model model = resolvedModel.get();

         // Merge the resolved model into the target if it was not already merged before.
         // It could have been merged before when the model contains another model definition that was already resolved
         if ( !modelAlreadyResolved( model, mergedModels ) ) {
            mergeModels( result, model );
            mergedModels.add( model );

            for ( final AspectModelUrn element : urnsToResolve( model, result ) ) {
               if ( !unresolvedUrns.contains( element.toString() ) ) {
                  unresolvedUrns.push( element.toString() );
               }
            }
         }
      }

      return Try.success( result );
   }

   /**
    * Returns the list of model element URIs that were found in the source model which need to be resolved as they
    * denote elements in the target model that are not yet defined there (i.e., no assertion "element a []" exists).
    *
    * @param source the source model
    * @param target the target model
    * @return the list of mode element URIs
    */
   private List<AspectModelUrn> urnsToResolve( final Model source, final Model target ) {
      final Property refines = source.createProperty( "urn:samm:org.eclipse.esmf.samm:meta-model:1.0.0#refines" );
      final List<AspectModelUrn> result = new ArrayList<>();
      for ( final String element : getAllUrnsInModel( source ) ) {
         if ( !target.contains( source.createResource( element ), RDF.type, (RDFNode) null )
               // Backwards compatibility with SAMM 1.0.0
               && !target.contains( source.createResource( element ), refines, (RDFNode) null )
         ) {
            result.add( AspectModelUrn.fromUrn( element ) );
         }
      }
      return result;
   }

   private boolean modelAlreadyResolved( final Model model, final Set<Model> resolvedModels ) {
      return resolvedModels.stream().anyMatch( model::isIsomorphicWith );
   }

   private final Model emptyModel = ModelFactory.createDefaultModel();

   /**
    * Applies a {@link ResolutionStrategy} to a URI to be resolved, but only if the URI is actually a valid {@link AspectModelUrn}.
    * For meta model elements or other URIs, an empty model is returned. This method returns only a failure, when the used resolution
    * strategy fails.
    *
    * @param urn                the URN to resolve
    * @param resolutionStrategy the resolution strategy to apply
    * @return the model containing the defintion of the given model element
    */
   private Try<Model> getModelForUrn( final String urn, final ResolutionStrategy resolutionStrategy ) {
      if ( urn.startsWith( RDF.getURI() ) || urn.startsWith( XSD.getURI() ) ) {
         return Try.success( emptyModel );
      }

      try {
         final AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn( replaceLegacyBammUrn( urn ) );
         if ( aspectModelUrn.getElementType() != ElementType.NONE ) {
            return Try.success( emptyModel );
         }
         return resolutionStrategy.apply( aspectModelUrn ).flatMap( model -> {
            if ( !containsType( model, urn ) ) {
               return Try.failure( new ModelResolutionException(
                     "Resolution strategy returned a model which does not contain element definition for " + urn ) );
            }
            return Try.success( model );
         } );
      } catch ( final UrnSyntaxException e ) {
         // If it's no valid Aspect Model URN but some other URI (e.g., a samm:see value), there is nothing
         // to resolve, so we return just an empty model
         return Try.success( emptyModel );
      }
   }

   private boolean containsType( final Model model, final String urn ) {
      if ( model.contains( model.createResource( urn ), RDF.type, (RDFNode) null ) ) {
         return true;
      } else if ( urn.startsWith( "urn:samm:" ) ) {
         // when deriving a URN from file (via "fileToUrn" method - mainly in samm-cli scenarios),
         // we assume new "samm" format, but could actually have been the old "bamm"
         return model.contains( model.createResource( toLegacyBammUrn( urn ) ), RDF.type, (RDFNode) null );
      }
      return false;
   }

   private String toLegacyBammUrn( final String urn ) {
      if ( urn.startsWith( "urn:samm:" ) ) {
         return urn.replace( "urn:samm:", "urn:bamm:" );
      }
      return urn;
   }

   /**
    * Adapter that enables the resolver to handle URNs with the legacy "urn:bamm:" prefix
    *
    * @param urn the URN to clean up
    * @return the original URN (if using valid urn:samm: scheme) or the the cleaned up URN
    */
   private String replaceLegacyBammUrn( final String urn ) {
      if ( urn.startsWith( "urn:bamm:" ) ) {
         return urn.replace( "urn:bamm:", "urn:samm:" );
      }
      return urn;
   }

   /**
    * Merge a model into an existing target model. Prefixes are only added when they are not already present, i.e.,
    * a model won't overwrite the empty prefix of the target model.
    *
    * @param target the model to merge into
    * @param other  the model to be merged
    */
   private void mergeModels( final Model target, final Model other ) {
      for ( final Map.Entry<String, String> prefixEntry : other.getNsPrefixMap().entrySet() ) {
         if ( !target.getNsPrefixMap().containsKey( prefixEntry.getKey() ) ) {
            target.setNsPrefix( prefixEntry.getKey(), prefixEntry.getValue() );
         }
      }

      other.listStatements().forEach( target::add );
   }

   /**
    * Convenience method for loading an resolving an Aspect Model from a file
    *
    * @param input the input file
    * @return the resolved model on success
    */
   public static Try<VersionedModel> loadAndResolveModel( final File input ) {
      return loadAndResolveModel( input.toURI() );
   }

   /**
    * Convenience method for loading an resolving an Aspect Model from a URI
    *
    * @param input the input URI
    * @return the resolved model on success
    */
   public static Try<VersionedModel> loadAndResolveModel( final URI input ) {
      final AspectModelResolver resolver = new AspectModelResolver();

      var strategy = new GroupStrategy( STRATEGY_BUILDERS.stream()
            .filter( p -> p.getKey().test( input ) )
            .map( p -> p.getValue().apply( input ) )
            .filter( Try::isSuccess )
            .map( Try::get )
            .toList() );

      return Try.withResources( () -> strategy.read( input )  )
            .of( stream -> resolver.resolveAspectModel( strategy, stream ) )
            .flatMap( Function.identity() );
   }

   /**
    * From an input Aspect Model URI, which is assumed to contain a model element definition with the same local name as the file,
    * determines the URN of this model element. The file is expected to reside in a valid location inside the models root
    * (see {@link FileSystemStrategy}). Note that the file is not opened or loaded and the method does not check whether an element
    * with the given URN actually exists in the file.
    *
    * @param uri the input model uri
    * @return the URN of the model element that corresponds to the file name and its location inside the models root
    */
   public static Try<AspectModelUrn> uriToUrn( final URI uri ) {
      return AspectModelUrn.from( uri )
            .recoverWith( UrnSyntaxException.class, throwable -> fileToUrn( new File( uri ) ) )
            ;
   }

   /**
    * From an input Aspect Model file, which is assumed to contain a model element definition with the same local name as the file,
    * determines the URN of this model element. The file is expected to reside in a valid location inside the models root
    * (see {@link FileSystemStrategy}). Note that the file is not opened or loaded and the method does not check whether an element
    * with the given URN actually exists in the file.
    *
    * @param inputFile the input model file
    * @return the URN of the model element that corresponds to the file name and its location inside the models root
    */
   public static Try<AspectModelUrn> fileToUrn( final File inputFile ) {
      final File versionDirectory = inputFile.getParentFile();
      if ( versionDirectory == null ) {
         throw new ModelResolutionException( "Could not determine parent directory of " + inputFile );
      }

      final String version = versionDirectory.getName();
      final File namespaceDirectory = versionDirectory.getParentFile();
      if ( namespaceDirectory == null ) {
         throw new ModelResolutionException( "Could not determine parent directory of " + versionDirectory );
      }

      final String namespace = namespaceDirectory.getName();
      final String aspectName = FilenameUtils.removeExtension( inputFile.getName() );
      final String urn = String.format( "urn:samm:%s:%s#%s", namespace, version, aspectName );
      return AspectModelUrn.from( urn ).mapFailure( Case(
            $( instanceOf( UrnSyntaxException.class ) ),
            e -> new ModelResolutionException( "The URN constructed from the input file path is invalid: " + urn, e ) )
      );
   }

   /**
    * Finds URN matched to file in the loaded model.
    *
    * @param model the loaded version model
    * @param file  the input model file
    * @return the URN of the model element that corresponds to the file name and its location inside the models root
    */
   public static AspectModelUrn urnFromModel( final VersionedModel model, final File file ) {
      final String aspectName = FilenameUtils.removeExtension( file.getName() );
      return Streams.stream( model.getRawModel().listSubjects() ).filter( s -> aspectName.equals( s.getLocalName() ) )
            .findFirst()
            .map( Resource::getURI )
            .map( AspectModelUrn::fromUrn )
            .orElseThrow();
   }

   /**
    * Similar to {@link #loadAndResolveModel(File)} except no additional files are loaded. If the input model file contains references
    * to elements from namespaces not defined in the same file, those references will not be resolved.
    *
    * @param inputFile the input model file
    * @return the loaded model file on success, including meta model definitions but not definitions of externally referenced elements
    */
   public static Try<VersionedModel> loadButNotResolveModel( final File inputFile ) {
      try ( final InputStream inputStream = new FileInputStream( inputFile ) ) {
         final SammAspectMetaModelResourceResolver metaModelResourceResolver = new SammAspectMetaModelResourceResolver();
         return TurtleLoader.loadTurtle( inputStream ).flatMap( model ->
               metaModelResourceResolver.getMetaModelVersion( model ).flatMap( metaModelVersion ->
                     metaModelResourceResolver.mergeMetaModelIntoRawModel( model, metaModelVersion ) ) );
      } catch ( final IOException exception ) {
         return Try.failure( exception );
      }
   }

   public static void register( Predicate<URI> predicate, Function<URI, Try<ResolutionStrategy>> strategy ) {
      Objects.requireNonNull( strategy );
      if ( predicate == null )
         predicate = __ -> true;

      STRATEGY_BUILDERS.add( Pair.of( predicate, strategy ) );

   }
}
