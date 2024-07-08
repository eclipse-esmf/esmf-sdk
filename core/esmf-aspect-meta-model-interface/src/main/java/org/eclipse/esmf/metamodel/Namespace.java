/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.metamodel;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.VersionNumber;

/**
 * Represents the namespace the model elements are contained in
 */
public interface Namespace extends ModelElementGroup, HasDescription {
   String ANONYMOUS = "urn:samm:anonymous.elements:0.0.0#";

   /**
    * The package part of the model namespace is an identifier given in
    * <a href="https://en.wikipedia.org/wiki/Reverse_domain_name_notation">reverse domain name notation</a>, e.g., com.example.myapp.
    *
    * @return the package part of the namespace
    */
   String packagePart();

   /**
    * The version part of the namespace. This is always a semantic version, e.g. 1.2.3.
    *
    * @return the version part
    */
   VersionNumber version();

   Optional<AspectModelFile> source();

   /**
    * The identifier of the namespace, e.g. urn:samm:com.example.myapp:1.2.3
    *
    * @return the identifier
    */
   default String urn() {
      return String.format( "urn:samm:%s:%s", packagePart(), version() );
   }

   /**
    * The RDF prefix to use for elements in this namespace, e.g. urn:samm:com.example.myapp:1.2.3#
    *
    * @return the prefix
    */
   default String elementUrnPrefix() {
      return urn() + "#";
   }
}
