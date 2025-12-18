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
package org.eclipse.esmf.aspectmodel.generator.json;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.eclipse.esmf.metamodel.Elements.*;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AbstractTestEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithAbstractEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithAbstractSingleEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithCollectionOfSimpleType;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithCollectionWithAbstractEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithComplexEntityCollectionEnum;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithComplexSet;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithComplexSetAsList;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithConstrainedSet;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithConstraintProperties;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithConstraints;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithCurie;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithEither;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithEntityCollection;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithEntityEnumerationAndLangString;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithEntityEnumerationAndNotInPayloadProperties;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithEnum;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithEnumHavingNestedEntities;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithExtendedEnumsWithNotInPayloadProperty;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithFixedPointConstraint;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithGTypeForRangeConstraints;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithMultiLanguageText;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithMultilanguageExampleValue;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithMultipleCollectionsOfSimpleType;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithMultipleEntities;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithMultipleEntitiesAndEither;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithMultipleEntityCollections;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithNestedEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithPropertyWithPayloadName;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithRangeConstraintWithoutMinMaxIntegerValue;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithRecursivePropertyWithOptional;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithSimpleProperties;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithSimpleTypesAndState;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithStructuredValue;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.Entity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.ExtendingTestEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.Id;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.NestedEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.TestEntityWithSimpleTypes;
import org.eclipse.esmf.aspectmodel.jackson.AspectModelJacksonModule;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfig;
import org.eclipse.esmf.aspectmodel.java.JavaCodeGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.java.QualifiedName;
import org.eclipse.esmf.aspectmodel.java.pojo.AspectModelJavaGenerator;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;
import org.eclipse.esmf.test.shared.compiler.JavaCompiler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectModelJsonPayloadGeneratorTest {
   private static final String PACKAGE = "org.eclipse.esmf.test.generatedtestclasses";
   private static DatatypeFactory datatypeFactory;

   @BeforeAll
   static void setup() {
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch ( final DatatypeConfigurationException exception ) {
         throw new RuntimeException( exception );
      }
   }

   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         "ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND" // will only work if "addTypeAttributeForEntityInheritance" is true in the config
   } )
   void testDeserializationForGeneratedJson( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final JavaCompiler.CompilationResult compilationResult = compile( aspect );

      final Class<?> aspectClass = compilationResult.compilationUnits().entrySet().stream()
            .filter( entry -> entry.getKey().getClassName().equals( aspect.getName() ) )
            .map( Map.Entry::getValue )
            .findFirst()
            .orElseThrow();
      assertThatCode( () -> {
         final String payload = new AspectModelJsonPayloadGenerator( aspect,
               AspectModelJsonPayloadGenerator.DEFAULT_CONFIG ).generateJson();
         assertThat( payload ).doesNotContain( "\"@type\"" );
         final ObjectMapper mapper = objectMapper();
         mapper.setTypeFactory( mapper.getTypeFactory().withClassLoader( compilationResult.classLoader() ) );
         mapper.readValue( payload, aspectClass );
      } ).doesNotThrowAnyException();
   }

   /**
    * Tests the combination of code generation using JsonTypeInfo.Id.NAME with corresponding JSON payload that contains a @type
    * attribute for the case that there are ambiguous entities inheriting from an AbstractEntity
    */
   @Test
   void testDeserializationForAmbiguousSubEntities() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_SAME_EXTEND ).aspect();
      final JavaCodeGenerationConfig codeGenerationConfig = JavaCodeGenerationConfigBuilder.builder()
            .packageName( PACKAGE )
            .enableJacksonAnnotations( true )
            .executeLibraryMacros( false )
            // Handling ambiguous subentities only works with JsonTypeInfo.Id.NAME and @type attribute
            .jsonTypeInfo( JavaCodeGenerationConfig.JsonTypeInfoType.NAME )
            .build();
      final JavaCompiler.CompilationResult compilationResult = compile( aspect, codeGenerationConfig );
      final Class<?> aspectClass = compilationResult.compilationUnits().entrySet().stream()
            .filter( entry -> entry.getKey().getClassName().equals( aspect.getName() ) )
            .map( Map.Entry::getValue )
            .findFirst()
            .orElseThrow();
      assertThatCode( () -> {
         final JsonPayloadGenerationConfig jsonPayloadGenerationConfig = JsonPayloadGenerationConfigBuilder.builder()
               .addTypeAttributeForEntityInheritance( true )
               .build();
         final String payload = new AspectModelJsonPayloadGenerator( aspect, jsonPayloadGenerationConfig ).generateJson();
         assertThat( payload ).contains( "\"@type\"" );
         final ObjectMapper mapper = objectMapper();
         mapper.setTypeFactory( mapper.getTypeFactory().withClassLoader( compilationResult.classLoader() ) );
         mapper.readValue( payload, aspectClass );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testGenerationOfTypeAttributeForEntityInheritance() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY ).aspect();
      final JsonPayloadGenerationConfig config = JsonPayloadGenerationConfigBuilder.builder()
            .addTypeAttributeForEntityInheritance( true )
            .build();
      final String json = new AspectModelJsonPayloadGenerator( aspect, config ).generateJson();
      assertThat( json ).contains( "\"@type\" : \"ExtendingTestEntity\"" );
   }

   private JavaCompiler.CompilationResult compile( final Aspect aspect, final JavaCodeGenerationConfig config ) {
      final AspectModelJavaGenerator codeGenerator = new AspectModelJavaGenerator( aspect, config );
      final Map<QualifiedName, ByteArrayOutputStream> outputs = new LinkedHashMap<>();
      codeGenerator.generate( name -> outputs.computeIfAbsent( name, name2 -> new ByteArrayOutputStream() ) );

      final Map<QualifiedName, String> sources = new LinkedHashMap<>();
      final List<QualifiedName> loadOrder = new ArrayList<>();
      for ( final Map.Entry<QualifiedName, ByteArrayOutputStream> entry : outputs.entrySet() ) {
         loadOrder.add( entry.getKey() );
         sources.put( entry.getKey(), entry.getValue().toString( StandardCharsets.UTF_8 ) );
      }

      final List<String> referencedClasses = Stream
            .concat( codeGenerator.getConfig().importTracker().getUsedImports().stream(),
                  codeGenerator.getConfig().importTracker().getUsedStaticImports().stream() )
            .collect( Collectors.toList() );
      return JavaCompiler.compile( loadOrder, sources, referencedClasses );
   }

   private JavaCompiler.CompilationResult compile( final Aspect aspect ) {
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .packageName( PACKAGE )
            .enableJacksonAnnotations( true )
            .executeLibraryMacros( false )
            .build();
      return compile( aspect, config );
   }

   @Test
   void testGenerateJsonForAspectWithCollectionOfEntities() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENTITY_LIST );

      final AspectWithEntityCollection aspectWithEntityCollection = parseJson( generatedJson, AspectWithEntityCollection.class );

      assertThat( aspectWithEntityCollection.getTestList() ).hasSizeBetween( 1, 5 );

      final TestEntityWithSimpleTypes testEntityWithSimpleTypes = aspectWithEntityCollection.getTestList().get( 0 );

      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertTestEntityWithSimpleTypes( testEntityWithSimpleTypes );
   }

   @Test
   void testGenerateJsonForAspectWithSimpleProperties() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES );

      final AspectWithSimpleProperties aspectWithSimpleProperties = parseJson( generatedJson, AspectWithSimpleProperties.class );

      assertThat( aspectWithSimpleProperties.getRandomValue() ).isNotBlank();
      assertThat( aspectWithSimpleProperties.getRandomValue() ).isNotNull();
      assertThat( aspectWithSimpleProperties.getTestFloat() ).isEqualTo( 2.25f );
      assertThat( aspectWithSimpleProperties.getTestLocalDateTime() ).isEqualTo(
            datatypeFactory.newXMLGregorianCalendar( "2018-02-28T14:23:32.918" ) );
      assertThat( aspectWithSimpleProperties.getTestInt() ).isEqualTo( 3 );
      assertThat( aspectWithSimpleProperties.getTestString() ).isEqualTo( "Example Value Test" );

      assertThat( aspectWithSimpleProperties.getTestLocalDateTimeWithoutExample() ).isNotNull();
      assertThat( aspectWithSimpleProperties.getTestDurationWithoutExample() ).isNotNull();
   }

   @Test
   void testGenerateJsonForAspectWithStateType() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES_AND_STATE );

      final AspectWithSimpleTypesAndState aspectWithSimpleTypes = parseJson( generatedJson, AspectWithSimpleTypesAndState.class );
      assertThat( aspectWithSimpleTypes.getRandomValue() ).isNotBlank();
      assertThat( aspectWithSimpleTypes.getTestFloat() ).isEqualTo( 2.25f );
      assertThat( aspectWithSimpleTypes.getTestLocalDateTime() ).isEqualTo(
            datatypeFactory.newXMLGregorianCalendar( "2018-02-28T14:23:32.918" ) );
      assertThat( aspectWithSimpleTypes.getTestInt() ).isEqualTo( 3 );
      assertThat( aspectWithSimpleTypes.getTestString() ).isEqualTo( "Example Value Test" );
      assertThat( aspectWithSimpleTypes.getAutomationProperty() ).isEqualTo( "Automation Default Prop" );
   }

   @Test
   void testGenerateJsonForAspectWithEntity() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENTITY_WITH_MULTIPLE_PROPERTIES );

      final AspectWithEntity aspectWithEntity = parseJson( generatedJson, AspectWithEntity.class );
      assertTestEntityWithSimpleTypes( aspectWithEntity.getTestEntity() );
   }

   @Test
   void testGenerateJsonForAspectWithRecursivePropertyWithOptional() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL );
      final AspectWithRecursivePropertyWithOptional aspectWithRecursivePropertyWithOptional = parseJson( generatedJson,
            AspectWithRecursivePropertyWithOptional.class );
      assertThat( aspectWithRecursivePropertyWithOptional.getTestProperty().getTestProperty().get().getTestProperty() ).isNotPresent();
   }

   @Test
   void testGenerateJsonForAspectWithMultipleEntities() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES );

      final AspectWithMultipleEntities aspectWithMultipleEntities = parseJson( generatedJson, AspectWithMultipleEntities.class );
      assertTestEntityWithSimpleTypes( aspectWithMultipleEntities.getTestEntityOne() );
      assertTestEntityWithSimpleTypes( aspectWithMultipleEntities.getTestEntityTwo() );
   }

   @Test
   void testGenerateJsonForAspectWithNestedEntity() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_NESTED_ENTITY );

      final AspectWithNestedEntity aspectWithNestedEntity = parseJson( generatedJson, AspectWithNestedEntity.class );

      final Entity entity = aspectWithNestedEntity.getEntity();
      assertThat( entity.getTestString() ).isEqualTo( "Example Value Test" );

      final NestedEntity nestedEntity = entity.getNestedEntity();
      assertThat( nestedEntity.getTestString() ).isEqualTo( "Example Value Test" );
   }

   @Test
   void testGenerateJsonForAspectWithMultipleCollectionsOfSimpleType() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTIPLE_COLLECTIONS_OF_SIMPLE_TYPE );

      final AspectWithMultipleCollectionsOfSimpleType aspectWithCollectionOfSimpleType = parseJson( generatedJson,
            AspectWithMultipleCollectionsOfSimpleType.class );

      assertThat( generatedJson ).contains( "[" ).contains( "]" );
   }

   @Test
   void testGenerateJsonForAspectWithCollectionOfSimpleType() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE );

      final AspectWithCollectionOfSimpleType aspectWithCollectionOfSimpleType = parseJson( generatedJson,
            AspectWithCollectionOfSimpleType.class );

      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( aspectWithCollectionOfSimpleType.getTestList() ).hasSizeBetween( 1, 5 );
   }

   @Test
   void testGenerateJsonForAspectWithEitherType() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_EITHER );
      final AspectWithEither aspectWithEither = parseJson( generatedJson, AspectWithEither.class );
      final Condition<AspectWithEither> isLeft = new Condition<>( x -> x.getEither().getLeft() != null, "left is present" );
      final Condition<AspectWithEither> isRight = new Condition<>( x -> x.getEither().getRight() != null, "right is present" );
      assertThat( aspectWithEither ).is( anyOf( isLeft, isRight ) );
   }

   @Test
   void testGenerateJsonForAspectWithCollectionWithElementCharacteristicAndEither() {
      final AspectModelUrn namespace = TestAspect.ASPECT.getUrn();
      final Aspect aspect = aspect( TestAspect.ASPECT.getUrn() )
            .property( property( namespace.withName( "property" ) )
                  .characteristic( list( namespace.withName( "List" ) )
                        .elementCharacteristic( either( namespace.withName( "Either" ) )
                              .left( samm_c.Text )
                              .right( samm_c.MultiLanguageText )
                              .build() )
                        .build() )
                  .build() )
            .build();

      assertThatCode( () -> {
         generateJsonForModel( aspect );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testGenerateJsonForAspectWithComplexCollectionOfMinSize2AndExampleValue() throws IOException {
      // We generate a payload for the set of entities, where the Entity's single property has an example value and the
      // set has a minimum size of 2. This means that the exampleValue may not reused for every entry.
      // We parse the generated JSON using a List instead of a Set though, so that Jackson doesn't "solve" the problem
      // by swallowing duplicate values: We want to ensure that the JSON does indeed not contain two duplicate entries
      // (i.e., keeps the "set" semantics)
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COMPLEX_SET );
      final AspectWithComplexSetAsList aspectWithComplexSet = parseJson( generatedJson, AspectWithComplexSetAsList.class );
      final List<URI> list = aspectWithComplexSet.getTestProperty().stream().map( Id::getProductId ).toList();
      assertThat( list ).hasSizeGreaterThan( 1 );
      assertThat( list.get( 0 ) ).isNotEqualTo( list.get( 1 ) );
   }

   @Test
   void testGenerateJsonForAspectWithEnumHavingNestedEntities() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENUM_HAVING_NESTED_ENTITIES );

      final AspectWithEnumHavingNestedEntities aspectWithEnum = parseJson( generatedJson, AspectWithEnumHavingNestedEntities.class );

      assertThat( aspectWithEnum.getSimpleResult().getValue() ).isEqualTo( "Yes" );
      final AspectWithEnumHavingNestedEntities.DetailEntity details = aspectWithEnum.getResult().getValue().getDetails();
      assertThat( details.getDescription() ).isEqualTo( "Result succeeded" );
      assertThat( details.getMessage() ).isEqualTo( "Evaluation succeeded." );
      assertThat( details.getNumericCode() ).isEqualTo( Short.valueOf( "10" ) );
   }

   @Test
   void testGenerateJsonForAspectWithEntityEnumerationAndLangString() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING );

      final AspectWithEntityEnumerationAndLangString aspectWithEnum = parseJson( generatedJson,
            AspectWithEntityEnumerationAndLangString.class );

      assertThat( aspectWithEnum.getTestProperty().getValue().getEntityProperty().get( "en" ) ).isEqualTo( "This is a test." );
   }

   @Test
   void testGenerateJsonForAspectWithComplexEnum() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COMPLEX_ENUM );

      final AspectWithEnum aspectWithEnum = parseJson( generatedJson, AspectWithEnum.class );

      assertThat( aspectWithEnum.getSimpleResult() ).isEqualTo( "Yes" );
      assertThat( aspectWithEnum.getResult().getDescription() ).isNotBlank();
      assertThat( aspectWithEnum.getResult().getNumericCode() ).isNotZero();
   }

   @Test
   void testGenerateJsonForAspectWithComplextEntityCollectionEnum() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM );

      final AspectWithComplexEntityCollectionEnum aspectWithComplexEntityCollectionEnum = parseJson( generatedJson,
            AspectWithComplexEntityCollectionEnum.class );
      final List<AspectWithComplexEntityCollectionEnum.MyEntityTwo> myEntityTwoList = aspectWithComplexEntityCollectionEnum
            .getMyPropertyOne().getValue()
            .getEntityPropertyOne();
      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( myEntityTwoList ).hasSize( 1 );
      assertThat( myEntityTwoList.get( 0 ).getEntityPropertyTwo() ).isEqualTo( "foo" );
   }

   @Test
   void testGenerateJsonForAspectWithMultipleEntitiesComplexEitherType() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_AND_EITHER );
      final AspectWithMultipleEntitiesAndEither aspectWithCollectionOfSimpleType = parseJson( generatedJson,
            AspectWithMultipleEntitiesAndEither.class );
      assertTestEntityWithSimpleTypes( aspectWithCollectionOfSimpleType.getTestEntityOne() );
      assertTestEntityWithSimpleTypes( aspectWithCollectionOfSimpleType.getTestEntityTwo() );
      final TestEntityWithSimpleTypes left = aspectWithCollectionOfSimpleType.getTestEitherProperty().getLeft();
      final TestEntityWithSimpleTypes right = aspectWithCollectionOfSimpleType.getTestEitherProperty().getRight();
      final TestEntityWithSimpleTypes testEntityWithSimpleTypes = Optional.ofNullable( left ).orElse( right );
      assertTestEntityWithSimpleTypes( testEntityWithSimpleTypes );
   }

   @Test
   void testGenerateJsonForAspectWithMultipleCollectionsOfEntities() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITY_COLLECTIONS );

      final AspectWithMultipleEntityCollections aspectWithEntityCollection = parseJson( generatedJson,
            AspectWithMultipleEntityCollections.class );

      assertThat( aspectWithEntityCollection.getTestListOne() ).hasSizeBetween( 1, 5 );
      assertThat( aspectWithEntityCollection.getTestListTwo() ).hasSizeBetween( 1, 5 );

      TestEntityWithSimpleTypes testEntityWithSimpleTypes = aspectWithEntityCollection.getTestListOne().get( 0 );
      assertTestEntityWithSimpleTypes( testEntityWithSimpleTypes );

      testEntityWithSimpleTypes = aspectWithEntityCollection.getTestListTwo().get( 0 );
      assertTestEntityWithSimpleTypes( testEntityWithSimpleTypes );
   }

   @Test
   void testGenerateAspectForCurie() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_CURIE );
      final AspectWithCurie aspectWithCurie = parseJson( generatedJson, AspectWithCurie.class );
      assertThat( aspectWithCurie.getTestCurie() ).isEqualTo( "unit:hectopascal" );
      assertThat( aspectWithCurie.getTestCurieWithoutExampleValue() ).startsWith( "unit" );
   }

   @Test
   void testGenerateAspectWithMultiLanguageText() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT );
      final AspectWithMultiLanguageText aspectWithMultiLanguageText = parseJson( generatedJson, AspectWithMultiLanguageText.class );
      assertThat( aspectWithMultiLanguageText.getProp() ).matches( langString ->
            langString.getLanguageTag().equals( Locale.ENGLISH ) || langString.getLanguageTag().equals( Locale.GERMAN ) );
   }

   @Test
   void testGenerateAspectWithMultiLanguageExampleValue() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTILANGUAGE_EXAMPLE_VALUE );
      final AspectWithMultilanguageExampleValue aspectWithMultiLanguageText = parseJson( generatedJson,
            AspectWithMultilanguageExampleValue.class );
      assertThat( aspectWithMultiLanguageText.getProp() ).matches( langString ->
            langString.getValue().equals( "Multilanguage example value." ) && langString.getLanguageTag().equals( Locale.GERMAN ) );
   }

   @Test
   void testGenerateAspectWithConstraint() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_CONSTRAINT );
      final AspectWithConstraintProperties aspectWithConstraint = parseJson( generatedJson, AspectWithConstraintProperties.class );
      assertThat( aspectWithConstraint.getStringLcProperty().length() ).isBetween( 20, 22 );
      assertThat( aspectWithConstraint.getBigIntRcProperty().intValue() ).isBetween( 10, 15 );
      assertThat( aspectWithConstraint.getDoubleRcProperty() ).isBetween( -0.1, 0.2 );
      assertThat( aspectWithConstraint.getFloatRcProperty() ).isBetween( 100f, 112f );
      assertThat( aspectWithConstraint.getIntRcProperty() ).isBetween( -1, -1 );
      assertThat( aspectWithConstraint.getStringRegexcProperty() ).matches( "[a-zA-Z]" );
   }

   @Test
   void testGenerateAspectWithConstraints() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_CONSTRAINTS );
      final AspectWithConstraints aspectWithConstraints = parseJson( generatedJson, AspectWithConstraints.class );
      assertThat( aspectWithConstraints.getTestPropertyCollectionLengthConstraint().size() ).isBetween( 1, 10 );
      assertThat( aspectWithConstraints.getTestPropertyWithMinLengthConstraint() ).isGreaterThanOrEqualTo( BigInteger.ONE );
      assertThat( aspectWithConstraints.getTestPropertyWithMinMaxLengthConstraint().length() ).isBetween( 1, 10 );
      assertThat( aspectWithConstraints.getTestPropertyRangeConstraintWithDoubleType() ).isBetween( 1.0, 10.0 );
      assertThat( aspectWithConstraints.getTestPropertyRangeConstraintWithFloatType() ).isBetween( 1.0f, 10.0f );
      assertThat( aspectWithConstraints.getTestPropertyWithMinRangeConstraint() ).isGreaterThanOrEqualTo( 1 );
      assertThat( aspectWithConstraints.getTestPropertyWithMinMaxRangeConstraint() ).isBetween( 1, 10 );
      assertThat( aspectWithConstraints.getTestPropertyWithDecimalMaxRangeConstraint() ).isLessThanOrEqualTo( BigDecimal.valueOf( 10.5 ) );
      assertThat( aspectWithConstraints.getTestPropertyWithDecimalMinDecimalMaxRangeConstraint() ).isBetween( BigDecimal.valueOf( 2.3 ),
            BigDecimal.valueOf( 10.5 ) );
      assertThat( aspectWithConstraints.getTestPropertyWithRegularExpression() ).matches( "^[a-zA-Z]\\.[0-9]" );
   }

   @Test
   void testGenerateAspectWithStructuredValue() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_STRUCTURED_VALUE );
      final AspectWithStructuredValue aspectWithStructuredValue = parseJson( generatedJson, AspectWithStructuredValue.class );
      assertThat( aspectWithStructuredValue.getDate().toString() ).isEqualTo( "2019-09-27" );
   }

   @Test
   void testGenerateAspectWithDateTimeTypeForRangeConstraints() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_G_TYPE_FOR_RANGE_CONSTRAINTS );
      final AspectWithGTypeForRangeConstraints aspectWithGtypeForRangeConstraints = parseJson( generatedJson,
            AspectWithGTypeForRangeConstraints.class );
      final Pattern dayPattern = Pattern.compile( "---(0[1-9]|[12][0-9]|3[01])(Z|([+\\-])((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" );
      final Pattern monthPattern = Pattern.compile( "--(0[1-9]|1[0-2])(Z|([+\\-])((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" );
      assertThat( dayPattern.matcher( aspectWithGtypeForRangeConstraints.getTestPropertyWithGDay().toString() ) ).matches();
      assertThat( monthPattern.matcher( aspectWithGtypeForRangeConstraints.getTestPropertyWithGMonth().toString() ) ).matches();
   }

   @Test
   void testGenerateJsonForAspectWithoutMinMaxIntegerValueOnRangeConstraint() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_INTEGER_VALUE );

      final AspectWithRangeConstraintWithoutMinMaxIntegerValue aspectWithSimpleProperties = parseJson( generatedJson,
            AspectWithRangeConstraintWithoutMinMaxIntegerValue.class );

      assertThat( aspectWithSimpleProperties.getTestInt() ).isNotNull();
   }

   @Test
   void testGenerateJsonForAspectWithoutMinMaxDoubleValueOnRangeConstraint() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_DOUBLE_VALUE );

      final AspectWithRangeConstraintWithoutMinMaxDoubleValue aspectWithRangeConstraintWithoutMinMaxDoubleValue =
            parseJson( generatedJson, AspectWithRangeConstraintWithoutMinMaxDoubleValue.class );

      assertThat( aspectWithRangeConstraintWithoutMinMaxDoubleValue.getDoubleProperty() ).isNotNull();
   }

   @Test
   void testGenerateJsonForAspectWithEntityEnumerationAndNotInPayloadProperties() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES );

      final AspectWithEntityEnumerationAndNotInPayloadProperties aspectWithEntityAndNoInPayloadProperty =
            parseJson( generatedJson, AspectWithEntityEnumerationAndNotInPayloadProperties.class );
      assertThat( aspectWithEntityAndNoInPayloadProperty.getSystemState().getValue().getState() ).isEqualTo( (short) 1 );
   }

   @Test
   void testGenerateJsonForAspectWithExtendedEnumsWithNotInPayloadProperty() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_EXTENDED_ENUMS_WITH_NOT_IN_PAYLOAD_PROPERTY );

      final AspectWithExtendedEnumsWithNotInPayloadProperty aspectWithExtendedEnumsWithNotInPayloadProperty =
            parseJson( generatedJson, AspectWithExtendedEnumsWithNotInPayloadProperty.class );
      assertThat( aspectWithExtendedEnumsWithNotInPayloadProperty.getResult().getValue().getDescription() ).isEqualTo( "No status" );
   }

   @Test
   void testGenerateJsonForAspectWithPropertyWithPayloadName() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME );

      final AspectWithPropertyWithPayloadName aspectWithPropertyWithPayloadName = parseJson( generatedJson,
            AspectWithPropertyWithPayloadName.class );
      assertThat( aspectWithPropertyWithPayloadName.getTest() ).isNotEmpty();
   }

   @Test
   void testGenerateJsonForAspectWithAbstractEntity() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY );

      final AspectWithAbstractEntity aspectWithAbstractEntity = parseJson( generatedJson, AspectWithAbstractEntity.class );
      final ExtendingTestEntity testProperty = aspectWithAbstractEntity.getTestProperty();
      assertThat( testProperty ).isNotNull();
      assertThat( testProperty.getAbstractTestProperty() ).isNotNull();
      assertThat( testProperty.getEntityProperty() ).isNotBlank();
   }

   @Test
   void testGenerateJsonForAspectWithCollectionWithAbstractEntity() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY );

      final AspectWithCollectionWithAbstractEntity aspectWithCollectionWithAbstractEntity = parseJson( generatedJson,
            AspectWithCollectionWithAbstractEntity.class );
      final Collection<AbstractTestEntity> testProperty = aspectWithCollectionWithAbstractEntity.getTestProperty();
      assertThat( testProperty ).isNotEmpty();
      final ExtendingTestEntity extendingTestEntity = (ExtendingTestEntity) testProperty.iterator().next();

      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( extendingTestEntity.getAbstractTestProperty() ).isNotNull();
      assertThat( extendingTestEntity.getEntityProperty() ).isNotBlank();
   }

   @Test
   void testGenerateJsonForAspectWithAbstractSingleEntity() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY );

      final AspectWithAbstractSingleEntity aspectWithAbstractSingleEntity = parseJson( generatedJson,
            AspectWithAbstractSingleEntity.class );
      final ExtendingTestEntity extendingTestEntity = (ExtendingTestEntity) aspectWithAbstractSingleEntity.getTestProperty();
      assertThat( extendingTestEntity.getAbstractTestProperty() ).isNotNull();
      assertThat( extendingTestEntity.getEntityProperty() ).isNotBlank();
   }

   @Test
   void testGenerateJsonForAspectWithConstrainedSetProperty() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_CONSTRAINED_SET );
      final AspectWithConstrainedSet aspectWithConstrainedSet = parseJson( generatedJson, AspectWithConstrainedSet.class );
      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( aspectWithConstrainedSet.getTestProperty() ).hasSizeGreaterThan( 0 );
   }

   @Test
   void testGenerateJsonForAspectWithComplexSet() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COMPLEX_SET );
      final AspectWithComplexSet aspectWithComplexSet = parseJson( generatedJson, AspectWithComplexSet.class );
      assertThat( aspectWithComplexSet.getTestProperty() ).hasSizeBetween( 1, 5 );

      final List<Id> list = aspectWithComplexSet.getTestProperty().stream().toList();
      // Entries are pair-wise non-equal
      IntStream.range( 1, list.size() )
            .mapToObj( i -> Map.entry( list.get( i - 1 ), list.get( i ) ) )
            .forEach( entry -> {
               assertThat( entry.getKey() ).isNotEqualTo( entry.getValue() );
            } );
   }

   @Test
   void testGenerateJsonForAspectWithFixedPointConstraint() throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_FIXED_POINT_CONSTRAINT );
      final AspectWithFixedPointConstraint aspectWithConstraint = parseJson( generatedJson, AspectWithFixedPointConstraint.class );
      assertThat( generatedJson ).contains( "testProperty" );
      assertThat( aspectWithConstraint.getTestProperty() ).matches( "\\s*\\d{3}\\.\\d{4,5}" );
   }

   private String generateJsonForModel( final Aspect aspect ) {
      final AspectModelJsonPayloadGenerator jsonGenerator = new AspectModelJsonPayloadGenerator( aspect );
      return jsonGenerator.generateJson();
   }

   private String generateJsonForModel( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      return generateJsonForModel( aspect );
   }

   private void assertTestEntityWithSimpleTypes( final TestEntityWithSimpleTypes testEntityWithSimpleTypes ) {
      assertThat( testEntityWithSimpleTypes.getRandomValue() ).isNotBlank();
      assertThat( testEntityWithSimpleTypes.getRandomValue() ).isNotNull();
      assertThat( testEntityWithSimpleTypes.getTestFloat() ).isEqualTo( 2.25f );
      assertThat( testEntityWithSimpleTypes.getTestLocalDateTime() ).isEqualTo(
            datatypeFactory.newXMLGregorianCalendar( "2018-02-28T14:23:32.918Z" ) );
      assertThat( testEntityWithSimpleTypes.getTestInt() ).isEqualTo( 3 );
      assertThat( testEntityWithSimpleTypes.getTestString() ).isEqualTo( "Example Value Test" );
   }

   private ObjectMapper objectMapper() {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule( new JavaTimeModule() );
      mapper.registerModule( new Jdk8Module() );
      mapper.registerModule( new AspectModelJacksonModule() );
      mapper.configure( JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION, true );
      mapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
      mapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
      return mapper;
   }

   private <T> T parseJson( final String json, final Class<T> targetClass ) throws IOException {
      return objectMapper().readValue( json, targetClass );
   }
}
