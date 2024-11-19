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

package org.eclipse.esmf.aspectmodel.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.metamodel.characteristic.impl.DefaultList;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultMeasurement;
import org.eclipse.esmf.metamodel.datatype.Curie;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestSharedAspect;

import com.github.javaparser.ast.CompilationUnit;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class StaticMetaModelJavaGeneratorTest extends StaticMetaModelGeneratorTest {

   /**
    * Tests that code generation succeeds for all test models for the latest meta model version
    *
    * @param testAspect the injected Aspect model
    */
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testCodeGeneration( final TestAspect testAspect ) {
      assertThatCode( () -> {
         final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( testAspect ) );
         final Pattern uninterpolatedTemplate = Pattern.compile( "\\$[a-zA-Z]" );
         result.compilationUnits.values().forEach( compilationUnit -> {
            // Check that all template variables have been replaced. If Velocity fails to insert a value (because evaluation of the
            // expression throws an exception), it leaves the template unchanged, i.e., leaving literal $ characters
            assertThat( compilationUnit.toString() ).doesNotContainPattern( uninterpolatedTemplate );
         } );
      } ).doesNotThrowAnyException();
   }

   /**
    * Tests that code generation succeeds for all test models, that have properties shared over two files, for the latest meta model version
    *
    * @param testAspect the injected shared Aspect models
    */
   @ParameterizedTest
   @EnumSource( value = TestSharedAspect.class )
   void testCodeGenerationSharedAspect( final TestSharedAspect testAspect ) {
      assertThatCode( () -> TestContext.generateStaticAspectCode()
            .apply( getGenerators( testAspect ) ) ).doesNotThrowAnyException();
   }

   @Test
   void testGenerateStaticMetaModelWithOptionalProperties() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES_WITH_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithOptionalPropertiesWithEntity" );
      final Class<?> entityClass = findGeneratedClass( result, "TestEntity" );

      result.assertNumberOfFiles( 4 );
      result.assertFields( "MetaAspectWithOptionalPropertiesWithEntity",
            fieldAssertions( "MetaAspectWithOptionalPropertiesWithEntity" )
                  .put( "TEST_STRING", TypeTokens.staticProperty( aspectClass, String.class ) )
                  .put( "TEST_OPTIONAL_STRING",
                        TypeTokens.staticContainerProperty( aspectClass, String.class, TypeTokens.optional( String.class ) ) )
                  .put( "TEST_OPTIONAL_ENTITY",
                        TypeTokens.staticContainerProperty( aspectClass, entityClass, TypeTokens.optional( entityClass ) ) )
                  .build(),
            new HashMap<>() );

      result.assertFields( "MetaTestEntity",
            fieldAssertions( "MetaTestEntity" )
                  .put( "CODE_PROPERTY", TypeTokens.staticProperty( entityClass, Integer.class ) )
                  .put( "TEST_SECOND_STRING", TypeTokens.staticProperty( entityClass, String.class ) )
                  .put( "TEST_INT_LIST",
                        TypeTokens.staticContainerProperty( entityClass, Integer.class, TypeTokens.list( Integer.class ) ) ).build(),
            new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithExtendedEnums() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_EXTENDED_ENUMS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithExtendedEnums" );
      final Class<?> evaluationResultClass = findGeneratedClass( result, "EvaluationResult" );
      final Class<?> evaluationResultsClass = findGeneratedClass( result, "EvaluationResults" );
      final Class<?> nestedResultClass = findGeneratedClass( result, "NestedResult" );

      result.assertNumberOfFiles( 8 );
      result.assertFields( "MetaAspectWithExtendedEnums",
            fieldAssertions( "MetaAspectWithExtendedEnums" )
                  .put( "RESULT", TypeTokens.staticProperty( aspectClass, evaluationResultsClass ) )
                  .put( "SIMPLE_RESULT", TypeTokens.staticProperty( aspectClass, findGeneratedClass( result, "YesNo" ) ) ).build(),
            new HashMap<>() );
      result.assertFields( "MetaEvaluationResult",
            fieldAssertions( "MetaEvaluationResult" )
                  .put( "AVERAGE", TypeTokens.staticContainerProperty( evaluationResultClass, BigInteger.class,
                        TypeTokens.optional( BigInteger.class ) ) )
                  .put( "NUMERIC_CODE", TypeTokens.staticProperty( evaluationResultClass, Short.class ) )
                  .put( "DESCRIPTION", TypeTokens.staticProperty( evaluationResultClass, String.class ) )
                  .put( "NESTED_RESULT", TypeTokens.staticProperty( evaluationResultClass, findGeneratedClass( result, "NestedResult" ) ) )
                  .build(), new HashMap<>() );
      result.assertFields( "MetaNestedResult",
            fieldAssertions( "MetaNestedResult" )
                  .put( "AVERAGE", TypeTokens.staticProperty( nestedResultClass, BigInteger.class ) )
                  .put( "DESCRIPTION", TypeTokens.staticProperty( nestedResultClass, String.class ) )
                  .build(), new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithEither() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> leftEntityClass = findGeneratedClass( result, "LeftEntity" );
      final Class<?> rightEntityClass = findGeneratedClass( result, "RightEntity" );

      result.assertNumberOfFiles( 6 );
      result.assertFields( "MetaAspectWithEitherWithComplexTypes",
            fieldAssertions( "MetaAspectWithEitherWithComplexTypes" )
                  .put( "TEST_PROPERTY", TypeTokens.staticProperty( findGeneratedClass( result, "AspectWithEitherWithComplexTypes" ),
                        TypeTokens.either( leftEntityClass, rightEntityClass ) ) ).build(), new HashMap<>() );

      result.assertFields( "MetaLeftEntity",
            fieldAssertions( "MetaLeftEntity" )
                  .put( "RESULT", TypeTokens.staticProperty( leftEntityClass, String.class ) ).build(),
            new HashMap<>() );

      result.assertFields( "MetaRightEntity",
            fieldAssertions( "MetaRightEntity" )
                  .put( "ERROR", TypeTokens.staticProperty( rightEntityClass, String.class ) ).build(),
            new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithMeasurement() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_MEASUREMENT;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "MetaAspectWithMeasurement",
            fieldAssertions( "MetaAspectWithMeasurement" )
                  .put( "TEST_PROPERTY",
                        TypeTokens.staticUnitProperty( findGeneratedClass( result, "AspectWithMeasurement" ), Float.class ) ).build(),
            new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithExtendedEntityAssertProperties() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_EXTENDED_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> testEntityClass = findGeneratedClass( result, "TestEntity" );

      result.assertNumberOfFiles( 8 );
      result.assertFields( "MetaAspectWithExtendedEntity",
            fieldAssertions( "MetaAspectWithExtendedEntity" )
                  .put( "TEST_PROPERTY", TypeTokens.staticContainerProperty( findGeneratedClass( result, "AspectWithExtendedEntity" ),
                        testEntityClass, TypeTokens.linkedHashSet( testEntityClass ) ) ).build(),
            new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithRecursiveAspectWithOptional() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithRecursivePropertyWithOptional" );
      final Class<?> testEntityClass = findGeneratedClass( result, "TestEntity" );

      result.assertNumberOfFiles( 4 );
      result.assertFields( "MetaAspectWithRecursivePropertyWithOptional",
            fieldAssertions( "MetaAspectWithRecursivePropertyWithOptional" )
                  .put( "TEST_PROPERTY", TypeTokens.staticProperty( aspectClass, testEntityClass ) ).build(), new HashMap<>() );
      result.assertFields( "MetaTestEntity",
            fieldAssertions( "MetaTestEntity" )
                  .put( "TEST_PROPERTY",
                        TypeTokens.staticContainerProperty( testEntityClass, testEntityClass, TypeTokens.optional( testEntityClass ) ) )
                  .build(), new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithDuration() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_DURATION;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "MetaAspectWithDuration",
            fieldAssertions( "MetaAspectWithDuration" )
                  .put( "TEST_PROPERTY",
                        TypeTokens.staticUnitProperty( findGeneratedClass( result, "AspectWithDuration" ), Integer.class ) ).build(),
            new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithCurie() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_CURIE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithCurie" );

      result.assertNumberOfFiles( 2 );
      result.assertFields( "MetaAspectWithCurie",
            fieldAssertions( "MetaAspectWithCurie" )
                  .put( "TEST_CURIE", TypeTokens.staticProperty( aspectClass, Curie.class ) )
                  .put( "TEST_CURIE_WITHOUT_EXAMPLE_VALUE", TypeTokens.staticProperty( aspectClass, Curie.class ) ).build(),
            new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithBinary() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BINARY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "MetaAspectWithBinary",
            fieldAssertions( "MetaAspectWithBinary" )
                  .put( "TEST_BINARY", TypeTokens.staticProperty( findGeneratedClass( result, "AspectWithBinary" ), byte[].class ) )
                  .build(),
            new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithState() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_STATE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithState" );
      final Class<?> testStateClass = findGeneratedClass( result, "TestState" );

      result.assertNumberOfFiles( 3 );

      result.assertFields( "MetaAspectWithState",
            fieldAssertions( "MetaAspectWithState" )
                  .put( "STATUS", TypeTokens.staticProperty( aspectClass, testStateClass ) )
                  .build(), new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithExtendedEntity() throws IOException {
      final TestSharedAspect aspect = TestSharedAspect.ASPECT_WITH_EXTENDED_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 8 );
      final String methodName = "getAllProperties";
      final boolean expectOverride = true;
      final int expectedNumberOfParameters = 0;
      final List<String> getAllPropertiesWithoutExtended = List.of( "returngetProperties();" );
      final List<String> getPropertiesMetaTestEntity = List.of(
            "returnStream.of(getProperties(),MetaParentTestEntity.INSTANCE.getAllProperties()).flatMap(Collection::stream).collect"
                  + "(Collectors.toList());" );
      final List<String> getPropertiesMetaParentTestEntity = List.of(
            "returnStream.of(getProperties(),MetaParentOfParentEntity.INSTANCE.getAllProperties()).flatMap(Collection::stream).collect"
                  + "(Collectors.toList());" );
      result.assertMethodBody( "MetaAspectWithExtendedEntity", methodName, expectOverride, Optional.empty(), expectedNumberOfParameters,
            getAllPropertiesWithoutExtended );
      result.assertMethodBody( "MetaParentTestEntity", methodName, expectOverride, Optional.empty(), expectedNumberOfParameters,
            getPropertiesMetaParentTestEntity );
      result.assertMethodBody( "MetaParentOfParentEntity", methodName, expectOverride, Optional.empty(), expectedNumberOfParameters,
            getAllPropertiesWithoutExtended );
      result.assertMethodBody( "MetaTestEntity", methodName, expectOverride, Optional.empty(), expectedNumberOfParameters,
            getPropertiesMetaTestEntity );
   }

   @Test
   void testGenerateStaticMetaModelWithConstraints() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_CONSTRAINTS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithConstraints" );

      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithConstraints",
            fieldAssertions( "MetaAspectWithConstraints" )
                  .put( "TEST_PROPERTY_WITH_REGULAR_EXPRESSION",
                        TypeTokens.staticConstraintProperty( aspectClass, String.class, DefaultCharacteristic.class ) )
                  .put( "TEST_PROPERTY_WITH_DECIMAL_MIN_DECIMAL_MAX_RANGE_CONSTRAINT",
                        TypeTokens.staticConstraintProperty( aspectClass, BigDecimal.class, DefaultMeasurement.class ) )
                  .put( "TEST_PROPERTY_WITH_DECIMAL_MAX_RANGE_CONSTRAINT",
                        TypeTokens.staticConstraintProperty( aspectClass, BigDecimal.class, DefaultMeasurement.class ) )
                  .put( "TEST_PROPERTY_WITH_MIN_MAX_RANGE_CONSTRAINT",
                        TypeTokens.staticConstraintProperty( aspectClass, Integer.class, DefaultMeasurement.class ) )
                  .put( "TEST_PROPERTY_WITH_MIN_RANGE_CONSTRAINT",
                        TypeTokens.staticConstraintProperty( aspectClass, Integer.class, DefaultMeasurement.class ) )
                  .put( "TEST_PROPERTY_RANGE_CONSTRAINT_WITH_FLOAT_TYPE",
                        TypeTokens.staticConstraintProperty( aspectClass, Float.class, DefaultMeasurement.class ) )
                  .put( "TEST_PROPERTY_RANGE_CONSTRAINT_WITH_DOUBLE_TYPE",
                        TypeTokens.staticConstraintProperty( aspectClass, Double.class, DefaultMeasurement.class ) )
                  .put( "TEST_PROPERTY_WITH_MIN_MAX_LENGTH_CONSTRAINT",
                        TypeTokens.staticConstraintProperty( aspectClass, String.class, DefaultCharacteristic.class ) )
                  .put( "TEST_PROPERTY_WITH_MIN_LENGTH_CONSTRAINT",
                        TypeTokens.staticConstraintProperty( aspectClass, BigInteger.class, DefaultCharacteristic.class ) )
                  .put( "TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT",
                        TypeTokens.staticConstraintContainerProperty( aspectClass, BigInteger.class, TypeTokens.list( BigInteger.class ),
                              DefaultList.class ) ).build(), new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithStructuredValue() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_NUMERIC_STRUCTURED_VALUE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithNumericStructuredValue" );

      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithNumericStructuredValue",
            fieldAssertions( "MetaAspectWithNumericStructuredValue" )
                  .put( "_datatypeFactory", DatatypeFactory.class )
                  .put( "YEAR", TypeTokens.staticProperty( aspectClass, Long.class ) )
                  .put( "MONTH", TypeTokens.staticProperty( aspectClass, Long.class ) )
                  .put( "DAY", TypeTokens.staticProperty( aspectClass, Long.class ) )
                  .put( "DATE", TypeTokens.staticProperty( aspectClass, XMLGregorianCalendar.class ) ).build(), new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithErrorCollection() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ERROR_COLLECTION;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithErrorCollection" );
      final Class<?> errorEntityClass = findGeneratedClass( result, "Error" );

      result.assertNumberOfFiles( 4 );

      result.assertFields( "MetaError",
            fieldAssertions( "MetaError" )
                  .put( "ERROR_NO", TypeTokens.staticProperty( errorEntityClass, Integer.class ) )
                  .put( "ERROR_TEXT", TypeTokens.staticProperty( errorEntityClass, String.class ) )
                  .put( "START_TIMESTAMP", TypeTokens.staticProperty( errorEntityClass, XMLGregorianCalendar.class ) )
                  .put( "_datatypeFactory", DatatypeFactory.class ).build(), new HashMap<>() );

      result.assertFields( "MetaAspectWithErrorCollection",
            fieldAssertions( "MetaAspectWithErrorCollection" )
                  .put( "ITEMS",
                        TypeTokens.staticContainerProperty( aspectClass, errorEntityClass, TypeTokens.collection( errorEntityClass ) ) )
                  .put( "_datatypeFactory", DatatypeFactory.class ).build(),
            new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithCollectionAndSimpleElementCharacteristic() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COLLECTION_AND_SIMPLE_ELEMENT_CHARACTERISTIC;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithCollectionAndSimpleElementCharacteristic",
            fieldAssertions( "MetaAspectWithCollectionAndSimpleElementCharacteristic" )
                  .put( "ITEMS", TypeTokens.staticContainerProperty(
                        findGeneratedClass( result, "AspectWithCollectionAndSimpleElementCharacteristic" ), String.class,
                        TypeTokens.collection( String.class ) ) ).build(), new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithCollectionAndElementCharacteristic() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COLLECTION_AND_ELEMENT_CHARACTERISTIC;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithCollectionAndElementCharacteristic" );
      final Class<?> testEntityClass = findGeneratedClass( result, "TestEntity" );

      result.assertNumberOfFiles( 4 );

      result.assertFields( "MetaAspectWithCollectionAndElementCharacteristic",
            fieldAssertions( "MetaAspectWithCollectionAndElementCharacteristic" )
                  .put( "ITEMS",
                        TypeTokens.staticContainerProperty( aspectClass, testEntityClass, TypeTokens.collection( testEntityClass ) ) )
                  .build(), new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithFixedPointConstraints() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_FIXED_POINT;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithFixedPoint",
            fieldAssertions( "MetaAspectWithFixedPoint" )
                  .put( "TEST_PROPERTY",
                        TypeTokens.staticConstraintProperty( findGeneratedClass( result, "AspectWithFixedPoint" ), BigDecimal.class,
                              DefaultMeasurement.class ) ).build(), new HashMap<>() );
   }

   @Test
   void testGenerateStaticMetaModelWithComplexEntityCollectionEnumeration() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithComplexEntityCollectionEnum" );
      final Class<?> enumerationClass = findGeneratedClass( result, "MyEnumerationOne" );
      final Class<?> entityOneClass = findGeneratedClass( result, "MyEntityOne" );
      final Class<?> entityTwoClass = findGeneratedClass( result, "MyEntityTwo" );

      result.assertNumberOfFiles( 7 );

      result.assertFields( "MetaAspectWithComplexEntityCollectionEnum",
            fieldAssertions( "MetaAspectWithComplexEntityCollectionEnum" )
                  .put( "MY_PROPERTY_ONE", TypeTokens.staticProperty( aspectClass, enumerationClass ) ).build(), new HashMap<>() );

      result.assertFields( "MetaMyEntityOne",
            fieldAssertions( "MetaMyEntityOne" )
                  .put( "ENTITY_PROPERTY_ONE",
                        TypeTokens.staticContainerProperty( entityOneClass, entityTwoClass, TypeTokens.list( entityTwoClass ) ) ).build(),
            new HashMap<>() );

      result.assertFields( "MetaMyEntityTwo",
            fieldAssertions( "MetaMyEntityTwo" )
                  .put( "ENTITY_PROPERTY_TWO", TypeTokens.staticProperty( entityTwoClass, String.class ) )
                  .build(), new HashMap<>() );
   }

   @Test
   void testCharacteristicInstantiationForEnums() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ENUM_AND_OPTIONAL_ENUM_PROPERTIES;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 3 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            """
                  new DefaultEnumeration(MetaModelBaseAttributes.builder().withUrn(AspectModelUrn.fromUrn(NAMESPACE + "TestEnumeration")).build(), new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer"), new ArrayList<Value>() {

                      {
                          add(new DefaultScalarValue(new BigInteger("1"), new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer")));
                          add(new DefaultScalarValue(new BigInteger("2"), new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer")));
                          add(new DefaultScalarValue(new BigInteger("3"), new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer")));
                      }
                  })""";

      result.assertConstructorArgumentForProperties( "MetaAspectWithEnumAndOptionalEnumProperties",
            ImmutableMap.<String, String> builder().put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall )
                  .put( "OPTIONAL_TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall ).build(), 1 );
   }

   @Test
   void testAspectWithPropertyWithPayloadName() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final String expectedPayloadNameArgument = "Optional.of(\"test\")";

      result.assertConstructorArgumentForProperties( "MetaAspectWithPropertyWithPayloadName",
            ImmutableMap.<String, String> builder().put( "TEST_PROPERTY", expectedPayloadNameArgument ).build(), 5 );
   }

   @Test
   void testAspectWithBlankNode() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BLANK_NODE;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "MetaAspectWithBlankNode",
            fieldAssertions( "MetaAspectWithBlankNode" )
                  .put( "LIST", TypeTokens.staticContainerProperty( findGeneratedClass( result, "AspectWithBlankNode" ), String.class,
                        TypeTokens.collection( String.class ) ) ).build(), new HashMap<>() );
   }

   @Test
   void testCharacteristicInstantiationForQuantifiableWithoutUnit() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            "new DefaultQuantifiable(MetaModelBaseAttributes" + ".builder()"
                  + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"TestQuantifiable\"))"
                  + ".withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Quantifiable\")"
                  + ".withDescription(Locale.forLanguageTag(\"en\"), \"This is a test Quantifiable\")"
                  + ".withSee(\"http://example.com/\").build(),"
                  + " new DefaultScalar(\"http://www.w3.org/2001/XMLSchema#float\"),"
                  + " Optional.empty())";

      result.assertConstructorArgumentForProperties( "MetaAspectWithQuantifiableWithoutUnit",
            ImmutableMap.<String, String> builder().put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall ).build(), 1 );
   }

   @Test
   void testCharacteristicInstantiationForPropertyWithExampleValue() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COLLECTION;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      result.assertNumberOfFiles( 2 );

      Map<String, Set<String>> expectedBaseAttributes = new HashMap<>();
      expectedBaseAttributes.put( "TEST_PROPERTY", Set.of(
            "withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"testProperty\"))",
            "withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Property\")",
            "withDescription(Locale.forLanguageTag(\"en\"), \"This is a test property.\")",
            "withSee(\"http://example.com/\")",
            "withSee(\"http://example.com/me\")"
      ) );

      result.assertMetaModelBaseAttributesForProperties( "MetaAspectWithCollection", expectedBaseAttributes );

      final String expectedExampleValue = "Optional.of(new DefaultScalarValue(\"Example Value\", new DefaultScalar(\"http://www.w3"
            + ".org/2001/XMLSchema#string\")))";

      result.assertConstructorArgumentForProperties(
            "MetaAspectWithCollection",
            ImmutableMap.<String, String> builder()
                  .put( "TEST_PROPERTY", expectedExampleValue )
                  .build(),
            2
      );
   }

   @Test
   void testCharacteristicInstantiationForQuantifiableWithUnit() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_QUANTIFIABLE_WITH_UNIT;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            "new DefaultQuantifiable(MetaModelBaseAttributes.builder().withUrn("
                  + "AspectModelUrn.fromUrn(NAMESPACE + \"TestQuantifiable\")).build(),"
                  + " new DefaultScalar(\"http://www.w3.org/2001/XMLSchema#float\"), Units.fromName(\"percent\"))";

      result.assertConstructorArgumentForProperties( "MetaAspectWithQuantifiableWithUnit",
            ImmutableMap.<String, String> builder().put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall ).build(), 1 );
   }

   @Test
   void testGenerateStaticMetaModelForAspectModelWithAbstractEntity() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ABSTRACT_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 6 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            "new DefaultSingleEntity(MetaModelBaseAttributes.builder()"
                  + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"EntityCharacteristic\"))"
                  + ".withPreferredName(Locale"
                  + ".forLanguageTag(\"en\"), \"Test Entity Characteristic\").withDescription(Locale.forLanguageTag(\"en\"), \"This is a "
                  + "test Entity "
                  + "Characteristic\").withSee(\"http://example.com/\").build(), DefaultEntity.createDefaultEntity("
                  + "MetaModelBaseAttributes.builder()"
                  + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + "
                  + "\"ExtendingTestEntity\"))"
                  + ".withPreferredName(Locale.forLanguageTag(\"en\"), \"Test Entity\").withDescription(Locale.forLanguageTag(\"en\"), "
                  + "\"This is a test entity\")"
                  + ".build(), MetaExtendingTestEntity.INSTANCE.getProperties(), Optional.of(DefaultAbstractEntity"
                  + ".createDefaultAbstractEntity"
                  + "(MetaModelBaseAttributes.builder()"
                  + ".withUrn(AspectModelUrn.fromUrn"
                  + "(NAMESPACE + \"AbstractTestEntity\")).withPreferredName(Locale.forLanguageTag(\"en\"), \"Abstract Test Entity\")"
                  + ".withDescription(Locale"
                  + ".forLanguageTag(\"en\"), \"This is a abstract test entity\").build(), MetaAbstractTestEntity.INSTANCE.getProperties"
                  + "(), Optional.empty(), List"
                  + ".of(AspectModelUrn.fromUrn(\"urn:samm:org.eclipse.esmf.test:1.0.0#ExtendingTestEntity\"))))))";

      result.assertConstructorArgumentForProperties( "MetaAspectWithAbstractEntity",
            ImmutableMap.<String, String> builder().put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall ).build(), 1 );
   }

   @Test
   void testGenerateStaticMetaModelForAspectModelWithCollectionWithAbstractEntity() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 6 );

      final String expectedTestPropertyCharacteristicConstructorCall =
            "new DefaultCollection(MetaModelBaseAttributes.builder()"
                  + ".withUrn(AspectModelUrn.fromUrn(NAMESPACE + \"EntityCollectionCharacteristic\")).withDescription(Locale"
                  + ".forLanguageTag(\"en\"), "
                  + "\"This is an entity collection characteristic\").build(), Optional.of(DefaultAbstractEntity"
                  + ".createDefaultAbstractEntity("
                  + "MetaModelBaseAttributes.builder()"
                  + ".withUrn("
                  + "AspectModelUrn.fromUrn(NAMESPACE + \"AbstractTestEntity\")).withDescription(Locale.forLanguageTag(\"en\"), "
                  + "\"This is an abstract test entity\").build(), MetaAbstractTestEntity.INSTANCE.getProperties(), Optional.empty(), "
                  + "List.of(AspectModelUrn.fromUrn(\"urn:samm:org.eclipse.esmf.test:1.0.0#ExtendingTestEntity\")))), Optional.empty())";

      result.assertConstructorArgumentForProperties( "MetaAspectWithCollectionWithAbstractEntity",
            ImmutableMap.<String, String> builder().put( "TEST_PROPERTY", expectedTestPropertyCharacteristicConstructorCall ).build(), 1 );
   }

   @Test
   void testGenerateStaticMetaModelWithoutFileHeader() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENUM;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );
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

   @Test
   void testGenerateAspectWithFileHeader() throws IOException {
      final String currentWorkingDirectory = System.getProperty( "user.dir" );
      final File templateLibFile = Path.of( currentWorkingDirectory, "/templates", "/test-macro-lib.vm" ).toFile();

      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENUM;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode()
            .apply( getGenerators( aspect, true, templateLibFile ) );

      final int currentYear = LocalDate.now().getYear();
      final String expectedCopyright = String.format( "Copyright (c) %s Test Inc. All rights reserved", currentYear );
      result.assertCopyright( TestAspect.ASPECT_WITH_COMPLEX_ENUM.getName(), expectedCopyright );
      result.assertCopyright( "EvaluationResults", expectedCopyright );
      result.assertCopyright( "EvaluationResult", expectedCopyright );
      result.assertCopyright( "MetaAspectWithComplexEnum", expectedCopyright );
      result.assertCopyright( "MetaEvaluationResult", expectedCopyright );
   }

   @Test
   void testGenerateStaticMetaModelWithUmlauts() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_UMLAUT_DESCRIPTION;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect ) );

      result.assertMethodBody( "MetaAspectWithUmlautDescription", "getDescriptions", true, Optional.empty(), 0,
            List.of(
                  "returnnewHashSet<>(){{add(newLangString(\"ImWortEntitätisteinUmlaut\",Locale.forLanguageTag(\"de\")));add(newLangString"
                        + "(\"Thisisatestdescription\",Locale.forLanguageTag(\"en\")));}};" ) );
   }
}
