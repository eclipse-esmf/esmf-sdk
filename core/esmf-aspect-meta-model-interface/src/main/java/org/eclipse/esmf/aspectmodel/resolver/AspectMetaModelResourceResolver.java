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

import java.util.Set;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.aspectmodel.MissingMetaModelVersionException;
import org.eclipse.esmf.aspectmodel.MultipleMetaModelVersionsException;
import org.eclipse.esmf.aspectmodel.UnsupportedVersionException;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import io.vavr.control.Try;

/**
 * Provides functionality to resolve Aspect Meta Model resources which reside in the classpath.
 */
public interface AspectMetaModelResourceResolver {

   /**
    * Returns the {@link VersionedModel} for a loaded raw Aspect model that includes the given <i>rawModel</i>and
    * the <i>model</i> which is the rawModel merged with the corresponding meta model
    *
    * @param rawModel The given raw Aspect model
    * @param version The meta model version the model corresponds to
    * @return the VersionedModel containing the model, meta model version and raw model
    */
   Try<VersionedModel> mergeMetaModelIntoRawModel( final Model rawModel, final VersionNumber version );

   default Try<VersionedModel> mergeMetaModelIntoRawModel( final Model rawModel, final KnownVersion version ) {
      return mergeMetaModelIntoRawModel( rawModel, VersionNumber.parse( version.toVersionString() ) );
   }

   /**
    * Retrieves the meta model version an Aspect model uses
    *
    * @param model The RDF model containing an Aspect Model
    * @return A {@link Try.Success} with the used meta model version, or a {@link Try.Failure} with
    *       one of {@link MissingMetaModelVersionException}, {@link MultipleMetaModelVersionsException}
    *       or {@link UnsupportedVersionException} (if the version can not be parsed).
    */
   default Try<VersionNumber> getBammVersion( final Model model ) {
      final Set<VersionNumber> metaModelVersionsUsedInModel = getUsedMetaModelVersions( model );

      if ( metaModelVersionsUsedInModel.isEmpty() ) {
         return Try.failure( new MissingMetaModelVersionException() );
      }

      if ( metaModelVersionsUsedInModel.size() > 1 ) {
         return Try.failure( new MultipleMetaModelVersionsException() );
      }
      return Try.success( metaModelVersionsUsedInModel.iterator().next() );
   }

   /**
    * Provides the Aspect statements based on the specific BAMM Meta Model version
    *
    * @param sourceModel the source model
    * @param target the target model
    * @return stream of statements
    */
   Stream<Statement> listAspectStatements( Model sourceModel, Model target );

   /**
    * Retrieves the set of meta model versions used in a model
    *
    * @param model the model
    * @return the set of meta model versions
    */
   Set<VersionNumber> getUsedMetaModelVersions( final Model model );
}
