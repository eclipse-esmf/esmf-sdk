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

public class SimpleRdfNamespace implements RdfNamespace {
   private final String shortForm;
   private final String uri;

   public SimpleRdfNamespace( final String shortForm, final String uri ) {
      this.shortForm = shortForm;
      this.uri = uri;
   }

   @Override
   public String getShortForm() {
      return shortForm;
   }

   @Override
   public String getUri() {
      return uri;
   }
}
