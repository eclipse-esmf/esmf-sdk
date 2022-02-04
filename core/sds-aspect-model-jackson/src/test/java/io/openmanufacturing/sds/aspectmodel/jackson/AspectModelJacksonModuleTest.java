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

package io.openmanufacturing.sds.aspectmodel.jackson;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.java.QualifiedName;
import io.openmanufacturing.sds.aspectmodel.java.exception.EnumAttributeNotFoundException;
import io.openmanufacturing.sds.aspectmodel.java.pojo.AspectModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.datatypes.LangString;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;
import io.openmanufacturing.sds.test.shared.compiler.JavaCompiler;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class AspectModelJacksonModuleTest extends MetaModelVersions {
   private static final String PACKAGE = "com.test";

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithMultiLanguageText( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_MULTI_LANGUAGE_TEXT, metaModelVersion );
      final Class<?> clazz = instance.getClass();
      final LangString prop = getValue( clazz, instance, "prop", LangString.class );
      assertThat( prop.getLanguageTag() ).isEqualTo( Locale.ENGLISH );
      assertThat( prop.getValue() ).isEqualTo( "Value in English" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithSimpleTypes( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_SIMPLE_TYPES, metaModelVersion );
      final Class<?> clazz = instance.getClass();

      final XMLGregorianCalendar timestamp = getValue( clazz, instance, "dateTimeProperty", XMLGregorianCalendar.class );
      assertThat( timestamp.toXMLFormat() ).isEqualTo( "2018-08-08T12:00:00.0000+01:00" );

      final XMLGregorianCalendar date = getValue( clazz, instance, "dateProperty", XMLGregorianCalendar.class );
      assertThat( date.toXMLFormat() ).isEqualTo( "2019-01-02" );

      final BigInteger number = getValue( clazz, instance, "unsignedLongProperty", BigInteger.class );
      assertThat( number ).isEqualTo( BigInteger.valueOf( 42L ) );

      final byte[] base64Binary = getValue( clazz, instance, "base64BinaryProperty", byte[].class );
      final String base64BinaryDecoded = new String( base64Binary, StandardCharsets.US_ASCII );
      assertThat( base64BinaryDecoded ).isEqualTo( "This is a test" );

      final byte[] hexBinary = getValue( clazz, instance, "hexBinaryProperty", byte[].class );
      final String hexBinaryDecoded = new String( hexBinary, StandardCharsets.US_ASCII );
      assertThat( hexBinaryDecoded ).isEqualTo( "This is a test" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithCollection( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_COLLECTIONS, metaModelVersion );
      final Class<?> clazz = instance.getClass();

      final List<Integer> listProperty = getValue( clazz, instance, "listProperty",
            new TypeToken<List<Integer>>() {
            }.getType() );
      assertThat( listProperty ).contains( 1, 2, 3 );

      final Set<String> setProperty = getValue( clazz, instance, "setProperty",
            new TypeToken<Set<String>>() {
            }.getType() );
      assertThat( setProperty ).containsExactlyInAnyOrder( "foo", "bar" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithEntity( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_SIMPLE_ENTITY, metaModelVersion );
      final Class<?> clazz = instance.getClass();

      final Field entityPropertyField = clazz.getDeclaredField( "entityProperty" );
      entityPropertyField.setAccessible( true );
      assertThat( entityPropertyField.getType().getName() ).isEqualTo( PACKAGE + ".Foo" );
      final Object entity = entityPropertyField.get( instance );

      final String foo = getValue( entity.getClass(), entity, "foo", String.class );
      assertThat( foo ).isEqualTo( "some value" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOptionalProperties( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTIES, metaModelVersion );
      final Class<?> clazz = instance.getClass();

      final XMLGregorianCalendar timestamp = getValue( clazz, instance, "timestampProperty", XMLGregorianCalendar.class );
      assertThat( timestamp.toXMLFormat() ).isEqualTo( "2018-08-08T12:00:00.0000+01:00" );

      final Optional<BigInteger> number = getValue( clazz, instance, "numberProperty",
            new TypeToken<Optional<BigInteger>>() {
            }.getType() );
      assertThat( number ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithStructuredValue( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_NUMERIC_STRUCTURED_VALUE, metaModelVersion );
      final Class<?> clazz = instance.getClass();

      final XMLGregorianCalendar date = getValue( clazz, instance, "date", XMLGregorianCalendar.class );
      assertThat( date.getYear() ).isEqualTo( 2020 );
      assertThat( date.getMonth() ).isEqualTo( 1 );
      assertThat( date.getDay() ).isEqualTo( 20 );

      final Long year = getValue( clazz, instance, "year", Long.class );
      assertThat( year ).isEqualTo( 2020L );

      final Long month = getValue( clazz, instance, "month", Long.class );
      assertThat( month ).isEqualTo( 1 );

      final Long day = getValue( clazz, instance, "day", Long.class );
      assertThat( day ).isEqualTo( 20 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithEnumeration( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_STRING_ENUMERATION, metaModelVersion );
      final Class<?> clazz = instance.getClass();

      final Field enumerationField = clazz.getDeclaredField( "enumerationProperty" );
      enumerationField.setAccessible( true );
      final Class<?> enumerationType = enumerationField.getType();
      assertThat( enumerationType.getName() ).isEqualTo( PACKAGE + ".EnumerationPropertyEnumeration" );
      assertThat( enumerationType.isEnum() ).isTrue();

      final Object enumerationValue = enumerationField.get( instance );
      final String foo = getValue( enumerationValue.getClass(), enumerationValue, "value", String.class );
      assertThat( foo ).isEqualTo( "foo" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithEntityEnumeration( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( Tuple.of( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_WITH_NOT_EXISTING_ENUM, "AspectWithEntityEnumeration" ),
            metaModelVersion );
      final Class<?> clazz = instance.getClass();
      final Field enumerationField = clazz.getDeclaredField( "systemState" );
      enumerationField.setAccessible( true );
      final Class<?> enumerationType = enumerationField.getType();

      assertThat( enumerationType.getName() ).isEqualTo( PACKAGE + ".SystemStateEnumeration" );
      assertThat( enumerationType.isEnum() ).isTrue();

      final Object enumerationValue = enumerationField.get( instance );
      final Field propertyField = enumerationValue.getClass().getDeclaredField( "value" );
      propertyField.setAccessible( true );

      assertThat( propertyField.getType().getSimpleName() ).isEqualTo( "SystemState" );
      assertThat( enumerationValue.toString() ).isEqualTo( "COOL_DOWN" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithEntityEnumerationWithNotExistingEnum( final KnownVersion metaModelVersion ) {
      assertThatExceptionOfType( EnumAttributeNotFoundException.class ).isThrownBy( () ->
                  generateInstance( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_WITH_NOT_EXISTING_ENUM, metaModelVersion ) )
            .withMessageContainingAll( "Tried to parse value", "but there is no enum field like that" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithEntityEnumerationAndNotInPayloadProperties( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES, metaModelVersion );
      final Class<?> clazz = instance.getClass();
      final Field enumerationField = clazz.getDeclaredField( "systemState" );
      enumerationField.setAccessible( true );
      final Class<?> enumerationType = enumerationField.getType();

      assertThat( enumerationType.getName() ).isEqualTo( PACKAGE + ".SystemStateEnumeration" );
      assertThat( enumerationType.isEnum() ).isTrue();

      final Object enumerationValue = enumerationField.get( instance );
      final Field propertyField = enumerationValue.getClass().getDeclaredField( "value" );
      propertyField.setAccessible( true );

      assertThat( propertyField.getType().getSimpleName() ).isEqualTo( "SystemState" );
      assertThat( enumerationValue.toString() ).isEqualTo( "COOL_DOWN" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithEitherWithComplexTypes( final KnownVersion metaModelVersion ) throws Exception {
      final Object instance = generateInstance( TestAspect.ASPECT_WITH_EITHER_WITH_COMPLEX_TYPES, metaModelVersion );
      final Class<?> clazz = instance.getClass();
      final Field testProperty = clazz.getDeclaredField( "testProperty" );
      testProperty.setAccessible( true );
      final Class<?> testPropertyType = testProperty.getType();
      assertThat( testPropertyType ).isEqualTo( Either.class );

      // It's an Either<LeftEntity, RightEntity> with LeftEntity and RightEntity being classes that are
      // generated from the Entities in the Aspect Model, and compiled in-memory, so we can only refer to them
      // as ? here
      final Either<?, ?> eitherValue = (Either<?, ?>) testProperty.get( instance );
      assertThat( eitherValue.getRight() ).isEmpty();
      final Object leftEntity = eitherValue.getLeft().get();
      final Field resultField = leftEntity.getClass().getDeclaredField( "result" );
      resultField.setAccessible( true );
      final Object value = resultField.get( leftEntity );
      assertThat( value ).isEqualTo( "eOMtThyhVNLWUZNRcBaQKxI" );
   }

   private Object generateInstance( final TestAspect model, final KnownVersion knownVersion ) throws IOException {
      return generateInstance( Tuple.of( model, model.getName() ), knownVersion );
   }

   private Object generateInstance( final Tuple2<TestAspect, String> modelNameAndPayloadName, final KnownVersion knownVersion ) throws IOException {
      final VersionedModel versionedModel = TestResources.getModel( modelNameAndPayloadName._1(), knownVersion ).get();
      final Class<?> pojo = AspectModelLoader.fromVersionedModel( versionedModel ).map( this::generatePojo ).get();
      final String jsonPayload = loadJsonPayload( modelNameAndPayloadName._1(), knownVersion,
            modelNameAndPayloadName._2() );
      return parseJson( jsonPayload, pojo );
   }

   private String loadJsonPayload( final TestAspect model, final KnownVersion knownVersion, final String payloadName ) throws IOException {
      final AspectModelUrn modelUrn = model.getUrn();
      final URL jsonUrl = getClass().getResource(
            String.format( "/%s/%s/%s/%s.json", knownVersion.toString().toLowerCase(),
                  modelUrn.getNamespace(), modelUrn.getVersion(), payloadName ) );
      return Resources.toString( jsonUrl, StandardCharsets.UTF_8 );
   }

   private Class<?> generatePojo( final Aspect aspect ) {
      final AspectModelJavaGenerator codeGenerator = new AspectModelJavaGenerator( aspect, PACKAGE, true, false, null );
      final Map<QualifiedName, ByteArrayOutputStream> outputs = new LinkedHashMap<>();
      codeGenerator.generate( name -> outputs.computeIfAbsent( name, name2 -> new ByteArrayOutputStream() ) );

      final Map<QualifiedName, String> sources = new LinkedHashMap<>();
      final List<QualifiedName> loadOrder = new ArrayList<>();
      for ( final Map.Entry<QualifiedName, ByteArrayOutputStream> entry : outputs.entrySet() ) {
         loadOrder.add( entry.getKey() );
         sources.put( entry.getKey(), entry.getValue().toString( StandardCharsets.UTF_8 ) );
      }

      final List<String> referencedClasses = Stream
            .concat( codeGenerator.getConfig().getImportTracker().getUsedImports().stream(),
                  codeGenerator.getConfig().getImportTracker().getUsedStaticImports().stream() )
            .collect( Collectors.toList() );
      final Map<QualifiedName, Class<?>> result = JavaCompiler.compile( loadOrder, sources, referencedClasses );
      return result.get( new QualifiedName( aspect.getName(), codeGenerator.getConfig().getPackageName() ) );
   }

   private <T> T parseJson( final String json, final Class<T> targetClass ) {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule( new JavaTimeModule() );
      mapper.registerModule( new Jdk8Module() );
      mapper.registerModule( new AspectModelJacksonModule( ) );
      mapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
      try {
         return mapper.readValue( json, targetClass );
      } catch ( final JsonProcessingException exeception ) {
         throw new EnumAttributeNotFoundException( exeception );
      }
   }

   private <T> T getValue( final Class<?> clazz, final Object instance, final String fieldName,
         final Class<T> fieldType ) throws NoSuchFieldException, IllegalAccessException {
      final Field propertyField = clazz.getDeclaredField( fieldName );
      propertyField.setAccessible( true );
      assertThat( propertyField.getType() ).isEqualTo( fieldType );
      return (T) propertyField.get( instance );
   }

   private <T> T getValue( final Class<?> clazz, final Object instance, final String fieldName, final Type fieldType )
         throws NoSuchFieldException, IllegalAccessException {
      final Field propertyField = clazz.getDeclaredField( fieldName );
      propertyField.setAccessible( true );
      assertThat( propertyField.getGenericType() ).isEqualTo( fieldType );
      return (T) propertyField.get( instance );
   }
}
