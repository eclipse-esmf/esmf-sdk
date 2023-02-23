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

package org.eclipse.esmf.metamodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.NamedElement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.eclipse.esmf.constraint.RangeConstraint;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.metamodel.impl.BoundDefinition;
import org.eclipse.esmf.test.TestAspect;

import org.eclipse.esmf.samm.KnownVersion;

public class RangeConstraintInstantiatorTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT, metaModelVersion );
      assertRangeConstraintWithMinAndMaxValue( aspect, BoundDefinition.AT_LEAST, BoundDefinition.AT_MOST );
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

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      Assertions.assertThat( rangeConstraint.getMaxValue() ).isPresent();
      Assertions.assertThat( rangeConstraint.getMaxValue().get().getValue() ).isEqualTo( 10.5f );
      Assertions.assertThat( rangeConstraint.getMinValue() ).isPresent();
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

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      Assertions.assertThat( rangeConstraint.getMaxValue() ).isNotPresent();
      Assertions.assertThat( rangeConstraint.getMinValue() ).isPresent();
      Assertions.assertThat( rangeConstraint.getMinValue().get().getValue() ).isEqualTo( 2.3f );
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

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      Assertions.assertThat( rangeConstraint.getMaxValue() ).isPresent();
      Assertions.assertThat( rangeConstraint.getMinValue() ).isNotPresent();
      Assertions.assertThat( rangeConstraint.getMaxValue().get().getValue() ).isEqualTo( 2.3f );
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

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      Assertions.assertThat( rangeConstraint.getMaxValue() ).isNotPresent();
      Assertions.assertThat( rangeConstraint.getMinValue() ).isPresent();
      Assertions.assertThat( rangeConstraint.getMinValue().get().getValue() ).isEqualTo( 2.3f );
      assertThat( rangeConstraint.getLowerBoundDefinition() ).isEqualTo( BoundDefinition.AT_LEAST );
      assertThat( rangeConstraint.getUpperBoundDefinition() ).isEqualTo( BoundDefinition.OPEN );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testRangeConstraintInstantiationWithOnlyUpperBoundExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND,
            metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      Assertions.assertThat( rangeConstraint.getMaxValue() ).isPresent();
      Assertions.assertThat( rangeConstraint.getMinValue() ).isNotPresent();
      Assertions.assertThat( rangeConstraint.getMaxValue().get().getValue() ).isEqualTo( 2.3f );
      assertThat( rangeConstraint.getLowerBoundDefinition() ).isEqualTo( BoundDefinition.OPEN );
      assertThat( rangeConstraint.getUpperBoundDefinition() ).isEqualTo( BoundDefinition.AT_MOST );
   }

   private void assertBaseAttributes( final NamedElement base ) {
      assertBaseAttributes( base,
            "Test Range Constraint",
            "This is a test range constraint.",
            "http://example.com/" );
   }
}
