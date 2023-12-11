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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Percentage;
import org.eclipse.esmf.aspectmodel.generator.NumericTypeTraits;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AbstractTestEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithAbstractEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithAbstractSingleEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithCollectionOfSimpleType;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithCollectionWithAbstractEntity;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithComplexEntityCollectionEnum;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithComplexSet;
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
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithGTypeForRangeConstraints;
import org.eclipse.esmf.aspectmodel.generator.json.testclasses.AspectWithGenericNumericProperty;
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
import org.eclipse.esmf.aspectmodel.resolver.services.DataType;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.characteristic.impl.DefaultTrait;
import org.eclipse.esmf.constraint.RangeConstraint;
import org.eclipse.esmf.constraint.impl.DefaultRangeConstraint;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectContext;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.impl.BoundDefinition;
import org.eclipse.esmf.metamodel.impl.DefaultAspect;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;

import io.vavr.collection.HashMap;

public class AspectModelJsonPayloadGeneratorTest extends MetaModelVersions {
   private static DatatypeFactory datatypeFactory;

   private final Random random = new Random();

   @BeforeAll
   static void setup() {
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch ( final DatatypeConfigurationException exception ) {
         throw new RuntimeException( exception );
      }
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithCollectionOfEntities( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENTITY_LIST, metaModelVersion );

      final AspectWithEntityCollection aspectWithEntityCollection = parseJson( generatedJson, AspectWithEntityCollection.class );

      assertThat( aspectWithEntityCollection.getTestList() ).hasSize( 1 );

      final TestEntityWithSimpleTypes testEntityWithSimpleTypes = aspectWithEntityCollection.getTestList().get( 0 );

      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertTestEntityWithSimpleTypes( testEntityWithSimpleTypes );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithSimpleProperties( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES, metaModelVersion );

      final AspectWithSimpleProperties aspectWithSimpleProperties = parseJson( generatedJson, AspectWithSimpleProperties.class );

      assertThat( aspectWithSimpleProperties.getRandomValue() ).isNotBlank();
      assertThat( aspectWithSimpleProperties.getRandomValue() ).isNotNull();
      assertThat( aspectWithSimpleProperties.getTestFloat() ).isEqualTo( 2.25f );
      assertThat( aspectWithSimpleProperties.getTestLocalDateTime() ).isEqualTo( datatypeFactory.newXMLGregorianCalendar( "2018-02-28T14:23:32.918" ) );
      assertThat( aspectWithSimpleProperties.getTestInt() ).isEqualTo( 3 );
      assertThat( aspectWithSimpleProperties.getTestString() ).isEqualTo( "Example Value Test" );

      assertThat( aspectWithSimpleProperties.getTestLocalDateTimeWithoutExample() ).isNotNull();
      assertThat( aspectWithSimpleProperties.getTestDurationWithoutExample() ).isNotNull();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithStateType( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_SIMPLE_PROPERTIES_AND_STATE, metaModelVersion );

      final AspectWithSimpleTypesAndState aspectWithSimpleTypes = parseJson( generatedJson, AspectWithSimpleTypesAndState.class );
      assertThat( aspectWithSimpleTypes.getRandomValue() ).isNotBlank();
      assertThat( aspectWithSimpleTypes.getTestFloat() ).isEqualTo( 2.25f );
      assertThat( aspectWithSimpleTypes.getTestLocalDateTime() ).isEqualTo( datatypeFactory.newXMLGregorianCalendar( "2018-02-28T14:23:32.918" ) );
      assertThat( aspectWithSimpleTypes.getTestInt() ).isEqualTo( 3 );
      assertThat( aspectWithSimpleTypes.getTestString() ).isEqualTo( "Example Value Test" );
      assertThat( aspectWithSimpleTypes.getAutomationProperty() ).isEqualTo( "Automation Default Prop" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithEntity( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENTITY_WITH_MULTIPLE_PROPERTIES, metaModelVersion );

      final AspectWithEntity aspectWithEntity = parseJson( generatedJson, AspectWithEntity.class );
      assertTestEntityWithSimpleTypes( aspectWithEntity.getTestEntity() );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithRecursivePropertyWithOptional( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL, metaModelVersion );
      final AspectWithRecursivePropertyWithOptional aspectWithRecursivePropertyWithOptional = parseJson( generatedJson,
            AspectWithRecursivePropertyWithOptional.class );
      assertThat( aspectWithRecursivePropertyWithOptional.getTestProperty().getTestProperty().get().getTestProperty() ).isNotPresent();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithMultipleEntities( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES, metaModelVersion );

      final AspectWithMultipleEntities aspectWithMultipleEntities = parseJson( generatedJson, AspectWithMultipleEntities.class );
      assertTestEntityWithSimpleTypes( aspectWithMultipleEntities.getTestEntityOne() );
      assertTestEntityWithSimpleTypes( aspectWithMultipleEntities.getTestEntityTwo() );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithNestedEntity( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_NESTED_ENTITY, metaModelVersion );

      final AspectWithNestedEntity aspectWithNestedEntity = parseJson( generatedJson, AspectWithNestedEntity.class );

      final Entity entity = aspectWithNestedEntity.getEntity();
      assertThat( entity.getTestString() ).isEqualTo( "Example Value Test" );

      final NestedEntity nestedEntity = entity.getNestedEntity();
      assertThat( nestedEntity.getTestString() ).isEqualTo( "Example Value Test" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithMultipleCollectionsOfSimpleType( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTIPLE_COLLECTIONS_OF_SIMPLE_TYPE, metaModelVersion );

      final AspectWithMultipleCollectionsOfSimpleType aspectWithCollectionOfSimpleType = parseJson( generatedJson,
            AspectWithMultipleCollectionsOfSimpleType.class );

      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( aspectWithCollectionOfSimpleType.getTestListInt() ).containsExactly( 35 );
      assertThat( aspectWithCollectionOfSimpleType.getTestListString() ).containsExactly( "test string" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithCollectionOfSimpleType( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE, metaModelVersion );

      final AspectWithCollectionOfSimpleType aspectWithCollectionOfSimpleType = parseJson( generatedJson, AspectWithCollectionOfSimpleType.class );

      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( aspectWithCollectionOfSimpleType.getTestList() ).containsExactly( 35 );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithEitherType( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_EITHER, metaModelVersion );
      final AspectWithEither aspectWithEither = parseJson( generatedJson, AspectWithEither.class );
      assertThat( aspectWithEither.getEither().getLeft() ).isNotBlank();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithEnumHavingNestedEntities( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENUM_HAVING_NESTED_ENTITIES, metaModelVersion );

      final AspectWithEnumHavingNestedEntities aspectWithEnum = parseJson( generatedJson, AspectWithEnumHavingNestedEntities.class );

      assertThat( aspectWithEnum.getSimpleResult().getValue() ).isEqualTo( "Yes" );
      final AspectWithEnumHavingNestedEntities.DetailEntity details = aspectWithEnum.getResult().getValue().getDetails();
      assertThat( details.getDescription() ).isEqualTo( "Result succeeded" );
      assertThat( details.getMessage() ).isEqualTo( "Evaluation succeeded." );
      assertThat( details.getNumericCode() ).isEqualTo( Short.valueOf( "10" ) );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithEntityEnumerationAndLangString( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING, metaModelVersion );

      final AspectWithEntityEnumerationAndLangString aspectWithEnum = parseJson( generatedJson, AspectWithEntityEnumerationAndLangString.class );

      assertThat( aspectWithEnum.getTestProperty().getValue().getEntityProperty().get( "en" ) ).isEqualTo( "This is a test." );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithComplexEnum( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COMPLEX_ENUM, metaModelVersion );

      final AspectWithEnum aspectWithEnum = parseJson( generatedJson, AspectWithEnum.class );

      assertThat( aspectWithEnum.getSimpleResult() ).isEqualTo( "Yes" );
      assertThat( aspectWithEnum.getResult().getDescription() ).isNotBlank();
      assertThat( aspectWithEnum.getResult().getNumericCode() ).isNotZero();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithComplextEntityCollectionEnum( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COMPLEX_ENTITY_COLLECTION_ENUM, metaModelVersion );

      final AspectWithComplexEntityCollectionEnum aspectWithComplexEntityCollectionEnum = parseJson( generatedJson,
            AspectWithComplexEntityCollectionEnum.class );
      final List<AspectWithComplexEntityCollectionEnum.MyEntityTwo> myEntityTwoList = aspectWithComplexEntityCollectionEnum
            .getMyPropertyOne().getValue()
            .getEntityPropertyOne();
      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( myEntityTwoList ).hasSize( 1 );
      assertThat( myEntityTwoList.get( 0 ).getEntityPropertyTwo() ).isEqualTo( "foo" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithMultipleEntitiesComplexEitherType( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITIES_AND_EITHER, metaModelVersion );
      final AspectWithMultipleEntitiesAndEither aspectWithCollectionOfSimpleType = parseJson( generatedJson, AspectWithMultipleEntitiesAndEither.class );
      assertTestEntityWithSimpleTypes( aspectWithCollectionOfSimpleType.getTestEntityOne() );
      assertTestEntityWithSimpleTypes( aspectWithCollectionOfSimpleType.getTestEntityTwo() );
      assertTestEntityWithSimpleTypes( aspectWithCollectionOfSimpleType.getTestEitherProperty().getLeft() );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithMultipleCollectionsOfEntities( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTIPLE_ENTITY_COLLECTIONS, metaModelVersion );

      final AspectWithMultipleEntityCollections aspectWithEntityCollection = parseJson( generatedJson, AspectWithMultipleEntityCollections.class );

      assertThat( aspectWithEntityCollection.getTestListOne() ).hasSize( 1 );
      assertThat( aspectWithEntityCollection.getTestListTwo() ).hasSize( 1 );

      TestEntityWithSimpleTypes testEntityWithSimpleTypes = aspectWithEntityCollection.getTestListOne().get( 0 );
      assertTestEntityWithSimpleTypes( testEntityWithSimpleTypes );

      testEntityWithSimpleTypes = aspectWithEntityCollection.getTestListTwo().get( 0 );
      assertTestEntityWithSimpleTypes( testEntityWithSimpleTypes );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateAspectForCurie( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_CURIE, metaModelVersion );
      final AspectWithCurie aspectWithCurie = parseJson( generatedJson, AspectWithCurie.class );
      assertThat( aspectWithCurie.getTestCurie() ).isEqualTo( "unit:hectopascal" );
      assertThat( aspectWithCurie.getTestCurieWithoutExampleValue() ).startsWith( "unit" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateAspectWithMultiLanguageText( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT, metaModelVersion );
      final AspectWithMultiLanguageText aspectWithMultiLanguageText = parseJson( generatedJson, AspectWithMultiLanguageText.class );
      final Condition<LangString> isEnglishLangString = new Condition<>( l -> l.getLanguageTag().equals( Locale.ENGLISH ), "is english" );
      assertThat( aspectWithMultiLanguageText.getProp() ).has( isEnglishLangString );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateAspectWithMultiLanguageExampleValue( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_MULTILANGUAGE_EXAMPLE_VALUE, metaModelVersion );
      final AspectWithMultilanguageExampleValue aspectWithMultiLanguageText = parseJson( generatedJson, AspectWithMultilanguageExampleValue.class );
      final Condition<LangString> isGermanLangString = new Condition<>( l -> l.getLanguageTag().equals( Locale.GERMAN ), "is german" );
      assertThat( aspectWithMultiLanguageText.getProp() ).has( isGermanLangString );
      final Condition<LangString> text = new Condition<>( l -> l.getValue().equals( "Multilanguage example value." ), "is equal" );
      assertThat( aspectWithMultiLanguageText.getProp() ).has( text );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateAspectWithConstraint( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_CONSTRAINT, metaModelVersion );
      final AspectWithConstraintProperties aspectWithConstraint = parseJson( generatedJson, AspectWithConstraintProperties.class );
      assertThat( aspectWithConstraint.getStringLcProperty().length() ).isBetween( 20, 22 );
      assertThat( aspectWithConstraint.getBigIntRcProperty().intValue() ).isBetween( 10, 15 );
      assertThat( aspectWithConstraint.getDoubleRcProperty() ).isBetween( -0.1, 0.2 );
      assertThat( aspectWithConstraint.getFloatRcProperty() ).isBetween( 100f, 112f );
      assertThat( aspectWithConstraint.getIntRcProperty() ).isBetween( -1, -1 );
      assertThat( aspectWithConstraint.getStringRegexcProperty() ).matches( "[a-zA-Z]" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateAspectWithConstraints( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_CONSTRAINTS, metaModelVersion );
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

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateAspectWithStructuredValue( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_STRUCTURED_VALUE, metaModelVersion );
      final AspectWithStructuredValue aspectWithStructuredValue = parseJson( generatedJson, AspectWithStructuredValue.class );
      assertThat( aspectWithStructuredValue.getDate().toString() ).isEqualTo( "2019-09-27" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateAspectWithDateTimeTypeForRangeConstraints( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_G_TYPE_FOR_RANGE_CONSTRAINTS, metaModelVersion );
      final AspectWithGTypeForRangeConstraints aspectWithGTypeForRangeConstraints = parseJson( generatedJson, AspectWithGTypeForRangeConstraints.class );
      final Pattern dayPattern = Pattern.compile( "---(0[1-9]|[12][0-9]|3[01])(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" );
      final Pattern monthPattern = Pattern.compile( "--(0[1-9]|1[0-2])(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" );
      assertTrue( dayPattern.matcher( aspectWithGTypeForRangeConstraints.getTestPropertyWithGDay().toString() ).matches() );
      assertTrue( monthPattern.matcher( aspectWithGTypeForRangeConstraints.getTestPropertyWithGMonth().toString() ).matches() );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithoutMinMaxIntegerValueOnRangeConstraint( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_INTEGER_VALUE, metaModelVersion );

      final AspectWithRangeConstraintWithoutMinMaxIntegerValue aspectWithSimpleProperties = parseJson( generatedJson,
            AspectWithRangeConstraintWithoutMinMaxIntegerValue.class );

      assertThat( aspectWithSimpleProperties.getTestInt() ).isNotNull();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithoutMinMaxDoubleValueOnRangeConstraint( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_DOUBLE_VALUE, metaModelVersion );

      final AspectWithRangeConstraintWithoutMinMaxDoubleValue aspectWithRangeConstraintWithoutMinMaxDoubleValue =
            parseJson( generatedJson, AspectWithRangeConstraintWithoutMinMaxDoubleValue.class );

      assertThat( aspectWithRangeConstraintWithoutMinMaxDoubleValue.getDoubleProperty() ).isNotNull();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithEntityEnumerationAndNotInPayloadProperties( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES, metaModelVersion );

      final AspectWithEntityEnumerationAndNotInPayloadProperties aspectWithEntityAndNoInPayloadProperty =
            parseJson( generatedJson, AspectWithEntityEnumerationAndNotInPayloadProperties.class );
      assertThat( aspectWithEntityAndNoInPayloadProperty.getSystemState().getValue().getState() ).isEqualTo( (short) 1 );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithExtendedEnumsWithNotInPayloadProperty( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_EXTENDED_ENUMS_WITH_NOT_IN_PAYLOAD_PROPERTY, metaModelVersion );

      final AspectWithExtendedEnumsWithNotInPayloadProperty aspectWithExtendedEnumsWithNotInPayloadProperty =
            parseJson( generatedJson, AspectWithExtendedEnumsWithNotInPayloadProperty.class );
      assertThat( aspectWithExtendedEnumsWithNotInPayloadProperty.getResult().getValue().getDescription() ).isEqualTo( "No status" );
   }

   @ParameterizedTest
   @MethodSource( value = "rangeTestSource" )
   void testGeneratedNumbersAreWithinRange( final RDFDatatype numericModelType, final Optional<BoundDefinition> boundKind ) {
      // number types are the same in all versions of the meta model, so we do not need to iterate over all of them,
      // just take the latest one
      final KnownVersion modelVersion = KnownVersion.SAMM_1_0_0;

      final Type numericType = new DefaultScalar( numericModelType.getURI(), modelVersion );
      final Resource dataTypeResource = ResourceFactory.createResource( numericType.getUrn() );
      final Class<?> nativeType = DataType.getJavaTypeForMetaModelType( dataTypeResource, modelVersion );
      final Pair<Number, Number> randomRange = generateRandomRangeForType( numericType, nativeType, boundKind.orElse( null ) );

      final Aspect dynamicAspect = createAspectWithDynamicNumericProperty( modelVersion, numericType, boundKind.orElse( null ), randomRange );
      final AspectModelJsonPayloadGenerator randomGenerator = new AspectModelJsonPayloadGenerator( dynamicAspect );
      final AspectModelJsonPayloadGenerator minGenerator = new AspectModelJsonPayloadGenerator( dynamicAspect, new MinValueRandomStrategy() );
      final AspectModelJsonPayloadGenerator maxGenerator = new AspectModelJsonPayloadGenerator( dynamicAspect, new MaxValueRandomStrategy() );
      try {
         final String generatedJson = randomGenerator.generateJson();
         final String minValue = minGenerator.generateJson();
         final String maxValue = maxGenerator.generateJson();
         System.out.printf( "%s: %s, min: %s, max: %s, random: %s%n", READABLE_RANGE_TYPE.get( boundKind.orElse( null ) ).get(),
               randomRange, minValue, maxValue, generatedJson );
         final AspectWithGenericNumericProperty validator = parseJson( generatedJson, AspectWithGenericNumericProperty.class );
         final AspectWithGenericNumericProperty minValidator = parseJson( minValue, AspectWithGenericNumericProperty.class );
         final AspectWithGenericNumericProperty maxValidator = parseJson( maxValue, AspectWithGenericNumericProperty.class );

         assertNumberInRange( validator.getTestNumber(), randomRange, boundKind.orElse( null ) );
         assertMinValue( minValidator.getTestNumber(), randomRange.getLeft(), boundKind.orElse( null ) );
         assertMaxValue( maxValidator.getTestNumber(), randomRange.getRight(), boundKind.orElse( null ) );
      } catch ( final IOException e ) {
         throw new RuntimeException( e );
      }
   }

   private void assertNumberInRange( final Number value, final Pair<Number, Number> range, final BoundDefinition boundKind ) {
      assertThat( value ).isNotNull();
      final BigDecimal numberValue = NumericTypeTraits.convertToBigDecimal( value );
      final BigDecimal lowerBound = NumericTypeTraits.convertToBigDecimal( range.getLeft() );
      final BigDecimal upperBound = NumericTypeTraits.convertToBigDecimal( range.getRight() );
      if ( BoundDefinition.GREATER_THAN.equals( boundKind ) ) {
         // exclusive range
         assertThat( numberValue ).isStrictlyBetween( lowerBound, upperBound );
      } else {
         // inclusive range
         assertThat( numberValue ).isBetween( lowerBound, upperBound );
      }
   }

   private void assertMinValue( final Number value, final Number rangeMinValue, final BoundDefinition boundKind ) {
      assertThat( value ).isNotNull();

      // for easy comparison
      final BigDecimal numberValue = NumericTypeTraits.convertToBigDecimal( value );
      final BigDecimal lowerBound = NumericTypeTraits.convertToBigDecimal( rangeMinValue );
      if ( BoundDefinition.GREATER_THAN.equals( boundKind ) ) {
         // exclusive range
         assertThat( numberValue ).isGreaterThan( lowerBound );
      } else {
         // inclusive range
         if ( NumericTypeTraits.isFloatingPointNumberType( rangeMinValue.getClass() ) ) {
            assertThat( numberValue ).isCloseTo( lowerBound, Percentage.withPercentage( 0.1 ) );
         } else {
            assertThat( numberValue ).isEqualByComparingTo( lowerBound );
         }
      }
   }

   private void assertMaxValue( final Number value, final Number rangeMaxValue, final BoundDefinition boundKind ) {
      assertThat( value ).isNotNull();

      // for easy comparison
      final BigDecimal numberValue = NumericTypeTraits.convertToBigDecimal( value );
      final BigDecimal upperBound = NumericTypeTraits.convertToBigDecimal( rangeMaxValue );

      // because we do not have enough significant digits in our random generator (Double), we cannot properly test all Long cases
      if ( upperBound.compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) > 0 ) {
         return;
      }
      if ( BoundDefinition.GREATER_THAN.equals( boundKind ) ) {
         // exclusive range
         assertThat( numberValue ).isLessThan( upperBound );
      } else {
         // inclusive range
         if ( NumericTypeTraits.isFloatingPointNumberType( rangeMaxValue.getClass() ) ) {
            assertThat( numberValue ).isCloseTo( upperBound, Percentage.withPercentage( 0.1 ) );
         } else {
            assertThat( numberValue ).isEqualByComparingTo( upperBound );
         }
      }
   }

   private static final HashMap<BoundDefinition, String> READABLE_RANGE_TYPE = HashMap.of(
         null, "Native range",
         BoundDefinition.OPEN, "Open range",
         BoundDefinition.AT_LEAST, "Inclusive range",
         BoundDefinition.GREATER_THAN, "Exclusive range"
   );

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testGenerateJsonForAspectWithPropertyWithPayloadName( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME, metaModelVersion );

      final AspectWithPropertyWithPayloadName aspectWithPropertyWithPayloadName = parseJson( generatedJson, AspectWithPropertyWithPayloadName.class );
      assertThat( aspectWithPropertyWithPayloadName.getTest() ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testGenerateJsonForAspectWithAbstractEntity( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );

      final AspectWithAbstractEntity aspectWithAbstractEntity = parseJson( generatedJson, AspectWithAbstractEntity.class );
      final ExtendingTestEntity testProperty = aspectWithAbstractEntity.getTestProperty();
      assertThat( testProperty ).isNotNull();
      assertThat( testProperty.getAbstractTestProperty() ).isNotNull();
      assertThat( testProperty.getEntityProperty() ).isNotBlank();
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testGenerateJsonForAspectWithCollectionWithAbstractEntity( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );

      final AspectWithCollectionWithAbstractEntity aspectWithCollectionWithAbstractEntity = parseJson( generatedJson,
            AspectWithCollectionWithAbstractEntity.class );
      final Collection<AbstractTestEntity> testProperty = aspectWithCollectionWithAbstractEntity.getTestProperty();
      assertThat( testProperty ).isNotEmpty();
      final ExtendingTestEntity extendingTestEntity = (ExtendingTestEntity) testProperty.iterator().next();

      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( extendingTestEntity.getAbstractTestProperty() ).isNotNull();
      assertThat( extendingTestEntity.getEntityProperty() ).isNotBlank();
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testGenerateJsonForAspectWithAbstractSingleEntity( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );

      final AspectWithAbstractSingleEntity aspectWithAbstractSingleEntity = parseJson( generatedJson, AspectWithAbstractSingleEntity.class );
      final ExtendingTestEntity extendingTestEntity = (ExtendingTestEntity) aspectWithAbstractSingleEntity.getTestProperty();
      assertThat( extendingTestEntity.getAbstractTestProperty() ).isNotNull();
      assertThat( extendingTestEntity.getEntityProperty() ).isNotBlank();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   void testGenerateJsonForAspectWithConstrainedSetProperty( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_CONSTRAINED_SET, metaModelVersion );
      final AspectWithConstrainedSet aspectWithConstrainedSet = parseJson( generatedJson, AspectWithConstrainedSet.class );
      assertThat( generatedJson ).contains( "[" ).contains( "]" );
      assertThat( aspectWithConstrainedSet.getTestProperty() ).hasSizeGreaterThan( 0 );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   void testGenerateJsonForAspectWithComplexSet( final KnownVersion metaModelVersion ) throws IOException {
      final String generatedJson = generateJsonForModel( TestAspect.ASPECT_WITH_COMPLEX_SET, metaModelVersion );
      final AspectWithComplexSet aspectWithComplexSet = parseJson( generatedJson, AspectWithComplexSet.class );
      assertThat( aspectWithComplexSet.getTestProperty() ).hasSize( 2 );
      final Iterator<Id> values = aspectWithComplexSet.getTestProperty().iterator();
      final Id id1 = values.next();
      final Id id2 = values.next();
      assertNotEquals( id1, id2 );
   }

   private String generateJsonForModel( final TestAspect model, final KnownVersion testedVersion ) {
      final VersionedModel versionedModel = TestResources.getModel( model, testedVersion ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspectUnchecked( versionedModel );
      final AspectModelJsonPayloadGenerator jsonGenerator = new AspectModelJsonPayloadGenerator( new AspectContext( versionedModel, aspect ) );
      try {
         final var payload = jsonGenerator.generateJson();
         return payload;
      } catch ( final IOException e ) {
         throw new RuntimeException( e );
      }
   }

   private void assertTestEntityWithSimpleTypes( final TestEntityWithSimpleTypes testEntityWithSimpleTypes ) {
      assertThat( testEntityWithSimpleTypes.getRandomValue() ).isNotBlank();
      assertThat( testEntityWithSimpleTypes.getRandomValue() ).isNotNull();
      assertThat( testEntityWithSimpleTypes.getTestFloat() ).isEqualTo( 2.25f );
      assertThat( testEntityWithSimpleTypes.getTestLocalDateTime() ).isEqualTo( datatypeFactory.newXMLGregorianCalendar( "2018-02-28T14:23:32.918Z" ) );
      assertThat( testEntityWithSimpleTypes.getTestInt() ).isEqualTo( 3 );
      assertThat( testEntityWithSimpleTypes.getTestString() ).isEqualTo( "Example Value Test" );
   }

   private <T> T parseJson( final String json, final Class<T> targetClass ) throws IOException {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule( new JavaTimeModule() );
      mapper.registerModule( new Jdk8Module() );
      mapper.registerModule( new AspectModelJacksonModule() );
      mapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
      mapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
      return mapper.readValue( json, targetClass );
   }

   // combination of all numeric meta model types with various range types
   private static List<Arguments> rangeTestSource() {
      final List<Arguments> result = new ArrayList<>();
      Lists.cartesianProduct( getMetaModelNumericTypes(), RANGE_CONSTRAINTS_TO_TEST )
            .forEach( list -> result.add( Arguments.of( list.get( 0 ), list.get( 1 ) ) ) );
      return result;
   }

   private static List<RDFDatatype> getMetaModelNumericTypes() {
      return DataType.getAllSupportedTypes()
            .stream()
            .filter( dataType -> dataType.getJavaClass() != null )
            .filter( dataType -> Number.class.isAssignableFrom( dataType.getJavaClass() ) )
            .collect( Collectors.toList() );
   }

   private static final List<Optional<BoundDefinition>> RANGE_CONSTRAINTS_TO_TEST = Arrays.asList(
         Optional.empty(), // no range constraint (type-specific bounds apply)
         Optional.of( BoundDefinition.OPEN ), // open range constraint (again, type specific bounds apply here)
         Optional.of( BoundDefinition.AT_LEAST ), // inclusive bounds
         Optional.of( BoundDefinition.GREATER_THAN ) // exclusive bounds
   );

   private Aspect createAspectWithDynamicNumericProperty( final KnownVersion modelVersion, final Type dataType,
         final BoundDefinition boundKind, final Pair<Number, Number> randomRange ) {
      final SAMM samm = new SAMM( modelVersion );
      final Characteristic constraint = boundKind == null ? createBasicCharacteristic( modelVersion, dataType, samm )
            : createTraitWithRangeConstraint( modelVersion, dataType, boundKind, samm, randomRange );
      final List<Property> properties = List.of( createProperty( modelVersion, "testNumber", constraint, samm ) );
      final MetaModelBaseAttributes aspectAttributes =
            MetaModelBaseAttributes.from( modelVersion, AspectModelUrn.fromUrn( samm.Aspect().getURI() ), "AspectWithNumericProperty" );
      return new DefaultAspect( aspectAttributes, properties, List.of(), List.of(), false );
   }

   private Property createProperty( final KnownVersion modelVersion, final String propertyName, final Characteristic characteristic, final SAMM samm ) {
      final MetaModelBaseAttributes propertyAttributes =
            MetaModelBaseAttributes.from( modelVersion, AspectModelUrn.fromUrn( samm.Property().getURI() ), propertyName );
      return new DefaultProperty( propertyAttributes, Optional.of( characteristic ), Optional.empty(), false, false, Optional.empty(), false,
            Optional.empty() );
   }

   Trait createTraitWithRangeConstraint( final KnownVersion modelVersion, final Type dataType, final BoundDefinition boundKind,
         final SAMM samm, final Pair<Number, Number> randomRange ) {
      final MetaModelBaseAttributes constraintAttibutes =
            MetaModelBaseAttributes.from( modelVersion, AspectModelUrn.fromUrn( samm.characteristic().getURI() ), "TestConstraint" );
      final Optional<ScalarValue> minValue = BoundDefinition.OPEN.equals( boundKind )
            ? Optional.empty()
            : Optional.of( new DefaultScalarValue( randomRange.getLeft(), new DefaultScalar( dataType.getUrn(), modelVersion ) ) );
      final Optional<ScalarValue> maxValue = BoundDefinition.OPEN.equals( boundKind )
            ? Optional.empty()
            : Optional.of( new DefaultScalarValue( randomRange.getRight(), new DefaultScalar( dataType.getUrn(), modelVersion ) ) );
      final RangeConstraint rangeConstraint = new DefaultRangeConstraint( constraintAttibutes, minValue, maxValue, boundKind,
            getMatchingUpperBound( boundKind ) );
      final MetaModelBaseAttributes traitAttributes = MetaModelBaseAttributes
            .from( modelVersion, AspectModelUrn.fromUrn( samm.characteristic().getURI() ), "TestTrait" );
      return new DefaultTrait( traitAttributes, createBasicCharacteristic( modelVersion, dataType, samm ), List.of( rangeConstraint ) );
   }

   private BoundDefinition getMatchingUpperBound( final BoundDefinition boundKind ) {
      return boundKind == BoundDefinition.AT_LEAST ? BoundDefinition.AT_MOST :
            boundKind == BoundDefinition.GREATER_THAN ? BoundDefinition.LESS_THAN :
                  BoundDefinition.OPEN;
   }

   Characteristic createBasicCharacteristic( final KnownVersion modelVersion, final Type dataType, final SAMM samm ) {
      return new DefaultCharacteristic( MetaModelBaseAttributes.builderFor( "NumberCharacteristic" )
            .withMetaModelVersion( modelVersion )
            .withUrn( AspectModelUrn.fromUrn( samm.baseCharacteristic().getURI() ) )
            .withPreferredName( Locale.forLanguageTag( "en" ), "NumberCharacteristic" )
            .withDescription( Locale.forLanguageTag( "en" ), "A simple numeric property." )
            .build(),
            Optional.of( dataType ) );
   }

   // generate a "human" test range
   private Pair<Number, Number> generateRandomRangeForType( final Type dataType, final Class<?> nativeType, final BoundDefinition boundKind ) {
      final Resource dataTypeResource = ResourceFactory.createResource( dataType.getUrn() );
      final Number min = NumericTypeTraits.getModelMinValue( dataTypeResource, nativeType );
      final Number max = NumericTypeTraits.getModelMaxValue( dataTypeResource, nativeType );
      if ( null == boundKind || BoundDefinition.OPEN.equals( boundKind ) ) {
         // no RangeConstraint or RangeConstraint with open bounds -> standard model type range applies
         return Pair.of( min, max );
      }

      Pair<Number, Number> range = TEST_RANGES.get( nativeType );

      // but not all model types allow positive/negative values
      final BigDecimal helperMin = NumericTypeTraits.convertToBigDecimal( min );
      final BigDecimal helperMax = NumericTypeTraits.convertToBigDecimal( max );
      // negative & non-positive types
      if ( helperMax.compareTo( BigDecimal.ZERO ) == 0 || helperMax.compareTo( BigDecimal.valueOf( -1 ) ) == 0 ) {
         range = Pair.of( range.getLeft(), max );
      }
      // unsigned & positive types?
      if ( helperMin.compareTo( BigDecimal.ZERO ) == 0 || helperMin.compareTo( BigDecimal.ONE ) == 0 ) {
         range = Pair.of( min, helperMax.compareTo( NumericTypeTraits.convertToBigDecimal( range.getRight() ) ) < 0 ?
               max :
               range.getRight() );
      }

      return range;
   }

   private static final java.util.Map<Class<?>, Pair<Number, Number>> TEST_RANGES = Map.of(
         Byte.class, Pair.of( (byte) -34, (byte) 112 ),
         Short.class, Pair.of( (short) -11856, (short) 27856 ),
         Integer.class, Pair.of( -118560, 278560 ),
         Long.class, Pair.of( -4523542345L, 45687855464565555L ),
         Float.class, Pair.of( -45.23f, 1056.764f ),
         Double.class, Pair.of( -1056.4737423, 734838.82734 ),
         BigInteger.class, Pair.of( BigInteger.valueOf( -14823487823L ), BigInteger.valueOf( 454655678786L ) ),
         BigDecimal.class, Pair.of( BigDecimal.valueOf( -87445.2345 ), BigDecimal.valueOf( 2345345.12345 ) )
   );

   /**
    * Special version of the random number generator that always starts with the lower bound
    * as the first item in the generated sequence.
    */
   private static class MinValueRandomStrategy extends Random {
      @Override
      public IntStream ints( final long streamSize, final int randomNumberOrigin, final int randomNumberBound ) {
         return IntStream.concat( IntStream.of( randomNumberOrigin ), super.ints( streamSize, randomNumberOrigin, randomNumberBound ).skip( 1 ) );
      }

      @Override
      public LongStream longs( final long streamSize, final long randomNumberOrigin, final long randomNumberBound ) {
         return LongStream.concat( LongStream.of( randomNumberOrigin ), super.longs( streamSize, randomNumberOrigin, randomNumberBound ).skip( 1 ) );
      }

      @Override
      public DoubleStream doubles( final long streamSize, final double randomNumberOrigin, final double randomNumberBound ) {
         return DoubleStream.concat( DoubleStream.of( randomNumberOrigin ), super.doubles( streamSize, randomNumberOrigin, randomNumberBound ).skip( 1 ) );
      }

      private static final long serialVersionUID = 4032598470646521142L;
   }

   /**
    * Special version of the random number generator that always starts with the upperBound
    * as the first item in the generated sequence.
    */
   private static class MaxValueRandomStrategy extends Random {
      @Override
      public IntStream ints( final long streamSize, final int randomNumberOrigin, final int randomNumberBound ) {
         return IntStream.concat( IntStream.of( randomNumberBound ), super.ints( streamSize, randomNumberOrigin, randomNumberBound ).skip( 1 ) );
      }

      @Override
      public LongStream longs( final long streamSize, final long randomNumberOrigin, final long randomNumberBound ) {
         return LongStream.concat( LongStream.of( randomNumberBound ), super.longs( streamSize, randomNumberOrigin, randomNumberBound ).skip( 1 ) );
      }

      @Override
      public DoubleStream doubles( final long streamSize, final double randomNumberOrigin, final double randomNumberBound ) {
         return DoubleStream.concat( DoubleStream.of( randomNumberBound ), super.doubles( streamSize, randomNumberOrigin, randomNumberBound ).skip( 1 ) );
      }

      private static final long serialVersionUID = -4713706160081659886L;
   }
}
