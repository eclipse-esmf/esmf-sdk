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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.UnsupportedVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidModelException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidNamespaceException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidRootElementCountException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.ModelElement;
import io.openmanufacturing.sds.metamodel.ModelNamespace;
import io.openmanufacturing.sds.metamodel.NamedElement;
import io.openmanufacturing.sds.metamodel.impl.DefaultModelNamespace;
import io.vavr.control.Try;

/**
 * Provides functionality to load an Aspect Model from a {@link VersionedModel} and use the correct BAMM resources to
 * instantiate it. To load a regular Aspect Model, use {@link #getElements(VersionedModel)} or {@link #getElementsUnchecked(VersionedModel)}.
 * To load elements from an RDF model that might contain elements from multiple namespaces, use {@link #getNamespaces(VersionedModel)}.
 *
 * Instances of {@code VersionedModel} are gained through an {@link AspectModelResolver}.
 */
public class AspectModelLoader {
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelLoader.class );

   private static final Set<KnownVersion> supportedVersions = ImmutableSet.of(
         KnownVersion.BAMM_1_0_0,
         KnownVersion.BAMM_2_0_0
   );

   private static final MigratorService migratorService = new MigratorService();

   private AspectModelLoader() {
   }

   /**
    * Creates an {@link Aspect} instance from a Turtle input model.
    *
    * @param versionedModel The RDF model representation of the Aspect model
    * @return A {@link Try} containing the {@link Aspect} on success and an {@link InvalidRootElementCountException}
    *       (when the Aspect model does not contain exactly one Aspect) or {@link InvalidVersionException}
    *       (when the meta model version is not supported by the Aspect loader) on failure
    * @deprecated Use {@link #getSingleAspect(VersionedModel)} instead to retain the same semantics, but better use {@link #getElements(VersionedModel)}
    * instead to also properly handle models that contain not exactly one Aspect
    *
    * @see AspectModelResolver
    */
   @Deprecated
   public static Try<Aspect> fromVersionedModel( final VersionedModel versionedModel ) {
      return getSingleAspect( versionedModel );
   }

   private static void validateNamespaceOfCustomUnits( final BAMM bamm, final Model rawModel ) {
      final List<String> customUnitsWithBammNamespace = new ArrayList<>();
      rawModel.listStatements( null, RDF.type, bamm.Unit() )
            .mapWith( Statement::getSubject )
            .filterKeep( subject -> subject.getNameSpace().equals( bamm.getNamespace() ) )
            .mapWith( Resource::getLocalName )
            .forEach( customUnitsWithBammNamespace::add );

      if ( !customUnitsWithBammNamespace.isEmpty() ) {
         throw new InvalidNamespaceException(
               String.format( "Aspect model contains unit(s) %s not specified in the unit catalog but referred with bamm namespace",
                     customUnitsWithBammNamespace ) );
      }
   }

   /**
    * Creates an {@link Aspect} instance from a Turtle input model.
    *
    * @param versionedModel The RDF model representation of the Aspect model
    * @return The Aspect *
    * @deprecated use {@link #getSingleAspectUnchecked(VersionedModel)} instead to retain the same semantics, but better use
    * {@link #getElementsUnchecked(VersionedModel)} instead to also properly handle models that contain not exactly one Aspect
    * @see #fromVersionedModel(VersionedModel)
    */
   @Deprecated
   public static Aspect fromVersionedModelUnchecked( final VersionedModel versionedModel ) {
      return fromVersionedModel( versionedModel ).getOrElseThrow( cause -> {
         LOG.error( "Could not load Aspect", cause );
         throw new AspectLoadingException( cause );
      } );
   }

   /**
    * Loads elements from an RDF model that possibly contains multiple namespaces, and organize the result into a
    * collection of {@link ModelNamespace}. Use this method only when you expect the RDF model to contain more than
    * one namespace (which is not the case when aspect models contain the usual element definitions with one namespace per file),
    * otherwise use {@link #getElements(VersionedModel)}.
    * @param versionedModel The RDF model representation of the Aspect model
    * @return the list of namespaces
    */
   public static Try<List<ModelNamespace>> getNamespaces( final VersionedModel versionedModel ) {
      return getElements( versionedModel ).map( elements ->
            elements.stream()
                  .filter( element -> element.is( NamedElement.class ) && element.as( NamedElement.class ).getAspectModelUrn().isPresent() )
                  .collect( Collectors.groupingBy( namedElement -> {
                     final String urn = namedElement.as( NamedElement.class ).getAspectModelUrn().orElseThrow().toString();
                     return urn.substring( 0, urn.indexOf( "#" ) );
                  } ) )
                  .entrySet()
                  .stream()
                  .map( entry -> DefaultModelNamespace.from( entry.getKey(), entry.getValue() ) )
                  .toList()
      );
   }

   /**
    * Creates Java instances for model element classes from the RDF input model
    * @param versionedModel The RDF model representation of the Aspect model
    * @return the list of loaded model elements on success
    */
   public static Try<List<ModelElement>> getElements( final VersionedModel versionedModel ) {
      final Optional<KnownVersion> metaModelVersion = KnownVersion.fromVersionString( versionedModel.getMetaModelVersion().toString() );
      if ( metaModelVersion.isEmpty() || !supportedVersions.contains( metaModelVersion.get() ) ) {
         return Try.failure( new UnsupportedVersionException( versionedModel.getMetaModelVersion() ) );
      }

      final Try<VersionedModel> updatedModel = metaModelVersion.get().isOlderThan( KnownVersion.getLatest() ) ?
            migratorService.updateMetaModelVersion( versionedModel ) : Try.success( versionedModel );
      if ( updatedModel.isFailure() ) {
         return Try.failure( updatedModel.getCause() );
      }

      final BAMM bamm = new BAMM( metaModelVersion.get() );

      try {
         validateNamespaceOfCustomUnits( bamm, versionedModel.getRawModel() );
      } catch ( final InvalidNamespaceException exception ) {
         return Try.failure( exception );
      }

      try {
         final VersionedModel model = updatedModel.get();
         final ModelElementFactory modelElementFactory = new ModelElementFactory( metaModelVersion.get(), model.getModel(), Map.of() );
         // List element definitions (... rdf:type ...) from the raw model (i.e. the actual aspect model to load)
         // but then load them from the resolved model, because it contains all necessary context (e.g. unit definitions)
         return Try.success( model.getRawModel().listStatements( null, RDF.type, (RDFNode) null ).toList().stream()
               .map( Statement::getSubject )
               .filter( RDFNode::isURIResource )
               .map( resource -> model.getModel().createResource( resource.getURI() ) )
               .map( resource -> modelElementFactory.create( ModelElement.class, resource ) )
               .toList() );
      } catch ( final RuntimeException exception ) {
         return Try.failure( new InvalidModelException( "Could not load Aspect model, please make sure the model is valid", exception ) );
      }
   }

   /**
    * Does the same as {@link #getElements(VersionedModel)} but throws an exception in case of failures.
    * @param versionedModel The RDF model representation of the Aspect model
    * @throws AspectLoadingException when elements can not be loaded
    * @return the list of model elements
    */
   public static List<ModelElement> getElementsUnchecked( final VersionedModel versionedModel ) {
      return getElements( versionedModel ).getOrElseThrow( cause -> {
         LOG.error( "Could not load elements", cause );
         throw new AspectLoadingException( cause );
      } );
   }

   /**
    * Convenience method that does the same as {@link #getElements(VersionedModel)} except it will return only the aspects contained in the model.
    * @param versionedModel The RDF model representation of the Aspect model
    * @return the list of model aspects
    */
   public static Try<List<Aspect>> getAspects( final VersionedModel versionedModel ) {
      return getElements( versionedModel ).map( elements ->
            elements.stream().filter( element -> element.is( Aspect.class ) )
                  .map( element -> element.as( Aspect.class ) )
                  .toList() );
   }

   /**
    * Does the same as {@link #getAspects(VersionedModel)} but throws an exception in case of failures.
    * @param versionedModel The RDF model representation of the Aspect model
    * @throws AspectLoadingException when elements can not be loaded
    * @return the list of model aspects
    */
   public static List<Aspect> getAspectsUnchecked( final VersionedModel versionedModel ) {
      return getAspects( versionedModel ).getOrElseThrow( cause -> {
         LOG.error( "Could not load aspects", cause );
         throw new AspectLoadingException( cause );
      } );
   }

   /**
    * Convenience method to load the single Aspect from a model, when the model contains exactly one Aspect.
    *
    * <b>Caution:</b> The method handles this special case. Aspect Models are allowed to contain any number of Aspects (including zero),
    * so for the general case you should use {@link #getElements(VersionedModel)} instead.
    *
    * @param versionedModel The RDF model representation of the Aspect model
    * @return the single Aspect contained in the model
    */
   public static Try<Aspect> getSingleAspect( final VersionedModel versionedModel ) {
      return getAspects( versionedModel ).flatMap( aspects -> {
         if ( aspects.size() != 1 ) {
            return Try.failure( new InvalidRootElementCountException( "Aspect model does not contain exactly one Aspect" ) );
         }
         return Try.success( aspects.get( 0 ) );
      } );
   }

   /**
    * Convenience method to load the single Aspect from a model, when the model contains exactly one Aspect. Does the same as
    * {@link #getSingleAspect(VersionedModel)} but throws an exception on failure.
    *
    * <b>Caution:</b> The method handles this special case. Aspect Models are allowed to contain any number of Aspects (including zero),
    * so for the general case you should use {@link #getElementsUnchecked(VersionedModel)} instead.
    *
    * @param versionedModel The RDF model representation of the Aspect model
    * @return the single Aspect contained in the model
    */
   public static Aspect getSingleAspectUnchecked( final VersionedModel versionedModel ) {
      return getSingleAspect( versionedModel ).getOrElseThrow( cause -> {
         LOG.error( "Could not load aspect", cause );
         throw new AspectLoadingException( cause );
      } );
   }
}
