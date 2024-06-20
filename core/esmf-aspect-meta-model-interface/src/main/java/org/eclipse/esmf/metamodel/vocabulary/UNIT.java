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

package org.eclipse.esmf.metamodel.vocabulary;

import org.eclipse.esmf.samm.KnownVersion;

// Since the class is an RDF vocabulary, naming rules for the class and for several methods (which should be named identically
// to the corresponding model elements) are suppressed.
@SuppressWarnings( { "checkstyle:AbbreviationAsWordInName", "CheckStyle" } )
public class UNIT implements Namespace {
   private final KnownVersion metaModelVersion;
   private final SAMM samm;

   public UNIT( final KnownVersion metaModelVersion, final SAMM samm ) {
      this.metaModelVersion = metaModelVersion;
      this.samm = samm;
   }

   @Override
   public String getUri() {
      return samm.getBaseUri() + "unit:" + metaModelVersion.toVersionString();
   }
}
