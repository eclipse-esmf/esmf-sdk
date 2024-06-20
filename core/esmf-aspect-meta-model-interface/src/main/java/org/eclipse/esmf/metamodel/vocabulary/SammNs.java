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

public class SammNs {
   public static final SAMM SAMM = new SAMM( KnownVersion.getLatest() );
   public static final SAMMC SAMMC = new SAMMC( KnownVersion.getLatest() );
   public static final SAMME SAMME = new SAMME( KnownVersion.getLatest(), SAMM );
   public static final UNIT UNIT = new UNIT( KnownVersion.getLatest(), SAMM );
}
