/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Locale;

import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.constraint.EncodingConstraint;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.LocaleConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;

import org.assertj.core.api.AbstractBigIntegerAssert;
import org.assertj.core.api.AbstractComparableAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ObjectAssert;

/**
 * Assert for {@link Constraint}.
 *
 * @param <SELF> the self type
 * @param <ACTUAL> the element type
 */
@SuppressWarnings( { "NewClassNamingConvention", "UnusedReturnValue", "checkstyle:ClassTypeParameterName",
      "checkstyle:MethodTypeParameterName" } )
public class ConstraintAssert<SELF extends ConstraintAssert<SELF, ACTUAL>, ACTUAL extends Constraint>
      extends ModelElementAssert<SELF, ACTUAL> {
   public ConstraintAssert( final ACTUAL actual ) {
      this( actual, ConstraintAssert.class, "Constraint" );
   }

   protected ConstraintAssert( final ACTUAL actual, final Class<?> selfType, final String modelElementType ) {
      super( actual, selfType, modelElementType );
   }

   /**
    * Assert for {@link LanguageConstraint}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class LanguageConstraintAssert<SELF extends LanguageConstraintAssert<SELF, ACTUAL>, ACTUAL extends LanguageConstraint>
         extends ConstraintAssert<SELF, ACTUAL> {
      public LanguageConstraintAssert( final ACTUAL actual ) {
         super( actual, LanguageConstraintAssert.class, "LanguageConstraint" );
      }

      public SELF hasLanguageCode( final Locale languageCode ) {
         assertThat( actual.getLanguageCode() ).isEqualTo( languageCode );
         return myself;
      }

      public ObjectAssert<Locale> languageCode() {
         return assertThat( actual.getLanguageCode() );
      }
   }

   public SELF isLanguageConstraint() {
      assertThat( actual ).isInstanceOf( LanguageConstraint.class );
      return myself;
   }

   @SuppressWarnings( { "unchecked", "IncorrectFormatting" } ) // For some reason, IntelliJ code style does not use correct indentation
   //@formatter:off
   public <S extends LanguageConstraintAssert<S, A>, A extends LanguageConstraint>
         LanguageConstraintAssert<S, A> isLanguageConstraintThat() {
      //@formatter:on
      isLanguageConstraint();
      return new LanguageConstraintAssert<>( (A) actual );
   }

   /**
    * Assert for {@link LocaleConstraint}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class LocaleConstraintAssert<SELF extends LocaleConstraintAssert<SELF, ACTUAL>, ACTUAL extends LocaleConstraint>
         extends ConstraintAssert<SELF, ACTUAL> {
      public LocaleConstraintAssert( final ACTUAL actual ) {
         super( actual, LocaleConstraintAssert.class, "LocaleConstraint" );
      }

      public SELF hasLocaleCode( final Locale localeCode ) {
         assertThat( actual.getLocaleCode() ).isEqualTo( localeCode );
         return myself;
      }

      public ObjectAssert<Locale> languageCode() {
         return assertThat( actual.getLocaleCode() );
      }
   }

   public SELF isLocaleConstraint() {
      assertThat( actual ).isInstanceOf( LocaleConstraint.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends LocaleConstraintAssert<S, A>, A extends LocaleConstraint> LocaleConstraintAssert<S, A> isLocaleConstraintThat() {
      isLocaleConstraint();
      return new LocaleConstraintAssert<>( (A) actual );
   }

   /**
    * Assert for {@link RangeConstraint}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class RangeConstraintAssert<SELF extends RangeConstraintAssert<SELF, ACTUAL>, ACTUAL extends RangeConstraint>
         extends ConstraintAssert<SELF, ACTUAL> {
      public RangeConstraintAssert( final ACTUAL actual ) {
         super( actual, RangeConstraintAssert.class, "RangeConstraint" );
      }

      public SELF hasMinValue( final ScalarValue value ) {
         assertThat( actual.getMinValue() ).isPresent().contains( value );
         return myself;
      }

      public SELF hasNoMinValue() {
         assertThat( actual.getMinValue() ).isEmpty();
         return myself;
      }

      @SuppressWarnings( "unchecked" )
      public <S extends ScalarValueAssert<S, A>, A extends ScalarValue> ScalarValueAssert<S, A> minValue() {
         assertThat( actual.getMinValue() ).as( "Expected a minValue on %s", modelElementType ).isPresent();
         return new ScalarValueAssert<>( (A) actual.getMinValue().orElseThrow() );
      }

      public SELF hasMaxValue( final ScalarValue value ) {
         assertThat( actual.getMaxValue() ).isPresent().contains( value );
         return myself;
      }

      public SELF hasNoMaxValue() {
         assertThat( actual.getMaxValue() ).isEmpty();
         return myself;
      }

      @SuppressWarnings( "unchecked" )
      public <S extends ScalarValueAssert<S, A>, A extends ScalarValue> ScalarValueAssert<S, A> maxValue() {
         assertThat( actual.getMaxValue() ).as( "Expected a maxValue on %s", modelElementType ).isPresent();
         return new ScalarValueAssert<>( (A) actual.getMaxValue().orElseThrow() );
      }

      public SELF hasLowerBound( final BoundDefinition boundDefinition ) {
         assertThat( actual.getLowerBoundDefinition() ).isEqualTo( boundDefinition );
         return myself;
      }

      public AbstractComparableAssert<?, BoundDefinition> lowerBound() {
         return assertThat( actual.getLowerBoundDefinition() );
      }

      public SELF hasUpperBound( final BoundDefinition boundDefinition ) {
         assertThat( actual.getUpperBoundDefinition() ).isEqualTo( boundDefinition );
         return myself;
      }

      public AbstractComparableAssert<?, BoundDefinition> upperBound() {
         return assertThat( actual.getUpperBoundDefinition() );
      }
   }

   public SELF isRangeConstraint() {
      assertThat( actual ).isInstanceOf( RangeConstraint.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends RangeConstraintAssert<S, A>, A extends RangeConstraint> RangeConstraintAssert<S, A> isRangeConstraintThat() {
      isRangeConstraint();
      return new RangeConstraintAssert<>( (A) actual );
   }

   /**
    * Assert for {@link EncodingConstraint}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class EncodingConstraintAssert<SELF extends EncodingConstraintAssert<SELF, ACTUAL>, ACTUAL extends EncodingConstraint>
         extends ConstraintAssert<SELF, ACTUAL> {
      public EncodingConstraintAssert( final ACTUAL actual ) {
         super( actual, EncodingConstraintAssert.class, "EncodingConstraint" );
      }

      public SELF hasValue( final Charset charset ) {
         assertThat( actual.getValue() ).isEqualTo( charset );
         return myself;
      }

      public AbstractComparableAssert<?, Charset> value() {
         return assertThat( actual.getValue() );
      }
   }

   public SELF isEncodingConstraint() {
      assertThat( actual ).isInstanceOf( EncodingConstraint.class );
      return myself;
   }

   @SuppressWarnings( { "unchecked", "IncorrectFormatting" } ) // For some reason, IntelliJ code style does not use correct indentation
   //@formatter:off
   public <S extends EncodingConstraintAssert<S, A>, A extends EncodingConstraint> EncodingConstraintAssert<S, A>
         isEncodingConstraintThat() {
      //@formatter:on
      isEncodingConstraint();
      return new EncodingConstraintAssert<>( (A) actual );
   }

   /**
    * Assert for {@link LengthConstraint}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class LengthConstraintAssert<SELF extends LengthConstraintAssert<SELF, ACTUAL>, ACTUAL extends LengthConstraint>
         extends ConstraintAssert<SELF, ACTUAL> {
      public LengthConstraintAssert( final ACTUAL actual ) {
         super( actual, LengthConstraintAssert.class, "LengthConstraint" );
      }

      public SELF hasMinValue( final BigInteger minValue ) {
         assertThat( actual.getMinValue() ).isPresent().contains( minValue );
         return myself;
      }

      public AbstractBigIntegerAssert<?> minValue() {
         if ( actual.getMinValue().isEmpty() ) {
            failWithMessage( "Expected %s to have a minValue, but it didn't", modelElementType );
         }
         return assertThat( actual.getMinValue().orElseThrow() );
      }

      public SELF hasMaxValue( final BigInteger maxValue ) {
         assertThat( actual.getMaxValue() ).isPresent().contains( maxValue );
         return myself;
      }

      public AbstractBigIntegerAssert<?> maxValue() {
         if ( actual.getMinValue().isEmpty() ) {
            failWithMessage( "Expected %s to have a maxValue, but it didn't", modelElementType );
         }
         return assertThat( actual.getMinValue().orElseThrow() );
      }
   }

   public SELF isLengthConstraint() {
      assertThat( actual ).isInstanceOf( LengthConstraint.class );
      return myself;
   }

   @SuppressWarnings( "unchecked" )
   public <S extends LengthConstraintAssert<S, A>, A extends LengthConstraint> LengthConstraintAssert<S, A> isLengthConstraintThat() {
      isLengthConstraint();
      return new LengthConstraintAssert<>( (A) actual );
   }

   /**
    * Assert for {@link RegularExpressionConstraint}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class RegularExpressionConstraintAssert<SELF extends RegularExpressionConstraintAssert<SELF, ACTUAL>,
         ACTUAL extends RegularExpressionConstraint> extends ConstraintAssert<SELF, ACTUAL> {
      public RegularExpressionConstraintAssert( final ACTUAL actual ) {
         super( actual, RegularExpressionConstraintAssert.class, "RegularExpressionConstraint" );
      }

      public SELF hasRegularExpression( final String regex ) {
         assertThat( actual.getValue() ).isEqualTo( regex );
         return myself;
      }

      public SELF hasRegularExpressionMatching( final String string ) {
         assertThat( string ).matches( actual.getValue() );
         return myself;
      }

      @SuppressWarnings( "unchecked" )
      public <S extends AbstractStringAssert<S>> AbstractStringAssert<S> regularExpression() {
         return (AbstractStringAssert<S>) assertThat( actual.getValue() );
      }
   }

   public SELF isRegularExpressionConstraint() {
      assertThat( actual ).isInstanceOf( RegularExpressionConstraint.class );
      return myself;
   }

   @SuppressWarnings( { "unchecked", "IncorrectFormatting" } ) // For some reason, IntelliJ code style does not use correct indentation
   //@formatter:off
   public <S extends RegularExpressionConstraintAssert<S, A>, A extends RegularExpressionConstraint> RegularExpressionConstraintAssert<S, A>
         isRegularExpressionConstraintThat() {
      //@formatter:on
      isRegularExpressionConstraint();
      return new RegularExpressionConstraintAssert<>( (A) actual );
   }

   /**
    * Assert for {@link FixedPointConstraint}.
    *
    * @param <SELF> the self type
    * @param <ACTUAL> the element type
    */
   public static class FixedPointConstraintAssert<SELF extends FixedPointConstraintAssert<SELF, ACTUAL>,
         ACTUAL extends FixedPointConstraint> extends ConstraintAssert<SELF, ACTUAL> {
      public FixedPointConstraintAssert( final ACTUAL actual ) {
         super( actual, FixedPointConstraintAssert.class, "FixedPointConstraint" );
      }

      public SELF hasScale( final Integer scale ) {
         assertThat( actual.getScale() ).isEqualTo( scale );
         return myself;
      }

      public AbstractIntegerAssert<?> scale() {
         return assertThat( actual.getScale() );
      }

      public SELF hasInteger( final Integer integer ) {
         assertThat( actual.getInteger() ).isEqualTo( integer );
         return myself;
      }

      public AbstractIntegerAssert<?> integer() {
         return assertThat( actual.getInteger() );
      }
   }

   public SELF isFixedPointConstraint() {
      assertThat( actual ).isInstanceOf( FixedPointConstraint.class );
      return myself;
   }

   @SuppressWarnings( { "unchecked", "IncorrectFormatting" } ) // For some reason, IntelliJ code style does not use correct indentation
   //@formatter:off
   public <S extends FixedPointConstraintAssert<S, A>, A extends FixedPointConstraint> FixedPointConstraintAssert<S, A>
         isFixedPointConstraintThat() {
      //@formatter:on
      isFixedPointConstraint();
      return new FixedPointConstraintAssert<>( (A) actual );
   }
}
