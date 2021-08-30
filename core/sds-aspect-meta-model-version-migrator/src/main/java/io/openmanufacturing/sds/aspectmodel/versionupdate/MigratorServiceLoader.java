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
package io.openmanufacturing.sds.aspectmodel.versionupdate;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * Collects a custom migration factory in the classpath and uses it to build a {@link MigratorService}
 */
public class MigratorServiceLoader {
   private static final Logger LOG = LoggerFactory.getLogger( MigratorServiceLoader.class );
   private static MigratorServiceLoader instance;
   private MigratorService migratorService;

   public static synchronized MigratorServiceLoader getInstance() {
      if ( instance == null ) {
         instance = new MigratorServiceLoader();
         instance.loadMigratorService();
      }
      return instance;
   }

   public MigratorService getMigratorService() {
      return migratorService;
   }

   private MigratorFactory createMigratorFactory( final Class<?> clazz ) {
      try {
         return (MigratorFactory) clazz.getDeclaredConstructor().newInstance();
      } catch ( final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
         LOG.error( "Cannot create {}. No default constructor available?", clazz, e );
         throw new IllegalArgumentException( "Cannot create" + clazz + " No default constructor available?" );
      }
   }

   private void loadMigratorService() {
      try ( final ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(
            MigratorFactory.class.getPackageName() ).scan() ) {
         final ClassInfoList migratorFactoryClasses = scanResult
               .getClassesImplementing( MigratorFactory.class.getName() );

         migratorService = migratorFactoryClasses
               .loadClasses( MigratorFactory.class ).stream().map( this::createMigratorFactory )
               .map( MigratorService::new ).findAny().orElse( new MigratorService() );
      }
   }
}
