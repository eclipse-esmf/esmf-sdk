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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.java.pojo.AspectModelJavaGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.resolution.types.ResolvedArrayType;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.common.reflect.TypeToken;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class AspectModelJavaGeneratorTest {
   private Collection<JavaGenerator> getGenerators( final TestAspect testAspect, final String customJavaPackageName ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .packageName( customJavaPackageName )
            .enableJacksonAnnotations( true )
            .executeLibraryMacros( false )
            .build();
      return List.of( new AspectModelJavaGenerator( aspect, config ) );
   }

   private Collection<JavaGenerator> getGenerators( final TestAspect testAspect, final boolean enableJacksonAnnotations,
         final boolean executeLibraryMacros, final File templateLibPath ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( enableJacksonAnnotations )
            .executeLibraryMacros( executeLibraryMacros )
            .templateLibFile( templateLibPath )
            .packageName( aspect.urn().getNamespaceMainPart() )
            .build();
      return List.of( new AspectModelJavaGenerator( aspect, config ) );
   }

   private Collection<JavaGenerator> getGenerators( final TestAspect testAspect ) {
      return getGenerators( TestResources.load( testAspect ) );
   }

   private Collection<JavaGenerator> getGenerators( final AspectModel aspectModel ) {
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( true )
            .executeLibraryMacros( false )
            .packageName( aspectModel.aspect().urn().getNamespaceMainPart() )
            .build();
      return List.of( new AspectModelJavaGenerator( aspectModel.aspect(), config ) );
   }

   private Collection<JavaGenerator> getGenerators( final TestAspect testAspect, final String namePrefix, final String namePostfix ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( true )
            .executeLibraryMacros( false )
            .packageName( aspectModel.aspect().urn().getNamespaceMainPart() )
            .namePrefix( namePrefix )
            .namePostfix( namePostfix )
            .build();
      return List.of( new AspectModelJavaGenerator( aspectModel.aspect(), config ) );
   }

   /**
    * Tests that code generation succeeds for all test models for the latest meta model version
    *
    * @param testAspect the injected Aspect model
    */
   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         "ASPECT_WITH_USED_AND_UNUSED_ENUMERATION", // No code will be generated for an unused enumeration
         "MODEL_WITH_BROKEN_CYCLES" // Contains elements that are not references from the Aspect
   } )
   void testCodeGeneration( final TestAspect testAspect ) {
      assertThatCode( () -> {
               final AspectModel aspectModel = TestResources.load( testAspect );
               final Model model = aspectModel.files().iterator().next().sourceModel();
               final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspectModel ) );
               final int numberOfFiles = result.compilationUnits.size();
               final List<Resource> structureElements = Streams.stream( model.listStatements( null, RDF.type, (RDFNode) null ) )
                     .filter( statement -> {
                        final Resource type = statement.getObject().asResource();
                        return type.equals( SammNs.SAMM.Aspect() )
                              || type.equals( SammNs.SAMM.Entity() )
                              || type.equals( SammNs.SAMM.Event() )
                              || type.equals( SammNs.SAMM.AbstractEntity() )
                              || type.equals( SammNs.SAMMC.Enumeration() );
                     } )
                     .map( Statement::getSubject )
                     .toList();

               final long numberOfStructureElements = structureElements.size();
               assertThat( numberOfFiles ).isGreaterThanOrEqualTo( (int) numberOfStructureElements );
            }
      ).doesNotThrowAnyException();
   }

   /**
    * Generates Java classes for an aspect model that has multiple entity properties, also nested ones.
    * In total 4 classes should be written.
    */
   @Test
   void testGenerateAspectModelMultipleEntitiesOnMultipleLevels() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testEntityOne", "TestEntity" )
            .put( "testEntityTwo", "TestEntity" )
            .put( "testString", String.class )
            .put( "testSecondEntity", "SecondTestEntity" )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForEntityClass = ImmutableMap.<String, Object> builder()
            .put( "testLocalDateTime", XMLGregorianCalendar.class )
            .put( "randomValue", String.class )
            .put( "testThirdEntity", "ThirdTestEntity" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_ON_MULTIPLE_LEVELS;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 4 );
      result.assertFields( "AspectWithMultipleEntitiesOnMultipleLevels", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithMultipleEntitiesOnMultipleLevels", expectedFieldsForAspectClass );
      result.assertFields( "TestEntity", expectedFieldsForEntityClass, new HashMap<>() );
      assertConstructor( result, "TestEntity", expectedFieldsForEntityClass );
      result.assertClassDeclaration( "AspectWithMultipleEntitiesOnMultipleLevels", Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList() );
      result.assertClassDeclaration( "TestEntity", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList() );
   }

   /**
    * Generates Java classes for an aspect model that has multiple enumeration properties as well as a entity property,
    * also nested ones. In total 5 classes should be written.
    */
   @Test
   void testGenerateAspectModelMultipleEnumerationsOnMultipleLevels() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testPropertyWithEnumOne", "TestEnumOneCharacteristic" )
            .put( "testPropertyWithEnumTwo", "TestEnumTwoCharacteristic" )
            .put( "testEntityWithEnumOne", "TestEntityWithEnumOne" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_MULTIPLE_ENUMERATIONS_ON_MULTIPLE_LEVELS;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 5 );
      result.assertFields( "AspectWithMultipleEnumerationsOnMultipleLevels", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithMultipleEnumerationsOnMultipleLevels", expectedFieldsForAspectClass );
      result.assertFields( "TestEnumOneCharacteristic", ImmutableMap.<String, Object> builder().put( "value", BigInteger.class ).build(),
            new HashMap<>() );
      result.assertEnumConstants( "TestEnumOneCharacteristic", ImmutableSet.of( "NUMBER_1", "NUMBER_2", "NUMBER_3" ),
            Collections.emptyMap() );
   }

   @Test
   void testGenerateRecursiveAspectModel() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", "Optional<TestEntity>" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "TestEntity", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "TestEntity", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectModelWithOptionalProperties() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "numberProperty", "Optional<BigInteger>" )
            .put( "timestampProperty", XMLGregorianCalendar.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithOptionalProperties", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithOptionalProperties", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectModelWithCurie() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testCurie", "Curie" )
            .put( "testCurieWithoutExampleValue", "Curie" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_CURIE;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithCurie", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithCurie", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectModelWithExtendedEnums() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "result", "EvaluationResults" )
            .put( "simpleResult", "YesNo" )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForEvaluationResults = ImmutableMap.<String, Object> builder()
            .put( "average", new TypeToken<Optional<BigInteger>>() {
            } )
            .put( "numericCode", Short.class )
            .put( "description", String.class )
            .put( "nestedResult", "NestedResult" )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForNestedResult = ImmutableMap.<String, Object> builder()
            .put( "average", BigInteger.class )
            .put( "description", String.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_EXTENDED_ENUMS;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 5 );
      result.assertFields( "AspectWithExtendedEnums", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithExtendedEnums", expectedFieldsForAspectClass );
      result.assertFields( "EvaluationResult", expectedFieldsForEvaluationResults, new HashMap<>() );
      assertConstructor( result, "EvaluationResult", expectedFieldsForEvaluationResults );
      result.assertFields( "EvaluationResults", ImmutableMap.<String, Object> builder().put( "value", "EvaluationResult" ).build(),
            new HashMap<>() );
      result.assertEnumConstants( "EvaluationResults", ImmutableSet.of( "RESULT_GOOD", "RESULT_BAD", "RESULT_NO_STATUS" ),
            Collections.emptyMap() );
      result.assertFields( "YesNo", ImmutableMap.<String, Object> builder().put( "value", String.class ).build(), new HashMap<>() );
      result.assertEnumConstants( "YesNo", ImmutableSet.of( "YES", "NO" ), Collections.emptyMap() );
      result.assertFields( "NestedResult", expectedFieldsForNestedResult, new HashMap<>() );
      assertConstructor( result, "NestedResult", expectedFieldsForNestedResult );
   }

   @Test
   void testGenerateAspectModelWithExtendedEnumsWithNotInPayloadProperty() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForEvaluationResults = ImmutableMap.<String, Object> builder()
            .put( "average", new TypeToken<Optional<BigInteger>>() {
            } )
            .put( "numericCode", Short.class )
            .put( "description", String.class )
            .put( "nestedResult", "NestedResult" )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForJacksonConstructor = ImmutableMap.<String, Object> builder()
            .put( "average", new TypeToken<Optional<BigInteger>>() {
            } )
            .put( "numericCode", Short.class )
            .put( "nestedResult", "NestedResult" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_EXTENDED_ENUMS_WITH_NOT_IN_PAYLOAD_PROPERTY;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect, true, false, null ) );

      final List<ConstructorDeclaration> constructorDeclarations = result.compilationUnits.get( "EvaluationResult" )
            .findAll( ConstructorDeclaration.class );
      assertThat( constructorDeclarations ).hasSize( 2 );

      result.assertFields( "EvaluationResult", expectedFieldsForEvaluationResults, ImmutableMap.<String, String> builder()
            .put( "average", "" )
            .put( "numericCode", "@NotNull" )
            .put( "description", "" )
            .put( "nestedResult", "@NotNull" )
            .build()
      );
      result.assertConstructor( "EvaluationResult", expectedFieldsForEvaluationResults, new HashMap<>() );

      final ConstructorDeclaration jacksonConstructor = constructorDeclarations.get( 1 );
      final NodeList<Parameter> parameters = jacksonConstructor.getParameters();
      result.assertConstructorParameters( parameters, expectedFieldsForJacksonConstructor,
            buildExpectedAnnotations( expectedFieldsForJacksonConstructor ) );
   }

   @BeforeAll
   public static void setup() {
      SammXsdType.setupTypeMapping();
   }

   @Test
   void testGenerateAspectModelWithConstraints() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testPropertyWithRegularExpression", String.class )
            .put( "testPropertyWithDecimalMinDecimalMaxRangeConstraint", BigDecimal.class )
            .put( "testPropertyWithDecimalMaxRangeConstraint", BigDecimal.class )
            .put( "testPropertyWithMinMaxRangeConstraint", Integer.class )
            .put( "testPropertyWithMinRangeConstraint", Integer.class )
            .put( "testPropertyRangeConstraintWithFloatType", Float.class )
            .put( "testPropertyRangeConstraintWithDoubleType", Double.class )
            .put( "testPropertyWithMinMaxLengthConstraint", String.class )
            .put( "testPropertyWithMinLengthConstraint", BigInteger.class )
            .put( "testPropertyCollectionLengthConstraint", new TypeToken<List<BigInteger>>() {
            } )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_CONSTRAINTS;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithConstraints", expectedFieldsForAspectClass,
            ImmutableMap.<String, String> builder()
                  .put( "testPropertyWithRegularExpression", "@NotNull" + "@Pattern(regexp = \"^[a-zA-Z]\\\\.[0-9]\")" )
                  .put( "testPropertyWithDecimalMinDecimalMaxRangeConstraint",
                        "@NotNull" + "@DecimalMin(value = \"2.3\")" + "@DecimalMax(value = \"10.5\")" )
                  .put( "testPropertyWithDecimalMaxRangeConstraint",
                        "@NotNull" + "@DecimalMax(value = \"10.5\")" )
                  .put( "testPropertyWithMinMaxRangeConstraint", "@NotNull"
                        + "@IntegerMin(value = 1, boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@IntegerMax(value = 10, boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithMinRangeConstraint", "@NotNull"
                        + "@IntegerMin(value = 1, boundDefinition = BoundDefinition.AT_LEAST)" )
                  .put( "testPropertyRangeConstraintWithFloatType", "@NotNull"
                        + "@FloatMin(value = \"1.0\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@FloatMax(value = \"10.0\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyRangeConstraintWithDoubleType", "@NotNull"
                        + "@DoubleMin(value = \"1.0\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@DoubleMax(value = \"10.0\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithMinMaxLengthConstraint", "@NotNull" + "@Size(min = 1, max = 10)" )
                  .put( "testPropertyWithMinLengthConstraint", "@NotNull" + "@Size(min = 1)" )
                  .put( "testPropertyCollectionLengthConstraint", "@NotNull" + "@Size(min = 1, max = 10)" )
                  .build() );
      assertConstructor( result, "AspectWithConstraints", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectModelWithOptionalAndConstraints() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "stringProperty", "Optional<@Size(max = 3) String>" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY_AND_CONSTRAINT;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithOptionalPropertyAndConstraint", expectedFieldsForAspectClass,
            ImmutableMap.<String, String> builder()
                  .put( "stringProperty", "" )
                  .build() );
      assertConstructor( result, "AspectWithOptionalPropertyAndConstraint", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectWithConstrainedCollection() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testCollection", new TypeToken<List<BigInteger>>() {
            } ).build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_CONSTRAINED_COLLECTION;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithConstrainedCollection", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithConstrainedCollection", expectedFieldsForAspectClass
      );
   }

   @Test
   void testGenerateAspectWithExclusiveRangeConstraint() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "floatProp", Float.class )
            .put( "doubleProp", Double.class )
            .put( "decimalProp", BigDecimal.class )
            .put( "integerProp", BigInteger.class )
            .put( "intProp", Integer.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_EXCLUSIVE_RANGE_CONSTRAINT;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithExclusiveRangeConstraint", expectedFieldsForAspectClass,
            ImmutableMap.<String, String> builder()
                  .put( "floatProp", "@NotNull"
                        + "@FloatMin(value = \"12.3\", boundDefinition = BoundDefinition.GREATER_THAN)"
                        + "@FloatMax(value = \"23.45\", boundDefinition = BoundDefinition.LESS_THAN)" )
                  .put( "doubleProp", "@NotNull"
                        + "@DoubleMin(value = \"12.3\", boundDefinition = BoundDefinition.GREATER_THAN)"
                        + "@DoubleMax(value = \"23.45\", boundDefinition = BoundDefinition.LESS_THAN)" )
                  .put( "decimalProp", "@NotNull"
                        + "@DecimalMin(value = \"12.3\", inclusive = false)"
                        + "@DecimalMax(value = \"23.45\", inclusive = false)" )
                  .put( "intProp", "@NotNull"
                        + "@IntegerMin(value = 12, boundDefinition = BoundDefinition.GREATER_THAN)"
                        + "@IntegerMax(value = 23, boundDefinition = BoundDefinition.LESS_THAN)" )
                  .put( "integerProp", "@NotNull"
                        + "@DecimalMin(value = \"12\", inclusive = false)"
                        + "@DecimalMax(value = \"23\", inclusive = false)" )
                  .build() );
      assertConstructor( result, "AspectWithExclusiveRangeConstraint", expectedFieldsForAspectClass
      );
   }

   @Test
   void testGenerateAspectWithEither() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", "Either<LeftEntity, RightEntity>" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 3 );
      result.assertFields( "AspectWithEitherWithComplexTypes", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithEitherWithComplexTypes", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectWithBoolean() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testBoolean", Boolean.class.getSimpleName() ).build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_BOOLEAN;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithBoolean", expectedFieldsForAspectClass,
            ImmutableMap.<String, String> builder().put( "testBoolean", "@NotNull" ).build() );
      assertConstructor( result, "AspectWithBoolean", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectWithBinary() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testBinary", new ResolvedArrayType( ResolvedPrimitiveType.BYTE ) )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_BINARY;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithBinary", expectedFieldsForAspectClass,
            ImmutableMap.<String, String> builder().put( "testBinary",
                        "@NotNull@JsonSerialize(using = HexBinarySerializer.class)@JsonDeserialize(using = HexBinaryDeserializer.class)" )
                  .build() );
      assertConstructor( result, "AspectWithBinary", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectWithCustomJavaPackageNameExpectCustomPackageDeclaration() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BINARY;
      final String customJavaPackageName = "test.test.test";
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect, customJavaPackageName ) );
      result.assertNumberOfFiles( 1 );
      result.assertNamespace( "AspectWithBinary", customJavaPackageName );
   }

   @Test
   void testGenerateAspectWithEmptyJavaPackageNameExpectDefaultPackageDeclaration() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_BINARY;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertNamespace( "AspectWithBinary", "org.eclipse.esmf.test" );
   }

   @Test
   void testGenerateAspectModelWithComplexEnumeration() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "result", "EvaluationResults" )
            .put( "simpleResult", "YesNo" )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForEvaluationResult = ImmutableMap.<String, Object> builder()
            .put( "numericCode", Short.class.getSimpleName() )
            .put( "description", String.class.getSimpleName() )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENUM;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 4 );
      result.assertFields( "AspectWithComplexEnum", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithComplexEnum", expectedFieldsForAspectClass );
      result.assertFields( "EvaluationResult", expectedFieldsForEvaluationResult, new HashMap<>() );
      assertConstructor( result, "EvaluationResult", expectedFieldsForEvaluationResult );
      result.assertFields( "EvaluationResults", ImmutableMap.<String, Object> builder().put( "value", "EvaluationResult" ).build(),
            new HashMap<>() );
      result.assertEnumConstants( "EvaluationResults",
            ImmutableSet.of( "RESULT_NO_STATUS", "RESULT_GOOD", "RESULT_BAD" ),
            ImmutableMap.<String, String> builder()
                  .put( "RESULT_NO_STATUS", "new EvaluationResult(Short.parseShort(\"-1\"), \"No status\")" )
                  .put( "RESULT_GOOD", "new EvaluationResult(Short.parseShort(\"1\"), \"Good\")" )
                  .put( "RESULT_BAD", "new EvaluationResult(Short.parseShort(\"2\"), \"Bad\")" )
                  .build() );

      result.assertFields( "YesNo", ImmutableMap.<String, Object> builder().put( "value", String.class ).build(), new HashMap<>() );
      result.assertEnumConstants( "YesNo", ImmutableSet.of( "YES", "NO" ), Collections.emptyMap() );
   }

   @Test
   void testGenerateAspectWithState() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "status", "TestState" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_STATE;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 2 );

      result.assertFields( "AspectWithState", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithState", expectedFieldsForAspectClass );

      result.assertEnumConstants( "TestState", ImmutableSet.of( "SUCCESS", "ERROR", "IN_PROGRESS" ), Collections.emptyMap() );
   }

   @Test
   void testGenerateAspectModelMultipleEntitiesOnMultipleLevelsWithoutJacksonAnnotations() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testEntityOne", "TestEntity" )
            .put( "testEntityTwo", "TestEntity" )
            .put( "testString", String.class )
            .put( "testSecondEntity", "SecondTestEntity" )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForEntityClass = ImmutableMap.<String, Object> builder()
            .put( "testLocalDateTime", XMLGregorianCalendar.class )
            .put( "randomValue", String.class )
            .put( "testThirdEntity", "ThirdTestEntity" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_ON_MULTIPLE_LEVELS;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, false, false, null ) );
      result.assertNumberOfFiles( 4 );
      result.assertFields( "AspectWithMultipleEntitiesOnMultipleLevels", expectedFieldsForAspectClass, new HashMap<>() );
      result.assertConstructor( "AspectWithMultipleEntitiesOnMultipleLevels", expectedFieldsForAspectClass, new HashMap<>() );
      result.assertFields( "TestEntity", expectedFieldsForEntityClass, new HashMap<>() );
      result.assertConstructor( "TestEntity", expectedFieldsForEntityClass, new HashMap<>() );
   }

   @Test
   void testGenerateAspectWithStructuredValue() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "date", XMLGregorianCalendar.class )
            .put( "year", Long.class )
            .put( "month", Long.class )
            .put( "day", Long.class )
            .build();

      final ImmutableMap<String, Object> expectedConstructorArguments = ImmutableMap.<String, Object> builder()
            .put( "date", XMLGregorianCalendar.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_NUMERIC_STRUCTURED_VALUE;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithNumericStructuredValue", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithNumericStructuredValue", expectedConstructorArguments );
   }

   @Test
   void testGenerateAspectModelWithComplexEnumerationInclOptional() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENUM_INCL_OPTIONAL;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 4 );
      result.assertEnumConstants( "EvaluationResults",
            ImmutableSet.of( "RESULT_NO_STATUS", "RESULT_GOOD", "RESULT_BAD" ),
            ImmutableMap.<String, String> builder()
                  .put( "RESULT_NO_STATUS", "new EvaluationResult(Optional.of(Short.parseShort(\"-1\")), Optional.of(\"No status\"))" )
                  .put( "RESULT_GOOD", "new EvaluationResult(Optional.of(Short.parseShort(\"1\")), Optional.of(\"Good\"))" )
                  .put( "RESULT_BAD", "new EvaluationResult(Optional.of(Short.parseShort(\"2\")), Optional.of(\"Bad\"))" )
                  .build() );
   }

   @Test
   void testGenerateAspectModelWithComplexEnumerations() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_COLLECTION_ENUM;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 9 );
      result.assertEnumConstants( "MyEnumerationOne", ImmutableSet.of( "ENTITY_INSTANCE_ONE" ),
            ImmutableMap.<String, String> builder()
                  .put( "ENTITY_INSTANCE_ONE", "new MyEntityOne(new ArrayList<>(){{ add(\"fooOne\");add(\"barOne\");add(\"bazOne\"); }})" )
                  .build() );

      result.assertEnumConstants( "MyEnumerationThree", ImmutableSet.of( "ENTITY_INSTANCE_THREE" ),
            ImmutableMap.<String, String> builder()
                  .put( "ENTITY_INSTANCE_THREE",
                        "new MyEntityThree(new LinkedHashSet<>(){{ add(\"fooThree\");add(\"barThree\");add(\"bazThree\"); }})" )
                  .build() );

      result.assertEnumConstants( "MyEnumerationFour", ImmutableSet.of( "ENTITY_INSTANCE_FOUR" ),
            ImmutableMap.<String, String> builder()
                  .put( "ENTITY_INSTANCE_FOUR",
                        "new MyEntityFour(new ArrayList<>(){{ add(\"fooFour\");add(\"barFour\");add(\"bazFour\"); }})" )
                  .build() );
   }

   @Test
   void testGenerateAspectModelWithComplexEntityCollectionEnumeration() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 4 );
      result.assertEnumConstants( "MyEnumerationOne", ImmutableSet.of( "ENTITY_INSTANCE_ONE" ),
            ImmutableMap.<String, String> builder()
                  .put( "ENTITY_INSTANCE_ONE", "new MyEntityOne(new ArrayList<>(){{ add(newMyEntityTwo(\"foo\")); }})" )
                  .build() );
   }

   @Test
   void testGenerateAspectModelWithFixedPointConstraint() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", BigDecimal.class.getSimpleName() )
            .build();

      final ImmutableMap<String, String> expectedAnnotations = ImmutableMap.<String, String> builder()
            .put( "testProperty", "@NotNull@Digits(fraction = 5, integer = 3)" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_FIXED_POINT;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );

      result.assertFields( "AspectWithFixedPoint", expectedFieldsForAspectClass, expectedAnnotations );
      assertConstructor( result, "AspectWithFixedPoint", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectModelWithList() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", new TypeToken<List<String>>() {
            } )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_LIST;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithList", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithList", expectedFieldsForAspectClass );
      result.assertClassDeclaration( "AspectWithList", Collections.emptyList(), Collections.emptyList(),
            Collections.singletonList( "CollectionAspect<List<String>,String>" ), Collections.emptyList() );
   }

   @Test
   void testGenerateAspectModelWithListAndElementCharacteristic() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", new TypeToken<List<String>>() {
            } )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_LIST_AND_ELEMENT_CHARACTERISTIC;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithListAndElementCharacteristic", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithListAndElementCharacteristic", expectedFieldsForAspectClass );
      result.assertClassDeclaration( "AspectWithListAndElementCharacteristic", Collections.emptyList(),
            Collections.emptyList(), Collections.singletonList( "CollectionAspect<List<String>,String>" ), Collections.emptyList() );
   }

   @Test
   void testGenerateAspectModelWithListAndElementConstraint() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", new TypeToken<List<Float>>() {
            } )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_LIST_AND_ELEMENT_CONSTRAINT;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithListAndElementConstraint", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithListAndElementConstraint", expectedFieldsForAspectClass );
      result.assertClassDeclaration( "AspectWithListAndElementConstraint", Collections.emptyList(),
            Collections.emptyList(), Collections.singletonList( "CollectionAspect<List<Float>,Float>" ),
            Collections.emptyList() );
      result.assertCollectionElementValidationAnnotations( "AspectWithListAndElementConstraint", "testProperty",
            "@NotNull private List<@FloatMin(value = \"2.3\", boundDefinition = BoundDefinition.AT_LEAST) "
                  + "@FloatMax(value = \"10.5\", boundDefinition = BoundDefinition.AT_MOST) Float>testProperty;" );
   }

   @Test
   void testGenerateAspectModelWithSet() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", new TypeToken<Set<String>>() {
            } )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_SET;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithSet", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithSet", expectedFieldsForAspectClass );
      result.assertClassDeclaration( "AspectWithSet", Collections.emptyList(), Collections.emptyList(),
            Collections.singletonList( "CollectionAspect<Set<String>,String>" ), Collections.emptyList() );
   }

   @Test
   void testGenerateAspectWithRdfLangString() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "prop", LangString.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithMultiLanguageText", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithMultiLanguageText", expectedFieldsForAspectClass );
   }

   private void assertConstructor( final GenerationResult result, final String className,
         final ImmutableMap<String, Object> expectedFields ) {
      final ImmutableMap<String, String> expectedAnnotationBuilder = buildExpectedAnnotations( expectedFields );
      result.assertConstructor( className, expectedFields, expectedAnnotationBuilder );
   }

   private ImmutableMap<String, String> buildExpectedAnnotations( final ImmutableMap<String, Object> expectedFields ) {
      final String expectedJsonAnnotation = "@JsonProperty(value = \"%s\")";
      final ImmutableMap.Builder<String, String> expectedAnnotationBuilder = ImmutableMap.builder();

      expectedFields.keySet().forEach( fieldName ->
            expectedAnnotationBuilder.put( fieldName, String.format( expectedJsonAnnotation, fieldName ) ) );
      return expectedAnnotationBuilder.build();
   }

   @Test
   void testGenerateAspectModelWithDurationTypeForRangeConstraints() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testPropertyWithDayTimeDuration", Duration.class )
            .put( "testPropertyWithDuration", Duration.class )
            .put( "testPropertyWithYearMonthDuration", Duration.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_DURATION_TYPE_FOR_RANGE_CONSTRAINTS;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithDurationTypeForRangeConstraints", expectedFieldsForAspectClass,
            ImmutableMap.<String, String> builder()
                  .put( "testPropertyWithDayTimeDuration", "@NotNull"
                        + "@DurationMin(value = \"P1DT5H\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@DurationMax(value = \"P1DT8H\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithDuration", "@NotNull"
                        + "@DurationMin(value = \"PT1H5M0S\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@DurationMax(value = \"PT1H5M3S\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithYearMonthDuration", "@NotNull"
                        + "@DurationMin(value = \"P5Y2M\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@DurationMax(value = \"P5Y3M\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .build() );
      assertConstructor( result, "AspectWithDurationTypeForRangeConstraints", expectedFieldsForAspectClass
      );
   }

   @Test
   void testGenerateAspectModelWithDateTimeTypeForRangeConstraints() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testPropertyWithDateTime", XMLGregorianCalendar.class )
            .put( "testPropertyWithDateTimeStamp", XMLGregorianCalendar.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_DATE_TIME_TYPE_FOR_RANGE_CONSTRAINTS;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithDateTimeTypeForRangeConstraints", expectedFieldsForAspectClass,
            ImmutableMap.<String, String> builder()
                  .put( "testPropertyWithDateTime", "@NotNull"
                        + "@GregorianCalendarMin(value = \"2000-01-01T14:23:00\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@GregorianCalendarMax(value = \"2000-01-02T15:23:00\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithDateTimeStamp", "@NotNull"
                        + "@GregorianCalendarMin(value = \"2000-01-01T14:23:00.66372+14:00\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@GregorianCalendarMax(value = \"2000-01-01T15:23:00.66372+14:00\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .build() );
      assertConstructor( result, "AspectWithDateTimeTypeForRangeConstraints", expectedFieldsForAspectClass
      );
   }

   @Test
   void testGenerateAspectModelWithGtypeForRangeConstraints() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testPropertyWithGYear", XMLGregorianCalendar.class )
            .put( "testPropertyWithGMonth", XMLGregorianCalendar.class )
            .put( "testPropertyWithGDay", XMLGregorianCalendar.class )
            .put( "testPropertyWithGYearMonth", XMLGregorianCalendar.class )
            .put( "testPropertyWithGMonthYear", XMLGregorianCalendar.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_G_TYPE_FOR_RANGE_CONSTRAINTS;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithGTypeForRangeConstraints", expectedFieldsForAspectClass,
            ImmutableMap.<String, String> builder()
                  .put( "testPropertyWithGYear", "@NotNull"
                        + "@GregorianCalendarMin(value = \"2000\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@GregorianCalendarMax(value = \"2001\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithGMonth", "@NotNull"
                        + "@GregorianCalendarMin(value = \"--04\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@GregorianCalendarMax(value = \"--05\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithGDay", "@NotNull"
                        + "@GregorianCalendarMin(value = \"---04\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@GregorianCalendarMax(value = \"---05\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithGYearMonth", "@NotNull"
                        + "@GregorianCalendarMin(value = \"2000-01\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@GregorianCalendarMax(value = \"2000-02\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .put( "testPropertyWithGMonthYear", "@NotNull"
                        + "@GregorianCalendarMin(value = \"--01-01\", boundDefinition = BoundDefinition.AT_LEAST)"
                        + "@GregorianCalendarMax(value = \"--01-02\", boundDefinition = BoundDefinition.AT_MOST)" )
                  .build() );
      assertConstructor( result, "AspectWithGTypeForRangeConstraints", expectedFieldsForAspectClass
      );
   }

   @Test
   void testGenerateAspectModelWithPropertyWithPayloadName() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "test", "String" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithPropertyWithPayloadName", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithPropertyWithPayloadName", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateAspectModelWithBlankNode() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "list", new TypeToken<Collection<String>>() {
            } )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_BLANK_NODE;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "AspectWithBlankNode", expectedFieldsForAspectClass, new HashMap<>() );
   }

   @Test
   void testGenerateEqualsForAspectWithEntity() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ENTITY;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );

      final Optional<PrimitiveType> expectedReturnType = Optional.of( PrimitiveType.booleanType() );
      final boolean expectOverride = true;
      final int expectedNumberOfParameters = 1;
      List<String> expectedMethodBody = List.of(
            "if(this==o){",
            "returntrue;",
            "}",
            "if(o==null||getClass()!=o.getClass()){",
            "returnfalse;",
            "}",
            "finalAspectWithEntitythat=(AspectWithEntity)o;",
            "returnObjects.equals(testProperty,that.testProperty);" );
      result.assertMethodBody( "AspectWithEntity", "equals", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );

      expectedMethodBody = List.of(
            "if(this==o){",
            "returntrue;",
            "}",
            "if(o==null||getClass()!=o.getClass()){",
            "returnfalse;",
            "}",
            "finalTestEntitythat=(TestEntity)o;",
            "returnObjects.equals(entityProperty,that.entityProperty);" );
      result.assertMethodBody( "TestEntity", "equals", expectOverride, expectedReturnType, expectedNumberOfParameters, expectedMethodBody );
   }

   @Test
   void testGenerateHashCodeForAspectWithEntity() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ENTITY;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );

      final Optional<PrimitiveType> expectedReturnType = Optional.of( PrimitiveType.intType() );
      final boolean expectOverride = true;
      final int expectedNumberOfParameters = 0;
      List<String> expectedMethodBody = List.of( "returnObjects.hash(testProperty);" );
      result.assertMethodBody( "AspectWithEntity", "hashCode", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );

      expectedMethodBody = List.of( "returnObjects.hash(entityProperty);" );
      result.assertMethodBody( "TestEntity", "hashCode", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );
   }

   @Test
   void testGenerateAspectModelWithAbstractEntity() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", "ExtendingTestEntity" )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForEntityClass = ImmutableMap.<String, Object> builder()
            .put( "entityProperty", String.class )
            .build();

      final ImmutableMap<String, Object> expectedConstructorArgumentsForEntityClass = ImmutableMap.<String, Object> builder()
            .put( "entityProperty", String.class )
            .put( "abstractTestProperty", BigInteger.class )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForAbstractEntityClass = ImmutableMap.<String, Object> builder()
            .put( "abstractTestProperty", BigInteger.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_ABSTRACT_ENTITY;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 3 );
      result.assertFields( "AspectWithAbstractEntity", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithAbstractEntity", expectedFieldsForAspectClass );
      result.assertFields( "ExtendingTestEntity", expectedFieldsForEntityClass, new HashMap<>() );
      assertConstructor( result, "ExtendingTestEntity", expectedConstructorArgumentsForEntityClass );
      result.assertFields( "AbstractTestEntity", expectedFieldsForAbstractEntityClass, new HashMap<>() );
      assertConstructor( result, "AbstractTestEntity", expectedFieldsForAbstractEntityClass );

      result.assertClassDeclaration( "AspectWithAbstractEntity", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList() );
      result.assertClassDeclaration( "AbstractTestEntity", Collections.singletonList( Modifier.abstractModifier() ),
            Collections.emptyList(), Collections.emptyList(), List.of( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)",
                  "@JsonSubTypes({ @JsonSubTypes.Type(value = ExtendingTestEntity.class, name = \"ExtendingTestEntity\") })" ) );
      result.assertClassDeclaration( "ExtendingTestEntity", Collections.emptyList(),
            Collections.singletonList( "AbstractTestEntity" ), Collections.emptyList(),
            List.of( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = ExtendingTestEntity.class)" ) );
   }

   @Test
   void testGenerateAspectModelWithCollectionWithAbstractEntity() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", "Collection<AbstractTestEntity>" )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForEntityClass = ImmutableMap.<String, Object> builder()
            .put( "entityProperty", String.class )
            .build();

      final ImmutableMap<String, Object> expectedConstructorArgumentsForEntityClass = ImmutableMap.<String, Object> builder()
            .put( "entityProperty", String.class )
            .put( "abstractTestProperty", BigInteger.class )
            .build();

      final ImmutableMap<String, Object> expectedFieldsForAbstractEntityClass = ImmutableMap.<String, Object> builder()
            .put( "abstractTestProperty", BigInteger.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, true, false, null ) );
      result.assertNumberOfFiles( 3 );
      result.assertFields( "AspectWithCollectionWithAbstractEntity", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "AspectWithCollectionWithAbstractEntity", expectedFieldsForAspectClass );
      result.assertFields( "ExtendingTestEntity", expectedFieldsForEntityClass, new HashMap<>() );
      assertConstructor( result, "ExtendingTestEntity", expectedConstructorArgumentsForEntityClass );
      result.assertFields( "AbstractTestEntity", expectedFieldsForAbstractEntityClass, new HashMap<>() );
      assertConstructor( result, "AbstractTestEntity", expectedFieldsForAbstractEntityClass );

      result.assertClassDeclaration( "AspectWithCollectionWithAbstractEntity", Collections.emptyList(),
            Collections.emptyList(),
            Collections.singletonList( "CollectionAspect<Collection<AbstractTestEntity>,AbstractTestEntity>" ),
            Collections.emptyList() );
      result.assertClassDeclaration( "AbstractTestEntity", Collections.singletonList( Modifier.abstractModifier() ),
            Collections.emptyList(), Collections.emptyList(),
            List.of( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)",
                  "@JsonSubTypes({ @JsonSubTypes.Type(value = ExtendingTestEntity.class, name = \"ExtendingTestEntity\") })" ) );
      result.assertClassDeclaration( "ExtendingTestEntity", Collections.emptyList(),
            Collections.singletonList( "AbstractTestEntity" ), Collections.emptyList(),
            List.of( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = ExtendingTestEntity.class)" ) );
   }

   @Test
   void testGenerateAspectModelWithEntityEnumerationAndLangString() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 3 );

      final ImmutableMap<String, String> expectedConstantArguments = ImmutableMap.<String, String> builder()
            .put( "ENTITY_INSTANCE",
                  "new TestEntity(new LangString(\"This is a test.\", Locale.forLanguageTag(\"en\")))" )
            .build();

      result.assertEnumConstants( "TestEnumeration", ImmutableSet.of( "ENTITY_INSTANCE" ), expectedConstantArguments );
   }

   @Test
   void testGenerateEqualsForAspectWithAbstractEntity() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ABSTRACT_ENTITY;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );

      final Optional<PrimitiveType> expectedReturnType = Optional.of( PrimitiveType.booleanType() );
      final boolean expectOverride = true;
      final int expectedNumberOfParameters = 1;
      List<String> expectedMethodBody = List.of(
            "if(this==o){",
            "returntrue;",
            "}",
            "if(o==null||getClass()!=o.getClass()){",
            "returnfalse;",
            "}",
            "finalAspectWithAbstractEntitythat=(AspectWithAbstractEntity)o;",
            "returnObjects.equals(testProperty,that.testProperty);" );
      result.assertMethodBody( "AspectWithAbstractEntity", "equals", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );

      expectedMethodBody = List.of(
            "if(this==o){",
            "returntrue;",
            "}",
            "if(o==null||getClass()!=o.getClass()){",
            "returnfalse;",
            "}",
            "finalAbstractTestEntitythat=(AbstractTestEntity)o;",
            "returnObjects.equals(abstractTestProperty,that.abstractTestProperty);" );
      result.assertMethodBody( "AbstractTestEntity", "equals", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );

      expectedMethodBody = List.of(
            "if(this==o){",
            "returntrue;",
            "}",
            "if(o==null||getClass()!=o.getClass()){",
            "returnfalse;",
            "}",
            "if(!super.equals(o)){",
            "returnfalse;",
            "}",
            "finalExtendingTestEntitythat=(ExtendingTestEntity)o;",
            "returnObjects.equals(entityProperty,that.entityProperty);" );
      result.assertMethodBody( "ExtendingTestEntity", "equals", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );
   }

   @Test
   void testGenerateHashCodeForAspectWithAbstractEntity() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ABSTRACT_ENTITY;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );

      final Optional<PrimitiveType> expectedReturnType = Optional.of( PrimitiveType.intType() );
      final boolean expectOverride = true;
      final int expectedNumberOfParameters = 0;
      List<String> expectedMethodBody = List.of( "returnObjects.hash(testProperty);" );
      result.assertMethodBody( "AspectWithAbstractEntity", "hashCode", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );

      expectedMethodBody = List.of( "returnObjects.hash(abstractTestProperty);" );
      result.assertMethodBody( "AbstractTestEntity", "hashCode", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );

      expectedMethodBody = List.of( "returnObjects.hash(super.hashCode(),entityProperty);" );
      result.assertMethodBody( "ExtendingTestEntity", "hashCode", expectOverride, expectedReturnType, expectedNumberOfParameters,
            expectedMethodBody );
   }

   @Test
   void testGenerateAspectWithoutFileHeader() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENUM;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      final CompilationUnit aspectClass = result.compilationUnits.get( TestAspect.ASPECT_WITH_COMPLEX_ENUM.getName() );
      assertThat( aspectClass.getComment() ).isEmpty();
      final CompilationUnit enumeration = result.compilationUnits.get( "EvaluationResults" );
      assertThat( enumeration.getComment() ).isEmpty();
      final CompilationUnit entity = result.compilationUnits.get( "EvaluationResult" );
      assertThat( entity.getComment() ).isEmpty();
   }

   @Test
   void testGenerateAspectWithFileHeader() throws IOException {
      final String currentWorkingDirectory = System.getProperty( "user.dir" );
      final File templateLibFile = Path.of( currentWorkingDirectory, "/templates", "/test-macro-lib.vm" ).toFile();

      final TestAspect aspect = TestAspect.ASPECT_WITH_COMPLEX_ENUM;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect, false, true, templateLibFile ) );

      final int currentYear = LocalDate.now().getYear();
      final String expectedCopyright = String.format( "Copyright (c) %s Test Inc. All rights reserved", currentYear );
      result.assertCopyright( TestAspect.ASPECT_WITH_COMPLEX_ENUM.getName(), expectedCopyright );
      result.assertCopyright( "EvaluationResults", expectedCopyright );
      result.assertCopyright( "EvaluationResult", expectedCopyright );
   }

   @Test
   void testGenerateAspectWithMultipleInheritance() throws IOException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_EXTENDED_ENTITY;
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( aspect ) );
      result.assertNumberOfFiles( 4 );
      result.assertClassDeclaration( "ParentOfParentEntity", Collections.singletonList( Modifier.abstractModifier() ),
            Collections.emptyList(), Collections.emptyList(), List.of( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)",
                  "@JsonSubTypes({ @JsonSubTypes.Type(value = ParentTestEntity.class, name = \"ParentTestEntity\"), @JsonSubTypes.Type"
                        + "(value = TestEntity"
                        + ".class, name = \"TestEntity\") })" ) );
      result.assertClassDeclaration( "ParentTestEntity", Collections.singletonList( Modifier.abstractModifier() ),
            Collections.singletonList( "ParentOfParentEntity" ), Collections.emptyList(),
            List.of( "@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)",
                  "@JsonSubTypes({ @JsonSubTypes.Type(value = TestEntity.class, name = \"TestEntity\") })" ) );
   }

   @Test
   void testGenerateAspectWithPrefixAndPostfix() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", String.class )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_PROPERTY;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, "Base", "Postfix" ) );
      result.assertNumberOfFiles( 1 );
      result.assertFields( "BaseAspectWithPropertyPostfix", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "BaseAspectWithPropertyPostfix", expectedFieldsForAspectClass );
   }

   @Test
   void testGenerateEntityWithPrefixAndPostfix() throws IOException {
      final ImmutableMap<String, Object> expectedFieldsForAspectClass = ImmutableMap.<String, Object> builder()
            .put( "testProperty", "BaseTestEntityPostfix" )
            .build();

      final TestAspect aspect = TestAspect.ASPECT_WITH_ENTITY;
      final GenerationResult result = TestContext.generateAspectCode()
            .apply( getGenerators( aspect, "Base", "Postfix" ) );
      result.assertNumberOfFiles( 2 );
      result.assertFields( "BaseAspectWithEntityPostfix", expectedFieldsForAspectClass, new HashMap<>() );
      assertConstructor( result, "BaseAspectWithEntityPostfix", expectedFieldsForAspectClass );
   }
}
