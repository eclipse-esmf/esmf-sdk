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

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.AspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.versionupdate.migrator.AbstractMigrator;
import io.openmanufacturing.sds.aspectmodel.versionupdate.migrator.Migrator;

public class TestMigratorFactory1 implements MigratorFactory {

   @Override
   public VersionNumber getLatestVersion() {
      return VersionNumber.parse( KnownVersion.BAMM_1_0_0.toVersionString() );
   }

   @Override
   public List<Migrator> createMigrators() {
      return List.of( new TestMigrator1(), new TestMigrator2(), new TestMigrator3(), new TestMigrator4() );
   }

   @Override
   public AspectMetaModelResourceResolver createAspectMetaModelResourceResolver() {
      return new SdsAspectMetaModelResourceResolver();
   }

   public static class TestMigrator1 extends AbstractMigrator {

      protected TestMigrator1() {
         super( VersionNumber.parse( "1.1.0" ), VersionNumber.parse( "1.2.0" ), 100 );
      }

      @Override
      public Model migrate( final Model sourceModel ) {
         return ModelFactory.createDefaultModel();
      }
   }

   public static class TestMigrator2 extends AbstractMigrator {

      protected TestMigrator2() {
         super( VersionNumber.parse( "1.1.0" ), VersionNumber.parse( "1.2.0" ), 50 );
      }

      @Override
      public Model migrate( final Model sourceModel ) {
         return ModelFactory.createDefaultModel();
      }
   }

   public static class TestMigrator3 extends AbstractMigrator {

      protected TestMigrator3() {
         super( VersionNumber.parse( "1.2.0" ), VersionNumber.parse( "1.3.0" ) );
      }

      @Override
      public Model migrate( final Model sourceModel ) {
         return ModelFactory.createDefaultModel();
      }
   }

   public static class TestMigrator4 extends AbstractMigrator {

      protected TestMigrator4() {
         super( VersionNumber.parse( "1.0.0" ), VersionNumber.parse( "1.1.0" ) );
      }

      @Override
      public Model migrate( final Model sourceModel ) {
         return ModelFactory.createDefaultModel();
      }
   }
}
