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

package org.eclipse.esmf.aspectmodel.edit;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;

public interface ChangeContext {
   Stream<AspectModelFile> aspectModelFiles();

   AspectChangeContextConfig config();

   List<AspectModelFile> createdFiles();

   List<AspectModelFile> modifiedFiles();

   List<AspectModelFile> removedFiles();

   void indicateFileIsAdded( AspectModelFile file );

   void indicateFileIsRemoved( AspectModelFile file );

   void indicateFileHasChanged( AspectModelFile file );

   void resetFileStates();
}
