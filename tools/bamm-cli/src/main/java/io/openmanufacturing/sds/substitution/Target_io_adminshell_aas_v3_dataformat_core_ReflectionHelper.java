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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.KeepOriginal;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import io.adminshell.aas.v3.dataformat.core.ReflectionHelper;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
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
 * <p>
 * Unfortunately, the ClassGraph logic in the ReflectionHelper is part of its static constructor, which means it can't be overridden by a
 * regular substitution method. For this reason, the whole class must be substituted which means that (1) all public static fields need to
 * be aliased and (2) all methods with public interfaces need to be mentioned explicitly with @KeepOriginal.
 */
@Substitute
@TargetClass( ReflectionHelper.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101" // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
} )
public final class Target_io_adminshell_aas_v3_dataformat_core_ReflectionHelper {
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   private static final String ROOT_PACKAGE_NAME = "io.adminshell.aas.v3";

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

   @KeepOriginal
   public static native boolean isModelInterface( final Class<?> type );

   @KeepOriginal
   public static native boolean isDefaultImplementation( final Class<?> type );

   @KeepOriginal
   public static native boolean hasDefaultImplementation( final Class<?> interfaceType );

   @KeepOriginal
   public static native <T> Class<? extends T> getDefaultImplementation( final Class<T> interfaceType );

   @KeepOriginal
   public static native boolean isModelInterfaceOrDefaultImplementation( final Class<?> type );

   @KeepOriginal
   public static native Class<?> getAasInterface( final Class<?> type );

   @KeepOriginal
   public static native Set<Class<?>> getAasInterfaces( final Class<?> type );

   @KeepOriginal
   public static native String getModelType( final Class<?> clazz );

   @KeepOriginal
   public static native Class<?> getMostSpecificTypeWithModelType( final Class<?> clazz );

   @KeepOriginal
   public static native Set<Class<?>> getSuperTypes( final Class<?> clazz, final boolean recursive );
}
