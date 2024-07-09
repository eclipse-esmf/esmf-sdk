/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.List;
import org.eclipse.esmf.metamodel.characteristic.Set;
import org.eclipse.esmf.metamodel.characteristic.SortedSet;
import org.eclipse.esmf.metamodel.characteristic.TimeSeries;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultTrait;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;

import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;

public class CollectionInstantiatorTest extends AbstractAspectModelInstantiatorTest {
   @Test
   public void testCollectionCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCollection" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Collection collection = (Collection) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( collection, expectedAspectModelUrn, "TestCollection",
            "Test Collection", "This is a test collection.", "http://example.com/" );

      final Scalar scalar = (Scalar) collection.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( collection.isAllowDuplicates() ).isTrue();
      assertThat( collection.isOrdered() ).isFalse();
      assertThat( collection.getElementCharacteristic() ).isEmpty();
   }

   @Test
   public void testCollectionCharacteristicWithEntityDataTypeExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCollection" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_COLLECTION );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Collection collection = (Collection) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( collection, expectedAspectModelUrn, "TestCollection",
            "Test Collection", "This is a test collection.", "http://example.com/" );

      assertThat( collection.getDataType().get() ).isInstanceOf( Entity.class );

      assertThat( collection.isAllowDuplicates() ).isTrue();
      assertThat( collection.isOrdered() ).isFalse();
   }

   @Test
   public void testListCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestList" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final List list = (List) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( list, expectedAspectModelUrn, "TestList",
            "Test List", "This is a test list.", "http://example.com/" );

      final Scalar scalar = (Scalar) list.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( list.isAllowDuplicates() ).isTrue();
      assertThat( list.isOrdered() ).isTrue();
   }

   @Test
   public void testSetCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestSet" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SET );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Set set = (Set) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( set, expectedAspectModelUrn, "TestSet",
            "Test Set", "This is a test set.", "http://example.com/" );

      final Scalar scalar = (Scalar) set.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( set.isAllowDuplicates() ).isFalse();
      assertThat( set.isOrdered() ).isFalse();
   }

   @Test
   public void testSortedSetCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestSortedSet" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SORTED_SET );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final SortedSet sortedSet = (SortedSet) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( sortedSet, expectedAspectModelUrn, "TestSortedSet",
            "Test Sorted Set", "This is a test sorted set.", "http://example.com/" );

      final Scalar scalar = (Scalar) sortedSet.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( sortedSet.isAllowDuplicates() ).isFalse();
      assertThat( sortedSet.isOrdered() ).isTrue();
   }

   @Test
   public void testTimeSeriesInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestTimeSeries" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_TIME_SERIES );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final TimeSeries timeSeries = (TimeSeries) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( timeSeries, expectedAspectModelUrn, "TestTimeSeries",
            "Test Time Series", "This is a test time series.", "http://example.com/" );

      assertThat( timeSeries.getDataType().get() ).isInstanceOf( Entity.class );

      assertThat( timeSeries.isAllowDuplicates() ).isFalse();
      assertThat( timeSeries.isOrdered() ).isTrue();
   }

   @Test
   public void testCollectionInstantiationWithElementCharacteristicExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCollection" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ELEMENT_CHARACTERISTIC );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Collection collection = (Collection) aspect.getProperties().get( 0 ).getCharacteristic().get();
      assertBaseAttributes( collection, expectedAspectModelUrn, "TestCollection",
            "Test Collection", "This is a test collection.", "http://example.com/" );

      final Scalar scalar = (Scalar) collection.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xstring.getURI() );

      assertThat( collection.isAllowDuplicates() ).isTrue();
      assertThat( collection.isOrdered() ).isFalse();

      assertThat( collection.getElementCharacteristic() ).isPresent();
      final Characteristic elementCharacteristic = collection.getElementCharacteristic().get();
      assertThat( elementCharacteristic ).isExactlyInstanceOf( DefaultCharacteristic.class );
   }

   @Test
   public void testCollectionInstantiationWithElementConstraintExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCollection" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ELEMENT_CONSTRAINT );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Collection collection = (Collection) aspect.getProperties().get( 0 ).getCharacteristic().get();
      assertBaseAttributes( collection, expectedAspectModelUrn, "TestCollection",
            "Test Collection", "This is a test collection.", "http://example.com/" );

      final Scalar scalar = (Scalar) collection.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xfloat.getURI() );

      assertThat( collection.isAllowDuplicates() ).isTrue();
      assertThat( collection.isOrdered() ).isFalse();

      assertThat( collection.getElementCharacteristic() ).isPresent();
      assertThat( collection.getElementCharacteristic().get() ).isExactlyInstanceOf( DefaultTrait.class );
   }
}
