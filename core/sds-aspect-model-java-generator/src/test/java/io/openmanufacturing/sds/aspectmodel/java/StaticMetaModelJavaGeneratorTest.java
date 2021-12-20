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

package io.openmanufacturing.sds.aspectmodel.java;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.javaparser.ast.CompilationUnit;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.metamodel.datatypes.Curie;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.impl.DefaultFixedPointConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultLengthConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultList;
import io.openmanufacturing.sds.metamodel.impl.DefaultMeasurement;
import io.openmanufacturing.sds.metamodel.impl.DefaultRangeConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultRegularExpressionConstraint;
import io.openmanufacturing.sds.staticmetamodel.StaticContainerProperty;
import io.openmanufacturing.sds.staticmetamodel.StaticProperty;
import io.openmanufacturing.sds.staticmetamodel.StaticUnitProperty;
import io.openmanufacturing.sds.staticmetamodel.constraint.StaticConstraintContainerProperty;
import io.openmanufacturing.sds.staticmetamodel.constraint.StaticConstraintProperty;
import io.openmanufacturing.sds.test.TestAspect;

public class StaticMetaModelJavaGeneratorTest extends StaticMetaModelGeneratorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithOptionalProperties( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES_WITH_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 4 );
      result.assertFields( "MetaAspectWithOptionalPropertiesWithEntity",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithOptionalPropertiesWithEntity" )
                  .put( "TEST_STRING", new TypeToken<StaticProperty<String>>() {
                  } )
                  .put( "TEST_OPTIONAL_STRING", new TypeToken<StaticContainerProperty<String, Optional<String>>>() {
                  } )
                  .put( "TEST_OPTIONAL_ENTITY", "StaticContainerProperty<TestEntity,Optional<TestEntity>>" )
                  .build(), new HashMap<>() );

      result.assertFields( "MetaTestEntity",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaTestEntity" )
                  .put( "CODE_PROPERTY", new TypeToken<StaticProperty<Integer>>() {
                  } )
                  .put( "TEST_SECOND_STRING", new TypeToken<StaticProperty<String>>() {
                  } )
                  .put( "TEST_INT_LIST", new TypeToken<StaticContainerProperty<Integer, List<Integer>>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithExtendedEnums( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_EXTENDED_ENUMS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 8 );
      result.assertFields( "MetaAspectWithExtendedEnums",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithExtendedEnums" )
                  .put( "RESULT", "StaticProperty<EvaluationResults>" )
                  .put( "SIMPLE_RESULT", "StaticProperty<YesNo>" )
                  .build(), new HashMap<>() );
      result.assertFields( "MetaEvaluationResult",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaEvaluationResult" )
                  .put( "AVERAGE", new TypeToken<StaticContainerProperty<BigInteger, Optional<BigInteger>>>() {
                  } )
                  .put( "NUMERIC_CODE", new TypeToken<StaticProperty<Short>>() {
                  } )
                  .put( "DESCRIPTION", new TypeToken<StaticProperty<String>>() {
                  } )
                  .put( "NESTED_RESULT", "StaticProperty<NestedResult>" )
                  .build(), new HashMap<>() );
      result.assertFields( "MetaNestedResult",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaNestedResult" )
                  .put( "AVERAGE", new TypeToken<StaticProperty<BigInteger>>() {
                  } )
                  .put( "DESCRIPTION", new TypeToken<StaticProperty<String>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithEither( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 6 );
      result.assertFields( "MetaAspectWithEitherWithComplexTypes",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithEitherWithComplexTypes" )
                  .put( "TEST_PROPERTY", "StaticProperty<Either<LeftEntity,RightEntity>>" )
                  .build(), new HashMap<>() );

      result.assertFields( "MetaLeftEntity",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaLeftEntity" )
                  .put( "RESULT", new TypeToken<StaticProperty<String>>() {
                  } )
                  .build(), new HashMap<>() );

      result.assertFields( "MetaRightEntity",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaRightEntity" )
                  .put( "ERROR", new TypeToken<StaticProperty<String>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithMeasurement( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_MEASUREMENT;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "MetaAspectWithMeasurement",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithMeasurement" )
                  .put( "TEST_PROPERTY", new TypeToken<StaticUnitProperty<Float>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithRecursiveAspectWithOptional( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 4 );
      result.assertFields( "MetaAspectWithRecursivePropertyWithOptional",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithRecursivePropertyWithOptional" )
                  .put( "TEST_PROPERTY", "StaticProperty<TestEntity>" )
                  .build(), new HashMap<>() );
      result.assertFields( "MetaTestEntity",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaTestEntity" )
                  .put( "TEST_PROPERTY", "StaticContainerProperty<TestEntity,Optional<TestEntity>>" )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithDuration( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_DURATION;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "MetaAspectWithDuration",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithDuration" )
                  .put( "TEST_PROPERTY", new TypeToken<StaticUnitProperty<Integer>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithCurie( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_CURIE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "MetaAspectWithCurie",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithCurie" )
                  .put( "TEST_CURIE", new TypeToken<StaticProperty<Curie>>() {
                  } )
                  .put( "TEST_CURIE_WITHOUT_EXAMPLE_VALUE",
                        new TypeToken<StaticProperty<Curie>>() {
                        } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithBinary( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BINARY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "MetaAspectWithBinary",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithBinary" )
                  .put( "TEST_BINARY", new TypeToken<StaticProperty<byte[]>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithState( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_STATE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 3 );

      result.assertFields( "MetaAspectWithState",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithState" )
                  .put( "STATUS", "StaticProperty<TestState>" )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithConstraints( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_CONSTRAINTS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithConstraints",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithConstraints" )
                  .put( "TEST_PROPERTY_WITH_REGULAR_EXPRESSION",
                        new TypeToken<StaticConstraintProperty<String, DefaultRegularExpressionConstraint, DefaultCharacteristic>>() {
                        } )
                  .put( "TEST_PROPERTY_WITH_DECIMAL_MIN_DECIMAL_MAX_RANGE_CONSTRAINT",
                        new TypeToken<StaticConstraintProperty<BigDecimal, DefaultRangeConstraint, DefaultMeasurement>>() {
                        } )
                  .put( "TEST_PROPERTY_WITH_DECIMAL_MAX_RANGE_CONSTRAINT",
                        new TypeToken<StaticConstraintProperty<BigDecimal, DefaultRangeConstraint, DefaultMeasurement>>() {
                        } )
                  .put( "TEST_PROPERTY_WITH_MIN_MAX_RANGE_CONSTRAINT",
                        new TypeToken<StaticConstraintProperty<Integer, DefaultRangeConstraint, DefaultMeasurement>>() {
                        } )
                  .put( "TEST_PROPERTY_WITH_MIN_RANGE_CONSTRAINT",
                        new TypeToken<StaticConstraintProperty<Integer, DefaultRangeConstraint, DefaultMeasurement>>() {
                        } )
                  .put( "TEST_PROPERTY_RANGE_CONSTRAINT_WITH_FLOAT_TYPE",
                        new TypeToken<StaticConstraintProperty<Float, DefaultRangeConstraint, DefaultMeasurement>>() {
                        } )
                  .put( "TEST_PROPERTY_RANGE_CONSTRAINT_WITH_DOUBLE_TYPE",
                        new TypeToken<StaticConstraintProperty<Double, DefaultRangeConstraint, DefaultMeasurement>>() {
                        } )
                  .put( "TEST_PROPERTY_WITH_MIN_MAX_LENGTH_CONSTRAINT",
                        new TypeToken<StaticConstraintProperty<String, DefaultLengthConstraint, DefaultCharacteristic>>() {
                        } )
                  .put( "TEST_PROPERTY_WITH_MIN_LENGTH_CONSTRAINT",
                        new TypeToken<StaticConstraintProperty<BigInteger, DefaultLengthConstraint, DefaultCharacteristic>>() {
                        } )
                  .put( "TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT",
                        new TypeToken<StaticConstraintContainerProperty<BigInteger, List<BigInteger>, DefaultLengthConstraint, DefaultList>>() {
                        } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithStructuredValue( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_NUMERIC_STRUCTURED_VALUE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithNumericStructuredValue",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithNumericStructuredValue" )
                  .put( "YEAR", new TypeToken<StaticProperty<Long>>() {
                  } )
                  .put( "MONTH", new TypeToken<StaticProperty<Long>>() {
                  } )
                  .put( "DAY", new TypeToken<StaticProperty<Long>>() {
                  } )
                  .put( "DATE", new TypeToken<StaticProperty<XMLGregorianCalendar>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithErrorCollection( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ERROR_COLLECTION;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 4 );

      result.assertFields( "MetaError",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaError" )
                  .put( "ERROR_NO", new TypeToken<StaticProperty<Integer>>() {
                  } )
                  .put( "ERROR_TEXT", new TypeToken<StaticProperty<String>>() {
                  } )
                  .put( "START_TIMESTAMP", new TypeToken<StaticProperty<XMLGregorianCalendar>>() {
                  } )
                  .build(), new HashMap<>() );

      result.assertFields( "MetaAspectWithErrorCollection",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithErrorCollection" )
                  .put( "ITEMS", "StaticContainerProperty<Error,Collection<Error>>" )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithCollectionAndSimpleElementCharacteristic( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COLLECTION_AND_SIMPLE_ELEMENT_CHARACTERISTIC;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithCollectionAndSimpleElementCharacteristic",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithCollectionAndSimpleElementCharacteristic" )
                  .put( "ITEMS", "StaticContainerProperty<String,Collection<String>>" )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithCollectionAndElementCharacteristic( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COLLECTION_AND_ELEMENT_CHARACTERISTIC;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 4 );

      result.assertFields( "MetaAspectWithCollectionAndElementCharacteristic",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithCollectionAndElementCharacteristic" )
                  .put( "ITEMS", "StaticContainerProperty<TestEntity,Collection<TestEntity>>" )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithFixedPointConstraints( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_FIXED_POINT;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithFixedPoint",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithFixedPoint" )
                  .put( "TEST_PROPERTY", new TypeToken<StaticConstraintProperty<BigDecimal, DefaultFixedPointConstraint, DefaultMeasurement>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testGenerateStaticMetaModelWithComplexEntityCollectionEnumeration( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 7 );

      result.assertFields( "MetaAspectWithComplexEntityCollectionEnum",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithComplexEntityCollectionEnum" )
                  .put( "MY_PROPERTY_ONE", "StaticProperty<MyEnumerationOne>" )
                  .build(), new HashMap<>() );

      result.assertFields( "MetaMyEntityOne",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaMyEntityOne" )
                  .put( "ENTITY_PROPERTY_ONE", "StaticContainerProperty<MyEntityTwo,List<MyEntityTwo>>" )
                  .build(), new HashMap<>() );

      result.assertFields( "MetaMyEntityTwo",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaMyEntityTwo" )
                  .put( "ENTITY_PROPERTY_TWO", "StaticProperty<String>" )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCharacteristicInstantiationForEnums( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ENUM_AND_OPTIONAL_ENUM_PROPERTIES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 3 );

      final String expectedTestPropertyCharacteristicConstructorCall = "new DefaultEnumeration("
            + "MetaModelBaseAttributes.from(KnownVersion." + KnownVersion.getLatest() + ", "
            + "AspectModelUrn.fromUrn(NAMESPACE + \"testProperty\"), \"TestEnumeration\"), "
            + "Optional.of(new DefaultScalar(\"http://www.w3.org/2001/XMLSchema#int\", KnownVersion." + KnownVersion.getLatest() + ")), "
            + "List.of(TestEnumeration.values()))";

      final String expectedOptionalTestPropertyCharacteristicConstructorCall = "new DefaultEnumeration("
            + "MetaModelBaseAttributes.from(KnownVersion." + KnownVersion.getLatest() + ", "
            + "AspectModelUrn.fromUrn(NAMESPACE + \"optionalTestProperty\"), \"TestEnumeration\"), "
            + "Optional.of(new DefaultScalar(\"http://www.w3.org/2001/XMLSchema#int\", KnownVersion." + KnownVersion.getLatest() + ")), "
            + "List.of(TestEnumeration.values()))";

      result.assertConstructorArgumentForProperties( "MetaAspectWithEnumAndOptionalEnumProperties",
            ImmutableMap.<String, String> builder()
                  .put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall )
                  .put( "OPTIONAL_TEST_PROPERTY", expectedOptionalTestPropertyCharacteristicConstructorCall )
                  .build(), 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithPropertyWithPayloadName( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String expectedPayloadNameArgument = "Optional.of(\"test\")";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithPayloadName",
            ImmutableMap.<String, String> builder()
                  .put( "TEST_PROPERTY", expectedPayloadNameArgument )
                  .build(), 5 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithBlankNode( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BLANK_NODE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithBlankNode",
            ImmutableMap.<String, Object> builder().put( "NAMESPACE", String.class )
                  .put( "MODEL_ELEMENT_URN", String.class )
                  .put( "CHARACTERISTIC_NAMESPACE", String.class )
                  .put( "INSTANCE", "MetaAspectWithBlankNode" )
                  .put( "LIST", new TypeToken<StaticContainerProperty<String, Collection<String>>>() {
                  } )
                  .build(), new HashMap<>() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCharacteristicInstantiationForQuantifiableWithoutUnit( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            "new DefaultQuantifiable(MetaModelBaseAttributes"
                  + ".builderFor(\"TestQuantifiable\")"
                  + ".withMetaModelVersion(KnownVersion." + KnownVersion.getLatest() + ")"
                  + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"TestQuantifiable\"))"
                  + ".withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Quantifiable\")"
                  + ".withDescription(Locale.forLanguageTag(\"en\"), \"This is a test Quantifiable\")"
                  + ".withSee(\"http://example.com/omp\").build(),"
                  + " Optional.of(new DefaultScalar(\"http://www.w3.org/2001/XMLSchema#float\", KnownVersion."
                  + KnownVersion.getLatest() + ")),"
                  + " Optional.empty())";

      result.assertConstructorArgumentForProperties( "MetaAspectWithQuantifiableWithoutUnit",
            ImmutableMap.<String, String> builder()
                  .put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall )
                  .build(), 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCharacteristicInstantiationForQuantifiableWithUnit( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_QUANTIFIABLE_WITH_UNIT;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 2 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            "new DefaultQuantifiable(MetaModelBaseAttributes.from(KnownVersion." + KnownVersion.getLatest() + ","
                  + " AspectModelUrn.fromUrn(NAMESPACE + \"testProperty\"), \"TestQuantifiable\"),"
                  + " Optional.of(new DefaultScalar(\"http://www.w3.org/2001/XMLSchema#float\", KnownVersion."
                  + KnownVersion.getLatest() + ")), Units.fromName(\"percent\"))";

      result.assertConstructorArgumentForProperties( "MetaAspectWithQuantifiableWithUnit",
            ImmutableMap.<String, String> builder()
                  .put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall )
                  .build(), 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testGenerateStaticMetaModelForAspectModelWithAbstractEntity( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ABSTRACT_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 6 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            "new DefaultSingleEntity(MetaModelBaseAttributes.builderFor(\"EntityCharacteristic\")"
                  + ".withMetaModelVersion(KnownVersion.BAMM_2_0_0)"
                  + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"EntityCharacteristic\"))"
                  + ".withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Entity Characteristic\")"
                  + ".withDescription(Locale.forLanguageTag(\"en\"), \"This is a test Entity Characteristic\")"
                  + ".withSee(\"http://example.com/omp\").build(), "
                  + "Optional.of(DefaultEntity.createDefaultEntity(MetaModelBaseAttributes.from(KnownVersion.BAMM_2_0_0,"
                  + " AspectModelUrn.fromUrn(NAMESPACE + \"ExtendingTestEntity\"), \"ExtendingTestEntity\"), "
                  + "MetaExtendingTestEntity.INSTANCE.getProperties(), Optional.of(DefaultAbstractEntity"
                  + ".createDefaultAbstractEntity(MetaModelBaseAttributes.from(KnownVersion.BAMM_2_0_0, "
                  + "AspectModelUrn.fromUrn(NAMESPACE + \"AbstractTestEntity\"), \"AbstractTestEntity\"), "
                  + "MetaAbstractTestEntity.INSTANCE.getProperties(), Optional.empty(), List.of(AspectModelUrn"
                  + ".fromUrn(\"urn:bamm:io.openmanufacturing.test:1.0.0#ExtendingTestEntity\")))))))";

      result.assertConstructorArgumentForProperties( "MetaAspectWithAbstractEntity",
            ImmutableMap.<String, String> builder()
                  .put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall )
                  .build(), 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testGenerateStaticMetaModelForAspectModelWithCollectionWithAbstractEntity( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      result.assertNumberOfFiles( 6 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            "new DefaultCollection(MetaModelBaseAttributes.from(KnownVersion.BAMM_2_0_0, AspectModelUrn"
                  + ".fromUrn(NAMESPACE + \"testProperty\"), \"EntityCollectionCharacteristic\"), Optional"
                  + ".of(DefaultAbstractEntity.createDefaultAbstractEntity(MetaModelBaseAttributes"
                  + ".from(KnownVersion.BAMM_2_0_0, AspectModelUrn.fromUrn(NAMESPACE + \"AbstractTestEntity\"),"
                  + " \"AbstractTestEntity\"), MetaAbstractTestEntity.INSTANCE.getProperties(), Optional.empty(), "
                  + "List.of(AspectModelUrn.fromUrn(\"urn:bamm:io.openmanufacturing.test:1.0.0#ExtendingTestEntity\")))), "
                  + "Optional.empty())";

      result.assertConstructorArgumentForProperties( "MetaAspectWithCollectionWithAbstractEntity",
            ImmutableMap.<String, String> builder()
                  .put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall )
                  .build(), 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testGenerateStaticMetaModelWithoutCopyrightHeader( final KnownVersion metaModelVersion ) throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENUM;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );
      final CompilationUnit aspectClass = result.compilationUnits.get( TestAspect.ASPECT_WITH_COMPLEX_ENUM.getName() );
      assertThat( aspectClass.getComment() ).isEmpty();
      final CompilationUnit enumeration = result.compilationUnits.get( "EvaluationResults" );
      assertThat( enumeration.getComment() ).isEmpty();
      final CompilationUnit entity = result.compilationUnits.get( "EvaluationResult" );
      assertThat( entity.getComment() ).isEmpty();
      final CompilationUnit staticAspectClass = result.compilationUnits.get( "MetaAspectWithComplexEnum" );
      assertThat( staticAspectClass.getComment() ).isEmpty();
      final CompilationUnit staticEntity = result.compilationUnits.get( "MetaEvaluationResult" );
      assertThat( staticEntity.getComment() ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testGenerateAspectWithCopyrightHeader( final KnownVersion metaModelVersion ) throws IOException {
      final String currentWorkingDirectory = System.getProperty( "user.dir" );
      final String templateLibPath = currentWorkingDirectory + "/templates";

      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENUM;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion, true,
            templateLibPath, "test-macro-lib.vm" ) );

      final int currentYear = LocalDate.now().getYear();
      final String expectedCopyright = String.format( "Copyright (c) %s OMP Test GmbH. All rights reserved", currentYear );
      result.assertCopyright( TestAspect.ASPECT_WITH_COMPLEX_ENUM.getName(), expectedCopyright );
      result.assertCopyright( "EvaluationResults", expectedCopyright );
      result.assertCopyright( "EvaluationResult", expectedCopyright );
      result.assertCopyright( "MetaAspectWithComplexEnum", expectedCopyright );
      result.assertCopyright( "MetaEvaluationResult", expectedCopyright );
   }
}