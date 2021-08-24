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

package io.openmanufacturing.sds.aspectmodel.java;

import java.util.Objects;

/**
 * Wraps the fully qualified name of a Java class
 */
public class QualifiedName {
   private final String className;
   private final String packageName;

   public QualifiedName( final String className, final String packageName ) {
      this.className = className;
      this.packageName = packageName;
   }

   /**
    * Returns the non-qualified class name, e.g. "Baz"
    *
    * @return The class name
    */
   public String getClassName() {
      return className;
   }

   /**
    * Returns the package name, e.g. "com.foo.bar"
    *
    * @return The package name
    */
   public String getPackageName() {
      return packageName;
   }

   /**
    * Returns the fully qualified class name, e.g. "com.foo.bar.Baz"
    *
    * @return The fully qualified class name
    */
   @Override
   public String toString() {
      return packageName + "." + className;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final QualifiedName that = (QualifiedName) o;
      return Objects.equals( className, that.className ) &&
             Objects.equals( packageName, that.packageName );
   }

   @Override
   public int hashCode() {
      return Objects.hash( className, packageName );
   }
}
