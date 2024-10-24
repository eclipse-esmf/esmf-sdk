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

package org.eclipse.esmf.buildtime;

import static org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper.DEFAULT_IMPLEMENTATION_PACKAGE_NAME;
import static org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper.DEFAULT_IMPLEMENTATION_PREFIX;
import static org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper.JSON_MIXINS_PACKAGE_NAME;
import static org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper.MIXIN_SUFFIX;
import static org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper.MODEL_PACKAGE_NAME;
import static org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper.MODEL_TYPE_SUPERCLASSES;
import static org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper.XML_MIXINS_PACKAGE_NAME;
import static org.eclipse.esmf.nativefeatures.AssetAdministrationShellFeature.ADMINSHELL_PROPERTIES;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.substitution.AdminShellConfig;
import org.eclipse.esmf.substitution.ImplementationInfo;
import org.eclipse.esmf.substitution.Target_org_eclipse_digitaltwin_aas4j_v3_dataformat_core_internal_util_ReflectionHelper;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper;

/**
 * This class generates the reflection information normally stored by {@link ReflectionHelper} and serializes it into a .properties file.
 * It is part of the substitution logic for this class, see
 * {@link Target_org_eclipse_digitaltwin_aas4j_v3_dataformat_core_internal_util_ReflectionHelper} for more information.
 * Note that this class is <i>only</i> supposed to run at build time (via execution from the Maven build) and is not part of the
 * resulting CLI codebase. Running this class is configured in the pom.xml of the CLI Maven module (via exec-maven-plugin).
 */
public class Aas4jClassSetup {
   private final AdminShellConfig config;

   public Aas4jClassSetup() {
      // The following replicates the logic from ReflectionHelper's static constructor, but instead stores its result
      // in the AdminShellConfig object that can then be written to a .properties file
      final ScanResult modelScan = new ClassGraph()
            .enableClassInfo()
            .acceptPackagesNonRecursive( MODEL_PACKAGE_NAME )
            .scan();
      config = new AdminShellConfig();
      config.typesWithModelType = scanModelTypes( modelScan );
      config.subtypes = scanSubtypes( modelScan );
      config.jsonMixins = scanMixins( modelScan, JSON_MIXINS_PACKAGE_NAME );
      config.xmlMixins = scanMixins( modelScan, XML_MIXINS_PACKAGE_NAME );
      config.defaultImplementations = scanDefaultImplementations( modelScan );
      config.interfaces = scanAasInterfaces();
      config.enums = modelScan.getAllEnums().loadClasses( Enum.class );
      config.interfacesWithoutDefaultImplementation = getInterfacesWithoutDefaultImplementation( modelScan );
      config.classesInModelPackage = classesInPackage( MODEL_PACKAGE_NAME );
      config.classesInDefaultImplementationPackage = classesInPackage( DEFAULT_IMPLEMENTATION_PACKAGE_NAME );
      config.classesInJsonMixinsPackage = classesInPackage( JSON_MIXINS_PACKAGE_NAME );
      config.classesInXmlMixinsPackage = classesInPackage( XML_MIXINS_PACKAGE_NAME );
   }

   public static void main( final String[] args ) throws IOException {
      try ( final FileOutputStream outputStream = new FileOutputStream( Path.of( args[0] ).resolve( ADMINSHELL_PROPERTIES ).toFile() ) ) {
         new Aas4jClassSetup().config.toProperties().store( outputStream, null );
      }
   }

   private Set<Class<?>> classesInPackage( final String packageName ) {
      try ( final ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages( packageName ).scan() ) {
         return new HashSet<>( scanResult.getAllClasses().loadClasses() );
      }
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#hasSubclass(ClassInfo)}
    */
   private boolean hasSubclass( final ClassInfo clazzInfo ) {
      return !getSubclasses( clazzInfo ).isEmpty();
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#scanModelTypes(ScanResult)}
    */
   private Set<Class<?>> scanModelTypes( final ScanResult modelScan ) {
      final Set<Class<?>> typesWithModelTypes;
      typesWithModelTypes = MODEL_TYPE_SUPERCLASSES.stream()
            .flatMap( x -> modelScan.getClassesImplementing( x.getName() ).loadClasses().stream() )
            .collect( Collectors.toSet() );
      typesWithModelTypes.addAll( MODEL_TYPE_SUPERCLASSES );
      return typesWithModelTypes;
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#scanSubtypes(ScanResult)}
    */
   private Map<Class<?>, Set<Class<?>>> scanSubtypes( final ScanResult modelScan ) {
      return modelScan.getAllInterfaces().stream()
            .filter( this::hasSubclass )
            .collect( Collectors.toMap( ClassInfo::loadClass, this::getSubclasses ) );
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#getSubclasses(ClassInfo)}
    */
   private Set<Class<?>> getSubclasses( final ClassInfo clazzInfo ) {
      return new HashSet<>( clazzInfo.getClassesImplementing()
            .directOnly()
            .filter( ClassInfo::isInterface )
            .loadClasses() );
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#scanMixins(ScanResult, String)}
    */
   private Map<Class<?>, Class<?>> scanMixins( final ScanResult modelScan, final String packageName ) {
      final ScanResult mixinScan = new ClassGraph()
            .enableClassInfo()
            .acceptPackagesNonRecursive( packageName )
            .scan();
      final Map<Class<?>, Class<?>> mixins = new HashMap<>();
      mixinScan.getAllClasses()
            .filter( x -> x.getSimpleName().endsWith( MIXIN_SUFFIX ) )
            .loadClasses()
            .forEach( x -> {
               final String modelClassName = x.getSimpleName().substring( 0, x.getSimpleName().length() - MIXIN_SUFFIX.length() );
               final ClassInfoList modelClassInfos = modelScan.getAllClasses()
                     .filter( y -> Objects.equals( y.getSimpleName(), modelClassName ) );
               if ( !modelClassInfos.isEmpty() ) {
                  mixins.put( modelClassInfos.get( 0 ).loadClass(), x );
               }
            } );
      return mixins;
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#scanDefaultImplementations(ScanResult)}
    */
   private List<ReflectionHelper.ImplementationInfo> scanDefaultImplementations( final ScanResult modelScan ) {
      final ScanResult defaulImplementationScan = new ClassGraph()
            .enableClassInfo()
            .acceptPackagesNonRecursive( DEFAULT_IMPLEMENTATION_PACKAGE_NAME )
            .scan();
      final List<ReflectionHelper.ImplementationInfo> defaultImplementations = new ArrayList<>();
      defaulImplementationScan.getAllClasses()
            .filter( x -> x.getSimpleName().startsWith( DEFAULT_IMPLEMENTATION_PREFIX ) )
            .loadClasses()
            .forEach( x -> {
               final String interfaceName = x.getSimpleName().substring( DEFAULT_IMPLEMENTATION_PREFIX.length() ); // using conventions
               final ClassInfoList interfaceClassInfos = modelScan.getAllClasses()
                     .filter( y -> y.isInterface() && Objects.equals( y.getSimpleName(), interfaceName ) );
               if ( !interfaceClassInfos.isEmpty() ) {
                  final Class<?> implementedClass = interfaceClassInfos.get( 0 ).loadClass();
                  defaultImplementations.add( new ImplementationInfo( implementedClass, x ) );
               }
            } );
      return defaultImplementations;
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#scanAasInterfaces()}
    */
   private Set<Class> scanAasInterfaces() {
      return config.defaultImplementations.stream().map( ReflectionHelper.ImplementationInfo::getInterfaceType )
            .collect( Collectors.toSet() );
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#getInterfacesWithoutDefaultImplementation(ScanResult)}
    */
   private Set<Class<?>> getInterfacesWithoutDefaultImplementation( final ScanResult modelScan ) {
      return modelScan.getAllInterfaces().loadClasses().stream()
            .filter( x -> !hasDefaultImplementation( x ) )
            .collect( Collectors.toSet() );
   }

   /**
    * Logic duplicated from {@link ReflectionHelper#hasDefaultImplementation(Class)}
    */
   public boolean hasDefaultImplementation( final Class<?> interfaceType ) {
      return config.defaultImplementations.stream().anyMatch( x -> x.getInterfaceType().equals( interfaceType ) );
   }
}
