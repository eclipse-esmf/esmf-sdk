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

import java.util.ArrayList;
import java.util.List;

public class Box {
   private String prototype;
   private final String title;
   private final List<String> entries = new ArrayList<>();

   public Box( final String prototype, final String title ) {
      this.prototype = prototype;
      this.title = title;
   }

   public void addEntry( final List<String> entry ) {
      entries.addAll( entry );
   }

   public void setPrototype( final String prototype ) {
      this.prototype = prototype;
   }

   public String getPrototype() {
      return prototype;
   }

   public String getTitle() {
      return title;
   }

   public List<String> getEntries() {
      return entries;
   }
}
