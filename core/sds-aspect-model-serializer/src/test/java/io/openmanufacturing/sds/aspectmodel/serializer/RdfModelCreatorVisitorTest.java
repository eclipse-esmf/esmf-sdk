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

package io.openmanufacturing.sds.aspectmodel.serializer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.vocabulary.Namespace;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;
import io.openmanufacturing.sds.test.TestResources;
import io.vavr.control.Try;

public class RdfModelCreatorVisitorTest extends MetaModelVersions {

   @ParameterizedTest
   @EnumSource( value = TestAspect.class, names = {
      "MOVEMENT",
      "ENTITY_INSTANCE_TEST1",
      "ENTITY_INSTANCE_TEST2",
      "ENTITY_INSTANCE_TEST3",
      "ENTITY_INSTANCE_TEST4",
      "ASPECT_WITH_ABSTRACT_ENTITY",
      "ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY",
      "ASPECT_WITH_ABSTRACT_SINGLE_ENTITY"
   } )
   public void testRdfModelCreatorVisitor( final TestAspect aspect ) {
      testRdfCreation( aspect, KnownVersion.getLatest() );
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class, names = {
      "ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES",
      "ASPECT_WITH_OPTIONAL_PROPERTY_WITH_PAYLOAD_NAME",
      "ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME",
      "ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL"
   } )
   public void testRdfModelCreatorVisitorPropertyUsageAttributes( final TestAspect aspect ) {
      testRdfCreation( aspect, KnownVersion.getLatest() );
   }

   private void testRdfCreation( final TestAspect testAspect, final KnownVersion knownVersion ) {
      final VersionedModel versionedModel = TestResources.getModel( testAspect, knownVersion ).get();

      final Try<Aspect> tryAspect = AspectModelLoader.fromVersionedModel( versionedModel );
      final Aspect aspect = tryAspect.getOrElseThrow( () -> new RuntimeException( tryAspect.getCause() ) );

      final Namespace namespace = () -> aspect.getAspectModelUrn().get().getUrnPrefix();
      final RdfModelCreatorVisitor visitor = new RdfModelCreatorVisitor( aspect.getMetaModelVersion(), namespace );
      final Model serializedModel = visitor.visitAspect( aspect, null );

      final Map<String, String> prefixMap = new HashMap<>( Namespace.createPrefixMap( knownVersion ) );
      prefixMap.put( "", namespace.getNamespace() );
      serializedModel.setNsPrefixes( prefixMap );

      final Model originalModel = TestResources.getModelWithoutResolution( testAspect, knownVersion ).getRawModel();

      serializedModel.clearNsPrefixMap();
      originalModel.getNsPrefixMap().forEach( serializedModel::setNsPrefix );
      assertThat(
         Arrays.stream( TestModel.modelToString( serializedModel ).split( "\\n" ) )
            .filter( line -> !line.contains( "bamm-c:values" ) )
            .filter( line -> !line.contains( "bamm:see" ) )
            .collect( Collectors.toSet() ) )
         .containsExactlyInAnyOrderElementsOf(
            Arrays.stream( TestModel.modelToString( originalModel ).split( "\\n" ) )
               .filter( line -> !line.contains( "bamm-c:values" ) )
               .filter( line -> !line.contains( "bamm:see" ) )
               .collect( Collectors.toSet() ) );
   }
}
