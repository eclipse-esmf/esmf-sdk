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

package org.eclipse.esmf.buildtime.template;

import javax.annotation.processing.Generated;

@Generated( "${generator}" )
public class VersionInfo {
   public static final String ESMF_SDK_VERSION = "${esmfSdkVersion}";
   public static final String ASPECT_META_MODEL_VERSION = "${aspectMetaModelVersion}";
}
