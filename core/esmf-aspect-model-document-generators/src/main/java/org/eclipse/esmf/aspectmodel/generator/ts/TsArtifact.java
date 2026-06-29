/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.ts;

import java.util.Objects;

import org.eclipse.esmf.aspectmodel.generator.Artifact;

/**
 * An {@link Artifact} that represents a Ts class
 */
public class TsArtifact implements Artifact<QualifiedName, String> {
   private final String content;
   private final String filename;
   private final String packageName;

   public TsArtifact( final String content, final String filename, final String packageName ) {
      this.content = content;
      this.filename = filename;
      this.packageName = packageName;
   }

   @Override
   public String getContent() {
      return content;
   }

   @Override
   public QualifiedName getId() {
      return new QualifiedName( filename, packageName );
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final TsArtifact that = (TsArtifact) o;
      return Objects.equals( content, that.content ) && Objects.equals( filename, that.filename )
            && Objects.equals( packageName, that.packageName );
   }

   @Override
   public int hashCode() {
      return Objects.hash( content, filename, packageName );
   }
}
