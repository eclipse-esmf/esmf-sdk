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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;

public final class BoxModel implements Namespace {
   private final KnownVersion metaModelVersion;
   private final SAMM SAMM;

   public BoxModel( final KnownVersion metaModelVersion ) {
      this.metaModelVersion = metaModelVersion;
      SAMM = new SAMM( metaModelVersion );
   }

   @Override
   public String getUri() {
      return SAMM.getBaseUri() + "meta-model:" + metaModelVersion.toVersionString() + "/boxmodel";
   }

   public Resource box() {
      return resource( "Box" );
   }

   public Resource edge() {
      return resource( "Edge" );
   }

   public Resource entry() {
      return resource( "Entry" );
   }

   public Property prototype() {
      return property( "prototype" );
   }

   public Property title() {
      return property( "title" );
   }

   public Property entries() {
      return property( "entries" );
   }

   public Property fillcolor() {
      return property( "fillcolor" );
   }

   public Property text() {
      return property( "text" );
   }

   public Property from() {
      return property( "from" );
   }

   public Property to() {
      return property( "to" );
   }

   public Property rootElement() {
      return property( "rootElement" );
   }
}
