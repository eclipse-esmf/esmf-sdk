/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.versionupdate.migrator;

import java.util.Optional;

import org.apache.jena.rdf.model.Model;

import io.openmanufacturing.sds.aspectmodel.VersionNumber;

public interface Migrator {

   int order();

   Optional<String> getDescription();

   VersionNumber targetVersion();

   VersionNumber sourceVersion();

   Model migrate( Model sourceModel );
}
