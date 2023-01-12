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

package io.openmanufacturing.sds.substitution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;

import com.google.common.reflect.TypeToken;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import io.adminshell.aas.v3.dataformat.core.ReflectionHelper;
import io.adminshell.aas.v3.dataformat.core.util.MostSpecificTypeTokenComparator;

/**
 * This is a GraalVM substitution class (see https://blog.frankel.ch/coping-incompatible-code-graalvm-compilation/#substitutions)
 * for {@link ReflectionHelper}, the central point of reflection information of the io.admin-shell.aas library. The original ReflectionHelper
 * has a static constructor that initializes several maps and collections (using ClassGraph scans) that hold information about implementations
 * of interfaces etc.. For the build of the native image, this logic is replaced by the following logic:
 * <ol>
 *    <li>At build time, the {@link io.openmanufacturing.sds.buildtime.IoAdminShellAasClassSetup} is ran (as a standalone program).
 *    This creates an instance of {@link AdminShellConfig} which contains all the information extracted from the ClassGraph scans.</li>
 *    <li>The AdminShellConfig is serialized into a .properties file.</li>
 *    <li>The .properties file is then included as a regular resource in the build.</li>
 *    <li>The original ReflectionHelper is substituted by this class. The fields holding the information are initialized with the
 *    values read from the .properties file.</li>
 * </ol>
 *
 * Unfortunately, the ClassGraph logic in the ReflectionHelper is part of its static constructor, which means it can't be overridden by a
 * regular substitution method. For this reason, the whole class must be substituted which means that (1) all public static fields need to
 * be aliased and (2) all methods with public interfaces need to be duplicated here.
 */
@Substitute
@TargetClass( ReflectionHelper.class )
public final class Target_io_adminshell_aas_v3_dataformat_core_ReflectionHelper {
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   private static String ROOT_PACKAGE_NAME = "io.adminshell.aas.v3";

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static String MODEL_PACKAGE_NAME = ROOT_PACKAGE_NAME + ".model";

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Set<Class<?>> TYPES_WITH_MODEL_TYPE;

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Map<Class<?>, Set<Class<?>>> SUBTYPES;

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Set<Class> INTERFACES;

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Map<Class<?>, Class<?>> JSON_MIXINS;

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Map<Class<?>, Class<?>> XML_MIXINS;

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static List<ReflectionHelper.ImplementationInfo> DEFAULT_IMPLEMENTATIONS;

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Set<Class<?>> INTERFACES_WITHOUT_DEFAULT_IMPLEMENTATION;

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static List<Class<Enum>> ENUMS;

   static {
      try ( final InputStream input = Target_io_adminshell_aas_v3_dataformat_core_ReflectionHelper.class.getResourceAsStream( "/adminshell.properties" ) ) {
         final Properties properties = new Properties();
         properties.load( input );
         final AdminShellConfig config = AdminShellConfig.fromProperties( properties );
         TYPES_WITH_MODEL_TYPE = config.typesWithModelType;
         SUBTYPES = config.subtypes;
         INTERFACES = config.interfaces;
         JSON_MIXINS = config.jsonMixins;
         XML_MIXINS = config.xmlMixins;
         DEFAULT_IMPLEMENTATIONS = config.defaultImplementations;
         INTERFACES_WITHOUT_DEFAULT_IMPLEMENTATION = config.interfacesWithoutDefaultImplementation;
         ENUMS = config.enums;
      } catch ( final FileNotFoundException e ) {
         throw new RuntimeException( e );
      } catch ( final IOException e ) {
         throw new RuntimeException( e );
      }
   }

   @Substitute
   public static boolean isModelInterface( final Class<?> type ) {
      return type.isInterface() && MODEL_PACKAGE_NAME.equals( type.getPackageName() );
   }

   @Substitute
   public static boolean isDefaultImplementation( final Class<?> type ) {
      return DEFAULT_IMPLEMENTATIONS.stream().anyMatch( x -> Objects.equals( x.getImplementationType(), type ) );
   }

   @Substitute
   public static boolean hasDefaultImplementation( final Class<?> interfaceType ) {
      return DEFAULT_IMPLEMENTATIONS.stream().anyMatch( x -> x.getInterfaceType().equals( interfaceType ) );
   }

   @Substitute
   public static <T> Class<? extends T> getDefaultImplementation( final Class<T> interfaceType ) {
      if ( isDefaultImplementation( interfaceType ) ) {
         return interfaceType;
      }
      if ( hasDefaultImplementation( interfaceType ) ) {
         return DEFAULT_IMPLEMENTATIONS.stream()
               .filter( x -> x.getInterfaceType().equals( interfaceType ) )
               .findFirst().get()
               .getImplementationType();
      }
      return null;
   }

   @Substitute
   public static boolean isModelInterfaceOrDefaultImplementation( final Class<?> type ) {
      return isModelInterface( type ) || isDefaultImplementation( type );
   }

   @Substitute
   public static Class<?> getAasInterface( final Class<?> type ) {
      final Set<Class<?>> implementedAasInterfaces = getAasInterfaces( type );
      if ( implementedAasInterfaces.isEmpty() ) {
         return null;
      }
      if ( implementedAasInterfaces.size() == 1 ) {
         return implementedAasInterfaces.iterator().next();
      }
      //         LOG.warn( "class '{}' implements more than one AAS interface, but only most specific one is returned", type.getName() );
      return implementedAasInterfaces.stream().map( x -> TypeToken.of( x ) )
            .sorted( new MostSpecificTypeTokenComparator() )
            .findFirst().get()
            .getRawType();
   }

   @Substitute
   public static Set<Class<?>> getAasInterfaces( final Class<?> type ) {
      final Set<Class<?>> result = new HashSet<>();
      if ( type != null ) {
         if ( INTERFACES.contains( type ) ) {
            result.add( type );
         }
         result.addAll( ClassUtils.getAllInterfaces( type ).stream().filter( x -> INTERFACES.contains( x ) ).collect( Collectors.toSet() ) );
      }
      return result;
   }

   @Substitute
   public static String getModelType( final Class<?> clazz ) {
      final Class<?> type = getMostSpecificTypeWithModelType( clazz );
      if ( type != null ) {
         return type.getSimpleName();
      }
      for ( final Class<?> interfaceClass : clazz.getInterfaces() ) {
         final String result = getModelType( interfaceClass );
         if ( result != null ) {
            return result;
         }
      }
      final Class<?> superClass = clazz.getSuperclass();
      if ( superClass != null ) {
         return getModelType( superClass );
      }
      return null;
   }

   @Substitute
   public static Class<?> getMostSpecificTypeWithModelType( final Class<?> clazz ) {
      if ( clazz == null ) {
         return null;
      }
      return TYPES_WITH_MODEL_TYPE.stream()
            .filter( x -> clazz.isInterface() ? x.equals( clazz ) : x.isAssignableFrom( clazz ) )
            .sorted( ( Class<?> o1, Class<?> o2 ) -> {
               // -1: o1 more special than o2
               // 0: o1 equals o2 or on same samelevel
               // 1: o2 more special than o1
               if ( o1.isAssignableFrom( o2 ) ) {
                  if ( o2.isAssignableFrom( o1 ) ) {
                     return 0;
                  }
                  return 1;
               }
               if ( o2.isAssignableFrom( o1 ) ) {
                  return -1;
               }
               return 0;
            } )
            .findFirst()
            .orElse( null );
   }

   @Substitute
   public static Set<Class<?>> getSuperTypes( final Class<?> clazz, final boolean recursive ) {
      final Set<Class<?>> result = SUBTYPES.entrySet().stream()
            .filter( x -> x.getValue().contains( clazz ) )
            .map( x -> x.getKey() )
            .collect( Collectors.toSet() );
      if ( recursive ) {
         result.addAll( result.stream()
               .flatMap( x -> getSuperTypes( x, true ).stream() )
               .collect( Collectors.toSet() ) );
      }
      return result;
   }
}
