/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentStore {
   private final Map<String, String> documents = new ConcurrentHashMap<>();

   public void put( final String uri, final String content ) {
      documents.put( uri, content );
   }

   public String get( final String uri ) {
      return documents.get( uri );
   }

   public String getOrDefault( final String uri, final String fallback ) {
      return documents.getOrDefault( uri, fallback );
   }

   public void remove( final String uri ) {
      documents.remove( uri );
   }
}
