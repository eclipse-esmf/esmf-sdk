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

package org.eclipse.esmf.aspectmodel.serializer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;
import io.openmanufacturing.sds.test.TestResources;
import io.vavr.control.Try;

public class RdfModelCreatorVisitorTest extends MetaModelVersions {

   @ParameterizedTest
   /*
    * Exclude the test models that contain unused elements; the resulting serialized models can
    * never be identical to the original because the unused elements are ignored when loading the
    * model and can therefore not be serialized.
    * Furthermore, exclude the test model of an Aspect without bamm:properties and bamm:operations,
    * since the serialization will always write "bamm:properties ()" (i.e., add the attribute with
    * an empty list as value) for now, so here the serialization will differ from the source model
    * as well.
    */
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         "ASPECT_WITH_USED_AND_UNUSED_CHARACTERISTIC",
         "ASPECT_WITH_USED_AND_UNUSED_COLLECTION",
         "ASPECT_WITH_USED_AND_UNUSED_CONSTRAINT",
         "ASPECT_WITH_USED_AND_UNUSED_EITHER",
         "ASPECT_WITH_USED_AND_UNUSED_ENUMERATION",
         "ASPECT_WITHOUT_PROPERTIES_AND_OPERATIONS",
         "ASPECT_WITH_ENUM_ONLY_ONE_SEE",
         "ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY",
         "ASPECT_WITH_ABSTRACT_SINGLE_ENTITY",
         "ASPECT_WITH_ABSTRACT_PROPERTY",
         "MODEL_WITH_CYCLES",
         "MODEL_WITH_BROKEN_CYCLES",
         "MODEL_WITH_BLANK_AND_ADDITIONAL_NODES"
   } )
   public void testRdfModelCreatorVisitor( final TestAspect aspect ) {
      testRdfCreation( aspect, KnownVersion.getLatest() );
   }

   private void testRdfCreation( final TestAspect testAspect, final KnownVersion knownVersion ) {
      final VersionedModel versionedModel = TestResources.getModel( testAspect, knownVersion ).get();

      final Try<Aspect> tryAspect = AspectModelLoader.getSingleAspect( versionedModel );
      final Aspect aspect = tryAspect.getOrElseThrow( () -> new RuntimeException( tryAspect.getCause() ) );

      final Namespace namespace = () -> aspect.getAspectModelUrn().get().getUrnPrefix();
      final RdfModelCreatorVisitor visitor = new RdfModelCreatorVisitor( aspect.getMetaModelVersion(), namespace );
      final Model serializedModel = visitor.visitAspect( aspect, null ).getModel();

      final Map<String, String> prefixMap = new HashMap<>( Namespace.createPrefixMap( knownVersion ) );
      prefixMap.put( "", namespace.getNamespace() );
      serializedModel.setNsPrefixes( prefixMap );

      final Model originalModel = TestResources.getModelWithoutResolution( testAspect, knownVersion ).getRawModel();

      serializedModel.clearNsPrefixMap();
      originalModel.getNsPrefixMap().forEach( serializedModel::setNsPrefix );

      final String serializedModelString = modelToString( serializedModel );
      final String originalModelString = modelToString( originalModel );
      if ( !serializedModelString.replaceAll( "\\s+", "" ).equals( originalModelString.replaceAll( "\\s+", "" ) ) ) {
         System.out.println( "Original:" );
         originalModel.write( System.out, "TTL" );
         System.out.println( "Serialized:" );
         serializedModel.write( System.out, "TTL" );
      }
      assertThat( serializedModelString ).isEqualToIgnoringWhitespace( originalModelString );
   }

   private String modelToString( final Model model ) {
      return Arrays.stream( TestModel.modelToString( model ).split( "\\n" ) )
            .filter( line -> !line.contains( "bamm-c:values" ) )
            .filter( line -> !line.contains( "bamm:see" ) )
            .map( this::sortLineWithRdfListOrLangString )
            .sorted()
            .collect( Collectors.joining() )
            .replaceAll( "  *", " " );
   }

   /**
    * In some test models, lines with RDF lists appear, e.g.:
    *   :property ( "foo" "bar" )
    * However, for some serialized models, the order of elements is non-deterministic since the underlying collection is a Set.
    * In order to check that the line is present in the two models, we simply tokenize and sort both lines, so we can compare them.
    */
   private String sortLineWithRdfListOrLangString( final String line ) {
      if ( line.contains( " ( " ) || line.contains( "@" ) ) {
         return Arrays.stream( line.split( " " ) ).sorted().collect( Collectors.joining() );
      }
      return line;
   }
}
