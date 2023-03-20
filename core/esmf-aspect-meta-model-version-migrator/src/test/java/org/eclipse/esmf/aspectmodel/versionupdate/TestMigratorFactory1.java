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
package org.eclipse.esmf.aspectmodel.versionupdate;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.AspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.Migrator;

import org.eclipse.esmf.samm.KnownVersion;

import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.versionupdate.migrator.AbstractMigrator;

public class TestMigratorFactory1 implements MigratorFactory {

   @Override
   public VersionNumber getLatestVersion() {
      return VersionNumber.parse( KnownVersion.SAMM_1_0_0.toVersionString() );
   }

   @Override
   public List<Migrator> createMigrators() {
      return List.of( new TestMigrator1(), new TestMigrator2(), new TestMigrator3(), new TestMigrator4() );
   }

   @Override
   public AspectMetaModelResourceResolver createAspectMetaModelResourceResolver() {
      return new SammAspectMetaModelResourceResolver();
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
