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
package org.eclipse.esmf.aspectmodel.generator;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The I18n resource bundle.
 */
public class I18nLanguageBundle {

   private static final String LANGUAGE_BUNDLE = "docu/aspect-model";
   private final Locale locale;
   private final ResourceBundle resourceBundle;

   public I18nLanguageBundle( final Locale locale ) {
      this.locale = locale;
      this.resourceBundle = ResourceBundle.getBundle( LANGUAGE_BUNDLE, locale );
   }

   public String getText( final String key ) {
      return resourceBundle.getString( key );
   }

   public Locale getLocale() {
      return locale;
   }
}
