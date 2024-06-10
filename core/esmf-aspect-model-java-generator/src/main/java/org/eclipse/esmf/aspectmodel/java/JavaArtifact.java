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

package org.eclipse.esmf.aspectmodel.java;

import java.util.Objects;

import org.eclipse.esmf.aspectmodel.generator.Artifact;

/**
 * An {@link Artifact} that represents a Java class
 */
public class JavaArtifact implements Artifact<QualifiedName, String> {
   private final String content;
   private final String filename;
   private final String javaPackageName;

   public JavaArtifact( final String content, final String filename, final String javaPackageName ) {
      this.content = content;
      this.filename = filename;
      this.javaPackageName = javaPackageName;
   }

   @Override
   public String getContent() {
      return content;
   }

   @Override
   public QualifiedName getId() {
      return new QualifiedName( filename, javaPackageName );
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final JavaArtifact that = (JavaArtifact) o;
      return Objects.equals( content, that.content ) && Objects.equals( filename, that.filename )
            && Objects.equals( javaPackageName, that.javaPackageName );
   }

   @Override
   public int hashCode() {
      return Objects.hash( content, filename, javaPackageName );
   }
}
