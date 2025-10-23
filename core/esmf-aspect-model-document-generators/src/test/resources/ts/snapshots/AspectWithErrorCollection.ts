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

import { ReplacedAspectArtifact, } from './ReplacedAspectArtifact';

/**
 * Generated class for ReplacedAspectArtifacts Aspect (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithReplacedAspectArtifactCollection).
 *  The ReplacedAspectArtifacts Aspect delivers a list of the currently active errors for a specific machine.
 * Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
 */


import { CollectionAspect, } from './core/collectionAspect';


export class AspectWithReplacedAspectArtifactCollection extends CollectionAspect<ReplacedAspectArtifact[], ReplacedAspectArtifact> {
    // NotNull
    items: ReplacedAspectArtifact[];
}

