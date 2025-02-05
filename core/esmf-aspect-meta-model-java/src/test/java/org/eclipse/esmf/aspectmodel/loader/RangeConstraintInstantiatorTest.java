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
import static org.eclipse.esmf.metamodel.builder.SammBuilder.*;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.api.Test;

public class RangeConstraintInstantiatorTest extends AbstractAspectModelInstantiatorTest {
   @Test
   public void testRangeConstraintInstantiationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT );
      assertRangeConstraintWithMinAndMaxValue( aspect, BoundDefinition.AT_LEAST, BoundDefinition.AT_MOST );
   }

   @Test
   public void testRangeConstraintInstantiationInclBoundDefinitionPropertiesExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_INCL_BOUND_DEFINITION_PROPERTIES );
      assertRangeConstraintWithMinAndMaxValue( aspect, BoundDefinition.GREATER_THAN, BoundDefinition.LESS_THAN );
   }

   private void assertRangeConstraintWithMinAndMaxValue( final Aspect aspect,
         final BoundDefinition boundDefinitionForLowerBound, final BoundDefinition boundDefinitionForUpperBound ) {
      assertThat( aspect ).properties().hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );
      assertThat( rangeConstraint )
            .hasMaxValue( value( 10.5f ) )
            .hasSomeMinValue()
            .hasLowerBound( boundDefinitionForLowerBound )
            .hasUpperBound( boundDefinitionForUpperBound );
   }

   @Test
   public void testRangeConstraintInstantiationWithOnlyLowerBoundInclBoundDefinitionExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND_INCL_BOUND_DEFINITION );

      assertThat( aspect ).properties().hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint )
            .hasNoMaxValue()
            .hasSomeMinValue()
            .hasMinValue( value( 2.3f ) )
            .hasLowerBound( BoundDefinition.GREATER_THAN )
            .hasUpperBound( BoundDefinition.OPEN );
   }

   @Test
   public void testRangeConstraintInstantiationWithOnlyUpperBoundInclBoundDefinitionExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND_INCL_BOUND_DEFINITION );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint )
            .hasSomeMaxValue()
            .hasNoMinValue()
            .hasMaxValue( value( 2.3f ) )
            .hasLowerBound( BoundDefinition.OPEN )
            .hasUpperBound( BoundDefinition.LESS_THAN );
   }

   @Test
   public void testRangeConstraintInstantiationWithOnlyLowerBoundDefinitionExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint )
            .hasNoMaxValue()
            .hasMinValue( value( 2.3f ) )
            .hasLowerBound( BoundDefinition.AT_LEAST )
            .hasUpperBound( BoundDefinition.OPEN );
   }

   @Test
   public void testRangeConstraintInstantiationWithOnlyUpperBoundExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Trait trait = (Trait) aspect.getProperties().get( 0 ).getCharacteristic().get();
      final RangeConstraint rangeConstraint = (RangeConstraint) trait.getConstraints().get( 0 );

      assertBaseAttributes( rangeConstraint );

      assertThat( rangeConstraint )
            .hasMaxValue( value( 2.3f ) )
            .hasNoMinValue()
            .hasLowerBound( BoundDefinition.OPEN )
            .hasUpperBound( BoundDefinition.AT_MOST );
   }

   private void assertBaseAttributes( final ModelElement base ) {
      assertBaseAttributes( base,
            "Test Range Constraint",
            "This is a test range constraint.",
            "http://example.com/" );
   }
}
