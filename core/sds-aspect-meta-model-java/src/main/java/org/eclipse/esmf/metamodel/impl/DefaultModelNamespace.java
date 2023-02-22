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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.ModelNamespace;

public class DefaultModelNamespace implements ModelNamespace {
   private final String packagePart;
   private final VersionNumber versionNumber;
   private final List<ModelElement> elements = new ArrayList<>();

   public DefaultModelNamespace( final String packagePart, final VersionNumber versionNumber, final List<ModelElement> elements ) {
      this.packagePart = packagePart;
      this.versionNumber = versionNumber;
      this.elements.addAll( elements );
   }

   /**
    * Accepts a namespace URI such as 'urn:bamm:com.example:1.0.0'
    * @param uri the namspace uri
    * @param elements the list of elements in the namspace
    * @return the model namespace
    */
   public static ModelNamespace from( final String uri, final List<ModelElement> elements ) {
      final String[] parts = uri.split( ":" );
      return new DefaultModelNamespace( parts[2], VersionNumber.parse( parts[3] ), elements );
   }

   /**
    * Accepts a namespace URI such as 'urn:bamm:com.example:1.0.0'
    * @param uri the namspace uri
    * @return the model namespace
    */
   public static ModelNamespace from( final String uri ) {
      return from( uri, List.of() );
   }

   @Override
   public String packagePart() {
      return packagePart;
   }

   @Override
   public VersionNumber version() {
      return versionNumber;
   }

   @Override
   public List<ModelElement> elements() {
      return Collections.unmodifiableList( elements );
   }
}
