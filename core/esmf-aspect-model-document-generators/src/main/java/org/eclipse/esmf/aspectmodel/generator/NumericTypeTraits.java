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
package org.eclipse.esmf.aspectmodel.generator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaVisitor;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides often needed information about various properties of the numeric types
 * plus some useful operations.
 */
public class NumericTypeTraits {

   private static final Logger LOG = LoggerFactory.getLogger( NumericTypeTraits.class );

   private static final List<Class<?>> FLOATING_POINT_TYPES = List.of( Float.class, Double.class, BigDecimal.class );
   private static final List<Class<?>> UNBOUNDED_TYPES = List.of( BigDecimal.class, BigInteger.class );

   private static final Map<Class<?>, BiFunction<Number, Float, Number>> ADDERS = Map.of(
         Byte.class, ( base, add ) -> (byte) (base.byteValue() + add.byteValue()),
         Short.class, ( base, add ) -> (short) (base.shortValue() + add.shortValue()),
         Integer.class, ( base, add ) -> base.intValue() + add.intValue(),
         Long.class, ( base, add ) -> base.longValue() + add.longValue(),
         Float.class, ( base, add ) -> base.floatValue() + add,
         Double.class, ( base, add ) -> base.doubleValue() + add,
         BigInteger.class, ( base, add ) -> ((BigInteger) base).add( BigInteger.valueOf( add.longValue() ) )
   );

   private static final Map<Class<?>, Function<Number, BigDecimal>> CONVERTERS = Map.of(
         Long.class, number -> BigDecimal.valueOf( number.longValue() ),
         BigInteger.class, number -> new BigDecimal( (BigInteger) number ),
         BigDecimal.class, BigDecimal.class::cast
   );

   /**
    * Does the given Java numeric type represent a floating point number type?
    *
    * @param valueType one of the native Java numeric types
    * @return true, when the given type is one of the floating point types false otherwise (integral numeric type)
    */
   public static boolean isFloatingPointNumberType( final java.lang.reflect.Type valueType ) {
      return FLOATING_POINT_TYPES.contains( valueType );
   }

   /**
    * Adds (subtracts) a (small) number to a numeric type polymorphically, returning the same numeric type.
    *
    * @param base number to add to
    * @param add number to add
    * @return result of the operation, has the same numeric type as the base
    */
   public static Number polymorphicAdd( final Number base, final float add ) {
      return ADDERS.getOrDefault( base.getClass(), ( num, adder ) -> ((BigDecimal) num).add( BigDecimal.valueOf( adder ) ) )
            .apply( base, add );
   }

   /**
    * BigDecimal is a numeric "supertype", all other numeric types can be represented by it.
    *
    * @param number number to convert to BigDecimal
    * @return result
    */
   public static BigDecimal convertToBigDecimal( final Number number ) {
      return CONVERTERS.getOrDefault( number.getClass(), num -> BigDecimal.valueOf( num.doubleValue() ) ).apply( number );
   }

   /**
    * Min value for a numeric Java type. BigDecimal and BigInteger are technically unbounded,
    * but to remain polymorphic over all Number-derived types, we return the bound for the Double class
    * for these two types.
    *
    * @param valueType Java Number-derived type to get the min value for
    * @return minimal possible value for the given native type
    */
   public static Number getNativeMinValue( final java.lang.reflect.Type valueType ) {
      return getNativeBound( valueType, NativeBounds.MIN_VALUE );
   }

   /**
    * Min value for a numeric Java type. BigDecimal and BigInteger are technically unbounded,
    * but to remain polymorphic over all Number-derived types, we return the bound for the Double class
    * for these two types.
    *
    * @param valueType Java Number-derived type to get the max value for
    * @return maximal possible value for the given native type
    */
   public static Number getNativeMaxValue( final java.lang.reflect.Type valueType ) {
      return getNativeBound( valueType, NativeBounds.MAX_VALUE );
   }

   /**
    * Because not all model types have their native equivalent in Java (for example unsigned types),
    * we need to find out what range constraints apply to the particular model type when calculating the valid range for the (native) type.
    *
    * @param dataType meta model data type
    * @return min value for the given meta model type
    */
   public static Number getModelMinValue( final Type dataType ) {
      final Resource dataTypeResource = ResourceFactory.createResource( dataType.getUrn() );
      final Class<?> nativeType = SammXsdType.getJavaTypeForMetaModelType( dataTypeResource );
      return getModelMinValue( dataTypeResource, nativeType );
   }

   /**
    * Because not all model types have their native equivalent in Java (for example unsigned types),
    * we need to find out what range constraints apply to the particular model type when calculating the valid range for the (native) type.
    *
    * @param dataTypeResource model type
    * @param valueType native type
    * @return min value for the given combination of model and native types
    */
   public static Number getModelMinValue( final Resource dataTypeResource, final java.lang.reflect.Type valueType ) {
      return getModelBound( dataTypeResource, valueType, ModelBounds.MIN_VALUE );
   }

   /**
    * Because not all model types have their native equivalent in Java (for example unsigned types),
    * we need to find out what range constraints apply to the particular model type when calculating the valid range for the (native) type.
    *
    * @param dataType meta model data type
    * @return max value for the given meta model type
    */
   public static Number getModelMaxValue( final Type dataType ) {
      final Resource dataTypeResource = ResourceFactory.createResource( dataType.getUrn() );
      final Class<?> nativeType = SammXsdType.getJavaTypeForMetaModelType( dataTypeResource );
      return getModelMaxValue( dataTypeResource, nativeType );
   }

   /**
    * Because not all model types have their native equivalent in Java (for example unsigned types),
    * we need to find out what range constraints apply to the particular model type when calculating the valid range for the (native) type.
    *
    * @param dataTypeResource model type
    * @param valueType native type
    * @return max value for the given combination of model and native types
    */
   public static Number getModelMaxValue( final Resource dataTypeResource, final java.lang.reflect.Type valueType ) {
      return getModelBound( dataTypeResource, valueType, ModelBounds.MAX_VALUE );
   }

   private static Number getModelBound( final Resource dataTypeResource, final java.lang.reflect.Type valueType,
         final ModelBounds whichBound ) {
      final Map<String, JsonNode> dataTypeConstraints = AspectModelJsonSchemaVisitor.OPEN_API_TYPE_DATA.get( dataTypeResource );
      if ( null != dataTypeConstraints ) {
         final NumericNode bound = (NumericNode) dataTypeConstraints.get( whichBound.getValue() );
         if ( null != bound ) {
            return bound.numberValue();
         }
      }
      return getNativeBound( valueType, ModelBounds.MIN_VALUE.equals( whichBound ) ? NativeBounds.MIN_VALUE : NativeBounds.MAX_VALUE );
   }

   private enum ModelBounds {
      MIN_VALUE( "minimum" ),
      MAX_VALUE( "maximum" );

      private final String value;

      ModelBounds( final String s ) {
         value = s;
      }

      public String getValue() {
         return value;
      }
   }

   private enum NativeBounds {
      MIN_VALUE( "MIN_VALUE" ),
      MAX_VALUE( "MAX_VALUE" );

      private final String value;

      NativeBounds( final String s ) {
         value = s;
      }

      public String getValue() {
         return value;
      }
   }

   private static Number getNativeBound( final java.lang.reflect.Type valueType, final NativeBounds whichBound ) {
      if ( UNBOUNDED_TYPES.contains( valueType ) ) {
         // for BigDecimal and BigInteger, get the widest range we technically have (that of the Double class)
         final BigDecimal bigBound = BigDecimal.valueOf( getNativeBound( Double.class, whichBound ).doubleValue() );
         return BigDecimal.class.equals( valueType.getClass() ) ? bigBound : bigBound.toBigInteger();
      }
      // inconsistent API: for Double and Float the MIN_VALUE returns semantically something different than for the other types
      if ( NumericTypeTraits.isFloatingPointNumberType( valueType ) ) {
         final Number max = (Number) getStaticFieldValue( valueType, NativeBounds.MAX_VALUE.getValue() );
         return NativeBounds.MAX_VALUE.equals( whichBound ) ? max : getMinFromMax( max );
      }
      return (Number) getStaticFieldValue( valueType, whichBound.getValue() );
   }

   private static Number getMinFromMax( final Number max ) {
      if ( Double.class.equals( max.getClass() ) ) {
         return -max.doubleValue();
      } else {
         return -max.floatValue();
      }
   }

   // We take advantage of the fact that most relevant Java classes have a static fields named "MIN_VALUE" and "MAX_VALUE"
   // and the resulting values are polymorphic over the Number class.
   private static Object getStaticFieldValue( final java.lang.reflect.Type valueType, final String fieldName ) {
      try {
         return Class.forName( valueType.getTypeName() )
               .getField( fieldName )
               .get( null );
      } catch ( final Exception e ) {
         LOG.error( "Exception {}, caused by unexpected data type: {}", e, valueType.getTypeName() );
         return 0;
      }
   }
}
