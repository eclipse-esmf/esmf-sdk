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

import static org.eclipse.esmf.nativefeatures.AssetAdministrationShellFeature.ADMINSHELL_PROPERTIES;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.esmf.buildtime.Aas4jClassSetup;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.KeepOriginal;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
 * for {@link ReflectionHelper}, the central point of reflection information of the io.admin-shell.aas library. The original
 * ReflectionHelper
 * has a static constructor that initializes several maps and collections (using ClassGraph scans) that hold information about
 * implementations
 * of interfaces etc.. For the build of the native image, this logic is replaced by the following logic:
 * <ol>
 *    <li>At build time, the {@link Aas4jClassSetup} is ran (as a standalone program).
 *    This creates an instance of {@link AdminShellConfig} which contains all the information extracted from the ClassGraph scans.</li>
 *    <li>The AdminShellConfig is serialized into a .properties file.</li>
 *    <li>The .properties file is then included as a regular resource in the build.</li>
 *    <li>The original ReflectionHelper is substituted by this class. The fields holding the information are initialized with the
 *    values read from the .properties file.</li>
 * </ol>
 * Unfortunately, the ClassGraph logic in the ReflectionHelper is part of its static constructor, which means it can't be overridden by a
 * regular substitution method. For this reason, the whole class must be substituted which means that (1) all public static fields need to
 * be aliased and (2) all methods with public interfaces need to be mentioned explicitly with @KeepOriginal.
 */
@Substitute
@TargetClass( ReflectionHelper.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101", // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
      "NewClassNamingConvention",
      "checkstyle:TypeName"
} )
public final class Target_org_eclipse_digitaltwin_aas4j_v3_dataformat_core_internal_util_ReflectionHelper {
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   private static final String ROOT_PACKAGE_NAME = "org.eclipse.digitaltwin.aas4j.v3";

   /**
    * @see ReflectionHelper#MODEL_PACKAGE_NAME
    */
   @SuppressWarnings( "NonConstantFieldWithUpperCaseName" ) // Field name must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static String MODEL_PACKAGE_NAME = ROOT_PACKAGE_NAME + ".model";

   /**
    * @see ReflectionHelper#TYPES_WITH_MODEL_TYPE
    */
   @SuppressWarnings( "NonConstantFieldWithUpperCaseName" ) // Field name must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Set<Class<?>> TYPES_WITH_MODEL_TYPE;

   /**
    * @see ReflectionHelper#SUBTYPES
    */
   @SuppressWarnings( "NonConstantFieldWithUpperCaseName" ) // Field name must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Map<Class<?>, Set<Class<?>>> SUBTYPES;

   /**
    * @see ReflectionHelper#INTERFACES
    */
   @SuppressWarnings( { "NonConstantFieldWithUpperCaseName", "rawtypes" } ) // Field name and signature must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Set<Class> INTERFACES;

   /**
    * @see ReflectionHelper#SUBTYPES
    */
   @SuppressWarnings( "NonConstantFieldWithUpperCaseName" ) // Field name must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Map<Class<?>, Class<?>> JSON_MIXINS;

   /**
    * @see ReflectionHelper#XML_MIXINS
    */
   @SuppressWarnings( "NonConstantFieldWithUpperCaseName" ) // Field name must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Map<Class<?>, Class<?>> XML_MIXINS;

   /**
    * @see ReflectionHelper#DEFAULT_IMPLEMENTATIONS
    */
   @SuppressWarnings( { "NonConstantFieldWithUpperCaseName", "rawtypes" } ) // Field name and signature must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static List<ReflectionHelper.ImplementationInfo> DEFAULT_IMPLEMENTATIONS;

   /**
    * @see ReflectionHelper#INTERFACES_WITHOUT_DEFAULT_IMPLEMENTATION
    */
   @SuppressWarnings( "NonConstantFieldWithUpperCaseName" ) // Field name must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static Set<Class<?>> INTERFACES_WITHOUT_DEFAULT_IMPLEMENTATION;

   /**
    * @see ReflectionHelper#ENUMS
    */
   @SuppressWarnings( { "NonConstantFieldWithUpperCaseName", "rawtypes" } ) // Field name and signature must match substituted class
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   public static List<Class<Enum>> ENUMS;

   static {
      try ( final InputStream input =
            Target_org_eclipse_digitaltwin_aas4j_v3_dataformat_core_internal_util_ReflectionHelper.class.getResourceAsStream(
                  "/" + ADMINSHELL_PROPERTIES ) ) {
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
      } catch ( final IOException e ) {
         throw new RuntimeException( e );
      }
   }

   /**
    * @param type see {@link ReflectionHelper#isModelInterface(Class)}
    * @return see {@link ReflectionHelper#isModelInterface(Class)}
    * @see ReflectionHelper#isModelInterface(Class)
    */
   @KeepOriginal
   public static native boolean isModelInterface( final Class<?> type );

   /**
    * @param type see {@link ReflectionHelper#isDefaultImplementation(Class)}
    * @return see {@link ReflectionHelper#isDefaultImplementation(Class)}
    * @see ReflectionHelper#isDefaultImplementation(Class)
    */
   @KeepOriginal
   public static native boolean isDefaultImplementation( final Class<?> type );

   /**
    * @param interfaceType see {@link ReflectionHelper#hasDefaultImplementation(Class)}
    * @return see {@link ReflectionHelper#hasDefaultImplementation(Class)}
    * @see ReflectionHelper#hasDefaultImplementation(Class)
    */
   @KeepOriginal
   public static native boolean hasDefaultImplementation( final Class<?> interfaceType );

   /**
    * @param <T> see {@link ReflectionHelper#getDefaultImplementation(Class)}
    * @param interfaceType see {@link ReflectionHelper#getDefaultImplementation(Class)}
    * @return see {@link ReflectionHelper#getDefaultImplementation(Class)}
    * @see ReflectionHelper#getDefaultImplementation(Class)
    */
   @KeepOriginal
   public static native <T> Class<? extends T> getDefaultImplementation( final Class<T> interfaceType );

   /**
    * @param type see {@link ReflectionHelper#hasDefaultImplementation(Class)}
    * @return see {@link ReflectionHelper#hasDefaultImplementation(Class)}
    * @see ReflectionHelper#hasDefaultImplementation(Class)
    */
   @KeepOriginal
   public static native boolean isModelInterfaceOrDefaultImplementation( final Class<?> type );

   /**
    * @param type see {@link ReflectionHelper#getAasInterface(Class)}
    * @return see {@link ReflectionHelper#getAasInterface(Class)}
    * @see ReflectionHelper#getAasInterface(Class)
    */
   @KeepOriginal
   public static native Class<?> getAasInterface( final Class<?> type );

   /**
    * @param type see {@link ReflectionHelper#getAasInterfaces(Class)}
    * @return see {@link ReflectionHelper#getAasInterfaces(Class)}
    * @see ReflectionHelper#getAasInterfaces(Class)
    */
   @KeepOriginal
   public static native Set<Class<?>> getAasInterfaces( final Class<?> type );

   /**
    * @param clazz see {@link ReflectionHelper#getModelType(Class)}
    * @return see {@link ReflectionHelper#getModelType(Class)}
    * @see ReflectionHelper#getModelType(Class)
    */
   @KeepOriginal
   public static native String getModelType( final Class<?> clazz );

   /**
    * @param clazz see {@link ReflectionHelper#getMostSpecificTypeWithModelType(Class)}
    * @return see {@link ReflectionHelper#getMostSpecificTypeWithModelType(Class)}
    * @see ReflectionHelper#getMostSpecificTypeWithModelType(Class)
    */
   @KeepOriginal
   public static native Class<?> getMostSpecificTypeWithModelType( final Class<?> clazz );

   /**
    * @param clazz see {@link ReflectionHelper#getSuperTypes(Class, boolean)}
    * @param recursive {@link ReflectionHelper#getSuperTypes(Class, boolean)}
    * @return see {@link ReflectionHelper#getSuperTypes(Class, boolean)}
    * @see ReflectionHelper#getSuperTypes(Class, boolean)
    */
   @KeepOriginal
   public static native Set<Class<?>> getSuperTypes( final Class<?> clazz, final boolean recursive );

   /**
    * @param element see {@link ReflectionHelper#setEmptyListsToNull(Object)}
    * @see ReflectionHelper#setEmptyListsToNull(Object)
    */
   @KeepOriginal
   public static native List<Runnable> setEmptyListsToNull( Object element );

   /**
    * @param element @see ReflectionHelper#createResetRunnable(Object, Field)
    * @param field @see ReflectionHelper#createResetRunnable(Object, Field)
    * @return @see ReflectionHelper#createResetRunnable(Object, Field)
    * @throws IllegalAccessException @see ReflectionHelper#createResetRunnable(Object, Field)
    * @see ReflectionHelper#createResetRunnable(Object, Field)
    */
   @KeepOriginal
   private static native Runnable createResetRunnable( Object element, Field field ) throws IllegalAccessException;
}
