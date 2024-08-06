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

package org.eclipse.esmf.metamodel.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;

public class DefaultNamespace implements Namespace {
   private final String packagePart;
   private final VersionNumber versionNumber;
   private final List<ModelElement> elements;
   private final Optional<AspectModelFile> source;

   public DefaultNamespace( final AspectModelUrn aspectModelUrn, final List<ModelElement> elements,
         final Optional<AspectModelFile> source ) {
      this( aspectModelUrn.getNamespace(), VersionNumber.parse( aspectModelUrn.getVersion() ), elements, source );
   }

   public DefaultNamespace( final String packagePart, final VersionNumber versionNumber, final List<ModelElement> elements,
         final Optional<AspectModelFile> source ) {
      this.packagePart = packagePart;
      this.versionNumber = versionNumber;
      this.source = source;
      this.elements = elements;
   }

   /**
    * Accepts a namespace URI such as 'urn:samm:com.example:1.0.0'
    *
    * @param uri the namespace uri
    * @param elements the list of elements in the namspace
    */
   public DefaultNamespace( final String uri, final List<ModelElement> elements, final Optional<AspectModelFile> source ) {
      this( uri.split( ":" )[2], VersionNumber.parse( uri.split( ":" )[3].replace( "#", "" ) ), elements, source );
   }

   //   /**
   //    * Accepts a namespace URI such as 'urn:samm:com.example:1.0.0'
   //    *
   //    * @param uri the namspace uri
   //    * @return the model namespace
   //    */
   //   public static Namespace from( final String uri ) {
   //      return from( uri, List.of(), Optional.empty() );
   //   }

   @Override
   public String packagePart() {
      return packagePart;
   }

   @Override
   public Optional<AspectModelFile> source() {
      return source;
   }

   @Override
   public VersionNumber version() {
      return versionNumber;
   }

   @Override
   public List<ModelElement> elements() {
      return Collections.unmodifiableList( elements );
   }

   @Override
   public String getName() {
      return "urn:samm:%s:%s".formatted( packagePart, versionNumber );
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final DefaultNamespace that = (DefaultNamespace) o;
      return Objects.equals( packagePart, that.packagePart ) && Objects.equals( versionNumber, that.versionNumber )
            && Objects.equals( elements, that.elements ) && Objects.equals( source, that.source );
   }

   @Override
   public int hashCode() {
      return Objects.hash( packagePart, versionNumber, elements, source );
   }
}
