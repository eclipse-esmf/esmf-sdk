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

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.List;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.Set;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.TimeSeries;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.impl.DefaultTrait;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;

public class CollectionInstantiatorTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCollection" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Collection collection = (Collection) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( collection, expectedAspectModelUrn, "TestCollection",
            "Test Collection", "This is a test collection.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) collection.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( collection.isAllowDuplicates() ).isTrue();
      assertThat( collection.isOrdered() ).isFalse();
      assertThat( collection.getElementCharacteristic() ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionCharacteristicWithEntityDataTypeExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCollection" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_COLLECTION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Collection collection = (Collection) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( collection, expectedAspectModelUrn, "TestCollection",
            "Test Collection", "This is a test collection.", "http://example.com/omp" );

      assertThat( collection.getDataType().get() ).isInstanceOf( Entity.class );

      assertThat( collection.isAllowDuplicates() ).isTrue();
      assertThat( collection.isOrdered() ).isFalse();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testListCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestList" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final List list = (List) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( list, expectedAspectModelUrn, "TestList",
            "Test List", "This is a test list.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) list.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( list.isAllowDuplicates() ).isTrue();
      assertThat( list.isOrdered() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSetCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestSet" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SET, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Set set = (Set) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( set, expectedAspectModelUrn, "TestSet",
            "Test Set", "This is a test set.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) set.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( set.isAllowDuplicates() ).isFalse();
      assertThat( set.isOrdered() ).isFalse();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSortedSetCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestSortedSet" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SORTED_SET, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final SortedSet sortedSet = (SortedSet) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( sortedSet, expectedAspectModelUrn, "TestSortedSet",
            "Test Sorted Set", "This is a test sorted set.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) sortedSet.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( sortedSet.isAllowDuplicates() ).isFalse();
      assertThat( sortedSet.isOrdered() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testTimeSeriesInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestTimeSeries" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_TIME_SERIES, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final TimeSeries timeSeries = (TimeSeries) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( timeSeries, expectedAspectModelUrn, "TestTimeSeries",
            "Test Time Series", "This is a test time series.", "http://example.com/omp" );

      assertThat( timeSeries.getDataType().get() ).isInstanceOf( Entity.class );

      assertThat( timeSeries.isAllowDuplicates() ).isFalse();
      assertThat( timeSeries.isOrdered() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionInstantiationWithElementCharacteristicExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCollection" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ELEMENT_CHARACTERISTIC, metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Collection collection = (Collection) aspect.getProperties().get( 0 ).getCharacteristic().get();
      assertBaseAttributes( collection, expectedAspectModelUrn, "TestCollection",
            "Test Collection", "This is a test collection.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) collection.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( collection.isAllowDuplicates() ).isTrue();
      assertThat( collection.isOrdered() ).isFalse();

      assertThat( collection.getElementCharacteristic() ).isPresent();
      final Characteristic elementCharacteristic = collection.getElementCharacteristic().get();
      assertThat( elementCharacteristic ).isExactlyInstanceOf( DefaultCharacteristic.class );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionInstantiationWithElementConstraintExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCollection" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ELEMENT_CONSTRAINT, metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Collection collection = (Collection) aspect.getProperties().get( 0 ).getCharacteristic().get();
      assertBaseAttributes( collection, expectedAspectModelUrn, "TestCollection",
            "Test Collection", "This is a test collection.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) collection.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xfloat.getURI() );

      assertThat( collection.isAllowDuplicates() ).isTrue();
      assertThat( collection.isOrdered() ).isFalse();

      assertThat( collection.getElementCharacteristic() ).isPresent();
      assertThat( collection.getElementCharacteristic().get() ).isExactlyInstanceOf( DefaultTrait.class );
   }
}
