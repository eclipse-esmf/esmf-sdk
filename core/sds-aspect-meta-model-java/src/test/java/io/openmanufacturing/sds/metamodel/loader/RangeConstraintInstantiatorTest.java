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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.metamodel.RangeConstraint;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;
import io.openmanufacturing.sds.test.TestAspect;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class RangeConstraintInstantiatorTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT, metaModelVersion );
      assertRangeConstraintWithMinAndMaxValue( aspect, BoundDefinition.AT_LEAST,
            BoundDefinition.AT_MOST );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintInstantiationInclBoundDefinitionPropertiesExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_INCL_BOUND_DEFINITION_PROPERTIES,
            metaModelVersion );
      assertRangeConstraintWithMinAndMaxValue( aspect, BoundDefinition.GREATER_THAN, BoundDefinition.LESS_THAN );
   }

   private void assertRangeConstraintWithMinAndMaxValue( final Aspect aspect,
         final BoundDefinition boundDefinitionForLowerBound, final BoundDefinition boundDefinitionForUpperBound ) {
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint.getMaxValue() ).isPresent();
      assertThat( rangeConstraint.getMaxValue().get() ).isEqualTo( 10.5f );
      assertThat( rangeConstraint.getMinValue() ).isPresent();
      assertThat( rangeConstraint.getLowerBoundDefinition() ).isEqualTo( boundDefinitionForLowerBound );
      assertThat( rangeConstraint.getUpperBoundDefinition() ).isEqualTo( boundDefinitionForUpperBound );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintInstantiationWithOnlyLowerBoundInclBoundDefinitionExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect(
            TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND_INCL_BOUND_DEFINITION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint.getMaxValue() ).isNotPresent();
      assertThat( rangeConstraint.getMinValue() ).isPresent();
      assertThat( rangeConstraint.getMinValue().get() ).isEqualTo( 2.3f );
      assertThat( rangeConstraint.getLowerBoundDefinition() ).isEqualTo( BoundDefinition.GREATER_THAN );
      assertThat( rangeConstraint.getUpperBoundDefinition() ).isEqualTo( BoundDefinition.OPEN );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintInstantiationWithOnlyUpperBoundInclBoundDefinitionExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect(
            TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND_INCL_BOUND_DEFINITION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint.getMaxValue() ).isPresent();
      assertThat( rangeConstraint.getMinValue() ).isNotPresent();
      assertThat( rangeConstraint.getMaxValue().get() ).isEqualTo( 2.3f );
      assertThat( rangeConstraint.getLowerBoundDefinition() ).isEqualTo( BoundDefinition.OPEN );
      assertThat( rangeConstraint.getUpperBoundDefinition() ).isEqualTo( BoundDefinition.LESS_THAN );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintInstantiationWithOnlyLowerBoundDefinitionExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND,
            metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint.getMaxValue() ).isNotPresent();
      assertThat( rangeConstraint.getMinValue() ).isPresent();
      assertThat( rangeConstraint.getMinValue().get() ).isEqualTo( 2.3f );
      assertThat( rangeConstraint.getLowerBoundDefinition() ).isEqualTo( BoundDefinition.AT_LEAST );
      assertThat( rangeConstraint.getUpperBoundDefinition() ).isEqualTo( BoundDefinition.OPEN );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintInstantiationWithOnlyUpperBoundExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND,
            metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint.getMaxValue() ).isPresent();
      assertThat( rangeConstraint.getMinValue() ).isNotPresent();
      assertThat( rangeConstraint.getMaxValue().get() ).isEqualTo( 2.3f );
      assertThat( rangeConstraint.getLowerBoundDefinition() ).isEqualTo( BoundDefinition.OPEN );
      assertThat( rangeConstraint.getUpperBoundDefinition() ).isEqualTo( BoundDefinition.AT_MOST );
   }

   private <T extends Base & IsDescribed> void assertBaseAttributes( final T base ) {
      assertBaseAttributes( base,
            "Test Range Constraint",
            "This is a test range constraint.",
            "http://example.com/omp" );
   }
}
