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

/**
 * The org.eclipse.esmf.buildtime package contains classes that are exclusively used (compiled & executed) during the build.
 * Classes in the package are self-contained programs (with a main() method) that create or modify files, e.g., resources.
 * The classes from this package are explicitly excluded from the jar of the module. Execution of the classes and configuration of command
 * line arguments is done in pom.xml.
 */
package org.eclipse.esmf.buildtime;