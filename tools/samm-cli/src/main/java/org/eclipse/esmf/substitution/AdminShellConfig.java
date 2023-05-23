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

package org.eclipse.esmf.substitution;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.util.ReflectionHelper;

/**
 * An instance of this class encapsulates the information normally stored by {@link ReflectionHelper}. It is used in its substitution class,
 * see {@link Target_io_adminshell_aas_v3_dataformat_core_ReflectionHelper} for more information.
 * This class provides capabilities to serialize and deserialize its objects to/from a {@link Properties} object; more convenient serialization
 * (e.g., Java's builtin serialization or Jackson's ObjectMapper) can't be used here due to the limitations of the GraalVM substitution classes.
 */
public class AdminShellConfig {
   public Set<Class<?>> typesWithModelType;
   public Map<Class<?>, Set<Class<?>>> subtypes;
   public Map<Class<?>, Class<?>> jsonMixins;
   public Map<Class<?>, Class<?>> xmlMixins;
   public List<ReflectionHelper.ImplementationInfo> defaultImplementations;
   public Set<Class> interfaces;
   public List<Class<Enum>> enums;
   public Set<Class<?>> interfacesWithoutDefaultImplementation;

   private static final String PREFIX = AdminShellConfig.class.getPackageName() + ".adminshell.";
   private static final String TYPES_WITH_MODEL_TYPE = PREFIX + "typesWithModelTypes";
   private static final String SUBTYPES = PREFIX + "subtypes";
   private static final String JSON_MIXINS = PREFIX + "jsonMixins";
   private static final String XML_MIXINS = PREFIX + "xmlMixins";
   private static final String DEFAULT_IMPLEMENTATIONS = PREFIX + "defaultImplementations";
   private static final String INTERFACES = PREFIX + "interfaces";
   private static final String ENUMS = PREFIX + "enums";
   private static final String INTERFACES_WITHOUT_DEFAULT_IMPLEMENTATION = PREFIX + "interfacesWithoutDefaultImplementation";

   private String serialize( final Class<?> clazz ) {
      return clazz.getName();
   }

   private <T, C extends Collection<T>> String serialize( final C collection, final Function<T, String> mapper ) {
      return collection.stream().map( element -> mapper.apply( element ) ).collect( Collectors.joining( "," ) );
   }

   private <K, V> String serialize( final Map<K, V> map, final Function<K, String> keyMapper, final Function<V, String> valueMapper ) {
      return map.entrySet().stream()
            .map( entry -> String.format( "%s->%s", keyMapper.apply( entry.getKey() ), valueMapper.apply( entry.getValue() ) ) )
            .collect( Collectors.joining( ";" ) );
   }

   private String serialize( final ReflectionHelper.ImplementationInfo<?> info ) {
      return String.format( "%s->%s", info.getInterfaceType().getName(), info.getImplementationType().getName() );
   }

   public Properties toProperties() {
      final Properties properties = new Properties();
      properties.setProperty( TYPES_WITH_MODEL_TYPE, serialize( typesWithModelType, this::serialize ) );
      properties.setProperty( SUBTYPES, serialize( subtypes, this::serialize, set -> serialize( set, this::serialize ) ) );
      properties.setProperty( JSON_MIXINS, serialize( jsonMixins, this::serialize, this::serialize ) );
      properties.setProperty( XML_MIXINS, serialize( xmlMixins, this::serialize, this::serialize ) );
      properties.setProperty( DEFAULT_IMPLEMENTATIONS, serialize( defaultImplementations, this::serialize ) );
      properties.setProperty( INTERFACES, serialize( interfaces, this::serialize ) );
      properties.setProperty( ENUMS, serialize( enums, this::serialize ) );
      properties.setProperty( INTERFACES_WITHOUT_DEFAULT_IMPLEMENTATION, serialize( interfacesWithoutDefaultImplementation, this::serialize ) );
      return properties;
   }

   private static Class<?> deserializeClass( final String classname ) {
      try {
         return Class.forName( classname );
      } catch ( final ClassNotFoundException e ) {
         throw new RuntimeException( e );
      }
   }

   private static <T, C extends Collection<T>> C deserializeCollection( final String collection, final Function<String, T> elementDeserializer,
         final Collector<? super T, ?, C> collector ) {
      return Arrays.asList( collection.split( "," ) ).stream()
            .filter( entry -> !entry.isEmpty() )
            .map( element -> elementDeserializer.apply( element ) )
            .collect( collector );
   }

   private static <K, V> Map<K, V> deserializeMap( final String map, final Function<String, K> keyDeserializer,
         final Function<String, V> valueDeserializer ) {
      return Arrays.asList( map.split( ";" ) ).stream()
            .filter( entry -> !entry.isEmpty() )
            .map( entry -> {
               final String[] parts = entry.split( "->" );
               return Map.entry( keyDeserializer.apply( parts[0] ), valueDeserializer.apply( parts[1] ) );
            } )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );
   }

   private static <T> ReflectionHelper.ImplementationInfo<T> deserializeImplementationInfo( final String info ) {
      final String[] parts = info.split( "->" );
      return new ImplementationInfo( deserializeClass( parts[0] ), deserializeClass( parts[1] ) );
   }

   public static AdminShellConfig fromProperties( final Properties properties ) {
      final AdminShellConfig config = new AdminShellConfig();
      config.typesWithModelType = deserializeCollection( properties.getProperty( TYPES_WITH_MODEL_TYPE ), AdminShellConfig::deserializeClass,
            Collectors.toSet() );
      config.subtypes = deserializeMap( properties.getProperty( SUBTYPES ), AdminShellConfig::deserializeClass,
            set -> deserializeCollection( set, AdminShellConfig::deserializeClass, Collectors.toSet() ) );
      config.jsonMixins = deserializeMap( properties.getProperty( JSON_MIXINS ), AdminShellConfig::deserializeClass, AdminShellConfig::deserializeClass );
      config.xmlMixins = deserializeMap( properties.getProperty( XML_MIXINS ), AdminShellConfig::deserializeClass, AdminShellConfig::deserializeClass );
      config.defaultImplementations = deserializeCollection( properties.getProperty( DEFAULT_IMPLEMENTATIONS ),
            AdminShellConfig::deserializeImplementationInfo, Collectors.toList() );
      config.interfaces = deserializeCollection( properties.getProperty( INTERFACES ), AdminShellConfig::deserializeClass, Collectors.toSet() );
      config.enums = deserializeCollection( properties.getProperty( ENUMS ), element -> (Class<Enum>) deserializeClass( element ), Collectors.toList() );
      config.interfacesWithoutDefaultImplementation = deserializeCollection( properties.getProperty( INTERFACES_WITHOUT_DEFAULT_IMPLEMENTATION ),
            AdminShellConfig::deserializeClass, Collectors.toSet() );
      return config;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AdminShellConfig that = (AdminShellConfig) o;
      return Objects.equals( typesWithModelType, that.typesWithModelType ) && Objects.equals( subtypes, that.subtypes )
            && Objects.equals( jsonMixins, that.jsonMixins ) && Objects.equals( xmlMixins, that.xmlMixins ) && Objects.equals(
            defaultImplementations, that.defaultImplementations ) && Objects.equals( interfaces, that.interfaces ) && Objects.equals( enums,
            that.enums ) && Objects.equals( interfacesWithoutDefaultImplementation, that.interfacesWithoutDefaultImplementation );
   }

   @Override
   public int hashCode() {
      return Objects.hash( typesWithModelType, subtypes, jsonMixins, xmlMixins, defaultImplementations, interfaces, enums,
            interfacesWithoutDefaultImplementation );
   }
}
