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
import static org.eclipse.esmf.aspectmodel.serializer.RdfComparison.modelToString;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SimpleRdfNamespace;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class RdfModelCreatorVisitorTest {
   /*
    * Exclude the test models that contain unused elements; the resulting serialized models can
    * never be identical to the original because the unused elements are ignored when loading the
    * model and can therefore not be serialized.
    * Furthermore, exclude the test model of an Aspect without samm:properties and samm:operations,
    * since the serialization will always write "samm:properties ()" (i.e., add the attribute with
    * an empty list as value) for now, so here the serialization will differ from the source model
    * as well.
    */
   @ParameterizedTest
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
         "ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND",
         "ASPECT_WITH_UMLAUT_DESCRIPTION",
         "MODEL_WITH_BROKEN_CYCLES",
         "MODEL_WITH_BLANK_AND_ADDITIONAL_NODES",
         "ASPECT_WITH_TIME_SERIES",
         "ASPECT_WITH_NAMESPACE_DESCRIPTION"
   } )
   public void testRdfModelCreatorVisitor( final TestAspect testAspect ) {
      final KnownVersion knownVersion = KnownVersion.getLatest();
      final AspectModel aspectModel = TestResources.load( testAspect );
      final Aspect aspect = aspectModel.aspect();

      final RdfNamespace namespace = new SimpleRdfNamespace( "", aspect.urn().getUrnPrefix() );
      final RdfModelCreatorVisitor visitor = new RdfModelCreatorVisitor( namespace );
      final Model serializedModel = visitor.visitAspect( aspect, null ).model();

      final Map<String, String> prefixMap = new HashMap<>( RdfNamespace.createPrefixMap() );
      prefixMap.put( "", namespace.getNamespace() );
      serializedModel.setNsPrefixes( prefixMap );

      final Model originalModel = aspectModel.files().iterator().next().sourceModel();

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
}
